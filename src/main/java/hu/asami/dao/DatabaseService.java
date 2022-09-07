package hu.asami.dao;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class DatabaseService {

    private Dao dao;
    private final DataSource dataSource;

    public DatabaseService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T extends DataTransferObject> List<T> selectList(Class<T> type, String sql, Object[] params) throws SQLException {
        createDao(type);
        return (List<T>) this.dao.select(sql, params);
    }

    public <T extends DataTransferObject> T selectRecord(Class<T> type, String sql, Object[] params) throws SQLException, DaoMoreThenOneResultException {
        createDao(type);
        List<T> ret = (List<T>) this.dao.select(sql, params);
        if(ret.size() == 1){
            return ret.get(0);
        }
        throw new DaoMoreThenOneResultException("More then one record returned from query.");
    }

    public <T extends DataTransferObject> Object insertRecord(Class<T> type, T o) throws SQLException {
        createDao(type);
        return this.dao.insert(o);
    }

    public <T extends DataTransferObject> boolean insertList(Class<T> type, List<T> list, boolean stopAtError) throws SQLException {
        createDao(type);
        if(stopAtError){
            return insertListStop(list);
        } else {
            return insertListNoStop(list);
        }
    }
    private <T extends DataTransferObject> boolean insertListStop(List<T> list) throws SQLException {
        boolean hiba = false;
        for(T t : list){
            if(this.dao.insert(t) == null){
                hiba = true;
            }
        }
        return hiba;
    }
    private <T extends DataTransferObject> boolean insertListNoStop(List<T> list){
        try{
            for(T t : list){
                this.dao.insert(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private <T extends DataTransferObject> int delete(Class<T> type, String sql, Object[] params) throws SQLException {
        createDao(type);
        return this.dao.delete(sql, params);
    }

    private <T extends DataTransferObject> int delete(Class<T> type, String sql) throws SQLException {
        createDao(type);
        return this.dao.delete(sql);
    }

    private <T extends DataTransferObject> int delete(Class<T> type, T o) throws SQLException {
        createDao(type);
        return this.dao.delete(o);
    }

    private int nonQuery(String sql, Object[] params) throws SQLException {
        return this.dao.nonQuery(sql, params);
    }

    private int nonQuery(String sql) throws SQLException {
        return this.dao.nonQuery(sql);
    }

    private <T extends DataTransferObject> void createDao(Class<T> type){
        if(!this.dao.getType().isAssignableFrom(type)){
            this.dao = new Dao(type, this.dataSource);
        }
    }
}