package com.tly.bigdata.util;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.tly.bigdata.component.LinkedCaseInsensitiveMap;
import com.tly.bigdata.exception.CommonRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * <pre>
 * JDBC工具类
 * </pre>
 * @author tly  1170382650@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:    修改人：  修改日期:     修改内容:
 * </pre>
 */
public class JdbcUtil {
    protected static final Logger logger = LoggerFactory.getLogger(JdbcUtil.class);

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 获取数据库名字
     * @param conn
     * @return
     */
    public static String getDbName (Connection conn) {
        try {
            /**
             * Starting in MySQL-4.1, "schema" is an alias for "database", but it doesn't act like a SQL-standard schema, 
             * so we don't support the concept in the JDBC driver.
             * From URL: https://forums.mysql.com/read.php?39,137564,137629#msg-137629 
             */
            return conn.getCatalog(); 
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取某个数据库连接对应数据库的所有表名
     * 
     * @param conn
     *            只是使用了一下, 不会关闭
     * @return
     */
    public static List<String> getAllTableNames(Connection conn) {
        List<String> list = new ArrayList<>();

        String sql = "SHOW TABLES";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        }
        catch (SQLException e) {
            throw new CommonRuntimeException(e, e.getMessage());
        }
        finally {
            close(rs, pstmt);
        }

        return list;
    }

    /**
     * 获取一个数据库连接; 使用完毕, 请自行关闭
     * 
     * @param config
     * @return
     */
    public static Connection getConnection(DBConfig config) {
        String url = String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull",
                config.getHost(), config.getPort(), config.getDbName());
        try {
            return DriverManager.getConnection(url, config.getUsername(), config.getPassword());
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取一行数据
     * @param connection
     * @param sql
     * @return
     */
    public Map<String, Object> queryForObject(Connection connection, String sql) {
        List<Map<String, Object>> results = queryForList(connection, sql);
        if (results != null && results.size() > 0) {
            return results.get(0);
        }
        return null;
    }

    /**
     * 获取一组数据
     * @param connection
     * @param sql
     * @return
     */
    public static List<Map<String, Object>> queryForList(Connection connection, String sql) {
        CheckUtil.checkSQLInject(sql);
        
        Statement stmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> results = new LinkedList<>();
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        catch (Exception e) {
            throw new CommonRuntimeException(e, e.getMessage());
        }
        finally {
            close(rs, stmt);
        }
        return results;
    }

    public static void batchInsertData(List<String> sqls){
        Connection conn = getConnection(null);

        PreparedStatement ps = null;
        try{
            conn.setAutoCommit(false);
            for (String sql: sqls) {
                ps = conn.prepareStatement(sql);
                ps.addBatch();
            }
            // 批量更新
            ps.executeBatch();

            // 提交事务
            conn.commit();

        }catch(Exception e){
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }finally {
            close(null, ps, conn);
        }
    }

    /**
     * 在 ResultSet 读取一行数据
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Map<String, Object> mapOfColValues = new LinkedCaseInsensitiveMap<Object>(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            // 字段名
            String field = lookupColumnName(rsmd, i);
            // 字段值
            Object obj = getColumnValue(rs, i);
            mapOfColValues.put(field, obj);
        }
        return mapOfColValues;
    }

    /**
     * 获取 ResultSet行 的 某列的值
     * @param rs
     * @param index
     * @return
     */
    public static Object getColumnValue(ResultSet rs, int index) {
        try {
            Object obj = rs.getObject(index);
            if (obj == null) {
                return null;
            }
            if (obj instanceof Blob) {
                obj = rs.getBytes(index);
            }
            else if (obj instanceof Clob) {
                obj = rs.getString(index);
            }
            else if (obj != null && obj instanceof java.sql.Date) {
                if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
                    obj = rs.getTimestamp(index);
                }
            }
            else if (Types.BOOLEAN == rs.getMetaData().getColumnType(index)) {
                obj = rs.getInt(index);
            }
            return obj;
        }
        catch (SQLException e) {
            throw new CommonRuntimeException(e, e.getMessage());
        }
    }

    /**
     * 获取 ResultSetMetaData 第 columnIndex 列的列名
     * 
     * @param resultSetMetaData
     * @param columnIndex
     * @return
     */
    private static String lookupColumnName(ResultSetMetaData resultSetMetaData, int columnIndex) {
        try {
            String name = resultSetMetaData.getColumnLabel(columnIndex);
            if (StringUtil.isNullOrEmpty(name)) {
                name = resultSetMetaData.getColumnName(columnIndex);
            }
            return name;
        }
        catch (SQLException e) {
            throw new CommonRuntimeException(e, e.getMessage());
        }
    }

    /**
     * 依次关闭 ResultSet, Statement
     * 
     * @param rs
     * @param stmt
     */
    public static void close(ResultSet rs, Statement stmt) {
        closeResultSet(rs);
        closeStatement(stmt);
    }

    /**
     * 依次关闭 Statement, Connection
     * 
     * @param stmt
     * @param conn
     */
    public static void close(Statement stmt, Connection conn) {
        close(null, stmt, conn);
    }

    /**
     * 依次关闭 ResultSet, Statement, Connection
     * 
     * @param rs
     * @param stmt
     * @param conn
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        closeResultSet(rs);
        closeStatement(stmt);
        closeConnection(conn);
    }

    /**
     * 关闭 ResultSet
     * 
     * @param rs
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            }
            catch (SQLException e) {
                logger.error("closeResultSet failed. msg=" + e.getMessage(), e);
            }
        }
    }

    /**
     * 关闭 Statement & CallableStatement & PreparedStatement
     * 
     * @param pstmt
     */
    public static void closeStatement(Statement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            }
            catch (SQLException e) {
                logger.error("closePreparedStatement failed. msg=" + e.getMessage(), e);
            }
        }
    }

    /**
     * 关闭 Connection
     * 
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true);
                }

                conn.close();
            }
            catch (SQLException e) {
                logger.error("closeConnection failed. msg=" + e.getMessage(), e);
            }
        }
    }

    /**
     * 获取 timestamp 类型数据, 并转换成 long
     * 
     * @param rs
     * @param column
     * @return
     * @throws SQLException
     */
    public static long getLongFromTimestamp(ResultSet rs, String column) throws SQLException {
        try {
            Timestamp ts = rs.getTimestamp(column);
            if (ts == null) {
                return 0;
            }

            return ts.getTime();
        }
        catch (SQLException e) {
            String msg = e.getMessage();
            if (msg != null && msg.endsWith("can not be represented as java.sql.Timestamp")) {
                logger.warn("jdbcUrl should be contains 'zeroDateTimeBehavior=convertToNull'");
                return 0L;
            }

            throw e;
        }
    }

    /**
     * 获取一个 Json类型 的数据
     * 
     * @param type
     * @param rs
     * @param column
     * @return
     * @throws SQLException
     */
    public static <T> T getJson(com.google.gson.reflect.TypeToken<T> type, ResultSet rs, String column) throws SQLException {
        String json = rs.getString(column);
        if (json == null) {
            return null;
        }

        return GsonUtil.fromJson(json, type.getType());
    }

    public static class DBConfig {
        private final String host; // 数据库host
        private final int port; // 数据库port
        private final String dbName; // 数据库名
        private final String username; // 数据库账号
        private final String password; // 数据库密码

        public DBConfig(String host, int port, String dbName, String username, String password) {
            super();

            CheckUtil.checkNotNull("DBConfig.host", host);
            CheckUtil.checkPositiveNumber("DBConfig.port", port);
            CheckUtil.checkNotNull("DBConfig.dbName", dbName);
            CheckUtil.checkNotNull("DBConfig.username", username);
            CheckUtil.checkNotNull("DBConfig.password", password);

            this.host = host;
            this.port = port;
            this.dbName = dbName;
            this.username = username;
            this.password = password;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getDbName() {
            return dbName;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public String toString() {
            return "DBConfig [host=" + host + ", port=" + port + ", dbName=" + dbName + ", username=" + username + ", password=" + password
                    + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((dbName == null) ? 0 : dbName.hashCode());
            result = prime * result + ((host == null) ? 0 : host.hashCode());
            result = prime * result + ((password == null) ? 0 : password.hashCode());
            result = prime * result + port;
            result = prime * result + ((username == null) ? 0 : username.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DBConfig other = (DBConfig) obj;
            if (dbName == null) {
                if (other.dbName != null)
                    return false;
            }
            else if (!dbName.equals(other.dbName))
                return false;
            if (host == null) {
                if (other.host != null)
                    return false;
            }
            else if (!host.equals(other.host))
                return false;
            if (password == null) {
                if (other.password != null)
                    return false;
            }
            else if (!password.equals(other.password))
                return false;
            if (port != other.port)
                return false;
            if (username == null) {
                if (other.username != null)
                    return false;
            }
            else if (!username.equals(other.username))
                return false;
            return true;
        }
    }
}
