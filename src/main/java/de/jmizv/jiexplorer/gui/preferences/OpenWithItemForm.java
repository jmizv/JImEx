package de.jmizv.jiexplorer.gui.preferences;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;

import de.jmizv.jiexplorer.db.JIThumbnailService;
import de.jmizv.jiexplorer.gui.JDirectoryChooser;



public class OpenWithItemForm extends javax.swing.JDialog {
	/**
	 *
	 */
	private static final long serialVersionUID = -1327052717589092526L;
	public static final String ADD_ACTION = "ADD_ACTION";
	public static final String EDIT_ACTION = "EDIT_ACTION";

	public static final int SAVE = 1;
	public static final int CANCEL = -1;

	private JLabel namejLabel;
	private JLabel commandjLabel;
	private JTextField namejTextField;
	private JButton commandBrowsejButton;
	private JButton wdirBrowsejButton;
	private JButton canceljButton;
	private JButton savejButton;
	private JTextField wdirjTextField;
	private JTextField commandjTextField;
	private JLabel wdirjLabel;
	private String actionStr;

	private int result = 1;
	private int row = 0;
	private JTable table;

	/**
	* Auto-generated main method to display this JDialog
	*/
	public static void main(final String[] args) {
		final JFrame frame = new JFrame();
		final OpenWithItemForm inst = new OpenWithItemForm(frame);
		inst.setVisible(true);
	}


	public OpenWithItemForm(final Frame frame, final int row, final JTable table, final String action) {
		super(frame);
		this.row = row;
		this.table = table;
		this.actionStr = action;
		initGUI();
	}

	public OpenWithItemForm(final Dialog frame, final int row, final JTable table, final String action) {
		super(frame);
		this.row = row;
		this.table = table;
		this.actionStr = action;
		initGUI();
	}

	public OpenWithItemForm(final JFrame frame) {
		super(frame);
		initGUI();
	}

