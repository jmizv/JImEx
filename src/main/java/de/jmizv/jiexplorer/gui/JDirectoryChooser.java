package de.jmizv.jiexplorer.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class JDirectoryChooser extends JFileChooser {

    /**
	 *
	 */
	private static final long serialVersionUID = 6109893815706775816L;
	private final String mFileDescription = "Directory";

    public JDirectoryChooser() {
    	super();

    	setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
    	setFileFilter( new DirectoryFilter() ) ;
    }

    public int showChooser(final JFrame aParent , final String aSelectButton ) {
        return showDialog(aParent, aSelectButton );
    }

    class DirectoryFilter extends javax.swing.filechooser.FileFilter {

    	@Override
		public boolean accept( final File aFilterFile ) {
    		return ( aFilterFile.isDirectory() );
    	}

    	@Override
		public String getDescription() {
    		return JDirectoryChooser.this.mFileDescription;
    	}
    }
}
