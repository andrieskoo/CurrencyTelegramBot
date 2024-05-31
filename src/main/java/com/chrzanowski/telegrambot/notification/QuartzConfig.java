package com.chrzanowski.telegrambot.notification;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

@Component
public class QuartzConfig {
    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        return new SpringBeanJobFactory() {
            @Override
            protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
                final Object job = super.createJobInstance(bundle);
                beanFactory.autowireBean(job);
                return job;
            }
        };
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(SpringBeanJobFactory springBeanJobFactory) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(springBeanJobFactory);
        return schedulerFactoryBean;
    }
}
