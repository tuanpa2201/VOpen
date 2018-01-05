/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 19, 2016
* Author: tuanpa
*
*/

package base.util;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServlet;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import base.VModuleDefine;
import base.VModuleManager;
import base.model.VModule;

public class HibernateUtil extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4767645896026910345L;
	private static SessionFactory sessionFactory = null;

	@SuppressWarnings("deprecation")
	private static SessionFactory buildSessionFactory(VModuleDefine newModDef) {
		try {
			Configuration cf = new Configuration();
			//Base model
			cf.addAnnotatedClass(VModule.class);
			
			ArrayList<VModuleDefine> modules = VModuleManager.getModuleInstalled();
			if (newModDef != null) {
				modules.add(newModDef);
			}
			Collection<Class<?>> baseClasses = VClassLoader.processBaseModel();
			
			for (Class<?> clazz : baseClasses) {
				cf.addAnnotatedClass(clazz);
			}
			
			Collection<Class<?>> modelClasses = VClassLoader.processModel(modules);
			//Installed module model
			for (Class<?> clazz : modelClasses) {
				cf.addAnnotatedClass(clazz);
			}
			
			//Remove all cache
			VCache.refreshAll();
			
			return cf.buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void startupSessionFactory() {
		try {
			Configuration cf = new Configuration();
			Collection<Class<?>> modelClasses = VClassLoader.processBaseModel();
			
			//Installed module model
			for (Class<?> clazz : modelClasses) {
				cf.addAnnotatedClass(clazz);
			}
			sessionFactory = cf.buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	
	
	/**
	 * Only reload when install, remove module
	 */
	public static void reloadSessionFactory(VModuleDefine newModDef) {
		sessionFactory = buildSessionFactory(newModDef);
	}

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			sessionFactory = buildSessionFactory(null);
		}
		return sessionFactory;
	}
}
