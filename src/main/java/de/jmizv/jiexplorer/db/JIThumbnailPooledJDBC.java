package de.jmizv.jiexplorer.db;

import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.metadata.IIOMetadata;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolUtils;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.SoftReferenceObjectPool;

import de.jmizv.jiexplorer.gui.cattree.JICatTreeNode;
import de.jmizv.jiexplorer.gui.datetree.JIDateTreeNode;
import de.jmizv.jiexplorer.gui.preferences.JIPreferences;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIUtility;
import de.jmizv.jiexplorer.util.MonthNameComparator;
import de.jmizv.jiexplorer.util.OpenWith;
import de.jmizv.jiexplorer.util.OrderedDiskObjectList;
import de.jmizv.jiexplorer.util.UID;

public class JIThumbnailPooledJDBC implements JIExplorerDB {
    public static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JIThumbnailPooledJDBC.class);

    protected static boolean compact = false;

    protected ObjectPool pool;
    protected static JIThumbnailPooledJDBC instance;

    protected JIThumbnailPooledJDBC() {
        try {
            final PoolableObjectFactory factory = PoolUtils.synchronizedPoolableFactory(new JIConnectionFactory());
            this.pool = PoolUtils.synchronizedPool(new SoftReferenceObjectPool(factory, 3));

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static JIExplorerDB getInstance() {
        if (instance == null) {
            instance = new JIThumbnailPooledJDBC();
        }
        return instance;
    }

    public static synchronized JIExplorerDB reload(final JComponent jc) {
        if (instance != null) {
            instance.close();
            instance = null;
        }
        instance = new JIThumbnailPooledJDBC();
        return instance;
    }


    public final String getPathUID(final String path) {
        //log.debug("JIThumbnailPooledJDBC::getPathUID - "+path);
        String dirPath = path;
        String uid_str = null;

        if (!new File(path).isDirectory()) {
            dirPath = StringEscapeUtils.escapeSql(path.substring(0, path.lastIndexOf("/")));
        }
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::getImageID pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();
            final Statement stat = conn.getConnection().createStatement();
            synchronized (this) {
                //final ResultSet rs = stat.executeQuery("SELECT uid FROM ThumbsDB WHERE path = '"+dirPath+"'");
                conn.getPrepSelectThumbTableID().clearParameters();
                conn.getPrepSelectThumbTableID().setString(1, dirPath);
                final ResultSet rs = conn.getPrepSelectThumbTableID().executeQuery();
                if (rs.next()) {
                    uid_str = rs.getString(1);
                }
                rs.close();
            }

            if (uid_str == null) {
                final UID uid = new UID();
                uid_str = uid.toString();
                final String dropTable = conn.getSqlDropImageTable(uid_str);
                final String createTable = conn.getSqlCreateImageTable(uid_str);
//                log.debug("JIThumbnailJDBC::getImageID getSqlDropImageTable = "+dropTable);
//                log.debug("JIThumbnailJDBC::getImageID getSqlCreateImageTable = "+createTable);
                stat.execute(dropTable);
                stat.execute(createTable);
                stat.close();
                conn.getPrepInsertThumbTableID().clearParameters();
                conn.getPrepInsertThumbTableID().setString(1, uid_str);
                conn.getPrepInsertThumbTableID().setString(2, dirPath);
                conn.getPrepInsertThumbTableID().execute();
            }
            return uid_str;

        } catch (final Exception e) {
            // Print out the error message
            //log.debug(e);
            e.printStackTrace(System.err);
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace(System.err);
            }
        }
        return null;
    }

    public final String getImageID(final String path) {
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::getImageID pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();
            conn.getPrepSelectImageID().clearParameters();
            conn.getPrepSelectImageID().setString(1, JIUtility.portablePath(StringEscapeUtils.escapeSql(path)));
            final ResultSet rs = conn.getPrepSelectImageID().executeQuery();
            if (rs.next()) {
                final String imageID = rs.getString(1);
                rs.close();
                return imageID;
            }

        } catch (final Exception e) {
            //log.debug(e);
            e.printStackTrace();
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return new UID().toString();
    }

    public final String getImageID(final DiskObject diskObject) {
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::getImageID pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();
            conn.getPrepSelectImageID().clearParameters();
            conn.getPrepSelectImageID().setString(1, JIUtility.portablePath(StringEscapeUtils.escapeSql(diskObject.getPath())));
            final ResultSet rs = conn.getPrepSelectImageID().executeQuery();
            if (rs.next()) {
                final String imageID = rs.getString(1);
                rs.close();
                return imageID;
            } else {
                final UID uid = new UID();
                diskObject.setUid(uid.toString());
                final Format formatter = new SimpleDateFormat("yyyy/MMM/dd");
                final String[] strDate = formatter.format(new java.util.Date(diskObject.getLastModified())).split("/");

                log.debug("putThumbnail " + strDate[0] + " / " + strDate[1] + " / " + strDate[1]);
                log.debug("putThumbnail " + diskObject.toLogString());

                conn.getPrepInsertThumb().clearParameters();
                conn.getPrepInsertThumb().setString(1, uid.toString());
                conn.getPrepInsertThumb().setString(2, StringEscapeUtils.escapeSql(diskObject.getPath()));
                conn.getPrepInsertThumb().setString(3, diskObject.getName());
                conn.getPrepInsertThumb().setLong(4, diskObject.getLength());
                conn.getPrepInsertThumb().setLong(5, diskObject.getLastModified());
                conn.getPrepInsertThumb().setInt(6, diskObject.getWidth());
                conn.getPrepInsertThumb().setInt(7, diskObject.getHeight());
                conn.getPrepInsertThumb().setInt(8, diskObject.getOrientationValue());
                conn.getPrepInsertThumb().setString(9, (strDate[0].length() > 4) ? strDate[0].substring(0, 3) : strDate[0]);
                conn.getPrepInsertThumb().setString(10, (strDate[1].length() > 3) ? strDate[1].substring(0, 2) : strDate[1]);
                conn.getPrepInsertThumb().setString(11, (strDate[2].length() > 2) ? strDate[2].substring(0, 1) : strDate[2]);
                conn.getPrepInsertThumb().execute();

                return uid.toString();
            }

        } catch (final Exception e) {
            //log.debug(e);
            e.printStackTrace();
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public final boolean putThumbnail(final DiskObject diskObject, final BufferedImage image, final IIOMetadata metadata) {
        if (JIPreferences.getInstance().isReadOnly()) {
            return false;
        }

        if ((diskObject == null) || (image == null)) {
            return false;
        }
        boolean result = false;

        JIGenericConnection conn = null;
        try {
            log.debug("JIThumbnailJDBC::putThumbnail pool Active = " + pool.getNumActive() + " Idle = " + pool.getNumIdle() + " - " + diskObject.getPath() + " - " + diskObject.getUid());
            conn = (JIGenericConnection) pool.borrowObject();
            final Statement stat = conn.getConnection().createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            final String pathUid = getPathUID(diskObject.getPath());


            result &= stat.execute("COMMIT");
            compact = true;
            final String query = "INSERT INTO " + pathUid + " (uid,image) VALUES (?,?)";
            //log.debug(query);
            final PreparedStatement ps = conn.getConnection().prepareStatement(query);
            ps.clearParameters();
            ps.setString(1, diskObject.getUid());
            ps.setBytes(2, JIUtility.bufferedImageToByteArray(image));
            ps.execute();

            stat.execute("UPDATE Images SET width=" + diskObject.getWidth() + ", " +
                         "height=" + diskObject.getHeight() + ", " +
                         "oreintation=" + diskObject.getOrientation() + " " +
                         "WHERE uid = '" + diskObject.getUid() + "'");

            result &= stat.execute("COMMIT");

            stat.close();
            return result;
        } catch (Exception e) {
            // Print out the error message
            //e.printStackTrace(System.err);
            return result;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public final void insertOpenWith(final OpenWith ow) {
        JIGenericConnection conn = null;
        try {
            boolean result = true;
            //log.debug("JIThumbnailJDBC::insertOpenWith pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();

            final Statement stat = conn.getConnection().createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            result &= stat.execute("INSERT INTO OpenWith (uid,name,command,workingDir) " +
                                   "VALUES ('" + ow.getUid() + "'," +
                                   "'" + ow.getCommandName() + "'," +
                                   "'" + JIUtility.portablePath(ow.getCommand()) + "'," +
                                   "'" + JIUtility.portablePath(ow.getWorkingDir()) + "')");
            stat.close();
            return;

        } catch (final Exception e) {
            //log.debug(e);
            e.printStackTrace();
            return;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final void updateOpenWith(final OpenWith ow) {
        JIGenericConnection conn = null;
        try {
            boolean result = true;
            //log.debug("JIThumbnailJDBC::insertOpenWith pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();

            final Statement stat = conn.getConnection().createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            result &= stat.execute("UPDATE OpenWith SET name = '" + ow.getCommandName() + "'," +
                                   "command = '" + JIUtility.portablePath(ow.getCommand()) + "'," +
                                   "workingDir = '" + JIUtility.portablePath(ow.getWorkingDir()) + "' " +
                                   "WHERE uid = '" + ow.getUid() + "'");
            stat.close();
            return;

        } catch (final Exception e) {
            //log.debug(e);
            e.printStackTrace();
            return;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final void deleteOpenWith(final OpenWith ow) {
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::insertOpenWith pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();

            final Statement stat = conn.getConnection().createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            boolean result = true;

            result &= stat.execute("DELETE OpenWith WHERE uid = '" + ow.getUid() + "'");
            return;

        } catch (final Exception e) {
            //log.debug(e);
            e.printStackTrace();
            return;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final Vector<OpenWith> getOpenWith() {
        JIGenericConnection conn = null;
        final Vector<OpenWith> vec = new Vector<OpenWith>();
        try {
            //log.debug("JIThumbnailJDBC::insertOpenWith pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();

            final Statement stat = conn.getConnection().createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            final ResultSet rs = stat.executeQuery("SELECT DISTINCT uid,name,command,workingDir FROM OpenWith");
            while (rs.next()) {
                final OpenWith ow = new OpenWith(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
                vec.add(ow);
            }
            rs.close();
            stat.close();
            return vec;

        } catch (final Exception e) {
            //log.debug(e);
            e.printStackTrace();
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public final void getDiskObject(final DiskObject diskObject) {
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::getDiskObject pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();
            conn.getPrepSelectDiskObject().clearParameters();
            conn.getPrepSelectDiskObject().setString(1, diskObject.getPath());
            final ResultSet rs = conn.getPrepSelectDiskObject().executeQuery();
            if (!rs.next()) {
                rs.close();
                return;
            }

            if ((diskObject != null) && ((diskObject.getWidth() == -1) || (diskObject.getHeight() == -1))) {
                diskObject.setLastModified(rs.getLong(4));
                diskObject.setWidth(rs.getInt(5));
                diskObject.setHeight(rs.getInt(6));
                diskObject.setOrientation(rs.getInt(7));
                diskObject.setUid(rs.getString(8));
            }
            rs.close();
            return;

        } catch (final Exception e) {
            //log.debug(e);
            e.printStackTrace();
            return;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final BufferedImage getThumbnail(final String path, final DiskObject diskObject) {
        JIGenericConnection tconn = null;
        try {
            final String pathUID = getPathUID(diskObject.getPath());
            //log.debug("JIThumbnailJDBC::getThumbnail pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            tconn = (JIGenericConnection) pool.borrowObject();

            tconn.getPrepSelectImageTable().clearParameters();
            final Statement stat = tconn.getConnection().createStatement();
            final ResultSet rs = stat.executeQuery("SELECT image FROM " + pathUID + "  WHERE uid = '" + diskObject.getUid() + "'");
            if (!rs.next()) {
                rs.close();
                log.debug("Not Found : SELECT image FROM " + pathUID + "  WHERE uid = '" + diskObject.getUid() + "'");
                return null;
            }

            final BufferedImage bufImage = JIUtility.byteArrayToBufferedImage(rs.getBytes(1));
            rs.close();
            return bufImage;

        } catch (final Exception e) {
            //log.debug(e);
            e.printStackTrace();
        } finally {
            try {
                pool.returnObject(tconn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public final Map<String, String> getValidThumbnails(final List<File> dirs) {
        final Map<String, String> results = new HashMap<String, String>();
        JIGenericConnection tconn = null;

        try {
            for (final File dir : dirs) {
                final String pathUID = getPathUID(dir.getAbsolutePath());
                //log.debug("JIThumbnailJDBC::getThumbnail pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
                tconn = (JIGenericConnection) pool.borrowObject();

                tconn.getPrepSelectImageTable().clearParameters();
                final Statement stat = tconn.getConnection().createStatement();
                final ResultSet rs = stat.executeQuery("SELECT path, uid FROM " + pathUID + "'");
                while (rs.next()) {
                    results.put(rs.getString(1), rs.getString(2));
                }
                rs.close();
            }

        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            try {
                pool.returnObject(tconn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public final BufferedImage getThumbnail(final DiskObject diskObject) {
        return getThumbnail(diskObject.getPath(), diskObject);
    }

    public final boolean copyFile(final File from, final File to) {
        JIGenericConnection conn = null;
        try {
            log.debug("JIThumbnailJDBC::copyFile  pool Active = " + pool.getNumActive() + " Idle = " + pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();
            final DiskObject toDObj = new DiskObject(to);
            final DiskObject fromDObj = new DiskObject(from);
            final BufferedImage image = getThumbnail(fromDObj);
            toDObj.setWidth(fromDObj.getWidth());
            toDObj.setHeight(fromDObj.getHeight());
            putThumbnail(toDObj, image, null);

            String query = "SELECT keey FROM KeyBridge " +
                           "WHERE uid = '" + getImageID(from.getPath()) + "' ";

            final Statement stat = conn.getConnection().createStatement();
            ResultSet rs = stat.executeQuery(query);
            Vector<String> vec = new Vector<String>();
            while (rs.next()) {
                vec.addElement(rs.getString(1));
            }

            final DiskObject[] files = new DiskObject[1];
            files[0] = new DiskObject(to);
            insertKeyWordsForImage(files, vec.elements(), false);

            query = "SELECT categoryUID FROM CategoryImage " +
                    "WHERE imageUID = '" + getImageID(from.getPath()) + "' ";
            rs.close();

            rs = stat.executeQuery(query);
            vec = new Vector<String>();
            while (rs.next()) {
                insertImagesInCategory(files, rs.getString(1), false);
            }
            stat.close();
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Key Word Methods
    public final DefaultListModel getKeyWords(final DiskObject[] f, final DefaultListModel dlm) {
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::getKeyWords  pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();

            String query = "SELECT keey FROM KeyWords " +
                           "WHERE keey NOT IN (  " +
                           "    SELECT keey " +
                           "    FROM Images JOIN KeyBridge " +
                           "    ON Images.uid = KeyBridge.uid " +
                           "    WHERE Images.uid = '" + f[0].getUid() + "' ";

            for (int i = 1; i < f.length; i++) {
                query += "OR Images.uid = '" + f[i].getUid() + "' ";
            }

            query += "GROUP BY keey ";
            query += "HAVING COUNT(Images.uid) = " + f.length + ")";

//			log.debug("query = "+query);
            final Statement stat = conn.getConnection().createStatement();
            final ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                dlm.addElement(rs.getString(1));
            }
            stat.close();
            return dlm;
        } catch (final Exception sqle) {
            sqle.printStackTrace();
            return null;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final OrderedDiskObjectList getKeyWordImagesOR(final String[] keys, final OrderedDiskObjectList dlm) {
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::getKeyWordImagesOR  pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();
            final OrderedDiskObjectList results = new OrderedDiskObjectList();

            String query = "SELECT DISTINCT name,path,length,lastModified,width,height,oreintation "
                           + "FROM Images JOIN KeyBridge "
                           + "ON Images.uid = KeyBridge.uid "
                           + "WHERE KeyBridge.keey = '" + keys[0].trim() + "' ";

            for (int i = 1; i < keys.length; i++) {
                query += "OR KeyBridge.keey = '" + keys[i].trim() + "' ";
            }

            query += "ORDER BY " + orderBy();

//			log.debug(" - getKeyWordImagesOR() query = "+query);
            final Statement stat = conn.getConnection().createStatement();
            final ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                final DiskObject dObj = new DiskObject(rs.getString(1),
                        rs.getString(2),
                        rs.getLong(3),
                        rs.getLong(4),
                        DiskObject.TYPE_FILE,
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getInt(7));
                if (dlm != null) {
                    dlm.addElement(dObj);
                }
                results.add(dObj);
            }
            stat.close();
            return results;
        } catch (final Exception sqle) {
//			log.debug(sqle);
            sqle.printStackTrace();
            return null;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }


    public final OrderedDiskObjectList getKeyWordImagesAND(final String[] keys, final OrderedDiskObjectList dlm) {
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::getKeyWordImagesAND  pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();
            final OrderedDiskObjectList results = new OrderedDiskObjectList();
            Object[] paths;
            String query;
            ArrayList<String> resultPaths;
            Statement stat;
            ResultSet rs;

            query = "SELECT DISTINCT Images.uid " +
                    "FROM Images JOIN KeyBridge " +
                    "ON Images.uid = KeyBridge.uid " +
                    "WHERE KeyBridge.keey = '" + keys[0].trim() + "' ";

//			log.debug(" - getKeyWordImagesAND() query = "+query);
            stat = conn.getConnection().createStatement();
            rs = stat.executeQuery(query);

            resultPaths = new ArrayList<String>();
            while (rs.next()) {
                resultPaths.add(rs.getString(1));
            }
            paths = resultPaths.toArray();

            for (int i = 1; i < keys.length; i++) {
                query = "SELECT DISTINCT Images.uid "
                        + "FROM Images JOIN KeyBridge "
                        + "ON Images.uid = KeyBridge.uid "
                        + "WHERE KeyBridge.keey = '" + keys[i].trim() + "' "
                        + "AND Images.uid IN ('" + paths[0] + "'";

                for (int j = 1; j < paths.length; j++) {
                    query += ",'" + paths[j] + "'";
                }

                query += ")";

//				log.debug(" - getKeyWordImagesAND() query = "+query);
                rs = stat.executeQuery(query);
                resultPaths = new ArrayList<String>();
                while (rs.next()) {
                    resultPaths.add(rs.getString(1));
                }
                paths = resultPaths.toArray();
            }

            if (paths.length > 0) {

                query = "SELECT DISTINCT path,name,length,lastModified,width,height,oreintation " +
                        "FROM Images " +
                        "WHERE uid IN ('" + paths[0] + "' ";

                for (int x = 1; x < paths.length; x++) {
                    query += ", '" + paths[x] + "' ";
                }

                query += ") ORDER BY " + orderBy();
//				log.debug(" - getKeyWordImagesAND() query = "+query);

                stat = conn.getConnection().createStatement();
                rs = stat.executeQuery(query);

                while (rs.next()) {
                    final DiskObject dObj = new DiskObject(rs.getString(2),
                            rs.getString(1),
                            rs.getLong(3),
                            rs.getLong(4),
                            DiskObject.TYPE_FILE,
                            rs.getInt(5),
                            rs.getInt(6),
                            rs.getInt(7));
                    if (dlm != null) {
                        dlm.addElement(dObj);
                    }
                    results.add(dObj);
                }
                stat.close();
            }
            return results;
        } catch (final Exception sqle) {
//			log.debug(sqle);
            sqle.printStackTrace();
            return null;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final DefaultListModel getKeyWords(final DefaultListModel dlm) {
        JIGenericConnection conn = null;
        try {
//    		log.debug("JIThumbnailJDBC::getKeyWords  pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();
            final ResultSet rs = conn.getConnection().createStatement().executeQuery("SELECT keey FROM KeyWords ORDER BY keey");
            while (rs.next()) {
                dlm.addElement(rs.getString(1));
            }
            return dlm;
        } catch (final Exception sqle) {
//			log.debug(sqle);
            sqle.printStackTrace();
            return null;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final DefaultListModel getKeyWordsForImage(final DiskObject[] f, final DefaultListModel dlm) {
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::getKeyWordsForImage  pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();

            String query = "SELECT keey "
                           + "FROM KeyBridge JOIN Images "
                           + "ON KeyBridge.uid = Images.uid "
                           + "WHERE Images.uid = '" + f[0].getUid() + "' ";

            for (int i = 1; i < f.length; i++) {
                query += "OR Images.uid = '" + f[i].getUid() + "' ";
            }

            query += "GROUP BY keey " +
                     "HAVING COUNT(Images.uid) = " + f.length +
                     " ORDER BY keey ";

            final Statement stat = conn.getConnection().createStatement();
            final ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                dlm.addElement(rs.getString(1));
            }

            stat.close();
            return dlm;
        } catch (final Exception sqle) {
//			log.debug(sqle);
            sqle.printStackTrace();
            return null;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final String getKeyWordsForImage(final DiskObject f) {
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::getKeyWordsForImage  pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();
            final StringBuffer strbuf = new StringBuffer();

            final String query = "SELECT keey "
                                 + "FROM KeyBridge JOIN Images "
                                 + "ON KeyBridge.uid = Images.uid "
                                 + "WHERE Images.uid = '" + f.getUid() + "' "
                                 + "GROUP BY keey "
                                 + "ORDER BY keey ";

            final Statement stat = conn.getConnection().createStatement();
            final ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                strbuf.append(rs.getString(1) + ", ");
            }

            stat.close();
            if (strbuf.length() > 0) {
                return strbuf.substring(0, strbuf.lastIndexOf(","));
            } else {
                return "";
            }
        } catch (final Exception sqle) {
//			log.debug(sqle);
            sqle.printStackTrace();
            return "";
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final boolean insertNewKeyWord(final String keyWord) {
        if (JIPreferences.getInstance().isReadOnly()) {
            return false;
        }
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::insertNewKeyWord  pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();
            boolean result = true;
            final Statement stat = conn.getConnection().createStatement();
            final String query = "INSERT INTO KeyWords (keey) VALUES ('" + keyWord + "')";
            result &= stat.execute(query);
            return result;
        } catch (final Exception e) {
//			log.debug(e);
            e.printStackTrace();
            return false;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final boolean deleteKeyWords(final String[] keyWords) {
        boolean result = true;
        for (final String keyWord : keyWords) {
            result &= deleteKeyWord(keyWord);
        }
        return result;
    }

    public final boolean deleteKeyWord(final String keyWord) {
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::deleteKeyWord  pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();
            boolean result = true;
            final Statement stat = conn.getConnection().createStatement();

            String query = "DELETE FROM KeyBridge WHERE keey = '" + keyWord + "'";
            result &= stat.execute(query);

            query = "DELETE FROM KeyWords WHERE keey = '" + keyWord + "'";
            result &= stat.execute(query);
            return result;
        } catch (final Exception e) {
//			log.debug(e);
            e.printStackTrace();
            return false;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final boolean insertKeyWordsForImage(final DiskObject[] f, final Enumeration<String> e, boolean append) {
        if (JIPreferences.getInstance().isReadOnly()) {
            return false;
        }
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::insertKeyWordsForImage  pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();
            boolean result = true;
            final Statement stat = conn.getConnection().createStatement();

            if (!append) {
                for (final DiskObject element : f) {
                    try {
                        final String query = "DELETE FROM KeyBridge WHERE uid = '"
                                             + element.getUid() + "'";
                        result &= stat.execute(query);
                    } catch (final SQLException exp) {
//							log.debug(exp);
                    }
                }
            }

            while (e.hasMoreElements()) {
                final String keyWord = e.nextElement();
//					log.debug("insertKeyWordsForImage " + keyWord);

                for (final DiskObject element : f) {
                    try {
                        final String query = "INSERT INTO KeyBridge (keey,uid) VALUES ('" + keyWord.trim().toLowerCase() + "','" + element.getUid() + "')";
                        result &= stat.execute(query);
                    } catch (final SQLException exp) {
                    }
                }

            }
            stat.execute("COMMIT");
            stat.close();
            return true;
        } catch (final Exception e1) {
            e1.printStackTrace();
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception ee) {
                ee.printStackTrace();
            }
        }
        return true;

    }

    // Category Methods
    public String getCategoryID(final String category) {
        String result = null;
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::getCategoryID  pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();
            final Statement stat = conn.getConnection().createStatement();

            final ResultSet rs = stat.executeQuery("SELECT uid FROM Categories WHERE category = '" + category + "'");

            if (rs.next()) {
                result = rs.getString(1);
            }

            rs.close();
            stat.close();
            return result;
        } catch (final Exception sqle) {
//			log.debug(sqle);
            sqle.printStackTrace();
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String getCategory(final String catID) {

        String result = null;
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::getCategory  pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();

            final Statement stat = conn.getConnection().createStatement();
            final ResultSet rs = stat.executeQuery("SELECT category FROM Categories WHERE uid = '" + catID + "' ");

            if (rs.next()) {
                result = rs.getString(1);
            }

            rs.close();
            stat.close();
            return result;
        } catch (final Exception sqle) {
//			log.debug(sqle);
            sqle.printStackTrace();
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String[] getImageCategoryNames(final DiskObject dObj) {
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::getCategory  pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();

            final Statement stat = conn.getConnection().createStatement();
            final ResultSet rs = stat.executeQuery("SELECT category FROM CategoryImage JOIN Categories ON CategoryImage.categoryUID = Categories.uid WHERE imageUID = '" + dObj.getUid() + "' ");

            final Vector<String> vec = new Vector<String>();
            while (rs.next()) {
                vec.add(rs.getString(1));
            }

            rs.close();
            stat.close();
            return vec.toArray(new String[vec.size()]);
        } catch (final Exception sqle) {
            sqle.printStackTrace();
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getCategoriesForImage(final DiskObject dObj) {
        final StringBuffer strBuf = new StringBuffer();
        for (final String name : getImageCategoryNames(dObj)) {
            strBuf.append(name).append(", ");
        }
        return (strBuf.length() > 2 ? strBuf.substring(0, strBuf.length() - 2) : "");
    }

    public String addCategoryTreeNode(final JICatTreeNode parent, final String category) {
        if (JIPreferences.getInstance().isReadOnly()) {
            return null;
        }
        String catID = null;
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::addCategoryTreeNode  pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();
            final Statement stat = conn.getConnection().createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            final UID uid = new UID();
            catID = uid.toString();

            stat.execute("INSERT INTO Categories (uid,category) VALUES ('" + catID + "','" + category + "')");
            stat.execute("COMMIT");
            stat.execute("INSERT INTO CategoryChild (parentUID,childUID) VALUES ('" + parent.getCategoryID() + "','" + catID + "')");
            stat.execute("COMMIT");
            stat.close();

        } catch (final Exception sqle) {
            sqle.printStackTrace();
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return catID;
    }

    public void deleteCategoryTreeNode(final JICatTreeNode node) {
        JIGenericConnection conn = null;
        try {
            //log.debug("JIThumbnailJDBC::deleteCategoryTreeNode  pool Active = "+pool.getNumActive()+" Idle = "+pool.getNumIdle());
            conn = (JIGenericConnection) pool.borrowObject();
            // Remove Child Nodes
            final Statement stat = conn.getConnection().createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            String query = "DELETE FROM CategoryImage " +
                           "WHERE categoryUID  = '" + node.getCategoryID() + "' ";

            stat.execute(query);

            query = "DELETE FROM CategoryChild " +
                    "WHERE childUID  = '" + node.getCategoryID() + "' ";

            stat.execute(query);

            query = "DELETE FROM CategoryChild " +
                    "WHERE parentUID  = '" + node.getCategoryID() + "' ";

            stat.execute(query);

            query = "DELETE FROM Categories " +
                    "WHERE uid  = '" + node.getCategoryID() + "' ";

            stat.execute(query);
            stat.close();
        } catch (final Exception sqle) {
            sqle.printStackTrace();
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean categoryExists(final JICatTreeNode node, final String newName) {
        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();
            boolean rtrnCode = false;
            final JICatTreeNode parent = node.getParent();
            final String parentID = parent.getCategoryID();
            final String query = "SELECT category " +
                                 "FROM Categories JOIN CategoryChild " +
                                 "ON Categories.uid = CategoryChild.childUID " +
                                 "WHERE category = '" + newName + "' " +
                                 "AND CategoryChild.parentUID = '" + parentID + "'";

            final Statement stat = conn.getConnection().createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            final ResultSet rs = stat.executeQuery(query);
            if (rs.next()) {
                rtrnCode = true;
            }
            rs.close();
            stat.close();

            return rtrnCode;
        } catch (final Exception sqle) {
            sqle.printStackTrace();
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void updateCategoryTreeNode(final JICatTreeNode node, final String newName) {
        if (JIPreferences.getInstance().isReadOnly()) {
            return;
        }

//		log.debug("updateCategoryTreeNode "+node);
        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();

            // Remove Child Nodes
            final String query = "UPDATE Categories " +
                                 "SET category = '" + newName + "' " +
                                 "WHERE uid = '" + node.getCategoryID() + "' ";

            final Statement stat = conn.getConnection().createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

//			log.debug(query);
            stat.execute(query);

            stat.close();
        } catch (final Exception sqle) {
            sqle.printStackTrace();
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public JICatTreeNode[] exploreCategoryTreeNodeIDs(final String category, final String uid) {
        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();
            final String query = "SELECT Categories.category, Categories.uid " +
                                 "FROM Categories JOIN CategoryChild " +
                                 "ON Categories.uid = CategoryChild.childUID " +
                                 "WHERE CategoryChild.parentUID = '" + uid.toString() + "'  " +
                                 "AND CategoryChild.childUID IS NOT NULL ";

//			log.debug("exploreCategoryTreeNode category = " + category);
            final Statement stat = conn.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            final ResultSet rs = stat.executeQuery(query);
            rs.last();
            final int rowCount = rs.getRow();
            rs.beforeFirst();
            int i = 0;
            final JICatTreeNode[] children = new JICatTreeNode[rowCount];
            while (rs.next()) {
                children[i++] = new JICatTreeNode(rs.getString(1), rs.getString(2));
            }
            stat.close();
            return children;
        } catch (final Exception sqle) {
            sqle.printStackTrace();
            return null;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean insertImagesInCategory(final DiskObject[] ff, final JICatTreeNode node, final boolean append) {
        if (JIPreferences.getInstance().isReadOnly()) {
            return false;
        }
        if (!append) {
            removeImagesForCategory(ff, node);
        }
        return insertImagesInCategory(ff, node.getCategoryID(), append);
    }

    private boolean insertImagesInCategory(final DiskObject[] ff, final String categoryID, final boolean append) {
        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();
            final Statement stat = conn.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            final ArrayList<DiskObject> files = new ArrayList<DiskObject>(Arrays.asList(ff));

            String query;
            boolean result = true;


            for (final DiskObject file : files) {
                try {
                    query = "INSERT INTO CategoryImage (categoryUID,imageUID) " + "VALUES ('" + categoryID + "','" + file.getUid() + "')";
                    log.debug(query);
                    result &= stat.execute(query);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }

            stat.execute("COMMIT");
            stat.close();
            return result;
        } catch (final Exception exp) {
            exp.printStackTrace();
            return false;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean insertImagesInCategory(final DiskObject[] ff, final Vector<JICatTreeNode> nodes, final boolean append) {
        if (JIPreferences.getInstance().isReadOnly()) {
            return false;
        }
        boolean result = true;

        insertImagesInCategory(ff, nodes.firstElement(), append);
        for (final JICatTreeNode node : nodes) {
            result &= insertImagesInCategory(ff, node, true);
        }
        return result;
    }

    public boolean removeImagesForCategory(final DiskObject[] ff, final JICatTreeNode node) {
        if (JIPreferences.getInstance().isReadOnly()) {
            return false;
        }
        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();
            final Statement stat = conn.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            final ArrayList<DiskObject> files = new ArrayList<DiskObject>(Arrays.asList(ff));

            boolean result = true;

            for (final DiskObject file : files) {

                final String sql = "DELETE FROM CategoryImage WHERE  categoryUID = '" + node.getCategoryID() + "' AND imageUID = '" + file.getUid() + "' ";
//				log.debug(sql);
                result &= stat.execute(sql);

//				for (final Object catID: node.getSubNodeIDs()) {
//					sql = "DELETE FROM CategoryImage WHERE  categoryUID = '" + catID + "' AND imageUID = '" + getImageID(file.getPath()) + "' ";
//					log.debug(sql);
//					result &= stat.execute(sql);
//				}
            }

            stat.execute("COMMIT");
            stat.close();
            return result;
        } catch (final Exception exp) {
            exp.printStackTrace();
            return false;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean removeImagesForCategory(final DiskObject[] ff, final Vector<JICatTreeNode> nodes) {
        if (JIPreferences.getInstance().isReadOnly()) {
            return false;
        }
        boolean result = true;

        for (final JICatTreeNode node : nodes) {
            result &= removeImagesForCategory(ff, node);
        }
        return result;
    }

    public final OrderedDiskObjectList getCategoryImages(final Vector<JICatTreeNode> nodesVec, final OrderedDiskObjectList dlm) {
        final OrderedDiskObjectList results = new OrderedDiskObjectList();
        if (nodesVec.size() <= 0) {
            return results;
        }

        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();

            String query = "SELECT DISTINCT name,path,length,lastModified,width,height,oreintation " +
                           "FROM Images JOIN CategoryImage " +
                           "ON Images.uid = CategoryImage.imageUID " +
                           "WHERE CategoryImage.categoryUID IN (";

            final Enumeration<JICatTreeNode> nodes = nodesVec.elements();
            JICatTreeNode node;

            if (nodes.hasMoreElements()) {
                node = nodes.nextElement();
                query += "'" + node.getCategoryID() + "'";

                if (JIPreferences.getInstance().isCategoryIncludeSubs()) {
                    final Enumeration<String> e = node.getSubNodeIDs().elements();
                    while (e.hasMoreElements()) {
                        query += ",'" + e.nextElement() + "' ";
                    }
                }

                while (nodes.hasMoreElements()) {
                    node = nodes.nextElement();
                    query += ",'" + node.getCategoryID().toString() + "' ";

                    if (JIPreferences.getInstance().isCategoryIncludeSubs()) {
                        final Enumeration<String> e = node.getSubNodeIDs().elements();
                        while (e.hasMoreElements()) {
                            query += ",'" + e.nextElement() + "' ";
                        }
                    }
                }
            }
            query += ") ORDER BY " + orderBy();

            final Statement stat = conn.getConnection().createStatement();
            final ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
//				log.debug("getCategoryImages diskObj = " + rs.getString(2));
                final DiskObject dObj = new DiskObject(rs.getString(1),
                        rs.getString(2),
                        rs.getLong(3),
                        rs.getLong(4),
                        DiskObject.TYPE_FILE,
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getInt(7));
                if (dlm != null) {
                    dlm.addElement(dObj);
                }
                results.add(dObj);
            }
            stat.close();
            return results;
        } catch (final Exception sqle) {
            sqle.printStackTrace();
            return null;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final OrderedDiskObjectList getCategoryImagesAND(final Vector<JICatTreeNode> nodesVec, final OrderedDiskObjectList dlm) {
        final OrderedDiskObjectList results = new OrderedDiskObjectList();
        if (nodesVec.size() <= 0) {
            return results;
        }

        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();
            Object[] uids;
            String query;
            ArrayList<String> resultPaths;
            Statement stat;
            ResultSet rs;

            JICatTreeNode node = nodesVec.elementAt(0);

            query = "SELECT DISTINCT Images.uid " +
                    "FROM Images JOIN CategoryImage " +
                    "ON Images.uid = CategoryImage.imageUID " +
                    "WHERE CategoryImage.categoryUID IN ('" + node.getCategoryID() + "' ";

            if (JIPreferences.getInstance().isCategoryIncludeSubs()) {
                for (final Object subNodeID : node.getSubNodeIDs()) {
                    query += ",'" + subNodeID + "' ";
                }
            }

            query += ")";

            stat = conn.getConnection().createStatement();
//			log.debug("getCategoryImagesAND - query = "+query);
            rs = stat.executeQuery(query);

            resultPaths = new ArrayList<String>();
            while (rs.next()) {
                resultPaths.add(rs.getString(1));
            }

            uids = resultPaths.toArray();

            for (int k = 1; k < nodesVec.size(); k++) {

                node = (JICatTreeNode) nodesVec.elementAt(k);

                query = "SELECT DISTINCT Images.uid " +
                        "FROM Images JOIN CategoryImage " +
                        "ON Images.uid = CategoryImage.imageUID " +
                        "WHERE CategoryImage.categoryUID IN ('" + node.getCategoryID() + "' ";

                if (JIPreferences.getInstance().isCategoryIncludeSubs()) {
                    for (final Object subNodeID : node.getSubNodeIDs()) {
                        query += ",'" + subNodeID + "' ";
                    }
                }

                query += ") AND Images.uid IN ('" + uids[0] + "'";

                for (int j = 1; j < uids.length; j++) {
                    query += ",'" + uids[j] + "'";
                }

                query += ")";

//				log.debug("getCategoryImagesAND - query = "+query);
                rs = stat.executeQuery(query);
                resultPaths = new ArrayList<String>();
                while (rs.next()) {
                    resultPaths.add(rs.getString(1));
                }
                uids = resultPaths.toArray();
                stat.close();
            }

            if (uids.length > 0) {
                query = "SELECT DISTINCT name,path,length,lastModified,width,height,oreintation " +
                        "FROM Images " +
                        "WHERE uid IN ('" + uids[0] + "' ";

                for (int x = 1; x < uids.length; x++) {
                    query += ", '" + uids[x] + "' ";
                }

                query += ") ORDER BY " + orderBy();

                stat = conn.getConnection().createStatement();
//				log.debug("getCategoryImagesAND - query = "+query);
                rs = stat.executeQuery(query);

                while (rs.next()) {
                    final DiskObject dObj = new DiskObject(rs.getString(1),
                            rs.getString(2),
                            rs.getLong(3),
                            rs.getLong(4),
                            DiskObject.TYPE_FILE,
                            rs.getInt(5),
                            rs.getInt(6),
                            rs.getInt(7));
                    if (dlm != null) {
                        dlm.addElement(dObj);
                    }
                    results.add(dObj);
                }
                stat.close();
            }
            return results;
        } catch (final Exception sqle) {
            sqle.printStackTrace();
            return null;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

//	public Vector<JICatTreeNode> getCategoriesForImage(DiskObject dObj) {
//    	JIGenericConnection conn = null;
//    	try {
//    		conn = (JIGenericConnection)pool.borrowObject();
//    		final Vector<JIDateTreeNode> results = new Vector<JIDateTreeNode>();
//			Statement stat;
//			ResultSet rs;
//
//			String query = "SELECT categories.category, categories.uid " +
//					"FROM categories JOIN categoryimage ON categoryimage.categoryUID=categories.uid " +
//					"WHERE categoryimage.imageUID = "+dObj.getUid();
//    	} catch (final Exception sqle) {
//			log.debug(sqle);
//			sqle.printStackTrace();
//			return null;
//    	} finally {
//    		try {
//    			pool.returnObject(conn);
//    		} catch (final Exception e) {
//    			e.printStackTrace();
//    		}
//    	}
//	}

    public Vector<JIDateTreeNode> getImageYears() {
        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();
            final Vector<JIDateTreeNode> results = new Vector<JIDateTreeNode>();
            Statement stat;
            ResultSet rs;

            final String query = "SELECT DISTINCT year " +
                                 "FROM Images ORDER By year";

//			log.debug(" - getImageYears() query = "+query);
            stat = conn.getConnection().createStatement();
            rs = stat.executeQuery(query);

            while (rs.next()) {
                results.addElement(new JIDateTreeNode(rs.getString(1), JIDateTreeNode.YEAR));
            }
            rs.close();
            stat.close();
            return results;
        } catch (final Exception sqle) {
            sqle.printStackTrace();
            return null;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Vector<JIDateTreeNode> getImageYearMonths(final String year) {
        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();
            final Vector<JIDateTreeNode> results = new Vector<JIDateTreeNode>(12);
            Statement stat;
            ResultSet rs;

            final String query = "SELECT DISTINCT month " +
                                 "FROM Images WHERE year = '" + year + "' ";

//			log.debug(" - getImageYearMonths() query = "+query);
            stat = conn.getConnection().createStatement();
            rs = stat.executeQuery(query);
            while (rs.next()) {
                results.addElement(new JIDateTreeNode(rs.getString(1), JIDateTreeNode.MONTH));
            }
            rs.close();
            stat.close();
            Collections.sort(results, new MonthNameComparator<String>());
            return results;
        } catch (final Exception sqle) {
            sqle.printStackTrace();
            return null;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Vector<JIDateTreeNode> getImageYearMonthDays(final String year, final String month) {
        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();
            final Vector<JIDateTreeNode> results = new Vector<JIDateTreeNode>();
            Statement stat;
            ResultSet rs;

            final String query = "SELECT DISTINCT day " +
                                 "FROM Images WHERE year = '" + year + "' AND month = '" + month + "' ";

//			log.debug(" - getImageYearMonthDays() query = "+query);
            stat = conn.getConnection().createStatement();
            rs = stat.executeQuery(query);

            while (rs.next()) {
                results.addElement(new JIDateTreeNode(rs.getString(1), JIDateTreeNode.DAY));
            }

            rs.close();
            stat.close();
            return results;
        } catch (final Exception sqle) {
            sqle.printStackTrace();
            return null;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }


    public OrderedDiskObjectList getDateImages(final Vector<JIDateTreeNode> nodes, final OrderedDiskObjectList dlm) {
        final OrderedDiskObjectList results = new OrderedDiskObjectList();
        if (nodes.size() <= 0) {
            return results;
        }

        JIGenericConnection conn = null;
        try {
            Statement stat;
            ResultSet rs;
            conn = (JIGenericConnection) pool.borrowObject();
            stat = conn.getConnection().createStatement();
            for (final JIDateTreeNode node : nodes) {

                String query = null;

                if (node.getType() == JIDateTreeNode.DAY) {
                    query = "SELECT DISTINCT name,path,length,lastModified,width,height,oreintation " +
                            "FROM Images WHERE year = '" + node.getParent().getParent().toString() + "' " +
                            "AND month = '" + node.getParent().toString() + "' AND day = '" + node.toString() + "'";
                } else if (node.getType() == JIDateTreeNode.MONTH) {
                    query = "SELECT DISTINCT name,path,length,lastModified,width,height,oreintation " +
                            "FROM Images WHERE year = '" + node.getParent().toString() + "' " +
                            "AND month = '" + node.toString() + "'";
                } else if (node.getType() == JIDateTreeNode.YEAR) {
                    query = "SELECT DISTINCT name,path,length,lastModified,width,height,oreintation " +
                            "FROM Images WHERE year = '" + node.toString() + "' ";
                }
                if (query != null) {

//					log.debug(" - getYearMonthDayImages() query = " + query + " ORDER By " + orderBy());
                    rs = stat.executeQuery(query + " ORDER By " + orderBy());

                    while (rs.next()) {
                        final DiskObject dObj = new DiskObject(rs.getString(1),
                                rs.getString(2),
                                rs.getLong(3),
                                rs.getLong(4),
                                DiskObject.TYPE_FILE,
                                rs.getInt(5),
                                rs.getInt(6),
                                rs.getInt(7));
                        if (dlm != null) {
                            dlm.addElement(dObj);
                        }
                        results.add(dObj);
                    }
                    rs.close();
                }
            }
            stat.close();
        } catch (final Exception sqle) {
            sqle.printStackTrace();
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public void clearConnections() {
        try {
            pool.clear();
        } catch (final UnsupportedOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            pool.close();
            pool = null;
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        instance = null;
    }

    public final boolean removeFile(final DiskObject diskObject) {
        final Statement stat;
        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();
            stat = conn.getConnection().createStatement();

            stat.execute("DELETE FROM Images " +
                         "WHERE uid = '" + diskObject.getUid() + "' ");

            stat.execute("DELETE FROM " + getPathUID(diskObject.getPath()) + " WHERE uid = '" + diskObject.getUid() + "' ");

            stat.execute("DELETE FROM KeyBridge " +
                         "WHERE uid = '" + diskObject.getUid() + "' ");

            stat.execute("DELETE FROM CategoryImage " +
                         "WHERE imageUID = '" + diskObject.getUid() + "' ");

            stat.execute("COMMIT");

            stat.close();

            return true;
        } catch (final Exception e) {
            //log.debug(e);
            e.printStackTrace();
            return false;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final boolean removeFile(final File file) {
        return removeFile(JIUtility.portablePath(file.getPath().trim()));
    }

    public final boolean removeFile(final String path) {
        final Statement stat;
        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();
            stat = conn.getConnection().createStatement();
            final String imageID = getImageID(path.trim());

            stat.execute("DELETE FROM Images " +
                         "WHERE uid = '" + imageID + "' ");

            stat.execute("DELETE FROM " + getPathUID(path) + " WHERE uid = '" + imageID + "' ");

            stat.execute("DELETE FROM KeyBridge " +
                         "WHERE uid = '" + imageID + "' ");

            stat.execute("DELETE FROM CategoryImage " +
                         "WHERE imageUID = '" + imageID + "' ");

            stat.execute("COMMIT");

            stat.close();

            return true;
        } catch (final Exception e) {
            //log.debug(e);
            e.printStackTrace();
            return false;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final boolean moveFile(final String from, final String to) {
        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();
            final Statement stat = conn.getConnection().createStatement();
            final String imageUID = getImageID(from);

            final boolean result = stat.execute("UPDATE Images " +
                                                "SET path = '" + JIUtility.portablePath(to) + "' " +
                                                "WHERE uid = '" + imageUID + "'");

            stat.execute("DELETE FROM " + getPathUID(from) + " WHERE uid = '" + imageUID + "' ");
            stat.execute("COMMIT");

            stat.close();

            return result;
        } catch (final Exception e) {
            //log.debug(e);
            e.printStackTrace();
            return false;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final boolean refreshThumbnails(final DiskObject[] diskObjects) {
        boolean result = true;
        for (final DiskObject dObj : diskObjects) {
            result &= refreshThumbnail(dObj);
        }
        return result;
    }

    public final boolean refreshThumbnail(final DiskObject diskObject) {
        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();
            final Statement stat = conn.getConnection().createStatement();

            stat.execute("DELETE FROM " + getPathUID(diskObject.getPath()) + " WHERE uid = '" + diskObject.getUid() + "' ");

            stat.close();
            return true;
        } catch (final Exception e) {
            //log.debug(e);
            e.printStackTrace();
            return false;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final boolean removeFromDBFor(final DiskObject[] diskObjects) {
        boolean result = true;
        for (final DiskObject dObj : diskObjects) {
            result &= removeFile(dObj);
        }
        return result;
    }

    public final boolean removeKeyWordsFor(final DiskObject[] diskObjects) {
        final Statement stat;
        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();
            stat = conn.getConnection().createStatement();

            for (final DiskObject dObj : diskObjects) {
                stat.execute("DELETE FROM KeyBridge " +
                             "WHERE uid = '" + dObj.getUid() + "' ");
            }

            stat.execute("COMMIT");
            stat.close();

            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final boolean removeCategoriesFor(final DiskObject[] diskObjects) {
        final Statement stat;
        JIGenericConnection conn = null;
        try {
            conn = (JIGenericConnection) pool.borrowObject();
            stat = conn.getConnection().createStatement();

            for (final DiskObject dObj : diskObjects) {
                stat.execute("DELETE FROM CategoryImage " +
                             "WHERE imageUID = '" + dObj.getUid() + "' ");
            }

            stat.execute("COMMIT");
            stat.close();

            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                pool.returnObject(conn);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final void clean() {
        final Thread runner = new Thread() {
            @Override
            public void run() {
                Runnable runnable = new Runnable() {
                    public void run() {

                        Statement stat = null;
                        JIGenericConnection conn = null;
                        try {
                            conn = (JIGenericConnection) pool.borrowObject();
                            stat = conn.getConnection().createStatement();
                            ResultSet rs = stat.executeQuery("SELECT path FROM Images");

                            int removeCount = 0;
                            Vector<String> cleanup = new Vector<String>();
                            while (rs.next()) {
                                String path = rs.getString(1);
                                if (!new File(rs.getString(1)).exists()) {
                                    cleanup.add(path);
                                    ++removeCount;
                                }
                            }
                            rs.close();

                            for (String pathval : cleanup) {

                                try {
                                    stat.execute("DELETE FROM Images WHERE path = '" + pathval + "'");
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    stat.execute("DELETE FROM KeyBridge WHERE uid = '" + getImageID(pathval) + "'");
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    stat.execute("DELETE FROM CategoryImage WHERE imageUID = '" + getImageID(pathval) + "'");
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }

                            stat.execute("DELETE FROM KeyBridge WHERE uid not in (SELECT uid FROM Images)");
                            stat.execute("DELETE FROM CategoryImage WHERE imageUID not in (SELECT uid FROM Images)");
                            stat.close();

                        } catch (Exception e) {
                            // Print out the error message
                            e.printStackTrace();
                        } finally {
                            try {
                                pool.returnObject(conn);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                SwingUtilities.invokeLater(runnable);
            }
        };
        runner.start();
    }

    protected String orderBy() {
        switch (JIPreferences.getInstance().getThumbnailSortOrder()) {
            case 4:
                return "LCASE(path), LCASE(name)";
            case 2:
                return "LCASE(length), LCASE(name)";
            case 3:
                return "LCASE(lastModified), LCASE(name)";
            case 1:
            case 0:
            default:
                return "LCASE(name)";
        }
    }
}
