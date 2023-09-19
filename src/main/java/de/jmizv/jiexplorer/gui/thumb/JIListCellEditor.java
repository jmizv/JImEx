package de.jmizv.jiexplorer.gui.thumb;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;



public class JIListCellEditor extends JWindow {
	/**
	 *
	 */
	private static final long serialVersionUID = 3452035038145970463L;

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JIThumbnailList.class);

	@SuppressWarnings("unused")
	private String name;
	private final JTextField textField;
	private final JIThumbnailList list;

	public JIListCellEditor(final JIThumbnailList list) {
		super(list.getFrame());
		this.list = list;
		this.textField = new JTextField();
		getContentPane().add(this.textField,BorderLayout.CENTER);
		setVisible(false);
		this.textField.addKeyListener(new TextField_KeyAdapter(this));
		this.textField.addFocusListener(new TextField_FocusAdapter(this));
	}

	public void textFieldKeyTyped(final KeyEvent e) {
		log.debug("KeyAdapter-keyTyped "+e);
		if(e.getKeyChar() == (char)KeyEvent.VK_ENTER) {
			this.list.editingStopped(this.textField.getText());
			setVisible(false);
		}
	}

	public void textFieldFocusLost(final FocusEvent e) {
		log.debug("FocusAdapter-focusLost "+e);
		this.list.editingCanceled();
	}

	public void edit(final String name,final Rectangle rec) {
		this.name = name;
		this.textField.setText(name);
		this.textField.setBounds(0,0,rec.width,rec.height);
		setBounds(rec);
		setVisible(true);
		compositeRequestFocus(this.textField);
	}

	static boolean compositeRequestFocus(final Component component) {
		if (component instanceof Container) {
			final Container container = (Container)component;
			if (container.isFocusCycleRoot()) {
				final FocusTraversalPolicy policy = container.getFocusTraversalPolicy();
				final Component comp = policy.getDefaultComponent(container);
				if (comp!=null) {
					comp.requestFocus();
					return true;
				}
			}
			final Container rootAncestor = container.getFocusCycleRootAncestor();
			if (rootAncestor!=null) {
				final FocusTraversalPolicy policy = rootAncestor.getFocusTraversalPolicy();
				final Component comp = policy.getComponentAfter(rootAncestor, container);

				if ((comp!=null) && SwingUtilities.isDescendingFrom(comp, container)) {
					comp.requestFocus();
					return true;
				}
			}
		}
		if (component.isFocusable()) {
			component.requestFocus();
			return true;
		}
		return false;
	}

}

class TextField_KeyAdapter extends KeyAdapter {
	JIListCellEditor adaptee;

	public TextField_KeyAdapter(final JIListCellEditor adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void keyTyped(final KeyEvent e) {
		this.adaptee.textFieldKeyTyped(e);
	}
}

class TextField_FocusAdapter extends FocusAdapter {
	JIListCellEditor adaptee;

	public TextField_FocusAdapter(final JIListCellEditor adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void focusLost(final FocusEvent e) {
		this.adaptee.textFieldFocusLost(e);
	}
}

