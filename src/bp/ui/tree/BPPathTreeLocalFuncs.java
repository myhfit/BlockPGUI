package bp.ui.tree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.Action;

import bp.BPGUICore;
import bp.context.BPFileContext;
import bp.context.BPFileContextLocal;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceDirLocal;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPPathTreeNodeActions;
import bp.ui.scomp.BPTree;
import bp.ui.scomp.BPTree.BPTreeNode;
import bp.ui.tree.BPPathTreePanel.BPEventUIPathTree;

public class BPPathTreeLocalFuncs implements BPPathTreeFuncs
{
	public String m_base;
	protected Predicate<BPResource> m_filter;
	protected BPPathTreeNodeActions m_actptree;
	protected int m_channelid;
	protected boolean m_skiproot;
	protected boolean m_readonly;

	public BPPathTreeLocalFuncs()
	{
		this(0);
	}

	public BPPathTreeLocalFuncs(int channelid)
	{
		m_channelid = channelid;
		m_actptree = new BPPathTreeNodeActions();
	}

	public void setSkipRoot(boolean flag)
	{
		m_skiproot = flag;
	}

	public void setChannelID(int channelid)
	{
		m_channelid = channelid;
	}

	@SuppressWarnings("unchecked")
	public void setTreeFilter(Predicate<?> filter)
	{
		m_filter = (Predicate<BPResource>) filter;
	}

	public void setFilter(Predicate<BPResource> filter)
	{
		m_filter = filter;
	}

	public void setup(BPFileContext context)
	{
		setup(context, null);
	}

	public void setup(BPFileContext context, String path)
	{
		if (context.isLocal())
		{
			String basepath = ((BPFileContextLocal) context).getBasePath();
			File basef = new File(basepath);
			m_base = basef.getAbsolutePath() + (path == null ? "" : File.separator + path);
		}
	}

	public String getRootPath()
	{
		return m_base;
	}

	public void setReadOnly(boolean flag)
	{
		m_readonly = flag;
	}

	public List<?> getRoots()
	{
		List<BPResource> rc = new ArrayList<BPResource>();
		if (m_base != null)
		{
			BPResourceDirLocal f = new BPResourceDirLocal(m_base);
			if (m_skiproot)
			{
				BPResourceFileSystem[] res = f.list();
				for (BPResourceFileSystem chd : res)
				{
					if (m_filter == null || m_filter.test(chd))
						rc.add(chd);
				}
			}
			else
			{
				if (m_filter == null || m_filter.test(f))
					rc.add(f);
			}
			sort(rc);
		}
		return rc;
	}

	protected void sort(List<BPResource> nodes)
	{
		nodes.sort((a, b) ->
		{
			boolean isdir1 = a.isLeaf();
			boolean isdir2 = b.isLeaf();
			if (isdir1 == isdir2)
			{
				return a.getName().compareToIgnoreCase(b.getName());
			}
			return (!isdir1) ? -1 : 1;
		});
	}

	public List<?> getChildren(BPTreeNode node, boolean isdelta)
	{
		BPResource r = (BPResource) node.getUserObject();
		List<BPResource> rc = null;
		if (!r.isLeaf())
		{
			BPResource[] fsch = r.listResources(isdelta);
			rc = new ArrayList<BPResource>(fsch.length);
			for (BPResource f : fsch)
			{
				if (m_filter == null || m_filter.test(f))
					rc.add(f);
			}
		}
		else
		{
		}
		if (rc != null)
			sort(rc);
		return rc;
	}

	public boolean isLeaf(BPTreeNode node)
	{
		if (node == null)
			return false;
		BPResource fi = (BPResource) node.getUserObject();
		return fi != null ? fi.isLeaf() : false;
	}

