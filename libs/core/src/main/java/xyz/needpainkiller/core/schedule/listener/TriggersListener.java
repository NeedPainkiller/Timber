package xyz.needpainkiller.core.schedule.listener;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TriggersListener implements TriggerListener {
    @Override
    public String getName() {
        return "globalTrigger";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        JobKey jobKey = trigger.getJobKey();
        log.info("triggerFired : {}", jobKey);
        log.debug("triggerFired at {} :: jobKey : {}", trigger.getStartTime(), jobKey);
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        JobKey jobKey = trigger.getJobKey();
        log.info("triggerMisfired : {}", jobKey);
        log.debug("triggerMisfired at {} :: jobKey : {}", trigger.getStartTime(), jobKey);

    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        JobKey jobKey = trigger.getJobKey();
        log.info("triggerComplete : {}", jobKey);
        log.debug("triggerComplete at {} :: jobKey : {}", trigger.getStartTime(), jobKey);
    }
}
