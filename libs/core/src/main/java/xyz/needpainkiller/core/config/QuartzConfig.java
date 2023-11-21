package xyz.needpainkiller.core.config;

import xyz.needpainkiller.core.schedule.AutowiringSpringBeanJobFactory;
import xyz.needpainkiller.core.schedule.listener.JobsListener;
import xyz.needpainkiller.core.schedule.listener.TriggersListener;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
@Configuration
public class QuartzConfig {

    @Autowired
    private TriggersListener triggersListener;

    @Autowired
    private JobsListener jobsListener;

    @Autowired
    private QuartzProperties quartzProperties;

    @Autowired
    private DataSource dataSource;

    /**
     * Quartz 관련 설정
     *
     * @param applicationContext the applicationContext
     * @return SchedulerFactoryBean
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(ApplicationContext applicationContext) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(jobFactory);

        schedulerFactoryBean.setApplicationContext(applicationContext);

        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());

        schedulerFactoryBean.setGlobalTriggerListeners(triggersListener);
        schedulerFactoryBean.setGlobalJobListeners(jobsListener);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setQuartzProperties(properties);
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);
        return schedulerFactoryBean;
    }

    @Bean
    public SmartLifecycle gracefulShutdownHookForQuartz(@Qualifier("schedulerFactoryBean") SchedulerFactoryBean schedulerFactoryBean) {
        return new SmartLifecycle() {
            private boolean isRunning = false;

            @Override
            public boolean isAutoStartup() {
                return true;
            }

            @Override
            public void stop(Runnable callback) {
                stop();
                log.info("Spring container is shutting down.");
                callback.run();
            }

            @Override
            public void start() {
                log.info("Quartz Graceful Shutdown Hook started.");
                isRunning = true;
            }

            @Override
            public void stop() {
                isRunning = false;

                try {
                    log.info("Quartz Graceful Shutdown...");
                    interruptJobs(schedulerFactoryBean);
                    schedulerFactoryBean.destroy();
                } catch (SchedulerException e) {
                    try {
                        log.info("Error shutting down Quartz: ", e);
                        schedulerFactoryBean.getScheduler().shutdown(false);
                    } catch (SchedulerException ex) {
                        log.error("Unable to shutdown the Quartz scheduler.", ex);
                    }
                }
            }

            @Override
            public boolean isRunning() {
                return isRunning;
            }

            @Override
            public int getPhase() {
                return Integer.MAX_VALUE;
            }

            private void interruptJobs(SchedulerFactoryBean schedulerFactoryBean) throws SchedulerException {
                Scheduler scheduler = schedulerFactoryBean.getScheduler();
                for (JobExecutionContext jobExecutionContext : scheduler.getCurrentlyExecutingJobs()) {
                    final JobDetail jobDetail = jobExecutionContext.getJobDetail();
                    log.info("interrupting job :: jobKey : {}", jobDetail.getKey());
                    scheduler.interrupt(jobDetail.getKey());
                }
            }
        };
    }


}
