package xyz.needpainkiller.core.schedule.dto;

import xyz.needpainkiller.core.schedule.model.ScheduleTriggerType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.ScheduleBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class JobRequest {

    private final Class<? extends Job> jobClass;
    private final ScheduleTriggerType triggerType;
    private String jobGroup = "SYSTEM";
    private String jobName;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'X'")
    private LocalDateTime startDateAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'X'")
    private LocalDateTime endDateAt;
    private long repeatIntervalInSeconds;
    private int repeatCount;
    private String cronExpression;
    private ScheduleBuilder<?> builder;
    private JobDataMap jobDataMap;
    private int priority;

    public JobRequest(Class<? extends Job> jobClass, ScheduleTriggerType triggerType) {
        this.jobClass = jobClass;
        if (triggerType == null) {
            this.triggerType = ScheduleTriggerType.NONE;
            throw new IllegalArgumentException("triggerType cannot be null");
        }
        this.triggerType = triggerType;
    }
}
