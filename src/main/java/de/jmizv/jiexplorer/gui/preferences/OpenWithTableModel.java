package de.jmizv.jiexplorer.gui.preferences;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import de.jmizv.jiexplorer.util.OpenWith;


public class OpenWithTableModel extends AbstractTableModel {
    /**
	 *
	 */
	private static final long serialVersionUID = -5629823935176491302L;
	public static final int NAME_INDEX = 0;
    public static final int COMMAND_INDEX = 1;
    public static final int WORKING_DIR_INDEX = 2;
    public static final int HIDDEN_INDEX = 3;

    protected String[] columnNames;
    protected Vector<OpenWith> dataVector;

    public OpenWithTableModel(final String[] columnNames) {
        this.columnNames = columnNames;
        this.dataVector = new Vector<OpenWith>();
    }

    public OpenWithTableModel(final String[] columnNames, final Vector<OpenWith> dataVector) {
        this.columnNames = columnNames;
        this.dataVector = dataVector;
    }

    @Override
	public String getColumnName(final int column) {
        return this.columnNames[column];
    }

    @Override
	public boolean isCellEditable(final int row, final int column) {
        if (column == HIDDEN_INDEX) {
			return false;
		} else {
			return true;
		}
    }

    @Override
	public Class<?> getColumnClass(final int column) {
        switch (column) {
            case NAME_INDEX:
            case COMMAND_INDEX:
            case WORKING_DIR_INDEX:
               return String.class;
            default:
               return Object.class;
        }
    }

    public Object getValueAt(final int row, final int column) {
        final OpenWith record = this.dataVector.get(row);
        switch (column) {
            case NAME_INDEX:
               return record.getCommandName();
            case COMMAND_INDEX:
               return record.getCommand();
            case WORKING_DIR_INDEX:
               return record.getWorkingDir();
            default:
               return new Object();
        }
    }

    @Override
	public void setValueAt(final Object value, final int row, final int column) {
        final OpenWith record = this.dataVector.get(row);
        switch (column) {
            case NAME_INDEX:
               record.setCommandName((String)value);
               break;
            case COMMAND_INDEX:
               record.setCommand((String)value);
               break;
            case WORKING_DIR_INDEX:
               record.setWorkingDir((String)value);
               break;
            default:
               System.out.println("invalid index");
        }
        fireTableCellUpdated(row, column);
    }

    public int getRowCount() {
        return this.dataVector.size();
    }

    public int getColumnCount() {
        return this.columnNames.length;
    }

    public boolean hasEmptyRow() {
        if (this.dataVector.size() == 0) {
			return false;
		}
        final OpenWith audioRecord = this.dataVector.get(this.dataVector.size() - 1);
        if (((audioRecord.getCommandName() == null) || (audioRecord.getCommandName().trim().length() < 1)) &&
        	((audioRecord.getCommand()     == null) || (audioRecord.getCommand().trim().length()     < 1))) {
			return true;
		} else {
			return false;
		}
    }

    public void addEmptyRow() {
        this.dataVector.add(new OpenWith());
        fireTableRowsInserted(
           this.dataVector.size() - 1,
           this.dataVector.size() - 1);
    }

    public Vector<OpenWith> getDataVector() {
    	return this.dataVector;
    }


}