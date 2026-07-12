package bp.ui.actions;

import javax.swing.Action;

import bp.ui.editor.BPTreeDataEditor;

public class BPTreeDataEditorActions implements BPActionHolder
{
	public Action actdelete;
	public Action actclone;
	public Action acteditkv;
	public Action acteditxy;
	public Action actgrabkeys;

	public BPTreeDataEditorActions(BPTreeDataEditor<?> editor)
	{
		actdelete = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNDEL, e -> editor.delete());
		actclone = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNCLONE, editor::showClone);
		acteditkv = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNEDIT, BPActionConstCommon.ACT_BTNEDIT_KV, editor::showEditKV);
		acteditxy = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNEDIT, BPActionConstCommon.ACT_BTNEDIT_XY, editor::showEditXY);
		actgrabkeys = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNEDIT, BPActionConstCommon.ACT_BTNEDIT_GRABKEYS, editor::grabKeys);
	}

	public Action[] getActions()
	{
		return new Action[] { actdelete, BPAction.separator(), acteditkv, acteditxy, actgrabkeys, actclone, BPAction.separator() };
	}
}
