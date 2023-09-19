package de.jmizv.jiexplorer.util;

import java.util.Comparator;

import de.jmizv.jiexplorer.gui.datetree.JIDateTreeNode;


public class MonthNameComparator<T> implements Comparator<JIDateTreeNode> {
	final static String[] ABREV_MONTH_NAMES = {"jan","feb","mar","apr","may","jun","jul","aug","sep","oct","nov","dec"};
	public int compare(final JIDateTreeNode o1, final JIDateTreeNode o2) {

		return getMonth(o1.toString()) - getMonth(o2.toString());
	}

	private int getMonth(final String month) {
		final String mnth = month.trim().substring(0, 3).toLowerCase();
		for (int i = 0; i < ABREV_MONTH_NAMES.length; i++) {
			if (mnth.equals(ABREV_MONTH_NAMES[i])) {
				return i;
			}
		}
		return -1;
	}
}
