package xyz.needpainkiller.core.lib.security.error;

import xyz.needpainkiller.core.lib.exceptions.CommonErrorCode;
import xyz.needpainkiller.core.lib.exceptions.ErrorCode;
import xyz.needpainkiller.core.lib.exceptions.error.BusinessException;

import java.util.Map;

public class TokenValidFailedException extends BusinessException {
    public TokenValidFailedException() {
        super(CommonErrorCode.TOKEN_VALIDATION_FAILED);
    }

    public TokenValidFailedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TokenValidFailedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public TokenValidFailedException(ErrorCode errorCode, Map<String, Object> model) {
        super(errorCode, model);
    }

    public TokenValidFailedException(ErrorCode errorCode, String message, Map<String, Object> model) {
        super(errorCode, message, model);
    }

}
