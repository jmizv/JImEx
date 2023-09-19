package de.jmizv.jiexplorer.gui.preferences;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import de.jmizv.jiexplorer.db.JIExplorerDB;

public class JDBCForm extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel thumbnailDBOptions = null;
	private JRadioButton embededHSQLRadioButton = null;
	private JRadioButton jdbcOtherRadioButton = null;
	private JRadioButton embededMySQLRadioButton = null;
	private JRadioButton jdbcMySQLRadioButton = null;
	private JPanel jdbcParamPanel = null;
	private JTextField driverClassNameTextField = null;
	private JLabel driverClassNameLabel = null;
	private JLabel jdbcURLLabel = null;
	private JTextField jdbcURLTextField = null;
	private JTextField userNameTextField = null;
	private JLabel userNameLabel = null;
	private JLabel passwordLabel = null;
	private JPasswordField userPasswordTextField = null;
	public  boolean updated = false;

	private ButtonGroup jdbcGroup;  //  @jve:decl-index=0:


	/**
	 * This is the default constructor
	 */
	public JDBCForm() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		final FlowLayout flowLayout = new FlowLayout();
		flowLayout.setHgap(10);
		flowLayout.setVgap(10);
		this.setLayout(flowLayout);
		this.setBounds(new Rectangle(0, 0, 457, 300));
		final GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = -1;
		gridBagConstraints1.weightx = 0.1D;
		gridBagConstraints1.weighty = 0.1D;
		gridBagConstraints1.gridy = -1;
		this.add(getThumbnailDBOptions(), null);
		this.add(getJdbcParamPanel(), null);
	}

	/**
	 * This method initializes thumbnailDBOptions
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getThumbnailDBOptions() {
		if (this.thumbnailDBOptions == null) {
			this.thumbnailDBOptions = new JPanel();
			this.thumbnailDBOptions.setLayout(new GridBagLayout());
			this.thumbnailDBOptions.setMinimumSize(new Dimension(0, 0));
			this.thumbnailDBOptions.setPreferredSize(new Dimension(430, 90));
			this.thumbnailDBOptions.setBorder(BorderFactory.createTitledBorder(null, "Thumbnail DB", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			this.thumbnailDBOptions.add(getEmbededHSQLRadioButton(), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 30, 0, 0), 0, 0));
			this.thumbnailDBOptions.add(getJdbcOtherRadioButton(), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 30, 0, 0), 0, 0));
			this.thumbnailDBOptions.add(getEmbededMySQLRadioButton(), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 30, 0, 0), 0, 0));
			this.thumbnailDBOptions.add(getJdbcMySQLRadioButton(), new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 30, 0, 0), 0, 0));

			this.jdbcGroup = new ButtonGroup();
			this.jdbcGroup.add(getEmbededHSQLRadioButton());
			this.jdbcGroup.add(getJdbcOtherRadioButton());
			this.jdbcGroup.add(getEmbededMySQLRadioButton());
			this.jdbcGroup.add(getJdbcMySQLRadioButton());
		}
		return this.thumbnailDBOptions;
	}

	/**
	 * This method initializes embededHSQLRadioButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getEmbededHSQLRadioButton() {
		if (this.embededHSQLRadioButton == null) {
			this.embededHSQLRadioButton = new JRadioButton();
			this.embededHSQLRadioButton.setSelected(JIPreferences.getInstance().getDatabaseType().equals("HSQL"));
			this.embededHSQLRadioButton.setText("HSQL embeded");
			this.embededHSQLRadioButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent e) {
					if (JDBCForm.this.embededHSQLRadioButton.isSelected()) {
						JDBCForm.this.driverClassNameTextField.setText(JIExplorerDB.HSQL_JDBC_DRIVER);
						JDBCForm.this.driverClassNameTextField.setEditable(true);
						JDBCForm.this.jdbcURLTextField.setText(JIPreferences.HSQL_CONNECTION_URL);
						JDBCForm.this.jdbcURLTextField.setEditable(true);
						JDBCForm.this.userNameTextField.setText("sa");
						JDBCForm.this.userNameTextField.setEditable(true);
						JDBCForm.this.userPasswordTextField.setText("");
						JDBCForm.this.userPasswordTextField.setEchoChar('*');
					}
				}
			});
		}
		return this.embededHSQLRadioButton;
	}

	/**
	 * This method initializes jdbcOtherRadioButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJdbcOtherRadioButton() {
		if (this.jdbcOtherRadioButton == null) {
			this.jdbcOtherRadioButton = new JRadioButton();
			this.jdbcOtherRadioButton.setEnabled(true);
			this.jdbcOtherRadioButton.setText("Other JDBC");
			this.jdbcOtherRadioButton.setSelected(JIPreferences.getInstance().getDatabaseType().equals("OTHER"));
			this.jdbcOtherRadioButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent e) {
					if (JDBCForm.this.jdbcOtherRadioButton.isSelected()) {
						JDBCForm.this.driverClassNameTextField.setText("");
						JDBCForm.this.driverClassNameTextField.setEditable(false);
						JDBCForm.this.jdbcURLTextField.setText("");
						JDBCForm.this.jdbcURLTextField.setEditable(false);
						JDBCForm.this.userNameTextField.setText("");
						JDBCForm.this.userNameTextField.setEditable(false);
						JDBCForm.this.userPasswordTextField.setText("");

						//								locationLabel.setEnabled(false);
						//								locationTextField.setEnabled(false);
						//								browseButton.setEnabled(false);
						//								locationLabel.setVisible(false);
						//								locationTextField.setVisible(false);
						//								browseButton.setVisible(false);
					}
				}
			});
		}
		return this.jdbcOtherRadioButton;
	}

	/**
	 * This method initializes embededMySQLRadioButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getEmbededMySQLRadioButton() {
		if (this.embededMySQLRadioButton == null) {
			this.embededMySQLRadioButton = new JRadioButton();
			this.embededMySQLRadioButton.setEnabled(false);
			this.embededMySQLRadioButton.setText("MySQL embeded");
		}
		return this.embededMySQLRadioButton;
	}

	/**
	 * This method initializes jdbcMySQLRadioButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getJdbcMySQLRadioButton() {
		if (this.jdbcMySQLRadioButton == null) {
			this.jdbcMySQLRadioButton = new JRadioButton();
			this.jdbcMySQLRadioButton.setSelected(JIPreferences.getInstance().getDatabaseType().equals("MYSQL"));
			this.jdbcMySQLRadioButton.setText("MySQL JDBC");
			this.jdbcMySQLRadioButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent e) {
					if (JDBCForm.this.jdbcMySQLRadioButton.isSelected()) {
						JDBCForm.this.driverClassNameTextField.setText(JIExplorerDB.MYSQL_DRIVER);
						JDBCForm.this.driverClassNameTextField.setEditable(true);
						JDBCForm.this.jdbcURLTextField.setText(JIExplorerDB.CONNECTION_URL);
						JDBCForm.this.jdbcURLTextField.setEditable(true);
						JDBCForm.this.userNameTextField.setText("");
						JDBCForm.this.userNameTextField.setEditable(true);
						JDBCForm.this.userPasswordTextField.setText("");

						//								locationLabel.setEnabled(false);
						//								locationTextField.setEnabled(false);
						//								browseButton.setEnabled(false);
						//								locationLabel.setVisible(false);
						//								locationTextField.setVisible(false);
						//								browseButton.setVisible(false);
					}
				}
			});
		}
		return this.jdbcMySQLRadioButton;
	}

	/**
	 * This method initializes jdbcParamPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJdbcParamPanel() {
		if (this.jdbcParamPanel == null) {
			this.passwordLabel = new JLabel();
			this.passwordLabel.setText("User Password");
			this.userNameLabel = new JLabel();
			this.userNameLabel.setText("User Name");
			this.jdbcURLLabel = new JLabel();
			this.jdbcURLLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			this.jdbcURLLabel.setText("JDBC URL");
			this.jdbcURLLabel.setHorizontalTextPosition(SwingConstants.LEADING);
			this.driverClassNameLabel = new JLabel();
			this.driverClassNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			this.driverClassNameLabel.setText("Driver Class Name");
			this.driverClassNameLabel.setHorizontalTextPosition(SwingConstants.LEADING);
			this.jdbcParamPanel = new JPanel();
			this.jdbcParamPanel.setLayout(new GridBagLayout());
			this.jdbcParamPanel.setPreferredSize(new Dimension(429, 154));
			this.jdbcParamPanel.setBorder(BorderFactory.createTitledBorder(null, "JDBC Preferences", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			this.jdbcParamPanel.add(getDriverClassNameTextField(), new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
			this.jdbcParamPanel.add(this.driverClassNameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 30, 0, 10), 0, 0));
			this.jdbcParamPanel.add(this.jdbcURLLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
			this.jdbcParamPanel.add(getJdbcURLTextField(), new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
			this.jdbcParamPanel.add(getUserNameTextField(), new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
			this.jdbcParamPanel.add(this.userNameLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
			this.jdbcParamPanel.add(this.passwordLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
			this.jdbcParamPanel.add(getUserPasswordTextField(), new GridBagConstraints(1, 4, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
		}
		return this.jdbcParamPanel;
	}

	/**
	 * This method initializes driverClassNameTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getDriverClassNameTextField() {
		if (this.driverClassNameTextField == null) {
			this.driverClassNameTextField = new JTextField();
			this.driverClassNameTextField.setMinimumSize(new Dimension(180, 20));
			this.driverClassNameTextField.setText("com.mysql.jdbc.Driver");
			this.driverClassNameTextField.setEditable(true);
			this.driverClassNameTextField.setPreferredSize(new Dimension(200, 20));
			this.driverClassNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
				@Override
				public void keyTyped(final java.awt.event.KeyEvent e) {
					JDBCForm.this.updated = true;
				}
			});
		}
		return this.driverClassNameTextField;
	}

	/**
	 * This method initializes jdbcURLTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJdbcURLTextField() {
		if (this.jdbcURLTextField == null) {
			this.jdbcURLTextField = new JTextField();
			this.jdbcURLTextField.setMinimumSize(new Dimension(180, 20));
			this.jdbcURLTextField.setText(JIPreferences.getInstance().getJDBCURL());
			this.jdbcURLTextField.setEditable(true);
			this.jdbcURLTextField.setPreferredSize(new Dimension(180, 20));
			this.jdbcURLTextField.addKeyListener(new java.awt.event.KeyAdapter() {
				@Override
				public void keyTyped(final java.awt.event.KeyEvent e) {
					JDBCForm.this.updated = true;
				}
			});
		}
		return this.jdbcURLTextField;
	}

	/**
	 * This method initializes userNameTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getUserNameTextField() {
		if (this.userNameTextField == null) {
			this.userNameTextField = new JTextField();
			this.userNameTextField.setMinimumSize(new Dimension(180, 20));
			this.userNameTextField.setEditable(true);
			this.userNameTextField.setText(JIPreferences.getInstance().getJBDCUserName());
			this.userNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
				@Override
				public void keyTyped(final java.awt.event.KeyEvent e) {
					JDBCForm.this.updated = true;
				}
			});
		}
		return this.userNameTextField;
	}

	/**
	 * This method initializes userPasswordTextField
	 *
	 * @return javax.swing.JPasswordField
	 */
	private JPasswordField getUserPasswordTextField() {
		if (this.userPasswordTextField == null) {
			this.userPasswordTextField = new JPasswordField();
			this.userPasswordTextField.setMinimumSize(new Dimension(180, 20));
			this.userPasswordTextField.setEditable(true);
			this.userPasswordTextField.setText(JIPreferences.getInstance().getJDBCPassword());
			this.userPasswordTextField.addKeyListener(new java.awt.event.KeyAdapter() {
				@Override
				public void keyTyped(final java.awt.event.KeyEvent e) {
					JDBCForm.this.updated = true;
				}
			});
		}
		return this.userPasswordTextField;
	}

	public String getDatabaseType(){
		if (this.embededHSQLRadioButton.isSelected()) {
			return "HSQL";
		}

		if (this.jdbcMySQLRadioButton.isSelected()) {
			return "MYSQL";
		}

		if (this.jdbcOtherRadioButton.isSelected()) {
			return "OTHER";
		}

		return "HSQL";
	}

	public String getJDBCDriver(){
		return this.driverClassNameTextField.getText();
	}

	public String getJDBCURL(){
		return this.jdbcURLTextField.getText();
	}

	public String getJBDCUserName(){
		return this.userNameTextField.getText();
	}

	public String getJDBCPassword(){
		return new String(this.userPasswordTextField.getPassword());
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
