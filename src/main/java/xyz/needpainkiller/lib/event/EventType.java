package xyz.needpainkiller.lib.event;

import lombok.Getter;
import org.apache.ibatis.type.MappedTypes;
import xyz.needpainkiller.lib.jpa.CodeEnumConverter;
import xyz.needpainkiller.lib.mybatis.CodeEnum;
import xyz.needpainkiller.lib.mybatis.CodeEnumTypeHandler;

import java.util.Arrays;

public enum EventType implements CodeEnum {

    NONE(0, "미확인"),
    CREATE(1, "생성"),
    UPDATE(2, "수정"),
    DELETE(3, "삭제");

    private final int code;
    @Getter
    private final String label;

    EventType(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public static EventType of(int code) {
        return Arrays.stream(values())
                .filter(v -> v.code == code)
                .findFirst().orElse(NONE);
    }

    public static EventType nameOf(String name) {
        return Arrays.stream(values())
                .filter(v -> name.equals(v.name()))
                .findFirst().orElse(NONE);
    }

    @Override
    public Integer getCode() {
        return null;
    }

    @MappedTypes(EventType.class)
    public static class TypeHandler extends CodeEnumTypeHandler<EventType> {
        public TypeHandler() {
            super(EventType.class);
        }
    }

    public static class Converter implements CodeEnumConverter<EventType> {
        @Override
        public Integer convertToDatabaseColumn(EventType attribute) {
            return attribute.getCode();
        }

        @Override
        public EventType convertToEntityAttribute(Integer dbData) {
            return of(dbData);
        }
    }
}
