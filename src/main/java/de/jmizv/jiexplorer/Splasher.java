package de.jmizv.jiexplorer;

public class Splasher {
   /**
    * Shows the splash screen, launches the application and then disposes
    * the splash screen.
    * @param args the command line arguments
    */
	public static void main(final String[] args) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
		System.setProperty("apple.awt.rendering", "VALUE_RENDER_SPEED"); // VALUE_RENDER_SPEED or VALUE_RENDER_QUALITY
		System.setProperty("apple.awt.interpolation", "VALUE_INTERPOLATION_NEAREST_NEIGHBOR"); // VALUE_INTERPOLATION_NEAREST_NEIGHBOR, VALUE_INTERPOLATION_BILINEAR, or VALUE_INTERPOLATION_BICUBIC
		System.setProperty("apple.awt.showGrowBox", "true");
		System.setProperty("com.apple.mrj.application.growbox.intrudes","false");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name","JIExplorer");

		SplashWindow.splash(Splasher.class.getResource("/icons/JIExplorerSplash.jpg"));
		SplashWindow.invokeMain("de.jmizv.jiexplorer.JILoader", args);
		SplashWindow.disposeSplash();
	}

}
