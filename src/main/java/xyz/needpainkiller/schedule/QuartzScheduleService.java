package xyz.needpainkiller.schedule;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import xyz.needpainkiller.lib.exceptions.CommonErrorCode;
import xyz.needpainkiller.schedule.dto.JobRequest;
import xyz.needpainkiller.schedule.dto.JobResponse;
import xyz.needpainkiller.schedule.dto.JobStatusResponse;
import xyz.needpainkiller.schedule.error.QuartzSchedulerException;
import xyz.needpainkiller.schedule.util.DateTimeUtil;
import xyz.needpainkiller.schedule.util.JobUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class QuartzScheduleService {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private ApplicationContext context;

    public void upsertJob(JobRequest jobRequest) {
        Class<? extends Job> jobClass = jobRequest.getJobClass();
        JobKey jobKey = JobKey.jobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
        if (isJobExists(jobKey)) {
            if (!isJobRunning(jobKey)) {
                deleteJob(jobKey);
                addJob(jobRequest, jobClass);
            }
        } else {
            addJob(jobRequest, jobClass);
        }
    }

    public boolean addJob(JobRequest jobRequest, Class<? extends Job> jobClass) {
        //todo : job history에도 기록하도록 함.

        if (Strings.isBlank(jobRequest.getJobName())) {
            throw new QuartzSchedulerException(CommonErrorCode.JOB_SCHEDULE_NAME_EMPTY);
        }

        JobKey jobKey = null;
        JobDetail jobDetail;
        Trigger trigger;

        try {
            trigger = JobUtil.createTrigger(jobRequest);
            jobDetail = JobUtil.createJob(jobRequest, jobClass, context);
            jobKey = JobKey.jobKey(jobRequest.getJobName(), jobRequest.getJobGroup());

            if (isJobExists(jobKey)) {
//                throw new QuartzSchedulerException(RPA_JOB_SCHEDULE_ALREADY_EXIST);
                log.error(CommonErrorCode.JOB_SCHEDULE_ALREADY_EXIST.getMessage() + " : {}", jobRequest.getJobName());
                return false;
            }

            Date dt = schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
            log.debug("Job with jobKey : {} scheduled successfully at date : {}", jobDetail.getKey(), dt);
            return true;
        } catch (SchedulerException e) {
            log.error("error occurred while scheduling with jobKey : {}", jobKey, e);
        }
        return false;
    }


    public void clearAllJob() throws SchedulerException {
        schedulerFactoryBean.getScheduler().clear();
    }

    public void clearAllJobByGroup(String groupName) throws SchedulerException {
        Set<JobKey> jobKeyList = schedulerFactoryBean.getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(groupName));
        schedulerFactoryBean.getScheduler().deleteJobs(new ArrayList<>(jobKeyList));
    }

    public boolean deleteJob(JobRequest jobRequest) {
        JobKey jobKey = JobKey.jobKey(jobRequest.getJobName(), jobRequest.getJobGroup());
        return deleteJob(jobKey);
    }

    public boolean deleteJob(JobKey jobKey) {
        //todo : job history에도 기록하도록 함.
        log.debug("[schedulerdebug] deleting job with jobKey : {}", jobKey);
        if (!isJobExists(jobKey)) {
            throw new QuartzSchedulerException(CommonErrorCode.JOB_SCHEDULE_NOT_EXIST);
        }
        if (isJobRunning(jobKey)) {
            throw new QuartzSchedulerException(CommonErrorCode.JOB_SCHEDULE_ALREADY_RUNNING);
        }

        try {
            return schedulerFactoryBean.getScheduler().deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error occurred while deleting job with jobKey : {}", jobKey, e);
        }
        return false;
    }

    public boolean pauseJob(JobKey jobKey) {
        //todo : job history에도 기록하도록 함.
        log.debug("[schedulerdebug] pausing job with jobKey : {}", jobKey);

        if (!isJobExists(jobKey)) {
            throw new QuartzSchedulerException(CommonErrorCode.JOB_SCHEDULE_NOT_EXIST);
        }
        if (isJobRunning(jobKey)) {
            throw new QuartzSchedulerException(CommonErrorCode.JOB_SCHEDULE_ALREADY_RUNNING);
        }
        if (getJobState(jobKey).equals("PAUSED")) {
            throw new QuartzSchedulerException(CommonErrorCode.JOB_SCHEDULE_ALREADY_PAUSED);
        }
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.pauseJob(jobKey);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
            if (!triggers.isEmpty()) {
                for (Trigger trigger : triggers) {
                    scheduler.pauseTrigger(trigger.getKey());
                }
            }
            return true;
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error occurred while deleting job with jobKey : {}", jobKey, e);
        }
        return false;
    }

    public boolean resumeJob(JobKey jobKey) {
        //todo : job history에도 기록하도록 함.
        log.debug("[schedulerdebug] resuming job with jobKey : {}", jobKey);
        if (!isJobExists(jobKey)) {
            throw new QuartzSchedulerException(CommonErrorCode.JOB_SCHEDULE_NOT_EXIST);
        }
        if (!getJobState(jobKey).equals("PAUSED")) {
            throw new QuartzSchedulerException(CommonErrorCode.JOB_SCHEDULE_ALREADY_RUNNING);
        }
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.resumeJob(jobKey);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
            if (!triggers.isEmpty()) {
                for (Trigger trigger : triggers) {
                    scheduler.resumeTrigger(trigger.getKey());
                }
            }
            return true;
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error occurred while resuming job with jobKey : {}", jobKey, e);
        }
        return false;
    }

    public JobStatusResponse getAllJobs() {
        JobResponse jobResponse;
        JobStatusResponse jobStatusResponse = new JobStatusResponse();
        List<JobResponse> jobs = new ArrayList<>();
        int numOfRunningJobs = 0;
        int numOfGroups = 0;
        int numOfAllJobs = 0;

        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            for (String groupName : scheduler.getJobGroupNames()) {
                numOfGroups++;
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);

                    jobResponse = JobResponse.builder()
                            .jobName(jobKey.getName())
                            .groupName(jobKey.getGroup())
                            .scheduleTime(DateTimeUtil.toString(triggers.get(0).getStartTime()))
                            .lastFiredTime(DateTimeUtil.toString(triggers.get(0).getPreviousFireTime()))
                            .nextFireTime(DateTimeUtil.toString(triggers.get(0).getNextFireTime()))
                            .build();

                    if (isJobRunning(jobKey)) {
                        jobResponse.setJobStatus("RUNNING");
                        numOfRunningJobs++;
                    } else {
                        String jobState = getJobState(jobKey);
                        jobResponse.setJobStatus(jobState);
                    }
                    numOfAllJobs++;
                    jobs.add(jobResponse);
                }
            }
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error while fetching all job info", e);
        }

        jobStatusResponse.setNumOfAllJobs(numOfAllJobs);
        jobStatusResponse.setNumOfRunningJobs(numOfRunningJobs);
        jobStatusResponse.setNumOfGroups(numOfGroups);
        jobStatusResponse.setJobs(jobs);
        return jobStatusResponse;
    }

    public boolean isJobRunning(JobKey jobKey) {
        try {
            List<JobExecutionContext> currentJobs = schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs();
            if (currentJobs != null) {
                for (JobExecutionContext jobCtx : currentJobs) {
                    if (jobKey.getName().equals(jobCtx.getJobDetail().getKey().getName())) {
                        return true;
                    }
                }
            }
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error occurred while checking job with jobKey : {}", jobKey, e);
        }
        return false;
    }

    public boolean isJobExists(JobKey jobKey) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            if (scheduler.checkExists(jobKey)) {
                return true;
            }
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] error occurred while checking job exists :: jobKey : {}", jobKey, e);
        }
        return false;
    }

    public String getJobState(JobKey jobKey) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);

            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());

            if (!triggers.isEmpty()) {
                for (Trigger trigger : triggers) {
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    if (Trigger.TriggerState.NORMAL.equals(triggerState)) {
                        return "SCHEDULED";
                    }
                    return triggerState.name().toUpperCase();
                }
            }
        } catch (SchedulerException e) {
            log.error("[schedulerdebug] Error occurred while getting job state with jobKey : {}", jobKey, e);
        }
        return "NONE";
    }
}
