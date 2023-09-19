/*
 * JIPreferences.java
 *
 * Created on March 28, 2005, 8:22 AM
 */

package de.jmizv.jiexplorer.gui.preferences;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import de.jmizv.jiexplorer.JIExplorer;
import de.jmizv.jiexplorer.db.JIExplorerDB;
import de.jmizv.jiexplorer.util.OpenWith;



public class JIPreferences {

	private static JIPreferences instance;

	public static final String DB_DIR = System.getProperty("user.home") + File.separator + ".JIThumbnailDB"+JIExplorer.VERSION;
    public static final String HSQL_CONNECTION_URL = "jdbc:hsqldb:file:" + DB_DIR + File.separator + "JIThumbnails";
	public static final String THUMB_VIEW = "THUMB_VIEW";
	public static final String PREVIEW_VIEW = "PREVIEW_VIEW";
	public static final String DETAIL_VIEW = "DETAIL_VIEW";

	// Slide Show Sequence Direction
	// (Forward = 0, Reverese = 1, Random = 2)
	public static final int SLIDE_SHOW_DIRECTION=0;

	// Slide Show Delay (in seconds)
	public static final int SLIDE_SHOW_DELAY=10;

	// Full screen video mode
	//	public static final DisplayMode DMODE[] = {
	//			new DisplayMode(1024, 768, 32, DisplayMode.REFRESH_RATE_UNKNOWN),
	//			new DisplayMode(1024, 768, 16, DisplayMode.REFRESH_RATE_UNKNOWN),
	//			new DisplayMode(800, 600, 32, DisplayMode.REFRESH_RATE_UNKNOWN),
	//			new DisplayMode(800, 600, 16, DisplayMode.REFRESH_RATE_UNKNOWN),
	//			new DisplayMode(640, 480, 32, DisplayMode.REFRESH_RATE_UNKNOWN),
	//			new DisplayMode(640, 480, 16, DisplayMode.REFRESH_RATE_UNKNOWN) };
	public static final int FULL_SCREEN_VIDEO_MODE=0;

	// Image Viewer Background color
	public static final int vColor=0;

	// Thumbnail & Image List sort algorithim
	// (File name = 0, File extension = 1, Size = 2, Date = 3, Path = 4)
	public static final int THUMBNAIL_SORT_ORDER=0;

	// Thumbnail Scroll Mode
	// (Horizontal = 1, Vertical = 2)
	public static final int THUMBNAIL_SCROLL_MODE=2;

	public static final boolean THUMBNAIL_TOOL_TIPS = false;
	public static final boolean THUMBNAIL_SORT_DESEND = false;
	public static final boolean IMAGE_VIEWER_QUALITY = false;
	public static final boolean IMAGE_VIEWER_SHRINK_TO_FIT_WINDOW = true;
	public static final boolean IMAGE_VIEWER_ZOOM_TO_FIT_WINDOW=true;
	public static final boolean IMAGE_VIEWER_WIN_FIT_SCREEN=true;
	public static final boolean IMAGE_VIEWER_WIN_FIT_IMAGE=false;
	public static final boolean IMAGE_VIEWER_CENTER_WIN=false;
	public static final boolean IMAGE_VIEWER_SAVE_WIN_POS=true;
	public static final boolean IMAGE_VIEWER_ON_TOP=false;
	public static final boolean IMAGE_VIEWER_SHOW_MENU=false;
	public static final boolean IMAGE_VIEWER_SHOW_FULL_PATH=false;
	public static final boolean IMAGE_VIEWER_FULL_SCREEN=false;
	public static final boolean IMAGE_VIEWER_HIDE_MOUSE=false;
	public static final boolean CATEGORY_INCLUDE_SUBS = false;


	public static final boolean LOAD_ICONS_RECURSIVELY = false;

	public static final boolean LOAD_ALL_IMAGE_ICONS = false;

	public static final boolean SHOW_DETAIL_TABLE_ICON = false;

	// Image Viewer default locatcation
	public static final Point IMAGE_VIEWER_LOC = new Point(30,30);
	public static final Point BROWSER_LOCATION = new Point(30,30);

	public static final Dimension BROWSER_DIM = new Dimension(900, 600);
	public static final Dimension ICON_DIM = new Dimension(150, 150);
	public static final Dimension DISK_OBJ_INFO_DIM = new Dimension(600, 600);

