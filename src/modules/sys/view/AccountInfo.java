package modules.sys.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Vlayout;

import base.controller.VActionResponse;
import base.controller.VController;
import base.controller.VEnv;
import base.model.VField;
import base.model.VObject;
import base.util.ZKEnv;
import base.view.editor.ImageEditor;
import base.view.editor.VEditor;

public class AccountInfo extends Div implements EventListener<Event> {

	/**
	 * @author MPV Account Infomation
	 */
	private static final long serialVersionUID = 1L;

	private Button btnChangeIfo;
	private Button btnChangePass;
	private Div divAvatar;
	private VObject currentUser = ZKEnv.getEnv().user;
	private VEditor vEditorFullName;
	private VEditor vEditorUserName;
	private VEditor vEditorBirthDay;
	private VEditor vEditorIDNo;
	private VEditor vEditorAddress;
	private VEditor vEditorPhone;
	private VEditor vEditorEmail;
	private ImageEditor imageEditor;
	private Radiogroup radiogroup;
	private Radio chMale;
	private Radio chFeMale;
	private String sex = "male";
	private VField vFieldFullName;
	private VField vFieldbirthday;
	private VField vFieldPhone;
	private VField vFieldUserName;
	private VField vFieldID;
	private VField vFieldAdd;
	private VField vFieldEmail;

	public AccountInfo() {
		this.init();
	}

	private void init() {
		this.setStyle("padding : 5px; font-family : Times New Roman; font-weight : bold; font-size : 14px");
		this.setAction("show : slideDown; hide : slideUp");
		this.setHflex("1");
		this.setVflex("min");

		Vlayout vlayout = new Vlayout();
		vlayout.setParent(this);
		vlayout.setHflex("1");
		vlayout.setVflex("min");

		this.createTopForm(vlayout);
		this.createDetailForm(vlayout);

	}

	private void createTopForm(Vlayout vlayout) {
		Div div = new Div();
		div.setParent(vlayout);
		div.setHflex("1");

		Groupbox group = new Groupbox();
		group.setParent(div);

		Caption caption = new Caption();
		caption.setParent(group);
		caption.setLabel("Details information");

		divAvatar = new Div();
		divAvatar.setParent(group);

		VField vFieldImage = VField.image("Avatar", new HashMap<>());
		imageEditor = new ImageEditor(vFieldImage);
		imageEditor.setParent(divAvatar);
		imageEditor.setValue(currentUser.getValue("avatar"));
	}

