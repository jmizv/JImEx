package de.jmizv.jiexplorer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.filetask.RenameFileTask;
import de.jmizv.jiexplorer.util.DiskObject;



public class BatchRenameDialog extends JFrame {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BatchRenameDialog.class);

	private static final long serialVersionUID = 1L;

	public static final String[] RADIX = {"Decimal","Hexidecimal","Octal"};
	public static final String[] SORT_ORDER = {"Name","Type","Size","Date"};
	public static final String[] DATE_FORMAT = {"yyyyMMddHHmmssSSS","yyyy-MMM-dd-HHmmssSSS"};

	public static final int[] RADIX_VALUES = {10,16,8};


	private JPanel jContentPane = null;
	private JTabbedPane jTabbedPane = null;
	private JPanel sequencePanel = null;
	private JPanel datePanel = null;
	private JLabel prefixLabel = null;
	private JTextField prefixTextField = null;
	private JLabel formatLabel = null;
	private JComboBox formatComboBox = null;
	private JLabel sprefixLabel = null;
	private JButton dOkButton = null;
	private JButton dCancelButton = null;
	private JTextField sprefixTextField = null;
	private JPanel buttonPanel = null;
	private JLabel sradixLabel = null;
	private JComboBox radixComboBox = null;
	private JLabel startAtLabel = null;
	private JTextField startAtTextField = null;
	private JLabel paddingLabel = null;
	private JTextField paddingTextField = null;
	private JLabel orderingLabel = null;
	private JComboBox orderingComboBox = null;


	/**
	 * This is the default constructor
	 */
	 public BatchRenameDialog() {
		super();
		initialize();
	}

	 /**
	  * This method initializes this
	  *
	  * @return void
	  */
	 private void initialize() {
		 this.setSize(203, 250);
		 this.setContentPane(getJContentPane());
		 this.setTitle("Batch Rename");
		 this.setIconImage(JIExplorer.smallIcon.getImage());

		 final Dimension screenSize = this.getToolkit().getScreenSize();
		 this.setLocation(screenSize.width/2 - this.getWidth()/2,
				 screenSize.height/2 - this.getHeight()/2);

		 this.setContentPane(getJContentPane());
	 }

	 /**
	  * This method initializes jContentPane
	  *
	  * @return javax.swing.JPanel
	  */
	 private JPanel getJContentPane() {
		 if (this.jContentPane == null) {
			 this.jContentPane = new JPanel();
			 this.jContentPane.setLayout(new BorderLayout());
			 this.jContentPane.add(getJTabbedPane(), BorderLayout.CENTER);
			 this.jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
		 }
		 return this.jContentPane;
	 }


	 /**
	  * This method initializes jTabbedPane
	  *
	  * @return javax.swing.JTabbedPane
	  */
	 private JTabbedPane getJTabbedPane() {
		 if (this.jTabbedPane == null) {
			 this.jTabbedPane = new JTabbedPane();
			 this.jTabbedPane.addTab("Sequence", null, getSequencePanel(), null);
			 this.jTabbedPane.addTab("Date", null, getDatePanel(), null);
		 }
		 return this.jTabbedPane;
	 }

	 /**
	  * This method initializes sequencePanel
	  *
	  * @return javax.swing.JPanel
	  */
	 private JPanel getSequencePanel() {
		 if (this.sequencePanel == null) {

			 final GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			 gridBagConstraints15.fill = GridBagConstraints.BOTH;
			 gridBagConstraints15.gridy = 4;
			 gridBagConstraints15.weightx = 0.4;
			 gridBagConstraints15.anchor = GridBagConstraints.WEST;
			 gridBagConstraints15.insets = new Insets(3, 5, 10, 10);
			 gridBagConstraints15.gridx = 1;

			 final GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			 gridBagConstraints14.gridx = 0;
			 gridBagConstraints14.anchor = GridBagConstraints.EAST;
			 gridBagConstraints14.weightx = 0.1;
			 gridBagConstraints14.insets = new Insets(3, 10, 10, 5);
			 gridBagConstraints14.gridy = 4;
			 this.orderingLabel = new JLabel();
			 this.orderingLabel.setText("Ordering:");
			 this.orderingLabel.setName("orderingLabel");

			 final GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			 gridBagConstraints13.fill = GridBagConstraints.BOTH;
			 gridBagConstraints13.gridy = 3;
			 gridBagConstraints13.weightx = 0.4;
			 gridBagConstraints13.anchor = GridBagConstraints.WEST;
			 gridBagConstraints13.insets = new Insets(3, 5, 3, 10);
			 gridBagConstraints13.gridx = 1;

			 final GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			 gridBagConstraints12.gridx = 0;
			 gridBagConstraints12.anchor = GridBagConstraints.EAST;
			 gridBagConstraints12.insets = new Insets(3, 10, 3, 5);
			 gridBagConstraints12.weightx = 0.1;
			 gridBagConstraints12.gridy = 3;
			 this.paddingLabel = new JLabel();
			 this.paddingLabel.setText("Padding:");
			 this.paddingLabel.setName("paddingLabel");

			 final GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			 gridBagConstraints11.fill = GridBagConstraints.BOTH;
			 gridBagConstraints11.gridy = 2;
			 gridBagConstraints11.weightx = 0.4;
			 gridBagConstraints11.insets = new Insets(3, 5, 3, 10);
			 gridBagConstraints11.gridx = 1;

			 final GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			 gridBagConstraints10.gridx = 0;
			 gridBagConstraints10.anchor = GridBagConstraints.EAST;
			 gridBagConstraints10.weightx = 0.1;
			 gridBagConstraints10.insets = new Insets(3, 10, 3, 5);
			 gridBagConstraints10.gridy = 2;
			 this.startAtLabel = new JLabel();
			 this.startAtLabel.setText("Start At:");
			 this.startAtLabel.setName("startAtLabel");

			 final GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			 gridBagConstraints9.fill = GridBagConstraints.BOTH;
			 gridBagConstraints9.gridy = 1;
			 gridBagConstraints9.weightx = 0.4;
			 gridBagConstraints9.anchor = GridBagConstraints.WEST;
			 gridBagConstraints9.insets = new Insets(3, 5, 3, 10);
			 gridBagConstraints9.gridx = 1;

			 final GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			 gridBagConstraints8.gridx = 0;
			 gridBagConstraints8.anchor = GridBagConstraints.EAST;
			 gridBagConstraints8.insets = new Insets(3, 10, 3, 5);
			 gridBagConstraints8.weightx = 0.1;
			 gridBagConstraints8.gridy = 1;
			 this.sradixLabel = new JLabel();
			 this.sradixLabel.setText("Radix:");
			 this.sradixLabel.setName("sradixLabel");

			 final GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			 gridBagConstraints6.fill = GridBagConstraints.BOTH;
			 gridBagConstraints6.gridy = 0;
			 gridBagConstraints6.weightx = 0.4;
			 gridBagConstraints6.anchor = GridBagConstraints.WEST;
			 gridBagConstraints6.insets = new Insets(10, 5, 3, 10);
			 gridBagConstraints6.gridx = 1;

			 final GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			 gridBagConstraints4.gridx = 0;
			 gridBagConstraints4.insets = new Insets(10, 10, 3, 5);
			 gridBagConstraints4.weightx = 0.1;
			 gridBagConstraints4.anchor = GridBagConstraints.EAST;
			 gridBagConstraints4.gridy = 0;

			 this.sprefixLabel = new JLabel();
			 this.sprefixLabel.setText("Prefix:");
			 this.sprefixLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			 this.sprefixLabel.setName("sprefixLabel");
			 this.sequencePanel = new JPanel();
			 this.sequencePanel.setLayout(new GridBagLayout());
			 this.sequencePanel.setName("Sequence");
			 this.sequencePanel.add(this.sprefixLabel, gridBagConstraints4);
			 this.sequencePanel.add(getSprefixTextField(), gridBagConstraints6);
			 this.sequencePanel.add(this.sradixLabel, gridBagConstraints8);
			 this.sequencePanel.add(getRadixComboBox(), gridBagConstraints9);
			 this.sequencePanel.add(this.startAtLabel, gridBagConstraints10);
			 this.sequencePanel.add(getStartAtTextField(), gridBagConstraints11);
			 this.sequencePanel.add(this.paddingLabel, gridBagConstraints12);
			 this.sequencePanel.add(getPaddingTextField(), gridBagConstraints13);
			 this.sequencePanel.add(this.orderingLabel, gridBagConstraints14);
			 this.sequencePanel.add(getOrderingComboBox(), gridBagConstraints15);
		 }
		 return this.sequencePanel;
	 }

	 /**
	  * This method initializes datePanel
	  *
	  * @return javax.swing.JPanel
	  */
	 private JPanel getDatePanel() {
		 if (this.datePanel == null) {

			 final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			 gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
			 gridBagConstraints3.gridy = 1;
			 gridBagConstraints3.weightx = 0.4;
			 gridBagConstraints3.anchor = GridBagConstraints.WEST;
			 gridBagConstraints3.insets = new Insets(3, 5, 3, 10);
			 gridBagConstraints3.gridx = 1;

			 final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			 gridBagConstraints2.gridx = 0;
			 gridBagConstraints2.insets = new Insets(3, 10, 3, 5);
			 gridBagConstraints2.weightx = 0.1;
			 gridBagConstraints2.anchor = GridBagConstraints.EAST;
			 gridBagConstraints2.gridy = 1;
			 this.formatLabel = new JLabel();
			 this.formatLabel.setText("Format:");
			 this.formatLabel.setHorizontalAlignment(SwingConstants.RIGHT);

			 final GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			 gridBagConstraints1.fill = GridBagConstraints.BOTH;
			 gridBagConstraints1.gridy = 0;
			 gridBagConstraints1.weightx = 0.4;
			 gridBagConstraints1.anchor = GridBagConstraints.WEST;
			 gridBagConstraints1.insets = new Insets(3, 5, 3, 10);
			 gridBagConstraints1.gridx = 1;
			 final GridBagConstraints gridBagConstraints = new GridBagConstraints();
			 gridBagConstraints.gridx = 0;
			 gridBagConstraints.insets = new Insets(3, 10, 3, 5);
			 gridBagConstraints.weightx = 0.1;
			 gridBagConstraints.anchor = GridBagConstraints.EAST;
			 gridBagConstraints.gridy = 0;
			 this.prefixLabel = new JLabel();
			 this.prefixLabel.setText("Prefix:");
			 this.prefixLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			 this.datePanel = new JPanel();
			 this.datePanel.setLayout(new GridBagLayout());
			 this.datePanel.setName("Date");
			 this.datePanel.add(this.prefixLabel, gridBagConstraints);
			 this.datePanel.add(getPrefixTextField(), gridBagConstraints1);
			 this.datePanel.add(this.formatLabel, gridBagConstraints2);
			 this.datePanel.add(getFormatComboBox(), gridBagConstraints3);
		 }
		 return this.datePanel;
	 }

	 /**
	  * This method initializes prefixTextField
	  *
	  * @return javax.swing.JTextField
	  */
	 private JTextField getPrefixTextField() {
		 if (this.prefixTextField == null) {
			 this.prefixTextField = new JTextField();
			 this.prefixTextField.setName("prefixTextField");
			 this.prefixTextField.setText("");
			 this.prefixTextField.setColumns(50);
		 }
		 return this.prefixTextField;
	 }

	 /**
	  * This method initializes formatComboBox
	  *
	  * @return javax.swing.JComboBox
	  */
	 private JComboBox getFormatComboBox() {
		 if (this.formatComboBox == null) {
			this.formatComboBox = new JComboBox(DATE_FORMAT);
		}
		 return this.formatComboBox;
	 }

	 /**
	  * This method initializes dOkButton
	  *
	  * @return javax.swing.JButton
	  */
	 private JButton getDOkButton() {
		 if (this.dOkButton == null) {
			 this.dOkButton = new JButton();
			 this.dOkButton.setName("Ok");
			 this.dOkButton.setPreferredSize(new Dimension(73, 26));
			 this.dOkButton.setText("Ok");

			 this.dOkButton.addActionListener(new ActionListener(){
				 public void actionPerformed(final ActionEvent ae) {
					 final DiskObject[] dObjs = JIExplorer.instance().getContext().getSelectedDiskObjects();
					 final Vector<File> vec = new Vector<File>();
					 final File parent = dObjs[0].getFile().getParentFile();

					 for (final DiskObject dObj : dObjs) {
						vec.add(dObj.getFile());
					}

					 final RenameFileTask renameTask = new RenameFileTask(vec, parent);
					 switch (BatchRenameDialog.this.jTabbedPane.getSelectedIndex()) {
					 case 0:
						renameTask.setPrefix(getSprefixTextField().getText());
						log.debug("PrefixTextField: "+getSprefixTextField().getText());
						renameTask.setPadding(Integer.valueOf(getPaddingTextField().getText()));
						log.debug("PaddingTextField: "+getPaddingTextField().getText());
						renameTask.setStartAt(getStartAtTextField().getText());
						log.debug("StartAtTextField: "+getStartAtTextField().getText());
						renameTask.setRadix(RADIX_VALUES[getRadixComboBox().getSelectedIndex()]);
						log.debug("RadixComboBox: "+RADIX_VALUES[getRadixComboBox().getSelectedIndex()]);
						renameTask.setBySequence(true);
						break;
					 case 1:
						renameTask.setPrefix(getPrefixTextField().getText());
						log.debug("PrefixTextField: "+getPrefixTextField().getText());
						renameTask.setFormatString(DATE_FORMAT[getFormatComboBox().getSelectedIndex()]);
						log.debug("FormatComboBox: "+getFormatComboBox().getSelectedIndex());
						renameTask.setBySequence(false);
						break;
					 default:
						break;
					 }

					 JIExplorer.instance().runAction(renameTask);
					 BatchRenameDialog.this.dispose();
				 }
			 });
		 }
		 return this.dOkButton;
	 }

	 /**
	  * This method initializes dCancelButton
	  *
	  * @return javax.swing.JButton
	  */
	 private JButton getDCancelButton() {
		 if (this.dCancelButton == null) {
			 this.dCancelButton = new JButton();
			 this.dCancelButton.setName("Cancel");
			 this.dCancelButton.setText("Cancel");

			 this.dCancelButton.addActionListener(new ActionListener(){
				 public void actionPerformed(final ActionEvent ae) {
					 BatchRenameDialog.this.dispose();
				 }
			 });
		 }
		 return this.dCancelButton;
	 }

	 /**
	  * This method initializes sprefixTextField
	  *
	  * @return javax.swing.JTextField
	  */
	 private JTextField getSprefixTextField() {
		 if (this.sprefixTextField == null) {
			 this.sprefixTextField = new JTextField();
			 this.sprefixTextField.setName("sprefixTextField");
		 }
		 return this.sprefixTextField;
	 }

	 /**
	  * This method initializes buttonPanel
	  *
	  * @return javax.swing.JPanel
	  */
	 private JPanel getButtonPanel() {
		 if (this.buttonPanel == null) {
			 final GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			 gridBagConstraints5.insets = new Insets(20, 0, 10, 0);
			 gridBagConstraints5.gridy = -1;
			 gridBagConstraints5.weightx = 0.1;
			 gridBagConstraints5.gridx = -1;

			 final GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			 gridBagConstraints7.insets = new Insets(20, 0, 10, 0);
			 gridBagConstraints7.gridy = -1;
			 gridBagConstraints7.weightx = 0.1;
			 gridBagConstraints7.gridx = -1;
			 this.buttonPanel = new JPanel();
			 this.buttonPanel.setLayout(new GridBagLayout());
			 this.buttonPanel.add(getDOkButton(), gridBagConstraints7);
			 this.buttonPanel.add(getDCancelButton(), gridBagConstraints5);
		 }
		 return this.buttonPanel;
	 }


	 /**
	  * This method initializes radixComboBox
	  *
	  * @return javax.swing.JComboBox
	  */
	 private JComboBox getRadixComboBox() {
		 if (this.radixComboBox == null) {
			 this.radixComboBox = new JComboBox(RADIX);
			 this.radixComboBox.setName("radixComboBox");
			 this.radixComboBox.setSelectedIndex(0);
		 }
		 return this.radixComboBox;
	 }

	 /**
	  * This method initializes startAtTextField
	  *
	  * @return javax.swing.JTextField
	  */
	 private JTextField getStartAtTextField() {
		 if (this.startAtTextField == null) {
			 this.startAtTextField = new JTextField();
			 this.startAtTextField.setName("startAtTextField");
			 this.startAtTextField.setText("0");
		 }
		 return this.startAtTextField;
	 }

	 /**
	  * This method initializes paddingTextField
	  *
	  * @return javax.swing.JTextField
	  */
	 private JTextField getPaddingTextField() {
		 if (this.paddingTextField == null) {
			 this.paddingTextField = new JTextField();
			 this.paddingTextField.setName("paddingTextField");
			 this.paddingTextField.setText("5");
		 }
		 return this.paddingTextField;
	 }

	 /**
	  * This method initializes orderingComboBox
	  *
	  * @return javax.swing.JComboBox
	  */
	 private JComboBox getOrderingComboBox() {
		 if (this.orderingComboBox == null) {
			 this.orderingComboBox = new JComboBox(SORT_ORDER);
			 this.orderingComboBox.setName("orderingComboBox");
			 this.orderingComboBox.setSelectedIndex(0);
		 }
		 return this.orderingComboBox;
	 }

}  //  @jve:decl-index=0:visual-constraint="10,10"