	public List<Action> getActions(BPTreeComponent<BPTree> tree, BPTreeNode node)
	{
		List<Action> rc = new ArrayList<Action>();
		if (node != null)
		{
			BPResource res = (BPResource) node.getUserObject();
			BPResource[] ress = tree.getSelectedLeafs(BPResource.class);
			if (res.isFileSystem())
			{
				BPResourceFileSystem resfs = (BPResourceFileSystem) res;
				if (resfs.isDirectory())
				{
					rc.add(m_actptree.getNewFileAction(tree, res, m_channelid));
					rc.add(m_actptree.getOpenFileAction(tree, res, m_channelid));
					rc.add(m_actptree.getOpenFileAsAction(tree, res, m_channelid));
					rc.add(m_actptree.getOpenFileExternalAction(tree, res, m_channelid));
					rc.add(m_actptree.getOpenFileWithToolAction(tree, res, m_channelid));
					rc.add(BPAction.separator());
					rc.add(m_actptree.getCopyAction(tree, res, m_channelid));
					if (!m_readonly)
					{
						rc.add(m_actptree.getCopyToAction(tree, ress, m_channelid));
						rc.add(BPAction.separator());
						rc.add(m_actptree.getDeleteResAction(tree, res, m_channelid));
						rc.add(m_actptree.getRenameResAction(tree, res, m_channelid));
					}
					rc.add(BPAction.separator());
					rc.add(m_actptree.getRefreshResAction(tree, res, m_channelid));
					rc.add(BPAction.separator());
					rc.add(m_actptree.getPropertyAction(tree, res, m_channelid));
				}
				else if (resfs.isFile())
				{
					rc.add(m_actptree.getOpenFileAction(tree, res, m_channelid));
					rc.add(m_actptree.getOpenFileAsAction(tree, res, m_channelid));
					rc.add(m_actptree.getOpenFileExternalAction(tree, res, m_channelid));
					rc.add(m_actptree.getOpenFileWithToolAction(tree, res, m_channelid));
					rc.add(BPAction.separator());
					rc.add(m_actptree.getCopyAction(tree, res, m_channelid));
					if (!m_readonly)
					{
						rc.add(m_actptree.getCopyToAction(tree, ress, m_channelid));
						rc.add(BPAction.separator());
						rc.add(m_actptree.getDeleteResAction(tree, res, m_channelid));
						rc.add(m_actptree.getRenameResAction(tree, res, m_channelid));
					}
					rc.add(BPAction.separator());
					rc.add(m_actptree.getPropertyAction(tree, res, m_channelid));
				}
			}
		}
		return rc;
	}

	protected BPResource[][] getResources(BPTree tree)
	{
		Object[][] respaths = ((BPTreeComponentBase) tree).getSelectedNodePaths();
		BPResource[][] resarrs = new BPResource[respaths.length][];
		for (int i = 0; i < respaths.length; i++)
		{
			Object[] respath = respaths[i];
			BPResource[] resarr = new BPResource[respath.length];
			System.arraycopy(respath, 0, resarr, 0, respath.length);
			resarrs[i] = resarr;
		}
		return resarrs;
	}

	public void onDelete(BPTree tree, BPTreeNode node)
	{
		if (node != null)
		{
			BPResource res = (BPResource) node.getUserObject();
			if (res != null)
			{
				BPGUICore.EVENTS_UI.trigger(m_channelid, new BPEventUIPathTree(BPEventUIPathTree.NODE_ACTION, new Object[] { getResources(tree), BPPathTreeNodeActions.ACTION_DELETES }));
			}
		}
	}

	public void onSelect(BPTree tree, BPTreeNode node)
	{
		if (node != null)
		{
			BPResource res = (BPResource) node.getUserObject();
			if (res != null)
			{
				BPGUICore.EVENTS_UI.trigger(m_channelid, new BPEventUIPathTree(BPEventUIPathTree.NODE_SELECT, res));
			}
		}
	}

	public void onOpen(BPTree tree, BPTreeNode node)
	{
		if (node != null)
		{
			BPResource res = (BPResource) node.getUserObject();
			if (res != null)
			{
				BPGUICore.EVENTS_UI.trigger(m_channelid, new BPEventUIPathTree(BPEventUIPathTree.NODE_OPEN, res));
			}
		}
	}

	public final static BPPathTreeLocalFuncs OnlySelect()
	{
		BPPathTreeLocalFuncs rc = new BPPathTreeLocalFuncs()
		{
			public List<Action> getActions(BPTreeComponent<BPTree> tree, BPTreeNode node)
			{
				List<Action> rc = new ArrayList<Action>();
				if (node != null)
				{
					BPResource res = (BPResource) node.getUserObject();
					if (!res.isLeaf())
					{
						rc.add(m_actptree.getNewFileOnlyDirAction(tree, res, m_channelid));
						rc.add(BPAction.separator());
						rc.add(m_actptree.getCopyAction(tree, res, m_channelid));
						rc.add(BPAction.separator());
						rc.add(m_actptree.getRefreshResAction(tree, res, m_channelid));
						rc.add(BPAction.separator());
						rc.add(m_actptree.getPropertyAction(tree, res, m_channelid));
					}
				}
				return rc;
			}
		};
		rc.setReadOnly(true);
		return rc;
	}
}
