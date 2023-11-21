package xyz.needpainkiller.core.lib.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The type Code enum type handler.
 * Mybatis 전용 타입 핸들러
 *
 * @param <E> the type parameter
 */
public class CodeEnumTypeHandler<E extends Enum<E>> implements TypeHandler<CodeEnum> {

    private final Class<E> type;

    /**
     * Instantiates a new Code enum type handler.
     *
     * @param type the type
     */
    public CodeEnumTypeHandler(Class<E> type) {
        this.type = type;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, CodeEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public CodeEnum getResult(ResultSet rs, String columnName) throws SQLException {
        Integer code = rs.getInt(columnName);
        return getCodeEnum(code);
    }

    @Override
    public CodeEnum getResult(ResultSet rs, int columnIndex) throws SQLException {
        Integer code = rs.getInt(columnIndex);
        return getCodeEnum(code);
    }

    @Override
    public CodeEnum getResult(CallableStatement cs, int columnIndex) throws SQLException {
        Integer code = cs.getInt(columnIndex);
        return getCodeEnum(code);
    }


    private CodeEnum getCodeEnum(Integer code) {
        if (code == null) {
            return null;
        }
        try {
            CodeEnum[] enumConstants = (CodeEnum[]) type.getEnumConstants();
            if (enumConstants == null) {
                return null;
            }
            for (CodeEnum codeNum : enumConstants) {
                Integer codeValue = codeNum.getCode();
                if (codeValue == null) {
                    continue;
                }
                if (codeValue.equals(code)) {
                    return codeNum;
                }
            }
            return null;
        } catch (RuntimeException e) {
            throw new TypeException("Can't make enum object '" + type + "'", e);
        }
    }
}