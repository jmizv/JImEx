package de.jmizv.jiexplorer.gui.dnd;

import java.io.File;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.filetask.CopyFileTask;
import de.jmizv.jiexplorer.filetask.MoveFileTask;
import de.jmizv.jiexplorer.gui.thumb.JIListModel;
import de.jmizv.jiexplorer.util.DiskObject;

public class ListTransferHandler extends AbstractTransferHandler {

   /**
	 *
	 */
	private static final long serialVersionUID = -5892684290906651831L;

@Override
public TransferableFileList exportFileList(final JComponent c) {
	   log.debug("exportFileList " + c);
	   final JList list = (JList) c;
	   this.indices = list.getSelectedIndices();
	   final Object[] obj = list.getSelectedValues();

	   final TransferableFileList transferable = new TransferableFileList();
	   for (final Object element : obj) {
		   if (element == null) {
			   c.getToolkit().beep();
		   } else {
			   transferable.add(((DiskObject) element).getFile());
		   }
	   }
	   return transferable;
   }

   @Override
public void importFileList(final JComponent c, final java.util.List<File> fileList) {
	   log.debug("importFileList " + fileList);
	   final JList target = (JList) c;
	   final JIListModel listModel = (JIListModel) target.getModel();
	   final int index = target.getSelectedIndex();

	   //Prevent the user from dropping data back on itself.
	   //For example, if the user is moving items #4,#5,#6 and #7 and
	   //attempts to insert the items after item #5, this would
	   //be problematic when removing the original items.
	   //So this is not allowed.
	   if ((this.indices != null) && (index >= this.indices[0] - 1) && (index <= this.indices[this.indices.length - 1])) {
		   this.indices = null;
		   return;
	   }

	   final File selectedDir = (File)JIExplorer.instance().getContext().getSelectedDirNodes().elementAt(0).getUserObject();;

	   if (selectedDir == null) {
		   log.error("Selected Dir == Null");
		   return;
	   }


	   if(selectedDir.getPath().equals(fileList.get(0).getParent())) {
		   this.importSuccess = false;
		   log.error("Recusive Copy");
	   } else {
		   log.debug("importFileList isCutOperation = "+ClipBoardAction.getInstance().isCutOperation());
		   if (ClipBoardAction.getInstance().isCutOperation()) {
			   final Object obj = ClipBoardAction.getInstance().getCutData();
			   if ((obj != null) && (obj instanceof java.util.List)) {
				   final java.util.List<File> files = (java.util.List<File>)obj;
				   if (files.containsAll(fileList)) {
					   final MoveFileTask moveTask = new MoveFileTask(fileList, selectedDir);
					   JIExplorer.instance().runAction(moveTask);
				   }
			   }
		   } else if (TransferHandler.MOVE == getSourceActions(c)) {
			   final MoveFileTask moveTask = new MoveFileTask(fileList, selectedDir);
			   JIExplorer.instance().runAction(moveTask);
		   } else {
			   final CopyFileTask copyTask = new CopyFileTask(fileList, selectedDir);
			   JIExplorer.instance().runAction(copyTask);
		   }


		   final Iterator<File> iterator = fileList.iterator();
		   while (iterator.hasNext()) {
			   final File file = iterator.next();
			   final File fileB = new File(selectedDir, file.getName());

			   log.debug("file = "+file);
			   log.debug("fileB = "+fileB);

			   final DiskObject diskObj = new DiskObject(fileB);
			   if (!listModel.contains(diskObj)) {
				   listModel.addElement(diskObj);
				   listModel.notifyAsUpdated(listModel.indexOf(diskObj));
			   }
		   }
	   }
   }

   @Override
public void cleanup(final JComponent c, final boolean remove) {

   }
}
