/*
 * JIPrefFrame.java
 *
 * Created on March 29, 2005, 7:27 AM
 */

package de.jmizv.jiexplorer.gui.preferences;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JTabbedPane;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.db.JIThumbnailService;



public class JIPrefFrame extends javax.swing.JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = -5328057899112993025L;
	private javax.swing.JButton okButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JPanel buttonPanel;
	private javax.swing.JTabbedPane jTabbedPane1;

	private ThumbnailForm thumbnailTab;
	private ViewerForm viewerTab;
	private OpenWithForm openWithTab;
	private JDBCForm dbConnectionsTab;

	/** Creates new form PrefFrame */
	public JIPrefFrame() {
		super();
		setIconImage(JIExplorer.smallIcon.getImage());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				dispose();
			}
		});

		initComponents();

		setSize(525, 435);

		final Dimension screenSize = this.getToolkit().getScreenSize();
		this.setLocation(screenSize.width/2 - this.getWidth()/2,
				screenSize.height/2 - this.getHeight()/2);

		setVisible(true);
	}

	public JIPrefFrame(final JIExplorer jiexplorer) {
		super();
		setIconImage(JIExplorer.smallIcon.getImage());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				dispose();
			}
		});

		initComponents();

		setSize(515, 415);
		final Dimension screenSize = this.getToolkit().getScreenSize();
		this.setLocation(screenSize.width/2 - this.getWidth()/2,
				screenSize.height/2 - this.getHeight()/2);
		setVisible(true);
	}


	private void initComponents() {

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setFont(new java.awt.Font("Arial", 0, 11));
		setMaximizedBounds(new java.awt.Rectangle(0, 0, 515, 415));


		getContentPane().setLayout(new BorderLayout());

		this.buttonPanel = new javax.swing.JPanel();
		this.okButton = new javax.swing.JButton();
		this.cancelButton = new javax.swing.JButton();

		this.jTabbedPane1 = new JTabbedPane();
		this.viewerTab = new ViewerForm();
		this.thumbnailTab = new ThumbnailForm();
		this.openWithTab = new OpenWithForm(this);
		this.dbConnectionsTab = new JDBCForm();

		this.jTabbedPane1.addTab("Viewer", this.viewerTab);
		this.jTabbedPane1.addTab("Thumbnails", this.thumbnailTab);
		this.jTabbedPane1.addTab("Open With", this.openWithTab);
		this.jTabbedPane1.addTab("Database", this.dbConnectionsTab);
		getContentPane().add(this.jTabbedPane1, BorderLayout.CENTER);


		this.thumbnailTab.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentShown(final java.awt.event.ComponentEvent evt) {
				//thumbnailTabComponentShown(evt);
			}
		});


		this.okButton.setText("OK");
		this.okButton.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(final java.awt.event.MouseEvent evt) {
				jButton1MouseClicked(evt);
			}
		});

		this.buttonPanel.add(this.okButton);

		this.cancelButton.setText("Cancel");
		this.cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(final java.awt.event.MouseEvent evt) {
				jButton2MouseClicked(evt);
			}
		});

		this.buttonPanel.add(this.cancelButton);

		getContentPane().add(this.buttonPanel, BorderLayout.SOUTH);
		this.buttonPanel.setBounds(0, 350, 500, 40);

		pack();
	}


	private void jButton1MouseClicked(final java.awt.event.MouseEvent evt) {
		savePreferences();
		this.dispose();
	}

	private void jButton2MouseClicked(final java.awt.event.MouseEvent evt) {
		this.dispose();
	}

	/**
	 * @param args the command line arguments
	 */

	private void savePreferences() {
		boolean dbChanged = false;
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		JIPreferences.getInstance().setSlideShowDirection(this.viewerTab.getSlideShowDirection());


		if (!this.dbConnectionsTab.getDatabaseType().equals(JIPreferences.getInstance().getDatabaseType())||
				this.dbConnectionsTab.updated	){
			JIPreferences.getInstance().setDatabaseType(this.dbConnectionsTab.getDatabaseType());
			JIPreferences.getInstance().setJDBCDriver(this.dbConnectionsTab.getJDBCDriver());
			JIPreferences.getInstance().setJDBCURL(this.dbConnectionsTab.getJDBCURL());
			JIPreferences.getInstance().setJDBCUserName(this.dbConnectionsTab.getJBDCUserName());
			JIPreferences.getInstance().setJDBCPassword(this.dbConnectionsTab.getJDBCPassword());
			dbChanged = true;
		}


		JIPreferences.getInstance().setImageViewerCenterWin(this.viewerTab.isImageViewerCenterWin());
		JIPreferences.getInstance().setIMAGE_VIEWER_FULL_SCREEN(this.viewerTab.isImageViewerFullScreenHideMouse());
		JIPreferences.getInstance().setImageViewerFullScreenHideMouse(this.viewerTab.isImageViewerFullScreenHideMouse());
		JIPreferences.getInstance().setImageViewerZoomFitWin(this.viewerTab.isImageViewerZoomFitWin());
		JIPreferences.getInstance().setImageViewerOnTop(this.viewerTab.isImageViewerOnTop());
		JIPreferences.getInstance().setImageViewerSaveWinPos(this.viewerTab.isImageViewerSaveWinPos());
		JIPreferences.getInstance().setImageViewerShowFullPath(this.viewerTab.isImageViewerShowFullPath());
		JIPreferences.getInstance().setImageViewerWinFitScreen(this.viewerTab.isImageViewerWinFitScreen());
		JIPreferences.getInstance().setSlideShowDelay(this.viewerTab.getSlideShowDelay());
		JIPreferences.getInstance().setIconDim(this.thumbnailTab.getIDim().width,this.thumbnailTab.getIDim().height);
		JIPreferences.getInstance().setThumbnailToolTips(this.thumbnailTab.isTToolTips());
		JIPreferences.getInstance().setThumbnailSortDesend(this.thumbnailTab.isTSortDesend());
		JIPreferences.getInstance().setThumbnailScrollMode(this.thumbnailTab.getTScrollMode());
		JIPreferences.getInstance().setThumbnailSortOrder(this.thumbnailTab.getTSort());
		JIPreferences.getInstance().setShowDetailTableIcon(this.thumbnailTab.isShowDetailTableIcon());
		JIPreferences.getInstance().setLoadIconsRecursively(this.thumbnailTab.isLoadIconsRecursively());
		JIPreferences.getInstance().setLoadAllImageIcons(this.thumbnailTab.isLoadAllImageIcons());


		JIPreferences.getInstance().setOpenWith(this.openWithTab.getOpenWith());

		System.out.println(JIPreferences.getInstance().toString());
		JIExplorer.instance().resetPreferences();



		if (dbChanged) {
			JIThumbnailService.reload();
		}
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

}
