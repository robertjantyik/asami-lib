package hu.asami.dao;

import hu.asami.dao.exceptions.DaoMoreThenOneResultException;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * <p>Single bean service for one {@link Dao} instance.</p>
 */
@Slf4j
@SuppressWarnings("unchecked")
public class DatabaseService {
    //region Fields
    private Dao dao;
    private final DataSource dataSource;

    //endregion
    //region Select
    public <T extends DataTransferObject> List<T> selectList(Class<T> type, String sql, Object[] params) throws SQLException {
        createDao(type);
        return this.dao.select(sql, params);
    }

    public <T extends DataTransferObject> T selectRecord(Class<T> type, String sql, Object[] params) throws SQLException, DaoMoreThenOneResultException {
        createDao(type);
        List<T> ret = this.dao.select(sql, params);
        if (ret.size() == 1) {
            return ret.get(0);
        }
        throw new DaoMoreThenOneResultException("More then one record returned from query." + this.dao.getSql());
    }

    //endregion
    //region Insert
    public <T extends DataTransferObject> Object insert(Class<T> type, T o) throws SQLException {
        createDao(type);
        return this.dao.insert(o);
    }

    public <T extends DataTransferObject> boolean insertWithoutId(Class<T> type, T o) throws SQLException {
        createDao(type);
        return this.dao.insertWithoutId(o);
    }

    public <T extends DataTransferObject> void insertList(Class<T> type, List<T> list, boolean stopOnError) throws SQLException {
        createDao(type);
        this.dao.insertList(list, stopOnError);
    }

    //endregion
    //region Delete
    public <T extends DataTransferObject> int delete(Class<T> type, String sql, Object[] params) throws SQLException {
        createDao(type);
        return this.dao.delete(sql, params);
    }

    public <T extends DataTransferObject> int delete(Class<T> type, String sql) throws SQLException {
        createDao(type);
        return this.dao.delete(sql);
    }

    public <T extends DataTransferObject> boolean delete(Class<T> type, T o) throws SQLException {
        createDao(type);
        return this.dao.delete(o);
    }

    //endregion
    //region NonQuery
    public ResultSet nonQuery(String sql, Object[] params) throws SQLException {
        return this.dao.nonQuery(sql, params);
    }

    public ResultSet nonQuery(String sql) throws SQLException {
        return this.dao.nonQuery(sql);
    }

    //endregion
    //region Private
    private <T extends DataTransferObject> void createDao(Class<T> type) {
        if (!this.dao.getType().isAssignableFrom(type)) {
            this.dao = new Dao(type, this.dataSource);
        }
    }

    //endregion
    public DatabaseService(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}