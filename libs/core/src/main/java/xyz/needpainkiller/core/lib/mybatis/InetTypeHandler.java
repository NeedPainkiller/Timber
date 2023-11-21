package xyz.needpainkiller.core.lib.mybatis;

import xyz.needpainkiller.core.helper.Inets;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InetTypeHandler implements TypeHandler<String> {

    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, Inets.aton(parameter));
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        return Inets.ntoa(rs.getLong(columnName));
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        return Inets.ntoa(rs.getLong(columnIndex));
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return Inets.ntoa(cs.getLong(columnIndex));
    }
}