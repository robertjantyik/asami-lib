package hu.asami.dao;

import hu.asami.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.StopWatch;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.postgresql.util.PGobject;
import org.springframework.lang.Nullable;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Dao<T extends DataTransferObject> {
    private Connection c;
    private PreparedStatement s;
    private Class<T> type;
    private String id;
    private DataSource dataSource;
    private StopWatch stopWatch;
    private StopWatch statementWatch;
    private List<Field> fields;
    private String table;
    private String sql;
    private List<Object> params = new ArrayList<>();

    public Class<T> getType() {
        return this.type;
    }

    public Dao(Class<T> type, DataSource dataSource){
        try {
            this.stopWatch = new StopWatch();
            this.statementWatch = new StopWatch();
            this.stopWatch.start();
            this.dataSource = dataSource;
            c = dataSource.getConnection();
            c.setAutoCommit(false);
            this.type = type;
            this.fields = Arrays.stream(type.getDeclaredFields()).filter(field -> field.getAnnotation(NotDatabaseField.class) == null).toList();
            this.table = type.getAnnotation(DbTable.class).value();
            this.id = "Dao<" + type.getName().substring(type.getName().lastIndexOf(".")).replaceAll("[.]", "") + ">";
            log.debug("Új DAO példány: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //region Insert
    public Object insert (T o) throws SQLException{
        try {
            checkConnection();
            createInsert(o);
            s = c.prepareStatement(this.sql);
            if(!this.params.isEmpty()){
                for(int i = 0; i < this.params.size(); i++){
                    s.setObject(i + 1, this.params.get(i));
                }
            }
            ResultSet rs = s.executeQuery();
            Object ret = null;
            while(rs.next()){
                ret = rs.getObject("id");
            }
            return ret;
        } catch (SQLException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | IOException e) {
            log.error(this.sql);
            e.printStackTrace();
            c.rollback();
        } finally {
            s.close();
            c.commit();
        }
        return null;
    }
    //endregion
    //region Update
    public int update(T o) throws SQLException {
        try {
            checkConnection();
            createUpdate(o);
            int ret;
            s = c.prepareStatement(this.sql);
            if(!this.params.isEmpty()){
                for(int i = 0; i < this.params.size(); i++){
                    s.setObject(i + 1, this.params.get(i));
                }
            }
            ret = s.executeUpdate();
            return ret;
        } catch (SQLException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | IOException e) {
            log.error(sql);
            e.printStackTrace();
            c.rollback();
        } finally {
            s.close();
            c.commit();
        }
        return 0;
    }
    //endregion
    //region Select
    public List<T> select(String sql) throws SQLException{
        return select(sql, null);
    }
    public List<T> select(String sql, Object[] params) throws SQLException{
        try {
            this.statementWatch.start();
            checkConnection();
            ResultSet rs;
            s = c.prepareStatement(sql);
            if(params != null) {
                for (int i = 0; i < params.length; i++) {
                    s.setObject(i + 1, params[i]);
                }
            }
            rs = s.executeQuery();
            log.debug(System.lineSeparator() + id + ": " + System.lineSeparator() + s.toString());
            this.statementWatch.stop();
            log.trace(id + ": ennyi idő volt a select: " + this.statementWatch.getTime());
            this.statementWatch.reset();
            this.statementWatch.start();
            return processSelect(rs);
        } catch (SQLException | InstantiationException | InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException | IOException e) {
            log.error(sql);
            e.printStackTrace();
            c.rollback();
        } finally {
            this.statementWatch.stop();
            log.trace(id + ": ennyi idő volt a feldolgozás: " + this.statementWatch.getTime());
            this.statementWatch.reset();
            s.close();
            c.commit();
        }
        return Collections.emptyList();
    }
    //endregion
    //region Delete
    public int delete(String sql) throws SQLException{
        return delete(sql, null);
    }
    public int delete(String sql, Object[] params) throws SQLException{
        try {
            checkConnection();
            int ret;

            s = c.prepareStatement(sql);
            if(params != null){
                for (int i = 0; i < params.length; i++) {
                    s.setObject(i + 1, params[i]);
                }
            }
            ret = s.executeUpdate();
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
            c.rollback();
        } finally {
            s.close();
            c.commit();
        }
        return 0;
    }
    public int delete(T o) throws SQLException{
        String sqlDelete = "";
        try {
            checkConnection();
            int ret;
            Object typeId = null;
            Optional<Field> idField = this.fields.stream().filter(f -> f.getAnnotation(hu.asami.annotations.Id.class) != null).findFirst();
            if(idField.isPresent()){
                typeId = o.getClass().getDeclaredMethod("get" + idField.get().getName().substring(0, 1).toUpperCase() + idField.get().getName().substring(1)).invoke(o);
            }
            sqlDelete = "delete from " + this.table + " where id = ?";
            s = c.prepareStatement(sqlDelete);
            s.setObject(1, typeId);
            ret = s.executeUpdate();
            return ret;
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            log.error(sqlDelete);
            e.printStackTrace();
            c.rollback();
        } finally {
            s.close();
            c.commit();
        }
        return 0;
    }
    //endregion
    //region NonQuery
    public int nonQuery(String sql) throws SQLException {
        return nonQuery(sql, null);
    }
    public int nonQuery(String sql, Object[] params) throws SQLException{
        try {
            checkConnection();
            int ret;
            s = c.prepareStatement(sql);
            if(params != null){
                for (int i = 0; i < params.length; i++) {
                    s.setObject(i + 1, params[i]);
                }
            }
            ret = s.executeUpdate();
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
            c.rollback();
        } finally {
            s.close();
            c.commit();
        }
        return 0;
    }
    //endregion

    private void checkConnection() throws SQLException {
        this.sql = "";
        this.params = new ArrayList<>();
        if (c.isClosed()){
            log.debug(id + ": connection closed, trying to reconnect.");
            c = this.dataSource.getConnection();
            c.setAutoCommit(false);
            log.debug(id + ":  new connection reopened.");
        }
    }
    private void createInsert(T o) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO " + this.table);
        StringBuilder values = new StringBuilder(" VALUES (");
        ListIterator<Field> iterator = this.fields.listIterator();
        boolean fieldNotEmpty = true;
        while(iterator.hasNext()) {
            Field field = iterator.next();
            String fieldName = field.getName();
            String fieldNameCamel = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Object fieldValue = o.getClass().getDeclaredMethod("get" + fieldNameCamel).invoke(o);
            if (fieldValue == null && field.getAnnotation(Null.class) == null) {
                continue;
            } else if (fieldNotEmpty) {
                sqlBuilder.append(" (");
                fieldNotEmpty = false;
            }
            sqlBuilder.append("\"").append(fieldName.toLowerCase()).append("\"");
            processFields(mapper, values, field, fieldValue);
            if (iterator.hasNext()) {
                sqlBuilder.append(" ,");
                values.append(" ,");
            }
        }
        if(!fieldNotEmpty){
            sqlBuilder.append(")");
            values.append(")");
            sqlBuilder.append(values);
        } else {
            sqlBuilder.append(" default values");
        }
        sqlBuilder.append(" returning id;");
        this.sql = sqlBuilder.toString().replace(" ,)", ")");
    }
    private void createUpdate(T o) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + this.table);
        ListIterator<Field> iterator = this.fields.listIterator();
        while(iterator.hasNext()) {
            Field field = iterator.next();
            String fieldName = field.getName();
            String fieldNameCamel = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Object fieldValue = o.getClass().getDeclaredMethod("get" + fieldNameCamel).invoke(o);
            sqlBuilder.append(iterator.previousIndex() > 0 ? " ," : " SET ").append("\"").append(fieldName.toLowerCase()).append("\" = ");
            processFields(mapper, sqlBuilder, field, fieldValue);
        }
        sqlBuilder.append(" WHERE id = ?");
        this.params.add(o.getClass().getDeclaredMethod("getId").invoke(o));
        this.sql = sqlBuilder.toString().replace(" ,)", ")");
    }
    private void processFields(ObjectMapper mapper, StringBuilder sql, Field field, Object fieldValue) throws SQLException, IOException {
        if (fieldValue instanceof List<?>) {
            if(field.getAnnotation(Json.class) != null) {
                sql.append(" ?");
                PGobject fieldJsonValue = new PGobject();
                fieldJsonValue.setType("json");
                fieldJsonValue.setValue(mapper.writeValueAsString(fieldValue));
                this.params.add(fieldJsonValue);
            } else {
                ListIterator<?> fieldValueIterator = ((List<?>) fieldValue).listIterator();
                sql.append("'{");
                while(fieldValueIterator.hasNext()){
                    if(fieldValueIterator.previousIndex() > -1){
                        sql.append(" ,");
                    }
                    sql.append(fieldValueIterator.next());
                }
                sql.append("}'");
            }
        } else {
            sql.append(" ?");
            this.params.add(fieldValue);
        }
    }
    private List<T> processSelect(ResultSet rs) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        List<T> ret = new ArrayList<>();
        while(rs.next()){
            T typeObject = type.getDeclaredConstructor().newInstance();
            ListIterator<Field> fieldIterator = this.fields.listIterator();
            while(fieldIterator.hasNext()){
                Field field = fieldIterator.next();
                String fieldName = field.getName();
                String fieldNameCamel = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method setter = type.getDeclaredMethod("set" + fieldNameCamel, field.getType());
                if(field.getType().isAssignableFrom(List.class)) {
                    if (field.getAnnotation(Json.class) != null) {
                        PGobject pGobject = (PGobject) rs.getObject(fieldName);
                        ObjectMapper mapper = new ObjectMapper();
                        Object fieldValue = mapper.readValue(pGobject.getValue(), field.getType());
                        setter.invoke(typeObject, fieldValue);
                    } else {
                        Array array = rs.getArray(fieldName);
                        if(array != null) {
                            List<Object> arrayList = new ArrayList<>(Arrays.asList((Object[]) array.getArray()));
                            setter.invoke(typeObject, arrayList);
                        } else {
                            setter.invoke(typeObject, Collections.emptyList());
                        }
                    }
                } else {
                    setter.invoke(typeObject, rs.getObject(fieldName));
                }
            }
            ret.add(typeObject);
        }
        return ret;
    }

    @PreDestroy
    private void close() throws SQLException {
        if(!c.isClosed()) {
            log.debug(id + ": closing connection.");
            c.rollback();
            c.close();
        }
        log.trace(id + ": \r\n bean élettartam: " + this.stopWatch.getTime());
    }
}
