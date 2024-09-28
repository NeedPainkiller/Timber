package xyz.needpainkiller.api.user.domain.error;

import org.springframework.http.HttpStatus;
import xyz.needpainkiller.lib.exceptions.ErrorCode;


public enum UserErrorCode implements ErrorCode {

    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "계정 권한이 확인되지 않음"),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, "요청하고자 하는 정보의 조회권한 없음"),
    USER_NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 계정입니다."),
    USER_LOCKED(HttpStatus.BAD_REQUEST, "잠금 처리된 계정입니다."),
    USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이미 등록된 유저"),
    USER_DISABLED(HttpStatus.BAD_REQUEST, "비활성화 또는 삭제된 유저"),
    USER_DELETE_SELF(HttpStatus.BAD_REQUEST, "자기 자신의 계정을 삭제할 수 없음"),

    USER_TENANT_SWITCH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "해당 계정은 테넌트를 전환할 수 없음"),

    USER_ID_LENGTH(HttpStatus.BAD_REQUEST, "계정 ID 의 길이가 조건에 맞지 않음"),
    USER_ID_NEED_ALPHABET(HttpStatus.BAD_REQUEST, "계정 ID 에 알파벳이 존재하지 않음"),
    USER_ID_NEED_NUM(HttpStatus.BAD_REQUEST, "계정 ID 에 숫자가 포함되지 않음"),
    USER_ID_IGNORE_SPECIAL(HttpStatus.BAD_REQUEST, "계정 ID 에 특수문자는 포함되지 않음"),

    USER_ID_IS_NOT_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "이메일 형식을 따를 것"),
    USER_ID_EMPTY(HttpStatus.BAD_REQUEST, "User ID 누락"),
    USER_NM_EMPTY(HttpStatus.BAD_REQUEST, "User Name 누락"),

    PASSWORD_NO_PERMISSION(HttpStatus.FORBIDDEN, "패스워드 변경 권한 없음"),
    PASSWORD_FAIL_UPDATE(HttpStatus.INTERNAL_SERVER_ERROR, "패스워드 변경 또는 초기화 실패"),
    PASSWORD_SAME_PASSWORD(HttpStatus.BAD_REQUEST, "변경하고자 하는 패스워드가 이전과 동일함"),
    PASSWORD_NOT_MATCH_CONFIRM(HttpStatus.BAD_REQUEST, "패스워드가 검증용 패스워드와 동일하지 않음"),
    PASSWORD_EMPTY(HttpStatus.BAD_REQUEST, "패스워드가 비워져 있음"),
    PASSWORD_LENGTH(HttpStatus.BAD_REQUEST, "패스워드의 길이가 조건에 맞지 않음"),
    PASSWORD_NEED_ALPHABET(HttpStatus.BAD_REQUEST, "패스워드에 알파벳이 존재하지 않음"),
    PASSWORD_NEED_NUM_SPECIAL(HttpStatus.BAD_REQUEST, "패스워드에 특수문자, 숫자가 포함되지 않음"),
    PASSWORD_EXPIRED(HttpStatus.BAD_REQUEST, "패스워드가 만료됨");

    private final HttpStatus httpStatus;
    private final String errorMessage;

    UserErrorCode(HttpStatus status, String errorMessage) {
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


