package xyz.needpainkiller.api.file.model;

import org.apache.ibatis.type.MappedTypes;
import xyz.needpainkiller.lib.jpa.CodeEnumConverter;
import xyz.needpainkiller.lib.mybatis.CodeEnum;
import xyz.needpainkiller.lib.mybatis.CodeEnumTypeHandler;

import java.io.Serializable;
import java.util.Arrays;

public enum FileServiceType implements CodeEnum, Serializable {
    NONE(0),
    DEFAULT(1),
    ATTACH(2),
    SCRIPT(3),
    CAPTURE(99),
    OTHER(9999);

    private final int code;

    FileServiceType(int code) {
        this.code = code;
    }

    public static FileServiceType of(int code) {
        return Arrays.stream(values())
                .filter(v -> v.code == code)
                .findFirst().orElse(NONE);
    }

    public static FileServiceType nameOf(String name) {
        return Arrays.stream(values())
                .filter(v -> name.equals(v.name()))
                .findFirst().orElse(NONE);
    }


    public static boolean isExist(FileServiceType serviceType) {
        return !serviceType.equals(NONE);
    }

    public static boolean isExist(int code) {
        return !of(code).equals(NONE);
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @MappedTypes(FileServiceType.class)
    public static class TypeHandler extends CodeEnumTypeHandler<FileServiceType> {
        public TypeHandler() {
            super(FileServiceType.class);
        }
    }

    public static class Converter implements CodeEnumConverter<FileServiceType> {
        @Override
        public Integer convertToDatabaseColumn(FileServiceType attribute) {
            return attribute.getCode();
        }

        @Override
        public FileServiceType convertToEntityAttribute(Integer dbData) {
            return of(dbData);
        }
    }
}
