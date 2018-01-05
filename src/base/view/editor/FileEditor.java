/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Sep 5, 2016
* Author: tuanpa
*
*/
package base.view.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zkmax.zul.Filedownload;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hlayout;

import base.model.VField;
import base.util.AppUtils;

public class FileEditor extends VEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3976861586812158740L;

	public FileEditor(VField field) {
		super(field);
	}
	
	Button btUpload;
	Button btDownload;
	Button btDelete;
	
	@Override
	public void initComponent() {
		component = new Hlayout();
		btUpload = new Button("Upload");
		btDownload = new Button("Download");
		btDelete = new Button("Delete");
		
		component.appendChild(btUpload);
		component.appendChild(btDownload);
		component.appendChild(btDelete);
		
		btUpload.setUpload("true,maxsize=50000"); //Max 50M
		btUpload.addEventListener(Events.ON_UPLOAD, this);
		
		btDownload.addEventListener(Events.ON_CLICK, this);
		btDelete.addEventListener(Events.ON_CLICK, this);
	}
	
	private File saveToFile(Media media) {
		String SAVE_PATH = AppUtils.getSourcePath().replace("/WEB-INF/classes", "");
		File baseDir = new File(SAVE_PATH + "uploadfile");
		if (!baseDir.exists()) {
			baseDir.mkdirs();
		}
		File file = new File(SAVE_PATH + media.getName());
		try {
			OutputStream fout = new FileOutputStream(file);
			if (media.isBinary()) {
				fout.write(media.getByteData());
			}
			else {
				fout.write(media.getStringData().getBytes());
			}
			fout.flush();
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
			file = null;
		}
		return file;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getTarget() == btUpload) {
			UploadEvent uEvent = (UploadEvent) event;
			Media media = uEvent.getMedia();
			File file = saveToFile(media);
			FTPClient ftpClient = new FTPClient();
			try {
				ftpClient.connect("125.212.226.54", 1821);
				ftpClient.login("ftpuser", "ftpuser");
				ftpClient.enterLocalPassiveMode();
				InputStream inputStream = new FileInputStream(file);
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				ftpClient.storeFile("vopen/" + media.getName(), inputStream);
				ftpClient.disconnect();
				setValue(media.getName());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		} else if (event.getTarget() == btDownload) {
			FTPClient ftpClient = new FTPClient();
			try {
				ftpClient.connect("125.212.226.54", 1821);
				ftpClient.login("ftpuser", "ftpuser");
				ftpClient.enterLocalPassiveMode();
				String SAVE_PATH = AppUtils.getSourcePath().replace("/WEB-INF/classes", "");
				File baseDir = new File(SAVE_PATH + "uploadfile");
				if (!baseDir.exists()) {
					baseDir.mkdirs();
				}
				File file = new File(SAVE_PATH + value.toString());
				OutputStream fout = new FileOutputStream(file);
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				ftpClient.retrieveFile("vopen/" + value.toString(), fout);
				fout.close();
				ftpClient.disconnect();
				Media media = new AMedia(file, null, null);
				Filedownload.save(media);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		} else if (event.getTarget() == btDelete) {
			setValue(null);
		}
		super.onEvent(event);
	}



	@Override
	public void setReadonly(Boolean readonly) {
		btUpload.setDisabled(readonly);
		btDownload.setDisabled(readonly);
		btDelete.setDisabled(readonly);
	}
}
