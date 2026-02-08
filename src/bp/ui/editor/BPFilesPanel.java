package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;

import bp.BPCore;
import bp.BPGUICore;
import bp.config.BPConfig;
import bp.data.BPDataContainer;
import bp.data.BPDataContainerArchive;
import bp.data.BPDataContainerBase;
import bp.data.BPDataContainerFileSystem;
import bp.data.BPXData;
import bp.data.BPXData.BPXDataArray;
import bp.data.BPXYData.BPXYDataList;
import bp.event.BPEventCoreUI;
import bp.format.BPFormat;
import bp.format.BPFormatDir;
import bp.format.BPFormatFeature;
import bp.format.BPFormatManager;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceHolder;
import bp.ui.BPViewer;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.actions.BPFileActions;
import bp.ui.container.BPToolBarSQ;
import bp.ui.dialog.BPDialogSimple;
import bp.ui.event.BPEventUIResourceOperation;
import bp.ui.form.BPFormPanelXYData;
import bp.ui.parallel.BPEventUISyncEditor;
import bp.ui.parallel.BPSyncGUI;
import bp.ui.parallel.BPSyncGUIController;
import bp.ui.parallel.BPSyncGUIControllerBase;
import bp.ui.scomp.BPTable;
import bp.ui.table.BPTableFuncsResourceFiles;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.FileUtil;
import bp.util.ObjUtil;
import bp.util.SystemUtil;
import bp.util.SystemUtil.SystemOS;

