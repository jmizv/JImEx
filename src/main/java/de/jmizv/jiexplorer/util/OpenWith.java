package de.jmizv.jiexplorer.util;

import java.util.List;

public class OpenWith {
	protected String uid;
    protected String commandName;
    protected String command;
    protected String workingDir;

    public OpenWith() {
    	this.uid = new UID().toString();
        this.commandName = "";
        this.command = "";
        this.workingDir = "";
    }

    public OpenWith(final String[] values) {
    	if ((values != null) && (values.length == 3)) {
    		this.uid = new UID().toString();
	        this.commandName = values[0];
	        this.command = values[1];
	        this.workingDir = values[2];
    	}
    }

    public OpenWith(final String value0, final String value1, final String value2) {
    	this.uid = new UID().toString();
    	this.commandName = value0;
    	this.command = value1;
    	this.workingDir = value2;
    }

    public OpenWith(final String uid,final String value0, final String value1, final String value2) {
    	this.uid = uid;
    	this.commandName = value0;
    	this.command = value1;
    	this.workingDir = value2;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public void setCommandName(final String title) {
        this.commandName = title;
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommand(final String artist) {
        this.command = artist;
    }

    public String getWorkingDir() {
        return this.workingDir;
    }

    public void setWorkingDir(final String album) {
        this.workingDir = album;
    }

    public void run(final String[] args) {
		try {
			final ProcessBuilder p = new ProcessBuilder(JIUtility.systemPath(this.command));
			final List<String> cmd = p.command();
			for (final String arg: args) {
				cmd.add(JIUtility.systemPath(arg));
			}
			p.redirectErrorStream(true);
			p.start();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
    }

    @Override
	public String toString() {
    	return this.commandName+"|"+this.command+"|"+this.workingDir;
    }

	/**
	 * @return the uid
	 */
	public synchronized final String getUid() {
		return this.uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public synchronized final void setUid(final String uid) {
		this.uid = uid;
	}
}