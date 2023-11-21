package xyz.needpainkiller.core.schedule.util;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import xyz.needpainkiller.core.helper.TimeHelper;
import xyz.needpainkiller.core.schedule.dto.JobRequest;
import xyz.needpainkiller.core.schedule.model.ScheduleTriggerType;

import java.text.ParseException;
import java.time.LocalDateTime;

@Slf4j
public final class JobUtil {
    private JobUtil() {
    }

    public static JobDetail createJob(JobRequest jobRequest, Class<? extends Job> jobClass, ApplicationContext context) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        factoryBean.setDurability(false);
        factoryBean.setApplicationContext(context);
        factoryBean.setName(jobRequest.getJobName());
        factoryBean.setGroup(jobRequest.getJobGroup());

        if (jobRequest.getJobDataMap() != null) {
            factoryBean.setJobDataMap(jobRequest.getJobDataMap());
        }

        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    public static Trigger createTrigger(JobRequest jobRequest) throws IllegalArgumentException {
        ScheduleTriggerType triggerType = jobRequest.getTriggerType();
        if (triggerType.equals(ScheduleTriggerType.SIMPLE)) {
            return createSimpleTrigger(jobRequest);
        } else if (triggerType.equals(ScheduleTriggerType.CRON)) {
            return createCronTrigger(jobRequest);
        } else if (triggerType.equals(ScheduleTriggerType.SCHEDULE)) {
            return createScheduleTrigger(jobRequest);
        } else {
            throw new IllegalStateException("unsupported trigger descriptor");
        }
    }

    private static Trigger createSimpleTrigger(JobRequest jobRequest) {
        LocalDateTime startDateAt = jobRequest.getStartDateAt();
        if (startDateAt == null) {
            throw new IllegalArgumentException("startDateAt is cannot be null");
        }

        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setName(jobRequest.getJobName());
        factoryBean.setGroup(jobRequest.getJobGroup());
        factoryBean.setStartTime(TimeHelper.fromLocalDateTimeToDate(startDateAt));
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        factoryBean.setRepeatInterval(0L);
        factoryBean.setRepeatCount(0);
        factoryBean.setPriority(jobRequest.getPriority());
/* 단순 반복 처리 시
        factoryBean.setRepeatInterval(jobRequest.getRepeatIntervalInSeconds() * 1000); //ms 단위임
        factoryBean.setRepeatCount(jobRequest.getRepeatCount());
*/
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    private static Trigger createCronTrigger(JobRequest jobRequest) {
        String cronExpression = jobRequest.getCronExpression();
        if (!CronExpression.isValidExpression(cronExpression)) {
            throw new IllegalArgumentException("Provided expression " + cronExpression + " is not a valid cron expression");
        }

        LocalDateTime startDateAt = jobRequest.getStartDateAt();

        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setTimeZone(TimeHelper.TIME_ZONE);
        factoryBean.setName(jobRequest.getJobName());
        factoryBean.setGroup(jobRequest.getJobGroup());
        factoryBean.setPriority(jobRequest.getPriority());
        if (startDateAt != null) {
            factoryBean.setStartTime(TimeHelper.fromLocalDateTimeToDate(jobRequest.getStartDateAt()));
        }
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        try {
            factoryBean.afterPropertiesSet();
        } catch (ParseException e) {
            log.error("Cron 스케쥴러 ParseException 발생 \njobRequest : {} \n message :{}", jobRequest, e.getMessage());
        }
        return factoryBean.getObject();
    }

    private static Trigger createScheduleTrigger(JobRequest jobRequest) {
        ScheduleBuilder<?> builder = jobRequest.getBuilder();
        return TriggerBuilder.newTrigger()
                .withIdentity(jobRequest.getJobName(), jobRequest.getJobGroup())
                .startAt(TimeHelper.fromLocalDateTimeToDate(jobRequest.getStartDateAt()))
                .endAt(TimeHelper.fromLocalDateTimeToDate(jobRequest.getEndDateAt()))
                .withPriority(jobRequest.getPriority())
                .withSchedule(builder).build();
    }


}
