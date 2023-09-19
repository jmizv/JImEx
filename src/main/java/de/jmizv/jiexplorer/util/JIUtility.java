package de.jmizv.jiexplorer.util;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.RandomAccessStreamReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.filechooser.FileSystemView;

import org.w3c.dom.Node;

import com.drew.metadata.exif.ExifReader;
import de.jmizv.jiexplorer.db.JIThumbnailService;
import de.jmizv.jiexplorer.gui.BatchRenameDialog;
import de.jmizv.jiexplorer.gui.JIIcon;
import de.jmizv.jiexplorer.gui.preferences.JIPreferences;

public final class JIUtility {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JIUtility.class);

    private static final String osName = System.getProperty("os.name").toLowerCase();
    public static final int TAG_THUMBNAIL_DATA = 61441;

    public static Icon folderIcon = FileSystemView.getFileSystemView().getSystemIcon(new File(System.getProperty("user.home")));

    public static int EXIF = 0xE1;
    public static int IPTC = 0xED;

    private static final Set<String> unwanted = new HashSet<>();

    static {
        unwanted.add("com.sun.media.imageioimpl.plugins.jpeg.CLibJPEGImageReader");
        unwanted.add("com.sun.media.imageioimpl.plugins.jpeg.CLibJPEGImageWriter");
    }

    public static String portablePath(final String path) {

        final StringBuilder result = new StringBuilder();
        //startIdx and idxOld delimit various chunks of aInput; these
        //chunks always end where aOldPattern begins
        int startIdx = 0;
        int idxOld;
        while ((idxOld = path.indexOf("\\", startIdx)) >= 0) {
            //grab a part of aInput which does not include aOldPattern
            result.append(path, startIdx, idxOld);
            //add aNewPattern to take place of aOldPattern
            result.append("/");

            //reset the startIdx to just after the current match, to see
            //if there are any further matches
            startIdx = idxOld + 1;
        }
        //the final chunk will go to the end of aInput
        result.append(path.substring(startIdx));

        return result.toString();
    }

    public static String systemPath(final String path) {

        if (File.separator.equals("\\")) {
            final StringBuilder result = new StringBuilder();
            //startIdx and idxOld delimit various chunks of aInput; these
            //chunks always end where aOldPattern begins
            int startIdx = 0;
            int idxOld;
            while ((idxOld = path.indexOf("/", startIdx)) >= 0) {
                //grab a part of aInput which does not include aOldPattern
                result.append(path, startIdx, idxOld);
                //add aNewPattern to take place of aOldPattern
                result.append("\\");

                //reset the startIdx to just after the current match, to see
                //if there are any further matches
                startIdx = idxOld + 1;
            }
            //the final chunk will go to the end of aInput
            result.append(path.substring(startIdx));
            return result.toString();
        }
        return path;
    }

    public static String escape(final String source) {
        final StringBuilder sbuf = new StringBuilder();
        final char[] chars = source.toCharArray();
        for (final char c : chars) {
            if (c == '\'') {
                sbuf.append("\\\'");
            } else {
                sbuf.append(c);
            }
        }
        return sbuf.toString();
    }

    public static String length2KB(final long length) {
        final long kbCount = (length + 1024) / 1024;
        final String strlength = String.valueOf(kbCount);
        return (kbCount > 999 ? strlength.substring(0, strlength.length() - 3) + "," + strlength.substring(strlength.length() - 3) : strlength) + " KB ";
    }

    public static int freeMem() {
        return (int) Runtime.getRuntime().freeMemory() / 1024 / 1024;
    }

    public static String memoryInf() { //boolean clean) {
        final long freeMem = Runtime.getRuntime().freeMemory();
        final long totalMem = Runtime.getRuntime().totalMemory();
        final long maxMem = Runtime.getRuntime().maxMemory();

        final String msg = "Free Mem: " + freeMem / 1024 / 1024 +
                           " Total Mem: " + totalMem / 1024 / 1024 +
                           " Max Mem: " + maxMem / 1024 / 1024;

        if (((double) totalMem / (double) maxMem > .75) && (freeMem < 40)) {
            System.runFinalization();
            System.gc();
            return msg + " - Clean up run";
        }
        return msg;
    }

    public static File[] getRoots() {
        return constructRoots();
    }

    private static File[] constructRoots() {
        File[] roots;
        final Vector<File> rootsVector = new Vector<>();

        if (osName.toLowerCase().startsWith("win")) {
            // Run through all possible mount points and check
            // for their existance.
            for (char c = 'C'; c <= 'Z'; c++) {
                final char device[] = {c, ':', '\\'};
                final String deviceName = new String(device);
                final File deviceFile = new File(deviceName);

                if ((deviceFile != null) && deviceFile.exists()) {
                    rootsVector.addElement(deviceFile);
                }
            }
        } else if (osName.toLowerCase().startsWith("mac")) {
            for (final File root : (new File("/Volumes")).listFiles()) {
                rootsVector.addElement(root);
            }
        } else {
            for (final File root : File.listRoots()) {
                rootsVector.addElement(root);
            }
        }
        roots = new File[rootsVector.size()];
        rootsVector.copyInto(roots);

        return roots;
    }

    public static Icon getSystemIcon(final DiskObject dObj) {
        if (dObj.getFile().exists()) {
            return FileSystemView.getFileSystemView().getSystemIcon(dObj.getFile());
        } else {
            return FileSystemView.getFileSystemView().getSystemIcon(new File(System.getProperty("user.home")));
        }
    }

    public static Icon getSystemIcon(final File f) {
        if (f.exists()) {
            return FileSystemView.getFileSystemView().getSystemIcon(f);
        } else {
            return FileSystemView.getFileSystemView().getSystemIcon(new File(System.getProperty("user.home")));
        }
    }

    public static String suffix(final String name) {
        final int i = name.lastIndexOf('.');
        if (i > 0) {
            return name.toLowerCase().substring(i + 1);
        }
        return null;
    }

    public static boolean isSupportedImage(final String suffix) {
        return suffix.equals("jpg") ||
               suffix.equals("gif") ||
               suffix.equals("png") ||
               suffix.equals("crw") ||
               suffix.equals("cr2") ||
               suffix.equals("dng") ||
               suffix.equals("mrw") ||
               suffix.equals("nef") ||
               suffix.equals("pef") ||
               suffix.equals("jpeg") ||
               suffix.equals("bmp");// ||
        //suffix.equals("tif");
    }

    public static boolean isSupportedImage(final File file) {
        final String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") ||
               fileName.endsWith(".gif") ||
               fileName.endsWith(".png") ||
               fileName.endsWith(".crw") ||
               fileName.endsWith(".cr2") ||
               fileName.endsWith(".dng") ||
               fileName.endsWith(".mrw") ||
               fileName.endsWith(".nef") ||
               fileName.endsWith(".pef") ||
               fileName.endsWith(".jpeg") ||
               fileName.endsWith(".bmp");// ||
        //fileName.endsWith(".tif");
    }

    public static BufferedImage openImage(final DiskObject dObj) {
        try {
            if (dObj.getSuffix().equals("cr2") || dObj.getSuffix().equals("crw")) {
                final ImageReader imageReader = getImageReader(dObj);
                return orientImage(imageReader.readThumbnail(0, 0), dObj.getOrientation());
            } else {
                final ImageReader imageReader = getImageReader(dObj);
                return orientImage(imageReader.read(0), dObj.getOrientation());
            }
        } catch (final Exception exp) {
            try {
                return orientImage(ImageIO.read(dObj.getFile()), dObj.getOrientation());
            } catch (final Exception e) {
                exp.printStackTrace(System.err);
            }
            return null;
        }
    }

    public static ExifDirectoryBase getExifDirectory(final Node node) {
        if (node.getNodeName().equals("unknown")) {
            if (Integer.parseInt(node.getAttributes().getNamedItem("MarkerTag").getNodeValue()) == EXIF) {
                final byte[] data = (byte[]) ((IIOMetadataNode) node).getUserObject();
                RandomAccessReader reader = new RandomAccessStreamReader(new ByteArrayInputStream(data));
                Metadata metadata = new Metadata();
                new ExifReader().extract(reader, metadata);
                return metadata.getFirstDirectoryOfType(ExifDirectoryBase.class);
            }
        }
        Node child = node.getFirstChild();
        while (child != null) {
            final ExifDirectoryBase directory = getExifDirectory(child);
            if (directory != null) {
                return directory;
            }
            child = child.getNextSibling();
        }
        return null;
    }

    public static BufferedImage blankImage() {
        final BufferedImage image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, 400, 400);
        g2d.dispose();
        return image;
    }

    public static float scaleFactor(final Dimension d1, final int w, final int h) {
        return Math.min((float) d1.width / w, (float) d1.height / h);
    }

    public static float scaleFactor(final Dimension d1, final Insets i, final int w, final int h) {
        return Math.min((float) (d1.width - (i.left + i.right)) / w,
                (float) (d1.height - (i.top + i.bottom)) / h);
    }

    public static byte[] bufferedImageToByteArray(final BufferedImage _image) {

		/*final JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(baOut);
		final JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(_image);
		param.setQuality(0.75f, false);
		encoder.setJPEGEncodeParam(param);*/
        try (final ByteArrayOutputStream baOut = new ByteArrayOutputStream()) {
            //encoder.encode(_image);
            ImageIO.write(_image, "jpg", baOut);
            return baOut.toByteArray();
        } catch (final IOException e) {
            e.printStackTrace(System.err);
            log.debug("could not encode image" + e);
        }
        return null;
    }

    public static BufferedImage byteArrayToBufferedImage(final byte[] _pic) {
        final BufferedImage image;
        try (final ByteArrayInputStream in = new ByteArrayInputStream(_pic)) {
            image = ImageIO.read(in);
            return image;
        } catch (final IOException e) {
            e.printStackTrace(System.err);
            log.debug(e);
        }
        return null;
    }

    public static BufferedImage orientImage(BufferedImage image, final int orientation) {

        //log.debug("orientImage orientation = "+orientation);
        if (orientation == 1) {
            return image;
        }

        boolean rotate = false;
        boolean clockWise = false;
        boolean mirrorHorizontal = false;
        boolean mirrorVertical = false;

        switch (orientation) {
            case 2:
                mirrorHorizontal = true;
                break;
            case 3:
                mirrorHorizontal = true;
                mirrorVertical = true;
                break;
            case 4:
                mirrorVertical = true;
                break;
            case 5:
                rotate = true;
                clockWise = true;
                mirrorHorizontal = true;
                break;
            case 6:
                rotate = true;
                clockWise = true;
                break;
            case 7:
                rotate = true;
                mirrorHorizontal = true;
                break;
            case 8:
                rotate = true;
                break;
        }

        final int targetWith = (rotate ? image.getHeight() : image.getWidth());
        final int targetHeight = (rotate ? image.getWidth() : image.getHeight());

        final AffineTransform transform = new AffineTransform();

        if (mirrorVertical) {
            transform.translate(0, targetHeight);
            transform.scale(1, -1);
        }
        if (mirrorHorizontal) {
            transform.translate(targetWith, 0);
            transform.scale(-1, 1);
        }
        if (rotate) {
            if (clockWise) {
                transform.translate(image.getHeight(), 0);
                transform.rotate(Math.PI / 2);
            } else {
                transform.translate(0, image.getWidth());
                transform.rotate(-Math.PI / 2);
            }
        }

        final BufferedImage copy = new BufferedImage(targetWith, targetHeight, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(image, transform, null);
        g2d.dispose();

        return copy;
    }

    public static boolean requestReplaceFile(final String path) {
        int rcode = JOptionPane.showConfirmDialog(null,
                "Do you want to replace \"" + path + "\" ?",
                "choose one", JOptionPane.YES_NO_OPTION);

        return rcode == JOptionPane.YES_OPTION;
    }


    public static int getFileCount(final File dir) {
        int results = 1;

        if (!dir.isDirectory()) {
            return results;
        }

        final File[] fileArray = dir.listFiles();
        for (final File element : fileArray) {
            results += getFileCount(element);
        }
        return results;
    }

    public static void deleteDirectory(final File dir) {
        if ((dir == null) || !dir.isDirectory() || dir.isHidden()) {
            return;
        }
        final File[] files = dir.listFiles();
        if (files != null) {
            for (final File f : files) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else {
                    JIThumbnailService.getInstance().removeFile(f);
                }
                deleteFile(f);
            }
        }
        dir.delete();
    }

    // This method returns a buffered image with the contents of an image
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent Pixels
        final boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            final GraphicsDevice gs = ge.getDefaultScreenDevice();
            final GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
        } catch (final HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        final Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    // This method returns true if the specified image has transparent pixels
    public static boolean hasAlpha(final Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage bimage) {
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        final PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (final InterruptedException e) {
        }

        // Get the image's color model
        final ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }


    public static BufferedImage createThumbnailRetry(final DiskObject thumbnail) {
        int retryCounter = 2;
        while (retryCounter > 0) {
            try {
                return createThumbnail(thumbnail);
            } catch (final OutOfMemoryError e) {
                //log.debug("Out of memory error detected ! Retrying !");
                //cachedThumbnails.clear();
                System.runFinalization();
                System.gc();
                retryCounter--;
            } catch (final Exception exp) {
                exp.printStackTrace();
                log.debug(exp);
                return null;
            }
        }
        return null;
    }

    private static BufferedImage createThumbnail(final DiskObject diskObject) throws IOException {
        final String fname = diskObject.getName().toLowerCase();

        if (fname.endsWith("jpg") || fname.endsWith("jpeg")) {
            final BufferedImage inImage = createThumbnailFromEXIF(diskObject);
            if (inImage != null) {
                return inImage;
            }
        }
        return createThumbnailFromFile(diskObject);
    }

    private static BufferedImage createThumbnailFromFile(final DiskObject diskObject) {
        BufferedImage image;
        ImageReader reader = null;

        try {
            reader = getImageReader(diskObject);

            if (reader != null) {

                if (reader.readerSupportsThumbnails()) {
                    try {
                        if (reader.hasThumbnails(0)) {
                            int indx = 0;
                            if (reader.getNumThumbnails(0) > 1) {
                                indx = 1;
                            }
                            image = reader.readThumbnail(0, indx);
                            diskObject.setHeight(reader.getThumbnailHeight(0, indx));
                            diskObject.setWidth(reader.getThumbnailWidth(0, indx));
                            final IIOMetadata iiometadata = reader.getImageMetadata(0);
                            if (iiometadata instanceof TIFFMetadataSupport) {
                                diskObject.setOrientation(((TIFFMetadataSupport) reader.getImageMetadata(0)).getPrimaryIFD().getOrientation().intValue());
                            } else {
                                diskObject.setOrientation(1);
                            }
                            return orientImage(scaleThumbnail(image), diskObject.getOrientation());
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                        log.debug(e.getMessage());
                        try {
                            image = ImageIO.read(diskObject.getFile());
                            diskObject.setHeight(image.getHeight(null));
                            diskObject.setWidth(image.getWidth(null));
                            return scaleThumbnail(image);
                        } catch (final Exception e1) {
                            return blankImage();
                        }
                    } finally {
                        reader.dispose();
                    }
                }
                try {
                    final ImageReadParam param = reader.getDefaultReadParam();

                    //
                    // Set Image sampling read every third byte
                    // BIG perfomance boost for generating thumbnails
                    //
                    if (diskObject.getLength() > 1024 * 80) {
                        if (diskObject.getLength() > 1024 * 999) {
                            param.setSourceSubsampling(8, 8, 0, 0);
                        }
                        param.setSourceSubsampling(3, 3, 0, 0);
                    } else {
                        param.setSourceSubsampling(1, 1, 0, 0);
                    }

                    image = reader.read(0, param);
                    diskObject.setHeight(reader.getHeight(0));
                    diskObject.setWidth(reader.getWidth(0));

                    final IIOMetadata iiometadata = reader.getImageMetadata(0);
                    if (iiometadata instanceof TIFFMetadataSupport) {
                        diskObject.setOrientation(((TIFFMetadataSupport) reader.getImageMetadata(0)).getPrimaryIFD().getOrientation().intValue());
                    } else {
                        diskObject.setOrientation(1);
                    }
                    return orientImage(scaleThumbnail(image), diskObject.getOrientation());
                } catch (final IndexOutOfBoundsException ioobe) {
                    log.debug("Unable to find an image reader.");
                    return blankImage();
                } catch (final Exception e) {
                    e.printStackTrace();
                    log.debug(e.getMessage());
                    image = ImageIO.read(diskObject.getFile());
                    diskObject.setHeight(image.getHeight(null));
                    diskObject.setWidth(image.getWidth(null));
                    return scaleThumbnail(image);
                } finally {
                    reader.dispose();
                }
            } else {
                image = ImageIO.read(diskObject.getFile());
                diskObject.setHeight(image.getHeight(null));
                diskObject.setWidth(image.getWidth(null));
                return scaleThumbnail(image);
            }

        } catch (final Exception e) {
            e.printStackTrace();
            log.debug(e.getMessage());
        }

        return null;


//		ImageRO

//		try {
//		JpegReader jpgReader = new JpegReader(diskObject.getFile(),false,8);
//		JpegMetadataReader jpgMetaDataReader = jpgReader.getJpegMetadataReader();
//		diskObject.height = jpgMetaDataReader.getHeight();
//		diskObject.width = jpgMetaDataReader.getWidth();
//		final float[] scale = new float[1];
//		scale[0] = Math.min((float)JIPreferences.getInstance().getIconDim().height/(float)diskObject.height,
//		(float)JIPreferences.getInstance().getIconDim().width/(float)diskObject.width);
//		JIUtility.toBufferedImage(MetadataUtils.getScaled(jpgReader,0,scale)[0]);
//		} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//		}
//		return null;
    }

    private static BufferedImage createThumbnailFromEXIF(final DiskObject diskObject) {
        try {
            ExifThumbnailDirectory directory = null;
            if (diskObject.getName().toLowerCase().endsWith(".jpg")) {
                directory = (ExifThumbnailDirectory) JpegMetadataReader.readMetadata(diskObject.getFile()).getFirstDirectoryOfType(ExifThumbnailDirectory.class);
            }
            if (directory != null && directory.containsTag(TAG_THUMBNAIL_DATA)) {
                if (directory.containsTag(ExifDirectoryBase.TAG_EXIF_IMAGE_HEIGHT)) {
                    diskObject.setHeight(directory.getInt(ExifDirectoryBase.TAG_EXIF_IMAGE_HEIGHT));
                }

                if (directory.containsTag(ExifDirectoryBase.TAG_EXIF_IMAGE_WIDTH)) {
                    diskObject.setWidth(directory.getInt(ExifDirectoryBase.TAG_EXIF_IMAGE_WIDTH));
                }

                if (directory.containsTag(ExifDirectoryBase.TAG_ORIENTATION)) {
                    diskObject.setOrientation(directory.getInt(ExifDirectoryBase.TAG_ORIENTATION));
                }
                var thumbnailData = JIUtility.byteArrayToBufferedImage(directory.getByteArray(TAG_THUMBNAIL_DATA));
                var scaledThumbnail = scaleThumbnail(thumbnailData);
                return JIUtility.orientImage(scaledThumbnail, diskObject.getOrientation());
            } else {
                final BufferedImage image = ImageIO.read(diskObject.getFile());
                diskObject.setHeight(image.getHeight(null));
                diskObject.setWidth(image.getWidth(null));
                return scaleThumbnail(image);
            }
        } catch (IndexOutOfBoundsException ioobe) {
            log.debug("Unable to find an image reader.");
            return blankImage();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            log.debug(e.getMessage());
        }
        return null;
//////////////
// ImageRO
//
//		try {
//			JpegReader jpgReader = new JpegReader(diskObject.getFile(),false,1);
//			JpegMetadataReader jpgMetaDataReader = jpgReader.getJpegMetadataReader();
//			diskObject.height = jpgMetaDataReader.getHeight();
//			diskObject.width = jpgMetaDataReader.getWidth();
//			final float[] scale = new float[1];
//			scale[0] = Math.min((float)JIPreferences.getInstance().getIconDim().height/(float)diskObject.height,
//					(float)JIPreferences.getInstance().getIconDim().width/(float)diskObject.width);
//			ExifApp1[] exifArray = jpgMetaDataReader.getExif();
//			if (exifArray == null || exifArray.length == 0) {
//				return createThumbnailFromFile(diskObject);
//			}
//			return JIUtility.toBufferedImage(MetadataUtils.getThumbnail(jpgReader));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
    }

    public static JIIcon scaleIcon(final BufferedImage inImage) {
        final double scale = Math.min((double) JIPreferences.getInstance().getAdjustedIconDim().height / (double) inImage.getHeight(null),
                (double) JIPreferences.getInstance().getAdjustedIconDim().width / (double) inImage.getWidth(null));

        // Determine scaled size of new image.
        int scaledH, scaledW;
        if ((scaledW = (int) (scale * inImage.getWidth(null))) <= 0) {
            scaledW = 1;
        }
        if ((scaledH = (int) (scale * inImage.getHeight(null))) <= 0) {
            scaledH = 1;
        }

        final BufferedImage outImage = new BufferedImage(scaledW, scaledH, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2d;
        g2d = outImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.drawImage(inImage, AffineTransform.getScaleInstance(scale, scale), null);
        g2d.dispose();

        return new JIIcon(outImage);
    }

    public final static JIIcon scaleTableIcon(final BufferedImage inImage) {
        final double scale = Math.min((double) 16 / (double) inImage.getHeight(null), (double) 16 / (double) inImage.getWidth(null));

        // Determine scaled size of new image.
        int scaledH, scaledW;
        if ((scaledW = (int) (scale * inImage.getWidth(null))) <= 0) {
            scaledW = 1;
        }
        if ((scaledH = (int) (scale * inImage.getHeight(null))) <= 0) {
            scaledH = 1;
        }


        final BufferedImage outImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2d;
        g2d = outImage.createGraphics();
        g2d.setColor(new Color(235, 235, 235));
        g2d.fillRect(0, 0, 16, 16);
        g2d.translate((16 / 2) - (scaledW / 2), (16 / 2) - (scaledH / 2));
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.drawImage(inImage, AffineTransform.getScaleInstance(scale, scale), null);
        g2d.dispose();

        return new JIIcon(outImage);
    }

    public final static BufferedImage scaleThumbnail(Image inImage) {
        final double scale = Math.min((double) 300 / (double) inImage.getHeight(null),
                (double) 300 / (double) inImage.getWidth(null));

        // Determine scaled size of new image.
        int scaledH, scaledW;
        if ((scaledW = (int) (scale * inImage.getWidth(null))) <= 0) {
            scaledW = 1;
        }
        if ((scaledH = (int) (scale * inImage.getHeight(null))) <= 0) {
            scaledH = 1;
        }

        final BufferedImage outImage = new BufferedImage(scaledW, scaledH, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2d;
        g2d = outImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.drawImage(inImage, AffineTransform.getScaleInstance(scale, scale), null);
        g2d.dispose();

        inImage = null;

        return outImage;
    }

//    public static BufferedImage readJAImage(String filename) {
//    	BufferedImage image = null;
//
//        // Use the JAI API unless JAI_IMAGE_READER_USE_CODECS is set
//        if (System.getProperty("JAI_IMAGE_READER_USE_CODECS") == null) {
//            image = JAI.create("fileload", filename).getAsBufferedImage();
//        } else {
//            try {
//                // Use the ImageCodec APIs
//                SeekableStream stream = new FileSeekableStream(filename);
//                String[] names = ImageCodec.getDecoderNames(stream);
//                ImageDecoder dec =
//                    ImageCodec.createImageDecoder(names[0], stream, null);
//                RenderedImage im = dec.decodeAsRenderedImage();
//                image = PlanarImage.wrapRenderedImage(im).getAsBufferedImage();
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        // If the source image is colormapped, convert it to 3-band RGB.
//        if(image.getColorModel() instanceof IndexColorModel) {
//            // Retrieve the IndexColorModel
//            IndexColorModel icm = (IndexColorModel)image.getColorModel();
//
//            // Cache the number of elements in each band of the colormap.
//            int mapSize = icm.getMapSize();
//
//            // Allocate an array for the lookup table data.
//            byte[][] lutData = new byte[3][mapSize];
//
//            // Load the lookup table data from the IndexColorModel.
//            icm.getReds(lutData[0]);
//            icm.getGreens(lutData[1]);
//            icm.getBlues(lutData[2]);
//
//            // Create the lookup table object.
//            LookupTableJAI lut = new LookupTableJAI(lutData);
//
//            // Replace the original image with the 3-band RGB image.
//            image = JAI.create("lookup", image, lut).getAsBufferedImage();
//        }
//
//        return image;
//    }


    public static ImageReader getImageReader(final DiskObject dObj)
            throws FileNotFoundException, IOException {

        dObj.validate();
        ImageInputStream imageInputStream = null;

        try {
            imageInputStream = ImageIO.createImageInputStream(dObj.getFile());
            for (final Iterator<ImageReader> iterator = ImageIO.getImageReadersBySuffix(dObj.getSuffix()); iterator.hasNext(); ) {
                final ImageReader reader = iterator.next();
                final String pluginClassName = reader.getOriginatingProvider().getPluginClassName();

                if ((reader != null) && !unwanted.contains(pluginClassName)
                    && reader.getOriginatingProvider().canDecodeInput(imageInputStream)
                    && !(dObj.getSuffix().equalsIgnoreCase("tif") && reader.getClass().getName().startsWith("it.tidalwave"))) {
                    reader.setInput(imageInputStream);
                    return reader;
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
            log.debug(e.getMessage());
        }

        try {
            if (imageInputStream != null) {
                imageInputStream.close();
            }
        } catch (final IOException e) {
            e.printStackTrace();
            log.debug(e.getMessage());
        }
        return null;
    }

    public static final void renameByDate(final OrderedDiskObjectList files, final String prefix, final int formatIndx, final ProgressMonitor pm) {
        final SimpleDateFormat format = new SimpleDateFormat(BatchRenameDialog.DATE_FORMAT[formatIndx]);

        if (pm != null) {
            pm.setMaximum(files.size());
        }

        int progressCnt = 0;

        for (final DiskObject f : files) {
            if (f.getFile().isFile()) {
                final int dot = f.getFile().getName().lastIndexOf(".");
                final String suffix = (dot > -1 ? f.getFile().getName().substring(dot) : "");
                final long datelong = f.getFile().lastModified();
                final Date date = new Date(datelong);
                final String dateStr = format.format(date);

                //final long size = f.length();
                //final byte[] bytes = new byte[(int)size];

                try {

                    final FileInputStream fis = new FileInputStream(f.getFile());
                    final FileOutputStream fos = new FileOutputStream(new File(f.getFile().getParent(), prefix + dateStr + (suffix.length() > 0 ? "." + suffix : "")));

                    int data;
                    do {
                        data = fis.read();
                        fos.write(data);
                    } while (data != -1);
                    fis.close();
                    fos.flush();
                    fos.close();

                } catch (final FileNotFoundException e) {
                    e.printStackTrace();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            if (pm != null) {
                pm.setProgress(++progressCnt);
            }
        }
    }


    public static final void renameBySequence(final OrderedDiskObjectList files, final String prefix, final int padding, final int radix, final String startAt, final int orderIndex, final ProgressMonitor pm) {

        if (pm != null) {
            pm.setMaximum(files.size());
        }
        int progressCnt = 0;
        int sequence = Integer.parseInt(startAt, radix);

        files.sort(orderIndex);
        for (final DiskObject f : files) {
            if (f.getFile().isFile()) {
                final int dot = f.getFile().getName().lastIndexOf(".");
                final String suffix = (dot > -1 ? f.getFile().getName().substring(dot) : "");
                final String seqStr = getNumericPaddedString(sequence++, radix, padding);

                final File nf = new File(f.getFile().getParent(), prefix + seqStr + (suffix.length() > 0 ? "." + suffix : ""));
                f.getFile().renameTo(nf);

            }
            if (pm != null) {
                pm.setProgress(++progressCnt);
            }
        }
    }


    public static final String getNumericPaddedString(final int value, final int radix, final int padding) {
        final String str = Integer.toString(value, radix);
        final StringBuffer strBuf = new StringBuffer();

        while ((padding > str.length()) && (strBuf.length() < (padding - str.length()))) {
            strBuf.append("0");
        }

        strBuf.append(str);
        return strBuf.toString();
    }

    public static final void deleteFile(final File file) {
        deleteFile(file, 0);
    }

    private static final void deleteFile(final File file, int count) {
        if (file.exists()) {
            if (count > 3) {
                file.deleteOnExit();
            } else if (!file.delete()) {
                System.gc();
                if (!file.delete()) {
                    try {
                        Thread.sleep(100);
                    } catch (final InterruptedException e) {
                    }
                    deleteFile(file, ++count);
                }
            }
        }
    }

    public static ImageIcon createImageIcon(String filePath) {
        try {
            return new ImageIcon(new File(filePath).toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void centerFrameOnScreen(Supplier<Integer> widthSupplier,
                                           Supplier<Integer> heightSupplier,
                                           Supplier<Dimension> screenSizeSupplier,
                                           BiConsumer<Integer, Integer> consumer) {
        Dimension screenSize = screenSizeSupplier.get();
        int x = screenSize.width / 2 - widthSupplier.get() / 2;
        int y = screenSize.height / 2 - heightSupplier.get() / 2;
        consumer.accept(x, y);
    }
}
