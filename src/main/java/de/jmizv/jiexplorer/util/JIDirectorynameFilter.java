package de.jmizv.jiexplorer.util;

import java.io.File;
import java.io.FilenameFilter;


public class JIDirectorynameFilter implements FilenameFilter {


   public boolean accept(final File dir, final String fname) {
      if (fname.startsWith(".")) {
		return false;
	}
      if (new File(dir,fname).isDirectory()) {
		return true;
	}
      return false;
   }
}