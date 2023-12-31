package de.jmizv.jiexplorer.gui.thumb;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import de.jmizv.jiexplorer.db.JIThumbnailCache;
import de.jmizv.jiexplorer.gui.preferences.JIPreferences;
import de.jmizv.jiexplorer.util.DiskObject;




public class JIListRenderer extends JPanel implements ListCellRenderer {
	//private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JIListRenderer.class);

	/**
	 *
	 */
	private static final long serialVersionUID = -5697748043124138986L;
	private final JLabel iconLabel = new JLabel();
	private final JTextField descriptionLabel = new JTextField();

	public JIListRenderer() {
		super(new BorderLayout());

		this.iconLabel.setBounds(0, 0, 300, 300);
		this.iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.iconLabel.setVerticalAlignment(SwingConstants.CENTER);
		this.descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.descriptionLabel.setEditable(false);

		add(this.iconLabel, BorderLayout.CENTER);
		add(this.descriptionLabel, BorderLayout.SOUTH);
		setPreferredSize(JIPreferences.getInstance().getIconDim());
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	}


	public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {

		if (value instanceof DiskObject) {
			final DiskObject diskObject = (DiskObject) value;
			this.iconLabel.setIcon(JIThumbnailCache.getInstance().getThumbnailFor(diskObject, (JIThumbnailList)list, index));
			this.descriptionLabel.setText(diskObject.getName());
			setBackground(isSelected ? list.getSelectionBackground():list.getBackground());
			return this;
		}
		return null;
	}


	@Override
	public void repaint(final long tm, final int x, final int y, final int width, final int height) {
	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	@Override
	public void repaint(final Rectangle r) {
	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	@Override
	protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final byte oldValue, final byte newValue) {
	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final char oldValue, final char newValue) {
	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final short oldValue, final short newValue) {
	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final int oldValue, final int newValue) {
	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final long oldValue, final long newValue) {
	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final float oldValue, final float newValue) {
	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final double oldValue, final double newValue) {
	}

	/**
	 * Overridden for performance reasons.
	 * See the <a href="#override">Implementation Note</a>
	 * for more information.
	 */
	@Override
	public void firePropertyChange(final String propertyName, final boolean oldValue, final boolean newValue) {
	}

}
