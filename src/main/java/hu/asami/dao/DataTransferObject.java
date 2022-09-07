package hu.asami.dao;

import javax.sql.DataSource;
import java.sql.SQLException;

public abstract class DataTransferObject {

    private Dao dao;
    private DataSource dataSource;

    private Class getType(){
        return this.getClass();
    }

    protected DataTransferObject(DataSource dataSource){
        this.dataSource = dataSource;
        this.dao = new Dao(getType(), dataSource);
    }
    protected DataTransferObject(){

    }

    private void checkDao(){
        if(this.dataSource == null){
            throw new UnsupportedOperationException();
        }
        if(this.dao == null) {
            this.dao = new Dao(getType(), dataSource);
        }
    }

    public Object insert() throws SQLException {
        checkDao();
        beforeUpdate();
        Object ret = this.dao.insert(this);
        afterUpdate();
        return ret;
    }

    public boolean update() throws SQLException {
        checkDao();
        beforeInsert();
        boolean ret = this.dao.update(this) == 1;
        afterInsert();
        return ret;
    }

    public boolean delete() throws SQLException {
        checkDao();
        beforeDelete();
        boolean ret = this.dao.delete(this) == 1;
        afterDelete();
        return ret;
    }
    protected abstract void beforeUpdate();
    protected abstract void afterUpdate();
    protected abstract void beforeInsert();
    protected abstract void afterInsert();
    protected abstract void beforeDelete();
    protected abstract void afterDelete();
}
