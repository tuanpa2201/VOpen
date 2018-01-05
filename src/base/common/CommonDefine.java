package base.common;

public class CommonDefine {

	public static final int NUMBER_RECORD_OF_CBB = 10;
	public static String CAN_NOT_FOUND_TITLE = "Không tìm thấy title";

	public static String COMMON_VALIDATE_FORM_VALUES = "Các trường (*) không được để trống !";
	public static int MAX_LENGTH_CONTENT_FIELD = 255;
	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public static final String NUM_PATTERN = "[0-9]+";
	public static final String NUM_CHARACTERS_PATTERN = ".*[^0-9A-Za-z].*";
	public static final String SEARCH_PATTERN = "\\p{Punct}";
	public static String ERRORS_STRING_IS_EMPTY = " không được để trống !";
	public static String ERRORS_STRING_INVALID = " không hợp lệ !";
	public static String ERRORS_STRING_IS_EXIST = " đã tồn tại !";
	public static String ERRORS_STRING_IS_HAS_SPECIAL = "Tồn tại ký tự đặc biệt";
	public static String ERRORS_STRING_IS_LIMIT_MAXLENGTH = "Vượt quá độ dài cho phép";
	public static String TITLE_NOTIFY_CUSTOMER_DRIVER_REGISTER = "Mai Linh thong bao ";
	public static final String LOG_DEBUG = "LOG_DEBUG";
	public static final String LOG_TRACKING = "LOG_TRACKING";
	public static final String LOG_DRIVER = "LOG_DRIVER";
	public static final String LOG_AMQP = "LOG_AMQP";
	public static final String LOG_EXCEPTION_AMQP = "LOG_EXCEPTION_AMQP";
	public static final String CLUSTER_LABEL = "C";
	public static final String ELEVATOR_LABEL = "E";
	public static final String FLOOR_LABEL = "F";
	public static final String DOOR_LABEL = "D";

	public static class Tittle {
		public static final String TITLE_BTN_ADD_NEW = "Thêm mới";
		public static final String TITLE_BTN_EDIT = "Sửa";
		public static final String TITLE_BTN_DELETE = "Xóa";
		public static final String TITLE_BTN_REFRESH = "Refresh";
		public static final String TITLE_BTN_SEARCH = "Tìm kiếm";
		public static final String TITLE_BTN_SAVE = "Lưu";
		public static final String TITLE_BTN_UPLOAD = "Upload";
		public static final String TITLE_BTN_CLOSE = "Đóng";
	}

	public static class GoogleMap {

		public static String URL_ICON_POINT_START = "./themes/images/beginmapp.png";
		public static String URL_ICON_POINT_END = "./themes/images/endmapp.png";
		public static String URL_ICON_ADDRESS1 = "./themes/images/point_add1_48.png";
		public static String URL_ICON_ADDRESS2 = "./themes/images/point_add2_48.png";
		public static String URL_ICON_ADDRESS3 = "./themes/images/point_add3_48.png";

		public static double MAP_LAT = 21.0031545;
		public static double MAP_LNG = 105.8446598;
		public static int MAP_ZOOM = 15;
		public static int MAP_TYPE = 0;
	}

	public static class VehicleIcon {
		public static final String ICON_4SEATS_NON_PROCESSING = "./themes/images/Vehicles/icon_4seats_kokhach.png";
		public static final String ICON_4SEATS_PROCESSING = "./themes/images/Vehicles/icon_4seats_cokhach.png";
		public static final String ICON_4SEATS_PAUSE = "./themes/images/Vehicles/icon_4seats_pause.png";
		public static final String ICON_4SEATS_STOP = "./themes/images/Vehicles/icon_4seats_stop.png";
		public static final String ICON_7SEATS_NON_PROCESSING = "./themes/images/Vehicles/icon_7seats_kokhach.png";
		public static final String ICON_7SEATS_PROCESSING = "./themes/images/Vehicles/icon_7seats_cokhach.png";
		public static final String ICON_7SEATS_PAUSE = "./themes/images/Vehicles/icon_7seats_pause.png";
		public static final String ICON_7SEATS_STOP = "./themes/images/Vehicles/icon_7seats_stop.png";
		public static final String ICON_LOST_GPS = "./themes/images/Vehicles/icon_lost_gps.png";
		public static final String ICON_LOST_GSM = "./themes/images/Vehicles/icon_lost_gsm.png";
		public static final String ICON_MAINTAIN = "./themes/images/Vehicles/icon_maintain.png";
		public static final String ICON_MAINTAIN_16PX = "./themes/images/Vehicles/icon_maintain_16.png";
	}

	public static class VehicleIconNew {
		public static final String ICON_4SEATS_RUNNING = "./themes/images/VehiclesNew/car_running_32.png";
		public static final String ICON_4SEATS_PAUSE = "./themes/images/VehiclesNew/car_pause_32.png";
		public static final String ICON_4SEATS_STOP = "./themes/images/VehiclesNew/car_stop_32_red.png";
		public static final String ICON_LOST_GPS = "./themes/images/VehiclesNew/car_lost_gps_32.png";
		public static final String ICON_LOST_SIGNAL = "./themes/images/VehiclesNew/car_lost_gsm_32.png";
		public static final String ICON_OVER_SPEED = "./themes/images/VehiclesNew/car_over_speed_32.png";
	}

	public static class AnnotationTitle {
		public static String TITLE_NULLABLE = "nullable";
		public static String TITTLE_MIN_LENGHT = "minLength";
		public static String TITTLE_MAX_LENGHT = "maxLength";
		public static String TITTLE_REGEX = "regex";
		public static String TITLE_IS_HAS_SPECIAL_CHAR = "isHasSpecialChar";
		public static String TITLE_IS_EMAIL = "isEmail";
		public static String TITLE_ALOW_EXIST = "alowrepeat";
	}
}
