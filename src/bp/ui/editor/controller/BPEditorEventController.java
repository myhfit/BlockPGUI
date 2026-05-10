package bp.ui.editor.controller;

import java.util.function.Consumer;

import bp.ui.editor.BPEditor;
import bp.ui.editor.BPEditor.BPEditorEvent;
import bp.util.LogicUtil.WeakRefGo;

public interface BPEditorEventController
{
	void dispatchEvent(String action, Object data, Object... params);

	void installHandler(Consumer<BPEditorEvent> handler);

	public static class BPEditorEventControllerBase implements BPEditorEventController
	{
		protected BPEditor<?> m_editor;
		protected WeakRefGo<Consumer<BPEditorEvent>> m_handlerref;

		public BPEditorEventControllerBase(BPEditor<?> editor)
		{
			m_editor = editor;
			m_handlerref = new WeakRefGo<>();
		}

		public void dispatchEvent(String action, Object data, Object... params)
		{
			final BPEditorEvent e = new BPEditorEvent(action, m_editor, data, params);
			m_handlerref.run(seg -> seg.accept(e));
		}

		public void installHandler(Consumer<BPEditorEvent> handler)
		{
			m_handlerref.setTarget(handler);
		}
	}
}
