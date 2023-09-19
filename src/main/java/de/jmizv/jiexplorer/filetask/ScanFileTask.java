package de.jmizv.jiexplorer.filetask;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.Semaphore;

import de.jmizv.jiexplorer.db.JIThumbnailService;
import de.jmizv.jiexplorer.util.DiskObject;
import de.jmizv.jiexplorer.util.JIUtility;


public class ScanFileTask extends FileTask {
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ScanFileTask.class);

	private final Semaphore available = new Semaphore(3, true);
	private final List<File> dirs;

	public ScanFileTask(final List<File> dirs, final List<File> files) {
    	super(files, null, true);
    	this.dirs = dirs;
		this.operator = "Scan ";
	}

	@Override
	public String getOperationName() {
		return "Scan ";
	}

	public void run() {

		final Map validFiles = JIThumbnailService.getInstance().getValidThumbnails(this.dirs);
		final ListIterator<File> listiterator = getSourceFilesList().listIterator();
		while (listiterator.hasNext() && !isCancelled()) {

			final File file = listiterator.next();
			if (!validFiles.containsKey(file.getAbsolutePath())) {
				log.debug("ScanFileTask - "+file.getAbsolutePath());
				final String suffix = JIUtility.suffix(file.getName());
				if ((suffix != null) && JIUtility.isSupportedImage(suffix)) {
					final DiskObject dObj = new DiskObject(file);
					JIThumbnailService.getInstance().getDiskObject(dObj);

					try {
						if ((JIThumbnailService.getInstance().getThumbnail(dObj) == null) && !isCancelled()) {
							this.available.acquire();
							final BufferedImage tIcon = JIUtility.createThumbnailRetry(dObj);
							if (tIcon != null) {
								JIThumbnailService.getInstance().putThumbnail(dObj, tIcon, null);
							}

							ScanFileTask.this.available.release();
						}
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
				incrementProgress();
				listener.fileTaskProgress(ScanFileTask.this);
			}
		}
		//this.executor.shutdown();
		this.listener.fileTaskCompleted(this);
	}

}
