package de.jmizv.jiexplorer.db;


import org.apache.commons.pool.BasePoolableObjectFactory;

import de.jmizv.jiexplorer.gui.preferences.JIPreferences;


public class JIConnectionFactory extends BasePoolableObjectFactory {
	public static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JIConnectionFactory.class);

	@Override
	public Object makeObject() {
		log.debug("JIConnectionFactory::makeObject - DatabaseType "+JIPreferences.getInstance().getDatabaseType());
		JIGenericConnection conn = null;
		try {
			conn = new JIGenericConnection(JIPreferences.getInstance().getDatabaseType());
			return conn;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void destroyObject(final Object obj) throws Exception {
		log.debug("JIConnectionFactory::destroyObject - "+obj.getClass().getName());
		super.destroyObject(obj);
		((JIGenericConnection)obj).close();
	}
}
