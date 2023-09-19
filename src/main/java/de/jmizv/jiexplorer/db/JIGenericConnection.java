package de.jmizv.jiexplorer.db;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import de.jmizv.jiexplorer.gui.preferences.JIPreferences;


public class JIGenericConnection {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JIGenericConnection.class);

    public static final String OTHER = "OTHER";
    public static final String JDBC_URL = "JDBC_URL";
    public static final String JDBC_PROVIDER = "JDBC_PROVIDER";
    public static final String JDBC_LOGIN = "JDBC_LOGIN";
    public static final String JDBC_PASSWORD = "JDBC_PASSWORD";

    public static final String SQL_COMMIT = "COMMIT";

    public static final String SQL_INSERT_ROOT_CATAGORY;
    public static final String SQL_INSERT_CATAGORY;
    public static final String SQL_INSERT_CATAGORYCHILD;
    public static final String SQL_SELECT_CATAGORYID;
    public static final String SQL_SELECT_CATAGORY;
    public static final String SQL_DELETE_CATAGORYIMAGE;
    public static final String SQL_DELETE_IMAGE_KEYWORD;
    public static final String SQL_DELETE_KEYBRIDGE;
    public static final String SQL_DELETE_KEYWORD;
    public static final String SQL_INSERT_KEYWORD;
    public static final String SQL_INSERT_KEYBRIDGE;
    public static final String SQL_SELECT_ALL_KEYWORDS;
    public static final String SQL_SELECT_IMAGE_ID;
    public static final String SQL_INSERT_IMAGE;
    public static final String SQL_INSERT_IMAGE_TABLE;
    public static final String SQL_SELECT_IMAGE_TABLE;
    public static final String SQL_DELETE_IMAGE_TABLE;
    public static final String SQL_SELECT_DISK_OBJECT;
    public static final String SQL_INSERT_THUMTABLEID;
    public static final String SQL_SELECT_THUMTABLEID;
    public static final String SQL_SELECT_OPEN_WITH;
    public static final String SQL_INSERT_OPEN_WITH;
    public static final String SQL_UPDATE_OPEN_WITH;
    public static final String SQL_DELETE_OPEN_WITH;


    static {
        SQL_INSERT_IMAGE = "INSERT INTO Images (uid,path,name,length,lastModified,width,height,oreintation,year,month,day) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        SQL_INSERT_IMAGE_TABLE = "INSERT INTO ImageTable (uid,image) VALUES (?,?)";
        SQL_SELECT_IMAGE_TABLE = "SELECT image FROM ImageTable WHERE uid = ? ";
        SQL_DELETE_IMAGE_TABLE = "DELETE FROM ImageTable WHERE uid = ? ";
        SQL_DELETE_CATAGORYIMAGE = "DELETE FROM CategoryImage WHERE  categoryUID = ? AND imageUID = ?";
        SQL_INSERT_ROOT_CATAGORY = "INSERT INTO Categories (uid,category) VALUES ('root','Category')";
        SQL_INSERT_CATAGORY = "INSERT INTO Categories (uid,category) VALUES (?,?)";
        SQL_INSERT_CATAGORYCHILD = "INSERT INTO CategoryChild (parentUID,childUID) VALUES (?,?);";
        SQL_SELECT_CATAGORYID = "SELECT uid FROM Categories WHERE category = ? ";
        SQL_SELECT_CATAGORY = "SELECT category FROM Categories WHERE uid = ? ";
        SQL_DELETE_IMAGE_KEYWORD = "DELETE FROM KeyBridge WHERE uid = ? ";
        SQL_DELETE_KEYBRIDGE = "DELETE FROM KeyBridge WHERE keey = ? ";
        SQL_DELETE_KEYWORD = "DELETE FROM KeyWords WHERE keey = ? ";
        SQL_INSERT_KEYBRIDGE = "INSERT INTO KeyBridge (keey,uid) VALUES (?,?)";
        SQL_INSERT_KEYWORD = "INSERT INTO KeyWords (keey) VALUES (?)";
        SQL_SELECT_ALL_KEYWORDS = "SELECT keey FROM KeyWords ORDER BY keey ";
        SQL_SELECT_IMAGE_ID = "SELECT uid FROM Images WHERE path = ? ";
        SQL_SELECT_DISK_OBJECT = "SELECT DISTINCT name,path,length,lastModified,width,height,oreintation,uid FROM Images WHERE path = ? ";

        SQL_INSERT_THUMTABLEID = "INSERT INTO ThumbsDB (uid,path) VALUES (?,?)";
        SQL_SELECT_THUMTABLEID = "SELECT uid FROM ThumbsDB WHERE path = ? ";

        SQL_SELECT_OPEN_WITH = "SELECT DISTINCT uid,name,command,workingDir FROM OpenWith";
        SQL_INSERT_OPEN_WITH = "INSERT INTO OpenWith (uid,name,command,workingDir) VALUES (?,?,?,?);";
        SQL_UPDATE_OPEN_WITH = "UPDATE OpenWith SET name = ?, command = ?,workingDir = ? WHERE uid = ?";
        SQL_DELETE_OPEN_WITH = "DELETE OpenWith WHERE uid = ?";
    }

    private String sql_drop_open_with;
    private String sql_drop_catagorychild;
    private String sql_drop_catagoryimage;
    private String sql_drop_catagories;
    private String sql_drop_keybridge;
    private String sql_drop_keywords;
    private String sql_drop_image;
    private String sql_drop_image_table;
    private String sql_drop_image_db;

    private String sql_create_open_with;
    private String sql_create_image_table;
    private String sql_create_image;
    private String sql_create_keywords;
    private String sql_create_catagories;
    private String sql_create_catagorychild;
    private String sql_create_catagoryimage;
    private String sql_create_keybridge;
    private String sql_create_image_db;

    private Connection conn;

    private PreparedStatement prepSelectCatID;
    private PreparedStatement prepSelectCat;
    private PreparedStatement prepInsertThumb;
    private PreparedStatement prepSelectImageID;
    private PreparedStatement prepSelectDiskObject;

    private PreparedStatement prepSelectImageTable;
    private PreparedStatement prepInsertImageTable;
    private PreparedStatement prepInsertThumbTableID;
    private PreparedStatement prepSelectThumbTableID;

    private final String jdbcType;
    private String jdbcLogin;
    private String jdbcPassword;
    private String jdbcURL;
    private String jdbcProvider;

    public JIGenericConnection(final String conf) {
        jdbcType = JIPreferences.getInstance().getDatabaseType();
        jdbcLogin = JIPreferences.getInstance().getJBDCUserName();
        jdbcPassword = JIPreferences.getInstance().getJDBCPassword();
        jdbcURL = JIPreferences.getInstance().getJDBCURL();
        jdbcProvider = JIPreferences.getInstance().getJDBCDriver();

        init(conf);
        try {
            createPrepStatements();
        } catch (SQLException e) {
            validate();
            //throw new RuntimeException("Could not prepare statements.", e);
        }
    }

    public boolean validate() {
        try {
            final Statement stat = conn.createStatement();

            // Check the database for required tables
            try {
                boolean result = true;
                result &= stat.execute("SELECT uid,path,name,width,height,oreintation,length,lastModified FROM Images");
                result &= stat.execute("SELECT uid,path FROM ThumbsDB");
                result &= stat.execute("SELECT keey FROM KeyWords");
                result &= stat.execute("SELECT uid,category FROM Categories");
                result &= stat.execute("SELECT parentUID,childUID FROM CategoryChild");
                result &= stat.execute("SELECT categoryUID,imageUID FROM CategoryImage");
                result &= stat.execute("SELECT uid,keey FROM KeyBridge");
                result &= stat.execute("SELECT uid,command FROM OpenWith");
            } catch (SQLException sqle) {
                return false;
            }

            // Close the Statement object, it is no longer used
            stat.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return false;
        }
        return true;
    }


    public static void defaultRecovery() {
        JIPreferences.getInstance().setDatabaseType("HSQL");
        JIPreferences.getInstance().setJDBCDriver(JIExplorerDB.HSQL_JDBC_DRIVER);
        JIPreferences.getInstance().setJDBCURL(JIPreferences.HSQL_CONNECTION_URL);
        JIPreferences.getInstance().setJDBCUserName("sa");
        JIPreferences.getInstance().setJDBCPassword("");
    }

    public final void createPrepStatements() throws SQLException {
        prepSelectImageID = conn.prepareStatement(SQL_SELECT_IMAGE_ID);
        prepInsertThumb = conn.prepareStatement(SQL_INSERT_IMAGE);
        prepSelectDiskObject = conn.prepareStatement(SQL_SELECT_DISK_OBJECT);

        prepSelectImageTable = conn.prepareStatement(SQL_SELECT_IMAGE_TABLE);
        prepInsertImageTable = conn.prepareStatement(SQL_INSERT_IMAGE_TABLE);
        prepInsertThumbTableID = conn.prepareStatement(SQL_INSERT_THUMTABLEID);
        prepSelectThumbTableID = conn.prepareStatement(SQL_SELECT_THUMTABLEID);

        prepSelectThumbTableID.setEscapeProcessing(true);
        prepSelectDiskObject.setEscapeProcessing(true);
        prepInsertImageTable.setEscapeProcessing(true);
        prepInsertThumb.setEscapeProcessing(true);
        prepSelectImageID.setEscapeProcessing(true);
    }

    public void init(final String conf) {
        final Properties properties = new Properties();
        try {
            properties.load(properties(conf));

            log.debug("Conf = " + conf);
            if (conf.equals(OTHER)) {
                jdbcURL = properties.getProperty(JDBC_URL);
                jdbcProvider = properties.getProperty(JDBC_PROVIDER);
                jdbcLogin = properties.getProperty(JDBC_LOGIN);
                jdbcPassword = properties.getProperty(JDBC_PASSWORD);
            }

            sql_drop_image = properties.getProperty("SQL_DROP_IMAGE");
            sql_drop_open_with = properties.getProperty("SQL_DROP_OPEN_WITH");
            sql_drop_keybridge = properties.getProperty("SQL_DROP_KEYBRIDGE");
            sql_drop_keywords = properties.getProperty("SQL_DROP_KEYWORDS");
            sql_drop_catagories = properties.getProperty("SQL_DROP_CATAGORIES");
            sql_drop_catagorychild = properties.getProperty("SQL_DROP_CATAGORYCHILD");
            sql_drop_catagoryimage = properties.getProperty("SQL_DROP_CATAGORYIMAGE");
            sql_drop_image_table = properties.getProperty("SQL_DROP_IMAGE_TABLE");
            sql_drop_image_db = properties.getProperty("SQL_DROP_IMAGE_DB");

            sql_create_image = properties.getProperty("SQL_CREATE_IMAGE");
            sql_create_image_table = properties.getProperty("SQL_CREATE_IMAGE_TABLE");
            sql_create_keywords = properties.getProperty("SQL_CREATE_KEYWORDS");
            sql_create_catagories = properties.getProperty("SQL_CREATE_CATAGORIES");
            sql_create_catagorychild = properties.getProperty("SQL_CREATE_CATAGORYCHILD");
            sql_create_catagoryimage = properties.getProperty("SQL_CREATE_CATAGORYIMAGE");
            sql_create_keybridge = properties.getProperty("SQL_CREATE_KEYBRIDGE");
            sql_create_open_with = properties.getProperty("SQL_CREATE_OPEN_WITH");
            sql_create_image_db = properties.getProperty("SQL_CREATE_IMAGE_DB");

            log.debug("sql_drop_image         = " + sql_drop_image);
            log.debug("sql_drop_open_with     = " + sql_drop_open_with);
            log.debug("sql_drop_keybridge     = " + sql_drop_keybridge);
            log.debug("sql_drop_keywords      = " + sql_drop_keywords);
            log.debug("sql_drop_catagories    = " + sql_drop_catagories);
            log.debug("sql_drop_catagorychild = " + sql_drop_catagorychild);
            log.debug("sql_drop_catagoryimage = " + sql_drop_catagoryimage);
            log.debug("sql_drop_image_table   = " + sql_drop_image_table);
            log.debug("sql_drop_image_db      = " + sql_drop_image_db);

            log.debug("sql_create_image         = " + sql_create_image);
            log.debug("sql_create_open_with     = " + sql_create_open_with);
            log.debug("sql_create_keybridge     = " + sql_create_keybridge);
            log.debug("sql_create_keywords      = " + sql_create_keywords);
            log.debug("sql_create_catagories    = " + sql_create_catagories);
            log.debug("sql_create_catagorychild = " + sql_create_catagorychild);
            log.debug("sql_create_catagoryimage = " + sql_create_catagoryimage);
            log.debug("sql_create_image_table   = " + sql_create_image_table);
            log.debug("sql_create_image_db      = " + sql_create_image_db);

            try {
                log.debug("JDBCDriver = " + jdbcProvider);
                log.debug("JDBC URL = " + jdbcURL);
                log.debug("login " + jdbcLogin);

                if (jdbcType.equals("HSQL") && jdbcURL.toLowerCase().startsWith("jdbc:hsqldb:file:")) {
                    final File dbDir = new File(JIPreferences.getInstance().getDBLocation().substring("jdbc:hsqldb:file:".length()));

                    // Check that the database directory exists
                    // Otherwise create it.
                    if (!dbDir.exists()) {
                        if (!dbDir.mkdir()) {
                            throw new RuntimeException("Could not create parent directories for " + dbDir.getAbsolutePath());
                        }
                    }
                }
                conn = getConnection(jdbcLogin, jdbcPassword, jdbcURL, jdbcProvider);
            } catch (final Exception e1) {
                e1.printStackTrace(System.err);
                defaultRecovery();
            }
        } catch (final Exception e) {
            e.printStackTrace(System.err);
            defaultRecovery();
        }
    }

    public boolean makeTables() {
        try {
            final Statement stat = getConnection().createStatement();
            boolean result = true;
            stat.execute(sql_drop_catagoryimage);
            stat.execute(sql_drop_catagorychild);
            stat.execute(sql_drop_catagories);
            stat.execute(sql_drop_keybridge);
            stat.execute(sql_drop_keywords);
            stat.execute(sql_drop_open_with);
            stat.execute(sql_drop_image_db);
            stat.execute(sql_drop_image_table);
            stat.execute(sql_drop_image);
            result &= stat.execute(sql_create_image);
            result &= stat.execute(sql_create_image_table);
            result &= stat.execute(sql_create_keywords);
            result &= stat.execute(sql_create_keybridge);
            result &= stat.execute(sql_create_catagories);
            result &= stat.execute(sql_create_catagorychild);
            result &= stat.execute(sql_create_catagoryimage);
            result &= stat.execute(sql_create_open_with);
            result &= stat.execute(sql_create_image_db);
            result &= stat.execute("INSERT INTO Categories (uid, category) VALUES('-1','Categories')");
            result &= stat.execute(SQL_COMMIT);
            stat.close();
            return true;
        } catch (final SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized void close() {
        try {
            if ((conn != null) && !conn.isClosed()) {
                conn.close();
                conn = null;
            }
        } catch (final SQLException e) {
            e.printStackTrace(System.err);
        }
    }

    private final Connection getConnection(final String jdbcUser, final String jdbcPassword, final String jdbcURL, final String jdbcProvider) {
        Connection connection = null;
        try {
            // Load the HSQL Database Engine JDBC driver
            Class.forName(jdbcProvider).newInstance();

            // Connect to the database
            // It will be create automatically if it does not yet exist
            // "sa" is the user name and "" is the (empty) password
            connection = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);
        } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
            e.printStackTrace(System.err);
        }

        return connection;
    }

    public final Connection getConnection() {
        return conn;
    }

    /**
     * @return the prepInsertImageTable
     */
    public synchronized final PreparedStatement getPrepInsertImageTable() {
        return prepInsertImageTable;
    }

    /**
     * @return the prepInsertThumb
     */
    public synchronized final PreparedStatement getPrepInsertThumb() {
        return prepInsertThumb;
    }

    /**
     * @return the prepInsertThumbTable
     */
    public synchronized final PreparedStatement getPrepInsertThumbTableID() {
        return prepInsertThumbTableID;
    }

    /**
     * @return the prepSelectCat
     */
    public synchronized final PreparedStatement getPrepSelectCat() {
        return prepSelectCat;
    }

    /**
     * @return the prepSelectCatID
     */
    public synchronized final PreparedStatement getPrepSelectCatID() {
        return prepSelectCatID;
    }

    /**
     * @return the prepSelectDiskObject
     */
    public synchronized final PreparedStatement getPrepSelectDiskObject() {
        return prepSelectDiskObject;
    }

    /**
     * @return the prepSelectImageID
     */
    public synchronized final PreparedStatement getPrepSelectImageID() {
        return prepSelectImageID;
    }

    /**
     * @return the prepSelectImageTable
     */
    public synchronized final PreparedStatement getPrepSelectImageTable() {
        return prepSelectImageTable;
    }

    /**
     * @return the prepSelectThumbTableID
     */
    public synchronized final PreparedStatement getPrepSelectThumbTableID() {
        return prepSelectThumbTableID;
    }

    /**
     * @param conn the conn to set
     */
    public synchronized final void setConnection(final Connection conn) {
        this.conn = conn;
    }

    /**
     * @return the sql_create_image_table
     */
    public synchronized final String getSqlCreateImageTable(final String tableUID) {
        return sql_create_image_table.replaceFirst("ImageTable", tableUID);
    }

    /**
     * @return the sql_drop_image_db
     */
    public synchronized final String getSqlDropImageTable(final String tableUID) {
        return sql_drop_image_table.replaceFirst("ImageTable", tableUID);
    }

    public ByteArrayInputStream properties(final String config) {
        final String EOL = System.getProperty("line.separator");
        final StringBuffer strbuff = new StringBuffer();

        if (config.equals("MYSQL")) {
            strbuff.append("SQL_DROP_CATAGORYCHILD=DROP TABLE IF EXISTS `CategoryChild`" + EOL);
            strbuff.append("SQL_DROP_CATAGORYIMAGE=DROP TABLE IF EXISTS `CategoryImage`" + EOL);
            strbuff.append("SQL_DROP_CATAGORIES=DROP TABLE IF EXISTS `Categories` " + EOL);
            strbuff.append("SQL_DROP_KEYBRIDGE=DROP TABLE IF EXISTS `KeyBridge` " + EOL);
            strbuff.append("SQL_DROP_KEYWORDS=DROP TABLE IF EXISTS `KeyWords`" + EOL);
            strbuff.append("SQL_DROP_IMAGE=DROP TABLE IF EXISTS `Images` " + EOL);
            strbuff.append("SQL_DROP_IMAGE_TABLE=DROP TABLE IF EXISTS `ImageTable`" + EOL);
            strbuff.append("SQL_DROP_OPEN_WITH=DROP TABLE IF EXISTS `OpenWith`" + EOL);
            strbuff.append("SQL_DROP_IMAGE_DB=DROP TABLE IF EXISTS `ThumbsDB`" + EOL);
            strbuff.append("SQL_CREATE_IMAGE=CREATE TABLE `Images` (`uid` varchar(64) NOT NULL,`path` varchar(255) default NULL,`name` varchar(255) default NULL,`width` int(11) default -1,`height` int(11) default -1,`oreintation` int(4) default 1,`length` bigint(20) default NULL,`lastModified` bigint(20) default NULL,PRIMARY KEY  (`uid`),`year` varchar(4),`month` varchar(3),`day` varchar(2),UNIQUE KEY `path` (`path`)) ENGINE=MyISAM DEFAULT CHARSET=latin1" + EOL);
            strbuff.append("SQL_CREATE_IMAGE_TABLE=CREATE TABLE `ImageTable` (`uid` varchar(64) NOT NULL,`image` BLOB NOT NULL,PRIMARY KEY  (`uid`),CONSTRAINT `FK_imagestable_1` FOREIGN KEY (`uid`) REFERENCES `images` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE=MyISAM DEFAULT CHARSET=latin1 " + EOL);
            strbuff.append("SQL_CREATE_KEYWORDS=CREATE TABLE `KeyWords` (`keey` varchar(64) NOT NULL,PRIMARY KEY  (`keey`)) ENGINE=MyISAM DEFAULT CHARSET=latin1" + EOL);
            strbuff.append("SQL_CREATE_CATAGORIES=CREATE TABLE `Categories` (`uid` varchar(64) NOT NULL,`category` varchar(254) NOT NULL default '',PRIMARY KEY  (`uid`)) ENGINE=MyISAM DEFAULT CHARSET=latin1" + EOL);
            strbuff.append("SQL_CREATE_CATAGORYCHILD=CREATE TABLE `CategoryChild` (`parentUID` varchar(64) NOT NULL,`childUID` varchar(64) NOT NULL default '',PRIMARY KEY  (`parentUID`,`childUID`),KEY `FK_CatagoryChild_child` (`childUID`)) ENGINE=MyISAM DEFAULT CHARSET=latin1" + EOL);
            strbuff.append("SQL_CREATE_CATAGORYIMAGE=CREATE TABLE `CategoryImage` (`categoryUID` varchar(64) NOT NULL,`imageUID` varchar(64) NOT NULL default '',PRIMARY KEY  (`categoryUID`,`imageUID`),KEY `FK_CatagoryImage_image` (`imageUID`)) ENGINE=MyISAM DEFAULT CHARSET=latin1" + EOL);
            strbuff.append("SQL_CREATE_KEYBRIDGE=CREATE TABLE `KeyBridge` (`uid` varchar(64) NOT NULL,`keey` varchar(64) NOT NULL default '',PRIMARY KEY  (`uid`,`keey`),KEY `FK_KeyBridge_keey` (`keey`),CONSTRAINT `FK_KeyBridge_uid` FOREIGN KEY (`uid`) REFERENCES `Images` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE,CONSTRAINT `FK_KeyBridge_keey` FOREIGN KEY (`keey`) REFERENCES `KeyWords` (`keey`) ON DELETE CASCADE ON UPDATE CASCADE) ENGINE=MyISAM DEFAULT CHARSET=latin1" + EOL);
            strbuff.append("SQL_CREATE_OPEN_WITH=CREATE TABLE `OpenWith` (`uid`  varchar(64) NOT NULL,`name`  varchar(64) NOT NULL default '',`command` varchar(255) default NULL,`workingDir` varchar(255) default NULL,PRIMARY KEY  (`uid`),UNIQUE KEY `command` (`command`)) ENGINE=MyISAM DEFAULT CHARSET=latin1" + EOL);
            strbuff.append("SQL_CREATE_IMAGE_DB=CREATE TABLE `ThumbsDB` (`uid`  varchar(64) NOT NULL,`path` varchar(255) default NULL,PRIMARY KEY  (`uid`),UNIQUE KEY `path` (`path`)) ENGINE=MyISAM DEFAULT CHARSET=latin1" + EOL);
            return new ByteArrayInputStream(strbuff.toString().getBytes());
        }
        strbuff.append("SQL_DROP_IMAGE=DROP TABLE Images IF EXISTS" + EOL);
        strbuff.append("SQL_DROP_OPEN_WITH=DROP TABLE OpenWith IF EXISTS" + EOL);
        strbuff.append("SQL_DROP_KEYBRIDGE=DROP TABLE KeyBridge IF EXISTS" + EOL);
        strbuff.append("SQL_DROP_KEYWORDS=DROP TABLE Keywords IF EXISTS" + EOL);
        strbuff.append("SQL_DROP_CATAGORIES=DROP TABLE Categories IF EXISTS" + EOL);
        strbuff.append("SQL_DROP_CATAGORYCHILD=DROP TABLE CategoryChild IF EXISTS" + EOL);
        strbuff.append("SQL_DROP_CATAGORYIMAGE=DROP TABLE CategoryImage IF EXISTS" + EOL);
        strbuff.append("SQL_DROP_IMAGE_TABLE=DROP TABLE ImageTable IF EXISTS" + EOL);
        strbuff.append("SQL_DROP_IMAGE_DB=DROP TABLE ThumbsDB IF EXISTS" + EOL);
        strbuff.append("SQL_CREATE_IMAGE=CREATE TABLE Images (uid VARCHAR(64) PRIMARY KEY,path VARCHAR(255),name VARCHAR(255),width INTEGER,height INTEGER,oreintation INTEGER,length BIGINT,lastModified BIGINT,year VARCHAR(4),month VARCHAR(3),day VARCHAR(2), UNIQUE (path))" + EOL);
        strbuff.append("SQL_CREATE_OPEN_WITH=CREATE TABLE OpenWith (uid VARCHAR(64) PRIMARY KEY,name VARCHAR(64),command VARCHAR(255),workingDir VARCHAR(255),UNIQUE (name))" + EOL);
        strbuff.append("SQL_CREATE_KEYBRIDGE=CREATE TABLE KeyBridge (uid VARCHAR(64),keey VARCHAR(64),PRIMARY KEY (uid, keey))" + EOL);
        strbuff.append("SQL_CREATE_KEYWORDS=CREATE TABLE Keywords (keey VARCHAR(64) PRIMARY KEY)" + EOL);
        strbuff.append("SQL_CREATE_CATAGORIES=CREATE TABLE Categories (uid VARCHAR(64)  PRIMARY KEY,category   VARCHAR(255))  " + EOL);
        strbuff.append("SQL_CREATE_CATAGORYCHILD=CREATE TABLE CategoryChild (parentUID VARCHAR(64), childUID VARCHAR(64),PRIMARY KEY (parentUID, childUID))   " + EOL);
        strbuff.append("SQL_CREATE_CATAGORYIMAGE=CREATE TABLE CategoryImage (categoryUID  VARCHAR(64), imageUID VARCHAR(64),PRIMARY KEY (categoryUID, imageUID))" + EOL);
        strbuff.append("SQL_CREATE_IMAGE_TABLE=CREATE TABLE ImageTable (uid varchar(64) PRIMARY KEY,image IMAGE)" + EOL);
        strbuff.append("SQL_CREATE_IMAGE_DB=CREATE TABLE ThumbsDB (uid  varchar(64),path varchar(255),PRIMARY KEY (uid),UNIQUE (path))" + EOL);
        return new ByteArrayInputStream(strbuff.toString().getBytes());
    }
}
