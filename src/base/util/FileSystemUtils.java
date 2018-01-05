/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 1, 2016
* Author: tuanpa
*
*/
package base.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import base.VModuleDefine;
import modules.smarthome.ShLogger;

public class FileSystemUtils {
	
	public static void copyResource(String fromUri, String toUri) {
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			// read this file into InputStream
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fromUri);

			File copyFile = new File(toUri);
			if (copyFile.exists())
				copyFile.delete();
			copyFile.getParentFile().mkdirs();
			
			outputStream = new FileOutputStream(copyFile);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

		} catch (IOException e) {
			e.printStackTrace();
			ShLogger.LOGGER.error("CopyFile|From:" + fromUri + "|To:" + toUri, e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}
	public static void copyResource(VModuleDefine modDef) {
		try {
		//Delete all resource if any
		deleteResource(modDef);
		//Copy resource
		if (modDef.getResources() != null) {
			for (String resourceUrl : modDef.getResources()) {
				String newURL = Thread.currentThread().getContextClassLoader().getResource(resourceUrl).getPath();
				newURL = newURL.replace("WEB-INF/classes", "");
				FileSystemUtils.copyResource(resourceUrl, newURL);
				ShLogger.LOGGER.info("CopyFile|URL:" + newURL);
			}
		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteResource(VModuleDefine modDef) {
		
		String filePath = AppUtils.getSourcePath().replace("WEB-INF/classes", "") + modDef.getWebPath();
		File file = new File(filePath);
		try {
			delete(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void delete(File file)
	    	throws IOException{
	 
	    	if(file.isDirectory()){
	 
	    		//directory is empty, then delete it
	    		if(file.list().length==0){
	    			
	    		   file.delete();
	    		   System.out.println("Directory is deleted : " 
	                                                 + file.getAbsolutePath());
	    			
	    		}else{
	    			
	    		   //list all the directory contents
	        	   String files[] = file.list();
	     
	        	   for (String temp : files) {
	        	      //construct the file structure
	        	      File fileDelete = new File(file, temp);
	        		 
	        	      //recursive delete
	        	     delete(fileDelete);
	        	   }
	        		
	        	   //check the directory again, if empty then delete it
	        	   if(file.list().length==0){
	           	     file.delete();
	        	     System.out.println("Directory is deleted : " 
	                                                  + file.getAbsolutePath());
	        	   }
	    		}
	    		
	    	}else{
	    		//if file, then delete it
	    		file.delete();
	    		System.out.println("File is deleted : " + file.getAbsolutePath());
	    	}
	    }
}
