package xyz.needpainkiller.api.user.domain.error;

import xyz.needpainkiller.lib.exceptions.BusinessException;
import xyz.needpainkiller.lib.exceptions.ErrorCode;

import java.util.Map;

public class RoleException extends BusinessException {

    public RoleException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RoleException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public RoleException(ErrorCode errorCode, Map<String, Object> model) {
        super(errorCode, model);
    }

    public RoleException(ErrorCode errorCode, String message, Map<String, Object> model) {
        super(errorCode, message, model);
    }
}
