package de.jmizv.jiexplorer.gui.preferences;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import de.jmizv.jiexplorer.db.JIThumbnailService;
import de.jmizv.jiexplorer.util.OpenWith;


public class OpenWithForm extends JPanel {
    /**
	 *
	 */
	private static final long serialVersionUID = -498439150906545326L;

	public static final String[] columnNames = {
        "Command Name", "Command", "Working Directory", ""
    };

    protected JTable table;
    protected JScrollPane scroller;
    protected OpenWithTableModel tableModel;
    protected JPanel buttonPanel;
    protected JButton add;
    protected JButton edit;
    protected JButton remove;
    protected Frame parent;

    public OpenWithForm(final Frame parent) {
    	this.parent = parent;
    	initGUI();
    }



    public void initGUI() {
        this.tableModel = new OpenWithTableModel(columnNames, JIThumbnailService.getInstance().getOpenWith());
        this.tableModel.addTableModelListener(new OpenWithForm.InteractiveTableModelListener());
        this.table = new JTable();
        this.table.setModel(this.tableModel);
        this.table.setSurrendersFocusOnKeystroke(true);
        if (!this.tableModel.hasEmptyRow()) {
			this.tableModel.addEmptyRow();
		}

        this.scroller = new javax.swing.JScrollPane(this.table);
        this.table.setPreferredScrollableViewportSize(new java.awt.Dimension(500, 300));
        final TableColumn hidden = this.table.getColumnModel().getColumn(OpenWithTableModel.HIDDEN_INDEX);
        hidden.setMinWidth(2);
        hidden.setPreferredWidth(2);
        hidden.setMaxWidth(2);
        hidden.setCellRenderer(new InteractiveRenderer(OpenWithTableModel.HIDDEN_INDEX));

        setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
        add(this.scroller);

        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(new BoxLayout(this.buttonPanel,BoxLayout.Y_AXIS));

        this.add = new JButton("Add");
        this.add.setAlignmentY(Component.TOP_ALIGNMENT);
        this.add.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add.setMinimumSize(new Dimension(100,10));
        this.add.addActionListener(new ActionListener() {
        	public void actionPerformed(final ActionEvent ae) {
        		if(!((OpenWithTableModel)OpenWithForm.this.table.getModel()).hasEmptyRow()){
        			((OpenWithTableModel)OpenWithForm.this.table.getModel()).getDataVector().add(new OpenWith());
        			((OpenWithTableModel)OpenWithForm.this.table.getModel()).fireTableRowsInserted(OpenWithForm.this.table.getRowCount(), OpenWithForm.this.table.getRowCount());
        		}
        		final OpenWithItemForm openWithItemForm = new OpenWithItemForm(OpenWithForm.this.parent,OpenWithForm.this.table.getRowCount()-1,OpenWithForm.this.table,OpenWithItemForm.ADD_ACTION);
        		openWithItemForm.setVisible(true);
        	}
        });

        this.edit = new JButton("Edit");
        this.edit.setAlignmentY(Component.TOP_ALIGNMENT);
        this.edit.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.edit.setMinimumSize(new Dimension(100,10));
        this.edit.addActionListener(new ActionListener() {
        	public void actionPerformed(final ActionEvent ae) {
        		final OpenWithItemForm openWithItemForm = new OpenWithItemForm(OpenWithForm.this.parent,OpenWithForm.this.table.getSelectedRow(),OpenWithForm.this.table,OpenWithItemForm.EDIT_ACTION);
        		openWithItemForm.setVisible(true);
        	}
        });

        this.remove = new JButton("Remove");
        this.remove.setAlignmentY(Component.TOP_ALIGNMENT);
        this.remove.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.remove.setMinimumSize(new Dimension(100,10));
        this.remove.addActionListener(new ActionListener() {
        	public void actionPerformed(final ActionEvent ae) {
				JIThumbnailService.getInstance().deleteOpenWith(((OpenWithTableModel)OpenWithForm.this.table.getModel()).dataVector.elementAt(OpenWithForm.this.table.getSelectedRow()));
        		((OpenWithTableModel)OpenWithForm.this.table.getModel()).getDataVector().removeElementAt(OpenWithForm.this.table.getSelectedRow());
        		((OpenWithTableModel)OpenWithForm.this.table.getModel()).fireTableRowsDeleted(OpenWithForm.this.table.getSelectedRow(), OpenWithForm.this.table.getSelectedRow());
        	}
        });

        this.buttonPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        this.buttonPanel.add(this.add);
        this.buttonPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        this.buttonPanel.add(this.edit);
        this.buttonPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        this.buttonPanel.add(this.remove);
        this.buttonPanel.add(Box.createVerticalGlue());

        add(this.buttonPanel);
    }

    public Vector<OpenWith> getOpenWith() {
    	return this.tableModel.dataVector;
    }

    public void highlightLastRow(final int row) {
        final int lastrow = this.tableModel.getRowCount();
        if (row == lastrow - 1) {
			this.table.setRowSelectionInterval(lastrow - 1, lastrow - 1);
		} else {
			this.table.setRowSelectionInterval(row + 1, row + 1);
		}

        this.table.setColumnSelectionInterval(0, 0);
    }

    class InteractiveRenderer extends DefaultTableCellRenderer {
        /**
		 *
		 */
		private static final long serialVersionUID = 6016997926559791435L;
		protected int interactiveColumn;

        public InteractiveRenderer(final int interactiveColumn) {
            this.interactiveColumn = interactiveColumn;
        }

        @Override
		public Component getTableCellRendererComponent(final JTable table,
           final Object value, final boolean isSelected, final boolean hasFocus, final int row,
           final int column)
        {
            final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if ((column == this.interactiveColumn) && hasFocus) {
                if (((OpenWithForm.this.tableModel.getRowCount() - 1) == row) &&
                   !OpenWithForm.this.tableModel.hasEmptyRow()) {
					OpenWithForm.this.tableModel.addEmptyRow();
				}
                highlightLastRow(row);
            }

            return c;
        }
    }

    public class InteractiveTableModelListener implements TableModelListener {
        public void tableChanged(final TableModelEvent evt) {
            if (evt.getType() == TableModelEvent.UPDATE) {
                final int column = evt.getColumn();
                final int row = evt.getFirstRow();
                System.out.println("row: " + row + " column: " + column);
                OpenWithForm.this.table.setColumnSelectionInterval(column + 1, column + 1);
                OpenWithForm.this.table.setRowSelectionInterval(row, row);
            }
        }
    }

    public static void main(final String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            final JFrame frame = new JFrame("Interactive Form");
            frame.addWindowListener(new WindowAdapter() {
                @Override
				public void windowClosing(final WindowEvent evt) {
                    System.exit(0);
                }
            });
            frame.getContentPane().add(new OpenWithForm(frame));
            frame.pack();
            frame.setVisible(true);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}