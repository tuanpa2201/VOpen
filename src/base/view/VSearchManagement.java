package base.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.impl.XulElement;

import base.model.VField;

public class VSearchManagement implements HandlerSearchEvent {
	public static final int isAND = 1;
	public static final int isOR = 0;
	private List<VSearch> fieldSearchs;
	private HandlerSearchEvent handler;
	private Auxhead headrelement;
	private Hlayout hldetail;
	private Integer searchType;

	public VSearchManagement(int searchType) {
		this.searchType = searchType;
		fieldSearchs = new ArrayList<>();
		headrelement = new Auxhead();
		headrelement.setSclass("smart-form");
		hldetail = new Hlayout();
	}

	public void addfieldSearch(String fieldName, VField vfield) {
		XulElement aux = new Auxheader();
		if (vfield != null && vfield.searchable) {
			VSearch vsearch = new VSearch(fieldName, vfield, null);
			vsearch.setHandelSearch(this);
			hldetail.appendChild(vsearch.getDetailSearch());
			aux.appendChild(vsearch.getVeditor());
			vsearch.getVeditor().setParent(aux);
			fieldSearchs.add(vsearch);
		}
		if (fieldName != null && fieldName.toLowerCase().equals("stt")) {
			Image img = new Image("./themes/images/filter.png");
			img.setStyle("margin-right:3px");
			aux.appendChild(img);
		}
		headrelement.appendChild(aux);
	}

	public Map<String, String> getMapTypeCompare() {
		return new HashMap<>();
	}

	public List<VSearch> getFieldSearchs() {
		return fieldSearchs;
	}

	public void setFieldSearchs(List<VSearch> fieldSearchs) {
		this.fieldSearchs = fieldSearchs;
	}

	public XulElement getHeadrelement() {
		return headrelement;
	}

	public void setHeadrelement(Auxhead headrelement) {
		this.headrelement = headrelement;
	}

	public Hlayout getHldetail() {
		return hldetail;
	}

	public void setHldetail(Hlayout hldetail) {
		this.hldetail = hldetail;
	}

	public HandlerSearchEvent getHandler() {
		return handler;
	}

	public void setHandler(HandlerSearchEvent handler) {
		this.handler = handler;
	}

	public Integer getSearchType() {
		return searchType;
	}

	public void setSearchType(Integer searchType) {
		this.searchType = searchType;
	}

	public static int getIsand() {
		return isAND;
	}

	public static int getIsor() {
		return isOR;
	}

	public void executeSearch(String Whereclause, java.util.Map<String, Object> params) {
		String whereclau = "";
		Map<String, Object> param = new HashMap<>();
		for (VSearch vSearch : fieldSearchs) {
			if (vSearch.getParams().get(vSearch.getFieldName()) != null) {
				whereclau = whereclau + " and " + vSearch.getWhereClause();
				param.putAll(vSearch.getParams());
			}
		}
		handler.executeSearch(whereclau.replaceFirst(" and ", ""), param);
	}
}
