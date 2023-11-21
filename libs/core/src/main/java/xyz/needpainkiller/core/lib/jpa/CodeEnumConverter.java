package xyz.needpainkiller.core.lib.jpa;


import jakarta.persistence.AttributeConverter;
import xyz.needpainkiller.core.lib.mybatis.CodeEnum;

public interface CodeEnumConverter<E extends CodeEnum> extends AttributeConverter<E, Integer> {

}
