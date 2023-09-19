package de.jmizv.jiexplorer.util;

import com.drew.metadata.exif.ExifDirectoryBase;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;

import java.awt.Dimension;
import java.io.*;
import java.net.URI;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import javax.imageio.metadata.IIOMetadata;
import javax.swing.filechooser.FileSystemView;

import com.drew.imaging.jpeg.JpegMetadataReader;
import de.jmizv.jiexplorer.db.JIThumbnailService;


public final class DiskObject implements Comparator<DiskObject>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String osName = System.getProperty("os.name").toLowerCase();

    private static final Format formatter = new SimpleDateFormat("yyyy/MMM/dd");

    public static final String TYPE_COMPUTER = "Computer";
    public static final String TYPE_DRIVER = "Driver";
    public static final String TYPE_FOLDER = "Folder";
    public static final String TYPE_FILE = "File";

    public static final String MIMETYPE_JPEG = "image/jpeg";
    public static final String MIMETYPE_GIF = "image/gif";
    public static final String MIMETYPE_PNG = "image/png";
    public static final String MIMETYPE_TIFF = "image/tiff";

    private String name;
    private String type;
    private String path;
    private String uid;

    //private File file = null;
    private String suffix = null;

    private int width = -1;
    private int height = -1;
    private int orientation = -1;

    private long length = 0;
    private long lastModified = 0;

    private boolean validated = false;

    boolean loading = false;


    private URI uri;

    private boolean isDir;
    private boolean exists;

    private String absolutePath;
    private String displayName;

    public void update(File file) {
        uri = file.toURI();
        length = file.length();
        lastModified = file.lastModified();
        isDir = file.isDirectory();
        exists = file.exists();
        absolutePath = file.getAbsolutePath();
        this.path = JIUtility.portablePath(absolutePath);

        try {
            if (file.exists()) {
                //type = FileSystemView.getFileSystemView().getSystemTypeDescription(file);
                displayName = FileSystemView.getFileSystemView().getSystemDisplayName(file);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            type = "";
            displayName = "";
        }

        name = !file.getName().isEmpty() ? file.getName() : file.toURI().getPath();
        if (osName.startsWith("win") && (name.indexOf('/') == 0)) {
            name = name.substring(1);
        }
    }

    public void update(String name, String path, long lastModified, long length) {
        this.length = length;
        this.lastModified = lastModified;
        this.path = (path != null ? JIUtility.portablePath(path) : null);

        this.name = name;
        if (osName.startsWith("win") && (name.indexOf('/') == 0)) {
            this.name = name.substring(1);
        }
    }

    public DiskObject(final String name, final String path, final long length, final long lastModified, final String type, final int width, final int height, final int oreintation) {
        this.name = name;
        this.length = length;
        this.lastModified = lastModified;
        this.path = (path != null ? JIUtility.portablePath(path) : null);

        this.width = width;
        this.height = height;
        this.orientation = oreintation;
        this.type = type;
    }

    public DiskObject(final File f) {
        update(f);
    }

    public DiskObject(final File f, final String type) {
        this.type = type;
        update(f);
    }

    public String getMimeType() {
        if (getSuffix().equals("jpg") || getSuffix().equals("jpeg")) {
            return MIMETYPE_JPEG;
        }
        if (getSuffix().equals("gif")) {
            return MIMETYPE_GIF;
        }
        if (getSuffix().equals("png")) {
            return MIMETYPE_PNG;
        }
        if (getSuffix().equals("tiff") || getSuffix().equals("tif")) {
            return MIMETYPE_TIFF;
        }
        return null;
    }

    public final String getShortDate() {
        final Format formatter = new SimpleDateFormat("dd/MM/yy hh:mm a");
        return formatter.format(new java.util.Date(this.lastModified));
    }

    public final String getDim() {
        if ((this.width > 0) && (this.height > 0)) {
            return this.width + " x " + this.height;
        }
        return "";
    }

    public Dimension getDimension() {
        return new Dimension(this.width, this.height);
    }

    public String getSize() {
        return JIUtility.length2KB(this.length);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public synchronized boolean setAsLoading() {
        if (!this.loading) {
            return (this.loading = true);
        }
        return false;
    }

    public synchronized void setAsLoaded() {
        this.loading = false;
    }

    public File getFile() {
        if (absolutePath != null) {
            return new File(absolutePath);
        } else {
            return new File(path);
        }

    }

    public int compare(DiskObject a, DiskObject b) {
        return a.getFile().compareTo(b.getFile());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DiskObject) {
            return compare(this, (DiskObject) obj) == 0;
        }
        return false;
    }

    public String[] getDateCategories() {
        return formatter.format(new java.util.Date(this.lastModified)).split("/");
    }

    public String toLogString() {
        return "[" + this.path + "] size: " + JIUtility.length2KB(this.length) + "  Date: " + this.getShortDate() + "  Dim: " + this.getDim() + "  UID: " + this.getUid();
    }

    public synchronized String getSuffix() {
        if (this.suffix == null) {
            this.suffix = JIUtility.suffix(this.name);
        }

        return this.suffix;
    }

    public boolean exists() {
        return getFile().exists();
    }


    public boolean validate() throws IOException {
        if (!this.validated) {
            if (!getFile().exists()) {
                throw new FileNotFoundException(getFile().getAbsolutePath());
            }

            if (!getFile().canRead()) {
                throw new IOException("Cannot read " + getFile().getAbsolutePath());
            }
        }
        return (this.validated = true);
    }

    public synchronized int getOrientation() {
        if (this.orientation == -1) {
            try {
                if (getSuffix().equals("jpg")) {
                    final ExifDirectoryBase directory = JpegMetadataReader.readMetadata(getFile()).getFirstDirectoryOfType(ExifDirectoryBase.class);
                    if (directory != null) {
                        if (directory.containsTag(ExifDirectoryBase.TAG_ORIENTATION)) {
                            this.orientation = directory.getInt(ExifDirectoryBase.TAG_ORIENTATION);
                        }
                        if (directory.containsTag(ExifDirectoryBase.TAG_EXIF_IMAGE_WIDTH)) {
                            this.width = directory.getInt(ExifDirectoryBase.TAG_EXIF_IMAGE_WIDTH);
                        }
                        if (directory.containsTag(ExifDirectoryBase.TAG_EXIF_IMAGE_HEIGHT)) {
                            this.height = directory.getInt(ExifDirectoryBase.TAG_EXIF_IMAGE_HEIGHT);
                        }
                    }
                } else {
                    IIOMetadata iioMetadata = JIUtility.getImageReader(this).getImageMetadata(0);
                    if (iioMetadata instanceof TIFFMetadataSupport) {
                        IFD ifd = ((TIFFMetadataSupport) iioMetadata).getPrimaryIFD();
                        this.orientation = ifd.getOrientation().intValue();
                        this.width = ifd.getImageWidth();
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace(System.err);
                this.orientation = 1;
            }
        }
        return this.orientation;
    }

    public synchronized int getOrientationValue() {
        return this.orientation;
    }

    public synchronized void setOrientation(final int orientation) {
        this.orientation = orientation;
    }

    /**
     * @return the uid
     */
    public synchronized String getUid() {
        if ((this.uid == null) || (this.uid.trim().length() < 2)) {
            this.uid = JIThumbnailService.getInstance().getImageID(this);
        }
        return this.uid;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(final long lastModified) {
        this.lastModified = lastModified;
    }

    public long getLength() {
        return this.length;
    }

    public void setLength(final long length) {
        this.length = length;
    }

    public String getName() {
        return (displayName == null ? name : displayName);
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public boolean isValidated() {
        return this.validated;
    }

    public void setValidated(final boolean validated) {
        this.validated = validated;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }
}

