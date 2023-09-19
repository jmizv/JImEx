package de.jmizv.jiexplorer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;

import de.jmizv.jiexplorer.JIExplorer;


public class HelpDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JEditorPane jHelpPane = null;

	private JScrollPane jScrollPane = null;

	/**
	 * @param owner
	 */
	public HelpDialog() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setSize(336, 365);
		this.setIconImage(JIExplorer.smallIcon.getImage());
		this.setTitle("Help");
		this.setName("helpFrame");
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				dispose();
			}
		});

		final Dimension screenSize = this.getToolkit().getScreenSize();
		this.setLocation(screenSize.width/2 - this.getWidth()/2,
				screenSize.height/2 - this.getHeight()/2);

		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (this.jContentPane == null) {
			this.jContentPane = new JPanel();
			this.jContentPane.setLayout(new BorderLayout());
			this.jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
		}
		return this.jContentPane;
	}

	/**
	 * This method initializes jHelpPane
	 *
	 * @return javax.swing.JEditorPane
	 */
	private JEditorPane getJHelpPane() {
		if (this.jHelpPane == null) {
			this.jHelpPane = new JEditorPane();
			this.jHelpPane.setContentType("text/html");
			this.jHelpPane.setText("<html>\n  <head>\n\n  </head>\n  <body>\n    <h3>Image Viewer Command Keys:</h3><table border=\"0\" cellpadding=\"2\" cellspacing=\"4\"><tr><td align=\"right\" valign=\"middle\" nowrap>ESC</td><td align=\"left\" valign=\"middle\" nowrap>&nbsp;</td><td align=\"left\" valign=\"middle\" nowrap>- exit image viewer</td></tr><tr><td align=\"right\" valign=\"middle\" nowrap>Space</td><td align=\"left\" valign=\"middle\" nowrap>&nbsp;</td><td align=\"left\" valign=\"middle\" nowrap>- next image</td></tr><tr><td align=\"right\" valign=\"middle\" nowrap>Enter</td><td align=\"left\" valign=\"middle\" nowrap>&nbsp;</td><td align=\"left\" valign=\"middle\" nowrap>- next image</td></tr><tr><td align=\"right\" valign=\"middle\" nowrap>Right Arrow</td><td align=\"left\" valign=\"middle\" nowrap>&nbsp;</td><td align=\"left\" valign=\"middle\" nowrap>- next image</td></tr><tr><td align=\"right\" valign=\"middle\" nowrap>Left Arrow</td><td align=\"left\" valign=\"middle\" nowrap>&nbsp;</td><td align=\"left\" valign=\"middle\" nowrap>- previous image</td></tr><tr><td align=\"right\" valign=\"middle\" nowrap>Up Arrow</td><td align=\"left\" valign=\"middle\" nowrap>&nbsp;</td><td align=\"left\" valign=\"middle\" nowrap>- zoom in</td></tr><tr><td align=\"right\" valign=\"middle\" nowrap>Down Arrow</td><td align=\"left\" valign=\"middle\" nowrap>&nbsp;</td><td align=\"left\" valign=\"middle\" nowrap>- zoom out</td></tr><tr><td align=\"right\" valign=\"middle\" nowrap>Alt F</td><td align=\"left\" valign=\"middle\" nowrap>&nbsp;</td><td align=\"left\" valign=\"middle\" nowrap>- enter / leave full screen mode</td></tr><tr><td align=\"right\" valign=\"middle\" nowrap>Alt S</td><td align=\"left\" valign=\"middle\" nowrap>&nbsp;</td><td align=\"left\" valign=\"middle\" nowrap>- start / stop slide show</td></tr></table>\n  </body>\n</html>\n");
			this.jHelpPane.setEditable(false);
		}
		return this.jHelpPane;
	}

	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (this.jScrollPane == null) {
			this.jScrollPane = new JScrollPane();
			this.jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			this.jScrollPane.setViewportBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			this.jScrollPane.setViewportView(getJHelpPane());
		}
		return this.jScrollPane;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
