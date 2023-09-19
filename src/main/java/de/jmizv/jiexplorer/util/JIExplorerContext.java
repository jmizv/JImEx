package de.jmizv.jiexplorer.util;

import java.util.Vector;

import de.jmizv.jiexplorer.gui.cattree.JICatTreeNode;
import de.jmizv.jiexplorer.gui.datetree.JIDateTreeNode;
import de.jmizv.jiexplorer.gui.tree.JITreeNode;


public class JIExplorerContext {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JIExplorerContext.class);

    public static final int DIRECTORY_STATE = 1;
	public static final int KEY_WORDS_STATE = 2;
	public static final int CATEGORY_STATE  = 3;
	public static final int DATE_STATE      = 4;

	protected String[]               selectedKeyWords    = null;
	protected DiskObject[]           selectedDiskObjects = null;
	protected Vector<JICatTreeNode>  selectedCatNodes    = null;
	protected Vector<JIDateTreeNode> selectedDateNodes   = null;
	protected Vector<JITreeNode>     selectedDirNodes    = null;

	protected boolean includeKeyWords    = false;
	protected boolean includeDiskObjects = false;
	protected boolean includeCatNodes    = false;
	protected boolean includeDateNodes   = false;
	protected boolean includeDirNodes    = false;

	protected int lastSelectedKeyWordsIndex   = -1;
	protected int lastSelectedDiskObjectIndex = -1;
	protected int lastSelectedCatNodesIndex   = -1;
	protected int lastSelectedDateNodesIndex  = -1;
	protected int lastSelectedDirNodesIndex   = -1;

	protected boolean statusBarProgressTaskRunning = false;

	protected int imageCnt = -1;

	protected int state = -1;
	/**
	 * @return the selsectedCatNodes
	 */
	public synchronized final Vector<JICatTreeNode> getSelectedCatNodes() {
		return this.selectedCatNodes;
	}
	/**
	 * @param selsectedCatNodes the selsectedCatNodes to set
	 * @param lastSelectedCatNode the lastSelectedCatNodesIndex to set
	 */
	public synchronized final void setSelectedCatNodes(final Vector<JICatTreeNode> selsectedCatNodes, final JICatTreeNode lastSelectedCatNode) {
		this.selectedCatNodes = selsectedCatNodes;
		this.lastSelectedCatNodesIndex = this.selectedCatNodes.indexOf(lastSelectedCatNode);
	}
	/**
	 * @param selsectedCatNodes the selsectedCatNodes to set
	 * @param lastSelectedCatNodesIndex the lastSelectedCatNodesIndex to set
	 */
	public synchronized final void setSelectedCatNodes(final Vector<JICatTreeNode> selsectedCatNodes, final int lastSelectedCatNodesIndex) {
		this.selectedCatNodes = selsectedCatNodes;
		this.lastSelectedCatNodesIndex = lastSelectedCatNodesIndex;
	}
	/**
	 * @return the selsectedDateNodes
	 */
	public synchronized final Vector<JIDateTreeNode> getSelectedDateNodes() {
		return this.selectedDateNodes;
	}
	/**
	 * @param selsectedDateNodes the selsectedDateNodes to set
	 * @param lastSelectedDateNode the lastSelectedDateNodesIndex to set
	 */
	public synchronized final void setSelectedDateNodes(final Vector<JIDateTreeNode> selsectedDateNodes, final JIDateTreeNode lastSelectedDateNode) {
		this.selectedDateNodes = selsectedDateNodes;
		this.lastSelectedDateNodesIndex = this.selectedDateNodes.indexOf(lastSelectedDateNode);
	}
	/**
	 * @param selsectedCatNodes the selsectedCatNodes to set
	 * @param lastSelectedCatNodesIndex the lastSelectedCatNodesIndex to set
	 */
	public synchronized final void setSelectedDateNodes(final Vector<JIDateTreeNode> selsectedDateNodes, final int lastSelectedDateNodesIndex) {
		this.selectedDateNodes = selsectedDateNodes;
		this.lastSelectedDateNodesIndex = lastSelectedDateNodesIndex;
	}
	/**
	 * @return the selsectedDirNodes
	 */
	public synchronized final Vector<JITreeNode> getSelectedDirNodes() {
		return this.selectedDirNodes;
	}
	/**
	 * @param selectedDirNodes the selectedDirNodes to set
	 */
	public synchronized final void setSelectedDirNodes(final Vector<JITreeNode> selectedDirNodes, final JITreeNode lastSelectedDirNodes) {
		this.selectedDirNodes = selectedDirNodes;
		this.lastSelectedDirNodesIndex = this.selectedDirNodes.indexOf(lastSelectedDirNodes);
	}
	/**
	 * @param selsectedCatNodes the selsectedCatNodes to set
	 * @param lastSelectedDirNodesIndex the lastSelectedDirNodesIndex to set
	 */
	public synchronized final void setSelectedDirNodes(final Vector<JITreeNode> selectedDirNodes, final int lastSelectedDirNodesIndex) {
		this.selectedDirNodes = selectedDirNodes;
		this.lastSelectedDirNodesIndex = lastSelectedDirNodesIndex;
	}
	/**
	 * @return the selsectedDiskObjects
	 */
	public synchronized final DiskObject[] getSelectedDiskObjects() {
		return this.selectedDiskObjects;
	}
	/**
	 * @param selsectedDiskObjects the selsectedDiskObjects to set
	 * @param lastSelectedDiskObject the lastSelectedDiskObjectIndex to set
	 */
	public synchronized final void setSelectedDiskObjects(final DiskObject[] selsectedDiskObjects, final DiskObject lastSelectedDiskObject) {
		log.debug("setSelectedDiskObjects - "+selsectedDiskObjects);
		this.selectedDiskObjects = selsectedDiskObjects;
		if ((selsectedDiskObjects != null) && (lastSelectedDiskObject != null)) {
			for (int i=0; i<this.selectedDiskObjects.length; i++) {
				if (lastSelectedDiskObject.equals(this.selectedDiskObjects[i])) {
					this.lastSelectedDiskObjectIndex = i;
					return;
				}
			}
		}
	}
	/**
	 * @param selsectedDiskObjects the selsectedDiskObjects to set
	 * @param lastSelectedDiskObject the lastSelectedDiskObjectIndex to set
	 */
	public synchronized final void setSelectedDiskObjects(final DiskObject[] selsectedDiskObjects, final int lastSelectedDiskObjectIndex) {
		log.debug("setSelectedDiskObjects - "+selsectedDiskObjects);
		this.selectedDiskObjects = selsectedDiskObjects;
		this.lastSelectedDiskObjectIndex = lastSelectedDiskObjectIndex;
	}
	/**
	 * @return the selsectedKeys
	 */
	public synchronized final String[] getSelectedKeyWords() {
		return this.selectedKeyWords;
	}
	/**
	 * @param selsectedKeys the selsectedKeys to set
	 */
	public synchronized final void setSelectedKeyWords(final Object[] selsectedKeys, final Object lastSelectedKeyWords) {
		final String[] keys = new String[selsectedKeys.length];
		int cnt = 0;
		for (final Object obj: selsectedKeys) {
			if (obj.equals(lastSelectedKeyWords)) {
				this.lastSelectedKeyWordsIndex = cnt;
			}
			keys[cnt++] = (String)obj;
		}
		this.selectedKeyWords = keys;
	}
	/**
	 * @param selectedKeyWords the selectedKeyWords to set
	 */
	public synchronized final void setSelectedKeyWords(final Object[] selectedKeyWords, final int lastSelectedKeyWordsIndex) {
		final String[] keys = new String[selectedKeyWords.length];
		int cnt = 0;
		for (final Object obj: selectedKeyWords) {
			keys[cnt++] = (String)obj;
		}
		this.selectedKeyWords = keys;
		this.lastSelectedKeyWordsIndex = lastSelectedKeyWordsIndex;
	}
	/**
	 * @return the includeCatNodes
	 */
	public synchronized final boolean isIncludeCatNodes() {
		return this.includeCatNodes;
	}
	/**
	 * @param includeCatNodes the includeCatNodes to set
	 */
	public synchronized final void setIncludeCatNodes(final boolean includeCatNodes) {
		this.includeCatNodes = includeCatNodes;
	}
	/**
	 * @return the includeDateNodes
	 */
	public synchronized final boolean isIncludeDateNodes() {
		return this.includeDateNodes;
	}
	/**
	 * @param includeDateNodes the includeDateNodes to set
	 */
	public synchronized final void setIncludeDateNodes(final boolean includeDateNodes) {
		this.includeDateNodes = includeDateNodes;
	}
	/**
	 * @return the includeDirNodes
	 */
	public synchronized final boolean isIncludeDirNodes() {
		return this.includeDirNodes;
	}
	/**
	 * @param includeDirNodes the includeDirNodes to set
	 */
	public synchronized final void setIncludeDirNodes(final boolean includeDirNodes) {
		this.includeDirNodes = includeDirNodes;
	}
	/**
	 * @return the includeKeyWords
	 */
	public synchronized final boolean isIncludeKeyWords() {
		return this.includeKeyWords;
	}
	/**
	 * @param includeKeyWords the includeKeyWords to set
	 */
	public synchronized final void setIncludeKeyWords(final boolean includeKeyWords) {
		this.includeKeyWords = includeKeyWords;
	}
	/**
	 * @return the state
	 */
	public synchronized final int getState() {
		return this.state;
	}
	/**
	 * @param state the state to set
	 */
	public synchronized final void setState(final int state) {
		this.state = state;
		clearListObjects();
	}
	/**
	 * @return the includeDiskObjects
	 */
	public synchronized final boolean isIncludeDiskObjects() {
		return this.includeDiskObjects;
	}
	/**
	 * @param includeDiskObjects the includeDiskObjects to set
	 */
	public synchronized final void setIncludeDiskObjects(final boolean includeDiskObjects) {
		this.includeDiskObjects = includeDiskObjects;
	}
	/**
	 * @return the lastSelectedDirNodesIndex
	 */
	public synchronized final int getLastSelectedDirNodesIndex() {
		return this.lastSelectedDirNodesIndex>-1?this.lastSelectedDirNodesIndex:0;
	}
	/**
	 * @return the lastSelectedDiskObjectIndex
	 */
	public synchronized final int getLastSelectedDiskObjectIndex() {
		return this.lastSelectedDiskObjectIndex>-1?this.lastSelectedDiskObjectIndex:0;
	}
	/**
	 * @return the lastSelectedKeyWordsIndex
	 */
	public synchronized final int getLastSelectedKeyWordsIndex() {
		return this.lastSelectedKeyWordsIndex>-1?this.lastSelectedKeyWordsIndex:0;
	}
	/**
	 * @param selectedKeyWords the selectedKeyWords to set
	 */
	public synchronized final void setSelectedKeyWords(final String[] selectedKeyWords, final String lastSelectedKeyWords) {
		this.selectedKeyWords = selectedKeyWords;
		for (int i=0; i<selectedKeyWords.length; i++) {
			if (selectedKeyWords[i].equals(lastSelectedKeyWords)) {
				this.lastSelectedKeyWordsIndex = i;
				return;
			}
		}
	}
	/**
	 * @param selectedKeyWords the selectedKeyWords to set
	 */
	public synchronized final void setSelectedKeyWords(final String[] selectedKeyWords, final int lastSelectedKeyWordsIndex) {
		this.selectedKeyWords = selectedKeyWords;
		this.lastSelectedKeyWordsIndex = lastSelectedKeyWordsIndex;
	}
	/**
	 * @return the imageCnt
	 */
	public synchronized final int getImageCnt() {
		return this.imageCnt;
	}
	/**
	 * @param imageCnt the imageCnt to set
	 */
	public synchronized final void setImageCnt(final int imageCnt) {
		this.imageCnt = imageCnt;
	}
	/**
	 * @return the statusBarProgressTaskRunning
	 */
	public synchronized final boolean isStatusBarProgressTaskRunning() {
		return this.statusBarProgressTaskRunning;
	}
	/**
	 * @param statusBarProgressTaskRunning the statusBarProgressTaskRunning to set
	 */
	public synchronized final void setStatusBarProgressTaskRunning(
			final boolean statusBarProgressTaskRunning) {
		this.statusBarProgressTaskRunning = statusBarProgressTaskRunning;
	}

	public synchronized final DiskObject getLastSelectedDiskObj() {
		if ((this.lastSelectedDiskObjectIndex > -1) && (this.selectedDiskObjects!= null) && (this.selectedDiskObjects.length > this.lastSelectedDiskObjectIndex)) {
			return this.selectedDiskObjects[this.lastSelectedDiskObjectIndex];
		}
		return null;
	}

	public synchronized final void clearListObjects() {
		log.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!clearListObjects!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		this.selectedDiskObjects = null;
		this.lastSelectedDiskObjectIndex = -1;
		this.imageCnt = -1;
	}
}
