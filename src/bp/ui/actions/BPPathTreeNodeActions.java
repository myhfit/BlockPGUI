package bp.ui.actions;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import bp.BPGUICore;
import bp.project.BPProjectItemFactory;
import bp.project.BPResourceProject;
import bp.res.BPResource;
import bp.ui.scomp.BPTree;
import bp.ui.tree.BPPathTreePanel.BPEventUIPathTree;
import bp.ui.tree.BPTreeComponent;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.EventUtil;

public class BPPathTreeNodeActions
{
	public final static String ACTION_REFRESH = "refresh";
	public final static String ACTION_NEWFILE = "newfile";
	public final static String ACTION_NEWFILEUNSAVED = "newfileunsaved";
	public final static String ACTION_NEWDIR = "newdir";
	public final static String ACTION_OPENFILE = "openfile";
	public final static String ACTION_OPENFILEAS = "openfileas";
	public final static String ACTION_DELETE = "delfile";
	public final static String ACTION_DELETES = "delfiles";
	public final static String ACTION_RENAME = "rename";
	public final static String ACTION_PROPERTIES = "prop";
	public final static String ACTION_COPY = "copy";
	public final static String ACTION_COPYTO = "copyto";
	public final static String ACTION_PASTE = "paste";
	public final static String ACTION_OPENEXTERNAL_SYSTEM = "openextsys";
	public final static String ACTION_EDITEXTERNAL_SYSTEM = "editextsys";
	public final static String ACTION_PRINTEXTERNAL_SYSTEM = "printextsys";
	public final static String ACTION_OPENWITHTOOL = "openwithtool";

	public BPPathTreeNodeActions()
	{
	}