	public static final int HORIZONTAL_DIVIDER_LOCATION = 400;
	public static final int VERTICAL_DIVIDER_LOC = 300;

	public static final int NAME_HEADER_WIDTH = 180;
	public static final int SIZE_HEADER_WIDTH = 75;
	public static final int DATE_HEADER_WIDTH = 117;
	public static final int DIM_HEADER_WIDTH = 82;

	public static final double IMAGE_VIEWER_ZOOM_FACTOR = 0.15;

	public static final int QUERY_AND_OR = 0;

	public static final String USER_HOME = System.getProperty("user.home");
	public static final String DATABASE_TYPE = "HSQL";
	public static final String JDBC_DRIVER = JIExplorerDB.HSQL_JDBC_DRIVER;
	public static final String JDBC_URL = HSQL_CONNECTION_URL;
	public static final String JDBC_USER_NAME = "sa";
	public static final String JDBC_PASSWORD = "";
	public static final String DB_LOCATION = DB_DIR;
	public static final String IMAGES_VIEW_TYPE = THUMB_VIEW;
	public static final String OPEN_WITH_KEYS = "";

	private final Preferences prefs;

	private JIPreferences() {
		this.prefs = Preferences.userNodeForPackage(this.getClass());
		getPreferences();
	}

	public static JIPreferences getInstance() {
		if (instance == null) {
			instance = new JIPreferences();
		}
		return instance;
	}

	public void clearPreferences() {
		try {
			this.prefs.clear();
		} catch (final BackingStoreException e) {
			System.err.println("JIPreferences::clearPreferences() - BackingStoreException ["+e.getMessage()+"]");
			e.printStackTrace();
		}
		instance = null;
	}

	public void getPreferences(){
		setQueryAndOr(this.prefs.getInt("qANDOR", QUERY_AND_OR));
		setImageViewerColor(this.prefs.getInt("vColor", vColor));
		setImageViewerLoc(this.prefs.getInt("vLocX", IMAGE_VIEWER_LOC.x),this.prefs.getInt("vLocY", IMAGE_VIEWER_LOC.y));
		setBrowserLoc(this.prefs.getInt("tLocX", BROWSER_LOCATION.x),this.prefs.getInt("tLocY", BROWSER_LOCATION.y));
		setIconDim(this.prefs.getInt("iDimWidth", ICON_DIM.width),this.prefs.getInt("iDimHeight", ICON_DIM.height));
		setBrowserDim(this.prefs.getInt("tDimWidth", BROWSER_DIM.width),this.prefs.getInt("tDimHeight", BROWSER_DIM.height));
		setImageViewerDim(this.prefs.getInt("vDimWidth", DISK_OBJ_INFO_DIM.width),this.prefs.getInt("vDimHeight", DISK_OBJ_INFO_DIM.height));
		setDatabaseType(this.prefs.get("databaseType", DATABASE_TYPE));
		setJDBCDriver(this.prefs.get("jdbcDriver", JDBC_DRIVER));
		setJDBCURL(this.prefs.get("jdbcURL", JDBC_URL));
		setJDBCUserName(this.prefs.get("jdbcUserName", JDBC_USER_NAME));
		setJDBCPassword(this.prefs.get("jdbcPassword", JDBC_PASSWORD));
		setDBLocation(this.prefs.get("dbLocation", DB_LOCATION));
		setDirectoryPath(this.prefs.get("directoryPath", USER_HOME));

		setNameHeaderWidth(this.prefs.getInt("nameHeaderWidth", NAME_HEADER_WIDTH));
		setDateHeaderWidth(this.prefs.getInt("dateHeaderWidth", DATE_HEADER_WIDTH));
		setSizeHeaderWidth(this.prefs.getInt("sizeHeaderWidth", SIZE_HEADER_WIDTH));
		setDimHeaderWidth(this.prefs.getInt("dimHeaderWidth", DIM_HEADER_WIDTH));
	}


