package bp.ui.actions;

import java.awt.event.KeyEvent;

import javax.swing.Action;

import bp.BPGUICore;
import bp.res.BPResource;
import bp.ui.event.BPEventUIResourceOperation;
import bp.ui.util.EventUtil;
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
		BPAction actopensys = BPAction.build("System Default").callback(e ->
		{
			BPGUICore.EVENTS_UI.trigger(channelid, new BPEventUIResourceOperation(BPEventUIResourceOperation.RES_ACTION, new Object[] { ress, ACTION_OPENEXTERNAL_SYSTEM, null }, UIUtil.getRouteContext(e.getSource())));
		}).mnemonicKey(KeyEvent.VK_S).getAction();
		Action[] actchd = new Action[] { actopensys };
		rc.putValue(BPAction.SUB_ACTIONS, actchd);
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
