/**
 * @author Ralph Markham
 * @package ${package_name} - @file ${file_name}
 * @date ${date}
 * @version
 */

package de.jmizv.jiexplorer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.Serial;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import net.roydesign.app.AboutJMenuItem;
import net.roydesign.app.Application;
import net.roydesign.app.PreferencesJMenuItem;
import net.roydesign.app.QuitJMenuItem;

import de.jmizv.jiexplorer.db.JIThumbnailService;
import de.jmizv.jiexplorer.gui.AboutFrame;
import de.jmizv.jiexplorer.gui.HelpDialog;
import de.jmizv.jiexplorer.gui.preferences.JIPrefFrame;
import de.jmizv.jiexplorer.gui.preferences.JIPreferences;


/**
 * @author rem
 * <p>
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JIExplorerMenuBar extends JMenuBar {

    @Serial
    private static final long serialVersionUID = 3330862891094602956L;
    private final JMenuItem clearPref;
    private final JMenuItem cleanThumbDB;

    private final JCheckBoxMenuItem thumbnails;
    private final JCheckBoxMenuItem preview;
    private final JCheckBoxMenuItem detail;

    private final JMenuItem newWorkBench;
    private final JMenuItem editWorkBench;

    private final JMenuItem name;
    private final JMenuItem date;
    private final JMenuItem size;
    private final JMenuItem type;
    private final JMenuItem help;

    public JIExplorerMenuBar() {

        // Get the application instance
        final Application app = Application.getInstance();

        app.setName(JIExplorer.APPNAME);

        // Create a menu to append the items to
        final JMenu menuFile = new JMenu("File");
        final JMenu menuView = new JMenu("View");
        final JMenu workBench = new JMenu("Work Bench");
        final JMenu menuHelp = new JMenu("Help");

        add(menuFile);
        //add(workBench);
        add(menuView);
        add(menuHelp);

        this.clearPref = new JMenuItem("Reset Preferences");
        this.clearPref.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                JIPreferences.getInstance().clearPreferences();
            }
        });
        menuFile.add(this.clearPref);

        this.cleanThumbDB = new JMenuItem("Clean ThumbDB");
        this.cleanThumbDB.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                JIThumbnailService.getInstance().clean();
            }
        });
        menuFile.add(this.cleanThumbDB);


        this.newWorkBench = new JMenuItem("New Work Bench");
        this.newWorkBench.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            }
        });
        workBench.add(this.newWorkBench);

        this.editWorkBench = new JMenuItem("Edit Work Bench");
        this.editWorkBench.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                JIThumbnailService.getInstance().clean();
            }
        });
        workBench.add(this.editWorkBench);


        final ButtonGroup group = new ButtonGroup();
        this.preview = new JCheckBoxMenuItem("Preview");
        this.preview.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                JIPreferences.getInstance().setImagesViewType(JIPreferences.PREVIEW_VIEW);
                JIExplorer.instance().prepareFileList();
            }
        });
        group.add(this.preview);
        menuView.add(this.preview);

        this.thumbnails = new JCheckBoxMenuItem("Thumbnail");
        this.thumbnails.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                JIPreferences.getInstance().setImagesViewType(JIPreferences.THUMB_VIEW);
                JIExplorer.instance().prepareFileList();
            }
        });
        group.add(this.thumbnails);
        menuView.add(this.thumbnails);

        this.detail = new JCheckBoxMenuItem("Detail");
        this.detail.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                JIPreferences.getInstance().setImagesViewType(JIPreferences.DETAIL_VIEW);
                JIExplorer.instance().prepareFileList();
            }
        });
        group.add(this.detail);
        menuView.add(this.detail);

        menuView.addSeparator();

        this.name = new JMenuItem("Name");
        this.name.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                JIPreferences.getInstance().setThumbnailSortOrder(0);
                JIExplorer.instance().resetPreferences();
            }
        });
        menuView.add(this.name);

        this.date = new JMenuItem("Date");
        this.date.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                JIPreferences.getInstance().setThumbnailSortOrder(3);
                JIExplorer.instance().resetPreferences();
            }
        });
        menuView.add(this.date);

        this.size = new JMenuItem("Size");
        this.size.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                JIPreferences.getInstance().setThumbnailSortOrder(2);
                JIExplorer.instance().resetPreferences();
            }
        });
        menuView.add(this.size);

        this.type = new JMenuItem("Type");
        this.type.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                JIPreferences.getInstance().setThumbnailSortOrder(1);
                JIExplorer.instance().resetPreferences();
            }
        });
        menuView.add(this.type);

        menuView.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(final ComponentEvent e) {
                JIExplorerMenuBar.this.detail.setSelected(JIPreferences.getInstance().getImagesViewType().equals(JIPreferences.DETAIL_VIEW));
                JIExplorerMenuBar.this.thumbnails.setSelected(JIPreferences.getInstance().getImagesViewType().equals(JIPreferences.THUMB_VIEW));
                JIExplorerMenuBar.this.preview.setSelected(JIPreferences.getInstance().getImagesViewType().equals(JIPreferences.PREVIEW_VIEW));
            }

        });

        this.help = new JMenuItem("Help");
        this.help.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final HelpDialog helpDialog = new HelpDialog();
                helpDialog.setVisible(true);
            }
        });
        menuHelp.add(this.help);

        // Get an About item instance. Here it's for Swing but there
        // are also AWT variants like getAboutMenuItem().
        final AboutJMenuItem about = app.getAboutJMenuItem();

        about.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                new AboutFrame(JIExplorer.VERSION);
            }
        });

        // If the menu is not already present because it's provided by
        // the OS (like on Mac OS X), then append it to our menu
        if (!AboutJMenuItem.isAutomaticallyPresent()) {
            menuHelp.add(about);
        }

        // Do the same thing for the Preferences and Quit items
        final PreferencesJMenuItem preferences = app.getPreferencesJMenuItem();
        preferences.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                new JIPrefFrame(JIExplorer.instance());
            }
        });
        if (!PreferencesJMenuItem.isAutomaticallyPresent()) {
            menuFile.add(preferences);
        }

        final QuitJMenuItem quit = app.getQuitJMenuItem();
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                JIExplorer.instance().close();
            }
        });
        if (!QuitJMenuItem.isAutomaticallyPresent()) {
            menuFile.addSeparator();
            menuFile.add(quit);
        }
    }
}

