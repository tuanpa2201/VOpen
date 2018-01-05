package modules.sys.view;

import java.util.List;

import base.controller.VController;
import base.controller.VEnv;
import modules.tracking.model.Vehicle;

public class NotificationTemplate {
  public static String getTemplate() {
	  StringBuilder templateBuilder = new StringBuilder();
	  templateBuilder.append("<div class=\"notification-container\">");
	  templateBuilder.append("<span class=\"notification-text\" style=\"width:100%\">");
	  templateBuilder.append("&nbsp;&nbsp Ban lãnh đạo <a href=\"http://vietek.com.vn/\" class=\"vietek\"> VIETEK </a>xin gửi lời chúc sức khỏe và cảm ơn chân thành tới quý khách hàng đã tin tưởng và sử dụng dịch vụ. </br>");
	  templateBuilder.append("Qua kiểm tra hệ thống, tài khoản của Quý khách có $vehicle_expire_size$ thiết bị đã hết thời gian sử dụng phí duy trì 1 năm.</br>");
	  templateBuilder.append("Danh sách bao gồm:<br/>");
	  templateBuilder.append("<a id=\"view_exp\" style=\"text-decoration: underline; color: red; cursor: pointer; margin: 5px 0\">");
	  templateBuilder.append("Danh sách xe</a></span>$List_vehicle_expire$</br>");			
	  templateBuilder.append("<span class=\"notification-text\" style=\"margin-top: 5px;width:100%\">");     
	  templateBuilder.append("<span style=\"margin-top: 5px\">");
	  templateBuilder.append("&nbsp;&nbsp Sau thời hạn đã thông báo trên, các thiết bị GSHT không được gia hạn mới sẽ dừng hoạt động. </br>");
	  templateBuilder.append("Để gia hạn dịch vụ cũng như giải đáp thắc mắc, quý khách vui lòng liên lạc với chúng tôi để được tư vấn đầy đủ. </br>");
	  templateBuilder.append("</span>  </span> <br/></div>");
      return templateBuilder.toString();
  }
  public static String getVehicleExpireNotification(List<Integer> vehicles) {
	  StringBuilder tableConten = new StringBuilder("<tr><th>TT</th><th>BKS</th><th>Ngày hết hạn</th><th>Ngày còn lại</th></tr>");
	  VController vehicleController = VEnv.sudo().get(Vehicle.modelName);
	  for (Integer id : vehicles) {
		  String tr = "<tr><th>TT</th><th>"+ vehicleController.getValue(id,"license_place") + "</th><th>" + vehicleController.getValue(id,"time_expiration") +"</th><th>" + vehicleController.getValue(id,"timeleft") + "</th></tr>";
		  tableConten.append(tr);
	  }
	  String conten = getTemplate();
	  conten.replace("$List_vehicle_expire$", tableConten.toString());
	  return conten;
  }
  public static String getNotificationFooter() {
	  StringBuilder templateBuilder = new StringBuilder();
	  templateBuilder.append("<div class = \"notification-footer\">");
	  templateBuilder.append("<div class = \"notification-company-info\">");
	  templateBuilder.append("<span class=\"notification-text\" style=\"font-weight: bold\">");
	  templateBuilder.append("Công ty Cổ phần phát triển Phần mềm Và Công nghệ Việt</span><br/>");
	  templateBuilder.append("<span style=\"font-size: 12px; margin-bottom: 10px;\">");
	  templateBuilder.append("Địa chỉ liên hệ&nbsp;&nbsp;&nbsp;: Tầng KT&nbsp;,&nbsp;Toà Nhà LICOGI 12&nbsp;,&nbsp;21 Đại Từ &nbsp;,&nbsp;Đại Kim&nbsp;,&nbsp;Hoàng Mai&nbsp;,&nbsp;Hà Nội");
	  templateBuilder.append("</span><br/>");
	  templateBuilder.append("<span style=\"font-size: 12px; margin-bottom: 10px\">");
	  templateBuilder.append("Điện thoại&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: 0466.819.667&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;&nbsp;0924.124.555"); 
	  templateBuilder.append("</span><br/>");
	  templateBuilder.append("<span style=\"font-size: 12px; margin-bottom: 10px\">");
	  templateBuilder.append("Email&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: Vietek@vietek.com.vn");
	  templateBuilder.append("</span></div>");
	  templateBuilder.append("</div> ");  	
	  return templateBuilder.toString();
	  
  }
}
