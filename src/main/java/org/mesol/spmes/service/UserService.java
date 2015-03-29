/*
 * Copyright 2014 Mes Solutions.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mesol.spmes.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.isNull;
import org.hibernate.criterion.Subqueries;
import org.mesol.spmes.config.PersistenceJPAConfig;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.security.Menu;
import org.mesol.spmes.model.security.User;
import org.mesol.spmes.model.security.UserGroup;
import org.mesol.spmes.model.security.UserShift;
import org.mesol.spmes.model.security.WorkCalendar;
import org.mesol.spmes.model.security.WorkDay;
import org.mesol.spmes.service.abs.AbstractServiceWithAttributes;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Class implements user functions
 * @version 1.0.0
 * @author ASementsov
 */
@Service
@PropertySource("classpath:/" + PersistenceJPAConfig.CONFIG)
public class UserService extends AbstractServiceWithAttributes
{
    @PersistenceContext
    private EntityManager           entityManager;

    /**
     * Truncate date remove hours
     * @param cal - calendar object
     * @return truncated calendar
     */
    private static Calendar trunc(final Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * Function retrieves current shift for given user
     * @param curDate - current date
     * @param user - user
     * @return user shift if available for this user
     */
    @Transactional
    public UserShift getCurrentShift(Date curDate, User user) {
        Session session = getHibernateSession();
        UserShift us = (UserShift)session.getNamedQuery("UserShift.currentShift")
            .setParameter("curTime", UserShift.convertTime(curDate))
            .uniqueResult();

        if (us == null)
            return null;

        // Check is user has admin role
        UserGroup ug = new UserGroup();
        ug.setName(BasicConstants.ADMIN_ROLE);
        if (!user.getAuthorities().contains(ug)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(curDate);
            trunc (cal);

            WorkDay wd = new WorkDay();
            wd.setDate(cal.getTime());
            wd.setShift(us);

            // Check available for shift
            Long count = ((Number)session.createCriteria(WorkCalendar.class)
                .add(eq ("workDay", wd))
                .setProjection(Projections.rowCount())
                .uniqueResult()).longValue();

            if (count == 0)
                return null;
        }

        return us;
    }

    /**
     * Function retrieves all shifts for current day
     * @return list of user shift
     */
    @Transactional
    public List<UserShift> getAlLShifts () {
        Session session = getHibernateSession();
        return session
            .createCriteria(UserShift.class)
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();
    }

    /**
     * Find user by username
     * @param username - user name to find
     * @return found user
     */
    @Transactional
    public User findByName (String username) {
        Session session = getHibernateSession();
        User usr = (User) session.createCriteria(User.class)
            .add (eq("username", username))
            .setFetchMode("groups", FetchMode.JOIN)
            .uniqueResult();

        if (usr == null && username.equalsIgnoreCase("admin")) {
             usr = new User();
             usr.setUsername("admin");
             usr.setPassword("admin");
             usr.setFirstName("Administrator");
             usr.setLastName("admin");
             save(usr);
        }

        return usr;
    }

    /**
     * Delete user by username
     * @param username - user name
     */
    @Transactional
    public void deleteByName(String username) {
        getHibernateSession().delete(findByName(username));
    }

    /**
     * Create or update user
     * @param usr - user information
     */
    @Transactional
    @Secured({BasicConstants.ADMIN_ROLE})
    public void save(User usr) {
        getHibernateSession().saveOrUpdate(usr);
    }

    /**
     * Function automatically fills work calendar. It called every Saturday.
     * It adds all defined shifts for MON-FRI for the next week
     * 
     * Scheduled at 00:00:00 every Saturday
     */
    @Transactional
    @Scheduled(cron = "${calendar.scheduler.cron}")
    public void fillWorkDays() {
        Calendar cal = Calendar.getInstance();
        trunc (cal);
        int dayOfWeek = cal.get (Calendar.DAY_OF_WEEK);

        do {
            switch (dayOfWeek) {
                case Calendar.SATURDAY:
                case Calendar.SUNDAY:
                    continue;
                
                default:
                    addWorkDay (cal);
            }

            cal.add(Calendar.DAY_OF_MONTH, 1);
            dayOfWeek = cal.get (Calendar.DAY_OF_WEEK);
        } while (dayOfWeek != Calendar.SATURDAY);
    }

    /**
     * Function adds date as work day to production calendar
     * @param truncedDate - date to add
     */
    @Transactional
    public void addWorkDay (Calendar truncedDate) {
        Session session = getHibernateSession();
        List<UserShift> shifts = getAlLShifts ();
        shifts.forEach((shift) -> {
            WorkDay wd = new WorkDay();
            wd.setDate(truncedDate.getTime());
            wd.setShift(shift);
            WorkCalendar wcal = new WorkCalendar();
            wcal.setWorkDay(wd);
            wcal.setComments("Automatically filled by saturday scheduler");
            session.save(wcal);
        });
    }

    /**
     * Function retrieves menu for current user
     * @param username - user name
     * @param parentId - id of parent menu item
     * @return menu items
     */
    @Transactional
    public List<Menu> getUserMenu (String username, Long parentId) {
        Session session = getHibernateSession();
        DetachedCriteria userGroups = DetachedCriteria.forClass(User.class)
            .createAlias("groups", "grp")
            .add(eq ("username", username))
            .setProjection(Property.forName("grp.id"));

        return session.createCriteria(Menu.class)
            .add(parentId == null ? isNull("parent") : eq("parent.id", parentId))
            .createAlias("groups", "grp")
            .addOrder(Order.asc("seq"))
            .add(Subqueries.propertyIn("grp.id", userGroups))
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();
    }

    /**
     * Find all users
     * @return list of users
     */
    @Transactional
    public List<User> findAll() {
        return super.findAll (User.class);
    }

    /**
     * Find user by id
     * @param userId user id
     * @return found user
     */
    @Transactional
    public User findOne(Long userId) {
        return entityManager.find(User.class, userId);
    }

    @Transactional
    public Menu addMenu (Menu menu) {
        Menu foundMenu = (Menu)getHibernateSession()
                    .createCriteria(Menu.class)
                    .add(eq("name", menu.getName()))
                    .add(eq("parent", menu.getParent()))
                    .uniqueResult();
        if (foundMenu == null) {
            foundMenu = entityManager.merge(menu);
        }
        
        return foundMenu;
    }

    @Transactional
    public UserGroup addGroup (UserGroup group) {
        UserGroup foundGroup = (UserGroup)getHibernateSession()
                    .createCriteria(UserGroup.class)
                    .add(eq("name", group.getName()))
                    .uniqueResult();
        if (foundGroup == null) {
            foundGroup = entityManager.merge(group);
        }

        return foundGroup;
    }

    /**
     * Implements the same function from AbstractController
     * @return entity manager
     */
    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
