
package de.jmizv.jiexplorer.gui.viewer;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serial;
import java.net.MalformedURLException;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.gui.JImagePanel;
import de.jmizv.jiexplorer.gui.preferences.JIPreferences;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIUtility;
import de.jmizv.jiexplorer.util.OrderedDiskObjectList;

/**
 * Frame to display an image.
 */
public class JIViewer extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JIViewer.class);

    protected boolean exit = false;

    protected BufferedImage source = null;

    protected final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    protected final GraphicsDevice device;
    protected Cursor blankCursor;

    protected final Action renderAction;
    protected final Action slideShowAction;
    protected final Action randomAction;
    protected final Action previousAction;
    protected final Action nextAction;
    protected final Action scaleupAction;
    protected final Action scaledownAction;
    protected final Action directionAction;
    protected final Action fullScreenAction;
    protected final Action exitAction;

    protected final JPopupMenu jPopupMenu = new JPopupMenu();
    protected final OrderedDiskObjectList jImageList;
    protected JImagePanel jImagePanel;

    boolean getFocus = false;

    private javax.swing.Timer ssTimer = null;

    private Point dragStartPoint;
    private long dragStartTime;
    private boolean dragInProgress = false;

    public JIViewer(OrderedDiskObjectList imageList) {
        super("JIViewer");

        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            final ImageIcon blank;
            try {
                blank = new ImageIcon(new File("/images/transcursor.gif").toURI().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Could not load image file", e);
            }
            this.blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(blank.getImage(), new Point(16, 16), "Blank");
        }
        setIconImage(JIExplorer.smallIcon.getImage());
        System.setProperty("apple.awt.showGrowBox", "false");
        this.jImageList = imageList;

        final GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.device = environment.getDefaultScreenDevice();

        this.jImagePanel = new JImagePanel();

        this.exitAction = new AbstractAction("Close") {

            @Serial
            private static final long serialVersionUID = 1L;

            public void actionPerformed(final ActionEvent e) {
                if (JIPreferences.getInstance().isImageViewerFullScreen()) {
                    toggleFullScreen();
                }
                if (JIViewer.this.ssTimer != null) {
                    JIViewer.this.ssTimer.stop(); // Kill Slide Show
                    JIViewer.this.ssTimer = null;
                }
                JIViewer.this.dispose();
            }
        };
        this.jImagePanel.registerKeyboardAction(this.exitAction, KeyStroke.getKeyStroke(
                KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_FOCUSED);


        // Full Screen
        this.fullScreenAction = new AbstractAction("Full Screen") {
            /**
             *
             */
            private static final long serialVersionUID = -1727242993727729140L;

            public void actionPerformed(final ActionEvent e) {
                toggleFullScreen();
            }
        };
        this.jImagePanel.registerKeyboardAction(this.fullScreenAction, KeyStroke
                .getKeyStroke(KeyEvent.VK_F, 512), JComponent.WHEN_FOCUSED);
        this.jImagePanel.registerKeyboardAction(this.fullScreenAction, KeyStroke
                .getKeyStroke(KeyEvent.VK_F, 256), JComponent.WHEN_FOCUSED);


        // Next
        this.nextAction = new AbstractAction("Next") {
            /**
             *
             */
            private static final long serialVersionUID = -3700843395082038924L;

            public void actionPerformed(final ActionEvent e) {
                processNextImage();
            }
        };
        this.jImagePanel.registerKeyboardAction(this.nextAction, KeyStroke.getKeyStroke(
                KeyEvent.VK_RIGHT, 0), JComponent.WHEN_FOCUSED);


        // Next
        this.directionAction = new AbstractAction() {
            /**
             *
             */
            private static final long serialVersionUID = 827001029751243086L;

            public void actionPerformed(final ActionEvent e) {
                processImage(JIViewer.this.jImageList.nextImage());
            }
        };
        this.jImagePanel.registerKeyboardAction(this.directionAction, KeyStroke.getKeyStroke(
                KeyEvent.VK_SPACE, 0), JComponent.WHEN_FOCUSED);
        this.jImagePanel.registerKeyboardAction(this.directionAction, KeyStroke.getKeyStroke(
                KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);


        // ScaleUp
        this.scaleupAction = new AbstractAction("ScaleUp") {
            /**
             *
             */
            private static final long serialVersionUID = 3858052872225781878L;

            public void actionPerformed(final ActionEvent e) {
                JIViewer.this.jImagePanel.scale(.05);
            }
        };
        this.jImagePanel.registerKeyboardAction(this.scaleupAction, KeyStroke.getKeyStroke(
                KeyEvent.VK_UP, 0), JComponent.WHEN_FOCUSED);


        // Previous
        this.previousAction = new AbstractAction("Previous") {
            /**
             *
             */
            private static final long serialVersionUID = -4291834041938747528L;

            public void actionPerformed(final ActionEvent e) {
                processPreviousImage();
            }
        };
        this.jImagePanel.registerKeyboardAction(this.previousAction, KeyStroke
                .getKeyStroke(KeyEvent.VK_LEFT, 0), JComponent.WHEN_FOCUSED);


        // ScaleDown
        this.scaledownAction = new AbstractAction("ScaleDown") {
            /**
             *
             */
            private static final long serialVersionUID = -7858867712824225958L;

            public void actionPerformed(final ActionEvent e) {
                JIViewer.this.jImagePanel.scale(-.05);
            }
        };
        this.jImagePanel.registerKeyboardAction(this.scaledownAction, KeyStroke
                .getKeyStroke(KeyEvent.VK_DOWN, 0), JComponent.WHEN_FOCUSED);


        // Random
        this.randomAction = new AbstractAction("Random") {
            /**
             *
             */
            private static final long serialVersionUID = 8061206978900392652L;

            public void actionPerformed(final ActionEvent e) {
                processRandomImage();
            }
        };
        this.jImagePanel.registerKeyboardAction(this.randomAction, KeyStroke
                .getKeyStroke(KeyEvent.VK_R, 0), JComponent.WHEN_FOCUSED);


        // Slide Show
        this.slideShowAction = new AbstractAction("Slide Show Start") {
            /**
             *
             */
            private static final long serialVersionUID = 7273603422265873291L;

            public void actionPerformed(final ActionEvent e) {
                if (JIViewer.this.ssTimer == null) {
                    startSlideShow();
                } else {
                    JIViewer.this.ssTimer.stop(); // Kill Slide Show
                    JIViewer.this.ssTimer = null;
                }
            }
        };
        this.jImagePanel.registerKeyboardAction(this.slideShowAction, KeyStroke
                .getKeyStroke(KeyEvent.VK_S, 512), JComponent.WHEN_FOCUSED);
        this.jImagePanel.registerKeyboardAction(this.slideShowAction, KeyStroke
                .getKeyStroke(KeyEvent.VK_S, 256), JComponent.WHEN_FOCUSED);


        // Render Toggel
        this.renderAction = new AbstractAction("Render Speed") {
            /**
             *
             */
            private static final long serialVersionUID = 3238446110582233401L;

            public void actionPerformed(final ActionEvent e) {
                JIPreferences.getInstance().setSetImageViewerQuality(!JIPreferences.getInstance().isImageViewerQuality());
                processImage(JIViewer.this.jImageList.currentImage());
            }
        };

        this.jImagePanel.addMouseListener(new JIViewerMouseAdapter());
        this.jImagePanel.addMouseMotionListener(new JIVeiwerMouseMotionAdapter());
        this.jImagePanel.addMouseWheelListener(new JIVeiwerMouseWheelListener());

        addComponentListener(new JIViewerComponentAdapter());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                JIViewer.this.exit = true;
                dispose();
            }
        });

        ((JPanel) getContentPane()).setOpaque(false);
        getLayeredPane().add(this.jImagePanel, Integer.MIN_VALUE);

        JIPreferences.getInstance().setIMAGE_VIEWER_FULL_SCREEN(false);

        setAlwaysOnTop(JIPreferences.getInstance().isImageViewerOnTop());

        pack();
        setVisible(true);
        processImage(this.jImageList.currentImage());
    }

    public final void frameLayout() {
        if (!JIPreferences.getInstance().isImageViewerFullScreen()) {
            setResizable(true);
            setUndecorated(false);
            setVisible(true);
        } else {
            setUndecorated(true);
            setResizable(false);
            this.device.setFullScreenWindow(this);
            this.device.setDisplayMode(this.device.getDisplayMode());
        }
        processImage(this.jImageList.currentImage());
    }

    public final void centerFrame() {
        final Dimension fDim = getSize();
        final int x = (this.screenSize.width - fDim.width) / 2;
        final int y = (this.screenSize.height - fDim.height) / 2;

        setLocation(x, y);
    }

    public final void toggleFullScreen() {
        JIPreferences.getInstance().setIMAGE_VIEWER_FULL_SCREEN(!JIPreferences.getInstance().isImageViewerFullScreen());
        if (!JIPreferences.getInstance().isImageViewerFullScreen()) {
            this.device.setFullScreenWindow(null);
        }
        dispose();
        frameLayout();
    }

    public final void processImage(final DiskObject dObj) {
        log.debug(JIUtility.memoryInf());

        if (JIPreferences.getInstance().isImageViewerFullScreenHideMouse() && System.getProperty("os.name").toLowerCase().startsWith("win") && JIPreferences.getInstance().isImageViewerFullScreen()) {
            setCursor(this.blankCursor);
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }

        this.jImagePanel.setBackground(JIPreferences.getInstance().getImageViewerColor());
        getContentPane().setBackground(JIPreferences.getInstance().getImageViewerColor());

        if ((dObj != null) && (dObj.getFile() != null)) {
            this.jImagePanel.setImage(JIUtility.openImage(dObj));
        } else {
            this.jImagePanel.setImage(JIUtility.blankImage());
        }


        String title = null;
        // Load the image and create the title
        if (((dObj != null) && (dObj.getFile() != null))) {
            if (!JIPreferences.getInstance().isImageViewerShowFullPath()) {
                title = dObj.getName() + "  ( " + this.jImagePanel.getImageWidth() + " x "
                        + this.jImagePanel.getImageHeight() + " ) " + this.jImagePanel.percent();
            } else {
                title = dObj.getPath() + "  ( " + this.jImagePanel.getImageWidth() + " x "
                        + this.jImagePanel.getImageHeight() + " ) " + this.jImagePanel.percent();
            }
        } else {
            title = "broken or invalid image file:";
        }

        if (!JIPreferences.getInstance().isImageViewerFullScreen()) {
            setTitle(title);
            resizeFrame();

            if (JIPreferences.getInstance().isImageViewerCenterWin()) {
                centerFrame();
            } else {
                setLocation(JIPreferences.getInstance().getImageViewerLoc());
            }
        }
        validate();

        if (JIPreferences.getInstance().isImageViewerFullScreenHideMouse() && System.getProperty("os.name").toLowerCase().startsWith("win") && JIPreferences.getInstance().isImageViewerFullScreen()) {
            setCursor(this.blankCursor);
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public void frameResized() {
        this.jImagePanel.frameResize();
    }

    public void processCurrentImage() {
        processImage(this.jImageList.currentImage());
    }

    public void processNextImage() {
        processImage(this.jImageList.nextImage());
    }

    public void processPreviousImage() {
        processImage(this.jImageList.previousImage());
    }

    public void processRandomImage() {
        processImage(this.jImageList.randImage());
    }

    public final void resizeFrame() {
        if (JIPreferences.getInstance().isImageViewerFullScreen()) {
            return;
        }

        final int w = this.jImagePanel.getScaleWidth();
        final int h = this.jImagePanel.getScaleHeight();
        final Insets inP = this.jImagePanel.getInsets();
        final Insets inF = getInsets();

        final int width = w + inP.left + inP.right + inF.left + inF.right;
        final int height = h + inP.top + inP.bottom + inF.top + inF.bottom;

        setSize(new Dimension(width, height));
        this.jImagePanel.setSize(w + inP.left + inP.right, h + inP.top + inP.bottom);
    }

    private void mouseReleased(final MouseEvent e) {
        if (this.getFocus) {
            this.getFocus = false;
            return;
        }

        if (SwingUtilities.isRightMouseButton(e) && JIPreferences.getInstance().isImageViewerFullScreen()) {
            // Enable right click selection. Left click selection is
            // auto-enabled,
            // and doesn't need to be enabled specifically.
            processImage(this.jImageList.previousImage());
        }

        if (SwingUtilities.isLeftMouseButton(e)) {
            processImage(this.jImageList.nextImage());
        }
    }

    private void mouseEvent(final MouseEvent e) {
        this.jPopupMenu.removeAll();

        this.jPopupMenu.add(this.nextAction);
        this.jPopupMenu.add(this.previousAction);
        this.jPopupMenu.add(this.randomAction);

        this.jPopupMenu.addSeparator();

        if (this.ssTimer == null) {
            this.slideShowAction.putValue(Action.NAME, "Start Slide Show");
            this.jPopupMenu.add(this.slideShowAction);
        } else {
            this.slideShowAction.putValue(Action.NAME, "Stop Slide Show");
            this.jPopupMenu.add(this.slideShowAction);
        }

        this.jPopupMenu.addSeparator();

        if (JIPreferences.getInstance().isImageViewerQuality()) {
            this.renderAction.putValue(Action.NAME, "Render Fast");
            this.jPopupMenu.add(this.renderAction);
        } else {
            this.renderAction.putValue(Action.NAME, "Render Quality");
            this.jPopupMenu.add(this.renderAction);
        }

        this.jPopupMenu.addSeparator();
        this.jPopupMenu.add(this.fullScreenAction);
        this.jPopupMenu.addSeparator();
        this.jPopupMenu.add(this.exitAction);
        this.jPopupMenu.show(JIViewer.this, e.getX(), e.getY());
    }

    private void startSlideShow() {
        this.ssTimer = new javax.swing.Timer(JIPreferences.getInstance().getSlideShowDelay() * 1000,
                new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        processImage(JIViewer.this.jImageList.nextImageFile(JIPreferences.getInstance().getSlideShowDirection()));
                    }
                });
        this.ssTimer.start(); // Start the timer
    }

    class JIViewerMouseAdapter extends MouseAdapter {

        @Override
        public void mousePressed(final MouseEvent e) {
            if ((e.getButton() == MouseEvent.BUTTON3) && !JIPreferences.getInstance().isImageViewerFullScreen()) {
                JIViewer.this.mouseEvent(e);
            } else {
                JIViewer.this.dragStartTime = new Date().getTime();
                JIViewer.this.dragStartPoint = new Point(e.getX() - JIViewer.this.jImagePanel.getOriginX(),
                        e.getY() - JIViewer.this.jImagePanel.getOriginY());
            }
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            if (!JIViewer.this.dragInProgress) {
                JIViewer.this.mouseReleased(e);
            } else {
                JIViewer.this.jImagePanel.setOriginX(e.getX() - JIViewer.this.dragStartPoint.x);
                JIViewer.this.jImagePanel.setOriginY(e.getY() - JIViewer.this.dragStartPoint.y);
                JIViewer.this.jImagePanel.repaint();

                JIViewer.this.dragStartPoint = null;
                JIViewer.this.dragInProgress = false;
            }
            e.consume();
        }
    }

    class JIVeiwerMouseMotionAdapter extends MouseMotionAdapter {

        @Override
        public void mouseDragged(final MouseEvent e) {
            JIViewer.this.jImagePanel.setOriginX(e.getX() - JIViewer.this.dragStartPoint.x);
            JIViewer.this.jImagePanel.setOriginY(e.getY() - JIViewer.this.dragStartPoint.y);
            if (new Date().getTime() - JIViewer.this.dragStartTime > 250) {
                JIViewer.this.dragInProgress = true;
                JIViewer.this.jImagePanel.repaint();
            }
        }
    }

    class JIVeiwerMouseWheelListener implements java.awt.event.MouseWheelListener {

        public void mouseWheelMoved(final MouseWheelEvent e) {
            if (e.getWheelRotation() < 0) {
                JIViewer.this.jImagePanel.scale(JIPreferences.getInstance().getImageViewerZoomFactor());
            } else {
                JIViewer.this.jImagePanel.scale(-JIPreferences.getInstance().getImageViewerZoomFactor());
            }
            String title;
            if (!JIPreferences.getInstance().isImageViewerShowFullPath()) {
                title = JIViewer.this.jImageList.currentImage().getName() + "  ( " + JIViewer.this.jImagePanel.getImageWidth() + " x "
                        + JIViewer.this.jImagePanel.getImageHeight() + " ) " + JIViewer.this.jImagePanel.percent();
            } else {
                title = JIViewer.this.jImageList.currentImage().getPath() + "  ( " + JIViewer.this.jImagePanel.getImageWidth() + " x "
                        + JIViewer.this.jImagePanel.getImageHeight() + " ) " + JIViewer.this.jImagePanel.percent();
            }
            setTitle(title);
        }
    }

    class JIViewerComponentAdapter extends java.awt.event.ComponentAdapter {

        @Override
        public void componentMoved(final ComponentEvent e) {
            JIPreferences.getInstance().setImageViewerLoc(JIViewer.this.getX(), JIViewer.this.getY());
        }
    }
}