package base.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.controller.VController;
import base.util.Filter;
import base.util.ZKEnv;

public abstract class ReferenceInput extends InputReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected VController controller;
	protected Filter filter;
	public ReferenceInput referenceInput;
	public String modelName;
	protected String fieldDisplay = null;
	protected Integer totalSize = null;
	protected List<ItemData> cache = null;
	protected int startIndex = 0;
	protected int cacheSize = 30;
	public Map<String, Object> filterExtends = new HashMap<>();
	private String orderBy = "id asc";

	public ReferenceInput(String modelName) {
		initFilter("", new HashMap<>());
		controller = ZKEnv.getEnv().get(modelName);
		this.modelName = modelName;
	}

	@Override
	public void setValue(Object value) {
		super.setValue(value);
		filterData(value);
	}

	/**
	 * 
	 * @return current list data
	 * 
	 */
	public List<ItemData> getData() {
		List<ItemData> data = new ArrayList<>();
		List<Integer> ids = controller.search(filter);
		if (ids != null) {
			for (Integer id : ids) {
				ItemData item = new ItemData();
				item.setId(id);
				item.setLabel(controller.getDisplayString(id));
				data.add(item);
				data.add(initItemData(id));
			}
		}
		return data;
	}

	public void addSearch(String fieldName, Object value) {
		filterExtends.put(fieldName, value);
	}

	public void clearSearch(String fieldName) {
		filterExtends.remove(fieldName);
	}

	public ItemData initItemData(int id) {
		ItemData item = new ItemData();
		item.setId(id);
		if (fieldDisplay != null) {
			item.setLabel(controller.getValue(id, fieldDisplay) + "");
		} else {
			item.setLabel(controller.getDisplayString(id));
		}
		return item;
	}

	public Integer getTotalSize() {
		if (totalSize == null) {
			totalSize = controller.count(getFilter()).intValue();
		}
		return totalSize;
	}

	public ItemData getItemDataAt(int index) {
		int indexTmp = 0;
		if (cache == null || index < startIndex || index >= startIndex + cacheSize)
			loadPageData(index);
		indexTmp = index - startIndex;
		if (indexTmp < 0) {
			indexTmp = 0;
		}
		return cache.get(indexTmp);

	}

	private void loadPageData(int index) {
		// TODO Auto-generated method stub
		startIndex = (index / cacheSize) * cacheSize;
		List<Integer> ids = controller.search(getFilter(), orderBy, startIndex, cacheSize);
		cache = new ArrayList<>();
		for (Integer id : ids) {
			cache.add(initItemData(id));
		}
	}

	private Filter getFilter() {
		Filter filterLoad = new Filter();
		StringBuilder whereClause = new StringBuilder(filter.hqlWhereClause);
		Map<String, Object> params = new HashMap<>();
		params.putAll(filter.params);
		for (String fieldName : filterExtends.keySet()) {
			if (filterExtends.get(fieldName) instanceof Collection<?>) {
				whereClause.append(" and " + fieldName + " IN (:" + fieldName + ")");
				params.put(fieldName, filterExtends.get(fieldName));
			} else if (filterExtends.get(fieldName) instanceof String) {
				whereClause.append(" and " + fieldName + " like :" + fieldName);
				params.put(fieldName, "%" + filterExtends.get(fieldName) + "%");
			} else {
				whereClause.append(" and " + fieldName + " = :" + fieldName);
				params.put(fieldName, filterExtends.get(fieldName));
			}
		}
		filterLoad.hqlWhereClause = whereClause.toString();
		filterLoad.params.putAll(params);
		return filterLoad;
	}

	public void reset() {
		cache = null;
		totalSize = null;
	}

	public void filterData(Object data) {
		if (referenceInput != null) {
			referenceInput.addSearch(this.paramName, data);
			referenceInput.reset();
		}
	}

	List<ItemData> loadByString(String input, int size) {
		List<ItemData> retVal = new ArrayList<>();
		List<Integer> ids = controller.searchByString(getFilter(), input, size);
		for (Integer id : ids) {
			retVal.add(initItemData(id));
		}
		return retVal;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		boolean isEquals = false;
		if (((ReferenceInput) o).modelName.equals(this.modelName)) {
			isEquals = true;
		}
		return isEquals;
	}

	public void initFilter(String whereClause, Map<String, Object> params) {
		filter = new Filter();
		filter.hqlWhereClause = "isActive = :isActive_to_check " + whereClause;
		filter.params.put("isActive_to_check", true);
		filter.params.putAll(params);
	}

	public String getFieldDisplay() {
		return fieldDisplay;
	}

	public void setFieldDisplay(String fieldDisplay) {
		this.fieldDisplay = fieldDisplay;
	}

}