	public BPAction getNewFileAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		BPAction rc = BPAction.build("New").mnemonicKey(KeyEvent.VK_N).getAction();
		BPAction actnewfile = BPAction.build("File").callback((e) -> BPGUICore.EVENTS_UI.trigger(channelid, BPEventUIPathTree.makeActionEvent(ACTION_NEWFILE, res))).mnemonicKey(KeyEvent.VK_F).getAction();
		BPAction actnewdir = BPAction.build("Directory").callback((e) -> BPGUICore.EVENTS_UI.trigger(channelid, BPEventUIPathTree.makeActionEvent(ACTION_NEWDIR, res))).mnemonicKey(KeyEvent.VK_D).getAction();
		Action[] actchd = new Action[] { actnewfile, actnewdir };
		rc.putValue(BPAction.SUB_ACTIONS, actchd);
		return rc;
	}

	public BPAction getNewFileOnlyDirAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		BPAction rc = BPAction.build("New").mnemonicKey(KeyEvent.VK_N).getAction();
		BPAction actnewdir = BPAction.build("Directory").callback((e) -> BPGUICore.EVENTS_UI.trigger(channelid, BPEventUIPathTree.makeActionEvent(ACTION_NEWDIR, res))).mnemonicKey(KeyEvent.VK_D).getAction();
		Action[] actchd = new Action[] { actnewdir };
		rc.putValue(BPAction.SUB_ACTIONS, actchd);
		return rc;
	}

	public Action getNewFileInProjectAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		Object[] objs = tree.getSelectedNodePath();
		BPResourceProject prj = null;
		for (int i = objs.length - 1; i >= 0; i--)
		{
			Object obj = objs[i];
			if (obj != null && obj instanceof BPResourceProject)
			{
				prj = (BPResourceProject) obj;
			}
		}
		BPAction rc = BPAction.build("New").mnemonicKey(KeyEvent.VK_N).getAction();
		BPAction actnewfile = BPAction.build("File").callback((e) -> BPGUICore.EVENTS_UI.trigger(channelid, BPEventUIPathTree.makeActionEvent(ACTION_NEWFILE, res))).mnemonicKey(KeyEvent.VK_F).getAction();
		BPAction actnewdir = BPAction.build("Directory").callback((e) -> BPGUICore.EVENTS_UI.trigger(channelid, BPEventUIPathTree.makeActionEvent(ACTION_NEWDIR, res))).mnemonicKey(KeyEvent.VK_D).getAction();
		Action[] actchd = null;
		if (prj == null)
		{
			actchd = new Action[] { actnewfile, actnewdir };
		}
		else
		{
			BPProjectItemFactory[] facs = prj.getItemFactories();
			List<Action> acts = new ArrayList<Action>();
			acts.add(actnewfile);
			acts.add(actnewdir);
			if (facs != null && facs.length > 0)
			{
				acts.add(BPAction.separator());
				final BPResourceProject pprj = prj;
				for (BPProjectItemFactory fac : facs)
				{
					acts.add(BPAction.build(fac.getName()).callback((e) -> CommonUIOperations.createProjectItem(pprj, res, fac)).getAction());
				}
			}
			actchd = acts.toArray(new Action[acts.size()]);
		}
		rc.putValue(BPAction.SUB_ACTIONS, actchd);
		return rc;
	}

	public BPAction getOpenFileAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPAction.build("Open").callback(new EventUtil.EventConsumerMakePathTreeAction(res, channelid, ACTION_OPENFILE)).mnemonicKey(KeyEvent.VK_O).getAction();
	}

	public BPAction getOpenFileAsAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPAction.build("Open As...").callback(new EventUtil.EventConsumerMakePathTreeAction(res, channelid, ACTION_OPENFILEAS)).mnemonicKey(KeyEvent.VK_A).getAction();
	}

	public BPAction getOpenFileExternalAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		BPAction rc = BPAction.build("Open External").getAction();
		BPAction actopensys = BPAction.build("Open").callback(new EventUtil.EventConsumerMakePathTreeAction(res, channelid, ACTION_OPENEXTERNAL_SYSTEM)).mnemonicKey(KeyEvent.VK_O).getAction();
		BPAction acteditsys = BPAction.build("Edit").callback(new EventUtil.EventConsumerMakePathTreeAction(res, channelid, ACTION_EDITEXTERNAL_SYSTEM)).mnemonicKey(KeyEvent.VK_E).getAction();
		BPAction actprintsys = BPAction.build("Print").callback(new EventUtil.EventConsumerMakePathTreeAction(res, channelid, ACTION_PRINTEXTERNAL_SYSTEM)).mnemonicKey(KeyEvent.VK_P).getAction();
		Action[] actchd = new Action[] { actopensys, acteditsys, actprintsys };
		rc.putValue(BPAction.SUB_ACTIONS, actchd);
		return rc;
	}

	public BPAction getOpenFileWithToolAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		BPAction rc = BPAction.build("Open With Tool...").callback(new EventUtil.EventConsumerNodeAction(tree.getSelectedLeafs(BPResource.class), channelid, ACTION_OPENWITHTOOL)).mnemonicKey(KeyEvent.VK_S).getAction();
		return rc;
	}

	public BPAction getDeleteResAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return getDeleteResourcesAction(tree, res, channelid);
	}

	public BPAction getDeleteResourcesAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		Object[][] respaths = tree.getSelectedNodeUserObjectPaths();
		BPResource[][] resarrs = new BPResource[respaths.length][];
		for (int i = 0; i < respaths.length; i++)
		{
			Object[] respath = respaths[i];
			BPResource[] resarr = new BPResource[respath.length];
			System.arraycopy(respath, 0, resarr, 0, respath.length);
			resarrs[i] = resarr;
		}
		return BPAction.build("Delete").callback(new EventUtil.EventConsumerNodeAction(resarrs, channelid, ACTION_DELETES)).mnemonicKey(KeyEvent.VK_D).getAction();
	}

	public BPAction getRenameResAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPAction.build("Rename").callback(new EventUtil.EventConsumerNodeAction(new BPResource[] { res }, channelid, ACTION_RENAME)).mnemonicKey(KeyEvent.VK_M).getAction();
	}

	public BPAction getRefreshResAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPAction.build("Refresh").callback((e) -> BPGUICore.EVENTS_UI.trigger(channelid, BPEventUIPathTree.makeActionEvent(ACTION_REFRESH, res))).mnemonicKey(KeyEvent.VK_R).getAction();
	}

	public BPAction getPropertyAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPAction.build("Properties...").callback(new EventUtil.EventConsumerNodeAction(new BPResource[] { res }, channelid, ACTION_PROPERTIES)).mnemonicKey(KeyEvent.VK_P).getAction();
	}

	public BPAction getCopyAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPAction.build("Copy").callback(new EventUtil.EventConsumerNodeAction(tree.getSelectedLeafs(BPResource.class), channelid, ACTION_COPY)).mnemonicKey(KeyEvent.VK_C).getAction();
	}

	// public BPAction getCopyToAction(BPTreeComponent<BPTree> tree, BPResource
	// res, int channelid)
	// {
	// return BPAction.build("Copy To...").callback(new
	// EventUtil.EventConsumerNodeAction(getSelectedResources(tree), channelid,
	// ACTION_COPYTO)).getAction();
	// }

	public BPAction getCopyToAction(BPTreeComponent<BPTree> tree, BPResource[] ress, int channelid)
	{
		return BPAction.build("Copy To...").callback(new EventUtil.EventConsumerNodeAction(ress, channelid, ACTION_COPYTO)).getAction();
	}
}
