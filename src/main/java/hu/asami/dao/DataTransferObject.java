package hu.asami.dao;

import jakarta.servlet.http.HttpServletRequest;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class DataTransferObject {
    //region Fields
    private Dao dao;
    private DataSource dataSource;

    //endregion
    //region Insert
    public Object insert() throws SQLException {
        checkDao();
        beforeUpdate();
        Object ret = this.dao.insert(this);
        afterUpdate();
        return ret;
    }

    //endregion
    //region Update
    public boolean update() throws SQLException {
        checkDao();
        beforeInsert();
        boolean ret = this.dao.update(this) == 1;
        afterInsert();
        return ret;
    }

    //endregion
    //region Delete
    public boolean delete() throws SQLException {
        checkDao();
        beforeDelete();
        boolean ret = this.dao.delete(this);
        afterDelete();
        return ret;
    }

    //endregion
    //region Private
    private Class<? extends DataTransferObject> getType() {
        return this.getClass();
    }

    private void checkDao() {
        if (this.dataSource == null) {
            throw new UnsupportedOperationException();
        }
        if (this.dao == null) {
            this.dao = new Dao(getType(), dataSource);
        }
    }

    //endregion
    //region Abstract
    protected abstract void beforeUpdate();

    protected abstract void afterUpdate();

    protected abstract void beforeInsert();

    protected abstract void afterInsert();

    protected abstract void beforeDelete();

    protected abstract void afterDelete();

    //endregion
    protected DataTransferObject(DataSource dataSource) {
        this.dataSource = dataSource;
        this.dao = new Dao(getType(), dataSource);
    }

    protected DataTransferObject(HttpServletRequest request) {
        List<Field> fields = Arrays.stream(this.getType().getDeclaredFields()).toList();
        for (Field f : fields) {
            try {
                this.getType().getDeclaredMethod("set" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1), f.getType()).invoke(this, request.getParameter(f.getName()));
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    protected DataTransferObject(DataSource dataSource, HttpServletRequest request) {
        this.dataSource = dataSource;
        List<Field> fields = Arrays.stream(this.getType().getDeclaredFields()).toList();
        for (Field f : fields) {
            try {
                this.getType().getDeclaredMethod("set" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1), f.getType()).invoke(this, request.getParameter(f.getName()));
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    protected DataTransferObject() {

    }
}
