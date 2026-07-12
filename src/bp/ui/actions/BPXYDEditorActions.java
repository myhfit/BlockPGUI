package bp.ui.actions;

import javax.swing.Action;

import bp.ui.editor.BPXYDEditor;

public class BPXYDEditorActions implements BPActionHolder
{
	public Action actnewline;
	public Action actdelete;
	public Action actclone;

	public BPXYDEditorActions(BPXYDEditor<?> editor)
	{
		actnewline = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNADD, BPActionConstCommon.ACT_BTNADD_NEWLINE, e -> editor.newLine());
		actdelete = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNDEL, e -> editor.delete());
		actclone = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNCLONE, editor::showClone);
	}

	public Action[] getActions()
	{
		return new Action[] { actnewline, actdelete, BPAction.separator(), actclone };
	}
}
