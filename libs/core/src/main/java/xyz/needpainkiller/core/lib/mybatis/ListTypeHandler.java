package xyz.needpainkiller.core.lib.mybatis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ListTypeHandler implements TypeHandler<List<Integer>> {

    private static final ObjectMapper mapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

    @Override
    public void setParameter(PreparedStatement ps, int i, List<Integer> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, convert(parameter));
    }

    @Override
    public List<Integer> getResult(ResultSet rs, String columnName) throws SQLException {
        return convert(rs.getString(columnName));
    }

    @Override
    public List<Integer> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return convert(rs.getString(columnIndex));
    }

    @Override
    public List<Integer> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convert(cs.getString(columnIndex));
    }

    private String convert(List<Integer> integerSet) {
        try {
            return mapper.writeValueAsString(integerSet);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private List<Integer> convert(String content) {
        List<Integer> value;
        if (content != null && !content.isEmpty()) {
            try {
//                value = mapper.readValue(content, new TypeReference<Set<Integer>>() {
//                });
                value = mapper.readValue(content, List.class);
            } catch (JsonProcessingException e) {
                value = null;
            }
        } else {
            value = null;
        }
        return value;
    }

}