package de.jmizv.jiexplorer;

import java.awt.Cursor;
import java.io.File;

import javax.swing.UIManager;

import org.apache.log4j.BasicConfigurator;

import de.jmizv.jiexplorer.gui.preferences.JIPreferences;

public class JILoader {

    public static boolean loading = true;

    public static void main(final String[] args) {
        JIExplorer.USER_ROOT = System.getProperty("user.home");
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            JIExplorer.ROOT_FOLDER = System.getProperty("java.io.tmpdir") + File.separator + "My Computer";
        } else {
            JIExplorer.ROOT_FOLDER = File.separator;
        }
        if (args != null) {
            for (final String arg : args) {
                if (arg.equals("-readonly")) {
                    JIPreferences.getInstance().setReadOnly(true);
                }
                if (arg.equals("-reset")) {
                    JIPreferences.getInstance().clearPreferences();
                }
            }
        }

        try {
            SplashWindow.setInstanceCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } catch (final RuntimeException e) {
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
        }

        JIExplorer.loading = true;

        final Thread runner = new Thread() {
            @Override
            public void run() {
                while (JIExplorer.loading) {
                    try {
                        Thread.sleep(60);
                    } catch (InterruptedException e) {
                    }
                    SplashWindow.progbar(0);
                }
            }
        };
        runner.start();

        BasicConfigurator.configure();

        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.ddscale", "true");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
        System.setProperty("apple.awt.rendering", "VALUE_RENDER_SPEED"); // VALUE_RENDER_SPEED or VALUE_RENDER_QUALITY
        System.setProperty("apple.awt.interpolation", "VALUE_INTERPOLATION_NEAREST_NEIGHBOR"); // VALUE_INTERPOLATION_NEAREST_NEIGHBOR, VALUE_INTERPOLATION_BILINEAR, or VALUE_INTERPOLATION_BICUBIC
        System.setProperty("apple.awt.showGrowBox", "true");
        System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "JIExplorer");

        JIExplorer.instance().setVisible(true);
        JIExplorer.instance().gotoLastDirectory();
        JIExplorer.loading = false;

        try {
            SplashWindow.setInstanceCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } catch (final RuntimeException e) {
        }
    }
}
