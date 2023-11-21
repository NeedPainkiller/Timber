package xyz.needpainkiller.core.schedule.error;

import xyz.needpainkiller.core.lib.exceptions.ErrorCode;
import xyz.needpainkiller.core.lib.exceptions.error.BusinessException;

import java.util.Map;

public class QuartzSchedulerException extends BusinessException {

    public QuartzSchedulerException(ErrorCode errorCode) {
        super(errorCode);
    }

    public QuartzSchedulerException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public QuartzSchedulerException(ErrorCode errorCode, Map<String, Object> model) {
        super(errorCode, model);
    }

    public QuartzSchedulerException(ErrorCode errorCode, String message, Map<String, Object> model) {
        super(errorCode, message, model);
    }
}
