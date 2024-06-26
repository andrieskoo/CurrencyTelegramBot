package com.chrzanowski.telegrambot.notification;

import com.chrzanowski.telegrambot.data.customer.Customer;
import com.chrzanowski.telegrambot.data.customer.CustomerService;
import com.chrzanowski.telegrambot.data.customersettings.CustomerSettings;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Calendar;

@Slf4j
@Component
public class NotificationSchedulerService {
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private Scheduler scheduler;


    public void init() throws SchedulerException {
        log.info("Init NotificationSchedulerService");
        scheduler = schedulerFactoryBean.getScheduler();
        scheduler.start();
        log.info("scheduler.isStarted() " + scheduler.isStarted());
    }

    public void updateScheduledJob(Long chatId, Integer hour) throws SchedulerException {
        if (scheduler == null){
            init();
        }
        log.info("Updating scheduled task for chatId = {} , hour = {}", chatId, hour);
        stopDailyTask(chatId.toString());
        if (hour != null) {
            startDailyTask(chatId.toString(), hour, 0);
        }
    }

    private void startDailyTask(String chatId, int hour, int minute) throws SchedulerException {
        if (existTask(chatId)){
            return;
        }
        log.info("Daily task will be executed at " + hour + " " + minute + " for chat id " + chatId);

        Calendar calendar = getConfiguredCalendar(hour, minute);

        JobDetail jobDetail = JobBuilder.newJob(NotificationJob.class)
                .withIdentity(chatId, "daily-jobs")
                .usingJobData("chatId", chatId)
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(chatId , "daily-triggers")
                .startAt(calendar.getTime())
                .withSchedule(SimpleScheduleBuilder.repeatHourlyForever(24))
//              .withSchedule(SimpleScheduleBuilder.repeatMinutelyForever(3))
                .build();
        scheduler.scheduleJob(jobDetail,  trigger);
    }


    private Calendar getConfiguredCalendar(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }

    private void stopDailyTask(String chatId) throws SchedulerException {
        JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(chatId, "daily-jobs"));
        Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(chatId, "daily-triggers"));
        if (jobDetail != null && trigger != null) {
            scheduler.unscheduleJob(trigger.getKey());
            scheduler.deleteJob(jobDetail.getKey());
            log.info("Job for chat id " + chatId + " has been unscheduled.");
        } else {
            log.info("No job found for chat id " + chatId);
        }
    }

    private boolean existTask(String chatId) throws SchedulerException {
        JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(chatId, "daily-jobs"));
        Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(chatId, "daily-triggers"));
        return jobDetail != null && trigger != null;
    }
}