package xyz.needpainkiller.api.tenant.domain.error;

import xyz.needpainkiller.lib.exceptions.BusinessException;
import xyz.needpainkiller.lib.exceptions.ErrorCode;

import java.io.Serial;
import java.util.Map;

public class TenantException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 7633976547219909240L;

    public TenantException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TenantException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public TenantException(ErrorCode errorCode, Map<String, Object> model) {
        super(errorCode, model);
    }

    public TenantException(ErrorCode errorCode, String message, Map<String, Object> model) {
        super(errorCode, message, model);
    }
}
