package de.jmizv.jiexplorer.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.util.JIUtility;


public class AboutFrame extends javax.swing.JFrame {

    @Serial
    private static final long serialVersionUID = -5772879081569322933L;
    private JLabel logoLabel;
    private JLabel versionLabel;
    private JButton okButton;
    private JLabel aboutTextLabel;
    private final String version;

    public static void main(final String[] args) {
        final AboutFrame inst = new AboutFrame("1.12");
        inst.setVisible(true);
    }

    public AboutFrame(final String version) {
        super();
        this.version = version;
        setIconImage(JIExplorer.smallIcon.getImage());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                dispose();
            }
        });

        initGUI();
        setVisible(true);
    }

    private void initGUI() {
        final GridBagLayout thisLayout = new GridBagLayout();
        thisLayout.rowWeights = new double[]{0.1, 0.1, 0.1};
        thisLayout.rowHeights = new int[]{7, 7, 7};
        thisLayout.columnWeights = new double[]{0.1, 0.1};
        thisLayout.columnWidths = new int[]{7, 7};
        getContentPane().setLayout(thisLayout);
        this.setIconImage(JIExplorer.smallIcon.getImage());
        this.setTitle("About JIExplorer");
        this.setResizable(false);
        this.setName("aboutDialog");
        {
            this.logoLabel = new JLabel();
            getContentPane().add(this.logoLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            this.logoLabel.setIcon(JIUtility.createImageIcon("images/JIExplorerAbout.jpg"));
        }
        {
            this.aboutTextLabel = new JLabel();
            getContentPane().add(this.aboutTextLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
            this.aboutTextLabel.setText("Version");
        }
        {
            this.versionLabel = new JLabel();
            getContentPane().add(this.versionLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
            this.versionLabel.setText(this.version);
            this.versionLabel.setPreferredSize(new java.awt.Dimension(40, 18));
            this.versionLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        }
        {
            this.okButton = new JButton();
            getContentPane().add(this.okButton, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            this.okButton.setText("OK");
            this.okButton.addActionListener(new ActionListener() {

                public void actionPerformed(final ActionEvent e) {
                    AboutFrame.this.dispose();
                }
            });
        }
        this.setSize(300, 400);
        Dimension screenSize = this.getToolkit().getScreenSize();
        this.setLocation(screenSize.width / 2 - this.getWidth() / 2,
                screenSize.height / 2 - this.getHeight() / 2);

    }

}
