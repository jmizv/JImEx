package de.jmizv.jiexplorer.gui.cattree;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.db.JIThumbnailService;
import de.jmizv.jiexplorer.util.DiskObject;



public final class JICatTreeFrame extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 8829469399676403287L;
	private JCheckBox appendCheckBox;
	private final JICatTree catTree;
	private final JPanel saveCancelPane;
	private final JButton btnClose;
	private final JButton btnSave;
	private final JButton btnRemove;

	private final BorderLayout borderLayout;

	public JICatTreeFrame() {
		super();
		setIconImage(JIExplorer.smallIcon.getImage());
		setTitle("Categories");

		this.catTree = new JICatTree();
		this.catTree.setEditable(true);
		this.saveCancelPane = new JPanel();
		final GridBagLayout saveCancelPaneLayout = new GridBagLayout();
		saveCancelPaneLayout.rowWeights = new double[] {0.1};
		saveCancelPaneLayout.rowHeights = new int[] {7};
		saveCancelPaneLayout.columnWeights = new double[] {0.1, 0.1, 0.1, 0.1};
		saveCancelPaneLayout.columnWidths = new int[] {7, 7, 7, 7};
		this.saveCancelPane.setLayout(saveCancelPaneLayout);
		this.btnClose = new JButton();
		this.btnSave = new JButton();
		this.btnRemove = new JButton();
		this.borderLayout = new BorderLayout();

		final int index = JIExplorer.instance().getContext().getLastSelectedDiskObjectIndex();
		final DiskObject obj = JIExplorer.instance().getContext().getSelectedDiskObjects()[index];

		final File imageFile = obj.getFile();
		if ((imageFile != null) && imageFile.exists()) {
			setTitle(imageFile.getName());
		} else {
			setTitle("Categories");
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				dispose();
			}
		});

		initComponents();
		setLocationRelativeTo(getParent());
		setVisible(true);
	}

	private DiskObject[] getImages() {
		final DiskObject[] objs = JIExplorer.instance().getContext().getSelectedDiskObjects();
//		final File[] imageFiles = new File[objs.length];
//		for (int i = 0; i < objs.length; i++)
//			imageFiles[i] = ((DiskObject) objs[i]).getFile();
		return objs;
	}

	public void setTitle() {
		final int index = JIExplorer.instance().getContext().getLastSelectedDiskObjectIndex();
		final DiskObject obj = JIExplorer.instance().getContext().getSelectedDiskObjects()[index];
		setTitle(obj.getName());
	}

	public void initComponents() {
		this.btnRemove.setText("Remove");
		this.btnRemove.setActionCommand("rmoveImagesInCategory");
		this.btnRemove.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				butRemoveActionPerformed(evt);
			}
		});

		this.saveCancelPane.add(this.btnRemove, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		this.btnSave.setText("Save");
		this.btnSave.setActionCommand("saveImagesInCategory");
		this.btnSave.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				butSaveActionPerformed(evt);
			}
		});

		this.saveCancelPane.add(this.btnSave, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		this.btnClose.setText("Close");
		this.btnClose.setActionCommand("cancelKeyWords");
		this.btnClose.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				butCancelActionPerformed(evt);
			}
		});

		this.saveCancelPane.add(this.btnClose, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		{
			this.appendCheckBox = new JCheckBox();
			this.appendCheckBox.setSelected(true);
			this.saveCancelPane.add(this.appendCheckBox, new GridBagConstraints(3, 0, 1, 1, 0.2, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.appendCheckBox.setText("Append");
		}

		getContentPane().setLayout(this.borderLayout);
		getContentPane().add(new JScrollPane(this.catTree), BorderLayout.CENTER);
		getContentPane().add(this.saveCancelPane, BorderLayout.SOUTH);

		setSize(new java.awt.Dimension(369, 340));
	}

	private void butRemoveActionPerformed(final java.awt.event.ActionEvent evt) {
		final Vector<JICatTreeNode> nodes = new Vector<JICatTreeNode>();
		for (final Object obj : this.catTree.getSelectedNodes()) {
			nodes.add((JICatTreeNode)obj);
		}
		JIThumbnailService.getInstance().removeImagesForCategory(getImages(), nodes);
	}

	private void butSaveActionPerformed(final java.awt.event.ActionEvent evt) {
		final Vector<JICatTreeNode> nodes = new Vector<JICatTreeNode>();
		for (final Object obj : this.catTree.getSelectedNodes()) {
			nodes.add((JICatTreeNode)obj);
		}
		JIThumbnailService.getInstance().insertImagesInCategory(getImages(), nodes, this.appendCheckBox.isSelected());
	}

	private void butCancelActionPerformed(final java.awt.event.ActionEvent evt) {
		dispose();
	}
}
