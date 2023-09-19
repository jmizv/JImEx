package de.jmizv.jiexplorer.db;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import de.jmizv.jiexplorer.gui.JIIcon;
import de.jmizv.jiexplorer.gui.table.JIFileTableModel;
import de.jmizv.jiexplorer.gui.thumb.JIThumbnailList;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIUtility;


public final class JIThumbnailCache {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JIThumbnailCache.class);

    private static JIThumbnailCache instance;

    private ExecutorService qExecutor;
    private ExecutorService dbExecutor;

    private boolean processAllIcons = false;
    private final Map<String, JIIcon> cachedThumbnails;


    private JIThumbnailCache() {
        this.qExecutor = Executors.newFixedThreadPool(2);
        this.dbExecutor = Executors.newFixedThreadPool(2);

        this.cachedThumbnails = Collections.synchronizedMap(new LinkedHashMap<>(80) {

            private static final long serialVersionUID = 1L;
            private static final int MAX_ENTRIES = 80;

            @Override
            protected boolean removeEldestEntry(final Map.Entry eldest) {
                return size() > MAX_ENTRIES;
            }
        });
        //ImageIO.setUseCache(false);

    }

    public static JIThumbnailCache getInstance() {
        if (instance == null) {
            instance = new JIThumbnailCache();
        }
        return instance;
    }

    public final boolean isProcessAllIcons() {
        return this.processAllIcons;
    }

    public synchronized final void invalidate() {
        this.cachedThumbnails.clear();
    }

    public void init(File dir) {
        dbExecutor.shutdownNow();
        qExecutor.shutdown();
        dbExecutor = Executors.newFixedThreadPool(3);
        qExecutor = Executors.newFixedThreadPool(2);

        // @TODO is this any good?
        System.runFinalization();
        System.gc();
    }

    public void close() {
        dbExecutor.shutdown();
        qExecutor.shutdown();
    }


    public Icon getThumbnailFor(final DiskObject diskObject, final JIThumbnailList list, final int index) {
        try {

            final JIIcon jiIcon = this.cachedThumbnails.get(diskObject.getPath());
            if (jiIcon != null) {
                return jiIcon;
            }
            final BufferedImage bImage = JIThumbnailService.getInstance().getThumbnail(diskObject);
            if (bImage != null) {
                return JIUtility.scaleIcon(bImage);
            }

        } catch (final Exception e) {
            log.debug(e);
        }
        if (diskObject.setAsLoading()) {
            loadThumbnail(diskObject, list, index);
            return null;
        }
        return JIUtility.getSystemIcon(diskObject);
    }

    private final void loadThumbnail(final DiskObject diskObject, final JIThumbnailList thumbnailList, final int index) {

        if (this.processAllIcons) {
            return;
        }

        try {
            /////////////////////////////////////////////////////////////////////////
            this.qExecutor.execute(new Runnable() {
                public void run() {
                    try {
                        if ((index > thumbnailList.getLastVisibleIndex()) ||
                            (index < thumbnailList.getFirstVisibleIndex())) {
                            return;
                        }

                        final BufferedImage tIcon = JIUtility.createThumbnailRetry(diskObject);

                        if (tIcon != null) {
                            JIThumbnailCache.this.cachedThumbnails.put(diskObject.getPath(), JIUtility.scaleIcon(tIcon));
                            thumbnailList.getThumbnailListModel().notifyAsUpdated(index);

                            ///////////////////////////////////////////////////////////
                            // store thumbnail into the cache
                            JIThumbnailCache.this.dbExecutor.execute(new Runnable() {
                                public void run() {
                                    try {
                                        JIThumbnailService.getInstance().putThumbnail(diskObject, tIcon, null);
                                    } catch (final Exception ea) {
                                        log.debug(ea);
                                    }
                                }
                            });
                            //
                            //////////////////////////////////////////////////////////////
                        }
                    } catch (final Exception e) {
                        log.debug(e);
                    } finally {
                        diskObject.setAsLoaded();
                        thumbnailList.getThumbnailListModel().notifyAsUpdated(index);
                    }
                }
            });
            /////////////////////////////////////////////////////////////////////////

        } catch (final Exception exp) {
            log.debug(exp);
        }
    }

    public Icon getThumbnailFor(final DiskObject diskObject, final JTable table, final int row) {
        try {

            final JIIcon jiIcon = this.cachedThumbnails.get(diskObject.getPath());
            if (jiIcon != null) {
                return jiIcon;
            }
            final BufferedImage bImage = JIThumbnailService.getInstance().getThumbnail(diskObject);
            if (bImage != null) {
                return JIUtility.scaleTableIcon(bImage);
            }

        } catch (final Exception e) {
            log.debug(e);
        }
        if (diskObject.setAsLoading()) {
            loadThumbnail(diskObject, table, row);
            return null;
        }
        return JIUtility.getSystemIcon(diskObject);
    }

    public void loadThumbnail(final DiskObject diskObject, final JTable table, final int row) {
        if (this.processAllIcons) {
            return;
        }

        try {
            /////////////////////////////////////////////////////////////////////////
            this.qExecutor.execute(new Runnable() {
                public void run() {
                    try {
                        final BufferedImage tIcon = JIUtility.createThumbnailRetry(diskObject);

                        if (tIcon != null) {
                            JIThumbnailCache.this.cachedThumbnails.put(diskObject.getPath(), JIUtility.scaleTableIcon(tIcon));
                            if (table != null) {
                                ((JIFileTableModel) table.getModel()).fireTableCellUpdated(row, 0);
                                ((JIFileTableModel) table.getModel()).fireTableCellUpdated(row, 4);
                            }
                            ///////////////////////////////////////////////////////////
                            // store thumbnail into the cache
                            JIThumbnailCache.this.dbExecutor.execute(new Runnable() {
                                public void run() {
                                    try {
                                        JIThumbnailService.getInstance().putThumbnail(diskObject, tIcon, null);
                                    } catch (final Exception ea) {
                                        log.debug(ea);
                                    }
                                }
                            });
                            //
                            //////////////////////////////////////////////////////////////
                        }
                    } catch (final Exception e) {
                        log.debug(e);
                    } finally {
                        diskObject.setAsLoaded();
                    }
                }
            });
            /////////////////////////////////////////////////////////////////////////

        } catch (final Exception exp) {
            log.debug(exp);
        }
        if (table != null) {
            ((AbstractTableModel) table.getModel()).fireTableCellUpdated(row, 3);
        }
    }

//	public final void refreshThumbnail(final JIThumbnailList thumbnailList) {
//		System.runFinalization();
//		System.gc();
//
//		final Object[] diskObjects = thumbnailList.getSelectedValues();
//		final int[] indices =   thumbnailList.getSelectedIndices();
//
//		if ((diskObjects != null) && (diskObjects.length > 0)) {
//			for(int i = 0; i < diskObjects.length; i++) {
//				// store thumbnail into the cache
//				JIThumbnailDB.getInstance().refreshThumbnail(((DiskObject)diskObjects[i]));
//				loadThumbnail((DiskObject)diskObjects[i],thumbnailList,indices[i]);
//				// notify thumbnailPanel that icon is now unavailable
//				thumbnailList.getThumbnailListModel().notifyAsUpdated(indices[i]);
//			}
//		}
//	}

    public void cleanThumbDB() {
        JIThumbnailService.getInstance().clean();
    }

    public final void setProcessAllIcons(final boolean processAllIcons) {
        this.processAllIcons = processAllIcons;
    }
}
