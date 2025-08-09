package bp.ui.tree;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import bp.ui.scomp.BPTree;
import bp.ui.tree.BPTreeFuncs.BPTreeFuncsVoid;
import bp.ui.util.UIUtil;

public class BPTreeComponentBase extends BPTree implements BPTreeComponent<BPTree>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8516452067413933407L;

	public BPTreeComponentBase()
	{
		addMouseListener(new UIUtil.BPMouseListener(null, this::onMouseDown, null, null, null));
		addKeyListener(new UIUtil.BPKeyListener(null, this::onKeyDown, null));
		addTreeSelectionListener(this::onTreeSelected);
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

	public Object getSelectedNodeUserObject()
	{
		Object rc = null;
		TreePath path = getSelectionPath();
		if (path != null)
		{
			rc = getUserObject(path.getLastPathComponent());
		}
		return rc;
	}

	public BPTreeNode getSelectedNode()
	{
		BPTreeNode rc = null;
		TreePath path = getSelectionPath();
		if (path != null)
		{
			rc = (BPTreeNode) path.getLastPathComponent();
		}
		return rc;
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
			case KeyEvent.VK_ENTER:
			{
				getTreeFuncs().onOpen(this, getSelectedNode());
				break;
			}
			case KeyEvent.VK_CONTEXT_MENU:
			{
				if (node != null)
				{
					List<Action> acts = getTreeFuncs().getActions(this, node);
					if (acts != null && acts.size() > 0)
					{
						JComponent[] items = UIUtil.makeMenuItems(acts.toArray(new Action[acts.size()]));
						JPopupMenu pop = new JPopupMenu();
						for (JComponent item : items)
						{
							pop.add(item);
						}
						Rectangle rect = getUI().getPathBounds(this, getSelectionPath());
						pop.show(this, rect.x, rect.y + rect.height);
					}
				}
				break;
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
			TreePath path = getPathForLocation(e.getX(), e.getY());
			if (path != null)
			{
				// check current selection
				TreePath[] paths = getSelectionPaths();
				boolean flag = false;
				if (paths != null && paths.length > 1)
				{
					Object lo = path.getLastPathComponent();
					for (TreePath selp : paths)
					{
						if (selp.getLastPathComponent() == lo)
						{
							flag = true;
							break;
						}
					}
				}
				if (!flag)
					setSelectionPath(path);
			}
			List<Action> acts = getTreeFuncs().getActions(this, ((path != null) ? (BPTreeNode) path.getLastPathComponent() : null));
			if (acts != null && acts.size() > 0)
			{
				JComponent[] items = UIUtil.makeMenuItems(acts.toArray(new Action[acts.size()]));
				JPopupMenu pop = new JPopupMenu();
				for (JComponent item : items)
				{
					pop.add(item);
				}
				pop.show(this, e.getX(), e.getY());
			}
		}
	}

	public void reloadModel()
	{
		((DefaultTreeModel) getModel()).reload();
	}

	public void clearResource()
	{
		setModel(new BPTreeModel(new BPTreeFuncsVoid()));
	}
}
