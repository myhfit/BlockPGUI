package bp.ui.actions;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.Action;

import bp.BPGUICore;
import bp.res.BPResource;
import bp.tool.BPTool;
import bp.tool.BPToolGUI;
import bp.ui.event.BPEventUIResourceOperation;
import bp.ui.util.EventUtil;
import bp.ui.util.SystemUIUtil;
import bp.ui.util.UIUtil;

public class BPFileActions
{
	public final static String ACTION_NEWFILE = "newfile";
	public final static String ACTION_NEWDIR = "newdir";
	public final static String ACTION_OPEN = "open";
	public final static String ACTION_OPENAS = "openas";
	public final static String ACTION_DELETE = "delete";
	public final static String ACTION_RENAME = "rename";
	public final static String ACTION_PROPERTIES = "prop";
	public final static String ACTION_OPENEXTERNAL_SYSTEM = "openextsys";
	public final static String ACTION_EDITEXTERNAL_SYSTEM = "editextsys";
	public final static String ACTION_PRINTEXTERNAL_SYSTEM = "printextsys";

	public BPFileActions()
	{
	}

	public BPAction getNewFileAction(BPResource res, int channelid)
	{
		BPAction rc = BPAction.build("New").mnemonicKey(KeyEvent.VK_N).getAction();
		BPAction actnewfile = BPAction.build("File").callback((e) -> BPGUICore.EVENTS_UI.trigger(channelid, BPEventUIResourceOperation.makeActionEvent(ACTION_NEWFILE, res, UIUtil.getRouteContext(e.getSource())))).mnemonicKey(KeyEvent.VK_F)
				.getAction();
		BPAction actnewdir = BPAction.build("Directory").callback((e) -> BPGUICore.EVENTS_UI.trigger(channelid, BPEventUIResourceOperation.makeActionEvent(ACTION_NEWDIR, res, UIUtil.getRouteContext(e.getSource())))).mnemonicKey(KeyEvent.VK_D)
				.getAction();
		Action[] actchd = new Action[] { actnewfile, actnewdir };
		rc.putValue(BPAction.SUB_ACTIONS, actchd);
		return rc;
	}

	public BPAction getOpenFileAction(BPResource[] ress, int channelid)
	{
		return BPAction.build("Open").callback(e ->
		{
			BPGUICore.EVENTS_UI.trigger(channelid, new BPEventUIResourceOperation(BPEventUIResourceOperation.RES_ACTION, new Object[] { ress, ACTION_OPEN, null }, UIUtil.getRouteContext(e.getSource())));
		}).mnemonicKey(KeyEvent.VK_O).getAction();
	}

	public BPAction getOpenFileAsAction(BPResource[] ress, int channelid)
	{
		return BPAction.build("Open As...").callback(e ->
		{
			BPGUICore.EVENTS_UI.trigger(channelid, new BPEventUIResourceOperation(BPEventUIResourceOperation.RES_ACTION, new Object[] { ress, ACTION_OPENAS, null }, UIUtil.getRouteContext(e.getSource())));
		}).mnemonicKey(KeyEvent.VK_A).getAction();
	}

	public BPAction getOpenFileExternalAction(BPResource[] ress, int channelid)
	{
		BPAction rc = BPAction.build("Open External").getAction();
		List<Action> actchd = new ArrayList<Action>();
		{
			BPAction actopensys = BPAction.build("Open").callback(e ->
			{
				BPGUICore.EVENTS_UI.trigger(channelid, new BPEventUIResourceOperation(BPEventUIResourceOperation.RES_ACTION, new Object[] { ress, ACTION_OPENEXTERNAL_SYSTEM, null }, UIUtil.getRouteContext(e.getSource())));
			}).mnemonicKey(KeyEvent.VK_O).getAction();
			BPAction acteditsys = BPAction.build("Edit").callback(e ->
			{
				BPGUICore.EVENTS_UI.trigger(channelid, new BPEventUIResourceOperation(BPEventUIResourceOperation.RES_ACTION, new Object[] { ress, ACTION_EDITEXTERNAL_SYSTEM, null }, UIUtil.getRouteContext(e.getSource())));
			}).mnemonicKey(KeyEvent.VK_E).getAction();
			BPAction actprintsys = BPAction.build("Print").callback(e ->
			{
				BPGUICore.EVENTS_UI.trigger(channelid, new BPEventUIResourceOperation(BPEventUIResourceOperation.RES_ACTION, new Object[] { ress, ACTION_PRINTEXTERNAL_SYSTEM, null }, UIUtil.getRouteContext(e.getSource())));
			}).mnemonicKey(KeyEvent.VK_P).getAction();
			actchd.add(actopensys);
			actchd.add(acteditsys);
			actchd.add(actprintsys);
		}
		if (ress.length == 1)
		{
			String ext = ress.length == 1 ? ress[0].getExt() : null;
			Supplier<Action[]> faab = SystemUIUtil.getFileAssocActionBuilder(ext);
			if (faab != null)
			{
				BPAction actopensysdyna = BPAction.build("System Assoc").getAction();
				actopensysdyna.putValue(BPAction.SUB_ACTIONS_FUNC, faab);
				actchd.add(actopensysdyna);
			}
		}
		rc.putValue(BPAction.SUB_ACTIONS, actchd.toArray(new Action[actchd.size()]));
		return rc;
	}
	
	public BPAction getOpenFileWithToolAction(BPResource[] ress, int channelid)
	{
		BPAction rc = BPAction.build("Open With Tool").mnemonicKey(KeyEvent.VK_W).getAction();
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

	public BPAction getDeleteResourcesAction(BPResource[] ress, int channelid)
	{
		return BPAction.build("Delete").callback(new EventUtil.EventConsumerResourceOPAction(ress, channelid, ACTION_DELETE)).mnemonicKey(KeyEvent.VK_D).getAction();
	}

	public BPAction getRenameResAction(BPResource res, int channelid)
	{
		return BPAction.build("Rename").callback(new EventUtil.EventConsumerResourceOPAction(new BPResource[] { res }, channelid, ACTION_RENAME)).mnemonicKey(KeyEvent.VK_M).getAction();
	}

	public BPAction getPropertyAction(BPResource[] ress, int channelid)
	{
		return BPAction.build("Properties...").callback(new EventUtil.EventConsumerResourceOPAction(ress, channelid, ACTION_PROPERTIES)).mnemonicKey(KeyEvent.VK_P).getAction();
	}
}
