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
@Transactional
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

    private UserShift getCurrentShift (Date curDate) {
        TypedQuery<UserShift> shiftQuery = em.createQuery("select u from UserShift u where :curTime between u.startTime and u.endTime", UserShift.class);
        shiftQuery.setParameter("curTime", UserShift.convertTime(curDate));
        List<UserShift> res = shiftQuery.getResultList();
        return res.isEmpty() ? null : res.iterator().next();
    }

    private List<UserShift> getAlLShifts () {
        TypedQuery<UserShift> shiftQuery = em.createQuery("select u from UserShift u", UserShift.class);
        return shiftQuery.getResultList();
    }

    public User findByName(String username) {
        User usr = repo.findByName(username);
        UserGroup ug = new UserGroup();
        ug.setName(ADMIN_GROUP);
        if (!usr.getAuthorities().contains(ug)) {
            Date curDate = new Date();

            // Get current shift
            UserShift us = getCurrentShift(curDate);
            // Shift not found, user cannot login
            if (us == null)
                return null;

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

        return usr;
    }

    public void deleteByName(String username) {
        repo.deleteByName(username);
    }

    public void save(User usr) {
        repo.save(usr);
    }

    /**
     * Function automatically fills work calendar. It called every Saturday.
     * It adds all defined shifts for MON-FRI for the next week
     * 
     * Scheduled at 00:00:00 every Saturday
     */
    @Scheduled(cron = "30 0 0 * * *")
    public void fillWorkDays () {
        List<UserShift> shifts = getAlLShifts ();

        Calendar cal = Calendar.getInstance();
        trunc (cal);
        int dayOfWeek = cal.get (Calendar.DAY_OF_WEEK);

        do {
            switch (dayOfWeek) {
                case Calendar.SATURDAY:
                case Calendar.SUNDAY:
                    continue;
                
                default:
                    shifts.forEach((shift) -> {
                        WorkDay wd = new WorkDay();
                        wd.setDate(cal.getTime());
                        wd.setShift(shift);
                        WorkCalendar wcal = new WorkCalendar();
                        wcal.setWorkDay(wd);
                        wcal.setComments("Automatically filled by spring shcduler");
                        em.merge(wcal);
                    });
            }

            cal.add(Calendar.DAY_OF_MONTH, 1);
            dayOfWeek = cal.get (Calendar.DAY_OF_WEEK);
        } while (dayOfWeek != Calendar.SATURDAY);
    }
}
