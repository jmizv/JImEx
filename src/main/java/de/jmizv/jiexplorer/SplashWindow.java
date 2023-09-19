package de.jmizv.jiexplorer;

import de.jmizv.jiexplorer.util.JIUtility;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.net.URL;


/**
 * A Splash window.
 * <p>
 * Usage: MyApplication is your application class. Create a Splasher class which
 * opens the splash window, invokes the main method of your Application class,
 * and disposes the splash window afterwards.
 * Please note that we want to keep the Splasher class and the SplashWindow class
 * as small as possible. The less code and the less classes must be loaded into
 * the JVM to open the splash screen, the faster it will appear.
 * <pre>
 * class Splasher {
 *    public static void main(String[] args) {
 *         SplashWindow.splash(Startup.class.getResource("splash.gif"));
 *         MyApplication.main(args);
 *         SplashWindow.disposeSplash();
 *    }
 * }
 * </pre>
 *
 * @author Werner Randelshofer
 * @version 2.1 2005-04-03 Revised.
 */
public class SplashWindow extends Window {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The current instance of the splash window.
     * (Singleton design pattern).
     */
    private static SplashWindow instance;

    /**
     * The splash image which is displayed on the splash window.
     */
    private final java.awt.Image image;

    /**
     * This attribute indicates whether the method
     * paint(Graphics) has been called at least once since the
     * construction of this window.<br>
     * This attribute is used to notify method splash(Image)
     * that the window has been drawn at least once
     * by the AWT event dispatcher thread.<br>
     * This attribute acts like a latch. Once set to true,
     * it will never be changed back to false again.
     *
     * @see #paint
     * @see #splash
     */
    private boolean paintCalled = false;
    private boolean loadingDB = false;

    /**
     * Creates a new instance.
     *
     * @param parent the parent of the window.
     * @param image  the splash image.
     */
    private SplashWindow(final Frame parent, final java.awt.Image image) {
        super(parent);
        this.image = image;

        // Load the image
        final MediaTracker mt = new MediaTracker(this);
        mt.addImage(image, 0);
        try {
            mt.waitForID(0);
        } catch (final InterruptedException ie) {

        }

        // Center the window on the screen
        JIUtility.centerFrameOnScreen(this::getWidth,
                this::getHeight,
                () -> Toolkit.getDefaultToolkit().getScreenSize(),
                this::setLocation);

        // Users shall be able to close the splash window by
        // clicking on its display area. This mouse listener
        // listens for mouse clicks and disposes the splash window.
        final MouseAdapter disposeOnClick = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                // Note: To avoid that method splash hangs, we
                // must set paintCalled to true and call notifyAll.
                // This is necessary because the mouse click may
                // occur before the contents of the window
                // has been painted.
                synchronized (SplashWindow.this) {
                    SplashWindow.this.paintCalled = true;
                    SplashWindow.this.notifyAll();
                }
                dispose();
            }
        };
        addMouseListener(disposeOnClick);
    }

    public static SplashWindow instance() {
        return instance;
    }

    /**
     * Updates the display area of the window.
     */
    @Override
    public void update(final Graphics g) {
        // Note: Since the paint method is going to draw an
        // image that covers the complete area of the component we
        // do not fill the component with its background color
        // here. This avoids flickering.
        paint(g);
    }

    /**
     * Paints the image on the window.
     */
    @Override
    public void paint(final Graphics g) {
        g.drawImage(this.image, 0, 0, this);

        //g.drawImage(icon,25+progress, 257,this);g
//    	g.setColor(Color.red);
//    	g.fillRoundRect(10+progress, 260, 88, 4, 2, 2);
//    	progress += increment;
//    	if (progress >= 300) {
//    		increment = -5;
//    	}
//    	if (progress == 0) {
//    		increment = 5;
//    	}

        // Notify method splash that the window
        // has been painted.
        // Note: To improve performance we do not enter
        // the synchronized block unless we have to.
        if (!this.paintCalled) {
            this.paintCalled = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    public static void progbar(final int i) {
//    	if (instance != null) {
//			instance.repaint();
//		}
    }

    public static void setInstanceCursor(final java.awt.Cursor cursor) {
        if (instance != null) {
            instance.setCursor(cursor);
            synchronized (instance) {
                instance.notifyAll();
            }
        }
    }

    /**
     * Open's a splash window using the specified image.
     *
     * @param image The splash image.
     */
    public static void splash(final Image image) {
        if ((instance == null) && (image != null)) {
            Frame f = new Frame();
            instance = new SplashWindow(f, image);
            instance.setVisible(true);

            // Note: To make sure the user gets a chance to see the
            // splash window we wait until its paint method has been
            // called at least once by the AWT event dispatcher thread.
            // If more than one processor is available, we don't wait,
            // and maximize CPU throughput instead.
            if (!EventQueue.isDispatchThread()
                && (Runtime.getRuntime().availableProcessors() == 1)) {
                synchronized (instance) {
                    while (!instance.paintCalled) {
                        try {
                            instance.wait();
                        } catch (final InterruptedException e) {
                        }
                    }
                }
            }
        }
    }

    /**
     * Open's a splash window using the specified image.
     *
     * @param imageURL The url of the splash image.
     */
    public static void splash(final URL imageURL) {
        if (imageURL != null) {
            splash(Toolkit.getDefaultToolkit().createImage(imageURL));
        }
    }

    /**
     * Closes the splash window.
     */
    public static void disposeSplash() {
        if (instance != null) {
            instance.getOwner().dispose();
            instance = null;
        }
    }

    /**
     * Invokes the main method of the provided class name.
     *
     * @param args the command line arguments
     */
    public static void invokeMain(final String className, final String[] args) {
        try {
            Class.forName(className)
                    .getMethod("main", String[].class)
                    .invoke(Class.forName(className), new Object[]{args});
        } catch (final Exception e) {
            throw new InternalError("Failed to invoke main method", e);
        }
    }

    public final boolean isLoadingDB() {
        return this.loadingDB;
    }

    public final static void setLoadingDB(final boolean loadingDB) {
        if (instance != null) {
            instance.loadingDB = loadingDB;
            instance.repaint();
        }
    }
}
