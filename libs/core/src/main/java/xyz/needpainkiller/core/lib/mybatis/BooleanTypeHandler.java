package xyz.needpainkiller.core.lib.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanTypeHandler implements TypeHandler<Boolean> {

    private static final int YES = 1;
    private static final int NO = 0;

    @Override
    public void setParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, convert(parameter));
    }

    @Override
    public Boolean getResult(ResultSet rs, String columnName) throws SQLException {
        return convert(rs.getInt(columnName));
    }

    @Override
    public Boolean getResult(ResultSet rs, int columnIndex) throws SQLException {
        return convert(rs.getInt(columnIndex));
    }

    @Override
    public Boolean getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convert(cs.getInt(columnIndex));
    }

    private int convert(Boolean b) {
        if (b == null) return NO;
        return b ? YES : NO;
    }

    private Boolean convert(Integer s) {
        return s.equals(YES);
    }

}