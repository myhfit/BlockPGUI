package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.function.Consumer;

import javax.swing.JPanel;

import bp.config.BPConfig;
import bp.data.BPDataContainerBase;
import bp.format.BPFormat;
import bp.format.BPFormatProject;
import bp.project.BPResourceProject;
import bp.res.BPResource;
import bp.ui.view.BPProjectOverviewComp;
import bp.ui.view.BPProjectOverviewCompFactory;
import bp.ui.view.BPProjectOverviewManager;

public class BPProjectPanel extends JPanel implements BPEditor<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8576684707228195625L;

	protected String m_id;
	protected int m_channelid;

	public BPProjectPanel()
	{
		setLayout(new BorderLayout());
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.PANEL;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void focusEditor()
	{
	}

	public String getEditorInfo()
	{
		return null;
	}

	public void save()
	{
	}

	public void setOverviewComp(BPProjectOverviewComp<?> comp)
	{
		add((Component) comp, BorderLayout.CENTER);
	}

	public void reloadData()
	{
	}

	public boolean needSave()
	{
		return false;
	}

	public void setNeedSave(boolean needsave)
	{
	}

	public void setID(String id)
	{
		m_id = id;
	}

	public String getID()
	{
		return m_id;
	}

	public void setChannelID(int channelid)
	{
		m_channelid = channelid;
	}

	public int getChannelID()
	{
		return m_channelid;
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
