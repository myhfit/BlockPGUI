package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.SwingUtilities;

import bp.res.BPResource;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.editor.BPEditor;
import bp.ui.editor.BPParallelEditorPanel;
import bp.ui.frame.BPFrame;
import bp.ui.util.UIUtil;

public class BPToolGUIParallelEditor extends BPToolGUIBase<BPToolGUIParallelEditor.BPToolGUIContextParallelEditor>
{
	public String getName()
	{
		return BPActionHelpers.getValue(BPActionConstCommon.TNAME_PARRAEDITOR, null, null);
	}

	public String getSubTitle()
	{
		return getName();
	}
	
	public boolean canInput(Class<?> cls)
	{
		if (BPResource.class.equals(cls))
			return true;
		return false;
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
			m_p = p;
			par.add(p, BorderLayout.CENTER);

			boolean added = false;
			if (params != null && params.length > 0)
			{
				Object obj = params[0];
				if (obj instanceof BPResource[])
				{
					p.batchAdd((BPResource[]) obj, null);
					added = true;
				}
				else if (obj instanceof BPResource)
				{
					p.batchAdd(new BPResource[] { (BPResource) obj }, null);
					added = true;
				}
				else if (obj instanceof BPEditor<?>[])
				{
					for (BPEditor<?> editor : (BPEditor<?>[]) obj)
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
			else
			{
				SwingUtilities.invokeLater(() -> p.refreshGrid());
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
