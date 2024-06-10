package xyz.needpainkiller.api.tenant.domain.error;

import org.springframework.http.HttpStatus;
import xyz.needpainkiller.lib.exceptions.ErrorCode;


public enum TenantErrorCode implements ErrorCode {

    TENANT_CONFLICT(HttpStatus.BAD_REQUEST, "타 테넌트의 정보는 조회 및 조작할 수 없습니다."),

    TENANT_ROOT_NOT_EXIST(HttpStatus.NOT_FOUND, "최초 테넌트 정보가 기입되지 않았습니다."),
    TENANT_SEARCH_EMPTY(HttpStatus.NOT_FOUND, "테넌트 조회를 위한 테넌트 ID 가 확인되지 않습니다."),
    TENANT_NOT_EXIST(HttpStatus.NOT_FOUND, "요청하고자 하는 테넌트 정보를 확인 할 수 없습니다."),
    TENANT_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 테넌트"),
    TENANT_TITLE_BLANK(HttpStatus.BAD_REQUEST, "테넌트의 이름은 비어있을 수 없습니다."),
    TENANT_TITLE_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "동일 테넌트명으로 테넌트가 이미 등록되었습니다."),
    TENANT_DEFAULT_CAN_NOT_DELETE(HttpStatus.BAD_REQUEST, "기본 테넌트는 삭제할 수 없습니다."),
    TENANT_SERVER_DATA_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "테넌트 정보에 서버 정보가 유효하지 않습니다."),
    TENANT_SERVER_URL_BLANK(HttpStatus.BAD_REQUEST, "테넌트의 서버 경로는 비어있을 수 없습니다."),
    TENANT_SERVER_URL_PATTERN_NOT_MATCH(HttpStatus.BAD_REQUEST, "테넌트의 서버 경로가 올바르지 않습니다."),
    TENANT_SERVER_URL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "동일 서버 경로로 테넌트가 이미 등록되었습니다.");

    private final HttpStatus httpStatus;
    private final String errorMessage;

    TenantErrorCode(HttpStatus status, String errorMessage) {
        this.httpStatus = status;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return name();
    }
}



