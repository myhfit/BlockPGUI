package bp.ui.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.Action;

import bp.BPGUICore;
import bp.project.BPProjectItemFactory;
import bp.project.BPResourceProject;
import bp.res.BPResource;
import bp.tool.BPTool;
import bp.tool.BPToolGUI;
import bp.ui.scomp.BPTree;
import bp.ui.tree.BPPathTreePanel.BPEventUIPathTree;
import bp.ui.tree.BPTreeComponent;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.EventUtil;

public class BPPathTreeNodeActions extends BPTreeNodeActions
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
		BPAction rc = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUNEW, null);
		BPAction actnewfile = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUNEWFILE, e -> BPGUICore.EVENTS_UI.trigger(channelid, BPEventUIPathTree.makeActionEvent(ACTION_NEWFILE, res)));
		BPAction actnewdir = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUNEWDIR, e -> BPGUICore.EVENTS_UI.trigger(channelid, BPEventUIPathTree.makeActionEvent(ACTION_NEWDIR, res)));
		Action[] actchd = new Action[] { actnewfile, actnewdir };
		rc.putValue(BPAction.SUB_ACTIONS, actchd);
		return rc;
	}

	public BPAction getNewFileOnlyDirAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		BPAction rc = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUNEW, null);
		BPAction actnewdir = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUNEWDIR, e -> BPGUICore.EVENTS_UI.trigger(channelid, BPEventUIPathTree.makeActionEvent(ACTION_NEWDIR, res)));
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
		BPAction rc = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUNEW, null);
		BPAction actnewfile = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUNEWFILE, e -> BPGUICore.EVENTS_UI.trigger(channelid, BPEventUIPathTree.makeActionEvent(ACTION_NEWFILE, res)));
		BPAction actnewdir = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUNEWDIR, e -> BPGUICore.EVENTS_UI.trigger(channelid, BPEventUIPathTree.makeActionEvent(ACTION_NEWDIR, res)));
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
		return BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUOPEN, new EventUtil.EventConsumerMakePathTreeAction(res, channelid, ACTION_OPENFILE));
	}

	public BPAction getOpenFileAsAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUOPENAS, new EventUtil.EventConsumerMakePathTreeAction(res, channelid, ACTION_OPENFILEAS));
	}

	public BPAction getOpenFileExternalAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		BPAction rc = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUOPENEXT, null);
		BPAction actopensys = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUOPENEXTSYS, new EventUtil.EventConsumerMakePathTreeAction(res, channelid, ACTION_OPENEXTERNAL_SYSTEM));
		BPAction acteditsys = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUOPENEXTEDIT, new EventUtil.EventConsumerMakePathTreeAction(res, channelid, ACTION_EDITEXTERNAL_SYSTEM));
		BPAction actprintsys = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUOPENEXTPRINT, new EventUtil.EventConsumerMakePathTreeAction(res, channelid, ACTION_PRINTEXTERNAL_SYSTEM));
		Action[] actchd = new Action[] { actopensys, acteditsys, actprintsys };
		rc.putValue(BPAction.SUB_ACTIONS, actchd);
		return rc;
	}

	public BPAction getOpenFileWithToolActionOld(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		BPAction rc = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUOPENTOOL, new EventUtil.EventConsumerNodeAction(tree.getSelectedLeafs(BPResource.class), channelid, ACTION_OPENWITHTOOL));
		return rc;
	}

	public BPAction getOpenFileWithToolAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		BPResource[] ress = tree.getSelectedLeafs(BPResource.class);
		BPAction rc = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUOPENTOOL, null);
		Supplier<Action[]> submenucb = () ->
		{
			Map<String, List<BPTool>> toolmap = new HashMap<String, List<BPTool>>(BPGUICore.TOOL_MAP);
			List<BPToolGUI> tools = new ArrayList<BPToolGUI>();
			for (List<BPTool> ts : toolmap.values())
			{
				for (BPTool t : ts)
				{
					if (t instanceof BPToolGUI)
					{
						BPToolGUI tg = (BPToolGUI) t;
						if (tg.canInput(BPResource.class))
							tools.add(tg);
					}
				}
			}
			tools.sort((a, b) -> a.getName().compareTo(b.getName()));
			List<Action> r2 = new ArrayList<Action>();
			for (BPToolGUI tool : tools)
			{
				BPAction act = BPAction.build(tool.getName()).callback((e) -> tool.showTool(new Object[] { ress })).getAction();
				r2.add(act);
			}
			return r2.toArray(new Action[r2.size()]);
		};
		rc.putValue(BPAction.SUB_ACTIONS_FUNC, submenucb);
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
		return BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUDEL, new EventUtil.EventConsumerNodeAction(resarrs, channelid, ACTION_DELETES));
	}

	public BPAction getRenameResAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPActionHelpers.getAction(BPActionConstCommon.CTX_MNURENAME, new EventUtil.EventConsumerNodeAction(new BPResource[] { res }, channelid, ACTION_RENAME));
	}

	public BPAction getRefreshResAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUREFRESH, e -> BPGUICore.EVENTS_UI.trigger(channelid, BPEventUIPathTree.makeActionEvent(ACTION_REFRESH, res)));
	}

	public BPAction getPropertyAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUPROP, new EventUtil.EventConsumerNodeAction(new BPResource[] { res }, channelid, ACTION_PROPERTIES));
	}

	public BPAction getCopyAction(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		return BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUCOPY, new EventUtil.EventConsumerNodeAction(tree.getSelectedLeafs(BPResource.class), channelid, ACTION_COPY));
	}

	public BPAction getCopyToAction(BPTreeComponent<BPTree> tree, BPResource[] ress, int channelid)
	{
		return BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUCOPYTO, new EventUtil.EventConsumerNodeAction(ress, channelid, ACTION_COPYTO));
	}
}