	private void createDetailForm(Vlayout vlayout) {
		Grid gridDetail = new Grid();
		gridDetail.setParent(vlayout);
		gridDetail.setHflex("1");

		Columns cols = new Columns();
		cols.setParent(gridDetail);

		Column col = new Column();
		col.setParent(cols);
		col.setWidth("30%");

		col = new Column();
		col.setParent(cols);
		col.setWidth("10%");

		col = new Column();
		col.setParent(cols);
		col.setWidth("60%");

		Rows rows = new Rows();
		rows.setParent(gridDetail);

		Row row = new Row();
		row.setParent(rows);

		Label label = new Label("Full name :");
		label.setParent(row);
		label.setStyle("color : black ; font-family : Time New Roman; font-size : 14px");

		label = new Label("(*)");
		label.setParent(row);
		label.setStyle("color : red");

		vFieldFullName = VField.string("", new HashMap<>());
		vEditorFullName = VEditor.getEditor(vFieldFullName);
		row.appendChild(vEditorFullName.component);
		vEditorFullName.setValue(currentUser.getValue("name"));

		row = new Row();
		row.setParent(rows);

		label = new Label("User name :");
		label.setParent(row);
		label.setStyle("color : black ; font-family : Time New Roman; font-size : 14px");

		label = new Label("");
		label.setParent(row);
		label.setStyle("color : red");

		vFieldUserName = VField.string("", new HashMap<>());
		vEditorUserName = VEditor.getEditor(vFieldUserName);
		row.appendChild(vEditorUserName.component);
		vEditorUserName.setValue(currentUser.getValue("username"));

		row = new Row();
		row.setParent(rows);

		label = new Label("Date of birth :");
		label.setParent(row);
		label.setStyle("color : black ; font-family : Time New Roman; font-size : 14px");

		label = new Label("(*)");
		label.setParent(row);
		label.setStyle("color : red");

		vFieldbirthday = VField.date("", new HashMap<>());
		vEditorBirthDay = VEditor.getEditor(vFieldbirthday);
		row.appendChild(vEditorBirthDay.component);
		if(currentUser.getValue("birthday") == null){
		  vEditorBirthDay.setValue(new Date());
		}else{
		  vEditorBirthDay.setValue(currentUser.getValue("birthday"));
		}
		((Datebox) vEditorBirthDay.component).setFormat("dd/MM/yyyy");
		row = new Row();
		row.setParent(rows);

		label = new Label("Sex :");
		label.setParent(row);
		label.setStyle("color : black ; font-family : Time New Roman; font-size : 14px");

		label = new Label("");
		label.setParent(row);
		label.setStyle("color : red");

		Hlayout hlSex = new Hlayout();
		hlSex.setParent(row);
		
		radiogroup = new Radiogroup();
		radiogroup.setParent(hlSex);
		radiogroup.setName("radiogroup");
		chMale = new Radio("Male");
		chMale.setName("male");
		chMale.setParent(radiogroup);
		chMale.setRadiogroup(radiogroup);
		chFeMale = new Radio("Female");
		chFeMale.setName("female");
		chFeMale.setParent(radiogroup);
		chFeMale.setRadiogroup(radiogroup);
		if(currentUser.getValue("sex") ==null || currentUser.getValue("sex") == "male"){
		  chMale.setChecked(true);
		}else{
		  chFeMale.setChecked(true);
		}
		radiogroup.addEventListener(Events.ON_CHECK, new EventListener<Event>() {

      @Override
      public void onEvent(Event arg0) throws Exception {
        sex = radiogroup.getSelectedItem().getName();
        
      }
    });
		row = new Row();
		row.setParent(rows);

		label = new Label("ID No/ Passport No :");
		label.setParent(row);
		label.setStyle("color : black ; font-family : Time New Roman; font-size : 14px");

		label = new Label("(*)");
		label.setParent(row);
		label.setStyle("color : red");

		vFieldID = VField.string("", new HashMap<>());
		vEditorIDNo = VEditor.getEditor(vFieldID);
		row.appendChild(vEditorIDNo.component);
		if(currentUser.getValue("passport") !=null){
		  vEditorIDNo.setValue(currentUser.getValue("passport"));
		}
		

		row = new Row();
		row.setParent(rows);

		label = new Label("Address :");
		label.setParent(row);
		label.setStyle("color : black ; font-family : Time New Roman; font-size : 14px");

		label = new Label("(*)");
		label.setParent(row);
		label.setStyle("color : red");

		vFieldAdd = VField.string("", new HashMap<>());
		vEditorAddress = VEditor.getEditor(vFieldAdd);
		row.appendChild(vEditorAddress.component);
		if(currentUser.getValue("address") !=null){
		  vEditorAddress.setValue(currentUser.getValue("address"));
    }

		row = new Row();
		row.setParent(rows);

		label = new Label("Phone number :");
		label.setParent(row);
		label.setStyle("color : black ; font-family : Time New Roman; font-size : 14px");

		label = new Label("(*)");
		label.setParent(row);
		label.setStyle("color : red");

		vFieldPhone = VField.string("", new HashMap<>());
		vEditorPhone = VEditor.getEditor(vFieldPhone);
		row.appendChild(vEditorPhone.component);
		if(currentUser.getValue("phoneNumber") !=null){
		  vEditorPhone.setValue(currentUser.getValue("phoneNumber"));
    }

		row = new Row();
		row.setParent(rows);

		label = new Label("E-mail :");
		label.setParent(row);
		label.setStyle("color : black ; font-family : Time New Roman; font-size : 14px");

		label = new Label("(*)");
		label.setParent(row);
		label.setStyle("color : red");

		vFieldEmail = VField.string("", new HashMap<>());
		vEditorEmail = VEditor.getEditor(vFieldEmail);
		row.appendChild(vEditorEmail.component);
		if(currentUser.getValue("email") !=null){
		  vEditorEmail.setValue(currentUser.getValue("email"));
    }

		row = new Row();
		row.setParent(rows);

		label = new Label("");
		label.setParent(row);
		label.setStyle("color : black ; font-family : Time New Roman; font-size : 14px");

		label = new Label("");
		label.setParent(row);
		label.setStyle("color : red");

		Hlayout hlayoutBtn = new Hlayout();
		hlayoutBtn.setParent(row);

		btnChangeIfo = new Button("Save Information");
		btnChangeIfo.setParent(hlayoutBtn);
		btnChangeIfo.setStyle("color : black ; font-family : Time New Roman; font-weight : bold; font-size : 14px");
		btnChangeIfo.addEventListener(Events.ON_CLICK, this);

		btnChangePass = new Button("Change Password");
		btnChangePass.setParent(hlayoutBtn);
		btnChangePass.setStyle("color : black ; font-family : Time New Roman; font-weight : bold; font-size : 14px");
		btnChangePass.addEventListener(Events.ON_CLICK, this);
	}

	
	@Override
	public void onEvent(Event event) throws Exception {
	  
		if (event.getTarget().equals(btnChangePass) && event.getName().equals(Events.ON_CLICK)) {
			ChangePass changpass = new ChangePass(currentUser);
			changpass.setParent(this.getParent());
			changpass.doModal();
//			this.detach();

		}

		if (event.getTarget().equals(btnChangeIfo) && event.getName().equals(Events.ON_CLICK)) {
			VController vController = VEnv.sudo().get("Sys.User");
			List<Integer> lstIds = new ArrayList<>();
			lstIds.add(currentUser.getId());

			if (vEditorFullName.getValue() == null || vEditorFullName.getValue().toString().length() == 0) {
				Messagebox.show("Không để trống trường tên!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
			} else if (vEditorPhone.getValue() == null || vEditorPhone.getValue().toString().length() == 0) {
				Messagebox.show("Không để trống trường số điện thoại!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
			} else if (vEditorEmail.getValue() == null || vEditorEmail.getValue().toString().length() == 0) {
				Messagebox.show("Không để trống trường Email!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
			} else {
				Map<String, Object> values = new HashMap<>();
				values.put("name", vEditorFullName.getValue());
				values.put("avatar", imageEditor.getValue());
				values.put("sex", sex);
				values.put("passport", vEditorIDNo.getValue());
			  values.put("address", vEditorAddress.getValue());
			  values.put("phoneNumber", vEditorPhone.getValue());
			  values.put("email", vEditorEmail.getValue());
				VActionResponse vres = vController.update(lstIds, values);
				if (vres.status) {
				  Messagebox.show("Lưu thông tin thành công!", "Thông tin", Messagebox.OK, Messagebox.INFORMATION);
				} else {
					Messagebox.show("Lưu không thành công!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
				}
			}
		}
	}
}
