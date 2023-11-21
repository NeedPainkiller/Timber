package xyz.needpainkiller.core.lib.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import xyz.needpainkiller.core.lib.exceptions.error.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.BindException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    /**
     * jakarta.validation.Valid or @Validated 으로 binding error 발생시 발생한다.
     * HttpMessageConverter 에서 등록한 HttpMessageConverter binding 못할경우 발생
     * 주로 @RequestBody, @RequestPart 어노테이션에서 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * @ModelAttribut 으로 binding error 발생시 BindException 발생한다.
     * ref https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-modelattrib-method-args
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ApiErrorResponse> handleBindException(BindException e) {
        log.error("handleBindException", e);
        ApiErrorResponse response = ApiErrorResponse.of(CommonErrorCode.INVALID_INPUT_VALUE, e);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * enum type 일치하지 않아 binding 못할 경우 발생
     * 주로 @RequestParam enum으로 binding 못했을 경우 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException", e);
        ApiErrorResponse response = ApiErrorResponse.of(CommonErrorCode.INVALID_INPUT_VALUE, e);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        ApiErrorResponse response = ApiErrorResponse.of(CommonErrorCode.METHOD_NOT_ALLOWED, e);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * HTTP 요청 시, CONFLICT 발생
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("handleIllegalArgumentException", e);
        ApiErrorResponse response = ApiErrorResponse.of(CommonErrorCode.REQUEST_IS_CONFLICT, e);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Authentication 객체가 필요한 권한을 보유하지 않은 경우 발생합
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.info("handleAccessDeniedException", e);
        ApiErrorResponse response = ApiErrorResponse.of(CommonErrorCode.USER_FORBIDDEN, e);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(InvalidFormatException.class)
    protected ResponseEntity<ApiErrorResponse> handleInvalidFormatException(InvalidFormatException e) {
        log.info("handleInvalidFormatException", e);
        ApiErrorResponse response = ApiErrorResponse.of(CommonErrorCode.REQUEST_JSON_PARSE_ERROR, e);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.info("handleHttpMessageNotReadableException", e);
        ApiErrorResponse response = ApiErrorResponse.of(CommonErrorCode.REQUEST_JSON_PARSE_ERROR, e);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    protected ResponseEntity<ApiErrorResponse> handleInsufficientAuthenticationException(InsufficientAuthenticationException e) {
        log.info("handleInsufficientAuthenticationException", e);
        ApiErrorResponse response = ApiErrorResponse.of(CommonErrorCode.USER_UNAUTHORIZED, e);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ApiErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.info("handleDataIntegrityViolationException", e);
        ApiErrorResponse response = ApiErrorResponse.of(CommonErrorCode.REQUEST_IS_CONFLICT);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<ApiErrorResponse> handleDataAccessException(DataAccessException e) {
        String errCode = "";
        String errMsg = "";
        log.info("handleDataIntegrityViolationException", e);
        if (e instanceof BadSqlGrammarException) {

            SQLException se = ((BadSqlGrammarException) e).getSQLException();

            log.debug("**BadSqlGrammarException {} ", se.getErrorCode());

        } else if (e instanceof InvalidResultSetAccessException) {

            SQLException se = ((InvalidResultSetAccessException) e).getSQLException();

            log.debug("**InvalidResultSetAccessException {} ", se.getErrorCode());

        } else if (e instanceof DuplicateKeyException) {

            log.debug("**DuplicateKeyException {} ", e.getMessage());

        } else if (e instanceof DataIntegrityViolationException) {
            // 고유성 제한 위반과 같은 데이터 삽입 또는 업데이트시 무결성 위반
            log.debug("**DataIntegrityViolationException {} ", e.getMessage());
            errCode = "1";
            errMsg = "데이터 중복오류";
        } else if (e instanceof DataAccessResourceFailureException) {
            // 데이터 액세스 리소스가 완전히 실패했습니다 (예 : 데이터베이스에 연결할 수 없음)
            log.debug("**DataAccessResourceFailureException {} ", e.getMessage());
            errCode = "1";
            errMsg = "데이터베이스 연결오류";
        } else if (e instanceof CannotAcquireLockException) {

            log.debug("**CannotAcquireLockException {} ", e.getMessage());

        } else if (e instanceof DeadlockLoserDataAccessException) {
            // 교착 상태로 인해 현재 작업이 실패했습니다.
            log.debug("**DeadlockLoserDataAccessException {} ", e.getMessage());
            errCode = "1";
            errMsg = "교착 상태로 인한 현재 작업 실패";
        } else if (e instanceof CannotSerializeTransactionException) {

            log.debug("**CannotSerializeTransactionException {} ", e.getMessage());

        } else {
            errMsg = e.getMessage();
            log.error("[DataAccessException] getMessage {}", e.getMessage());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("errCode", errCode);
        map.put("errMsg", errMsg);
        ApiErrorResponse response = ApiErrorResponse.of(CommonErrorCode.REQUEST_IS_CONFLICT, map);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiErrorResponse> handleException(Exception e) {
        log.error("handleEntityNotFoundException", e);
        ApiErrorResponse response = ApiErrorResponse.of(CommonErrorCode.INTERNAL_SERVER_ERROR, e);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ApiErrorResponse> handleRuntimeException(RuntimeException e) {
        log.info("handleRuntimeException", e);
        ApiErrorResponse response = ApiErrorResponse.of(CommonErrorCode.INTERNAL_SERVER_ERROR, e);
        return new ResponseEntity<>(response, response.getStatus());
    }


    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException e) {
        log.error("handleBusinessException", e);
        ApiErrorResponse response = ApiErrorResponse.of(e.getErrorCode(), e);
        return new ResponseEntity<>(response, response.getStatus());
    }


}