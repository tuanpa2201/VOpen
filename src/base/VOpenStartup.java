/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Jul 29, 2016
* Author: tuanpa
*
*/
package base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import base.model.VObject;
import base.util.HibernateUtil;
import base.util.VClassLoader;

/**
 * 
 * @author tuanpa
 *
 * Startup all service that will be run when deploy
 */
public class VOpenStartup extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4139945909777710750L;
	public static final Date startAppTime = new Date();
	
	private ArrayList<Class<?>> listService = new ArrayList<>();
	
	public static VOpenStartup instance = null;
	
	public VOpenStartup() {
		instance = this;
		clearServices();
		
		//Startup Hibernate for Base module
		HibernateUtil.startupSessionFactory();
		
		//Load all module
		VModuleManager.validateModules();
		
		//Hibernate service
		HibernateUtil.reloadSessionFactory(null);
		
		startServices();
	}
	
	public void addService(Class<?> serviceClass) {
		listService.add(serviceClass);
	}
	
	private void clearServices() {
		listService = new ArrayList<>();
	}
	
	private void startServices() {
		for (Class<?> serviceClass : listService) {
			try {
				serviceClass.newInstance();
			} catch (Exception e) {
				System.out.println("[ERROR] can not start service: " + serviceClass.getName());
			}
		}
	}
	public static void main(String[] args) {
		new VOpenStartup();
		String hql = "from " + VClassLoader.getModelClass("Sys.Menu").getName();
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		int count = 0;
		try {
			Query query = session.createQuery(hql);
			List<?> list = query.list();
			for (Object obj : list) {
				if (obj instanceof VObject) {
					VObject vobj = (VObject) obj;
					if (vobj.getValue("action") != null) {
						count ++;
						System.out.println(vobj);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		System.out.println(count);
	}
}
