package bp.ui.tree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.Action;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.TreePath;

import bp.BPGUICore;
import bp.context.BPFileContext;
import bp.res.BPResource;
import bp.res.BPResourceDirLocal;
import bp.res.BPResourceFileLocal;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceFileSystemLocal;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPPathTreeNodeActions;
import bp.ui.scomp.BPTree;
import bp.ui.scomp.BPTree.BPTreeModel;
import bp.ui.scomp.BPTree.BPTreeNode;
import bp.ui.tree.BPPathTreePanel.BPEventUIPathTree;

public class BPPathTreeComputerFuncs implements BPPathTreeFuncs
{
	protected Predicate<BPResource> m_filter;
	protected BPPathTreeNodeActions m_actptree;
	protected int m_channelid;
	protected boolean m_skiproot;

	public BPPathTreeComputerFuncs()
	{
		this(0);
	}

	public BPPathTreeComputerFuncs(int channelid)
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
	}

	public String getRootPath()
	{
		return null;
	}

	public List<?> getRoots()
	{
		List<BPResource> rc = new ArrayList<BPResource>();
		File[] rootfs = FileSystemView.getFileSystemView().getRoots();
		for (File rootf : rootfs)
		{
			BPResourceFileSystemLocal f = null;
			if (rootf.isDirectory())
				f = new BPResourceDirLocal(rootf);
			else if (rootf.isFile())
				f = new BPResourceFileLocal(rootf);
			if (f != null && m_filter == null || m_filter.test(f))
				rc.add(f);
		}
		sort(rc);
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
		{
			FileSystemView fsv = FileSystemView.getFileSystemView();
			for (BPResource f : rc)
			{
				if (!f.isLeaf() && f instanceof BPResourceDirLocal)
				{
					BPResourceDirLocal dir = ((BPResourceDirLocal) f);
					File fobj = dir.getFileObject();
					if (fobj.getClass().getSimpleName().contains("ShellFolder"))
						dir.setDisplayName(fsv.getSystemDisplayName(fobj));
				}
			}
			sort(rc);
		}
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
			if (res.isFileSystem())
			{
				BPResourceFileSystem resfs = (BPResourceFileSystem) res;
				if (resfs.isDirectory())
				{
					rc.add(m_actptree.getNewFileAction(tree, res, m_channelid));
					rc.add(m_actptree.getOpenFileAsAction(tree, res, m_channelid));
					rc.add(m_actptree.getOpenFileExternalAction(tree, res, m_channelid));
					rc.add(BPAction.separator());
					rc.add(m_actptree.getDeleteResAction(tree, res, m_channelid));
					rc.add(m_actptree.getRenameResAction(tree, res, m_channelid));
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
					rc.add(BPAction.separator());
					rc.add(m_actptree.getDeleteResAction(tree, res, m_channelid));
					rc.add(m_actptree.getRenameResAction(tree, res, m_channelid));
					rc.add(BPAction.separator());
					rc.add(m_actptree.getPropertyAction(tree, res, m_channelid));
				}
			}
		}
		return rc;
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

	public boolean canLocatePath()
	{
		return true;
	}

	public void locatePath(BPTreeComponentBase tree, String path)
	{
		List<File> fs = new ArrayList<File>();
		File fp = new File(path);
		FileSystemView fsv = FileSystemView.getFileSystemView();
		if (!fp.exists())
			return;
		{
			File f;
			do
			{
				f = fp;
				fs.add(0, f);
				fp = fsv.getParentDirectory(f);
			} while (fp != null);
		}
		BPTreeModel m = (BPTreeModel) tree.getModel();
		BPTreeNode rootnode = null;
		int si = 0;
		for (int i = fs.size() - 1; i >= 0; i--)
		{
			File f = fs.get(i);
			BPResourceFileSystem r = f.isDirectory() ? new BPResourceDirLocal(f) : new BPResourceFileLocal(f);
			rootnode = m.findTreeNode(r);
			if (rootnode != null)
			{
				si = i;
				break;
			}
		}
		if (rootnode == null)
		{
			rootnode = (BPTreeNode) m.getRoot();
			si = 0;
		}
		BPTreeNode node = rootnode;
		List<Object> tps = new ArrayList<Object>();
		{
			do
			{
				tps.add(0, node);
				node = (BPTreeNode) node.getParent();
			} while (node != null);
		}
		node = rootnode;
		for (int i = si + 1; i < fs.size(); i++)
		{
			tree.expandPath(new TreePath(tps.toArray()));

			File f = fs.get(i);
			BPResourceFileSystemLocal r = f.isDirectory() ? new BPResourceDirLocal(f) : new BPResourceFileLocal(f);

			BPTreeNode subnode = null;
			for (BPTreeNode tn : node.getChildrenList())
			{
				if (r.equals(tn.getUserObject()))
				{
					subnode = tn;
					break;
				}
			}
			if (subnode != null)
			{
				tps.add(subnode);
			}
			else
				break;
			node = subnode;
		}
		if (tps.size() > 0)
		{
			TreePath tp2 = new TreePath(tps.toArray());
			tree.setSelectionPath(tp2);
			tree.scrollPathToVisible(tp2);
		}
	}

	public final static BPPathTreeComputerFuncs OnlySelect()
	{
		return new BPPathTreeComputerFuncs()
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
					}
				}
				return rc;
			}
		};
	}
}