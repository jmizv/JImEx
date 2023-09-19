/*
 * Created on Apr 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.jmizv.jiexplorer.gui.dnd;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;


public class TransferActionListener implements ActionListener, PropertyChangeListener {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TransferActionListener.class);
	private JComponent focusOwner = null;

	public TransferActionListener() {
		final KeyboardFocusManager manager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		manager.addPropertyChangeListener("permanentFocusOwner", this);
	}

	public void propertyChange(final PropertyChangeEvent e) {
		//log.debug("propertyChange " + e);
		final Object o = e.getNewValue();
		if (o instanceof JComponent) {
			this.focusOwner = (JComponent) o;
		} else {
			this.focusOwner = null;
		}
	}

	public void actionPerformed(final ActionEvent e) {
		//log.debug("actionPerformed " + e);
		if (this.focusOwner == null) {
			return;
		}

		final String action = e.getActionCommand();
		//log.debug("getActionCommand " + action);

		//focusOwner.notifyAll();
		final Action a = this.focusOwner.getActionMap().get(action);
		final ActionMap actionMap = this.focusOwner.getActionMap();
		final Object[] keys = actionMap.keys();
		//for (int i=0; i<keys.length;i++)
			//log.debug("ActionMap Key " + keys[i]);

		if (a != null) {
			a.actionPerformed(new ActionEvent(this.focusOwner, ActionEvent.ACTION_PERFORMED, null));
		}
	}
}

