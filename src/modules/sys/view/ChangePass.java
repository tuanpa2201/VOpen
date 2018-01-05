package modules.sys.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import base.controller.VActionResponse;
import base.controller.VController;
import base.controller.VEnv;
import base.model.VField;
import base.model.VObject;
import base.util.SecurityUtils;
import base.view.editor.VEditor;

public class ChangePass extends Window implements EventListener<Event> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Button btnConfirm;

	private VEditor vEditorUserName;
	private VEditor vEditorCurrentPass;
	private VEditor vEditorNewPass;
	private VEditor vEditorConfirmPass;

	private VObject obj;

	public ChangePass(VObject obj) {
		this.obj = obj;
		this.init();
	}

	private void init() {
		this.setClosable(true);
		this.setMaximizable(true);
		this.setTitle("CHANGE PASSWORD");
		this.setStyle("padding : 5px; font-family : Times New Roman; font-weight : bold; font-size : 14px");
		this.setWidth("400px");
		this.setHeight("300px");
		this.setBorder("normal");
		this.setPosition("center,center");
		this.setAction("show : slideDown; hide : slideUp");

		Vlayout vlayout = new Vlayout();
		vlayout.setParent(this);
		vlayout.setHflex("1");
		vlayout.setVflex("1");

		this.createForm(vlayout);
	}

	private void createForm(Vlayout vlayout) {
		Grid gridDetail = new Grid();
		gridDetail.setParent(vlayout);
		gridDetail.setHflex("1");

		Columns cols = new Columns();
		cols.setParent(gridDetail);

		Column col = new Column();
		col.setParent(cols);
		col.setWidth("40%");

		col = new Column();
		col.setParent(cols);
		col.setWidth("10%");

		col = new Column();
		col.setParent(cols);
		col.setWidth("50%");

		Rows rows = new Rows();
		rows.setParent(gridDetail);

		Row row = new Row();
		row.setParent(rows);

		Label label = new Label("User name :");
		label.setParent(row);
		label.setStyle("color : black ; font-family : Time New Roman; font-size : 14px");

		label = new Label("");
		label.setParent(row);
		label.setStyle("color : red");

		VField vFieldUserName = VField.string("", new HashMap<>());
		vEditorUserName = VEditor.getEditor(vFieldUserName);
		row.appendChild(vEditorUserName.component);
		vEditorUserName.setValue("" + obj.getValue("username"));

		row = new Row();
		row.setParent(rows);

		label = new Label("Current password :");
		label.setParent(row);
		label.setStyle("color : black ; font-family : Time New Roman; font-size : 14px");

		label = new Label("(*)");
		label.setParent(row);
		label.setStyle("color : red");

		VField vFieldCurrentPass = VField.string("", new HashMap<>());
		vEditorCurrentPass = VEditor.getEditor(vFieldCurrentPass);
		row.appendChild(vEditorCurrentPass.component);
		vEditorCurrentPass.setValue("");
		((Textbox) vEditorCurrentPass.component).setType("password");

		row = new Row();
		row.setParent(rows);

		label = new Label("New password :");
		label.setParent(row);
		label.setStyle("color : black ; font-family : Time New Roman; font-size : 14px");

		label = new Label("(*)");
		label.setParent(row);
		label.setStyle("color : red");

		VField vFieldNewPass = VField.string("", new HashMap<>());
		vEditorNewPass = VEditor.getEditor(vFieldNewPass);
		row.appendChild(vEditorNewPass.component);
		vEditorNewPass.setValue("");
		((Textbox) vEditorNewPass.component).setType("password");

		row = new Row();
		row.setParent(rows);

		label = new Label("Re-enter new password :");
		label.setParent(row);
		label.setStyle("color : black ; font-family : Time New Roman; font-size : 14px");

		label = new Label("(*)");
		label.setParent(row);
		label.setStyle("color : red");

		VField vFieldConfirmPass = VField.string("", new HashMap<>());
		vEditorConfirmPass = VEditor.getEditor(vFieldConfirmPass);
		row.appendChild(vEditorConfirmPass.component);
		vEditorConfirmPass.setValue("");
		((Textbox) vEditorConfirmPass.component).setType("password");

		row = new Row();
		row.setParent(rows);

		label = new Label("");
		label.setParent(row);

		label = new Label("");
		label.setParent(row);

		btnConfirm = new Button("Confirm");
		btnConfirm.setParent(row);
		btnConfirm.setStyle("color : black ; font-family : Time New Roman; font-weight : bold; font-size : 14px");
		btnConfirm.addEventListener(Events.ON_CLICK, this);
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (event.getTarget().equals(btnConfirm) && event.getName().equals(Events.ON_CLICK)) {
			String oldPass = (String) vEditorCurrentPass.getValue();
			String newPass = (String) vEditorNewPass.getValue();
			System.err.println(newPass);
			String confirmPass = (String) vEditorConfirmPass.getValue();
			String oldPassMD5 = SecurityUtils.encryptMd5(oldPass);

			btnConfirm.setDisabled(true);
			if (oldPass == null || oldPass.length() == 0) {
				Messagebox.show("Không để trống trường password hiện tại!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
				btnConfirm.setDisabled(false);
			} else if (newPass == null || newPass.length() == 0) {
				Messagebox.show("Không để trống trường password mới!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
				btnConfirm.setDisabled(false);
			} else if (confirmPass == null || confirmPass.length() == 0) {
				Messagebox.show("Không để trống trường password xác thực!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
				btnConfirm.setDisabled(false);
			} else {
				if (!oldPassMD5.equals(obj.getValue("password"))) {
					Messagebox.show("Nhập sai password!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
					btnConfirm.setDisabled(false);
				} else if (!isPassword(newPass)) {
					Messagebox.show("Password mới không dưới 4 ký tự hoặc vượt quá 25 ký tự!", "Lỗi", Messagebox.OK,
							Messagebox.ERROR);
					btnConfirm.setDisabled(false);
				} else if (!newPass.equals(confirmPass)) {
					Messagebox.show("Nhập sai confirmPass", "Lỗi", Messagebox.OK, Messagebox.ERROR);
					btnConfirm.setDisabled(false);
				} else {
					VController vContrl = VEnv.sudo().get("Sys.User");
					List<Integer> lstIds = new ArrayList<>();
					lstIds.add(obj.getId());
					Map<String, Object> values = new HashMap<>();
					values.put("password", SecurityUtils.encryptMd5(newPass));
					VActionResponse vres = vContrl.update(lstIds, values);
					if (vres.status) {
						Messagebox.show("Đổi password thành công!", "Thông báo", Messagebox.OK, Messagebox.INFORMATION,
								new EventListener<Event>() {

									@Override
									public void onEvent(Event arg0) throws Exception {
										setVisible(false);
									}
								});
					} else {
						Messagebox.show("Đổi password không thành công!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
					}
				}
			}
		}
	}

	// Check password validate
	private boolean isPassword(String str) {
		boolean temp = false;
		if (str.length() < 4 || str.length() > 25) {
			temp = false;
		} else {
			temp = true;
		}
		return temp;
	}
}
