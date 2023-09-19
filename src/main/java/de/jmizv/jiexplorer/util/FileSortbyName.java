package de.jmizv.jiexplorer.util;

import java.io.File;
import java.util.Comparator;

import de.jmizv.jiexplorer.gui.preferences.JIPreferences;



public final class FileSortbyName<T> implements Comparator<File> {
   public int compare(final File a, final File b) {
      final String stra = a.getName();
      final String strb = b.getName();
      return (JIPreferences.getInstance().isThumbnailSortDesend())
              ? strb.compareToIgnoreCase(stra)
              : stra.compareToIgnoreCase(strb);
   }
}
