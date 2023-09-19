package de.jmizv.jiexplorer.gui.dnd;


public class ClipBoardAction {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ClipBoardAction.class);
	private static ClipBoardAction instance = null;

	private boolean cutOperation = false;
	private Object cutData = null;

	private ClipBoardAction() {
	}

	public static ClipBoardAction getInstance() {
		if (instance == null) {
			instance = new ClipBoardAction();
		}
		return instance;
	}

	public boolean isCutOperation() {
		return cutOperation;
	}

	public void setCutOperation(final boolean cutOperation) {
		log.debug("setCutOperation = "+cutOperation);
		this.cutOperation = cutOperation;
		if (!this.cutOperation) {
			cutData = null;
		}
	}

	public Object getCutData() {
		return cutData;
	}

	public void setCutData(final Object cutData) {
		this.cutData = cutData;
	}
}
