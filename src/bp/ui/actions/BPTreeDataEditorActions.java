package bp.ui.actions;

import javax.swing.Action;

import bp.ui.editor.BPTreeDataEditor;
import bp.ui.res.icon.BPIconResV;

public class BPTreeDataEditorActions implements BPActionHolder
{
	public Action actdelete;
	public Action actclone;
	protected BPTreeDataEditor<?> m_editor;

	public BPTreeDataEditorActions(BPTreeDataEditor<?> editor)
	{
		m_editor = editor;
		actdelete = BPAction.build("Delete").callback((e) -> m_editor.delete()).vIcon(BPIconResV.DEL()).tooltip("Delete").getAction();
		actclone = BPAction.build("Clone").callback(m_editor::showClone).vIcon(BPIconResV.CLONE()).tooltip("Clone Data").getAction();
	}

	public Action[] getActions()
	{
		return new Action[] { actdelete, BPAction.separator(), actclone, BPAction.separator() };
	}
}
