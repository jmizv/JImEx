package de.jmizv.jiexplorer.filetask;

public interface FileTaskListener
{
    public void fileTaskCancelled(FileTask filetask);

    public void fileTaskCompleted(FileTask filetask);

    public void fileTaskProgress(FileTask filetask);

    public boolean promptOverride(FileTask filetask);

    public boolean promptAllOverride(FileTask filetask);

    public boolean promptRetryCreate(FileTask filetask);

    public boolean promptRetryDelete(FileTask filetask);
}
