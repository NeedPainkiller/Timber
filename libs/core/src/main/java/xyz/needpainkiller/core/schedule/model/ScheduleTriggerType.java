package xyz.needpainkiller.core.schedule.model;

import xyz.needpainkiller.core.lib.mybatis.CodeEnum;
import xyz.needpainkiller.core.lib.mybatis.CodeEnumTypeHandler;
import org.apache.ibatis.type.MappedTypes;

import java.io.Serializable;
import java.util.Arrays;

public enum ScheduleTriggerType implements CodeEnum, Serializable {
    NONE(1),
    SIMPLE(1),
    CRON(2),
    SCHEDULE(3);

    private final int code;

    ScheduleTriggerType(int code) {
        this.code = code;
    }

    public static ScheduleTriggerType of(int code) {
        return Arrays.stream(values())
                .filter(v -> v.code == code)
                .findFirst().orElse(NONE);
    }

    public static ScheduleTriggerType nameOf(String name) {
        return Arrays.stream(values())
                .filter(v -> name.equals(v.name()))
                .findFirst().orElse(NONE);
    }


    public static boolean isExist(ScheduleTriggerType authorityType) {
        return !authorityType.equals(NONE);
    }

    public static boolean isExist(int code) {
        return !of(code).equals(NONE);
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @MappedTypes(ScheduleTriggerType.class)
    public static class TypeHandler extends CodeEnumTypeHandler<ScheduleTriggerType> {
        public TypeHandler() {
            super(ScheduleTriggerType.class);
        }
    }
}
