package base.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zul.AbstractListModel;

import base.util.Filter;
import base.util.StringUtils;

public abstract class AbstractModelReport implements HandlerSearchReport {

	/**
	 * 
	 */
	private List<RowReport> cache = null;
	protected boolean isPaging = false;
	private Integer count = null;
	private int cacheSize = 30;
	private int startIndex = 0;
	protected Filter filter;
	protected Map<String, Object> params;

	public AbstractModelReport(boolean isPaging) {
		// TODO Auto-generated constructor stub
		this.params = new HashMap<>();
		this.filter = new Filter();
		this.isPaging = isPaging;
	}

	public void initModel() {
		if (!isPaging) {
			loadAllData();
			count = cache.size();
		}

	}

	public void initInputs(Map<String, InputReport> inputs) {
		refresh();
		params.put("_beginTime", inputs.get("timeBegin").getValue());
		params.put("_endTime", inputs.get("timeStop").getValue());
	}

	private void loadPageData(int index) {
		startIndex = (index / cacheSize) * cacheSize;
		cache = getPageData(startIndex, cacheSize);
	}

	private void loadAllData() {
		cache = getAllData();
	}

	private RowReport getReportAt(int index) {
		int indexTmp = 0;
		if (isPaging) {
			if (cache == null || index < startIndex || index >= startIndex + cacheSize)
				loadPageData(index);
			indexTmp = index - startIndex;
			if (indexTmp < 0) {
				indexTmp = 0;
			}
		} else {
			indexTmp = index;
		}
		return cache.get(indexTmp);
	}

	/**
	 * 
	 * @return total rows report
	 */
	public abstract int getTotalSize();

	/**
	 * 
	 * @param startIndex
	 *            - begin index page
	 * @param cacheSize
	 *            - size page
	 * @return all data in page
	 */
	public abstract List<RowReport> getPageData(int startIndex, int cacheSize);

	/**
	 * 
	 * @return LoadALL Data for Report, used when no paging
	 */
	public abstract List<RowReport> getAllData();

	private void refresh() {
		params = new HashMap<>();
		cache = null;
		count = null;
	}

	public List<?> getDataToExport() {
		List<?> listDataExport = null;
		if (isPaging) {
			listDataExport = getPageData(0, getTotalSize());
		} else {
			listDataExport = getAllData();
		}
		return listDataExport;
	}

	public Integer getCount() {
		return count;
	}

	@Override
	public void executeSearch(Map<String, Object> valuesSearch) {
		// TODO Auto-generated method stub
		String whereClause = "";
		for (String paramName : valuesSearch.keySet()) {
			whereClause = whereClause + " and " + paramName + " =:" + paramName;
		}
		filter.hqlWhereClause = StringUtils.Trim(whereClause.trim(), "and");
		filter.params = valuesSearch;
	}

	public listModel getModel() {
		listModel model = new listModel();
		initModel();
		return model;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	class listModel extends AbstractListModel<RowReport> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public RowReport getElementAt(int rowIndex) {
			// TODO Auto-generated method stub
			return getReportAt(rowIndex);
		}

		@Override
		public int getSize() {
			// TODO Auto-generated method stub
			if (count == null) {
				count = getTotalSize();
			}
			return count;
		}

	}
}
