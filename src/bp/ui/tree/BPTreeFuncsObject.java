package bp.ui.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import bp.ui.scomp.BPTree.BPTreeNode;

public class BPTreeFuncsObject implements BPTreeFuncs
{
	protected Object root;

	public BPTreeFuncsObject(Object data)
	{
		root = data;
	}

	public List<?> getRoots()
	{
		List<Object> rc = new ArrayList<Object>();
		rc.add(root);
		return rc;
	}

	public List<?> getChildren(BPTreeNode node, boolean isdelta)
	{
		Object v = node.getUserObject();
		return getChildren(v);
	}

	public List<?> getChildren(Object v)
	{
		List<Object> rc = null;
		if (v != null)
		{
			if (v instanceof List)
			{
				rc = new ArrayList<Object>();
				List<?> vs = (List<?>) v;
				for (Object chd : vs)
				{
					rc.add(chd);
				}
			}
			else if (v instanceof Map)
			{
				rc = new ArrayList<Object>();
				Map<?, ?> vm = (Map<?, ?>) v;
				for (Entry<?, ?> entry : vm.entrySet())
				{
					rc.add(new Object[] { entry.getKey(), entry.getValue() });
				}
				rc.sort((a, b) ->
				{
					return ((String) ((Object[]) a)[0]).toLowerCase().compareTo(((String) ((Object[]) b)[0]).toLowerCase());
				});
			}
			else if (v.getClass().isArray())
			{
				rc = new ArrayList<Object>();
				Object chdvs = ((Object[]) v)[1];
				if (chdvs != null && (chdvs instanceof List || chdvs instanceof Map || chdvs.getClass().isArray()))
					rc.addAll(getChildren(chdvs));
				else
					rc.add(chdvs);
			}
		}
		return rc;
	}

	public boolean isLeaf(BPTreeNode node)
	{
		Object v = node.getUserObject();
		if (v == null)
			return false;
		return !(v instanceof List || v instanceof Map || v.getClass().isArray());
	}
}
