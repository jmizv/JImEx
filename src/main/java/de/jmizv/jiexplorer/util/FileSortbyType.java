
package de.jmizv.jiexplorer.util;

import java.io.File;
import java.util.Comparator;

import de.jmizv.jiexplorer.gui.preferences.JIPreferences;


public class FileSortbyType<T> implements Comparator<File> {
   public int compare(final File a, final File b) {
      final String stra = a.getName();
      final String strb = b.getName();
      final int indexA = stra.lastIndexOf(".");
      final int indexB = strb.lastIndexOf(".");
      final String strA = (indexA > -1 ? stra.substring(indexA) : "")+stra;
      final String strB = (indexB > -1 ? strb.substring(indexB) : "")+strb;
      final int result = (JIPreferences.getInstance().isThumbnailSortDesend())
              ? strB.compareToIgnoreCase(strA)
              : strA.compareToIgnoreCase(strB);
      return (result != 0)?result:(JIPreferences.getInstance().isThumbnailSortDesend())
              ? strb.compareToIgnoreCase(stra)
              : stra.compareToIgnoreCase(strb);
   }
}