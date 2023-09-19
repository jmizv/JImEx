package de.jmizv.jiexplorer.gui.preferences;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ThumbnailForm extends javax.swing.JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -2982343462366184860L;
	private JPanel thumbDisplayPanel;
	private JLabel thumbSizeLabel;
	private JLabel xLabel;
	private JTextField tfThumbHeight;
	private JTextField tfThumbWidth;
	private JSlider jSliderIconWidth;
	private JSlider jSliderIconHeight;

	private JPanel sortPanel;
	private JCheckBox cbDescending;

	private JPanel arrangePanel;
	private JRadioButton rbDate;
	private JRadioButton rbSize;
	private JRadioButton rbType;
	private JRadioButton rbName;
	private ButtonGroup arrangeGroup;

	private JPanel otherPanel;  //  @jve:decl-index=0:visual-constraint="10,612"
	private JCheckBox cbToolTips;

	private JPanel scrollMode;
	private JRadioButton rbVerticalScroll;
	private JRadioButton rbHorizontalScroll;
	private ButtonGroup scrollGroup;
	private JCheckBox cbShowDetailTableIcons = null;
	private JCheckBox cbLoadAllImageIcons = null;
	private JCheckBox cbLoadImageIconsRecursively = null;



	/**
	 * This method initializes cbToolTips2
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCbToolTips2() {
		if (this.cbShowDetailTableIcons == null) {
			this.cbShowDetailTableIcons = new JCheckBox();
			this.cbShowDetailTableIcons.setText("Show Detail Table Icons");
			this.cbShowDetailTableIcons.setLocation(new Point(10, 42));
			this.cbShowDetailTableIcons.setSize(new Dimension(173, 24));
			this.cbShowDetailTableIcons.setSelected(JIPreferences.getInstance().isShowDetailTableIcon());
		}
		return this.cbShowDetailTableIcons;
	}

	/**
	 * This method initializes cbLoadAllImageIcons
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCbLoadAllImageIcons() {
		if (this.cbLoadAllImageIcons == null) {
			this.cbLoadAllImageIcons = new JCheckBox();
			this.cbLoadAllImageIcons.setText("Process All Dir Image  Icons");
			this.cbLoadAllImageIcons.setSize(new Dimension(200, 24));
			this.cbLoadAllImageIcons.setLocation(new Point(10, 64));
			this.cbLoadAllImageIcons.setActionCommand("Process All Dir Image  Icons");
			this.cbLoadAllImageIcons.setSize(new Dimension(187, 24));
			this.cbLoadAllImageIcons.setSelected(JIPreferences.getInstance().isLoadAllImageIcons());
		}
		return this.cbLoadAllImageIcons;
	}

	/**
	 * This method initializes cbLoadImageIconsRecursively
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCbLoadImageIconsRecursively() {
		if (this.cbLoadImageIconsRecursively == null) {
			this.cbLoadImageIconsRecursively = new JCheckBox();
			this.cbLoadImageIconsRecursively.setText("Load Icons Recursively");
			this.cbLoadImageIconsRecursively.setSize(new Dimension(202, 24));
			this.cbLoadImageIconsRecursively.setLocation(new Point(10, 86));
			this.cbLoadImageIconsRecursively.setActionCommand("Load Icons Recursively");
			this.cbLoadImageIconsRecursively.setSize(new Dimension(186, 24));
			this.cbLoadImageIconsRecursively.setSelected(JIPreferences.getInstance().isLoadIconsRecursively());
		}
		return this.cbLoadImageIconsRecursively;
	}

	/**
	* Auto-generated main method to display this
	* JPanel inside a new JFrame.
	*/
	public static void main(final String[] args) {
		final JFrame frame = new JFrame();
		frame.getContentPane().add(new ThumbnailForm());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	public ThumbnailForm() {
		super();
		initGUI();

		this.tfThumbWidth.setText(String.valueOf(this.jSliderIconWidth.getValue()));
		this.tfThumbHeight.setText(String.valueOf(this.jSliderIconHeight.getValue()));
	}

	private void initGUI() {
		try {

			this.setLayout(null);
			this.setPreferredSize(new java.awt.Dimension(512, 358));
			this.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentShown(final ComponentEvent evt) {
					ThumbnailForm.this.tfThumbWidth.setText(String.valueOf(ThumbnailForm.this.jSliderIconWidth.getValue()));
					ThumbnailForm.this.tfThumbHeight.setText(String.valueOf(ThumbnailForm.this.jSliderIconHeight.getValue()));
					ThumbnailForm.this.thumbDisplayPanel.repaint();
				}
			});

			this.thumbDisplayPanel = new JPanel() {
				/**
				 *
				 */
				private static final long serialVersionUID = 2892838683834063793L;

				@Override
				public void paint(final Graphics g) {
					final java.awt.Graphics2D g2d = (java.awt.Graphics2D)g;

					g2d.setColor(java.awt.Color.WHITE);
					g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

					final int x = (this.getWidth() - ThumbnailForm.this.jSliderIconWidth.getValue())/2;
					final int y = (this.getHeight() - ThumbnailForm.this.jSliderIconHeight.getValue())/2;
					g2d.setColor(java.awt.Color.DARK_GRAY);
					g2d.fillRect(x, y, ThumbnailForm.this.jSliderIconWidth.getValue(), ThumbnailForm.this.jSliderIconHeight.getValue());
					g2d.dispose();
				}
			};
			final GridBagLayout thumbDisplayPanelLayout = new GridBagLayout();
			this.add(this.thumbDisplayPanel);
			this.thumbDisplayPanel.setBackground(Color.white);
			this.thumbDisplayPanel.setLayout(thumbDisplayPanelLayout);
			this.thumbDisplayPanel.setBounds(10, 40, 220, 220);

			this.jSliderIconWidth = new JSlider();
			this.add(this.jSliderIconWidth);
			this.jSliderIconWidth.setMaximum(200);
			this.jSliderIconWidth.setMinimum(70);
			this.jSliderIconWidth.setInverted(true);
			this.jSliderIconWidth.setMajorTickSpacing(30);
			this.jSliderIconWidth.setMinorTickSpacing(10);
			this.jSliderIconWidth.setValue(JIPreferences.getInstance().getIconDim().width);
			this.jSliderIconWidth.setPaintLabels(true);
			this.jSliderIconWidth.setPaintTicks(true);
			this.jSliderIconWidth.setMaximumSize(new java.awt.Dimension(200,46));
			this.jSliderIconWidth.setMinimumSize(new java.awt.Dimension(200,46));
			this.jSliderIconWidth.setToolTipText("Thumbnail Width");
			this.jSliderIconWidth.setBounds(20, 260, 200, 47);
			this.jSliderIconWidth.addChangeListener(new ChangeListener() {
				public void stateChanged(final ChangeEvent evt) {
					ThumbnailForm.this.tfThumbWidth.setText(String.valueOf(ThumbnailForm.this.jSliderIconWidth.getValue()));
					ThumbnailForm.this.tfThumbHeight.setText(String.valueOf(ThumbnailForm.this.jSliderIconHeight.getValue()));
					ThumbnailForm.this.thumbDisplayPanel.repaint();
				}
			});

			this.jSliderIconHeight = new JSlider();
			this.add(this.jSliderIconHeight);
			this.jSliderIconHeight.setMaximum(200);
			this.jSliderIconHeight.setOrientation(SwingConstants.VERTICAL);
			this.jSliderIconHeight.setMinimum(70);
			this.jSliderIconHeight.setMajorTickSpacing(30);
			this.jSliderIconHeight.setMinorTickSpacing(10);
			this.jSliderIconHeight.setValue(JIPreferences.getInstance().getIconDim().height);
			this.jSliderIconHeight.setPaintLabels(true);
			this.jSliderIconHeight.setPaintTicks(true);
			this.jSliderIconHeight.setBounds(230, 50, 50, 200);
			this.jSliderIconHeight.addChangeListener(new ChangeListener() {
				public void stateChanged(final ChangeEvent evt) {
					ThumbnailForm.this.tfThumbWidth.setText(String.valueOf(ThumbnailForm.this.jSliderIconWidth.getValue()));
					ThumbnailForm.this.tfThumbHeight.setText(String.valueOf(ThumbnailForm.this.jSliderIconHeight.getValue()));
					ThumbnailForm.this.thumbDisplayPanel.repaint();
				}
			});

			this.thumbSizeLabel = new JLabel();
			this.add(this.thumbSizeLabel);
			this.thumbSizeLabel.setText("Thumbnail Size:");
			this.thumbSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			this.thumbSizeLabel.setBounds(10, 10, 110, 20);

			this.scrollMode = new JPanel();
			this.add(this.scrollMode);
			this.scrollMode.setMinimumSize(new java.awt.Dimension(40, 40));
			this.scrollMode.setBorder(BorderFactory.createTitledBorder(null, "Thumbnail Scroll Mode", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			this.scrollMode.setLayout(null);
			this.scrollMode.setLocation(new Point(290, 0));
			this.scrollMode.setSize(new Dimension(200, 70));
			this.rbHorizontalScroll = new JRadioButton();
			this.scrollMode.add(this.rbHorizontalScroll);
			this.rbHorizontalScroll.setText("Horizontal Scrolling");
			this.rbHorizontalScroll.setSelected((JIPreferences.getInstance().getThumbnailScrollMode() == 1)
					? true
							: false);
			this.rbHorizontalScroll.setBounds(10, 20, 160, 23);

			this.rbVerticalScroll = new JRadioButton();
			this.scrollMode.add(this.rbVerticalScroll);
			this.rbVerticalScroll.setText("Vertical Scrolling");
			this.rbVerticalScroll.setSelected((JIPreferences.getInstance().getThumbnailScrollMode() == 2)
					? true
							: false);
			this.rbVerticalScroll.setBounds(10, 40, 160, 23);

			this.scrollGroup = new ButtonGroup();
			this.scrollGroup.add(this.rbHorizontalScroll);
			this.scrollGroup.add(this.rbVerticalScroll);

			this.otherPanel = new JPanel();
			this.add(this.otherPanel);
			this.otherPanel.setBorder(BorderFactory.createTitledBorder(null, "Other", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			this.otherPanel.setLayout(null);
			this.otherPanel.setSize(new Dimension(200, 118));
			this.otherPanel.setLocation(new Point(290, 198));

			this.cbToolTips = new JCheckBox();
			this.otherPanel.add(this.cbToolTips);
			this.otherPanel.add(getCbToolTips2(), null);
			this.otherPanel.add(getCbLoadAllImageIcons(), null);
			this.otherPanel.add(getCbLoadImageIconsRecursively(), null);
			this.cbToolTips.setText("Show Tool Tips");
			this.cbToolTips.setSelected(JIPreferences.getInstance().isThumbnailToolTips());
			this.cbToolTips.setBounds(10, 20, 175, 23);

			this.arrangePanel = new JPanel();
			this.add(this.arrangePanel);
			this.arrangePanel.setBorder(BorderFactory.createTitledBorder(null, "Arrange Thumbnails By", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			this.arrangePanel.setLayout(null);
			this.arrangePanel.setLocation(new Point(290, 70));
			this.arrangePanel.setSize(new Dimension(200, 70));
			this.rbName = new JRadioButton();
			this.arrangePanel.add(this.rbName);
			this.rbName.setText("Name");
			this.rbName.setSelected((JIPreferences.getInstance().getThumbnailSortOrder() == 0)
					? true
							: false);
			this.rbName.setBounds(10, 20, 80, 23);

			this.rbType = new JRadioButton();
			this.arrangePanel.add(this.rbType);
			this.rbType.setText("Type");
			this.rbType.setSelected((JIPreferences.getInstance().getThumbnailSortOrder() == 1)
					? true
							: false);
			this.rbType.setBounds(10, 40, 80, 23);

			this.rbSize = new JRadioButton();
			this.arrangePanel.add(this.rbSize);
			this.rbSize.setText("Size");
			this.rbSize.setSelected((JIPreferences.getInstance().getThumbnailSortOrder() == 3)
					? true
							: false);
			this.rbSize.setBounds(100, 40, 80, 23);

			this.rbDate = new JRadioButton();
			this.arrangePanel.add(this.rbDate);
			this.rbDate.setText("Date");
			this.rbDate.setSelected((JIPreferences.getInstance().getThumbnailSortOrder() == 2)
					? true
							: false);
			this.rbDate.setBounds(100, 20, 80, 23);

			this.arrangeGroup = new ButtonGroup();
			this.arrangeGroup.add(this.rbName);
			this.arrangeGroup.add(this.rbType);
			this.arrangeGroup.add(this.rbSize);
			this.arrangeGroup.add(this.rbDate);

			this.sortPanel = new JPanel();
			this.add(this.sortPanel);
			this.sortPanel.setMinimumSize(new java.awt.Dimension(40, 40));
			this.sortPanel.setBorder(BorderFactory.createTitledBorder(null, "Thumbnail Sort Ascending", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			this.sortPanel.setLayout(null);
			this.sortPanel.setLocation(new Point(290, 144));
			this.sortPanel.setSize(new Dimension(200, 50));
			this.cbDescending = new JCheckBox();
			this.sortPanel.add(this.cbDescending);
			this.cbDescending.setText("Descending");
			this.cbDescending.setSelected(JIPreferences.getInstance().isThumbnailSortDesend());
			this.cbDescending.setBounds(10, 20, 170, 23);


			this.tfThumbWidth = new JTextField();
			this.add(this.tfThumbWidth);
			this.tfThumbWidth.setEditable(false);
			this.tfThumbWidth.setBounds(120, 10, 40, 20);

			this.xLabel = new JLabel();
			this.add(this.xLabel);
			this.xLabel.setText("x");
			this.xLabel.setBounds(170, 10, 12, 20);
			this.xLabel.setHorizontalAlignment(SwingConstants.CENTER);
			this.xLabel.setRequestFocusEnabled(false);

			this.tfThumbHeight = new JTextField();
			this.add(this.tfThumbHeight);
			this.tfThumbHeight.setEditable(false);
			this.tfThumbHeight.setBounds(190, 10, 40, 20);



		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isTSortDesend() {
		return this.cbDescending.isSelected();
	}

	public boolean isTToolTips() {
		return this.cbToolTips.isSelected();
	}

	public int getTSort() {
		final Enumeration buttons = this.arrangeGroup.getElements();
		while (buttons.hasMoreElements()) {
			final AbstractButton ab = (AbstractButton)buttons.nextElement();
			if (ab.isSelected()) {
				if(ab.getText().equals(this.rbName.getText())) {
					return 0;
				}
				if(ab.getText().equals(this.rbType.getText())) {
					return 1;
				}
				if(ab.getText().equals(this.rbSize.getText())) {
					return 2;
				}
				if(ab.getText().equals(this.rbDate.getText())) {
					return 3;
				}
			}
		}
		return 0;
	}

	public int getTScrollMode() {
		final Enumeration buttons = this.scrollGroup.getElements();
		while (buttons.hasMoreElements()) {
			final AbstractButton ab = (AbstractButton)buttons.nextElement();
			if (ab.isSelected()) {
				if(ab.getText().equals(this.rbHorizontalScroll.getText())) {
					return 1;
				}
				if(ab.getText().equals(this.rbVerticalScroll.getText())) {
					return 2;
				}
			}
		}
		return 0;
	}

	public Dimension getIDim() {
		return new Dimension(this.jSliderIconWidth.getValue(), this.jSliderIconHeight.getValue());
	}

	public final boolean isShowDetailTableIcon() {
		return this.cbShowDetailTableIcons.isSelected();
	}

	public final boolean isLoadIconsRecursively() {
		return this.cbLoadImageIconsRecursively.isSelected();
	}

	public final boolean isLoadAllImageIcons() {
		return this.cbLoadAllImageIcons.isSelected();
	}
}
