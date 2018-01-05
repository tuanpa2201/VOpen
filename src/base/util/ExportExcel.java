package base.util;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.zkoss.util.media.AMedia;
import org.zkoss.zul.Filedownload;

import base.report.RowReport;
import base.report.VColumn;

public class ExportExcel {
	private static final XSSFColor COLOR_GREY_HIGH = new XSSFColor(new Color(126, 131, 162));
	private static final XSSFColor COLOR_GREY_LOW = new XSSFColor(new Color(230, 228, 228));
	private static final String FONT_NAME = "Times New Roman";

	private String fileName;
	private String reportName;
	private String reportTimeRange;
	private String extendHeader;
	private List<String> columnName;
	private Boolean addExportTime;

	public ExportExcel() {
		this.addExportTime = true;
		this.columnName = new ArrayList<>();
		this.fileName = "Export Data Viewer.xlsx";
		this.reportName = "Report Content";
		this.reportTimeRange = "(No content avaiable)";
		this.extendHeader = "Creater: Copyright by Vietek©" + Calendar.getInstance().get(Calendar.YEAR);
	}

	public ExportExcel(String filename, String reportname, String timerange, String extend) {
		this.fileName = filename;
		this.reportName = reportname;
		this.reportTimeRange = timerange;
		this.extendHeader = extend;
		this.columnName = new ArrayList<>();
		this.addExportTime = true;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportTimeRange() {
		return reportTimeRange;
	}

	public void setReportTimeRange(String reportTimeRange) {
		this.reportTimeRange = reportTimeRange;
	}

	public String getExtendHeader() {
		return extendHeader;
	}

	public void setExtendHeader(String extendHeader) {
		this.extendHeader = extendHeader;
	}

	public List<String> getColumnName() {
		return columnName;
	}

	public void setColumnName(List<String> columnName) {
		this.columnName = columnName;
	}

	public Boolean getAddExportTime() {
		return addExportTime;
	}

	public void setAddExportTime(Boolean addExportTime) {
		this.addExportTime = addExportTime;
	}

	public void getContent(List<?> list, XSSFSheet ms, Short startindex) {
		for (Object item : list) {
			try {
				Method method = item.getClass().getMethod("getReportRow", XSSFSheet.class, Short.class,
						item.getClass());
				method.invoke(item.getClass().newInstance(), ms, startindex, item);
				startindex++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void getContent(List<VColumn> columns, List<?> list, XSSFSheet ms, Short startindex) {
		Short rowIndex = 0;
		for (Object item : list) {
			try {
				getReportData(ms, startindex, rowIndex, item, columns);
				rowIndex++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void getReportData(XSSFSheet sheet, Short startIndex, Short rowIndex, Object clazz, List<VColumn> columns) {
		try {
			XSSFColor COLOR_GREY_HIGH = new XSSFColor(new Color(126, 131, 162));
			Row row = sheet.createRow(startIndex + rowIndex);
			row.setHeight((short) (1.5 * sheet.getDefaultRowHeight()));
			XSSFCreationHelper createHelper = sheet.getWorkbook().getCreationHelper();
			XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBottomBorderColor(COLOR_GREY_HIGH);
			style.setBorderLeft(BorderStyle.THIN);
			style.setLeftBorderColor(COLOR_GREY_HIGH);
			style.setBorderRight(BorderStyle.THIN);
			style.setRightBorderColor(COLOR_GREY_HIGH);
			style.setBorderTop(BorderStyle.THIN);
			style.setTopBorderColor(COLOR_GREY_HIGH);
			style.setWrapText(true);

			int colIndex = 0;
			for (VColumn column : columns) {
				XSSFCellStyle currentstyle = (XSSFCellStyle) style.clone();
				Cell cell = row.createCell(colIndex);
				Object val = null;
				if (column.getColumnName() != null && column.getColumnName().equals("stt")) {
					val = rowIndex + 1;
				} else {
					val = ((RowReport) clazz).getValue(column.getColumnName());
				}

				if (val != null) {
					if (Integer.class.isAssignableFrom(val.getClass()) || int.class.isAssignableFrom(val.getClass())) {
						cell.setCellValue((Integer) val);
						cell.setCellStyle(style);
					} else if (Float.class.isAssignableFrom(val.getClass())
							|| float.class.isAssignableFrom(val.getClass())) {
						cell.setCellValue((Float) val);
						cell.setCellStyle(style);
					} else if (Double.class.isAssignableFrom(val.getClass())
							|| double.class.isAssignableFrom(val.getClass())) {
						cell.setCellValue((Double) val);
						cell.setCellStyle(style);
					} else if (String.class.isAssignableFrom(val.getClass())) {
						cell.setCellValue((String) val);
						cell.setCellStyle(style);
					} else if (Date.class.isAssignableFrom(val.getClass())) {
						Date date = (Date) val;
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						int hours = cal.get(Calendar.HOUR_OF_DAY);
						int minute = cal.get(Calendar.MINUTE);
						int second = cal.get(Calendar.SECOND);

						if ((hours + minute + second) == 0) {
							currentstyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
						} else {
							currentstyle
									.setDataFormat(createHelper.createDataFormat().getFormat("HH:mm:ss dd/MM/yyyy"));
						}
						cell.setCellValue((Date) val);
						cell.setCellStyle(currentstyle);
					} else if (Timestamp.class.isAssignableFrom(val.getClass())) {
						long timestamp = ((Timestamp) val).getTime();
						Date date = new Date(timestamp);
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						int hours = cal.get(Calendar.HOUR_OF_DAY);
						int minute = cal.get(Calendar.MINUTE);
						int second = cal.get(Calendar.SECOND);

						if ((hours + minute + second) == 0) {
							currentstyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
						} else {
							currentstyle
									.setDataFormat(createHelper.createDataFormat().getFormat("HH:mm:ss dd/MM/yyyy"));
						}
						cell.setCellValue(date);
						cell.setCellStyle(currentstyle);
					}
					cell.setCellStyle(currentstyle);
				}
				colIndex++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getHeaderContent(XSSFSheet ms, List<VColumn> fields, int startindex) {
		Row row = ms.createRow(startindex++);
		row.setHeight((short) (1.5 * ms.getDefaultRowHeight()));
		int index = 0;
		for (VColumn vColumn : fields) {
			Cell cell = row.createCell((short) index);
			XSSFCellStyle style = getHeaderStyle(ms.getWorkbook(), 12, null, true, false, COLOR_GREY_HIGH);
			if (!this.getColumnName().isEmpty()) {
				if (this.getColumnName().get(index) != null) {
					cell.setCellValue(new XSSFRichTextString(this.getColumnName().get(index)));
				} else {
					cell.setCellValue(new XSSFRichTextString(
							Translate.translate(ZKEnv.getEnv(), null, vColumn.getColumnTitle())));
				}
			} else {
				cell.setCellValue(
						new XSSFRichTextString(Translate.translate(ZKEnv.getEnv(), null, vColumn.getColumnTitle())));
			}

			cell.setCellStyle(style);
			index++;
		}
	}

	public void getHeaderContent(XSSFSheet ms, Field[] fields, int startindex) {
		Row row = ms.createRow(startindex++);
		row.setHeight((short) (1.5 * ms.getDefaultRowHeight()));
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			Cell cell = row.createCell((short) i);
			XSSFCellStyle style = getHeaderStyle(ms.getWorkbook(), 12, null, true, false, COLOR_GREY_HIGH);
			if (!this.getColumnName().isEmpty()) {
				if (this.getColumnName().get(i) != null) {
					cell.setCellValue(new XSSFRichTextString(this.getColumnName().get(i)));
				} else {
					cell.setCellValue(new XSSFRichTextString(f.getName()));
				}
			} else {
				cell.setCellValue(new XSSFRichTextString(f.getName()));
			}

			cell.setCellStyle(style);
		}
	}

	public XSSFCellStyle getHeaderStyle(XSSFWorkbook wb, int fontsize, String fontname, Boolean bold, Boolean italic,
			XSSFColor color) {
		XSSFCellStyle styleSubHeader = (XSSFCellStyle) wb.createCellStyle();

		XSSFFont xSSFFont = wb.createFont();
		if (fontname == null) {
			xSSFFont.setFontName(FONT_NAME);
		} else {
			xSSFFont.setFontName(fontname);
		}
		if (bold == null) {
			xSSFFont.setBold(true);
		} else {
			xSSFFont.setBold(bold);
		}
		if (italic == null) {
			xSSFFont.setItalic(false);
		} else {
			xSSFFont.setItalic(italic);
		}
		xSSFFont.setFontHeightInPoints((short) fontsize);
		styleSubHeader.setFont(xSSFFont);

		styleSubHeader.setVerticalAlignment(VerticalAlignment.CENTER);
		styleSubHeader.setAlignment(HorizontalAlignment.CENTER);
		if (color != null) {
			styleSubHeader.setFillForegroundColor(COLOR_GREY_LOW);
			styleSubHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		styleSubHeader.setBorderBottom(BorderStyle.THIN);
		styleSubHeader.setBottomBorderColor(COLOR_GREY_HIGH);
		styleSubHeader.setBorderLeft(BorderStyle.THIN);
		styleSubHeader.setLeftBorderColor(COLOR_GREY_HIGH);
		styleSubHeader.setBorderRight(BorderStyle.THIN);
		styleSubHeader.setRightBorderColor(COLOR_GREY_HIGH);
		styleSubHeader.setBorderTop(BorderStyle.THIN);
		styleSubHeader.setTopBorderColor(COLOR_GREY_HIGH);
		styleSubHeader.setWrapText(true);
		return styleSubHeader;
	}

	private XSSFCellStyle getTimeStyle(XSSFWorkbook wb, int fontsize, String fontname) {
		XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();

		XSSFFont xSSFFont = wb.createFont();
		if (fontname == null) {
			xSSFFont.setFontName(FONT_NAME);
		} else {
			xSSFFont.setFontName(fontname);
		}
		xSSFFont.setBold(false);
		xSSFFont.setItalic(true);
		xSSFFont.setFontHeightInPoints((short) fontsize);
		style.setFont(xSSFFont);

		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setAlignment(HorizontalAlignment.RIGHT);

		style.setWrapText(true);
		return style;
	}

	private void setupMergingOnValidCells(XSSFSheet sheet, CellRangeAddress region, XSSFCellStyle cellStyle,
			String cellvalue) {
		for (int rowNum = region.getFirstRow(); rowNum <= region.getLastRow(); rowNum++) {
			Row row = sheet.createRow(rowNum);
			for (int colNum = region.getFirstColumn(); colNum <= region.getLastColumn(); colNum++) {
				XSSFCell currentCell = (XSSFCell) row.getCell(colNum);
				if (currentCell == null) {
					currentCell = (XSSFCell) row.createCell(colNum);
					if (colNum == region.getFirstColumn()) {
						currentCell.setCellValue(new XSSFRichTextString(cellvalue));
					}
				}
				currentCell.setCellStyle(cellStyle);
			}
		}
		sheet.addMergedRegion(region);
	}

	public void exportExcel(List<VColumn> columns, List<?> list) {
		List<String> colnames = new ArrayList<>();
		for (VColumn vColumn : columns) {
			colnames.add(vColumn.getColumnTitle());
		}
		Short rowIndex = 1;

		XSSFWorkbook my_workbook = new XSSFWorkbook();
		XSSFSheet my_sheet = my_workbook.createSheet("Report Temp");

		rowIndex = getWorkingSheet(my_workbook, my_sheet, rowIndex, columns.size());

		getHeaderContent(my_sheet, columns, rowIndex++);

		getContent(columns, list, my_sheet, rowIndex);

		saveFileToLocal(my_sheet, columns.size());
	}

	public Short getWorkingSheet(XSSFWorkbook my_workbook, XSSFSheet my_sheet, Short rowIndex, int colCount) {

		XSSFCellStyle style;
		CellRangeAddress region;

		// them thoi gian ket xuat file
		if (this.getAddExportTime()) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
			Date date = new Date();
			style = getTimeStyle(my_workbook, 10, null);
			region = new CellRangeAddress(rowIndex, rowIndex++, 0, colCount - 1);
			setupMergingOnValidCells(my_sheet, region, style, dateFormat.format(date));
		}

		// them ten bao cao
		if (this.getReportName() != null && !this.getReportName().isEmpty()) {
			style = getHeaderStyle(my_workbook, 15, null, true, false, null);
			region = new CellRangeAddress(rowIndex, rowIndex++, 0, colCount - 1);
			setupMergingOnValidCells(my_sheet, region, style, this.getReportName());
		}

		// them thoi gian bao cao
		if (this.getReportTimeRange() != null && !this.getReportTimeRange().isEmpty()) {
			style = getHeaderStyle(my_workbook, 11, null, false, true, null);
			region = new CellRangeAddress(rowIndex, rowIndex++, 0, colCount - 1);
			setupMergingOnValidCells(my_sheet, region, style, this.getReportTimeRange());
		}

		// them thong tin bo sung khac
		if (this.getExtendHeader() != null && !this.getExtendHeader().isEmpty()) {
			style = getHeaderStyle(my_workbook, 11, null, false, true, null);
			region = new CellRangeAddress(rowIndex, rowIndex++, 0, colCount - 1);
			setupMergingOnValidCells(my_sheet, region, style, this.getExtendHeader());
		}

		return rowIndex;
	}

	public void saveFileToLocal(XSSFSheet my_sheet, int colCount) {
		try {
			for (int i = 0; i < colCount; i++) {
				my_sheet.autoSizeColumn(i);
			}

			/* Write changes to the workbook */
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			my_sheet.getWorkbook().write(out);
			AMedia amedia = new AMedia(getFileName(), "xlsx", "application/file", out.toByteArray());
			Filedownload.save(amedia);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exportExcel(Class<?> clazz, List<?> list) {
		try {
			Field[] fields = clazz.getDeclaredFields();
			int colCount = fields.length;
			Short rowIndex = 1;

			XSSFWorkbook my_workbook = new XSSFWorkbook();
			XSSFSheet my_sheet = my_workbook.createSheet("Report Temp");

			rowIndex = getWorkingSheet(my_workbook, my_sheet, rowIndex, colCount);

			getHeaderContent(my_sheet, fields, rowIndex++);

			getContent(list, my_sheet, rowIndex);

			saveFileToLocal(my_sheet, colCount);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Row getRowData(XSSFSheet sheet, Short index, Object clazz)
			throws IllegalArgumentException, IllegalAccessException {
		XSSFColor COLOR_GREY_HIGH = new XSSFColor(new Color(126, 131, 162));
		Row row = sheet.createRow(index);
		row.setHeight((short) (1.5 * sheet.getDefaultRowHeight()));
		XSSFCreationHelper createHelper = sheet.getWorkbook().getCreationHelper();
		XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(COLOR_GREY_HIGH);
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(COLOR_GREY_HIGH);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(COLOR_GREY_HIGH);
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(COLOR_GREY_HIGH);
		style.setWrapText(true);

		Field[] comps = clazz.getClass().getDeclaredFields();
		for (int i = 0; i < comps.length; i++) {
			Field f = comps[i];
			XSSFCellStyle currentstyle = (XSSFCellStyle) style.clone();
			f.setAccessible(true);
			Cell cell = row.createCell(i);

			if (Integer.class.isAssignableFrom(f.getType()) || int.class.isAssignableFrom(f.getType())) {
				cell.setCellValue((Integer) f.get(clazz));
				cell.setCellStyle(style);
			} else if (Float.class.isAssignableFrom(f.getType()) || float.class.isAssignableFrom(f.getType())) {
				cell.setCellValue((Float) f.get(clazz));
				cell.setCellStyle(style);
			} else if (Double.class.isAssignableFrom(f.getType()) || double.class.isAssignableFrom(f.getType())) {
				cell.setCellValue((Double) f.get(clazz));
				cell.setCellStyle(style);
			} else if (String.class.isAssignableFrom(f.getType())) {
				cell.setCellValue((String) f.get(clazz));
				cell.setCellStyle(style);
			} else if (Date.class.isAssignableFrom(f.getType())) {
				Date date = (Date) f.get(clazz);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				int hours = cal.get(Calendar.HOUR_OF_DAY);
				int minute = cal.get(Calendar.MINUTE);
				int second = cal.get(Calendar.SECOND);

				if ((hours + minute + second) == 0) {
					currentstyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
				} else {
					currentstyle.setDataFormat(createHelper.createDataFormat().getFormat("HH:mm:ss dd/MM/yyyy"));
				}
				cell.setCellValue((Date) f.get(clazz));
				cell.setCellStyle(currentstyle);
			} else if (Timestamp.class.isAssignableFrom(f.getType())) {
				long timestamp = ((Timestamp) f.get(clazz)).getTime();
				Date date = new Date(timestamp);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				int hours = cal.get(Calendar.HOUR_OF_DAY);
				int minute = cal.get(Calendar.MINUTE);
				int second = cal.get(Calendar.SECOND);

				if ((hours + minute + second) == 0) {
					currentstyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
				} else {
					currentstyle.setDataFormat(createHelper.createDataFormat().getFormat("HH:mm:ss dd/MM/yyyy"));
				}
				Date val = new Date(timestamp);
				cell.setCellValue(val);
				cell.setCellStyle(currentstyle);
			}
			cell.setCellStyle(currentstyle);
		}

		return row;
	}

	/*
	 * public Row getReportRow(XSSFSheet ms, Short startindex, AvengeTeam
	 * avengeTeam){ try { return getRowData(ms, startindex, avengeTeam); } catch
	 * (Exception e) { e.printStackTrace(); return null; } }
	 */
}
