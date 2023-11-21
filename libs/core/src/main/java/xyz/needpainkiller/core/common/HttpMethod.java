package xyz.needpainkiller.core.common;

import org.apache.ibatis.type.MappedTypes;
import xyz.needpainkiller.core.lib.jpa.CodeEnumConverter;
import xyz.needpainkiller.core.lib.mybatis.CodeEnum;
import xyz.needpainkiller.core.lib.mybatis.CodeEnumTypeHandler;

import java.util.Arrays;

public enum HttpMethod implements CodeEnum {
    NONE(0),
    GET(1), HEAD(2), POST(3), PUT(4), PATCH(5), DELETE(6), OPTIONS(7), TRACE(8);

    private final int code;

    HttpMethod(int code) {
        this.code = code;
    }

    public static HttpMethod of(int code) {
        return Arrays.stream(values())
                .filter(v -> v.code == code)
                .findFirst().orElse(NONE);
    }

    public static HttpMethod nameOf(String name) {
        return Arrays.stream(values())
                .filter(v -> name.equals(v.name()))
                .findFirst().orElse(NONE);
    }

    public static boolean hasPayload(HttpMethod httpMethod) {
        return httpMethod.equals(POST) || httpMethod.equals(PUT);
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @MappedTypes(HttpMethod.class)
    public static class TypeHandler extends CodeEnumTypeHandler<HttpMethod> {
        public TypeHandler() {
            super(HttpMethod.class);
        }
    }

    public static class Converter implements CodeEnumConverter<HttpMethod> {
        @Override
        public Integer convertToDatabaseColumn(HttpMethod attribute) {
            return attribute.getCode();
        }

        @Override
        public HttpMethod convertToEntityAttribute(Integer dbData) {
            return of(dbData);
        }
    }

}