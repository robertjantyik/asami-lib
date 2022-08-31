package hu.asami.dao;

import hu.asami.annotations.DbTable;
import hu.asami.annotations.NotDatabaseField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.StopWatch;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Dao<T> {
    private Connection c;
    private PreparedStatement s;
    private Class<T> type;
    private String id;
    private DataSource dataSource;
    private StopWatch stopWatch;
    private StopWatch statementWatch;

    public Dao(Class<T> type, DataSource dataSource){
        try {
            this.stopWatch = new StopWatch();
            this.statementWatch = new StopWatch();
            this.stopWatch.start();
            this.dataSource = dataSource;
            c = dataSource.getConnection();
            c.setAutoCommit(false);
            this.type = type;
            this.id = "Dao<" + type.getName().substring(type.getName().lastIndexOf(".")).replaceAll("[.]", "") + ">";
            log.debug("Új DAO példány: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Long insert (T o) throws SQLException{
        StringBuilder sql = new StringBuilder();
        String sSql = "";
        Map<Integer, byte[]> byteaList = new HashMap<>();
        try {
            boolean empty = true;
            checkConnection();
            StringBuilder values = new StringBuilder(" VALUES(");
            sql.append("INSERT INTO ");
            sql.append(o.getClass().getAnnotation(DbTable.class).value());
            Field[] f = o.getClass().getDeclaredFields();
            List<Field> fList = Arrays.stream(f).filter(field -> field.getAnnotation(NotDatabaseField.class) == null).collect(Collectors.toList());
            f = fList.toArray(new Field[]{});
            for(int i = 0; i < f.length; i++){
                String str = f[i].getName();
                Object fo = o.getClass().getDeclaredMethod("get" + str.substring(0, 1).toUpperCase() + str.substring(1))
                        .invoke(o);
                if(fo == null){
                    continue;
                } else if(empty){
                    sql.append("(");
                    empty = false;
                }
                sql.append("\"" + str.toLowerCase(Locale.ROOT) + "\"");
                if(fo instanceof String){
                    values.append("'" + fo + "'");
                } else if(fo instanceof Long){
                    values.append(fo);
                } else if(fo instanceof Integer){
                    values.append(fo);
                } else if(Boolean.TRUE.equals(fo) || Boolean.FALSE.equals(fo)){
                    values.append(fo);
                } else if(fo instanceof List){
                    values.append("'{");
                    for(int k = 0; k < ((List<?>) fo).size(); k++){
                        values.append((k==0?"":" ,") + ((List<?>) fo).get(k));
                    }
                    values.append("}'");
                } else if(fo.getClass().getName().equals("java.util.Date")) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    values.append("'" + df.format(fo) + "'");
                } else if(fo instanceof byte[]){
                    values.append("?");
                    byteaList.put(1, (byte[]) fo);
                } else {
                    log.error(id + ": nem feldolgozott osztály: " + fo.getClass().getName());
                }
                if(i < f.length){
                    sql.append(" ,");
                    values.append(" ,");
                }
            }
            if(!empty) {
                values.append(")");
                sql.append(")");
                sql.append(values);
            } else {
                sql.append(" default values");
            }
            sql.append(" returning id");
            sSql = sql.toString().replace(" ,)", ")");
            s = c.prepareStatement(sSql);
            if(byteaList.size() > 0){
                for(int i = 1; i <= byteaList.size(); i++){
                    s.setBytes(i, byteaList.get(i));
                }
            }
            ResultSet rs = s.executeQuery();
            Long ret = null;
            while(rs.next()){
                ret = rs.getLong("id");
            }
            return ret;
        } catch (SQLException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error(sSql);
            e.printStackTrace();
            c.rollback();
        } finally {
            s.close();
            c.commit();
        }
        return null;
    }

    public int update(T o) throws SQLException {
        StringBuilder sql = new StringBuilder();
        try {
            checkConnection();
            sql.append("UPDATE ");
            String dbTable = o.getClass().getAnnotation(DbTable.class).value();
            sql.append(dbTable);
            Field[] f = o.getClass().getDeclaredFields();
            List<Field> fList = Arrays.stream(f).filter(field -> field.getAnnotation(NotDatabaseField.class) == null).collect(Collectors.toList());
            f = fList.toArray(new Field[]{});
            for(int i = 0; i < f.length; i++){
                String str = f[i].getName();
                Object fo = o.getClass().getDeclaredMethod("get" + str.substring(0, 1).toUpperCase() + str.substring(1))
                        .invoke(o);
                sql.append((i==0?" SET ":" ,") + "\"" + str.toLowerCase(Locale.ROOT) + "\"" + " = ");
                if(fo == null){
                    sql.append("null");
                }else if(fo instanceof String){
                    sql.append("'" + fo + "'");
                } else if(fo instanceof Long){
                    sql.append(fo);
                } else if(fo instanceof Integer){
                    sql.append(fo);
                } else if(Boolean.TRUE.equals(fo) || Boolean.FALSE.equals(fo)){
                    sql.append(fo);
                } else if(fo instanceof List){
                    sql.append("'{");
                    for(int k = 0; k < ((List<?>) fo).size(); k++){
                        sql.append((k==0?"":" ,") + ((List<?>) fo).get(k));
                    }
                    sql.append("}'");
                }else if(fo.getClass().getName().equals("java.util.Date")){
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    sql.append("'" + df.format(fo) + "'");
                } else {
                    log.error(id + ": nem feldolgozott osztály: " + fo.getClass().getName());
                }
            }
            sql.append(" WHERE " + dbTable + ".id = " + o.getClass().getDeclaredMethod("getId")
                    .invoke(o));
            int ret;
            s = c.prepareStatement(sql.toString());
            ret = s.executeUpdate();
            return ret;
        } catch (SQLException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error(sql.toString());
            e.printStackTrace();
            c.rollback();
        } finally {
            s.close();
            c.commit();
        }
        return 0;
    }

    public List<T> select(String sql) throws SQLException{
        return select(sql, null);
    }
    public List<T> select(String sql, Object[] params) throws SQLException{
        try {
            this.statementWatch.start();
            checkConnection();
            List<T> ret = new ArrayList<>();
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
            Field[] f = type.getDeclaredFields();
            List<Field> fList = Arrays.stream(f).filter(field -> field.getAnnotation(NotDatabaseField.class) == null).collect(Collectors.toList());
            f = fList.toArray(new Field[]{});
            while(rs.next()){
                Object o = type.getDeclaredConstructor().newInstance();
                for(int i = 0; i < f.length; i++){
                    String str = f[i].getName();
                    try{
                        if(f[i].getType().getName().equals(List.class.getName())){
                           Array array = rs.getArray(str.toLowerCase(Locale.ROOT));
                           if(array == null){
                               continue;
                           }
                           Method method = type.getDeclaredMethod("set" + str.substring(0, 1).toUpperCase() + str.substring(1), Long[].class);
                           method.invoke(o, array.getArray());

                        } else if(f[i].getType().getName().equals(java.util.Date.class.getName())){
                            Method method = type.getDeclaredMethod("set" + str.substring(0, 1).toUpperCase() + str.substring(1), java.util.Date.class);
                            method.invoke(o, new java.util.Date(rs.getDate(str.toLowerCase(Locale.ROOT)).getTime()));
                        } else if(f[i].getType().getName().equals(byte[].class.getName()) ){
                            Method method = type.getDeclaredMethod("set" + str.substring(0, 1).toUpperCase() + str.substring(1), byte[].class);
                            method.invoke(o, rs.getBytes(str.toLowerCase(Locale.ROOT)));
                        } else {
                            Method method = type.getDeclaredMethod("set" + str.substring(0, 1).toUpperCase() + str.substring(1), f[i].getType());
                            method.invoke(o, rs.getObject(str.toLowerCase(Locale.ROOT), f[i].getType()));
                        }
                    } catch (SQLException | IllegalArgumentException e){
                        log.error(id + ": ERROR: field: " + str, e);
                        continue;
                    }
                }
                ret.add((T) o);
            }
            return ret;
        } catch (SQLException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
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
        return null;
    }

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
            //c.close();
        }
        return 0;
    }

    public int delete(T o) throws SQLException{
        String sql = "";
        try {
            checkConnection();
            int ret;
            Long id = Long.valueOf((String) o.getClass().getDeclaredMethod("getId").invoke(o));
            String table = o.getClass().getAnnotation(DbTable.class).value();
            sql = "delete from " + table + " where id = ?";
            s = c.prepareStatement(sql);
            s.setLong(1, id);
            ret = s.executeUpdate();
            return ret;
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            log.error(sql);
            e.printStackTrace();
            c.rollback();
        } finally {
            s.close();
            c.commit();
            //c.close();
        }
        return 0;
    }

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
    private void checkConnection() throws SQLException {
        if (c.isClosed()){
            log.debug(id + ": connection closed, trying to reconnect.");
            c = this.dataSource.getConnection();
            c.setAutoCommit(false);
            log.debug(id + ":  new connection reopened.");
        }
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