	private void initGUI() {
		try {
			final GridBagLayout thisLayout = new GridBagLayout();
			thisLayout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.6};
			thisLayout.rowHeights = new int[] {7, 7, 7, 7};
			thisLayout.columnWeights = new double[] {0.1, 0.1, 0.1, 0.1};
			thisLayout.columnWidths = new int[] {7, 7, 7, 7};
			getContentPane().setLayout(thisLayout);
			this.setTitle("Open With Editor");
			{
				this.namejLabel = new JLabel();
				getContentPane().add(this.namejLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
				this.namejLabel.setText("Name");
			}
			{
				this.commandjLabel = new JLabel();
				getContentPane().add(this.commandjLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
				this.commandjLabel.setText("Command");
			}
			{
				this.wdirjLabel = new JLabel();
				getContentPane().add(this.wdirjLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
				this.wdirjLabel.setText("Working Dir");
			}
			{
				final String str = (this.table.getValueAt(this.row, 0) != null?(String)this.table.getValueAt(this.row, 0):"");
				this.namejTextField = new JTextField(str);
				getContentPane().add(this.namejTextField, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			}
			{
				final String str = (this.table.getValueAt(this.row, 0) != null?(String)this.table.getValueAt(this.row, 1):"");
				this.commandjTextField = new JTextField(str);
				getContentPane().add(this.commandjTextField, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
			}
			{
				final String str = (this.table.getValueAt(this.row, 0) != null?(String)this.table.getValueAt(this.row, 2):"");
				this.wdirjTextField = new JTextField(str);
				getContentPane().add(this.wdirjTextField, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
				this.wdirjTextField.setEnabled(true);
				this.wdirjTextField.setEditable(true);
			}
			{
				this.savejButton = new JButton();
				getContentPane().add(
					this.savejButton,
					new GridBagConstraints(
						1,
						3,
						1,
						1,
						0.0,
						0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0),
						0,
						0));
				this.savejButton.setText("Save");
				this.savejButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(final MouseEvent evt) {
						System.out.println("savejButton.mouseClicked, event=" + evt);
						OpenWithItemForm.this.result = OpenWithItemForm.SAVE;
						OpenWithItemForm.this.table.setValueAt(OpenWithItemForm.this.namejTextField.getText(), OpenWithItemForm.this.row, 0);
						OpenWithItemForm.this.table.setValueAt(OpenWithItemForm.this.commandjTextField.getText(), OpenWithItemForm.this.row, 1);
						OpenWithItemForm.this.table.setValueAt(OpenWithItemForm.this.wdirjTextField.getText(), OpenWithItemForm.this.row, 2);

						if (OpenWithItemForm.this.actionStr.equals(ADD_ACTION)) {
							//JIThumbnailDB.getInstance().insertOpenWith(new OpenWith(namejTextField.getText(),commandjTextField.getText(),wdirjTextField.getText()));
							JIThumbnailService.getInstance().insertOpenWith(((OpenWithTableModel)OpenWithItemForm.this.table.getModel()).dataVector.elementAt(OpenWithItemForm.this.row));
						} else {
							JIThumbnailService.getInstance().updateOpenWith(((OpenWithTableModel)OpenWithItemForm.this.table.getModel()).dataVector.elementAt(OpenWithItemForm.this.row));
						}
						OpenWithItemForm.this.setVisible(false);
						OpenWithItemForm.this.dispose();
					}
				});
			}
			{
				this.canceljButton = new JButton();
				getContentPane().add(
					this.canceljButton,
					new GridBagConstraints(
						2,
						3,
						1,
						1,
						0.0,
						0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0),
						0,
						0));
				this.canceljButton.setText("Cancel");
				this.canceljButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(final MouseEvent evt) {
						System.out.println("canceljButton.mouseClicked, event=" + evt);
						OpenWithItemForm.this.result = OpenWithItemForm.CANCEL;
						OpenWithItemForm.this.setVisible(false);
						OpenWithItemForm.this.dispose();
					}
				});
			}
			{
				this.commandBrowsejButton = new JButton();
				getContentPane().add(
					this.commandBrowsejButton,
					new GridBagConstraints(
						3,
						1,
						1,
						1,
						0.0,
						0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0),
						0,
						0));
				this.commandBrowsejButton.setText("Browse");
				this.commandBrowsejButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(final MouseEvent evt) {
						System.out.println("commandBrowsejButton.mouseClicked, event=" + evt);
						final JFileChooser chooser = new JFileChooser();
						final int returnVal = chooser.showOpenDialog(OpenWithItemForm.this);
					    if(returnVal == JFileChooser.APPROVE_OPTION) {
					       System.out.println("You chose to open this file: " +
					            chooser.getSelectedFile().getPath());
					       OpenWithItemForm.this.commandjTextField.setText(chooser.getSelectedFile().getPath());
					    }
					}
				});
			}
			{
				this.wdirBrowsejButton = new JButton();
				getContentPane().add(
					this.wdirBrowsejButton,
					new GridBagConstraints(
						3,
						2,
						1,
						1,
						0.0,
						0.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0),
						0,
						0));
				this.wdirBrowsejButton.setText("Browse");
				this.wdirBrowsejButton.setEnabled(true);
				this.wdirBrowsejButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(final MouseEvent evt) {
						System.out.println("wdirBrowsejButton.mouseClicked, event=" + evt);
						final JDirectoryChooser chooser = new JDirectoryChooser();
						final int returnVal = chooser.showOpenDialog(OpenWithItemForm.this);
					    if(returnVal == JFileChooser.APPROVE_OPTION) {
					       System.out.println("You chose to open this file: " + chooser.getSelectedFile().getPath());
					       OpenWithItemForm.this.wdirjTextField.setText(chooser.getSelectedFile().getPath());
					    }
					}
				});
			}
			this.setSize(400, 200);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the reponce
	 */
	public final int getResult() {
		return this.result;
	}
}
