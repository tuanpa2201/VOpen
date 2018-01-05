package base.common;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.exporter.excel.ExcelExporter;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;

public class CommonUtils {
	public static boolean checkNotNull(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof String) {
			if (((String) obj).trim().length() == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Export result from grid to excel file, note that grid in single page mode
	 * to allow all data exported into excel
	 * 
	 * @author Dzungnd
	 * @param grid
	 *            data need exporting
	 * @param filename
	 *            output file name
	 * @throws Exception
	 */
	public static void exportListboxToExcel(@BindingParam("ref") Component component, String filename) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ExcelExporter exporter = new ExcelExporter();
			Listbox listbox = null;
			Grid grid = null;
			if (component instanceof Grid) {
				grid = (Grid) component;
				grid.renderAll();

				exporter.export(grid, out);
			}
			if (component instanceof Listbox) {
				listbox = (Listbox) component;
				listbox.renderAll();

				exporter.export(listbox, out);
			}
			AMedia amedia = new AMedia(filename, "xlsx", "application/file", out.toByteArray());
			Filedownload.save(amedia);
			out.close();
		} catch (Exception e) {
			AppLogger.logDebug.error("", e);
		}
	}

	public static boolean containsNumber(String c) {
		for (int i = 0; i < c.length(); ++i) {
			if (Character.isDigit(c.charAt(i)) == false)
				return false;
		}
		return true;
	}

	
	public static boolean matchRegex(String regex, String input){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input.toLowerCase());
		return matcher.matches();
	}
}
