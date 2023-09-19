package de.jmizv.jiexplorer.util;

import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JOptionPane;


public class StockDialogs
{

    public static final String ignoreRetryCancel[] = {
        "Ignore", "Retry", "Cancel"
    };
    public static final String yesAllNo[] = {
        "Yes", "Yes for All", "No", "No for All"
    };
    public static final String yesNo[] = {
        "Yes", "No"
    };
    public static final String retryCancel[] = {
        "Retry", "Cancel"
    };

    public StockDialogs() { }

    public static int ignoreRetryCancel(final JComponent jcomponent, final String s, final String s1) {
        return JOptionPane.showOptionDialog(jcomponent, s, s1, 1, 3, null, ignoreRetryCancel, ignoreRetryCancel[0]);
    }

    public static int overrideAllConfirmation(final JComponent jcomponent, final String s, final String s1) {
    	return JOptionPane.showOptionDialog(jcomponent, s, s1, 0, 3, null, yesAllNo, yesAllNo[0]);
    }

    public static int overrideConfirmation(final JComponent jcomponent, final String s, final String s1) {
        return JOptionPane.showConfirmDialog(jcomponent, s, s1, 0, 3);
    }

    public static int retryCancel(final JComponent jcomponent, final String s, final String s1) {
        return JOptionPane.showOptionDialog(jcomponent, s, s1, 0, 3, null, retryCancel, retryCancel[0]);
    }

    public static int yesNo(final Frame frame, final String s, final String s1) {
    	if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			return JOptionPane.showOptionDialog(frame, s, s1, 0, 3, null, yesNo, yesNo[0]);
		}
    	return 0;
    }
    public static int yesNo(final JComponent jcomponent, final String s, final String s1) {
    	if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			return JOptionPane.showOptionDialog(jcomponent, s, s1, 0, 3, null, yesNo, yesNo[0]);
		}
    	return 0;
    }

}