
package de.jmizv.jiexplorer.util;

import java.io.File;
import java.util.Comparator;

import de.jmizv.jiexplorer.gui.preferences.JIPreferences;


public class FileSortbyPath<T> implements Comparator<File> {
   public int compare(final File a, final File b) {
      final String stra = a.getPath();
      final String strb = b.getPath();
      return (JIPreferences.getInstance().isThumbnailSortDesend())
              ? strb.compareToIgnoreCase(stra)
              : stra.compareToIgnoreCase(strb);
   }
}