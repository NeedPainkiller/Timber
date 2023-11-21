package xyz.needpainkiller.core.lib.mybatis.translator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.*;

import javax.sql.DataSource;
import java.sql.SQLException;

public class DacSQLErrorCodeSQLExceptionTranslator implements SQLExceptionTranslator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DataSource dataSource;

    public DacSQLErrorCodeSQLExceptionTranslator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DataAccessException translate(String task, String sql, SQLException ex) {
        String dbName = null;
        try {
            dbName = JdbcUtils.extractDatabaseMetaData(dataSource, "getDatabaseProductName");
            if (dbName != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Database product name cached for DataSource [" + dataSource.getClass().getName() + '@'
                            + Integer.toHexString(dataSource.hashCode()) + "]: name is '" + dbName + "'");
                }
            }
        } catch (MetaDataAccessException mdaEx) {
            logger.warn("Error while extracting database product name - falling back to empty error codes", mdaEx);
        }

        SQLErrorCodes sqlErrorCodes = (dbName == null) ? new SQLErrorCodes() : SQLErrorCodesFactory.getInstance().getErrorCodes(dbName);

        //logger.debug("ex.code :: {}", ex.getErrorCode());

        return new SQLErrorCodeSQLExceptionTranslator(sqlErrorCodes).translate(task, sql, ex);
    }
}