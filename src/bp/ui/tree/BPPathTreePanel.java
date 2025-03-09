package bp.ui.tree;

import java.awt.BorderLayout;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.tree.TreePath;

import bp.BPCore;
import bp.config.UIConfigs;
import bp.context.BPFileContext;
import bp.event.BPEventCoreUI;
import bp.event.BPEventUI;
import bp.res.BPResource;
import bp.ui.BPComponent;
import bp.ui.container.BPToolBarSQ;
import bp.ui.editor.BPPathSelector;
import bp.ui.scomp.BPTree.BPTreeModel;
import bp.ui.scomp.BPTree.BPTreeNode;
import bp.ui.util.UIUtil;
import bp.util.IOUtil;

public class BPPathTreePanel extends JPanel implements BPComponent<JPanel>, BPPathSelector
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4861381550383542735L;

	protected BPTreeComponentBase m_tree;
	protected BPToolBarSQ m_toolbar;
	protected Consumer<String> m_selhandler;
	protected int m_channelid;

	protected Consumer<BPEventCoreUI> m_refreshpathtreehandler;

	public BPPathTreePanel()
	{
		m_refreshpathtreehandler = this::onCoreUIRefreshPathTree;

		setBorder(new EmptyBorder(0, 0, 0, 0));
		setLayout(new BorderLayout());
		m_tree = new BPTreeComponentBase();
		m_tree.setRootVisible(false);
		m_tree.setTreeFont();
		JScrollPane scroll = new JScrollPane();
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		scroll.setViewportView(m_tree);

		m_toolbar = new BPToolBarSQ();
		m_toolbar.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()), new EmptyBorder(1, 1, 1, 1)));
		m_toolbar.setVisible(false);

		add(m_toolbar, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
	}

	public void setToolBarActions(Action[] actions)
	{
		boolean vis = false;
		if (actions != null && actions.length > 0)
		{
			vis = true;
			m_toolbar.setActions(actions);
		}
		m_toolbar.setVisible(vis);
	}

	public void setToolBarVisible(boolean flag)
	{
		m_toolbar.setVisible(flag);
	}

	public void setSelectFileHandler(Consumer<String> handler)
	{
		m_selhandler = handler;
	}

	public void refreshContextPath()
	{
		BPFileContext context = BPCore.getFileContext();
		if (context.isLocal())
		{
			loadContext(context);
		}
	}

	public void refresh()
	{
		m_tree.reloadModel();
	}

	public void refreshTree(BPResource res, boolean recursive)
	{
		((BPTreeModel) m_tree.getModel()).reloadByUserData(res, recursive);
	}

	public void loadContext(BPFileContext context)
	{
		BPPathTreeFuncs funcs = (BPPathTreeFuncs) m_tree.getTreeFuncs();
		funcs.setup(context);
		m_tree.reloadModel();
	}

	public void setPathTreeFuncs(BPPathTreeFuncs funcs)
	{
		m_tree.setTreeFuncs(funcs);
	}

	protected BPResource getSelectResource()
	{
		BPResource rc = null;
		TreePath path = m_tree.getSelectionPath();
		if (path != null)
		{
			BPTreeNode node = (BPTreeNode) path.getLastPathComponent();
			rc = (BPResource) node.getUserObject();
		}
		return rc;
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.TREE;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void setEventChannelID(int channelid)
	{
		m_channelid = channelid;
	}

	public static class BPEventUIPathTree extends BPEventUI
	{
		public final static String EVENTKEY_PATHTREE = "E_UI_PATHTREE";

		public final static String NODE_SELECT = "NODE_SELECT";
		public final static String NODE_OPEN = "NODE_OPEN"; // [[res],format,factory]
		public final static String NODE_ACTION = "NODE_ACTION";

		public BPEventUIPathTree(String subkey, Object[] datas)
		{
			this.key = EVENTKEY_PATHTREE;
			this.subkey = subkey;
			this.datas = datas;
		}

		public BPEventUIPathTree(String subkey, BPResource res)
		{
			this(subkey, new Object[] { new BPResource[] { res } });
		}

		public BPResource getSelectedResource()
		{
			BPResource rc = null;
			Object obj0 = datas[0];
			if (obj0 == null)
				rc = null;
			if (obj0 instanceof BPResource[][])
			{
				BPResource[][] paths = (BPResource[][]) obj0;
				if (paths.length == 0)
					rc = null;
				else
				{
					BPResource[] resources = paths[0];
					if (resources != null && resources.length > 0)
					{
						rc = resources[resources.length - 1];
					}
				}
			}
			else
			{
				BPResource[] resources = (BPResource[]) datas[0];
				if (resources != null && resources.length > 0)
				{
					rc = resources[resources.length - 1];
				}
			}
			return rc;
		}

		public Object[] getOpenNodeParams()
		{
			Object[] ps = new Object[2];
			if (datas.length > 1)
				ps[0] = datas[1];
			if (datas.length > 2)
				ps[1] = datas[2];
			return ps;
		}

		public Object[] getActionParams()
		{
			Object ps = datas[2];
			Object[] rc = null;
			if (ps != null && ps instanceof Object[])
			{
				rc = (Object[]) ps;
			}
			else
			{
				rc = new Object[] { ps };
			}
			return rc;
		}

		public BPResource[] getSelectedResourcePath()
		{
			return (BPResource[]) datas[0];
		}

		public BPResource[][] getSelectedResourcePaths()
		{
			return (BPResource[][]) datas[0];
		}

		public BPResource[] getSelectedResourcesFromPaths()
		{
			BPResource[][] paths = (BPResource[][]) datas[0];
			BPResource[] rc = new BPResource[paths.length];
			for (int i = 0; i < paths.length; i++)
			{
				BPResource[] path = paths[i];
				if (path != null && path.length > 0)
				{
					rc[i] = path[path.length - 1];
				}
			}
			return rc;
		}

		public String getActionName()
		{
			return (String) datas[1];
		}

		public static BPEventUIPathTree makeActionEvent(String actionname, BPResource res, Object... actionparams)
		{
			return new BPEventUIPathTree(NODE_ACTION, new Object[] { new BPResource[] { res }, actionname, actionparams });
		}
	}

	public BPTreeFuncs getPathTreeFuncs()
	{
		return m_tree.getTreeFuncs();
	}

	public BPTreeComponentBase getTreeComponent()
	{
		return m_tree;
	}

	public BPResource[] getSelectedResourcePath()
	{
		Object[] nodes = m_tree.getSelectedNodePath();
		BPResource[] rc = new BPResource[nodes.length];
		System.arraycopy(nodes, 0, rc, 0, nodes.length);
		return rc;
	}

	public BPResource getSelectedResource()
	{
		return (BPResource) m_tree.getSelectedNodeUserObject();
	}

	public void onCoreUIRefreshPathTree(BPEventCoreUI event)
	{
		BPResource res = (event.datas == null ? null : (BPResource) event.datas[0]);
		boolean recursive = ((event.datas.length < 2) ? true : (Boolean) event.datas[1]);

		if (res != null)
		{
			UIUtil.inUI(() -> refreshTree(res, recursive));
		}
		else
		{
			UIUtil.inUI(() -> refreshContextPath());
		}
	}

	public Consumer<BPEventCoreUI> getCoreUIRefreshPathTreeHandler()
	{
		return m_refreshpathtreehandler;
	}

	public Object[][] getSelectedPaths()
	{
		Object[][] rc = null;
		Object[][] selres = m_tree.getSelectedNodePaths();
		if (selres != null)
		{
			int l = selres.length;
			rc = new Object[l][];
			System.arraycopy(selres, 0, rc, 0, l);
		}
		return rc;
	}

	public String getPathType()
	{
		return IOUtil.PATH_TYPE_LOCALFS;
	}

	public Object[] getResourcesUnder(Object path)
	{
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T getRootData()
	{
		return (T) ((BPPathTreeFuncs) m_tree.getTreeFuncs()).getRootPath();
	}
}
