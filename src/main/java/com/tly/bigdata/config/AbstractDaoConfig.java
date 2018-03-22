package com.tly.bigdata.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.tly.bigdata.exception.CommonRuntimeException;
import com.tly.bigdata.util.FileUtil;
import com.tly.bigdata.util.JdbcUtil;
import com.tly.bigdata.util.LangUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;


/**
 *
 * <pre>
 * 抽象DaoConfig
 *      子类的 poolName 建议使用 enum; 或者提供 getGameConnection/getLogConnection类似接口
 * </pre>
 * @author tly  1170382650@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:    修改人：  修改日期:     修改内容:
 * </pre>
 */
public abstract class AbstractDaoConfig {
    private static Logger logger = LoggerFactory.getLogger(AbstractDaoConfig.class);
    protected static Map<String, BoneCPDataSource> dataSourceMap = new ConcurrentHashMap<String, BoneCPDataSource>(); // Map<PoolName, DataSource>

	static {
        // Tomcat下, BoneCP 的配置中 driverClass 没有效果, 需要手动加载
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
	
	/**
	 * 配置 config/db.xml文件中所有的连接池
	 */
	public synchronized static void startup() {
	    startup("common/db.xml");
	}
	
	/**
	 * 配置 path 文件中所有的连接池
	 * @param path
	 */
	public synchronized static void startup(String path) {
	    List<String> poolNameList = getAllPoolNameFromDbXML(path);
	    
	    for (String poolName : poolNameList) {
	        startup(path, poolName);
	    }
	}

	/**
	 * 获取配置文件里面的所有连接池名
	 * @param path
	 * @return
	 */
    private static List<String> getAllPoolNameFromDbXML(String path) {
        List<String> sectionNameList = new ArrayList<>();
	    
	    InputStream xmlConfigFile = FileUtil.loadStream(path);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(xmlConfigFile);
            doc.getDocumentElement().normalize();

            // 读取所有 named-config 节点的 name 属性
            NodeList config = doc.getElementsByTagName("named-config");
            if (config != null && config.getLength() > 0) {
                for (int i = 0; i < config.getLength(); i++) {
                    Node node = config.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        NamedNodeMap attributes = node.getAttributes();
                        if (attributes != null && attributes.getLength() > 0) {
                            Node name = attributes.getNamedItem("name");
                            sectionNameList.add(name.getNodeValue());
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            throw new CommonRuntimeException(e, e.getMessage());
        }
        finally {
            LangUtil.close(xmlConfigFile);
        }
        
        return sectionNameList;
    }
    
	/**
	 * 配置连接
	 * @param path
	 * @param poolName
	 */
    public synchronized static void startup(String path, String poolName) {
        if (dataSourceMap.containsKey(path)) {
            throw new CommonRuntimeException("AbstractDaoConfig.startup poolName=%s be exist.", poolName);
        }
        
        // 创建 DataSource
        InputStream is = FileUtil.loadStream(path);
        BoneCPConfig config = null;
        try {
            config = new BoneCPConfig(is, poolName); // 里面已经关闭 is
        }
        catch (Exception e) {
            throw new CommonRuntimeException(e, "AbstractDaoConfig.startup.msg=" + e.getMessage());
        }
        BoneCPDataSource ds = new BoneCPDataSource(config);
        logger.info("AbstractDaoConfig.{}.jdbcUrl={} 创建 DataSource", poolName, config.getJdbcUrl());
        
        // 测试 DataSource
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            stmt = conn.prepareStatement("SELECT NOW()");
            rs = stmt.executeQuery();
            rs.next();                
            logger.info("AbstractDaoConfig.{}.jdbcUrl={} 连接成功. NOW={}", poolName, config.getJdbcUrl(), rs.getString(1));
            
            dataSourceMap.put(poolName, ds);
        }
        catch (SQLException e) {
            LangUtil.close(ds);
            logger.error("AbstractDaoConfig.{}.jdbcUrl={} 连接失败", poolName, config.getJdbcUrl());                
            throw new CommonRuntimeException(e, e.getMessage());
        }
        finally {
            JdbcUtil.close(rs, stmt, conn);
        }
    }
    
	/**
	 * 关闭所有连接池
	 */
    public static synchronized void shutdown () {
        if (dataSourceMap != null) {
            LangUtil.close(dataSourceMap.values());
            dataSourceMap = null;
        }
    }
    
    /**
     * 获取某个连接池的连接
     * @param poolName
     * @return
     */
    protected static Connection getDbConnection (String poolName) {
        BoneCPDataSource statds = dataSourceMap.get(poolName);
        if (statds == null) {
            throw new CommonRuntimeException("不存在连接池. poolName=%s", poolName);
        }
        
        try {
            return statds.getConnection();
        }
        catch (Exception e) {
            throw new CommonRuntimeException(e, e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        Log4jConfig.init();
        startup("common/db.xml");
    }
}