   public String toString() {
      final String EOL = System.getProperty("line.separator");
      final StringBuffer buf = new StringBuffer();

      try {
		for (final String key : this.prefs.keys()) {
			buf.append(key).append(" = ").append(this.prefs.get(key,"")).append(EOL);
		}
	} catch (final BackingStoreException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return buf.toString();

   }

   public void savePreferences() {
      try { this.prefs.sync(); } catch (final Exception exp) {
    	  exp.printStackTrace();
      }
   }

   public final String getDirectoryPath() {
	   return this.prefs.get("directoryPath",USER_HOME);
   }
   public final void setDirectoryPath(final String path) {
	   this.prefs.put("directoryPath",path);
   }

   public final String getImagesViewType() {
	   return this.prefs.get("fileView",IMAGES_VIEW_TYPE);
   }

   public final void setImagesViewType(final String fv) {
	   this.prefs.put("fileView",fv);
   }

   public final String getDBLocation() {
		return DB_DIR;
   }

   public final void setDBLocation(final String location) {
	   this.prefs.put("dbLocation",location);
   }

   /**
    * @return Returns the HORIZONTAL_DIVIDER_LOCATION.
    */
   public final int getHorizontalDividerLoc() {
	   return this.prefs.getInt("hDividerLoc",HORIZONTAL_DIVIDER_LOCATION);
   }
   /**
    * @param HORIZONTAL_DIVIDER_LOCATION The tSort to set.
    */
   public final void setHorizontalDividerLoc(final int i) {
	   this.prefs.putInt("hDividerLoc",i);
   }


   public final int getQueryAndOr() {
	   return this.prefs.getInt("qANDOR",QUERY_AND_OR);
   }

   public final void setQueryAndOr(final int i) {
	   this.prefs.putInt("qANDOR",i);
   }

   /**
    * @return Returns the IMAGE_VIEWER_ZOOM_FACTOR.
    */
   public final double getImageViewerZoomFactor() {
	   return this.prefs.getDouble("vZoomFactor",IMAGE_VIEWER_ZOOM_FACTOR);
   }

   public final void setImageViewerZoomFactor(final double i) {
	   this.prefs.putDouble("vZoomFactor",i);
   }

   /**
    * @return Returns the VERTICAL_DIVIDER_LOC.
    */
   public final int getVerticalDividerLoc() {
	   return this.prefs.getInt("vDividerLoc",VERTICAL_DIVIDER_LOC);
   }
   /**
    * @param VERTICAL_DIVIDER_LOC The tSort to set.
    */
   public final void setVerticalDividerLoc(final int i) {
	   this.prefs.putInt("vDividerLoc",i);
   }

   public final boolean isCategoryIncludeSubs() {
      return this.prefs.getBoolean("CATEGORY_INCLUDE_SUBS",CATEGORY_INCLUDE_SUBS);
   }

   public final void setCategoryIncludeSubs(final boolean b) {
      this.prefs.putBoolean("CATEGORY_INCLUDE_SUBS",b);
   }

   public final boolean isThumbnailToolTips() {
      return this.prefs.getBoolean("tToolTips",THUMBNAIL_TOOL_TIPS);
   }

   public final void setThumbnailToolTips(final boolean toolTips) {
      this.prefs.putBoolean("tToolTips",toolTips);
   }

   public final int getThumbnailScrollMode() {
      return this.prefs.getInt("tScrollMode",THUMBNAIL_SCROLL_MODE);
   }

   public final void setThumbnailScrollMode(final int scrollMode) {
      if ((scrollMode > 0) && (scrollMode < 3)) {
		this.prefs.putInt("tScrollMode",scrollMode);
	}
   }

   public final int getThumbnailSortOrder() {
      return this.prefs.getInt("tSort",THUMBNAIL_SORT_ORDER);
   }

   public final void setThumbnailSortOrder(final int sort) {
      if ((sort > -1) && (sort < 4)) {
		this.prefs.putInt("tSort",sort);
	}
   }

   public final Color getImageViewerColor() {
      return new Color(this.prefs.getInt("vColor",vColor));
   }

   public final void setImageViewerColor(final int color) {
      if ((color < 0) && (color > -16777215)) {
		this.prefs.putInt("vColor",color);
	}
   }

   public final Point getImageViewerLoc() {
	      return new Point(this.prefs.getInt("vLocX",IMAGE_VIEWER_LOC.x),this.prefs.getInt("vLocY",IMAGE_VIEWER_LOC.y));
   }

   public final void setImageViewerLoc(final int x, final int y) {
      this.prefs.putInt("vLocX",x);
      this.prefs.putInt("vLocY",y);
   }

   public final Point getBrowserLoc() {
      return new Point(this.prefs.getInt("tLocX",BROWSER_LOCATION.x),this.prefs.getInt("tLocY",BROWSER_LOCATION.y));
   }

   public final void setBrowserLoc(final int x, final int y) {
      this.prefs.putInt("tLocX",x);
      this.prefs.putInt("tLocY",y);
   }

   public final Dimension getAdjustedIconDim() {
      return new Dimension(this.prefs.getInt("iDimWidth",ICON_DIM.width) - 12,  this.prefs.getInt("iDimHeight",ICON_DIM.height) - 12);
   }

   public final Dimension getIconDim() {
      return new Dimension(this.prefs.getInt("iDimWidth",ICON_DIM.width), this.prefs.getInt("iDimHeight",ICON_DIM.height));
   }

   public final void setIconDim(final int width, final int height) {
      this.prefs.putInt("iDimWidth",width);
      this.prefs.putInt("iDimHeight",height);
   }

   /**
    * @return Returns the vDim.
    */
   public final Dimension getImageViewerDim() {
	      return new Dimension(this.prefs.getInt("vDimWidth",DISK_OBJ_INFO_DIM.width), this.prefs.getInt("vDimHeight",DISK_OBJ_INFO_DIM.height));
   }
   /**
    * @param DISK_OBJ_INFO_DIM The vDim to set.
    */
   public final void setImageViewerDim(final int width, final int height) {
      this.prefs.putInt("vDimWidth",width);
      this.prefs.putInt("vDimHeight",height);
   }

   public final Dimension getBrowserDim() {
      return new Dimension(this.prefs.getInt("tDimWidth",BROWSER_DIM.width), this.prefs.getInt("tDimHeight",BROWSER_DIM.height));
   }

   public final void setBrowserDim(final int width, final int height) {
      this.prefs.putInt("tDimWidth",width);
      this.prefs.putInt("tDimHeight",height);
   }

   /**
    * @return Returns the SLIDE_SHOW_DELAY.
    */
   public final int getSlideShowDelay() {
      return this.prefs.getInt("ssDelay",SLIDE_SHOW_DELAY);
   }
   /**
    * @param SLIDE_SHOW_DELAY The ssDirection to set.
    */
   public final void setSlideShowDelay(final int delay) {
      if ((delay >= 0) && (delay <= 2000)) {
		this.prefs.putInt("ssDelay",delay);
	}
   }

   /**
    * @return Returns the SLIDE_SHOW_DIRECTION.
    */
   public final int getSlideShowDirection() {
      return this.prefs.getInt("ssDirection",SLIDE_SHOW_DIRECTION);
   }
   /**
    * @param SLIDE_SHOW_DIRECTION The ssDirection to set.
    */
   public final void setSlideShowDirection(final int direction) {
	   if ((direction >= 0) && (direction <= 2)) {
		this.prefs.putInt("ssDirection",direction);
	}
   }

   public final int getFullScreenVideoMode() {
	   return this.prefs.getInt("vMode",FULL_SCREEN_VIDEO_MODE);
   }

   public final void setFullScreenVideoMode(final int mode) {
	   if ((mode >= 0) && (mode <= 5)) {
		this.prefs.putInt("vMode",mode);
	}
   }

   public final boolean isImageViewerCenterWin() {
	   return this.prefs.getBoolean("vCenterWin",IMAGE_VIEWER_CENTER_WIN);
   }

   public final void setImageViewerCenterWin(final boolean centerWin) {
	   this.prefs.putBoolean("vCenterWin",centerWin);
   }

   public final boolean isThumbnailSortDesend() {
	   return this.prefs.getBoolean("tSortDesend",THUMBNAIL_SORT_DESEND);
   }

   public final void setThumbnailSortDesend(final boolean sortDesend) {
	   this.prefs.putBoolean("tSortDesend",sortDesend);
   }

   public final boolean isImageViewerQuality() {
	   return this.prefs.getBoolean("vQuality",IMAGE_VIEWER_QUALITY);
   }

   public final void setSetImageViewerQuality(final boolean quality) {
	   this.prefs.putBoolean("vQuality",quality);
   }
   /**
    * @return Returns the vFitWin.
    */
   public final boolean isImageViewerShrinkFitWin() {
	   return this.prefs.getBoolean("vShrinkFitWin",IMAGE_VIEWER_SHRINK_TO_FIT_WINDOW);
   }
   /**
    * @param fitWin The vFitWin to set.
    */
   public final void setImageViewerShrinkFitWin(final boolean fitWin) {
	   this.prefs.putBoolean("vShrinkFitWin",fitWin);
   }
   /**
    * @return Returns the vFitWin.
    */
   public final boolean isImageViewerZoomFitWin() {
	   return this.prefs.getBoolean("vZoomFitWin",IMAGE_VIEWER_ZOOM_TO_FIT_WINDOW);
   }
   /**
    * @param fitWin The vFitWin to set.
    */
   public final void setImageViewerZoomFitWin(final boolean fitWin) {
	   this.prefs.putBoolean("vZoomFitWin",fitWin);
   }
   /**
    * @return Returns the vFullScreen.
    */
   public final boolean isImageViewerFullScreen() {
	   return this.prefs.getBoolean("vFullScreen",IMAGE_VIEWER_FULL_SCREEN);
   }
   /**
    * @param fullScreen The vFullScreen to set.
    */
   public final void setIMAGE_VIEWER_FULL_SCREEN(final boolean fullScreen) {
	   this.prefs.putBoolean("vFullScreen",fullScreen);
   }
   /**
    * @return Returns the vFullScreenHideMouse.
    */
   public final boolean isImageViewerFullScreenHideMouse() {
	   return this.prefs.getBoolean("vFullScreenHideMouse",IMAGE_VIEWER_HIDE_MOUSE);
   }
   /**
    * @param fullScreenHideMouse The vFullScreenHideMouse to set.
    */
   public final void setImageViewerFullScreenHideMouse(final boolean hidemouse) {
	   this.prefs.putBoolean("vFullScreenHideMouse",hidemouse);
   }
   /**
    * @return Returns the vOnTop.
    */
   public final boolean isImageViewerOnTop() {
	   return this.prefs.getBoolean("vOnTop",IMAGE_VIEWER_ON_TOP);
   }
   /**
    * @param onTop The vOnTop to set.
    */
   public final void setImageViewerOnTop(final boolean onTop) {
	   this.prefs.putBoolean("vOnTop",onTop);
   }

   public final boolean isImageViewerShowMenu() {
	   return this.prefs.getBoolean("vShowMenu",IMAGE_VIEWER_SHOW_MENU);
   }

   public final void setIMAGE_VIEWER_SHOW_MENU(final boolean showMenu) {
	   this.prefs.putBoolean("vShowMenu",showMenu);
   }
   public boolean isImageViewerShowFullPath() {
	   return this.prefs.getBoolean("vShowFullPath",IMAGE_VIEWER_SHOW_FULL_PATH);
   }

   public void setImageViewerShowFullPath(final boolean showFullPath) {
	   this.prefs.putBoolean("vShowFullPath",showFullPath);
   }
   /**
    * @return Returns the vSaveWinPos.
    */
   public boolean isImageViewerSaveWinPos() {
	   return this.prefs.getBoolean("vSaveWinPos",IMAGE_VIEWER_SAVE_WIN_POS);
   }
   /**
    * @param saveWinPos The vSaveWinPos to set.
    */
   public void setImageViewerSaveWinPos(final boolean saveWinPos) {
	   this.prefs.putBoolean("vSaveWinPos",saveWinPos);
   }
   /**
    * @return Returns the vWinFitImage.
    */
   public boolean isImageViewerWinFitImage() {
	   return this.prefs.getBoolean("vWinFitImage",IMAGE_VIEWER_WIN_FIT_IMAGE);
   }
   /**
    * @param winFitImage The vWinFitImage to set.
    */
   public final void setImageViewerWinFitImage(final boolean winFitImage) {
	   this.prefs.putBoolean("vWinFitImage",winFitImage);

	   if (isImageViewerWinFitScreen() && winFitImage) {
		setImageViewerWinFitScreen(!winFitImage);
	}
   }
   /**
    * @return Returns the vWinFitScreen.
    */
   public boolean isImageViewerWinFitScreen() {
	   return this.prefs.getBoolean("vWinFitScreen",IMAGE_VIEWER_WIN_FIT_SCREEN);
   }
   /**
    * @param winFitScreen The vWinFitScreen to set.
    */
   public void setImageViewerWinFitScreen(final boolean winFitScreen) {
	   this.prefs.putBoolean("vWinFitScreen",winFitScreen);

	   if (winFitScreen && isImageViewerWinFitImage()) {
		this.prefs.putBoolean("vWinFitImage",!winFitScreen);
	}
   }

   /**
    * @return the databaseType
    */
   public final String getDatabaseType() {
	   return this.prefs.get("databaseType", DATABASE_TYPE);
   }

   /**
    * @param DATABASE_TYPE the databaseType to set
    */
   public final void setDatabaseType(final String dbType) {
	   if ((dbType != null) && (dbType.startsWith("HSQL") || dbType.startsWith("MYSQL") || dbType.startsWith("OTHER"))) {
		this.prefs.put("databaseType", dbType);
	}
   }

   /**
    * @return the jdbcDriver
    */
   public final String getJDBCDriver() {
	   return this.prefs.get("jdbcDriver", JDBC_DRIVER);
   }

   /**
    * @param JDBC_DRIVER the jdbcDriver to set
    */
   public final void setJDBCDriver(final String jdbc_driver) {
	   if (jdbc_driver != null) {
		this.prefs.put("jdbcDriver", jdbc_driver);
	}
   }

   /**
    * @return the jdbcPassword
    */
   public final String getJDBCPassword() {
	   return this.prefs.get("jdbcPassword", JDBC_PASSWORD);
   }

   /**
    * @param JDBC_PASSWORD the jdbcPassword to set
    */
   public final void setJDBCPassword(final String jdbc_password) {
	   this.prefs.put("jdbcPassword", jdbc_password);
   }

   /**
    * @return the jdbcURL
    */
   public final String getJDBCURL() {
	   return this.prefs.get("jdbcURL", JDBC_URL);
   }

   /**
    * @param JDBC_URL the jdbcURL to set
    */
   public final void setJDBCURL(final String jdbc_URL) {
	   this.prefs.put("jdbcURL", jdbc_URL);
   }

   /**
    * @return the jdbcUserName
    */
   public final String getJBDCUserName() {
	   return this.prefs.get("jdbcUserName", JDBC_USER_NAME);
   }

   /**
    * @param JDBC_USER_NAME the jdbcUserName to set
    */
   public final void setJDBCUserName(final String jdbc_user_name) {
	   this.prefs.put("jdbcUserName", jdbc_user_name);
   }

   /**
    * @return the dateHeaderWidth
    */
   public final int getDateHeaderWidth() {
	   return this.prefs.getInt("dateHeaderWidth", DATE_HEADER_WIDTH);
   }

   /**
    * @param DATE_HEADER_WIDTH the dateHeaderWidth to set
    */
   public final void setDateHeaderWidth(final int date_header_width) {
	   this.prefs.putInt("dateHeaderWidth", date_header_width);
   }

   /**
    * @return the dimHeaderWidth
    */
   public final int getDimHeaderWidth() {
	   return this.prefs.getInt("dimHeaderWidth", DIM_HEADER_WIDTH);
   }

   /**
    * @param DIM_HEADER_WIDTH the dimHeaderWidth to set
    */
   public final void setDimHeaderWidth(final int dim_header_width) {
	   this.prefs.putInt("dimHeaderWidth", dim_header_width);
   }

   /**
    * @return the nameHeaderWidth
    */
   public final int getNameHeaderWidth() {
	   return this.prefs.getInt("nameHeaderWidth", NAME_HEADER_WIDTH);
   }

   /**
    * @param NAME_HEADER_WIDTH the nameHeaderWidth to set
    */
   public final void setNameHeaderWidth(final int name_header_width) {
	   this.prefs.putInt("nameHeaderWidth", name_header_width);
   }

   public final int getSizeHeaderWidth() {
	   return this.prefs.getInt("sizeHeaderWidth", SIZE_HEADER_WIDTH);
   }

   public final void setSizeHeaderWidth(final int size_header_width) {
	   this.prefs.putInt("sizeHeaderWidth", size_header_width);
   }

   public final boolean isShowDetailTableIcon() {
	   return this.prefs.getBoolean("showDetailTableIcon", SHOW_DETAIL_TABLE_ICON);
   }

   public final void setShowDetailTableIcon(final boolean showDetailTableIcon) {
	   this.prefs.putBoolean("showDetailTableIcon", showDetailTableIcon);
   }


   /**
    * @return the openWithKeys
    */
   public final String[] getOpenWithKeys() {
	   if (OPEN_WITH_KEYS != null) {
		return OPEN_WITH_KEYS.split("\\|");
	} else {
		return null;
	}
   }


   /**
    * @return the Vector<OpenWith>
    */
   public final Vector<OpenWith> getOpenWith() {
	   final String[] openWithKeys = this.prefs.get("openWithKeys","").split("\\|");
	   final Vector<OpenWith> results = new Vector<OpenWith>();
	   for(final String key: openWithKeys) {
		   final String owStr = this.prefs.get("OpenWith"+key, "");
		   if ((owStr != null) && (owStr.trim().length() > 0)) {
			   System.out.println("Open With Key = " + key);
			   results.add(new OpenWith(owStr.split("\\|")));
		   }
	   }
	   return results;
   }

   /**
    * @param openWith the openWithKeys to set
    */
   public final void setOpenWith(final Vector<OpenWith> openWith) {
	   final StringBuffer strBuf = new StringBuffer();
	   for(final OpenWith ow: openWith) {
		   strBuf.append(ow.getCommandName()+"|");
		   this.prefs.put("OpenWith"+ow.getCommandName(), ow.toString());
	   }
	   this.prefs.put("openWithKeys", strBuf.toString().substring(0,strBuf.toString().length()-1));
   }

   /**
    * @return the readOnly
    */
   public final boolean isReadOnly() {
	   return false;
   }

   /**
    * @param readOnly the readOnly to set
    */
   public final void setReadOnly(final boolean readOnly) {
   }

   public final boolean isLoadIconsRecursively() {
	   return this.prefs.getBoolean("loadIconsRecursively", LOAD_ICONS_RECURSIVELY);
   }

   public final void setLoadIconsRecursively(final boolean loadIconsRecursively) {
	   this.prefs.putBoolean("loadIconsRecursively", loadIconsRecursively);
   }

   public final boolean isLoadAllImageIcons() {
	   return this.prefs.getBoolean("loadAllImageIcons", LOAD_ALL_IMAGE_ICONS);
   }

   public final void setLoadAllImageIcons(final boolean loadAllImageIcons) {
	   this.prefs.putBoolean("loadAllImageIcons", loadAllImageIcons);
   }



   public static void main (final String[] args) {
	   JIPreferences.getInstance().setNameHeaderWidth(180);
	   JIPreferences.getInstance().setSizeHeaderWidth(70);
	   JIPreferences.getInstance().setDateHeaderWidth(120);
	   JIPreferences.getInstance().setDimHeaderWidth(95);

	   final OpenWith ow = new OpenWith();
	   ow.setCommandName("Photoshop");
	   ow.setCommand("C:\\Program Files\\Adobe\\Adobe Photoshop CS2\\Photoshop.exe");
	   ow.setWorkingDir("C:\\Program Files\\Adobe\\Adobe Photoshop CS2\\");
	   final Vector<OpenWith> vec = new Vector<OpenWith>();
	   vec.add(ow);
	   JIPreferences.getInstance().setOpenWith(vec);
	   final Vector<OpenWith> vec2 = JIPreferences.getInstance().getOpenWith();


	   final String[] openWithKeys = JIPreferences.getInstance().getOpenWithKeys();
	   for(final String key: openWithKeys) {
		System.out.println("Open With Key = " + key);
	}

	   for (final OpenWith openWith: vec) {
		System.out.println(openWith);
	}
	   System.out.println();
	   for (final OpenWith openWith: vec2) {
		System.out.println(openWith);
	}
	   System.out.println();
	   System.out.println(JIPreferences.getInstance().toString());
   }
}
