
package de.jmizv.jiexplorer.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JSplitPane;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.gui.preferences.JIPreferences;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIExplorerContext;
import de.jmizv.jiexplorer.util.JIObservable;
import de.jmizv.jiexplorer.util.JIUtility;


public class JImagePanel extends AbstractDiskObjectDisplay {

	/**
	 *
	 */
	private static final long serialVersionUID = -3999586854091461876L;

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JImagePanel.class);

	private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	/** image to display */
	protected BufferedImage source = null;
	protected BufferedImage scaleImage = null;

	protected double scale = 1.0;

	/** image origin relative to panel origin */
	protected int originX = 0;
	protected int originY = 0;
	protected int dragX = 0;
	protected int dragY = 0;

	protected int pWidth = 0;
	protected int pHeight = 0;

	public boolean dragState = false;

	// Parent Type
	public boolean scaleToParent = false;

	//protected final Container jIViewer;

	public JImagePanel() {
		this(false);
	}

	/** Creates a new instance of ImageDisplay */
	/** default constructor */
	public JImagePanel(final boolean scaleToParent) {
		super();
		this.scaleToParent = scaleToParent;
		this.addComponentListener(new JIViewerComponentAdapter());
	}

	public void display(final DiskObject dObj) {
		if ((dObj != null) && (dObj.getFile() != null)) {
			setImage(JIUtility.openImage(dObj));
		} else {
			setImage(JIUtility.blankImage());
		}
	}

	public void frameResize() {
		scaleImage();
		repaint();
	}

	public final BufferedImage getImage() {
		return this.scaleImage;
	}

	public final int getImageHeight() {
		return this.source.getHeight();
	}

	public final int getImageWidth() {
		return this.source.getWidth();
	}

	/** get the image origin */
	public final Point getOrigin() {
		return new Point(this.originX, this.originY);
	}

	/**
	 * @return the originX
	 */
	public synchronized final int getOriginX() {
		return this.originX;
	}

	/**
	 * @return the originY
	 */
	public synchronized final int getOriginY() {
		return this.originY;
	}

	public final int getScaleHeight() {
		return this.scaleImage.getHeight();
	}

	public final int getScaleWidth() {
		return this.scaleImage.getWidth();
	}

	/** paint routine */
	@Override
	public final void paint(final Graphics g) {

		if(JIPreferences.getInstance().isImageViewerFullScreen()) {
			g.setColor(Color.black);
		} else {
			g.setColor(Color.white);
		}

		g.fillRect(0, 0, this.pWidth, this.pHeight);

		if (this.scaleImage != null) {
			final int width = this.scaleImage.getWidth(this);
			final int height = this.scaleImage.getHeight(this);
			//log.debug("originX = " + originX + " originY = " + originY + " width = " + width + " height = " + height);
			g.drawImage(this.scaleImage, this.originX, this.originY, width, height, this);
		}
	}

	public String percent() {
		return String.valueOf((int)(((float)this.scaleImage.getHeight()/(float)this.source.getHeight())*100))+"%";
	}

	public void scale(final double adjustment) {
		final double oldScale = this.scale;
		this.scale += adjustment;
		if (this.scale != 1) {
			final int new_width = (int) (this.source.getWidth() * this.scale);
			final int old_width = (int) (this.source.getWidth() * oldScale);

			final int new_height = (int) (this.source.getHeight() * this.scale);
			final int old_height = (int) (this.source.getHeight() * oldScale);

			this.originX -= (new_width/2) - (old_width/2);
			this.originY -= (new_height/2) - (old_height/2);

			this.scaleImage = null;
			this.scaleImage = new BufferedImage(new_width, new_height, BufferedImage.TYPE_INT_RGB);
			final Graphics2D g2d = this.scaleImage.createGraphics();
			g2d.setRenderingHint( RenderingHints.KEY_RENDERING, JIPreferences.getInstance().isImageViewerQuality()?RenderingHints.VALUE_RENDER_QUALITY:RenderingHints.VALUE_RENDER_SPEED);
			g2d.drawImage( this.source, AffineTransform.getScaleInstance(this.scale, this.scale), null );
			g2d.dispose();
		} else {
			this.scaleImage = this.source;
		}
		repaint();
	}

	protected final void setDimensions() {
		final Insets insets = getInsets();
		final Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();

		if (this.scaleToParent){
			if (getParent() instanceof JSplitPane) {
				final int divLoc  = ((JSplitPane) getParent()).getDividerLocation();
				final int divSize = ((JSplitPane) getParent()).getDividerSize();
				final int divOrnt = ((JSplitPane) getParent()).getOrientation();

				if (divOrnt == JSplitPane.VERTICAL_SPLIT) {
					if(((JSplitPane) getParent()).getTopComponent().equals(this)) {
						this.pHeight = divLoc - (insets.left + insets.right);
					} else {
						this.pHeight = getParent().getHeight() - (divLoc + divSize + insets.top + insets.bottom);
					}
					this.pWidth =  getParent().getWidth() - (insets.left + insets.right);
				} else {
					if(((JSplitPane) getParent()).getLeftComponent().equals(this)) {
						this.pWidth = divLoc - (insets.left + insets.right);
					} else {
						this.pWidth = getParent().getWidth() - (divLoc + divSize + insets.left + insets.right);
					}
					this.pHeight = getParent().getHeight() - (insets.top + insets.bottom);
				}

			} else {
				this.pWidth = getParent().getWidth() - (insets.left + insets.right);
				this.pHeight = getParent().getHeight() - (insets.top + insets.bottom);
			}

		} else if (JIPreferences.getInstance().isImageViewerFullScreen()) {
			this.pWidth = dimScreen.width;
			this.pHeight = dimScreen.height;
		} else {
			this.pWidth = (int)( dimScreen.width * .97 );
			this.pHeight = (int)( dimScreen.height * .93 );
		}
	}

	protected final void scaleImage() {

		this.scale = 1.0;

		final Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();

		setDimensions();
		final Dimension scaleDim = new Dimension(this.pWidth, this.pHeight);

		if (this.scaleToParent){

			if ((this.source != null) && (scaleDim != null)) {
				this.scale = JIUtility.scaleFactor(scaleDim, this.getParent().getInsets(), this.source.getWidth(), this.source.getHeight());

				scale(0);

				final int w = this.scaleImage.getWidth();
				final int h = this.scaleImage.getHeight();

				//setBounds(0, 0, w, h);
				doLayout();

				this.originX = ((this.pWidth/2)-(w/2));
				this.originY = ((this.pHeight/2)-(h/2));
			}

		} else if ( JIPreferences.getInstance().isImageViewerWinFitScreen() || (this.source.getWidth() > dimScreen.width) || (this.source.getHeight() > dimScreen.height) ) {
			if (JIPreferences.getInstance().isImageViewerFullScreen()) {
				this.scale = JIUtility.scaleFactor(scaleDim, this.source.getWidth(), this.source.getHeight());
			} else {
				this.scale = JIUtility.scaleFactor(scaleDim, this.getParent().getInsets(), this.source.getWidth(), this.source.getHeight());
			}
			scale(0);

			final int w = this.scaleImage.getWidth();
			final int h = this.scaleImage.getHeight();

			if (!JIPreferences.getInstance().isImageViewerFullScreen()) {
				setBounds(0, 0, w, h);
				this.originX = 0;
				this.originY = 0;
			} else {
				setBounds(0, 0, screenSize.width, screenSize.height);
				this.originX = (screenSize.width-w)/2;
				this.originY = (screenSize.height-h)/2;
			}
		}
	}

	/** use to display a new image */
	public final void setImage(final BufferedImage im) {
		this.source = null;
		this.scaleImage = null;

		this.source = im;

		scaleImage();

		this.dragX = 0;
		this.dragY = 0;

		repaint();
	}

	/** move image within it's container */
	public final void setOrigin(final int x, final int y) {
		this.originX = x;
		this.originY = y;
		repaint();
	}

	/**
	 * @param originX the originX to set
	 */
	public synchronized final void setOriginX(final int originX) {
		this.originX = originX;
	}

	/**
	 * @param originY the originY to set
	 */
	public synchronized final void setOriginY(final int originY) {
		this.originY = originY;
	}

	@Override
	public void update(final JIObservable o, final Object arg) {
		log.debug(JIUtility.memoryInf());

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		setBackground(JIPreferences.getInstance().getImageViewerColor());

		final JIExplorerContext context = JIExplorer.instance().getContext();
		final int index = context.getLastSelectedDiskObjectIndex();
		final DiskObject[] dobj = context.getSelectedDiskObjects();
		if (index < dobj.length) {
			display(dobj[index]);
		}

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	class JIViewerComponentAdapter extends java.awt.event.ComponentAdapter {

		@Override
		public void componentResized(final ComponentEvent e) {
			frameResize();
			repaint();
		}
	}
}
