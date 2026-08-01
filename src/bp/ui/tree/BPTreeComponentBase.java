package bp.ui.tree;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import bp.ui.actions.BPAction;
import bp.ui.scomp.BPTree;
import bp.ui.tree.BPTreeFuncs.BPTreeActionType;
import bp.ui.tree.BPTreeFuncs.BPTreeFuncsVoid;
import bp.ui.util.UIUtil;
import bp.util.LogicUtil.WeakRefGoFunction;

public class BPTreeComponentBase extends BPTree implements BPTreeComponent<BPTree>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8516452067413933407L;

	protected WeakRefGoFunction<List<Action>, List<Action>> m_fixerref;

	public BPTreeComponentBase()
	{
		addMouseListener(new UIUtil.BPMouseListener(null, this::onMouseDown, null, null, null));
		addKeyListener(new UIUtil.BPKeyListener(null, this::onKeyDown, null));
		addTreeSelectionListener(this::onTreeSelected);
		m_fixerref = new WeakRefGoFunction<>();
	}

	public BPTree getComponent()
	{
		return this;
	}

	public void setTreeFuncs(BPTreeFuncs funcs)
	{
		setModel(new BPTreeModel(funcs));
	}

	public BPTreeFuncs getTreeFuncs()
	{
		TreeModel model = getModel();
		return model instanceof BPTreeModel ? ((BPTreeModel) model).getTreeFuncs() : null;
	}

	public void setMultiSelect(boolean flag)
	{
		getSelectionModel().setSelectionMode(flag ? TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION : TreeSelectionModel.SINGLE_TREE_SELECTION);
	}

	public Object getSelectedNodeUserObject()
	{
		TreePath path = getSelectionPath();
		return path != null ? getUserObject(path.getLastPathComponent()) : null;
	}

	public BPTreeNode getSelectedNode()
	{
		TreePath path = getSelectionPath();
		return path != null ? (BPTreeNode) path.getLastPathComponent() : null;
	}

	public Object[] getSelectedNodeUserObjectPath()
	{
		TreePath path = getSelectionPath();
		List<Object> rc = new ArrayList<Object>();
		if (path != null)
		{
			for (int i = 0; i < path.getPathCount(); i++)
			{
				if (path != null)
				{
					Object uo = getUserObject(path.getPathComponent(i));
					if (uo != null)
						rc.add(uo);
				}
			}
		}
		return rc.toArray();
	}

	public Object[][] getSelectedNodeUserObjectPaths()
	{
		TreePath[] paths = getSelectionPaths();
		Object[][] rc = null;
		if (paths != null)
		{
			rc = new Object[paths.length][];
			for (int i = 0; i < paths.length; i++)
			{
				TreePath path = paths[i];
				List<Object> r = new ArrayList<Object>();
				for (int j = 0; j < path.getPathCount(); j++)
				{
					Object uo = getUserObject(path.getPathComponent(j));
					if (uo != null)
						r.add(uo);
				}
				rc[i] = r.toArray();
			}
		}
		else
			rc = new Object[0][];
		return rc;
	}

	public Object[] getSelectedNodePath()
	{
		Object[] rc = null;
		TreePath path = getSelectionPath();
		if (path != null)
		{
			Object[] nodes = path.getPath();
			rc = new Object[nodes.length];
			for (int i = 0; i < nodes.length; i++)
				rc[i] = getUserObject(nodes[i]);
		}
		return rc;
	}

	public Object[][] getSelectedNodePaths()
	{
		Object[][] rc = null;
		TreePath[] paths = getSelectionPaths();
		if (paths != null)
		{
			rc = new Object[paths.length][];
			for (int i = 0; i < paths.length; i++)
			{
				Object[] nodes = paths[i].getPath();
				rc[i] = new Object[nodes.length];
				for (int j = 0; j < nodes.length; j++)
					rc[i][j] = getUserObject(nodes[j]);
			}
		}
		return rc;
	}

	protected Object getUserObject(Object node)
	{
		return node == null ? null : ((BPTreeNode) node).getUserObject();
	}

	protected void onTreeSelected(TreeSelectionEvent e)
	{
		TreePath p = e.getPath();
		if (p != null)
		{
			BPTreeNode node = (BPTreeNode) p.getLastPathComponent();
			if (node != null)
				getTreeFuncs().onSelect(this, node);
		}
	}

	protected void onKeyDown(KeyEvent e)
	{
		int keycode = e.getKeyCode();
		BPTreeNode node = getSelectedNode();
		switch (keycode)
		{
			case KeyEvent.VK_F2:
			{
				if (e.getModifiers() == 0)
					getTreeFuncs().onAction(this, getSelectedNode(), BPTreeActionType.EXT_ACT_RENAME);
				break;
			}
			case KeyEvent.VK_ENTER:
			{
				if (e.isAltDown())
					getTreeFuncs().onAction(this, getSelectedNode(), BPTreeActionType.EXT_ACT_PROPERTY);
				else
					getTreeFuncs().onOpen(this, getSelectedNode());
				break;
			}
			case KeyEvent.VK_CONTEXT_MENU:
			{
				if (node != null)
				{
					List<Action> acts = tryFixAction(getTreeFuncs().getActions(this, node));
					if (acts != null && acts.size() > 0)
					{
						JComponent[] items = UIUtil.makeMenuItems(acts.toArray(new Action[acts.size()]));
						JPopupMenu pop = new JPopupMenu();
						for (int i = 0; i < items.length; i++)
							pop.add(items[i]);
						Rectangle rect = getUI().getPathBounds(this, getSelectionPath());
						pop.show(this, rect.x, rect.y + rect.height);
					}
				}
				break;
			}
			case KeyEvent.VK_DELETE:
			{
				getTreeFuncs().onDelete(this, getSelectedNode());
				break;
			}
			default:
			{
				if(UIUtil.checkISCopy(e))
				{
					if (getTreeFuncs().isOverwriteCopy())
					{
						e.consume();
						getTreeFuncs().onCopy(this, getSelectedNode());
					}
				}
				else if(UIUtil.checkISPaste(e))
				{
					e.consume();
					getTreeFuncs().onPaste(this, getSelectedNode());
				}
			}
		}
	}

	protected void onMouseDown(MouseEvent e)
	{
		int btn = e.getButton();
		BPTreeNode node = getSelectedNode();
		if (btn == MouseEvent.BUTTON1)
		{
			if (node != null)
			{
				if (e.getClickCount() == 2)
				{
					getTreeFuncs().onOpen(this, getSelectedNode());
				}
				else
				{
				}
			}
		}
		else if (btn == MouseEvent.BUTTON3)
		{
			TreePath[] selpaths = getSelectionPaths();
			TreePath path = getPathForLocation(e.getX(), e.getY());
			boolean isselempty = path == null;
			boolean isshift = e.isShiftDown();
			if (path == null && !isshift)
			{
				int h = getRowHeight();
				int r = e.getY() / h;
				if (r < getRowCount())
				{
					TreePath path2 = getPathForRow(r);
					if (path2 != null)
					{
						if (checkInPaths(selpaths, path2))
						{
							path = path2;
							isselempty = false;
						}
						else if (path2.getPathCount() == 2)
						{
							path = path2;
							isselempty = false;
						}
						else if (path2.getPathCount() > 2)
						{
							path = path2.getParentPath();
						}
					}
				}
			}
			if (path != null)
			{
				if (!checkInPaths(selpaths, path))
					setSelectionPath(path);
			}
			List<Action> acts = tryFixAction(getTreeFuncs().getActions(this, ((path != null) ? (BPTreeNode) path.getLastPathComponent() : null)));
			if (acts != null && acts.size() > 0)
			{
				if (isselempty)
				{
					Action act = BPAction.build("@" + ((BPTreeNode) path.getLastPathComponent()).toString()).getAction();
					act.putValue(BPAction.IS_TITLE, true);
					acts.add(0,act);
					acts.add(1,BPAction.separator());
				}
				JComponent[] items = UIUtil.makeMenuItems(acts.toArray(new Action[acts.size()]));
				JPopupMenu pop = new JPopupMenu();
				for (int i = 0; i < items.length; i++)
					pop.add(items[i]);
				pop.show(this, e.getX(), e.getY());
			}
		}
	}
	
	protected final static boolean checkInPaths(TreePath[] selpaths,TreePath path)
	{
		boolean rc = false;
		if (path != null && selpaths != null && selpaths.length > 0)
		{
			Object lo = path.getLastPathComponent();
			for (int i = 0; i < selpaths.length; i++)
			{
				if (selpaths[i].getLastPathComponent() == lo)
				{
					rc = true;
					break;
				}
			}
		}
		return rc;
	}

	protected List<Action> tryFixAction(List<Action> acts)
	{
		if (acts != null)
		{
			List<Action> newacts = m_fixerref.apply(acts);
			if (newacts != null)
				return newacts;
		}
		return acts;
	}

	public void reloadModel()
	{
		((DefaultTreeModel) getModel()).reload();
	}

	public void clearResource()
	{
		BPTreeFuncs tf = getTreeFuncs();
		if (tf != null)
			tf.clearResource();
		setModel(new BPTreeModel(new BPTreeFuncsVoid()));
	}

	@SuppressWarnings("unchecked")
	public <T> T[] getSelectedLeafs(Class<T> leafcls)
	{
		Object[][] paths = getSelectedNodeUserObjectPaths();
		T[] rc = (T[]) Array.newInstance(leafcls, paths.length);
		for (int i = 0; i < paths.length; i++)
		{
			Object[] path = paths[i];
			rc[i] = (T) path[path.length - 1];
		}
		return rc;
	}

	public void setContextActionFixer(Function<List<Action>, List<Action>> fixer)
	{
		m_fixerref.setTarget(fixer);
	}
}