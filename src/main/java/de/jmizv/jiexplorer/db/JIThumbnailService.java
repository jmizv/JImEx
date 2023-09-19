package de.jmizv.jiexplorer.db;

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.metadata.IIOMetadata;
import javax.swing.DefaultListModel;

import de.jmizv.jiexplorer.gui.cattree.JICatTreeNode;
import de.jmizv.jiexplorer.gui.datetree.JIDateTreeNode;
import de.jmizv.jiexplorer.gui.preferences.JIPreferences;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIUtility;
import de.jmizv.jiexplorer.util.OpenWith;
import de.jmizv.jiexplorer.util.OrderedDiskObjectList;
import de.jmizv.jiexplorer.util.StockDialogs;

public class JIThumbnailService implements JIExplorerDB {

    private static JIExplorerDB instance;


    private final JIExplorerDB delegate;
    private boolean dbAvailable = true;

    private JIThumbnailService() {
        JIGenericConnection conn;
        try {
            conn = new JIGenericConnection(JIPreferences.getInstance().getDatabaseType());
            if (!conn.validate()) {
                if (StockDialogs.yesNo(new Frame(), "Corrupt/Undefined DB - press yes to create.", "") == 0) {
                    if (!conn.makeTables()) {
                        JIGenericConnection.defaultRecovery();
                        if (!conn.validate()) {
                            if (StockDialogs.yesNo(new Frame(), "Corrupt/Undefined DB - press yes to create.", "") == 0) {
                                if (!conn.makeTables()) {
                                    dbAvailable = false;
                                    JIGenericConnection.defaultRecovery();
                                }
                            }
                        }

                    }
                }
            }
        } catch (final Exception e) {
            JIGenericConnection.defaultRecovery();
            conn = new JIGenericConnection(JIPreferences.getInstance().getDatabaseType());
            if (!conn.validate()) {
                if (StockDialogs.yesNo(new Frame(), "Corrupt/Undefined DB - press yes to create.", "") == 0) {
                    if (!conn.makeTables()) {
                        dbAvailable = false;
                        JIGenericConnection.defaultRecovery();
                    }
                }
            }
        }
        conn.close();
        if (dbAvailable) {
            delegate = JIThumbnailPooledJDBC.getInstance();
        } else {
            delegate = null;
        }
    }

    public static JIExplorerDB getInstance() {
        if (instance == null) {
            instance = new JIThumbnailService();
        }
        return instance;
    }

