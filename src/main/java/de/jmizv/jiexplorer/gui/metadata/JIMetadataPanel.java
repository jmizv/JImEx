package de.jmizv.jiexplorer.gui.metadata;

import it.tidalwave.imageio.tiff.IFD;

import java.awt.BorderLayout;
import java.io.Serial;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.drew.metadata.Directory;

public class JIMetadataPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;

    private JTable metadataTable;

    private AbstractTableModel mdModel;

    public JIMetadataPanel() {
        super();
    }

    public JIMetadataPanel(final Directory directory) {
        super();
        this.mdModel = new JIMetadataTableModel(directory);
        initGUI(directory.getName());
    }

    public JIMetadataPanel(final IFD directory) {
        super();
        this.mdModel = new JIIFDMetadataTableModel(directory);
        initGUI("IFD");
    }

    public void displayMetadata(final Directory directory) {
        removeAll();

        this.mdModel = new JIMetadataTableModel(directory);
        initGUI(directory.getName());
    }

    public void displayMetadata(final IFD directory) {
        removeAll();

        this.mdModel = new JIIFDMetadataTableModel(directory);
        initGUI("IFD");
    }

    private void initGUI(final String metadataType) {
        setLayout(new BorderLayout());

        this.metadataTable = new JTable(this.mdModel);
        add(new JScrollPane(this.metadataTable), BorderLayout.CENTER);
    }
}
