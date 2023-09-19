package de.jmizv.jiexplorer.db;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.metadata.IIOMetadata;
import javax.swing.DefaultListModel;

import de.jmizv.jiexplorer.gui.cattree.JICatTreeNode;
import de.jmizv.jiexplorer.gui.datetree.JIDateTreeNode;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.OpenWith;
import de.jmizv.jiexplorer.util.OrderedDiskObjectList;


public interface JIExplorerDB {
    String HSQL_JDBC_DRIVER = "org.hsqldb.jdbcDriver";

    String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    String CONNECTION_URL = "jdbc:mysql://localhost/JIExplorer";

    boolean putThumbnail(final DiskObject diskObject, final BufferedImage image, final IIOMetadata metadata);

    BufferedImage getThumbnail(final DiskObject diskObject);

    Map<String, String> getValidThumbnails(final List<File> dirs);

    boolean removeFile(final DiskObject diskObject);

    boolean removeFile(final File file);

    boolean removeFile(final String path);

    boolean removeFromDBFor(final DiskObject[] diskObjects);

    boolean removeKeyWordsFor(final DiskObject[] diskObjects);

    boolean removeCategoriesFor(final DiskObject[] diskObjects);

    boolean refreshThumbnails(final DiskObject[] diskObjects);

    boolean refreshThumbnail(final DiskObject diskObject);

    DefaultListModel<String> getKeyWords(DiskObject[] f, DefaultListModel<String> dlm);

    OrderedDiskObjectList getKeyWordImagesOR(String[] keys, OrderedDiskObjectList dlm);

    OrderedDiskObjectList getKeyWordImagesAND(String[] keys, OrderedDiskObjectList dlm);

    DefaultListModel<String> getKeyWords(DefaultListModel<String> dlm);

    DefaultListModel<String> getKeyWordsForImage(DiskObject[] f, DefaultListModel<String> dlm);

    String getKeyWordsForImage(DiskObject f);

    boolean insertNewKeyWord(String keyWord);

    boolean deleteKeyWord(String keyWord);

    boolean deleteKeyWords(final String[] keyWords);

    boolean insertKeyWordsForImage(final DiskObject[] f, final Enumeration<String> e, boolean append);

    void getDiskObject(final DiskObject dObj);

    String getCategoryID(final String path);

    String getCategory(final String catID);

    String addCategoryTreeNode(final JICatTreeNode parent, final String category);

    void deleteCategoryTreeNode(final JICatTreeNode node);

    boolean categoryExists(final JICatTreeNode node, final String newName);

    void updateCategoryTreeNode(final JICatTreeNode node, final String newName);

    JICatTreeNode[] exploreCategoryTreeNodeIDs(final String category, final String categoryID);

    boolean insertImagesInCategory(final DiskObject[] ff, final Vector<JICatTreeNode> nodesVec, boolean append);

    boolean removeImagesForCategory(final DiskObject[] ff, final Vector<JICatTreeNode> nodesVec);

    OrderedDiskObjectList getCategoryImages(final Vector<JICatTreeNode> nodesVec, final OrderedDiskObjectList dlm);

    OrderedDiskObjectList getCategoryImagesAND(final Vector<JICatTreeNode> nodesVec, final OrderedDiskObjectList dlm);

    String getCategoriesForImage(final DiskObject dObj);

    String[] getImageCategoryNames(final DiskObject dObj);

    boolean moveFile(final String from, final String to);

    boolean copyFile(final File from, final File to);

    Vector<JIDateTreeNode> getImageYears();

    Vector<JIDateTreeNode> getImageYearMonths(final String year);

    Vector<JIDateTreeNode> getImageYearMonthDays(final String year, final String month);

    OrderedDiskObjectList getDateImages(final Vector<JIDateTreeNode> node, final OrderedDiskObjectList dlm);

    Vector<OpenWith> getOpenWith();

    void deleteOpenWith(OpenWith ow);

    void updateOpenWith(OpenWith ow);

    void insertOpenWith(OpenWith ow);

    String getImageID(final DiskObject path);

    String getImageID(final String path);

    void clearConnections();

    void clean();

    void close();
}
