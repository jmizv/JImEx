package de.jmizv.jiexplorer.util;


public interface JIObservable {

	public static final String DATE_CHANGED = "DATE_CHANGED";
	public static final String KEYWORDS_CHANGED = "KEYWORDS_CHANGED";
	public static final String CATAGORY_CHANGED = "CATAGORY_CHANGED";
	public static final String SECTION_CHANGED = "SECTION_CHANGED";
	public static final String PROGERSS_START = "PROGERSS_START";
	public static final String PROGERSS_CANCELLED = "PROGERSS_CANCELLED";
	public static final String PROGERSS_FINISHED = "PROGERSS_FINISHED";
	public static final String DIRECTORY_LOADED = "DIRECTORY_LOADED";
	public static final String DIRECTORY_LOADING = "DIRECTORY_LOADING";
	public static final String DIRECTORY_SIZE = "DIRECTORY_SIZE";
	public static final String SELECT_DIRECTORY = "SELECT_DIRECTORY";
	public static final String ADD_FILE = "ADD_FILE";

	public void addObserver(JIObserver o);
	public void deleteObserver(JIObserver o);
	public void notifyObservers();
	public void notifyObservers(Object arg);
	public void deleteObservers();
	public boolean hasChanged();
	public int countObservers();
}
