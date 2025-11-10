package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;

import bp.res.BPResource;
import bp.ui.editor.BPEditor;
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
			m_p = p;
			par.add(p, BorderLayout.CENTER);

			boolean added = false;
			if (params != null && params.length > 0)
			{
				Object objarr = params[0];
				if (objarr instanceof BPResource[])
				{
					p.batchAdd((BPResource[]) objarr, null);
					added = true;
				}
				else if (objarr instanceof BPEditor<?>[])
				{
					for (BPEditor<?> editor : (BPEditor<?>[]) objarr)
					{
						p.addEditor(editor);
					}
					added = true;
				}
			}
			if (!added)
			{
				p.init2Editor();
			}
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
