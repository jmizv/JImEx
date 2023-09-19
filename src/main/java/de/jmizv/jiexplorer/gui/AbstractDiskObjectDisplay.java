package de.jmizv.jiexplorer.gui;

import java.awt.Cursor;

import javax.swing.JComponent;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIObservable;


public abstract class AbstractDiskObjectDisplay extends JComponent implements DiskObjectDisplay {

	public void update(final JIObservable o, final Object arg) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		final DiskObject[] list_selection = JIExplorer.instance().getContext().getSelectedDiskObjects();
		final int index = JIExplorer.instance().getContext().getLastSelectedDiskObjectIndex() > -1?JIExplorer.instance().getContext().getLastSelectedDiskObjectIndex():0;

			display(list_selection[index]);

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