public class BPFilesPanel extends JPanel implements BPEditor<JPanel>, BPViewer<BPDataContainer>, BPSyncGUI
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7009889832393872251L;

	public final static String SYNCPOSTYPE_FILES_LIST = "FILES_L";
	public final static String SYNCSELSTYPE_FILES = "FILES";

	protected BPDataContainer m_con;
	protected List<BPResource> m_children;
	protected int m_seli = -1;
	protected WeakReference<Consumer<String>> m_dynainfo = null;
	protected String m_info;
	protected int m_channelid;
	protected BPToolBarSQ m_toolbar;
	protected Action[] m_acts;
	protected BPTable<BPResource> m_table;
	protected BPTableFuncsResourceFiles m_tablefuncs;
	protected Consumer<BPEventCoreUI> m_refreshpathhandler;
	protected Consumer<BPEventUISyncEditor> m_synccb;
	protected boolean m_navmode;
	protected JScrollPane m_scroll;
	protected BPSyncGUIController m_syncobj;

	protected String m_id;

	public BPFilesPanel()
	{
		m_navmode = true;
		init();
	}

	protected void init()
	{
		setLayout(new BorderLayout());
		m_toolbar = new BPToolBarSQ(true);
		m_toolbar.setBorderVertical(0);
		m_tablefuncs = new BPTableFuncsResourceFiles();
		m_table = new BPTable<BPResource>(m_tablefuncs);
		m_table.addMouseListener(new UIUtil.BPMouseListener(this::onTableClick, null, null, null, null));
		m_table.getInputMap(BPTable.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deletefiles");
		m_table.getActionMap().put("deletefiles", BPAction.build("deletefiles").callback(this::onDeleteFile).getAction());
		m_table.setBorder(null);
		m_table.getColumnModel().getColumn(2).setCellRenderer(new BPTable.BPTableRendererFileSize());
		m_table.getColumnModel().getColumn(3).setCellRenderer(new BPTable.BPTableRendererDateTime());
		m_table.getSelectionModel().addListSelectionListener(this::onSelectionChanged);
		m_table.setTableFont();
		JScrollPane scroll = new JScrollPane();
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		scroll.setViewportView(m_table);
		m_scroll = scroll;

		BPAction actrefresh = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNREFRESH, e -> refresh());
		BPAction actstat = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNSTAT, e -> stat());
		m_toolbar.setActions(new Action[] { BPAction.separator(), actrefresh, BPAction.separator(), actstat }, this);

		scroll.getHorizontalScrollBar().addAdjustmentListener(this::onScroll);
		scroll.getVerticalScrollBar().addAdjustmentListener(this::onScroll);

		add(scroll, BorderLayout.CENTER);
		add(m_toolbar, BorderLayout.WEST);
		initActions();
		initBPEvents();
	}

	protected void initBPEvents()
	{
		m_refreshpathhandler = this::onRefreshPathEvent;

		m_synccb = this::onSyncEditor;
		m_syncobj = new BPSyncGUIControllerBase(m_synccb);

		BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_REFRESHPATHTREE, m_refreshpathhandler);
	}

	protected void initActions()
	{
		m_acts = new Action[] {};
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.PANEL;
	}

	public JPanel getComponent()
	{
		return this;
	}

	protected void onCopy(ActionEvent e)
	{
		copy();
	}

	protected void onScroll(AdjustmentEvent e)
	{
		if (m_syncobj.checkSyncAndNoBlock())
		{
			int[] xy = new int[] { m_scroll.getHorizontalScrollBar().getValue(), m_scroll.getVerticalScrollBar().getValue() };
			m_syncobj.trigger(BPEventUISyncEditor.syncPosition(m_id, SYNCPOSTYPE_FILES_LIST, xy));
		}
	}

	public BPSyncGUIController getSyncStatusController()
	{
		return m_syncobj;
	}

	public void copy()
	{
	}

	protected static class FileStatItem
	{
		public long count;
		public long size;
		public String label;
	}

	protected void stat()
	{
		List<String> cats = Arrays.asList("Filename Extension");
		String cat = UIStd.select(cats, "Select Statistics Function", null);
		if (cat == null)
			return;
		switch (cat)
		{
			case "Filename Extension":
			{
				BPResource res = m_tablefuncs.getBaseResource();
				if (res != null && res.isFileSystem())
				{
					BPResourceFileSystem fres = (BPResourceFileSystem) res;
					List<FileStatItem> stats = new ArrayList<FileStatItem>();
					boolean extci = SystemUtil.getOS() == SystemOS.Windows;
					if (fres.isDirectory() && fres.isDirectory())
					{
						Map<String, FileStatItem> countmap = new LinkedHashMap<String, FileStatItem>();
						File f = new File(fres.getFileFullName());
						FileUtil.forEachFile(f, true, (d, sf) ->
						{
							String ext = FileUtil.getExt(sf.getName());
							if (extci)
								ext = ext.toLowerCase();
							FileStatItem item = countmap.get(ext);
							long s = sf.length();
							if (item == null)
							{
								item = new FileStatItem();
								item.label = ext;
								countmap.put(ext, item);
								stats.add(item);
							}
							item.count++;
							item.size += s;
							return true;
						});
					}
					else if (m_con instanceof BPDataContainerFileSystem)
					{
						Map<String, FileStatItem> countmap = new LinkedHashMap<String, FileStatItem>();
						List<BPResource> chds = m_table.getBPTableModel().getDatas();
						for (BPResource chd : chds)
						{
							String ext = FileUtil.getExt(chd.getName());
							if (extci)
								ext = ext.toLowerCase();
							long s = 0;
							BPResourceHolder hres = (BPResourceHolder) chd;
							byte[] bs = hres.getData();
							if (bs != null)
								s = bs.length;
							else
								continue;

							FileStatItem item = countmap.get(ext);
							if (item == null)
							{
								item = new FileStatItem();
								item.label = ext;
								countmap.put(ext, item);
								stats.add(item);
							}
							item.count++;
							item.size += s;
						}
					}
					if (stats != null)
					{
						BPFormPanelXYData p = new BPFormPanelXYData();
						List<BPXData> newdatalist = new ArrayList<BPXData>();
						for (FileStatItem item : stats)
						{
							BPXDataArray itemline = new BPXDataArray(new Object[] { item.label, item.count, item.size });
							newdatalist.add(itemline);
						}
						BPXYDataList newdata = new BPXYDataList(new Class<?>[] { String.class, Long.class, Long.class }, new String[] { "Label", "Count", "Size" }, null, newdatalist, true);
						p.showData(ObjUtil.makeMap("_xydata", newdata), false);
						BPDialogSimple.showComponent(p, BPDialogSimple.COMMANDBAR_OK, null, BPGUICore.S_BP_TITLE + " - Statistics", this.getFocusCycleRootAncestor());
					}
				}
				break;
			}
		}
	}

	protected void refresh()
	{
		setBaseResource(m_con.getResource());
	}

	protected void onRefreshPathEvent(BPEventCoreUI e)
	{
		if (BPEventCoreUI.EVENTKEY_COREUI_REFRESHPATHTREE.equals(e.key))
		{
			String subkey = e.subkey;
			if (subkey != null && subkey.equals(m_id))
			{
				refresh();
			}
		}
	}

	protected void setBaseResource(BPResource res)
	{
		List<BPResource> children = new ArrayList<BPResource>();
		m_tablefuncs.setBaseResource(res);
		boolean isdir = res.isFileSystem() && ((BPResourceFileSystem) res).isDirectory();
		List<BPResource> childrenf = new ArrayList<BPResource>();
		List<BPResource> childrend = new ArrayList<BPResource>();

		if (isdir)
		{
			BPResourceFileSystem fres = (BPResourceFileSystem) res;
			BPResource[] subfs = fres.listResources();
			for (BPResource subf : subfs)
			{
				if (subf.isFileSystem())
				{
					BPResourceFileSystem f = (BPResourceFileSystem) subf;
					if (checkEntry(f.getName(), f.isDirectory()))
					{
						if (f.isFile())
						{
							childrenf.add(f);
						}
						else
						{
							childrend.add(f);
						}
					}
				}
			}
			children.addAll(childrend);
			children.addAll(childrenf);
			childrend.clear();
			childrenf.clear();
		}
		else if (m_con instanceof BPDataContainerFileSystem)
		{
			m_con.open();
			BPDataContainerFileSystem confs = (BPDataContainerFileSystem) m_con;
			confs.readFull(this::checkEntry);
			BPResource[] subfs = confs.listResources();
			for (BPResource subf : subfs)
			{
				children.add(subf);
			}
		}

		initList(children);
	}

	public void bind(BPDataContainer con, boolean noread)
	{
		m_con = con;
		if (!noread)
		{
			BPResource res = m_con.getResource();
			setBaseResource(res);
		}
	}

	protected void initList(List<BPResource> children)
	{
		m_table.getBPTableModel().setDatas(children);
		m_table.initRowSorter();
		m_table.refreshData();
	}

	protected boolean checkEntry(String name, boolean isdir)
	{
		return true;
	}

	protected void sendDynamicInfo(String info)
	{
		WeakReference<Consumer<String>> dynainfo = m_dynainfo;
		if (dynainfo != null)
		{
			Consumer<String> cb = dynainfo.get();
			if (cb != null)
			{
				cb.accept(info);
			}
		}
	}

	public void unbind()
	{
		m_con.close();
		m_con = null;
	}

	public void clearResource()
	{
		m_syncobj.clearResource();
		if (m_table != null)
		{
			m_table.clearResource();
			removeAll();
			m_table = null;
		}
		if (m_con != null)
		{
			m_con.close();
			m_con = null;
		}
	}

	public BPDataContainer getDataContainer()
	{
		return m_con;
	}

	public void focusEditor()
	{
		this.requestFocus();
	}

	public void save()
	{
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

	public boolean isRoutable()
	{
		return true;
	}

	public void setChannelID(int channelid)
	{
		m_channelid = channelid;
		m_tablefuncs.setChannelID(channelid);
		m_syncobj.setChannelID(channelid);
	}

	public int getChannelID()
	{
		return m_channelid;
	}

	public String getEditorInfo()
	{
		return m_info;
	}

	public void setOnDynamicInfo(Consumer<String> info)
	{
		m_dynainfo = new WeakReference<Consumer<String>>(info);
	}

	public Action[] getEditMenuActions()
	{
		return m_acts;
	}

	protected void onTableClick(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
		{
			List<BPResource> ress = m_table.getSelectedDatas();
			if (ress != null && ress.size() > 0)
			{
				BPGUICore.EVENTS_UI.trigger(m_channelid,
						new BPEventUIResourceOperation(BPEventUIResourceOperation.RES_ACTION, new Object[] { ress.toArray(new BPResource[ress.size()]), BPFileActions.ACTION_OPEN, null }, UIUtil.getRouteContext(e.getSource())));
			}
		}
	}

	protected void onDeleteFile(ActionEvent e)
	{
		List<BPResource> ress = m_table.getSelectedDatas();
		if (ress != null && ress.size() > 0)
		{
			BPGUICore.EVENTS_UI.trigger(m_channelid,
					new BPEventUIResourceOperation(BPEventUIResourceOperation.RES_ACTION, new Object[] { ress.toArray(new BPResource[ress.size()]), BPFileActions.ACTION_DELETE, null }, UIUtil.getRouteContext(e.getSource())));
		}
	}

	public final static class BPEditorFactoryFiles implements BPEditorFactory
	{
		public String[] getFormats()
		{
			List<BPFormat> fs = BPFormatManager.getFormatsByFeature(BPFormatFeature.PATHTREE);
			String[] fnames = new String[fs.size()];
			for (int i = 0; i < fs.size(); i++)
			{
				fnames[i] = fs.get(i).getName();
			}
			return fnames;
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPFilesPanel();
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			if (res != null)
			{
				if (res.isFileSystem())
				{
					BPResourceFileSystem resfs = (BPResourceFileSystem) res;
					BPDataContainer con = null;
					if (resfs.isDirectory())
					{
						con = new BPDataContainerBase();
					}
					else
					{
						if (format.checkFeature(BPFormatFeature.ARCHIVE))
						{
							con = new BPDataContainerArchive();
						}
					}
					if (con != null)
					{
						con.bind(res);
						((BPFilesPanel) editor).bind(con, false);
					}
				}
				else if (format.checkFeature(BPFormatFeature.ARCHIVE))
				{
					BPDataContainer con = new BPDataContainerArchive();
					con.bind(res);
					((BPFilesPanel) editor).bind(con, false);
				}
			}
		}

		public String getName()
		{
			return "Files Viewer";
		}

		public boolean handleFormat(String formatkey)
		{
			if (formatkey.equals(BPFormatDir.FORMAT_DIR))
				return true;
			return false;
		}
	}

	public BPResource[] getSubResources(String[] path)
	{
		BPResource[] rc = new BPResource[path.length];
		BPResource base = m_tablefuncs.getBaseResource();
		if (base.isFileSystem() && !base.isLeaf())
		{
			BPResourceDir d = (BPResourceDir) base;
			for (int i = 0; i < path.length; i++)
			{
				rc[i] = d.getChild(path[i]);
			}
		}
		return rc;
	}

	public int[] getFileIndex(BPResource[] ress)
	{
		int[] rc = new int[ress.length];
		List<BPResource> datas = m_table.getBPTableModel().getDatas();
		for (int i = 0; i < ress.length; i++)
		{
			rc[i] = datas.indexOf(ress[i]);
		}
		return rc;
	}

	protected void onSyncEditor(BPEventUISyncEditor e)
	{
		if (BPEventUISyncEditor.SYNC_POS.equals(e.subkey))
		{
			if (SYNCPOSTYPE_FILES_LIST.equals(e.getSyncDataType()))
			{
				String id = (String) e.datas[0];
				if (!m_id.equals(id))
				{
					int[] xy = e.getSyncData();
					m_scroll.getHorizontalScrollBar().setValue(xy[0]);
					m_scroll.getVerticalScrollBar().setValue(xy[1]);
				}
			}
		}
		else if (BPEventUISyncEditor.SYNC_SELECTION.equals(e.subkey))
		{
			if (SYNCSELSTYPE_FILES.equals(e.getSyncDataType()))
			{
				String id = (String) e.datas[0];
				if (!m_id.equals(id))
				{
					String[] sels = e.getSyncData();
					m_syncobj.blockSync(() ->
					{
						int[] tcs = getFileIndex(ObjUtil.collectNotEmpty(getSubResources(sels)).toArray(new BPResource[0]));
						if (tcs.length > 0)
						{
							m_table.setSelectionRows(tcs);
							m_table.scrollRectToVisible(m_table.getCellRect(m_table.convertRowIndexToView(tcs[0]), 0, true));
						}
						else
							m_table.clearSelection();
					});
				}
			}
		}
	}

	protected void onSelectionChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting() && m_syncobj.checkSyncAndNoBlock())
		{
			List<BPResource> ress = m_table.getSelectedDatas();
			String[] resstrs = new String[ress.size()];
			BPResource base = m_tablefuncs.getBaseResource();
			if (base.isFileSystem() && !base.isLeaf())
			{
				String basestr = ((BPResourceFileSystem) base).getFileFullName();
				for (int i = 0; i < ress.size(); i++)
				{
					resstrs[i] = ((BPResourceFileSystem) ress.get(i)).getFileFullName().substring(basestr.length());
				}
				m_syncobj.trigger(BPEventUISyncEditor.syncSelection(m_id, SYNCSELSTYPE_FILES, resstrs));
			}
			else
			{
				for (int i = 0; i < ress.size(); i++)
				{
					resstrs[i] = ress.get(i).getName();
				}
				m_syncobj.trigger(BPEventUISyncEditor.syncSelection(m_id, SYNCSELSTYPE_FILES, resstrs));
			}
		}
	}
}