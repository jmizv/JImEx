package de.jmizv.jiexplorer.gui.keyword;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.Serial;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.db.JIThumbnailService;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIUtility;


public class KeyWordsFrame extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;

    private JPanel jContentPane;

    private JPanel selectButtonsPanel;

    private JScrollPane availableKeyWordsScrollPane;

    private JScrollPane selectedKeyWordsScrollPane;

    private JList<String> availableKeyWordsList;

    private JList<String> selectedKeyWordsList;

    private JButton addKeyWordButton;

    private JButton removeKeyWordButton;

    private JPanel addKeyWordsPanel;

    private JTextField newKeyWordsTextField;

    private JButton addNewKeyWordsButton;

    private JPanel buttonPanel;

    private JButton cancelButton;

    private JButton okButton;

    private JCheckBox appendCheckBox;

    /**
     * This is the default constructor
     */
    public KeyWordsFrame() {
        super();
        initialize();
    }

    private void initialize() {
        this.setSize(368, 316);
        this.setIconImage(JIExplorer.smallIcon.getImage());
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setName("keyWords");
        this.setLocationRelativeTo(getParent());
        this.setContentPane(getJContentPane());
        this.setTitle("Key Words");
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (this.jContentPane == null) {
            final GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridwidth = 3;
            gridBagConstraints21.gridy = 3;
            gridBagConstraints21.insets = new Insets(0, 15, 15, 15);
            gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints21.gridx = 0;
            final GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridwidth = 3;
            gridBagConstraints11.gridy = 2;
            gridBagConstraints11.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints11.ipadx = 0;
            gridBagConstraints11.insets = new Insets(15, 15, 15, 15);
            gridBagConstraints11.gridx = 0;
            final GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.fill = GridBagConstraints.BOTH;
            gridBagConstraints1.weighty = 1.0;
            gridBagConstraints1.insets = new Insets(5, 2, 2, 5);
            gridBagConstraints1.weightx = 0.8;
            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new Insets(5, 5, 2, 2);
            gridBagConstraints.weightx = 1.0;
            final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints2.anchor = GridBagConstraints.CENTER;
            gridBagConstraints2.gridx = 1;
            this.jContentPane = new JPanel();
            this.jContentPane.setLayout(new GridBagLayout());
            this.jContentPane.add(getAddKeyWordsPanel(), gridBagConstraints11);
            this.jContentPane.add(getAvailableKeyWordsScrollPane(), gridBagConstraints);
            this.jContentPane.add(getSelectButtonsPanel(), gridBagConstraints2);
            this.jContentPane.add(getSelectedKeyWordsScrollPane(), gridBagConstraints1);
            this.jContentPane.add(getButtonPanel(), gridBagConstraints21);
        }
        return this.jContentPane;
    }

    /**
     * This method initializes selectButtonsPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getSelectButtonsPanel() {
        if (this.selectButtonsPanel == null) {
            final GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.anchor = GridBagConstraints.NORTH;
            gridBagConstraints4.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints4.gridy = 1;
            final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.anchor = GridBagConstraints.SOUTH;
            gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
            gridBagConstraints3.gridy = 0;
            this.selectButtonsPanel = new JPanel();
            this.selectButtonsPanel.setLayout(new GridBagLayout());
            this.selectButtonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            this.selectButtonsPanel.add(getAddKeyWordButton(), gridBagConstraints3);
            this.selectButtonsPanel.add(getRemoveKeyWordButton(), gridBagConstraints4);
        }
        return this.selectButtonsPanel;
    }

    /**
     * This method initializes availableKeyWordsScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getAvailableKeyWordsScrollPane() {
        if (this.availableKeyWordsScrollPane == null) {
            this.availableKeyWordsScrollPane = new JScrollPane();
            this.availableKeyWordsScrollPane.setBorder(BorderFactory.createTitledBorder(null, "Available Key Words", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            this.availableKeyWordsScrollPane.setViewportView(getAvailableKeyWordsList());
        }
        return this.availableKeyWordsScrollPane;
    }

    /**
     * This method initializes selectedKeyWordsScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getSelectedKeyWordsScrollPane() {
        if (this.selectedKeyWordsScrollPane == null) {
            this.selectedKeyWordsScrollPane = new JScrollPane();
            this.selectedKeyWordsScrollPane.setBorder(BorderFactory.createTitledBorder(null, "Selected Key Words", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            this.selectedKeyWordsScrollPane.setViewportView(getSelectedKeyWordsList());
        }
        return this.selectedKeyWordsScrollPane;
    }

    private JList<String> getAvailableKeyWordsList() {
        if (this.availableKeyWordsList == null) {
            this.availableKeyWordsList = new JList<>();
            this.availableKeyWordsList.setModel(new DefaultListModel<>());
            this.availableKeyWordsList.setSize(new Dimension(266, 154));
            this.availableKeyWordsList.setLayoutOrientation(JList.VERTICAL_WRAP);
        }
        return this.availableKeyWordsList;
    }

    private JList<String> getSelectedKeyWordsList() {
        if (this.selectedKeyWordsList == null) {
            this.selectedKeyWordsList = new JList<>();
            this.selectedKeyWordsList.setModel(new DefaultListModel<>());
            this.selectedKeyWordsList.setSize(new Dimension(266, 154));
            this.selectedKeyWordsList.setLayoutOrientation(JList.VERTICAL_WRAP);
        }
        return this.selectedKeyWordsList;
    }

    private JButton getAddKeyWordButton() {
        if (this.addKeyWordButton == null) {
            this.addKeyWordButton = new JButton();
            this.addKeyWordButton.setIcon(JIUtility.createImageIcon("/icons/SingleNext.png"));
            this.addKeyWordButton.setPreferredSize(new Dimension(20, 30));
            this.addKeyWordButton.setPressedIcon(JIUtility.createImageIcon("/icons/SingleNextOver.png"));
            this.addKeyWordButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(final java.awt.event.ActionEvent e) {
                    addActionPerformed(e);
                }
            });
        }
        return this.addKeyWordButton;
    }

    private JButton getRemoveKeyWordButton() {
        if (this.removeKeyWordButton == null) {
            this.removeKeyWordButton = new JButton();
            this.removeKeyWordButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    removeActionPerformed();
                }
            });
            this.removeKeyWordButton.setIcon(JIUtility.createImageIcon("/icons/SinglePrevious.png"));
            this.removeKeyWordButton.setPreferredSize(new Dimension(20, 30));
            this.removeKeyWordButton.setPressedIcon(JIUtility.createImageIcon("/icons/SinglePreviousOver.png"));
        }
        return this.removeKeyWordButton;
    }

    private JPanel getAddKeyWordsPanel() {
        if (this.addKeyWordsPanel == null) {
            final GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridwidth = 1;
            gridBagConstraints10.gridy = 0;
            gridBagConstraints10.gridx = 2;
            final GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.insets = new Insets(0, 5, 0, 5);
            gridBagConstraints7.gridy = 1;
            gridBagConstraints7.gridx = 2;
            final GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.insets = new Insets(0, 0, 0, 5);
            gridBagConstraints6.gridy = 1;
            gridBagConstraints6.gridx = 0;
            final GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.fill = GridBagConstraints.BOTH;
            gridBagConstraints5.insets = new Insets(2, 5, 2, 5);
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.weightx = 1.0;
            JLabel newKeyWordsLabel = new JLabel();
            newKeyWordsLabel.setText("Key Word:");
            this.addKeyWordsPanel = new JPanel();
            this.addKeyWordsPanel.setLayout(new GridBagLayout());
            this.addKeyWordsPanel.add(newKeyWordsLabel, gridBagConstraints6);
            this.addKeyWordsPanel.add(getNewKeyWordsTextField(), gridBagConstraints5);
            this.addKeyWordsPanel.add(getAddNewKeyWordsButton(), gridBagConstraints7);
            this.addKeyWordsPanel.add(getAppendCheckBox(), gridBagConstraints10);
        }
        return this.addKeyWordsPanel;
    }

    private JTextField getNewKeyWordsTextField() {
        if (this.newKeyWordsTextField == null) {
            this.newKeyWordsTextField = new JTextField();
            this.newKeyWordsTextField.setPreferredSize(new Dimension(100, 20));
            this.newKeyWordsTextField.setColumns(20);
        }
        return this.newKeyWordsTextField;
    }

    private JButton getAddNewKeyWordsButton() {
        if (this.addNewKeyWordsButton == null) {
            this.addNewKeyWordsButton = new JButton();
            this.addNewKeyWordsButton.setText("Add");
            this.addNewKeyWordsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(final java.awt.event.ActionEvent e) {
                    addKeyWordActionPerformed();
                }
            });
        }
        return this.addNewKeyWordsButton;
    }

    private JPanel getButtonPanel() {
        if (this.buttonPanel == null) {
            final GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.weightx = 0.1;
            gridBagConstraints9.gridy = 0;
            final GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.gridx = 1;
            gridBagConstraints8.weightx = 0.1;
            gridBagConstraints8.gridy = 0;
            this.buttonPanel = new JPanel();
            this.buttonPanel.setLayout(new GridBagLayout());
            this.buttonPanel.add(getCancelButton(), gridBagConstraints8);
            this.buttonPanel.add(getOkButton(), gridBagConstraints9);
        }
        return this.buttonPanel;
    }

    private JButton getCancelButton() {
        if (this.cancelButton == null) {
            this.cancelButton = new JButton();
            this.cancelButton.setText("Close");
            this.cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(final java.awt.event.ActionEvent e) {
                    butCancelActionPerformed();
                }
            });
        }
        return this.cancelButton;
    }

    private JButton getOkButton() {
        if (this.okButton == null) {
            this.okButton = new JButton();
            this.okButton.setText("Save");
            this.okButton.setPreferredSize(new Dimension(73, 26));
            this.okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(final java.awt.event.ActionEvent e) {
                    butSaveActionPerformed();
                }
            });
        }
        return this.okButton;
    }

    private DiskObject[] getImages() {
        return JIExplorer.instance().getContext().getSelectedDiskObjects();
    }

    private void removeActionPerformed() {
        List<String> selectedStrings = this.selectedKeyWordsList.getSelectedValuesList();
        DefaultListModel<String> availListModel = (DefaultListModel<String>) availableKeyWordsList.getModel();
        DefaultListModel<String> imageListModel = (DefaultListModel<String>) selectedKeyWordsList.getModel();

        for (String element : selectedStrings) {
            availListModel.addElement(element);
            imageListModel.removeElement(element);
        }
    }

    private void addActionPerformed(final ActionEvent evt) {
        List<String> selectedStrings = availableKeyWordsList.getSelectedValuesList();
        DefaultListModel<String> availListModel = (DefaultListModel<String>) availableKeyWordsList.getModel();
        DefaultListModel<String> imageListModel = (DefaultListModel<String>) selectedKeyWordsList.getModel();

        for (String element : selectedStrings) {
            imageListModel.addElement(element);
            availListModel.removeElement(element);
        }
    }

    private void butCancelActionPerformed() {
        dispose();
    }


    private void butSaveActionPerformed() {
        final DefaultListModel<String> imageListModel = (DefaultListModel<String>) this.selectedKeyWordsList.getModel();
        final Enumeration<String> strEnum = imageListModel.elements();
        JIThumbnailService.getInstance().insertKeyWordsForImage(getImages(), strEnum, this.appendCheckBox.isSelected());
        dispose();
    }

    private void addKeyWordActionPerformed() {
        final String[] keyWords = this.newKeyWordsTextField.getText().trim().toLowerCase().split(",");
        final DefaultListModel<String> imageListModel = (DefaultListModel<String>) this.selectedKeyWordsList.getModel();
        for (final String element : keyWords) {
            if ((element != null) && !element.trim().isEmpty()) {
                JIThumbnailService.getInstance().insertNewKeyWord(element.trim());
                imageListModel.addElement(element.trim());
            }
        }
        this.newKeyWordsTextField.setText("");
    }

    protected DefaultListModel<String> getKeyWordList(DefaultListModel<String> dlm) {
        return JIThumbnailService.getInstance().getKeyWords(getImages(), dlm);
    }

    protected DefaultListModel<String> getImageKeyWords(final DefaultListModel<String> dlm) {
        return JIThumbnailService.getInstance().getKeyWordsForImage(getImages(), dlm);
    }

    public void loadListModels() {
        this.selectedKeyWordsList.setModel(getImageKeyWords(new DefaultListModel<>()));
        this.availableKeyWordsList.setModel(getKeyWordList(new DefaultListModel<>()));
    }

    private JCheckBox getAppendCheckBox() {
        if (this.appendCheckBox == null) {
            this.appendCheckBox = new JCheckBox();
            this.appendCheckBox.setText("Append");
            this.appendCheckBox.setName("appendCheckBox");
            this.appendCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
            this.appendCheckBox.setHorizontalAlignment(SwingConstants.RIGHT);
            this.appendCheckBox.setMnemonic(KeyEvent.VK_UNDEFINED);
            this.appendCheckBox.setSelected(true);
        }
        return this.appendCheckBox;
    }
}