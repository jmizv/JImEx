package de.jmizv.jiexplorer.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.gui.preferences.JIPreferences;
import de.jmizv.jiexplorer.gui.tree.JITreeNode;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIObservable;
import de.jmizv.jiexplorer.util.JIObserver;


public class ButtonBar extends JPanel implements JIObserver {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ButtonBar.class);
	private static final String[] SORT_ORDER_LIST = {"Name", "Type", "Size", "Date", "Path"};

	private static final long serialVersionUID = 1L;
	private JLabel addressLabel;
	private JTextField addressTextField;
	private JLabel workBenchLabel;
	private JLabel sortOrderLabel;
	private JComboBox sortOrderComboBox;
	private JCheckBox detailViewCheckBox;
	private JCheckBox thumbnailViewCheckBox;
	private JCheckBox previewCheckBox;

	private ButtonGroup viewButtonGroup ;  //  @jve:decl-index=0:

	private JIExplorer jiexplorer;

	/**
	 * This is the default constructor
	 */
	public ButtonBar(final JIExplorer jiexplorer) {
		super();
		this.jiexplorer = jiexplorer;
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		final GridBagConstraints sortOrderComboBoxGC = new GridBagConstraints();
		sortOrderComboBoxGC.fill = GridBagConstraints.VERTICAL;
		sortOrderComboBoxGC.gridy = 0;
		sortOrderComboBoxGC.weightx = 0.1;
		sortOrderComboBoxGC.insets = new Insets(3, 8, 3, 6);
		sortOrderComboBoxGC.anchor = GridBagConstraints.WEST;
		sortOrderComboBoxGC.gridx = 5;

		final GridBagConstraints workBenchLabelGC = new GridBagConstraints();
		workBenchLabelGC.gridx = 2;
		workBenchLabelGC.insets = new Insets(3, 10, 3, 3);
		workBenchLabelGC.weightx = 0.1;
		workBenchLabelGC.anchor = GridBagConstraints.EAST;
		workBenchLabelGC.gridy = 0;
		this.workBenchLabel = new JLabel();
		this.workBenchLabel.setText("Work Bench");

		final GridBagConstraints workbenchComboBoxGC = new GridBagConstraints();
		workbenchComboBoxGC.fill = GridBagConstraints.VERTICAL;
		workbenchComboBoxGC.gridy = 0;
		workbenchComboBoxGC.weightx = 0.1;
		workbenchComboBoxGC.anchor = GridBagConstraints.WEST;
		workbenchComboBoxGC.insets = new Insets(3, 8, 3, 6);
		workbenchComboBoxGC.gridx = 3;

		final GridBagConstraints detailViewCheckBoxGC = new GridBagConstraints();
		detailViewCheckBoxGC.gridx = 6;
		detailViewCheckBoxGC.gridy = 0;

		final GridBagConstraints thumbnailViewCheckBoxGC = new GridBagConstraints();
		thumbnailViewCheckBoxGC.gridx = 7;
		thumbnailViewCheckBoxGC.gridy = 0;

		final GridBagConstraints previewCheckBoxGC = new GridBagConstraints();
		previewCheckBoxGC.gridx = 8;
		previewCheckBoxGC.gridy = 0;

		final GridBagConstraints sortOrderLabelGC = new GridBagConstraints();
		sortOrderLabelGC.weightx = 0.1;
		sortOrderLabelGC.insets = new Insets(3, 3, 3, 3);
		sortOrderLabelGC.gridx = 4;
		sortOrderLabelGC.gridy = 0;
		sortOrderLabelGC.anchor = GridBagConstraints.EAST;


		this.sortOrderLabel = new JLabel();
		this.sortOrderLabel.setText("Sort Order");
		final GridBagConstraints addressTextFieldGC = new GridBagConstraints();
		addressTextFieldGC.fill = GridBagConstraints.BOTH;
		addressTextFieldGC.gridy = 0;
		addressTextFieldGC.weightx = 3.0;
		addressTextFieldGC.insets = new Insets(3, 2, 3, 2);
		addressTextFieldGC.gridx = 1;

		final GridBagConstraints addressLabelGC = new GridBagConstraints();
		addressLabelGC.gridx = 0;
		addressLabelGC.weightx = 0.0;
		addressLabelGC.insets = new Insets(3, 5, 3, 3);
		addressLabelGC.gridy = 0;
		this.addressLabel = new JLabel();
		this.addressLabel.setText("Address:");
		this.setSize(626, 30);
		this.setLayout(new GridBagLayout());
		this.add(this.addressLabel, addressLabelGC);
		this.add(getAddressTextField(), addressTextFieldGC);
		//this.add(workBenchLabel, workBenchLabelGC);
		//this.add(getWorkbenchComboBox(), workbenchComboBoxGC);
		this.add(this.sortOrderLabel, sortOrderLabelGC);
		this.add(getSortOrderComboBox(), sortOrderComboBoxGC);
		this.add(getDetailViewCheckBox(), detailViewCheckBoxGC);
		this.add(getThumbnailViewCheckBox(), thumbnailViewCheckBoxGC);
		this.add(getPreviewCheckBox(), previewCheckBoxGC);
		getViewButtonGroup();
	}

	private JTextField getAddressTextField() {
		if (this.addressTextField == null) {
			this.addressTextField = new JTextField();
			this.addressTextField.setPreferredSize(new Dimension(150, 16));
		}
		return this.addressTextField;
	}

	private static ImageIcon createImageIcon(String resourceFileLocation) {
		try {
			return new ImageIcon(new File(resourceFileLocation).toURI().toURL());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private JCheckBox getDetailViewCheckBox() {
		if (this.detailViewCheckBox == null) {
			this.detailViewCheckBox = new JCheckBox();
			this.detailViewCheckBox.setDisabledIcon(createImageIcon("/icons/detail16a.png"));
			this.detailViewCheckBox.setName("detailViewCheckBox");
			this.detailViewCheckBox.setIcon(createImageIcon("/icons/detail16a.png"));
			this.detailViewCheckBox.setSelected(JIPreferences.getInstance().getImagesViewType().equals(JIPreferences.DETAIL_VIEW));
			this.detailViewCheckBox.setSelectedIcon(createImageIcon("/icons/detail16.png"));
			this.detailViewCheckBox.addActionListener(e -> {
                JIPreferences.getInstance().setImagesViewType(JIPreferences.DETAIL_VIEW);
                JIExplorer.instance().prepareFileList();
            });
		}
		return this.detailViewCheckBox;
	}

	private JCheckBox getThumbnailViewCheckBox() {
		if (this.thumbnailViewCheckBox == null) {
			this.thumbnailViewCheckBox = new JCheckBox();
			this.thumbnailViewCheckBox.setName("thumbnailViewCheckBox");
			this.thumbnailViewCheckBox.setIcon(createImageIcon("/icons/thumbnail16a.png"));
			this.thumbnailViewCheckBox.setSelectedIcon(createImageIcon("/icons/thumbnail16.png"));
			this.thumbnailViewCheckBox.setSelected(JIPreferences.getInstance().getImagesViewType().equals(JIPreferences.THUMB_VIEW));
			this.thumbnailViewCheckBox.addActionListener(e -> {
                JIPreferences.getInstance().setImagesViewType(JIPreferences.THUMB_VIEW);
                JIExplorer.instance().prepareFileList();
            });
		}
		return this.thumbnailViewCheckBox;
	}

	private JCheckBox getPreviewCheckBox() {
		if (this.previewCheckBox == null) {
			this.previewCheckBox = new JCheckBox();
			this.previewCheckBox.setIcon(createImageIcon("/icons/preview16a.png"));
			this.previewCheckBox.setName("previewCheckBox");
			this.previewCheckBox.setSelectedIcon(createImageIcon("/icons/preview16.png"));
			this.previewCheckBox.setSelected(JIPreferences.getInstance().getImagesViewType().equals(JIPreferences.PREVIEW_VIEW));
			this.previewCheckBox.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					JIPreferences.getInstance().setImagesViewType(JIPreferences.PREVIEW_VIEW);
					JIExplorer.instance().prepareFileList();
				}
			});
		}
		return this.previewCheckBox;
	}

	private ButtonGroup getViewButtonGroup() {
		if (this.viewButtonGroup == null) {
			this.viewButtonGroup = new ButtonGroup();
			this.viewButtonGroup.add(getPreviewCheckBox());
			this.viewButtonGroup.add(getThumbnailViewCheckBox());
			this.viewButtonGroup.add(getDetailViewCheckBox());
		}
		return this.viewButtonGroup;
	}

	public void update(final JIObservable o, final Object obj) {
		if (obj instanceof String ) {
			if(((String)obj).equals(JIObservable.SECTION_CHANGED)) {
				final DiskObject[] list_selection = JIExplorer.instance().getContext().getSelectedDiskObjects();
				if ((list_selection != null) && (list_selection.length > 0)) {

					final File file = (list_selection[0]).getFile();
					if (file != null) {
						log.debug("JIObservable.SECTION_CHANGED file = "+file.getPath());
						this.addressTextField.setText(file.getPath());
					}
				}
			} else if (((String)obj).equals(JIObservable.DIRECTORY_LOADING)) {
				final Vector<JITreeNode> list_selection = JIExplorer.instance().getContext().getSelectedDirNodes();
				final int index = JIExplorer.instance().getContext().getLastSelectedDirNodesIndex();
				if ((list_selection != null) && (!list_selection.isEmpty())) {
					if (list_selection.elementAt(index) != null){
						log.debug("JIObservable.DIRECTORY_CHANGED dir = "+list_selection.elementAt(index).getUserObject());
						this.addressTextField.setText(((DiskObject)list_selection.elementAt(index).getUserObject()).getPath());
					}
				}
			}
		}
	}

	private JComboBox getSortOrderComboBox() {
		if (this.sortOrderComboBox == null) {
			this.sortOrderComboBox = new JComboBox();
			this.sortOrderComboBox.setPreferredSize(new Dimension(80, 18));
			this.sortOrderComboBox.insertItemAt(SORT_ORDER_LIST[0], 0);
			this.sortOrderComboBox.insertItemAt(SORT_ORDER_LIST[1], 1);
			this.sortOrderComboBox.insertItemAt(SORT_ORDER_LIST[2], 2);
			this.sortOrderComboBox.insertItemAt(SORT_ORDER_LIST[3], 3);
			this.sortOrderComboBox.insertItemAt(SORT_ORDER_LIST[4], 4);
			this.sortOrderComboBox.setSelectedIndex(JIPreferences.getInstance().getThumbnailSortOrder());
			this.sortOrderComboBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent e) {
                    JIPreferences.getInstance().setThumbnailSortOrder(ButtonBar.this.sortOrderComboBox.getSelectedIndex());
                    ButtonBar.this.jiexplorer.sortFileList();
				}
			});
		}
		return this.sortOrderComboBox;
	}
}
