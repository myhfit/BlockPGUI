package bp.ui.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.Action;

import bp.BPGUICore;
import bp.context.BPFileContext;
import bp.context.BPProjectsContext;
import bp.project.BPResourceProject;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceHolder;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPPathTreeNodeActions;
import bp.ui.actions.BPProjectTreeNodeActions;
import bp.ui.scomp.BPTree;
import bp.ui.scomp.BPTree.BPTreeNode;
import bp.ui.tree.BPPathTreePanel.BPEventUIPathTree;

public class BPProjectsTreeFuncs implements BPPathTreeFuncs
{
	public BPProjectsContext m_context;
	protected Predicate<BPResource> m_filter;

	protected int m_channelid;

	protected BPPathTreeNodeActions m_actptree;
	protected BPProjectTreeNodeActions m_actprjtree;

	public BPProjectsTreeFuncs(int channelid)
	{
		m_channelid = channelid;
		m_actptree = new BPPathTreeNodeActions();
		m_actprjtree = new BPProjectTreeNodeActions();
	}

	public void setup(BPFileContext context)
	{
		if (context.isProjectsContext())
		{
			m_context = (BPProjectsContext) context;
		}
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

	public List<?> getRoots()
	{
		List<BPResource> rc = new ArrayList<BPResource>();
		if (m_context != null)
		{
			BPResourceProject[] prjs = m_context.listProject();
			for (BPResourceProject chd : prjs)
			{
				if (m_filter == null || m_filter.test(chd))
					rc.add(chd);
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
		BPResourceProject prj = (BPResourceProject) node.getRoot().getUserObject();
		List<BPResource> rc = null;
		if (!r.isLeaf())
		{
			BPResource[] fsch = r.listResources(isdelta);
			if (fsch != null)
			{
				rc = new ArrayList<BPResource>(fsch.length);
				for (BPResource f : fsch)
				{
					BPResource wres = prj.wrapResource(f);
					if (wres != null)
					{
						if (m_filter == null || m_filter.test(wres))
							rc.add(wres);
					}
				}
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
			if (res.isProjectResource() && res.fullHandleAction())
			{
				List<Action> prjactions = BPProjectsTreeNodeActionManager.getActions(tree, res, m_channelid);
				if (prjactions != null && prjactions.size() > 0)
				{
					rc.addAll(prjactions);
				}
			}
			else
			{
				if (res.isFileSystem())
				{
					BPResourceFileSystem resfs = (BPResourceFileSystem) res;
					if (resfs.isDirectory())
					{
						rc.add(m_actptree.getNewFileInProjectAction(tree, res, m_channelid));
						rc.add(m_actptree.getOpenFileAction(tree, res, m_channelid));
						rc.add(m_actptree.getOpenFileAsAction(tree, res, m_channelid));
						rc.add(m_actptree.getOpenFileExternalAction(tree, res, m_channelid));
						rc.add(BPAction.separator());
						rc.add(m_actptree.getCopyAction(tree, res, m_channelid));
						rc.add(m_actptree.getCopyToAction(tree, ress, m_channelid));
						rc.add(BPAction.separator());
						rc.add(m_actptree.getDeleteResAction(tree, res, m_channelid));
						rc.add(m_actptree.getRenameResAction(tree, res, m_channelid));
						rc.add(BPAction.separator());
						rc.add(m_actptree.getRefreshResAction(tree, res, m_channelid));
					}
					else if (resfs.isFile())
					{
						rc.add(m_actptree.getOpenFileAction(tree, res, m_channelid));
						rc.add(m_actptree.getOpenFileAsAction(tree, res, m_channelid));
						rc.add(m_actptree.getOpenFileExternalAction(tree, res, m_channelid));
						rc.add(BPAction.separator());
						rc.add(m_actptree.getCopyAction(tree, res, m_channelid));
						rc.add(m_actptree.getCopyToAction(tree, ress, m_channelid));
						rc.add(BPAction.separator());
						rc.add(m_actptree.getDeleteResAction(tree, res, m_channelid));
						rc.add(m_actptree.getRenameResAction(tree, res, m_channelid));
					}
				}
				else if (res instanceof BPResourceHolder)
				{
					rc.add(m_actptree.getOpenFileAction(tree, res, m_channelid));
					rc.add(m_actptree.getOpenFileAsAction(tree, res, m_channelid));
					rc.add(BPAction.separator());
					rc.add(m_actptree.getDeleteResAction(tree, res, m_channelid));
				}
				if (res.isProjectResource())
				{
					List<Action> prjactions = BPProjectsTreeNodeActionManager.getActions(tree, res, m_channelid);
					if (prjactions != null && prjactions.size() > 0)
					{
						if (rc.size() > 0)
							rc.add(BPAction.separator());
						rc.addAll(prjactions);
					}
				}
				if (rc.size() > 0)
					rc.add(BPAction.separator());
				rc.add(m_actptree.getPropertyAction(tree, res, m_channelid));
				if (res instanceof BPResourceProject)
				{
					rc.add(m_actprjtree.getOverviewAction(tree, res, m_channelid));
					rc.add(m_actprjtree.getStatsAction(tree, res, m_channelid));
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

	public String getRootPath()
	{
		return m_context.getRootDir().getFileFullName();
	}
}