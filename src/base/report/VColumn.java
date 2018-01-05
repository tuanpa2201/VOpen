package base.report;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

public class VColumn {
	private Integer columnSize;
	private String columnName;
	private String columnTitle;
	private boolean searchable;
	private boolean isFrozen = false;
	private boolean isFunction = false;
	private String functionName;
	private String align;
	private String format;
	private EventListener<Event> eventListener;

	public VColumn(String colName, String colTitle, Integer size, boolean searchable) {
		this.columnName = colName;
		this.columnTitle = colTitle;
		this.searchable = searchable;
		this.columnSize = size;
	}

	public VColumn(boolean isfunction, String functionName, EventListener<Event> eventListener, String colTitle,
			Integer size, String align) {
		this.isFunction = isfunction;
		this.eventListener = eventListener;
		this.functionName = functionName;
		this.columnTitle = colTitle;
		this.columnSize = size;
		this.align = align;
	}

	public VColumn(String colName, String colTitle, Integer size, boolean searchable, boolean frozen) {
		this.columnName = colName;
		this.columnTitle = colTitle;
		this.searchable = searchable;
		this.columnSize = size;
		this.isFrozen = frozen;
	}
	public VColumn(String colName, String colTitle, Integer size, boolean searchable, String format) {
		this.columnName = colName;
		this.columnTitle = colTitle;
		this.searchable = searchable;
		this.columnSize = size;
		this.format = format;
	}
	
	public VColumn(String colName, String colTitle, Integer size, boolean searchable, boolean frozen, String format) {
		this.columnName = colName;
		this.columnTitle = colTitle;
		this.searchable = searchable;
		this.columnSize = size;
		this.isFrozen = frozen;
		this.format = format;
	}
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnTitle() {
		return columnTitle;
	}

	public void setColumnTitle(String columnTitle) {
		this.columnTitle = columnTitle;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	public void setColumnSize(Integer columnSize) {
		this.columnSize = columnSize;
	}

	public Integer getColumnSize() {
		return columnSize;
	}

	public boolean isFrozen() {
		return isFrozen;
	}

	public void setFrozen(boolean isFrozen) {
		this.isFrozen = isFrozen;
	}

	public boolean isFunction() {
		return isFunction;
	}

	public void setFunction(boolean isFunction) {
		this.isFunction = isFunction;
	}

	public EventListener<Event> getEventListener() {
		return eventListener;
	}

	public void setEventListener(EventListener<Event> eventListener) {
		this.eventListener = eventListener;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
