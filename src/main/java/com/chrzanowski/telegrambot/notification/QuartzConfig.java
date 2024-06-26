package com.chrzanowski.telegrambot.notification;

import lombok.extern.slf4j.Slf4j;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import java.util.Properties;

@Slf4j
@Configuration
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
    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer()
    {
        return new SchedulerFactoryBeanCustomizer()
        {
            @Override
            public void customize(SchedulerFactoryBean bean)
            {
                bean.setQuartzProperties(createQuartzProperties());
            }
        };
    }

    private Properties createQuartzProperties()
    {
        Properties props = new Properties();
        props.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
        return props;
    }

}
