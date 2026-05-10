package bp.ui.tree;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bp.typeext.Pair;
import bp.typeext.Pair.PairBase;
import bp.ui.scomp.BPTree.BPTreeNode;

public class BPTreeFuncsObject extends BPTreeFuncsAbstract implements BPTreeFuncs
{
	protected Object m_root;
	protected boolean m_sortkey;

	public BPTreeFuncsObject(Object data)
	{
		m_root = data;
	}

	public List<?> getRoots()
	{
		List<Object> rc = new ArrayList<Object>();
		rc.add(m_root);
		return rc;
	}

	public void setSortKey(boolean issortkey)
	{
		m_sortkey = issortkey;
	}

	public void initNode(BPTreeNode node, Object userobj)
	{
		super.initNode(node, userobj);
		if (userobj != null && userobj instanceof Pair)
			node.setVirtual(true);
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
				for (Object k : vm.keySet())
				{
					rc.add(new PairBase<>(k, vm.get(k)));
				}
				if (m_sortkey)
				{
					rc.sort((a, b) -> ((Pair<?, ?>) a).compareToByLeftText((Pair<?, ?>) b));
				}
			}
			else if (v instanceof Pair)
			{
				rc = new ArrayList<Object>();
				rc.add(((Pair<?, ?>) v).getRight());
			}
			else if (v.getClass().isArray())
			{
				rc = new ArrayList<Object>();
				Object[] arr = null;
				if (v instanceof Object[])
				{
					arr = (Object[]) v;
				}
				else
				{
					int l = Array.getLength(v);
					arr = new Object[l];
					System.arraycopy(v, 0, arr, 0, l);
				}
				for (Object data : arr)
					rc.add(data);
			}
		}
		return rc;
	}

	public boolean isLeaf(BPTreeNode node)
	{
		Object v = node.getUserObject();
		if (v == null)
			return false;
		return !(v instanceof List || v instanceof Map || v instanceof Pair || v.getClass().isArray());
	}
}
