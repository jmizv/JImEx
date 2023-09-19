package de.jmizv.jiexplorer.gui.dnd;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public abstract class AbstractTransferHandler  extends TransferHandler {

	public static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AbstractTransferHandler.class);

	protected int[] indices = null;

	protected int addIndex = -1; //Location where items were added

	protected int addCount = 0; //Number of items added.

	protected boolean importSuccess = true;

	@Override
	protected Transferable createTransferable(final JComponent c) {
		return exportFileList(c);
	}


   @Override
   public void exportToClipboard(final JComponent comp,
		   							final Clipboard clip,
		   							final int action)
				throws IllegalStateException {

	   log.debug("exportToClipboard operation = "+action);
	   ClipBoardAction.getInstance().setCutOperation(action == MOVE);
	   final int clipboardAction = getSourceActions(comp) & action;
	   if (clipboardAction != NONE) {
		   final Transferable t = createTransferable(comp);
		   if (t != null) {
			   try {
				   if (ClipBoardAction.getInstance().isCutOperation()) {
					   final Object obj = t.getTransferData(DataFlavor.javaFileListFlavor);
					   if ((obj != null) && (obj instanceof java.util.List)) {
						   final java.util.List<File> files = (java.util.List<File>)obj;
						   ClipBoardAction.getInstance().setCutData(files);
					   }
				   }
				   clip.setContents(t, null);
				   exportDone(comp, t, clipboardAction);
				   return;
			   } catch (final IllegalStateException ise) {
				   exportDone(comp, t, NONE);
				   throw ise;
			   } catch (final Exception e) {
				   clip.setContents(t, null);
				   exportDone(comp, t, clipboardAction);
				   return;
			   }
		   }
	   }

	   exportDone(comp, null, NONE);
   }

   @Override
   public int getSourceActions(final JComponent c) {
	   return COPY_OR_MOVE;
   }

   @Override
   public boolean importData(final JComponent c, final Transferable t) {
	   log.debug("importData " + t);
	   if (canImport(c, t.getTransferDataFlavors())) {
		   try {
			   log.debug("Transferable = "+t.getClass().getName());
			   final java.util.List<File> fList = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
			   log.debug("importData passed - calling importFileList " + fList);

			   this.importSuccess = true;
			   importFileList(c, fList);
			   return true;
		   } catch (final UnsupportedFlavorException ufe) {
			   ufe.printStackTrace();
		   } catch (final IOException ioe) {
			   ioe.printStackTrace();
		   }
	   }
	   return false;
   }

   @Override
   protected void exportDone(final JComponent c,
		   						final Transferable data,
		   						final int action) {
	   this.indices = null;
   }

   @Override
   public boolean canImport(final JComponent c, final DataFlavor[] flavors) {
	   log.debug("canImport " + flavors);
	   if (this.indices != null) {
		   return false;
	   }
	   for (final DataFlavor element : flavors) {
		   if (DataFlavor.javaFileListFlavor.equals(element)) {
			   return true;
		   }
	   }

	   return false;
   }

   public abstract void cleanup(final JComponent c, final boolean remove);

   public abstract TransferableFileList exportFileList(final JComponent c);

   public abstract void importFileList(final JComponent c, final java.util.List<File> fileList);
}
