package de.jmizv.jiexplorer.gui;

import com.drew.metadata.exif.ExifDirectoryBase;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFMetadataSupport;

import java.awt.BorderLayout;
import java.awt.Cursor;

import javax.imageio.metadata.IIOMetadata;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.gui.metadata.JIImagedataTableModel;
import de.jmizv.jiexplorer.gui.metadata.JIMetadataPanel;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIObservable;
import de.jmizv.jiexplorer.util.JIUtility;

public class DiskObjectTabInfo extends JTabbedPane implements DiskObjectDisplay  {

	/**
	 *
	 */
	private static final long serialVersionUID = -706920756504199734L;
	private final JPanel infoPanel;
	private final JIMetadataPanel exifPanel;
	private final JIMetadataPanel iptcPanel;
	private final JIMetadataPanel jpegPanel;
	private final JImagePanel previewPanel;
	private JTable imageInfoTable;

	public DiskObjectTabInfo() {
		super();
		this.infoPanel = new JPanel();
		this.exifPanel = new JIMetadataPanel();
		this.iptcPanel = new JIMetadataPanel();
		this.jpegPanel = new JIMetadataPanel();
		this.previewPanel = new JImagePanel(true);

		addTab("General", null, this.infoPanel, null);
		addTab("Exif", null, this.exifPanel, null);
		addTab("Iptc", null, this.iptcPanel, null);
		addTab("Jpeg", null, this.jpegPanel, null);
		addTab("Preview", null, this.previewPanel, null);

		setVisible(true);
	}

	public void resize() {
		final int divSize = ((JSplitPane) getParent()).getDividerSize();
		final int divLoc = ((JSplitPane) getParent()).getDividerLocation();
		setPreferredSize(new java.awt.Dimension(getParent().getWidth(), getParent().getHeight()-(divLoc+divSize)));
	}

	public void display(final DiskObject dObj) {

		resize();

		this.infoPanel.removeAll();
		this.infoPanel.setLayout(new BorderLayout());

		this.imageInfoTable = new JTable(new JIImagedataTableModel(dObj));
		this.imageInfoTable.getColumnModel().getColumn(0).setPreferredWidth(65);
		this.infoPanel.add(new JScrollPane(this.imageInfoTable),BorderLayout.CENTER);

		if (dObj.getFile() != null) {
			if (dObj.getName().toLowerCase().endsWith(".jpg")) {
				try {
					final Metadata metadata = JpegMetadataReader.readMetadata(dObj.getFile());
					Directory directory = metadata.getFirstDirectoryOfType(ExifDirectoryBase.class);

					if (directory != null) {
						this.exifPanel.displayMetadata(directory);
						this.setTitleAt(1, "Exif");
					}

					directory = metadata.getFirstDirectoryOfType(IptcDirectory.class);

					if (directory != null) {
						this.iptcPanel.displayMetadata(directory);
						this.setTitleAt(2, "Iptc");
					}

					directory = metadata.getFirstDirectoryOfType(JpegDirectory.class);

					if (directory != null) {
						this.jpegPanel.displayMetadata(directory);
						this.setTitleAt(3, "Jpeg");
					}
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			} else {
				try {
					final IIOMetadata iioMetadata = JIUtility.getImageReader(dObj).getImageMetadata(0);
					if (iioMetadata instanceof TIFFMetadataSupport) {
						IFD ifd = ((TIFFMetadataSupport)iioMetadata).getExifIFD();

						if (ifd != null) {
							this.exifPanel.displayMetadata(ifd);
							this.setTitleAt(1, "Exif");
						}

						ifd = ((TIFFMetadataSupport)iioMetadata).getPrimaryIFD();

						if (ifd != null) {
							this.iptcPanel.displayMetadata(ifd);
							this.setTitleAt(2, "IFD");
						}

						ifd = ((TIFFMetadataSupport)iioMetadata).getRasterIFD();

						if (ifd != null) {
							this.jpegPanel.displayMetadata(ifd);
							this.setTitleAt(3, "RasterIFD");
						}
					} else {
						this.exifPanel.displayMetadata(new IFD());
						this.iptcPanel.displayMetadata(new IFD());
						this.jpegPanel.displayMetadata(new IFD());
					}
				} catch (final Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace(System.err);
				}
			}
		}
		this.previewPanel.display(dObj);
	}

	public void update(final JIObservable o, final Object arg) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		final DiskObject[] list_selection = JIExplorer.instance().getContext().getSelectedDiskObjects();
		final int index = JIExplorer.instance().getContext().getLastSelectedDiskObjectIndex() > -1?JIExplorer.instance().getContext().getLastSelectedDiskObjectIndex():0;

		if ((list_selection != null) && (list_selection.length > index)) {
			display(list_selection[index]);
		}

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
