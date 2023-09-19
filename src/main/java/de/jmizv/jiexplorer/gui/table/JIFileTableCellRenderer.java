package de.jmizv.jiexplorer.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import de.jmizv.jiexplorer.db.JIThumbnailCache;
import de.jmizv.jiexplorer.db.JIThumbnailService;
import de.jmizv.jiexplorer.gui.preferences.JIPreferences;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIUtility;


public class JIFileTableCellRenderer extends JLabel implements TableCellRenderer, Serializable {
	//private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JIFileTableCellRenderer.class);

    /**
	 *
	 */
	private static final long serialVersionUID = 5550091566901029470L;
	protected static Border noFocusBorder = new EmptyBorder(1, 4, 1, 4);
    private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 4, 1, 4);

    // We need a place to store the color the JLabel should be returned
    // to after its foreground and background colors have been set
    // to the selection background color.
    // These ivars will be made protected when their names are finalized.
    private Color unselectedForeground;
    private Color unselectedBackground;

    /**
     * Creates a default table cell renderer.
     */
    public JIFileTableCellRenderer() {
    	super();
    	setOpaque(true);
        setBorder(getNoFocusBorder());
    }

    private static Border getNoFocusBorder() {
        if (System.getSecurityManager() != null) {
			return SAFE_NO_FOCUS_BORDER;
		} else {
			return noFocusBorder;
		}
    }

    /**
     * Overrides <code>JComponent.setForeground</code> to assign
     * the unselected-foreground color to the specified color.
     *
     * @param c set the foreground color to this value
     */
    @Override
	public void setForeground(final Color c) {
        super.setForeground(c);
        this.unselectedForeground = c;
    }

    /**
     * Overrides <code>JComponent.setBackground</code> to assign
     * the unselected-background color to the specified color.
     *
     * @param c set the background color to this value
     */
    @Override
	public void setBackground(final Color c) {
        super.setBackground(c);
        this.unselectedBackground = c;
    }

    /**
     * Notification from the <code>UIManager</code> that the look and feel
     * [L&F] has changed.
     * Replaces the current UI object with the latest version from the
     * <code>UIManager</code>.
     *
     * @see JComponent#updateUI
     */
    @Override
	public void updateUI() {
        super.updateUI();
        setForeground(null);
        setBackground(null);
    }

    /**
     *
     * Returns the default table cell renderer.
     * <p>
     * During a printing operation, this method will be called with
     * <code>isSelected</code> and <code>hasFocus</code> values of
     * <code>false</code> to prevent selection and focus from appearing
     * in the printed output. To do other customization based on whether
     * or not the table is being printed, check the return value from
     * {@link javax.swing.JComponent#isPaintingForPrint()}.
     *
     * @param table  the <code>JTable</code>
     * @param value  the value to assign to the cell at
     *			<code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row  the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     * @see javax.swing.JComponent#isPaintingForPrint()
     */
    public Component getTableCellRendererComponent(final JTable table, final Object value,
    		boolean isSelected, final boolean hasFocus, final int row, final int column) {

    	if (isSelected) {
    		super.setForeground(table.getSelectionForeground());
    		super.setBackground(table.getSelectionBackground());
    	} else {
    		super.setForeground((this.unselectedForeground != null) ? this.unselectedForeground
    				: table.getForeground());
    		super.setBackground((this.unselectedBackground != null) ? this.unselectedBackground
    				: table.getBackground());
    	}

    	setFont(table.getFont());

    	if (hasFocus) {
    		Border border = null;
    		if (isSelected) {
				border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
			}
    		if (border == null) {
				border = UIManager.getBorder("Table.focusCellHighlightBorder");
			}
    		setBorder(border);

    		if (!isSelected && table.isCellEditable(row, column)) {
    			Color col;
    			col = UIManager.getColor("Table.focusCellForeground");
    			if (col != null) {
					super.setForeground(col);
				}
    			col = UIManager.getColor("Table.focusCellBackground");
    			if (col != null) {
					super.setBackground(col);
				}
    		}
    	} else {
			setBorder(getNoFocusBorder());
		}

    	if ((value instanceof DiskObject) && JIPreferences.getInstance().isShowDetailTableIcon()) {
    		final DiskObject dObj = (DiskObject)value;

    		if (dObj.getFile().exists()) {
				if (JIUtility.isSupportedImage(dObj.getSuffix())) {
					setIcon(JIThumbnailCache.getInstance().getThumbnailFor(dObj, table, row));
				} else {
					setIcon(FileSystemView.getFileSystemView().getSystemIcon(dObj.getFile()));
				}
			}
    		setIconTextGap(8);
    	} else {
			setIcon(null);
		}

    	setText((value == null) ? "" : value.toString());
    	if (column == 0) {
			this.setHorizontalAlignment(SwingConstants.LEFT);
		} else {
			this.setHorizontalAlignment(SwingConstants.RIGHT);
		}
    	return this;
    }

	public final Icon getThumbnailFor(final DiskObject diskObject, final JTable table, final int index) {
		try {

			final BufferedImage bImage = JIThumbnailService.getInstance().getThumbnail(diskObject);
			if (bImage != null) {
				return JIUtility.scaleTableIcon(bImage);
			}

		} catch (final Exception e) {
			e.printStackTrace();
		}
		if (diskObject.setAsLoading()) {
			JIThumbnailCache.getInstance().loadThumbnail(diskObject, table, index);
		}
		return JIUtility.getSystemIcon(diskObject);
	}


    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public boolean isOpaque() {
    	final Color back = getBackground();
    	Component p = getParent();
    	if (p != null) {
			p = p.getParent();
		}
    	// p should now be the JTable.
    	boolean colorMatch = (back != null) && (p != null) &&
    	back.equals(p.getBackground()) &&
    	p.isOpaque();
    	return !colorMatch && super.isOpaque();
    }


    @Override
	public void invalidate() {}
    @Override
	public void validate() {}
    @Override
	public void revalidate() {}
    @Override
	public void repaint(final long tm, final int x, final int y, final int width, final int height) {}
    @Override
	public void repaint(final Rectangle r) { }
    @Override
	public void repaint() {}

    @Override
	protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
	// Strings get interned...
	if ((propertyName=="text")
                || (propertyName == "labelFor")
                || (propertyName == "displayedMnemonic")
                || (((propertyName == "font") || (propertyName == "foreground"))
                    && (oldValue != newValue)
                    && (getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null))) {
		super.firePropertyChange(propertyName, oldValue, newValue);
	}
    }


    @Override
	public void firePropertyChange(final String propertyName, final boolean oldValue, final boolean newValue) { }
    protected void setValue(final Object value) {
    	setText((value == null) ? "" : value.toString());
    }

    public static class UIResource extends DefaultTableCellRenderer implements javax.swing.plaf.UIResource
    {

		/**
		 *
		 */
		private static final long serialVersionUID = 9002471595308898712L;
    }
}
