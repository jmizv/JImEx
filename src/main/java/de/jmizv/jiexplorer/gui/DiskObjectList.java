package de.jmizv.jiexplorer.gui;

import java.io.File;

import javax.swing.JComponent;

import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIExplorerContext;
import de.jmizv.jiexplorer.util.JIObservable;


public interface DiskObjectList extends JIObservable {
	public JIFileModel getFileListModel();
	public void loadDirectory(File dir);
	public void addFile(File file);
	public void reload() ;
	public void resetPreferences() ;
	public JIExplorerContext getReloadContext();
	public void setReloadContext(JIExplorerContext reloadContext);
	public int[] getSelectedIndices();
	public void restoreSelection(DiskObject lastSelectedDObj);
	public JComponent getRendererComponent();
}
