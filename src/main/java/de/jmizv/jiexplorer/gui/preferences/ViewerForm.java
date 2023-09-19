package de.jmizv.jiexplorer.gui.preferences;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Point;


public class ViewerForm extends javax.swing.JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -8271729178875607440L;
	private JPanel sequencePanel;
	private JRadioButton rbRandom;
	private JRadioButton rbReverse;
	private JRadioButton rbForward;
	private ButtonGroup sequenceGroup;

	private JPanel windowsAttributePanel;
	private JCheckBox cbWinFitScreen;
	private JCheckBox cbShowFullPath;
	private JCheckBox cbSaveWinPos;
	private JCheckBox cbCenterWin;

	private JPanel imageAutoSizePanel;
	private JCheckBox cbImgZoom2Fit;

	private JPanel viewerPanel;
	private JCheckBox cbOnTop;
	private JCheckBox cbFullScreenNoMouse;

	private JPanel slideShowDelayPanel;
	private JLabel ssSecondsLabel;
	private JTextArea tDelay;
	private JSlider slDelay;
	/**
	* Auto-generated main method to display this
	* JPanel inside a new JFrame.
	*/
	public static void main(final String[] args) {
		final JFrame frame = new JFrame();
		frame.getContentPane().add(new ViewerForm());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	public ViewerForm() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {

			this.setLayout(null);
			this.setPreferredSize(new java.awt.Dimension(490, 315));
			this.setAutoscrolls(true);

			this.slideShowDelayPanel = new JPanel();
			this.add(this.slideShowDelayPanel);
			this.slideShowDelayPanel.setMinimumSize(new Dimension(40,40));
			this.slideShowDelayPanel.setBorder(BorderFactory.createTitledBorder(null, "Slide Show Delay", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			this.slideShowDelayPanel.setLayout(null);
			this.slideShowDelayPanel.setLocation(new Point(10, 150));
			this.slideShowDelayPanel.setSize(new Dimension(238, 90));
			this.slDelay = new JSlider();
			this.slideShowDelayPanel.add(this.slDelay);
			this.slDelay.setValue(JIPreferences.getInstance().getSlideShowDelay());
			this.slDelay.setMaximum(241);
			this.slDelay.setMinimum(1);
			this.slDelay.setMajorTickSpacing(60);
			this.slDelay.setMinorTickSpacing(20);
			this.slDelay.setPaintTicks(true);
			this.slDelay.setBounds(10, 20, 220, 32);
			this.slDelay.addChangeListener(new ChangeListener() {
				public void stateChanged(final ChangeEvent evt) {
					ViewerForm.this.tDelay.setText(String.valueOf(ViewerForm.this.slDelay.getValue()));
				}
			});

			this.tDelay = new JTextArea();
			this.slideShowDelayPanel.add(this.tDelay);
			this.tDelay.setText(String.valueOf(this.slDelay.getValue()));
			this.tDelay.setBounds(20, 60, 70, 17);
			this.tDelay.addCaretListener(new CaretListener() {
				public void caretUpdate(final CaretEvent evt) {
					try {
						if ((ViewerForm.this.tDelay.getText() != null) && (ViewerForm.this.tDelay.getText().trim().length() > 0)) {
							final int value = Integer.parseInt(ViewerForm.this.tDelay.getText().trim());
							if (ViewerForm.this.slDelay.getValue() != value) {
								final int slMin = ViewerForm.this.slDelay.getMinimum();
								final int slMax = ViewerForm.this.slDelay.getMaximum();
								ViewerForm.this.slDelay.setValue(value<slMin?slMin:(value>slMax?slMax:value));
							}
						}
					} catch (final Exception exp) {}
				}
			});

			this.ssSecondsLabel = new JLabel();
			this.slideShowDelayPanel.add(this.ssSecondsLabel);
			this.ssSecondsLabel.setText("seconds");
			this.ssSecondsLabel.setBounds(100, 60, 100, 15);

			this.viewerPanel = new JPanel();
			this.add(this.viewerPanel);
			this.viewerPanel.setMinimumSize(new java.awt.Dimension(40, 40));
			this.viewerPanel.setBorder(BorderFactory.createTitledBorder(null, "Image Viewer", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			this.viewerPanel.setLayout(null);
			this.viewerPanel.setBounds(259, 189, 210, 77);

			this.cbFullScreenNoMouse = new JCheckBox();
			this.viewerPanel.add(this.cbFullScreenNoMouse);
			this.cbFullScreenNoMouse.setText("Full Screen Hide Mouse");
			this.cbFullScreenNoMouse.setSelected(JIPreferences.getInstance().isImageViewerFullScreenHideMouse());
			this.cbFullScreenNoMouse.setBounds(7, 21, 189, 21);

			this.cbOnTop = new JCheckBox();
			this.viewerPanel.add(this.cbOnTop);
			this.cbOnTop.setText("Always On Top");
			this.cbOnTop.setSelected(JIPreferences.getInstance().isImageViewerOnTop());
			this.cbOnTop.setBounds(7, 42, 168, 21);

			this.imageAutoSizePanel = new JPanel();
			this.add(this.imageAutoSizePanel);
			this.imageAutoSizePanel.setBorder(BorderFactory.createTitledBorder(null, "Image Auto Size", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			this.imageAutoSizePanel.setLayout(null);
			this.imageAutoSizePanel.setBounds(259, 119, 210, 56);

			this.cbImgZoom2Fit = new JCheckBox();
			this.imageAutoSizePanel.add(this.cbImgZoom2Fit);
			this.cbImgZoom2Fit.setText("Zoom to fit window");
			this.cbImgZoom2Fit.setSelected(JIPreferences.getInstance().isImageViewerZoomFitWin());
			this.cbImgZoom2Fit.setBounds(10, 20, 170, 23);

			this.windowsAttributePanel = new JPanel();
			this.add(this.windowsAttributePanel);
			this.windowsAttributePanel.setBorder(BorderFactory.createTitledBorder(null, "Window Attributes", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			this.windowsAttributePanel.setLayout(null);
			this.windowsAttributePanel.setSize(new Dimension(238, 133));
			this.windowsAttributePanel.setLocation(new Point(10, 10));
			this.cbCenterWin = new JCheckBox();
			this.windowsAttributePanel.add(this.cbCenterWin);
			this.cbCenterWin.setText("Center window ");
			this.cbCenterWin.setSelected(JIPreferences.getInstance().isImageViewerCenterWin());
			this.cbCenterWin.setBounds(7, 28, 160, 21);

			this.cbSaveWinPos = new JCheckBox();
			this.windowsAttributePanel.add(this.cbSaveWinPos);
			this.cbSaveWinPos.setText("Save window pos.");
			this.cbSaveWinPos.setSelected(JIPreferences.getInstance().isImageViewerSaveWinPos());
			this.cbSaveWinPos.setBounds(7, 49, 160, 21);

			this.cbShowFullPath = new JCheckBox();
			this.windowsAttributePanel.add(this.cbShowFullPath);
			this.cbShowFullPath.setText("Show full image path");
			this.cbShowFullPath.setSelected(JIPreferences.getInstance().isImageViewerShowFullPath());
			this.cbShowFullPath.setBounds(7, 70, 160, 21);

			this.cbWinFitScreen = new JCheckBox();
			this.windowsAttributePanel.add(this.cbWinFitScreen);
			this.cbWinFitScreen.setText("Window fill screen");
			this.cbWinFitScreen.setSelected(JIPreferences.getInstance().isImageViewerWinFitScreen());
			this.cbWinFitScreen.setBounds(7, 91, 160, 21);

			this.sequencePanel = new JPanel();
			this.add(this.sequencePanel);
			this.sequencePanel.setBorder(BorderFactory.createTitledBorder(null, "Slide Show Sequence", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			this.sequencePanel.setLayout(null);
			this.sequencePanel.setSize(new Dimension(210, 100));
			this.sequencePanel.setLocation(new Point(259, 10));
			this.rbForward = new JRadioButton();
			this.sequencePanel.add(this.rbForward);
			this.rbForward.setText("Forward");
			this.rbForward.setSelected((JIPreferences.getInstance().getSlideShowDirection() == 0)
					? true
							: false);
			this.rbForward.setBounds(10, 20, 100, 23);


			this.rbReverse = new JRadioButton();
			this.sequencePanel.add(this.rbReverse);
			this.rbReverse.setText("Reverse");
			this.rbReverse.setSelected((JIPreferences.getInstance().getSlideShowDirection() == 1)
					? true
							: false);
			this.rbReverse.setBounds(10, 40, 90, 23);


			this.rbRandom = new JRadioButton();
			this.sequencePanel.add(this.rbRandom);
			this.rbRandom.setText("Random");
			this.rbRandom.setSelected((JIPreferences.getInstance().getSlideShowDirection() == 2)
					? true
							: false);
			this.rbRandom.setBounds(10, 60, 100, 23);

			this.sequenceGroup = new ButtonGroup();
			this.sequenceGroup.add(this.rbForward);
			this.sequenceGroup.add(this.rbReverse);
			this.sequenceGroup.add(this.rbRandom);


		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public int getSlideShowDirection() {
		final Enumeration buttons = this.sequenceGroup.getElements();
		while (buttons.hasMoreElements()) {
			final AbstractButton ab = (AbstractButton)buttons.nextElement();
			if (ab.isSelected()) {
				if(ab.getText().equals(this.rbForward.getText())) {
					return 0;
				}
				if(ab.getText().equals(this.rbReverse.getText())) {
					return 1;
				}
				if(ab.getText().equals(this.rbRandom.getText())) {
					return 2;
				}
			}
		}
		return 0;
	}
	public int getSlideShowDelay() {
		return this.slDelay.getValue();
	}
	public boolean isImageViewerWinFitScreen() {
		return this.cbWinFitScreen.isSelected();
	}
	public boolean isImageViewerShowFullPath() {
		return this.cbShowFullPath.isSelected();
	}
	public boolean isImageViewerSaveWinPos() {
		return this.cbSaveWinPos.isSelected();
	}
	public boolean isImageViewerCenterWin() {
		return this.cbCenterWin.isSelected();
	}
	public boolean isImageViewerZoomFitWin() {
		return this.cbImgZoom2Fit.isSelected();
	}
	public boolean isImageViewerOnTop() {
		return this.cbOnTop.isSelected();
	}
	public boolean isImageViewerFullScreenHideMouse() {
		return this.cbFullScreenNoMouse.isSelected();
	}
}
