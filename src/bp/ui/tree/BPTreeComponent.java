package bp.ui.tree;

import java.awt.Component;
import java.util.List;
import java.util.function.Function;

import javax.swing.Action;

import bp.ui.BPComponent;
import bp.ui.scomp.BPTree;
import bp.ui.scomp.BPTree.BPTreeNode;

public interface BPTreeComponent<C extends Component> extends BPComponent<C>
{
	void setTreeFuncs(BPTreeFuncs funcs);

	default BPComponentType getComponentType()
	{
		return BPComponentType.TREE;
	}

	BPTreeNode getSelectedNode();

	Object getSelectedNodeUserObject();

	Object[] getSelectedNodeUserObjectPath();

	Object[][] getSelectedNodeUserObjectPaths();

	Object[] getSelectedNodePath();

	<T> T[] getSelectedLeafs(Class<T> leafcls);

	void setContextActionFixer(Function<List<Action>, List<Action>> fixer);
	
	public final static class BPTreeComponentFake implements BPTreeComponent<BPTree>
	{
		private Object m_root;
		private Object m_node;

		public BPTreeComponentFake(Object root, Object node)
		{
			m_root = root;
			m_node = node;
		}

		public BPTree getComponent()
		{
			return null;
		}

		public void setTreeFuncs(BPTreeFuncs funcs)
		{
		}

		public BPTreeNode getSelectedNode()
		{
			return new BPTreeNode(m_node);
		}

		public Object getSelectedNodeUserObject()
		{
			return m_node;
		}

		public Object[] getSelectedNodeUserObjectPath()
		{
			return new Object[] { m_root, m_node };
		}

		public Object[][] getSelectedNodeUserObjectPaths()
		{
			return new Object[][] { getSelectedNodeUserObjectPath() };
		}

		public Object[] getSelectedNodePath()
		{
			return new Object[] { getSelectedNode() };
		}

		public <T> T[] getSelectedLeafs(Class<T> leafcls)
		{
			return null;
		}

		public void setContextActionFixer(Function<List<Action>, List<Action>> fixer)
		{
		}
	}
}
