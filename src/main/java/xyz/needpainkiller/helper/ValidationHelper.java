package xyz.needpainkiller.helper;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.util.Strings;
import xyz.needpainkiller.api.authentication.error.PasswordException;
import xyz.needpainkiller.api.user_hex.domain.error.UserException;
import xyz.needpainkiller.lib.exceptions.BusinessException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xyz.needpainkiller.lib.exceptions.CommonErrorCode.*;

@UtilityClass
public class ValidationHelper {

    private static final String PATTERN_MATCH_EMAIL = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
    private static final String PATTERN_MATCH_ALPHABET = ".*([a-zA-Z]).*";
    private static final String PATTERN_MATCH_NUMBERS = ".*([0-9]).*";
    private static final String PATTERN_MATCH_NUMBERS_SPECIALS = ".*([0-9`~!@#$%^&*()=_+\\\\-\\\\[\\\\]{}:;',./?\\\\\\\\|]).*";

    private static final String PATTERN_MATCH_SPECIALS = ".*([`~!@#$%^&*()=_+\\\\-\\\\[\\\\]{}:;',./?\\\\\\\\|]).*";

    public static boolean isEmailFormat(String source) {
        Pattern p = Pattern.compile(PATTERN_MATCH_EMAIL);
        Matcher m = p.matcher(source);
        return m.matches();
    }

    public static void checkEmailFormat(String email) throws BusinessException {
        if (!isEmailFormat(email)) {
            throw new BusinessException(USER_ID_IS_NOT_EMAIL_FORMAT);
        }
    }

    public static void checkUserData(String userId, String userNm) {
        if (Strings.isBlank(userId)) {
            throw new UserException(USER_ID_EMPTY);
        } else if (userId.length() < 4 || userId.length() > 15) {
            throw new UserException(USER_ID_LENGTH);
        } else if (!Pattern.matches(PATTERN_MATCH_ALPHABET, userId)) { // 문자 미포함
//            throw new UserException(USER_ID_NEED_ALPHABET); // 비활성화
        } else if (!Pattern.matches(PATTERN_MATCH_NUMBERS, userId)) { // 숫자 | 특수문자 미포함
//            throw new UserException(USER_ID_NEED_NUM); // 비활성화
        } else if (Pattern.matches(PATTERN_MATCH_SPECIALS, userId)) { // 특수문자 X
            throw new UserException(USER_ID_IGNORE_SPECIAL);
        }

        if (Strings.isBlank(userNm)) {
            throw new UserException(USER_NM_EMPTY);
        }
    }

    public static void checkUserData(String userId, String userNm, String userPwd) {
        checkUserData(userId, userNm);
        checkPassword(userPwd);
    }

/*    public static void checkUserData(String userId, String userNm, String email) {
        checkUserData(userId, userNm);
        checkEmailFormat(email);
    }

    public static void checkUserData(String userId, String userNm, String email, String userPwd) {
        checkUserData(userId, userNm);
        checkEmailFormat(email);
        checkPassword(userPwd);
    }*/

    public static boolean isEmptyInteger(Integer integer) {
        if (integer == null) {
            return true;
        }
        return integer.equals(0);
    }

    public static boolean isEmptyLong(Long aLong) {
        if (aLong == null) {
            return true;
        }
        return aLong.equals(0L);
    }

    public static void checkPassword(String currentPW, String newPW, String confirmPW) {
        if (currentPW.length() == 0 || newPW.length() == 0 || confirmPW.length() == 0) { // 빈값
            throw new PasswordException(PASSWORD_EMPTY);
        } else if (newPW.length() < 6 || newPW.length() > 512) { // 자리수 틀림
            throw new PasswordException(PASSWORD_LENGTH);
        } else if (!newPW.equals(confirmPW)) { // 변경 패스워트과 검증 패스워드 불일치
            throw new PasswordException(PASSWORD_NOT_MATCH_CONFIRM);
        } else if (newPW.equals(currentPW)) { // 기존 패스워드와 동일
            throw new PasswordException(PASSWORD_SAME_PASSWORD);
        } else if (!Pattern.matches(PATTERN_MATCH_ALPHABET, newPW)) { // 문자 미포함
            throw new PasswordException(PASSWORD_NEED_ALPHABET);
        } else if (!Pattern.matches(PATTERN_MATCH_NUMBERS_SPECIALS, newPW)) { // 숫자 | 특수문자 미포함
            throw new PasswordException(PASSWORD_NEED_NUM_SPECIAL);
        }
    }

    public static void checkPassword(String password) {
        if (password.length() == 0) { // 빈값
            throw new PasswordException(PASSWORD_EMPTY);
        } else if (password.length() < 10 || password.length() > 512) { // 자리수 틀림
            throw new PasswordException(PASSWORD_LENGTH);
        } else if (!Pattern.matches(PATTERN_MATCH_ALPHABET, password)) { // 문자 미포함
            throw new PasswordException(PASSWORD_NEED_ALPHABET);
        } else if (!Pattern.matches(PATTERN_MATCH_NUMBERS_SPECIALS, password)) { // 숫자 | 특수문자 미포함
            throw new PasswordException(PASSWORD_NEED_NUM_SPECIAL);
        }
    }


    public static void checkAnyRequiredEmpty(String... source) {
        for (String s : source) {
            if (Strings.isBlank(s)) {
                throw new BusinessException(REQUEST_REQUIRED_DATA_EMPTY);
            }
        }
    }

    public static void checkAnyRequiredEmpty(Integer... source) {
        for (Integer s : source) {
            if (isEmptyInteger(s)) {
                throw new BusinessException(REQUEST_REQUIRED_DATA_EMPTY);
            }
        }
    }

    public static void checkAnyRequiredEmpty(Long... source) {
        for (Long s : source) {
            if (isEmptyLong(s)) {
                throw new BusinessException(REQUEST_REQUIRED_DATA_EMPTY);
            }
        }
    }

}
