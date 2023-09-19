package de.jmizv.jiexplorer.gui.metadata;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.exif.ExifDirectoryBase;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.Serial;

import javax.imageio.metadata.IIOMetadata;
import javax.swing.*;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIUtility;

public class JIMetaDataFrame extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;
    private final DiskObject dObj;
    private JTabbedPane jTabbedPane1;
    private JPanel infoPanel;
    private JTable imageInfoTable;

    public JIMetaDataFrame() {
        super();
        this.setSize(400, 300);
        this.setIconImage(JIExplorer.smallIcon.getImage());
        this.dObj = JIExplorer.instance().getContext().getSelectedDiskObjects()[JIExplorer.instance().getContext().getLastSelectedDiskObjectIndex()];
        this.setTitle("MetaData [" + this.dObj.getName() + "]");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                dispose();
            }
        });
        initGUI();
        setLocationRelativeTo(getParent());
        setVisible(true);
    }

    private void initGUI() {
        try {
            this.jTabbedPane1 = new JTabbedPane();
            getContentPane().add(this.jTabbedPane1, BorderLayout.CENTER);

            this.infoPanel = new JPanel();
            this.infoPanel.setLayout(new BorderLayout());
            this.jTabbedPane1.addTab("General", null, this.infoPanel, null);

            this.imageInfoTable = new JTable(new JIImagedataTableModel(this.dObj));
            this.infoPanel.add(new JScrollPane(this.imageInfoTable), BorderLayout.CENTER);

            if (this.dObj.getFile() != null) {
                if (this.dObj.getName().toLowerCase().endsWith(".jpg")) {

                    final Metadata metadata = JpegMetadataReader.readMetadata(this.dObj.getFile());
                    Directory directory = metadata.getFirstDirectoryOfType(ExifDirectoryBase.class);

                    if (directory != null) {
                        final JPanel exifPanel = new JIMetadataPanel(directory);
                        this.jTabbedPane1.addTab("Exif", null, exifPanel, null);
                    }

                    directory = metadata.getFirstDirectoryOfType(IptcDirectory.class);

                    if (directory != null) {
                        final JPanel iptcPanel = new JIMetadataPanel(directory);
                        this.jTabbedPane1.addTab("Iptc", null, iptcPanel, null);
                    }

                    directory = metadata.getFirstDirectoryOfType(JpegDirectory.class);

                    if (directory != null) {
                        final JPanel jpegPanel = new JIMetadataPanel(directory);
                        this.jTabbedPane1.addTab("Jpeg", null, jpegPanel, null);
                    }
                } else {
                    final IIOMetadata iioMetadata = JIUtility.getImageReader(this.dObj).getImageMetadata(0);
                    IFD ifd = ((TIFFMetadataSupport) iioMetadata).getExifIFD();

                    if (ifd != null) {
                        final JPanel exifPanel = new JIMetadataPanel(ifd);
                        this.jTabbedPane1.addTab("Exif", null, exifPanel, null);
                    }

                    ifd = ((TIFFMetadataSupport) iioMetadata).getPrimaryIFD();

                    if (ifd != null) {
                        final JPanel exifPanel = new JIMetadataPanel(ifd);
                        this.jTabbedPane1.addTab("IFD", null, exifPanel, null);
                    }

                    ifd = ((TIFFMetadataSupport) iioMetadata).getRasterIFD();

                    if (ifd != null) {
                        final JPanel exifPanel = new JIMetadataPanel(ifd);
                        this.jTabbedPane1.addTab("RasterIFD", null, exifPanel, null);
                    }
                }
            }

        } catch (IOException | JpegProcessingException e) {
            throw new RuntimeException("Could not read file.", e);
        }
    }
}
