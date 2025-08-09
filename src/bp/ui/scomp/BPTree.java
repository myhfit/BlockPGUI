package bp.ui.scomp;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Predicate;

import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import bp.config.UIConfigs;
import bp.ui.tree.BPTreeFuncs;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;

public class BPTree extends JTree implements TreeWillExpandListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1257250896614010178L;

	public BPTree()
	{
		setBorder(new EmptyBorder(0, 0, 0, 0));
		addTreeWillExpandListener(this);
	}

	public void setMonoFont()
	{
		setFont(UIUtil.monoFont(Font.PLAIN, UIConfigs.TREEFONT_SIZE()));
		setRowHeight((int) ((float) UIConfigs.TREE_ROWHEIGHT()));
	}

	public void setTreeFont()
	{
		int fontsize = UIConfigs.TREEFONT_SIZE();
		Font tfont = new Font(UIConfigs.TREE_FONT_NAME(), Font.PLAIN, fontsize);
		setFont(tfont);
		setRowHeight((int) ((float) UIConfigs.TREE_ROWHEIGHT()));
	}

	public void treeWillExpand(TreeExpansionEvent e)
	{
		TreeModel model = getModel();
		if (model != null && model instanceof BPTreeModel)
		{
			BPTreeModel m = (BPTreeModel) model;
			UIStd.wrapSeg(() -> m.expand(e.getPath().getLastPathComponent()));
		}
	}

	public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException
	{

	}

	public BPTreeModel getBPModel()
	{
		TreeModel m = getModel();
		if (m instanceof BPTreeModel)
			return (BPTreeModel) m;
		return null;
	}

	public static class BPTreeModel extends DefaultTreeModel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -7292469833768032547L;

		protected BPTreeFuncs m_treefunc = null;

		public BPTreeModel(BPTreeFuncs treefunc)
		{
			super(new BPTreeNode(null), true);
			m_treefunc = treefunc;
			if (treefunc != null)
			{
				DefaultMutableTreeNode rootnode = (DefaultMutableTreeNode) root;
				List<?> roots = treefunc.getRoots();
				for (Object r : roots)
				{
					BPTreeNode n = new BPTreeNode(r);
					rootnode.add(n);
				}
			}
		}

		public void reload()
		{
			if (m_treefunc != null)
			{
				DefaultMutableTreeNode rootnode = (DefaultMutableTreeNode) root;
				rootnode.removeAllChildren();
				List<?> roots = m_treefunc.getRoots();
				for (Object r : roots)
				{
					BPTreeNode n = new BPTreeNode(r);
					rootnode.add(n);
				}
			}
			super.reload();
		}

		public BPTreeNode findTreeNode(Object data)
		{
			TreeNode root = (TreeNode) getRoot();
			BPTreeNode t = null;
			for (int i = 0; i < root.getChildCount(); i++)
			{
				BPTreeNode node = (BPTreeNode) root.getChildAt(i);
				t = find(node, data);
				if (t != null)
					return t;
			}
			return t;
		}

		private final static BPTreeNode find(BPTreeNode node, Object data)
		{
			if (data.equals(node.getUserObject()))
			{
				return node;
			}
			if (node.isLoaded())
			{
				for (int i = 0; i < node.getChildCount(); i++)
				{
					BPTreeNode t = find((BPTreeNode) node.getChildAt(i), data);
					if (t != null)
						return t;
				}
			}
			return null;
		}

		public BPTreeFuncs getTreeFuncs()
		{
			return m_treefunc;
		}

		public void expand(Object lastPathComponent)
		{
			expand(lastPathComponent, false);
		}

		public void expand(Object lastPathComponent, boolean isdelta)
		{
			BPTreeNode node = (BPTreeNode) lastPathComponent;
			if (!node.isLoaded())
			{
				node.setLoaded(true);
				List<?> chd = m_treefunc.getChildren(node, isdelta);
				if (chd != null)
				{
					for (Object c : chd)
					{
						DefaultMutableTreeNode newnode = new BPTreeNode(c);
						node.add(newnode);
					}
				}
			}
			this.reload(node);
		}

		public void replace(Object lastPathComponent)
		{
			BPTreeNode node = (BPTreeNode) lastPathComponent;
			if (!node.isLoaded())
			{
				node.setLoaded(true);
				List<?> newchds = m_treefunc.getChildren(node);
				List<BPTreeNode> oldchds = node.getChildrenList();
				Map<Object, BPTreeNode> oldmap = new HashMap<Object, BPTreeNode>();
				for (BPTreeNode oldchd : oldchds)
				{
					oldmap.put(oldchd.getUserObject(), oldchd);
				}
				node.removeAllChildren();
				if (newchds != null)
				{
					for (Object newchd : newchds)
					{
						BPTreeNode oldnode = oldmap.get(newchd);
						if (oldnode != null)
						{
							node.add(oldnode);
						}
						else
						{
							DefaultMutableTreeNode newnode = new BPTreeNode(newchd);
							node.add(newnode);
						}
					}
				}
			}
			this.reload(node);
		}

		public boolean isLeaf(Object node)
		{
			if (m_treefunc != null)
				return m_treefunc.isLeaf((BPTreeNode) node);
			return false;
		}

		public void reloadByUserData(Object data, boolean recursive)
		{
			BPTreeNode node = findTreeNode(data);
			if (node != null)
			{
				if (recursive)
				{
					node.removeAllChildren();
					node.setLoaded(false);
					expand(node, true);
				}
				else
				{
					node.setLoaded(false);
					replace(node);
				}
			}
		}
	}

	public static class BPTreeNode extends DefaultMutableTreeNode
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5260711684545130974L;
		protected Predicate<Object> m_filter;
		protected List<Object> m_fchildren;
		protected boolean m_loaded = false;

		public BPTreeNode(Object o)
		{
			super(o);
		}

		public void removeAllChildren()
		{
			super.removeAllChildren();
			if (m_filter != null && children != null)
			{
				m_fchildren = new ArrayList<Object>();
				for (Object o : children)
				{
					if (m_filter.test(((BPTreeNode) o).getUserObject()))
						m_fchildren.add(o);
				}
			}
		}

		public void setChildren(List<BPTreeNode> nodes)
		{
			children = new Vector<>(nodes);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public List<BPTreeNode> getChildrenList()
		{
			List<BPTreeNode> rc = (children == null ? new ArrayList<BPTreeNode>() : new ArrayList<BPTreeNode>((List) children));
			return rc;
		}

		public void setFilter(Predicate<Object> filter)
		{
			m_filter = filter;
			if (filter == null)
			{
				if (m_fchildren != null)
				{
					m_fchildren.clear();
					m_fchildren = null;
				}
			}
			else
			{
				if (children != null)
				{
					m_fchildren = new ArrayList<Object>();
					for (Object o : children)
					{
						if (filter.test(((BPTreeNode) o).getUserObject()))
							m_fchildren.add(o);
					}
				}
			}
			if (children != null)
			{
				for (Object o : children)
				{
					((BPTreeNode) o).setFilter(filter);
				}
			}
		}

		public TreeNode getChildAt(int index)
		{
			if (children == null)
				throw new ArrayIndexOutOfBoundsException("node has no children");
			if (m_filter != null)
			{
				return (TreeNode) m_fchildren.get(index);
			}
			else
			{
				return super.getChildAt(index);
			}
		}

		public int getChildCount()
		{
			if (children == null)
				return 0;
			if (m_filter != null)
			{
				return m_fchildren.size();
			}
			else
			{
				return super.getChildCount();
			}
		}

		public boolean isLoaded()
		{
			return m_loaded;
		}

		public void setLoaded(boolean flag)
		{
			m_loaded = flag;
		}

		public BPTreeNode getRoot()
		{
			if (parent != null)
			{
				BPTreeNode r = ((BPTreeNode) parent).getRoot();
				return ((r == null) ? this : r);
			}
			return null;
		}
	}

}
