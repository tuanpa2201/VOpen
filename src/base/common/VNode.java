package base.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import base.controller.VController;
import base.util.Filter;

public class VNode {
	private Integer data;
	private String label;
	private VNode parent = null;
	private Set<VNode> childrens;
	protected VController controller;
	private LinkedHashMap<Integer, VNode> diagrams = null;
	private boolean isLeaf = true;
	private boolean isRoot = false;

	private VNode(Integer data, VController controller) {
		// TODO Auto-generated constructor stub
		super();
		this.data = data;
		this.controller = controller;
		this.childrens = new HashSet<>();
		this.label = this.controller.getValue(data, "name") + "";
	}

	public VNode(VController controller) {
		// TODO Auto-generated constructor stub
		this.data = null;
		this.controller = controller;
		this.childrens = new HashSet<>();
		diagrams = new LinkedHashMap<>();
		this.isLeaf = false;
		this.isRoot = true;
	}

	public Integer getData() {
		return data;
	}

	public void setData(Integer data) {
		this.data = data;
	}

	public VNode getParent() {
		return parent;
	}

	public void setParent(VNode parent) {
		this.parent = parent;
	}

	public Set<VNode> getChildrens() {
		return childrens;
	}

	public void setChildrens(Set<VNode> childrens) {
		this.childrens = childrens;
	}

	public VNode addNode(Integer nodedata) {
		VNode node = new VNode(nodedata, this.controller);
		node.setParent(this);
		List<Integer> childrenDatas = getChildrenNearby(nodedata);
		if (childrenDatas.size() > 0) {
			diagrams.put(nodedata, node);
			node.setLeaf(false);
			node.setDiagrams(this.diagrams);
			for (Integer childrenData : childrenDatas) {
				node.addNode(childrenData);
			}
		}
		childrens.add(node);
		return node;
	}

	private List<Integer> getChildrenNearby(Integer father) {
		List<Integer> retVal = new ArrayList<>();
		Map<String, Object> params = new HashMap<>();
		params.put("parentid", father);
		Filter filter = new Filter("parentId.id =:parentid", params);
		List<Integer> childrens = controller.search(filter);
		if (childrens != null && childrens.size() > 0) {
			retVal.addAll(childrens);
		}
		return retVal;
	}

	public List<Integer> toList() {
		List<Integer> retList = new ArrayList<>();
		if (data != null) {
			retList.add(data);
		}
		if (childrens.size() > 0) {
			for (VNode children : childrens) {
				retList.addAll(children.toList());
			}
		}
		return retList;
	}

	public List<Integer> toList(Integer parentData) {
		List<Integer> listData = new ArrayList<>();
		VNode nodeParent = diagrams.get(parentData);
		if (nodeParent != null) {
			listData.addAll(nodeParent.toList());
		} else {
			listData.add(parentData);
		}
		return listData;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public void setDiagrams(LinkedHashMap<Integer, VNode> diagrams) {
		this.diagrams = diagrams;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isRoot() {
		return isRoot;
	}

}
