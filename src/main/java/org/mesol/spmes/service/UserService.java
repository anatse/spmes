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
import javax.persistence.TypedQuery;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.security.User;
import org.mesol.spmes.model.security.UserGroup;
import org.mesol.spmes.model.security.UserShift;
import org.mesol.spmes.model.security.WorkCalendar;
import org.mesol.spmes.model.security.WorkDay;
import org.mesol.spmes.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Service
public class UserService 
{
    private static final Logger     logger = Logger.getLogger(UserService.class);
    
    public static final String     ADMIN_GROUP = "mes_admin";

    @Autowired
    private UserRepo                repo;
        
    @PersistenceContext
    private EntityManager           em;

    private static Calendar trunc (final Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    @Transactional
    public UserShift getCurrentShift (Date curDate, User user) {
        TypedQuery<UserShift> shiftQuery = em.createQuery("select u from UserShift u where :curTime between u.startTime and u.endTime", UserShift.class);
        shiftQuery.setParameter("curTime", UserShift.convertTime(curDate));
        List<UserShift> res = shiftQuery.getResultList();
        UserShift us = res.isEmpty() ? null : res.iterator().next();
        if (us == null)
            return null;

        // Check is user has admin role
        UserGroup ug = new UserGroup();
        ug.setName(ADMIN_GROUP);
        if (!user.getAuthorities().contains(ug)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(curDate);
            trunc (cal);

            WorkDay wd = new WorkDay();
            wd.setDate(cal.getTime());
            wd.setShift(us);

            // Check available for shift
            TypedQuery<Long> workdayCount = em.createQuery("select count(s) from WorkCalendar s where s.workDay = :workDay", Long.class);
            workdayCount.setParameter("workDay", wd);
            Long count = workdayCount.getSingleResult();
            if (count == 0)
                return null;
        }

        return us;
    }

    @Transactional
    public List<UserShift> getAlLShifts () {
        TypedQuery<UserShift> shiftQuery = em.createQuery("select u from UserShift u", UserShift.class);
        return shiftQuery.getResultList();
    }

    @Transactional
    public User findByName(String username) {
        User usr = repo.findByName(username);
        if (getCurrentShift (new Date(), usr) == null)
            usr = null;

        return usr;
    }

    @Transactional
    public void deleteByName(String username) {
        repo.deleteByName(username);
    }

    @Transactional
    public void save(User usr) {
        repo.save(usr);
    }

    /**
     * Function automatically fills work calendar. It called every Saturday.
     * It adds all defined shifts for MON-FRI for the next week
     * 
     * Scheduled at 00:00:00 every Saturday
     */
    @Transactional
    @Scheduled(cron = "30 * * * * *")
    public void fillWorkDays () {
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
    
    @Transactional
    public void addWorkDay (Calendar truncedDate) {
        List<UserShift> shifts = getAlLShifts ();
        shifts.forEach((shift) -> {
            WorkDay wd = new WorkDay();
            wd.setDate(truncedDate.getTime());
            wd.setShift(shift);
            WorkCalendar wcal = new WorkCalendar();
            wcal.setWorkDay(wd);
            wcal.setComments("Automatically filled by saturday scheduler");
            em.merge(wcal);
        });
    }
}
