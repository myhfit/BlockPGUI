package bp.ui.tree;

import java.util.ArrayList;
import java.util.List;

import bp.res.BPResource;
import bp.ui.scomp.BPTree.BPTreeNode;

public class BPTreeFuncsResource extends BPTreeFuncsAbstract
{
	protected List<BPResource> m_roots;

	public void setRoots(List<BPResource> roots)
	{
		m_roots = roots;
	}

	public List<?> getRoots()
	{
		return m_roots;
	}

	public List<?> getChildren(BPTreeNode node, boolean isdelta)
	{
		Object obj = node.getUserObject();
		List<BPResource> rc = new ArrayList<BPResource>();
		if (obj != null && obj instanceof BPResource)
		{
			BPResource res = (BPResource) obj;
			BPResource[] sub = res.listResources();
			if (sub != null && sub.length > 0)
			{
				for (int i = 0; i < sub.length; i++)
					rc.add(sub[i]);
			}
		}
		return rc;
	}

	public boolean isLeaf(BPTreeNode node)
	{
		Object obj = node.getUserObject();
		if (obj != null && obj instanceof BPResource)
		{
			BPResource res = (BPResource) obj;
			return res.isLeaf();
		}
		else if(node.isRoot())
			return false;
		else
			return true;
	}

}
