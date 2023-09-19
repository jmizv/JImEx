package de.jmizv.jiexplorer.gui;

import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIObserver;

public interface DiskObjectDisplay extends JIObserver  {

	public void display(DiskObject dObj);
}
