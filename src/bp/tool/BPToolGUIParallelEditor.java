package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;

import bp.ui.editor.BPParallelEditorPanel;
import bp.ui.frame.BPFrame;
import bp.ui.util.UIUtil;

public class BPToolGUIParallelEditor extends BPToolGUIBase<BPToolGUIParallelEditor.BPToolGUIContextParallelEditor>
{
	public String getName()
	{
		return "Parallel Editor";
	}

	public String getSubTitle()
	{
		return getName();
	}

	protected void setFramePrefers(BPFrame f)
	{
		f.pack();
		UIUtil.setPercentWindow(f, 0.8f, 0.8f);
		if (!f.isLocationByPlatform())
			f.setLocationRelativeTo(null);
	}

	protected BPToolGUIContextParallelEditor createToolContext()
	{
		return new BPToolGUIContextParallelEditor();
	}

	protected static class BPToolGUIContextParallelEditor implements BPToolGUIBase.BPToolGUIContext
	{
		BPParallelEditorPanel m_p;

		public void initUI(Container par, Object... params)
		{
			BPParallelEditorPanel p = new BPParallelEditorPanel();
			p.setEditable(true);
			p.init2Editor();
			m_p = p;
			par.add(p, BorderLayout.CENTER);
		}

		public void initDatas(Object... params)
		{
		}

		public void clearResource()
		{
			BPParallelEditorPanel p = m_p;
			m_p = null;
			if (p != null)
				p.clearResource();
		}
	}
}
