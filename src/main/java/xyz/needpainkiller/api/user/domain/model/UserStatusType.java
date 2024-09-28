package xyz.needpainkiller.api.user.domain.model;

import lombok.Getter;
import org.apache.ibatis.type.MappedTypes;
import xyz.needpainkiller.lib.jpa.CodeEnumConverter;
import xyz.needpainkiller.lib.mybatis.CodeEnum;
import xyz.needpainkiller.lib.mybatis.CodeEnumTypeHandler;

import java.util.Arrays;

public enum UserStatusType implements CodeEnum {


    NONE(0, false, "미확인"),
    OK(1, true, "사용"),
    OK_SSO(10, true, "사용"),
    NOT_VERIFIED(2, false, "미인증"),
    LOCKED(3, false, "잠금"),
    NOT_USED(4, false, "미사용"),
    WITHDRAWAL(5, false, "미승인");

    private final int code;
    @Getter
    private final boolean loginable;
    @Getter
    private final String label;

    UserStatusType(int code, boolean loginable, String label) {
        this.code = code;
        this.loginable = loginable;
        this.label = label;
    }

    public static UserStatusType of(int code) {
        return Arrays.stream(values())
                .filter(v -> v.code == code)
                .findFirst().orElse(NONE);
    }

    public static UserStatusType nameOf(String name) {
        return Arrays.stream(values())
                .filter(v -> name.equals(v.name()))
                .findFirst().orElse(NONE);
    }

    public static boolean isExist(UserStatusType status) {
        return !status.equals(NONE);
    }

    public static boolean isExist(int code) {
        return !of(code).equals(NONE);
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @MappedTypes(UserStatusType.class)
    public static class TypeHandler extends CodeEnumTypeHandler<UserStatusType> {
        public TypeHandler() {
            super(UserStatusType.class);
        }
    }

    public static class Converter implements CodeEnumConverter<UserStatusType> {
        @Override
        public Integer convertToDatabaseColumn(UserStatusType attribute) {
            return attribute.getCode();
        }

        @Override
        public UserStatusType convertToEntityAttribute(Integer dbData) {
            return of(dbData);
        }
    }

}
