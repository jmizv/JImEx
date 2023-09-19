package de.jmizv.jiexplorer.filetask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class FileTask implements Runnable
{
    protected FileTaskListener listener;
    private File destinationFolder;
    private List<File> sourceFilesList;
    private File source;
    private File destination;
    private boolean cancelled;
    private boolean responceAllYes;
    private final boolean responceAllNo;
    private long total;
    private long overallProgress;
    protected String operator;
    protected int performed;

    public FileTask(final File file, final File destDir) {
    	this(file, destDir, false);
    }

    public FileTask(final List<File> list, final File file) {
    	this(list, file, false);
    }

    public FileTask(final File file, final File destDir, final boolean recursive) {
		if (recursive && file.isDirectory()) {
			this.sourceFilesList = getFiles(file);
		} else {
			this.sourceFilesList = new ArrayList<File>();
			this.sourceFilesList.add(file);
		}
		this.destinationFolder = destDir;
		this.operator = "To ";
		this.responceAllYes = false;
		this.responceAllNo = false;
		this.total = this.sourceFilesList.size();
    }

    public FileTask(final List<File> list, final File file, final boolean recursive) {
		this.sourceFilesList = new ArrayList<File>();
		for (final File f: list) {
			if (recursive && f.isDirectory()) {
				this.sourceFilesList.addAll(getFiles(f));
			} else {
				this.sourceFilesList.add(f);
			}
		}
		this.destinationFolder = file;
		this.operator = "To ";
		this.responceAllYes = false;
		this.responceAllNo = false;
		this.total = this.sourceFilesList.size();
    }

    private List<File> getFiles(final File dir) {
    	final ArrayList<File> list = new ArrayList<File>();
		final File[] farray = dir.listFiles();
		for (final File f: farray){
			list.add(f);
			if (f.isDirectory()) {
				list.addAll(getFiles(f));
			}
		}
		return list;
    }

    protected void close(final InputStream inputstream) {

		try {
		    if (inputstream != null) {
				inputstream.close();
			}
		} catch (final IOException ioexception) {
		    ioexception.printStackTrace();
		}
    }

    protected void close(final OutputStream outputstream) {
		try {
		    if (outputstream != null) {
				outputstream.close();
			}
		} catch (final IOException ioexception) {
		    ioexception.printStackTrace();
		}
    }

    protected boolean confirmDelete(final File file) {
		boolean bool = true;
		while_break:
		do {
		    do {
				if (file.canWrite()) {
					break while_break;
				}
			} while (!this.listener.promptRetryDelete(this));
		    bool = false;
		} while (false);
		return bool;
    }

    protected boolean confirmOverride() {
		if (this.destination.exists()) {
			return this.listener.promptOverride(this);
		}
		return true;
    }

    protected boolean confirmAllOverride() {
		if (this.destination.exists()) {
			if (this.responceAllNo) {
				return false;
			}

			if (this.responceAllYes) {
				return true;
			}

			return this.listener.promptAllOverride(this);
		}
		return true;
    }

    public boolean isCancelled() {
    	return this.cancelled;
    }

    public void setCancelled(final boolean bool) {
    	this.cancelled = bool;
    }

    public File getDestination() {
    	return this.destination;
    }

    public void setDestination(final File file) {
    	this.destination = file;
    }

    public File getDestinationFolder() {
    	return this.destinationFolder;
    }

    public void setDestinationFolder(final File file) {
    	this.destinationFolder = file;
    }

    public boolean isDone() {
    	return this.overallProgress >= this.total;
    }

    public String getFileName() {
    	return this.source != null ? this.source.getName() : null;
    }

    public long getFileSize() {
    	return this.source != null ? this.source.length() : 0L;
    }

    public void setListener(final FileTaskListener filetasklistener) {
    	this.listener = filetasklistener;
    }

    public abstract String getOperationName();

    protected FileOutputStream getFileOutputstream(final File file) {
    	FileOutputStream fileoutputstream = null;
    	for (;;) {
			try {
    			fileoutputstream = new FileOutputStream(file);
    			break;
    		} catch (final FileNotFoundException filenotfoundexception) {
    			if (!this.listener.promptRetryCreate(this)) {
					break;
				}
    		}
		}
    	return fileoutputstream;
    }

    public long getOverallProgress() {
    	return this.overallProgress;
    }

    public int getProgressPercent() {
    	return (int)(100 *((double)this.overallProgress/(double)this.total));
    }

    public String getProgressString() {
    	return String.valueOf((int)(100 *((double)this.overallProgress/(double)this.total)))+"%";
    }

    public void setOverallProgress(final long l) {
    	this.overallProgress = l;
    }

    public synchronized void incrementProgress() {
    	++this.overallProgress;
    }

    public File getSource() {
    	return this.source;
    }

    public void setSource(final File file) {
    	this.source = file;
    }

    public List<File> getSourceFilesList() {
    	return this.sourceFilesList;
    }

    public void setSourceFilesList(final List<File> list) {
    	this.sourceFilesList = list;
    }

    public long getTotal() {
    	return this.total;
    }

    public void setTotal(final long l) {
    	this.total = l;
    }

    public boolean wasSuccessful() {
    	return this.performed == this.sourceFilesList.size();
    }

    public String getOperator() {
    	return this.operator;
    }

	public void setResponceAllYes(final boolean replaceAllYes) {
		this.responceAllYes = replaceAllYes;
	}

	public boolean isResponceAllYes() { return this.responceAllYes; }

	public boolean isResponceAllNo() { return this.responceAllNo; }

	public void setResponceAllNo(final boolean replaceAllNo) {
		this.responceAllYes = replaceAllNo;
	}
}
