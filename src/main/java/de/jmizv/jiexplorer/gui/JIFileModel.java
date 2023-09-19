package de.jmizv.jiexplorer.gui;

import de.jmizv.jiexplorer.util.JIExplorerContext;
import de.jmizv.jiexplorer.util.OrderedDiskObjectList;


public interface JIFileModel {
	public void setData() ;
	public void setKeyWordData() ;
	public void setCategoryData();
	public void setDateData();
	public void reload();
	public JIExplorerContext getReloadContext();
	public void setReloadContext(JIExplorerContext reloadContext);
	public OrderedDiskObjectList getDiskObjectList();
	public void sort(int type);
}
