package bp.ui.actions;

import javax.swing.Action;

import bp.ui.editor.BPXYDEditor;
import bp.ui.res.icon.BPIconResV;

public class BPXYDEditorActions implements BPActionHolder
{
	public Action actnewline;
	public Action actdelete;
	public Action actclone;
	protected BPXYDEditor<?> m_editor;

	public BPXYDEditorActions(BPXYDEditor<?> editor)
	{
		m_editor = editor;
		actnewline = BPAction.build("New Line").callback((e) -> m_editor.newLine()).vIcon(BPIconResV.ADD()).tooltip("Create New Line").getAction();
		actdelete = BPAction.build("Delete").callback((e) -> m_editor.delete()).vIcon(BPIconResV.DEL()).tooltip("Delete").getAction();
		actclone = BPAction.build("Clone").callback(m_editor::showClone).vIcon(BPIconResV.CLONE()).tooltip("Clone Data").getAction();
	}

	public Action[] getActions()
	{
		return new Action[] { actnewline, actdelete, BPAction.separator(), actclone, BPAction.separator() };
	}
}
