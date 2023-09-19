package de.jmizv.jiexplorer.filetask;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import de.jmizv.jiexplorer.util.SpringUtilities;
import de.jmizv.jiexplorer.util.StockDialogs;



public class ProgressDialog extends JDialog implements FileTaskListener {
    /**
	 *
	 */
	private static final long serialVersionUID = -901382204259819832L;

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ProgressDialog.class);

    private FileTask fileTask = null;
    private JLabel messageLabel1 = null;
    private JLabel messageLabel = null;
    private JProgressBar progressBar = null;
    private JButton cancelButton = null;
    private String completionMessage = null;
    public ProgressDialog(final Frame parent, final String title, final FileTask task, final String message) {
        super(parent, title, true);
        //super(title);
        this.fileTask = task;
        initGUI(parent, message);
    }

    public ProgressDialog(final Dialog parent, final String title, final FileTask task, final String message) {
        super(parent, title, true);
        //super(title);
        this.fileTask = task;
        initGUI(parent, message);
    }

    private void initGUI(final Component parent, String message) {
    	setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        final FontMetrics fontmetrics = this.getFontMetrics(this.getFont());
        final int width = fontmetrics.stringWidth("From "+this.fileTask.getDestinationFolder().getPath());


    	final JPanel panel = new JPanel();
    	panel.setLayout(new SpringLayout());
    	panel.setBorder(new EmptyBorder(5,5,5,5));

        if (message == null) {
			message = "Please wait...";
		}
        this.messageLabel = new JLabel(message);
        this.messageLabel.setHorizontalAlignment(SwingConstants.LEFT);

        this.messageLabel1 = new JLabel("");
        this.messageLabel1.setHorizontalAlignment(SwingConstants.LEFT);

        this.progressBar = new JProgressBar();

        this.cancelButton = new JButton("Cancel");
        final JPanel cancelPanel = new JPanel();
        cancelPanel.setLayout(new FlowLayout());

        cancelPanel.add(this.cancelButton);
        getContentPane().add(cancelPanel, BorderLayout.SOUTH);
        this.cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                	ProgressDialog.this.fileTask.setCancelled(true); }} );

        panel.add(this.messageLabel);
        panel.add(this.messageLabel1);
        panel.add(this.progressBar);

        SpringUtilities.makeCompactGrid(panel,3,1,6,6,6,6);

        getContentPane().add(panel,  BorderLayout.CENTER);

        setPreferredSize(new Dimension((width < 240)?240:width+10,150));

        pack();
        //setLocationRelativeTo(parent);
    }

    public void setCompletionMessage(final String msg) { this.completionMessage = msg; }

    public void run() {
        this.progressBar.setMaximum((int)this.fileTask.getTotal());
        final Thread run = new Thread() {
        	@Override
    		public void run() {
        			try {
        				ProgressDialog.this.fileTask.setListener(ProgressDialog.this);
        				ProgressDialog.this.fileTask.run();
        			} catch (final Throwable t) { }
        			finished();
        	}
        };

        run.start();
        show();         // this will block until the work thread finishes
    }


    public void finished() {
        if (this.completionMessage == null) {
			ProgressDialog.this.dispose();
		} else {
            this.messageLabel.setText(this.completionMessage);

            getContentPane().remove(this.progressBar);
            getContentPane().remove(this.cancelButton);
            final JButton closeButton = new JButton("Close");
            getContentPane().add(closeButton, BorderLayout.SOUTH);
            closeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        ProgressDialog.this.dispose(); }} );

            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            invalidate();
            pack();
        }
    }

    public class PromptRetryDelete implements Runnable {

        public int response;

        public PromptRetryDelete(final FileTask fileTask) {
        	super();
        }

        public void run() {
            final String s = (new StringBuilder()).append("Cannot remove:\n").append(ProgressDialog.this.fileTask.getFileName()).append(".\n").append("Do you wish to Retry or Cancel?").toString();
            this.response = StockDialogs.retryCancel(getRootPane(), s, ProgressDialog.this.fileTask.getOperationName());
        }
    }

    public class PromptRetryCreate implements Runnable {

        public int response;

        public PromptRetryCreate(final FileTask fileTask) {
        	super();
        }

        public void run() {
            final String s = (new StringBuilder()).append("Cannot create or write to:\n").append(ProgressDialog.this.fileTask.getDestination().getAbsolutePath()).append(".\n").append("Do you wish to Retry or Cancel?").toString();
            this.response = StockDialogs.retryCancel(getRootPane(), s, ProgressDialog.this.fileTask.getOperationName());
        }
    }

    public class PromptOverride implements Runnable {

        public int response;

        public PromptOverride(final FileTask fileTask) {
        	super();
        }

        public void run() {
            final String s = (new StringBuilder()).append("Do you wish to override\n").append(ProgressDialog.this.fileTask.getDestination().getAbsolutePath()).append("\nwith\n").append(ProgressDialog.this.fileTask.getSource().getAbsolutePath()).append("?").toString();
            this.response = StockDialogs.overrideConfirmation(getRootPane(), s, ProgressDialog.this.fileTask.getOperationName());
        }
    }

    public class PromptAllOverride implements Runnable {

        public int response;

        public PromptAllOverride(final FileTask fileTask) {
        	super();
        }

        public void run() {
            final String s = (new StringBuilder()).append("Do you wish to override\n").append(ProgressDialog.this.fileTask.getDestination().getAbsolutePath()).append("\nwith\n").append(ProgressDialog.this.fileTask.getSource().getAbsolutePath()).append("?").toString();
            this.response = StockDialogs.overrideAllConfirmation(getRootPane(), s, ProgressDialog.this.fileTask.getOperationName());
        }
    }

	public void fileTaskCancelled(final FileTask filetask) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                finished();
            }
        });
	}

	public void fileTaskCompleted(final FileTask filetask) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                finished();
            }
        });
    }

	public void fileTaskProgress(final FileTask filetask) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if(!filetask.isDone() && !filetask.isCancelled()) {
					final double total = filetask.getTotal();
					final double progress = filetask.getOverallProgress();
					final int progPercent = (int)(100D * (progress/total));

					String msg = filetask.getOperationName() + filetask.getFileName();
					ProgressDialog.this.messageLabel.setText(msg);

					msg = filetask.getOperator() + filetask.getDestinationFolder().getPath();
					ProgressDialog.this.messageLabel1.setText(msg);

					ProgressDialog.this.progressBar.setMaximum((int)total);
					ProgressDialog.this.progressBar.setValue((int)progress);

					msg = progPercent+"%";
					ProgressDialog.this.progressBar.setStringPainted(msg != null);
					ProgressDialog.this.progressBar.setString(msg);
				}
			}
		});
	}

	public boolean promptOverride(final FileTask filetask) {
        final PromptOverride promptoverride = new PromptOverride(filetask);
        try {
			SwingUtilities.invokeAndWait(promptoverride);
			return promptoverride.response == 0;
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean promptAllOverride(final FileTask filetask) {
        final PromptAllOverride promptoverride = new PromptAllOverride(filetask);
        try {
			SwingUtilities.invokeAndWait(promptoverride);
			if (promptoverride.response == 1) {
				filetask.setResponceAllYes(true);
			} if (promptoverride.response == 3) {
					filetask.setResponceAllNo(true);
				}

			return promptoverride.response < 2;
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean promptRetryCreate(final FileTask filetask) {
        final PromptRetryCreate promptretrycreate = new PromptRetryCreate(filetask);
        try {
			SwingUtilities.invokeAndWait(promptretrycreate);
			return promptretrycreate.response == 0;
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean promptRetryDelete(final FileTask filetask) {
        final PromptRetryDelete promptretrydelete = new PromptRetryDelete(filetask);
        try {
			SwingUtilities.invokeAndWait(promptretrydelete);
			return promptretrydelete.response == 0;
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;
	}

	public static void main(final String[] args) {
		   try {
			   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		   } catch (final Exception e) {}

		final File srcDir = new File("C:\\tmp\\DSC");
		final File destDir = new File("C:\\tmp\\pspdash");
		File[] files = srcDir.listFiles();

		ArrayList list = new ArrayList();
		for (final File element : files) {
			list.add(element);
		}
		final CopyFileTask copyTask = new CopyFileTask(list, destDir);
		final JFrame frame = new JFrame("ProgressDialog Test");
		frame.setSize(50, 50);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		ProgressDialog pd = new ProgressDialog(frame,copyTask.getOperationName(),copyTask,null);
		pd.run();

		files = destDir.listFiles();

		list = new ArrayList();
		for (final File element : files) {
			list.add(element);
		}
		final DeleteFileTask deleteTask = new DeleteFileTask(list);
		pd = new ProgressDialog(frame,deleteTask.getOperationName(),deleteTask,null);
		pd.run();
	}
}

