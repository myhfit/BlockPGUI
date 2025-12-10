package bp.ui.tree;

import java.util.function.Consumer;

import bp.format.BPFormat;
import bp.res.BPResource;
import bp.ui.actions.BPPathTreeNodeActions;
import bp.ui.scomp.BPTree;
import bp.ui.scomp.BPTree.BPTreeModel;
import bp.ui.tree.BPPathTreePanel.BPEventUIPathTree;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIUtil;

public class BPPathTreeNodeCommonHandler
{
	protected BPTreeComponent<? extends BPTree> m_tree;
	protected Consumer<BPEventUIPathTree> m_cb;

	public BPPathTreeNodeCommonHandler(BPTreeComponent<? extends BPTree> tree)
	{
		m_tree = tree;
	}

	public void setTree(BPTreeComponent<? extends BPTree> tree)
	{
		m_tree = tree;
	}

	public Consumer<BPEventUIPathTree> makeEventListener()
	{
		m_cb = this::onPathTreeEvent;
		return m_cb;
	}

	public void onPathTreeEvent(BPEventUIPathTree event)
	{
		switch (event.subkey)
		{
			// case BPEventUIPathTree.NODE_OPEN:
			// {
			// BPResource res = event.getSelectedResource();
			// if (res.isFileSystem() && ((BPResourceFileSystem) res).isFile())
			// openFile(((BPResourceLocalFile) res).getPathName(), null, null,
			// false);
			// break;
			// }
			case BPEventUIPathTree.NODE_ACTION:
			{
				BPResource res = event.getSelectedResource();
				switch (event.getActionName())
				{
					case BPPathTreeNodeActions.ACTION_NEWFILE:
					{
						showNewFile(res);
						break;
					}
					// case BPPathTreeNodeActions.ACTION_NEWFILEUNSAVED:
					// {
					// newEditor((BPResourceFile) event.getSelectedResource());
					// break;
					// }
					case BPPathTreeNodeActions.ACTION_NEWDIR:
					{
						showNewDir(res, null);
						break;
					}
					case BPPathTreeNodeActions.ACTION_PROPERTIES:
					{
						showProperty(res);
						break;
					}
					case BPPathTreeNodeActions.ACTION_RENAME:
					{
						CommonUIOperations.showRenameResource(res);
						break;
					}
					case BPPathTreeNodeActions.ACTION_COPY:
					{
						CommonUIOperations.copyResources(event.getSelectedResources());
						break;
					}
					case BPPathTreeNodeActions.ACTION_COPYTO:
					{
						CommonUIOperations.showCopyResourcesTo(event.getSelectedResources(), null);
						break;
					}
					// case BPPathTreeNodeActions.ACTION_OPENFILE:
					// {
					// if (res.isFileSystem() && ((BPResourceFileSystem)
					// res).isFile())
					// openFile(((BPResourceLocalFile) res).getPathName(), null,
					// null, false);
					// break;
					// }
					// case BPPathTreeNodeActions.ACTION_OPENFILEAS:
					// {
					// if (res.isFileSystem() && ((BPResourceFileSystem)
					// res).isFile())
					// openFileAs(((BPResourceLocalFile) res).getPathName());
					// break;
					// }
					// case BPPathTreeNodeActions.ACTION_DELETE:
					// {
					// deleteResource(event.getSelectedResource());
					// break;
					// }
					case BPPathTreeNodeActions.ACTION_REFRESH:
					{
						refreshPathTree(res);
						break;
					}
				}
				break;
			}
		}
	};

	public void refreshPathTree(BPResource res)
	{
		if (res != null)
		{
			UIUtil.inUI(() -> ((BPTreeModel) m_tree.getComponent().getModel()).reloadByUserData(res, true));
		}
	}

	public void showNewFile(BPResource res)
	{
		CommonUIOperations.showNewFile(res, m_tree);
	}

	public void showNewDir(BPResource res, BPFormat format)
	{
		CommonUIOperations.showNewDirectory(res, m_tree);
	}

	public void showProperty(BPResource res)
	{
		CommonUIOperations.showProperty(res, m_tree);
	}
}
