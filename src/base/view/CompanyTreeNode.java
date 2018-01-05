package base.view;

import java.util.Collection;

import org.zkoss.zul.DefaultTreeNode;

public class CompanyTreeNode extends DefaultTreeNode<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CompanyTreeNode(Integer data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	public CompanyTreeNode(Integer comChil, Collection<CompanyTreeNode> nodeChil) {
		// TODO Auto-generated constructor stub
		super(comChil, nodeChil);
	}

}
