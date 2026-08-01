package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.function.Consumer;

import bp.config.BPConfig;
import bp.data.BPDataContainerBase;
import bp.format.BPFormat;
import bp.format.BPFormatProject;
import bp.project.BPResourceProject;
import bp.res.BPResource;
import bp.ui.view.BPProjectOverviewComp;
import bp.ui.view.BPProjectOverviewCompFactory;
import bp.ui.view.BPProjectOverviewManager;

public class BPProjectPanel extends BPAbstractEditorPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8576684707228195625L;

	public BPProjectPanel()
	{
		setLayout(new BorderLayout());
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.PANEL;
	}

	public String getEditorName()
	{
		return ((BPProjectOverviewComp<?>) getComponent(0)).getProject().getName();
	}

	public String getEditorInfo()
	{
		return null;
	}

	public boolean isNoSave()
	{
		return true;
	}

	public void setOverviewComp(BPProjectOverviewComp<?> comp)
	{
		add((Component) comp, BorderLayout.CENTER);
	}

	public void setOnDynamicInfo(Consumer<String> info)
	{
	}

	public boolean isRoutable()
	{
		return true;
	}

	public static class BPEditorFactoryProject implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatProject.FORMAT_PROJECT };
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			BPEditor<?> rc = null;
			BPResourceProject prj = (BPResourceProject) res;
			BPProjectOverviewCompFactory<?> fac = BPProjectOverviewManager.getFactory(prj.getProjectTypeName());
			if (fac != null && fac.check(prj))
			{
				BPProjectPanel pnl = new BPProjectPanel();
				pnl.setOverviewComp(((BPProjectOverviewCompFactory) fac).create(prj));
				rc = pnl;
			}
			else
			{
				rc = new BPFilesPanel();
			}
			return rc;
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			if (editor instanceof BPFilesPanel)
			{
				BPDataContainerBase con = new BPDataContainerBase();
				con.bind(res);
				((BPFilesPanel) editor).bind(con, false);
			}
		}

		public String getName()
		{
			return "Project Panel";
		}

		public boolean handleFormat(String formatkey)
		{
			return true;
		}
	}
}
