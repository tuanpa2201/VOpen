/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Sep 5, 2016
* Author: tuanpa
*
*/
package base.view.editor;

import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Vlayout;

import base.model.VField;

public class ImageEditor extends VEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3976861586812158740L;

	public ImageEditor(VField field) {
		super(field);
	}

	Button btnUpload;
	Button btnClear;
	Image img;

	@Override
	public void initComponent() {
		component = new Vlayout();
		img = new Image();
		img.setWidth("100px");
		img.setHeight("100px");

		Hlayout hl = new Hlayout();

		component.appendChild(img);
		component.appendChild(hl);

		btnUpload = new Button("Upload");
		btnUpload.setWidth("50px");
		btnUpload.setHeight("min");
		btnClear = new Button("Clear");
		btnClear.setWidth("50px");
		btnClear.setHeight("min");
		btnClear.addEventListener(Events.ON_CLICK, this);

		hl.appendChild(btnUpload);
		hl.appendChild(btnClear);

		btnUpload.setUpload("true");
		btnUpload.addEventListener(Events.ON_UPLOAD, this);
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getTarget() == btnUpload) {
			UploadEvent uEvent = (UploadEvent) event;
			Media media = uEvent.getMedia();
			if (!media.getContentType().contains("image/jpeg") && !media.getContentType().contains("image/png")) {
				Messagebox.show("Upload sai định dạng file ảnh!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
			} else if (media.getByteData().length > 1000 * 1024) {
				Messagebox.show("Dung lượng file vượt quá 1MB!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
			} else {
				byte[] image = media.getByteData();
				setValue(image);
			}
		}
		if (event.getName().equals(Events.ON_CLICK) && event.getTarget().equals(btnClear)) {
			setValue(null);
		}
		super.onEvent(event);
	}

	public AImage byteToImage(byte[] imageBytes) {
		AImage decodedimg = null;
		if (imageBytes != null && imageBytes.length > 10) {
			try {
				decodedimg = new AImage("img", imageBytes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return decodedimg;
	}

	@Override
	public void setValue(Object value) {
		super.setValue(value);
		AImage aImage = byteToImage((byte[]) value);
		if (aImage != null) {
			img.setContent(aImage);
		} else {
			img.setSrc("./themes/images/noimage.png");
		}
	}

	@Override
	public void setReadonly(Boolean readonly) {
		btnUpload.setDisabled(readonly);
	}
}
