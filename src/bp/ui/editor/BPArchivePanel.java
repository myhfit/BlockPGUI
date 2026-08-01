package bp.ui.editor;

import java.awt.BorderLayout;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import bp.config.BPConfig;
import bp.data.BPDataContainer;
import bp.data.BPDataContainerBase;
import bp.format.BPFormat;
import bp.format.BPFormatFeature;
import bp.format.BPFormatManager;
import bp.res.BPResource;
import bp.ui.BPViewer;
import bp.ui.tree.BPArchiveTreeFuncs;
import bp.ui.tree.BPArchiveTreeFuncs.BPTreeCellRendererZip;
import bp.ui.tree.BPArchiveTreeFuncs.BPZipFileTreeFuncs;
import bp.ui.tree.BPTreeComponentBase;
import bp.util.IOUtil;

public class BPArchivePanel extends BPAbstractEditorPanel implements BPViewer<BPDataContainer>, BPPathSelector
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1581493255863279140L;

	protected BPTreeComponentBase m_tree;
	protected JScrollPane m_scroll;
	protected BPArchiveTreeFuncs m_funcs;

	protected BPDataContainer m_con;

	protected BPFormat m_format;

	public BPArchivePanel()
	{
		init();
	}

	protected void init()
	{
		m_tree = new BPTreeComponentBase();
		m_scroll = new JScrollPane();
		m_scroll.setViewportView(m_tree);
		m_scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		setLayout(new BorderLayout());
		add(m_scroll, BorderLayout.CENTER);
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.TREE;
	}

	public void bind(BPDataContainer con, boolean noread)
	{
		m_con = con;
		if (!noread)
		{
			m_con.open();
			try
			{
				if (m_format.checkFeature(BPFormatFeature.ZIP))
				{
					BPZipFileTreeFuncs funcs = new BPZipFileTreeFuncs(m_channelid);
					funcs.setup(m_con);
					m_funcs = funcs;
					m_tree.setTreeFont();
					m_tree.setCellRenderer(new BPTreeCellRendererZip());
					m_tree.setRootVisible(false);
					m_tree.setTreeFuncs(m_funcs);
				}
				else
				{
					BPZipFileTreeFuncs funcs = new BPZipFileTreeFuncs(m_channelid);
					funcs.setup(m_con);
					m_funcs = funcs;
					m_tree.setTreeFont();
					m_tree.setCellRenderer(new BPTreeCellRendererZip());
					m_tree.setRootVisible(false);
					m_tree.setTreeFuncs(m_funcs);
				}
			}
			finally
			{
				m_con.close();
			}
		}
	}

	public void unbind()
	{
		BPDataContainer con = m_con;
		m_con = null;
		if (con != null)
		{
			con.close();
			con.unbind();
		}
	}

	public BPDataContainer getDataContainer()
	{
		return m_con;
	}

	public String getEditorInfo()
	{
		return "Archive";
	}

	public void setOnDynamicInfo(Consumer<String> info)
	{

	}

	public void setFormat(BPFormat format)
	{
		m_format = format;
	}

	public Object[][] getSelectedPaths()
	{
		Object[][] rc = null;
		Object[][] selres = m_tree.getSelectedNodePaths();
		if (selres != null)
		{
			int l = selres.length;
			rc = new Object[l][];
			System.arraycopy(selres, 0, rc, 0, l);
		}
		return rc;
	}

	public String getPathType()
	{
		if (m_format.checkFeature(BPFormatFeature.ZIP))
			return IOUtil.PATH_TYPE_ZIP;
		return null;
	}

	public Object[] getResourcesUnder(Object path)
	{
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T getRootData()
	{
		return (T) m_con;
	}

	public void clearResource()
	{
		if (m_con != null)
			unbind();
		removeAll();
	}

	public final static class BPEditorFactoryArchive implements BPEditorFactory
	{
		public String[] getFormats()
		{
			List<BPFormat> fs = BPFormatManager.getFormatsByFeature(BPFormatFeature.ARCHIVE);
			String[] fnames = new String[fs.size()];
			for (int i = 0; i < fs.size(); i++)
				fnames[i] = fs.get(i).getName();
			return fnames;
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPArchivePanel();
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			if (res.isIO())
			{
				BPDataContainerBase con = new BPDataContainerBase();
				con.bind(res);
				((BPArchivePanel) editor).setFormat(format);
				((BPArchivePanel) editor).bind(con);
			}
		}

		public String getName()
		{
			return "Archive Editor";
		}
	}
}