    public static synchronized JIExplorerDB reload() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
        instance = new JIThumbnailService();
        return instance;
    }

    public String addCategoryTreeNode(final JICatTreeNode parent, final String category) {
        return (dbAvailable) ? delegate.addCategoryTreeNode(parent, category) : category;

    }

    public boolean categoryExists(final JICatTreeNode node, final String newName) {
        return dbAvailable && delegate.categoryExists(node, newName);
    }

    public void clean() {
        if (dbAvailable) {
            delegate.clean();
        }
    }

    public void clearConnections() {
        if (dbAvailable) {
            delegate.clearConnections();
        }
    }

    public void close() {
        delegate.close();
    }

    public boolean copyFile(final File from, final File to) {
        return dbAvailable && delegate.copyFile(from, to);
    }

    public void deleteCategoryTreeNode(final JICatTreeNode node) {
        if (dbAvailable) {
            delegate.deleteCategoryTreeNode(node);
        }
    }

    public boolean deleteKeyWords(final String[] keyWords) {
        return dbAvailable && delegate.deleteKeyWords(keyWords);
    }

    public boolean deleteKeyWord(final String keyWord) {
        return dbAvailable && delegate.deleteKeyWord(keyWord);
    }

    public JICatTreeNode[] exploreCategoryTreeNodeIDs(final String category, final String categoryID) {
        return (dbAvailable) ? delegate.exploreCategoryTreeNodeIDs(category, categoryID) : new JICatTreeNode[0];
    }

    public String getCategory(final String catID) {
        return (dbAvailable) ? delegate.getCategory(catID) : null;
    }

    public String getCategoryID(final String path) {
        return (dbAvailable) ? delegate.getCategoryID(path) : null;
    }

    public OrderedDiskObjectList getCategoryImages(final Vector<JICatTreeNode> nodesVec, final OrderedDiskObjectList dlm) {
        return (dbAvailable) ? delegate.getCategoryImages(nodesVec, dlm) : new OrderedDiskObjectList(0);
    }

    public OrderedDiskObjectList getCategoryImagesAND(final Vector<JICatTreeNode> nodesVec, final OrderedDiskObjectList dlm) {
        return (dbAvailable) ? delegate.getCategoryImagesAND(nodesVec, dlm) : new OrderedDiskObjectList(0);
    }

    public String getImageID(final DiskObject path) {
        return (dbAvailable) ? delegate.getImageID(path) : "";
    }

    public String getImageID(final String path) {
        return (dbAvailable) ? delegate.getImageID(path) : "";
    }

    public OrderedDiskObjectList getKeyWordImagesAND(final String[] keys, final OrderedDiskObjectList dlm) {
        return (dbAvailable) ? delegate.getKeyWordImagesAND(keys, dlm) : new OrderedDiskObjectList(0);
    }

    public OrderedDiskObjectList getKeyWordImagesOR(final String[] keys, final OrderedDiskObjectList dlm) {
        return (dbAvailable) ? delegate.getKeyWordImagesOR(keys, dlm) : new OrderedDiskObjectList(0);
    }

    public DefaultListModel<String> getKeyWords(final DefaultListModel<String> dlm) {
        return (dbAvailable) ? delegate.getKeyWords(dlm) : dlm;
    }

    public DefaultListModel<String> getKeyWords(final DiskObject[] f, final DefaultListModel<String> dlm) {
        return (dbAvailable) ? delegate.getKeyWords(f, dlm) : dlm;
    }

    public DefaultListModel<String> getKeyWordsForImage(final DiskObject[] f, final DefaultListModel<String> dlm) {
        return (dbAvailable) ? delegate.getKeyWordsForImage(f, dlm) : dlm;
    }

    public BufferedImage getThumbnail(final DiskObject diskObject) {
        return (dbAvailable) ? delegate.getThumbnail(diskObject) : JIUtility.blankImage();
    }

    public boolean insertImagesInCategory(final DiskObject[] ff, final Vector<JICatTreeNode> nodesVec, final boolean append) {
        return dbAvailable && delegate.insertImagesInCategory(ff, nodesVec, append);
    }

    public boolean insertKeyWordsForImage(final DiskObject[] f, final Enumeration<String> e, final boolean append) {
        return dbAvailable && delegate.insertKeyWordsForImage(f, e, append);
    }

    public boolean insertNewKeyWord(final String keyWord) {
        return dbAvailable && delegate.insertNewKeyWord(keyWord);
    }

    public boolean moveFile(final String from, final String to) {
        return dbAvailable && delegate.moveFile(from, to);
    }

    public boolean putThumbnail(final DiskObject diskObject, final BufferedImage image, final IIOMetadata metadata) {
        return dbAvailable && delegate.putThumbnail(diskObject, image, metadata);
    }

    public boolean removeFile(final DiskObject diskObject) {
        return dbAvailable && delegate.removeFile(diskObject);
    }

    public boolean removeFile(final File file) {
        return dbAvailable && delegate.removeFile(file);
    }

    public boolean removeFile(final String path) {
        return dbAvailable && delegate.removeFile(path);
    }

    public boolean removeImagesForCategory(final DiskObject[] ff, final Vector<JICatTreeNode> nodesVec) {
        return dbAvailable && delegate.removeImagesForCategory(ff, nodesVec);
    }

    public void updateCategoryTreeNode(final JICatTreeNode node, final String newName) {
        delegate.updateCategoryTreeNode(node, newName);
    }

    public void getDiskObject(final DiskObject dObj) {
        delegate.getDiskObject(dObj);
    }

    public Vector<JIDateTreeNode> getImageYearMonthDays(final String year, final String month) {
        return (dbAvailable) ? delegate.getImageYearMonthDays(year, month) : new Vector<>(0);
    }

    public Vector<JIDateTreeNode> getImageYearMonths(final String year) {
        return (dbAvailable) ? delegate.getImageYearMonths(year) : new Vector<>(0);
    }

    public Vector<JIDateTreeNode> getImageYears() {
        return (dbAvailable) ? delegate.getImageYears() : new Vector<>(0);
    }

    public OrderedDiskObjectList getDateImages(final Vector<JIDateTreeNode> nodes, final OrderedDiskObjectList dlm) {
        return (dbAvailable) ? delegate.getDateImages(nodes, dlm) : new OrderedDiskObjectList(0);
    }

    public void deleteOpenWith(final OpenWith ow) {
        delegate.deleteOpenWith(ow);
    }

    public Vector<OpenWith> getOpenWith() {
        return (dbAvailable) ? delegate.getOpenWith() : new Vector<>(0);
    }

    public void insertOpenWith(final OpenWith ow) {
        delegate.insertOpenWith(ow);
    }

    public void updateOpenWith(final OpenWith ow) {
        delegate.updateOpenWith(ow);
    }

    public String getKeyWordsForImage(final DiskObject f) {
        return (dbAvailable) ? delegate.getKeyWordsForImage(f) : "";
    }

    public String getCategoriesForImage(final DiskObject dObj) {
        return (dbAvailable) ? delegate.getCategoriesForImage(dObj) : "";
    }

    public String[] getImageCategoryNames(final DiskObject dObj) {
        return (dbAvailable) ? delegate.getImageCategoryNames(dObj) : new String[0];
    }

    public boolean removeCategoriesFor(final DiskObject[] diskObjects) {
        return dbAvailable && delegate.removeCategoriesFor(diskObjects);
    }

    public boolean removeFromDBFor(final DiskObject[] diskObjects) {
        return dbAvailable && delegate.removeFromDBFor(diskObjects);
    }

    public boolean removeKeyWordsFor(final DiskObject[] diskObjects) {
        return dbAvailable && delegate.removeKeyWordsFor(diskObjects);
    }

    public boolean refreshThumbnail(final DiskObject diskObject) {
        return dbAvailable && delegate.refreshThumbnail(diskObject);
    }

    public boolean refreshThumbnails(final DiskObject[] diskObjects) {
        return dbAvailable && delegate.refreshThumbnails(diskObjects);
    }

    public Map<String, String> getValidThumbnails(final List<File> dirs) {
        return (dbAvailable) ? delegate.getValidThumbnails(dirs) : new HashMap<>();
    }
}
