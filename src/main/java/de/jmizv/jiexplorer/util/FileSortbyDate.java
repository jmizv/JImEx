
package de.jmizv.jiexplorer.util;

import java.io.File;
import java.util.Comparator;

import de.jmizv.jiexplorer.gui.preferences.JIPreferences;


public class FileSortbyDate<T> implements Comparator<File> {
   public int compare(final File a, final File b) {
      final Long lnga = a.lastModified();
      final Long lngb = b.lastModified();
      return (JIPreferences.getInstance().isThumbnailSortDesend())
              ? lngb.compareTo(lnga)
              : lnga.compareTo(lngb);
   }
}
