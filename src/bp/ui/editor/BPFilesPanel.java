package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
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
import bp.locale.BPLocaleConstCC;
import bp.locale.BPLocaleHelpers;
import bp.res.BPResource;
import bp.res.BPResourceDir;
import bp.res.BPResourceFile;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceHolder;
import bp.transform.BPTransformerFactory;
import bp.transform.BPTransformerManager;
import bp.transform.BPTransformerRuleFilter;
import bp.ui.BPViewer;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.actions.BPFileActions;
import bp.ui.container.BPToolBarSQ;
import bp.ui.dialog.BPDialogSimple;
import bp.ui.editor.controller.BPEditorController;
import bp.ui.event.BPEventUIResourceOperation;
import bp.ui.form.BPFormPanelXYData;
import bp.ui.parallel.BPEventUISyncEditor;
import bp.ui.scomp.BPFilterComponent;
import bp.ui.scomp.BPFilterDataListPanel;
import bp.ui.scomp.BPPipedFilterDataListPanel;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableColumnModel;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.table.BPTableFuncsResourceFiles;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.FileUtil;
import bp.util.ObjUtil;
import bp.util.Std;
import bp.util.SystemUtil;
import bp.util.SystemUtil.SystemOS;

public class BPFilesPanel extends JPanel implements BPEditor<JPanel>, BPViewer<BPDataContainer>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7009889832393872251L;

	public final static String SYNCPOSTYPE_FILES_LIST = "FILES_L";
	public final static String SYNCSELSTYPE_FILES = "FILES";

	protected BPDataContainer m_con;
	protected int m_seli = -1;
	protected WeakReference<Consumer<String>> m_dynainfo = null;
	protected String m_info;
	protected int m_channelid;
	protected BPToolBarSQ m_toolbar;
	protected Action m_acttogglelistsub;
	protected Action m_actrefresh;
	protected Action[] m_acts;
	protected BPTable<BPResource> m_table;
	protected BPTableFuncsResourceFiles m_tablefuncs;
	protected Consumer<BPEventCoreUI> m_refreshpathhandler;
	protected boolean m_navmode;
	protected JScrollPane m_scroll;
	protected boolean m_listsub;
	protected boolean m_isonlylist;
	protected BPEditorController m_ec;

	protected String m_id;

	public BPFilesPanel()
	{
		m_ec = new BPEditorController(this);
		m_navmode = true;
		m_listsub = false;
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
		initTableColumn();
		m_table.getSelectionModel().addListSelectionListener(this::onSelectionChanged);
		m_table.setTableFont();
		JScrollPane scroll = new JScrollPane();
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		scroll.setViewportView(m_table);
		m_scroll = scroll;

		m_actrefresh = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNREFRESH, e -> refresh());
		BPAction actstat = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNSTAT, e -> stat());
		BPAction actfilter = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNFILTER, e -> onShowFilter(false));
		BPAction actcfilter = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNCHAINFILTER, e -> onShowFilter(true));
		m_acttogglelistsub = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNTOGGLE, BPActionConstCommon.ACT_BTNTOGGLE_LISTSUB, this::toggleListSub);
		m_toolbar.setActions(new Action[] { BPAction.separator(), m_actrefresh, m_acttogglelistsub, BPAction.separator(), actstat, actfilter, actcfilter }, this);

		scroll.getHorizontalScrollBar().addAdjustmentListener(this::onScroll);
		scroll.getVerticalScrollBar().addAdjustmentListener(this::onScroll);

		add(scroll, BorderLayout.CENTER);
		add(m_toolbar, BorderLayout.WEST);
		initActions();
		initBPEvents();
	}

	protected void initTableColumn()
	{
		BPTableColumnModel tcm = new BPTableColumnModel();
		m_table.setColumnModel(tcm);
		m_table.createDefaultColumnsFromModel();
		tcm.getColumn(3).setCellRenderer(new BPTable.BPTableRendererFileSize());
		tcm.getColumn(4).setCellRenderer(new BPTable.BPTableRendererDateTime());
		tcm.setColumnHide("Path", !m_listsub);
		tcm.saveCache();
		if (!m_listsub)
		{
			tcm.removeColumn(tcm.getColumn(0));
		}
		m_table.setAutoResizeMode(BPTable.AUTO_RESIZE_NEXT_COLUMN);
		tcm.applyDefaultColumnWidth(m_table.getBPTableModel().getTableFuncs());
	}

	protected void initBPEvents()
	{
		m_refreshpathhandler = this::onRefreshPathEvent;

		m_ec.initStatusSync((BiConsumer<BPEventUISyncEditor, BPFilesPanel>) BPFilesPanel::onSyncEditorOuter);

		BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_REFRESHPATHTREE, m_refreshpathhandler);
	}

	protected void initActions()
	{
		m_acts = new Action[] {};
	}

	public void setListSub(boolean flag)
	{
		m_listsub = flag;
	}

	public boolean isListSub()
	{
		return m_listsub;
	}

	protected void toggleListSub(ActionEvent e)
	{
		m_listsub = !m_listsub;
		BPTableModel<?> m = m_table.getBPTableModel();
		BPTableColumnModel cm = m_table.getBPColumnModel();
		cm.setColumnHide("Path", !m_listsub);
		List<String> cols = new ArrayList<String>();
		for (int i = 0; i < m.getColumnCount(); i++)
		{
			String colname = m.getColumnRawName(i);
			cols.add(colname);
		}
		m_table.initColumnsFromModel(cols);
		cm.applyDefaultColumnWidth(m.getTableFuncs());
		refresh();
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
		if (m_ec.syncstatus.checkSyncAndNoBlock())
		{
			int[] xy = new int[] { m_scroll.getHorizontalScrollBar().getValue(), m_scroll.getVerticalScrollBar().getValue() };
			m_ec.syncstatus.trigger(BPEventUISyncEditor.syncPosition(m_id, SYNCPOSTYPE_FILES_LIST, xy));
		}
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
		String cat = UIStd.select(cats, UIUtil.wrapBPTitles(BPActionConstCommon.TXT_SEL, BPActionConstCommon.TXT_STATISTICS, BPLocaleConstCC.METHOD), null);
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
					if (m_isonlylist)
					{
						Map<String, FileStatItem> countmap = new LinkedHashMap<String, FileStatItem>();
						List<BPResource> selress = m_table.getBPTableModel().getDatas();
						for (BPResource selres : selress)
						{
							String ext = selres.getExt();
							if (extci)
								ext = ext.toLowerCase();
							FileStatItem item = countmap.get(ext);
							long s = (selres instanceof BPResourceFile) ? ((BPResourceFile) selres).getSize() : 0;
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
					else if (fres.isDirectory() && fres.isDirectory())
					{
						Map<String, FileStatItem> countmap = new LinkedHashMap<String, FileStatItem>();
						File f = new File(fres.getFileFullName());
						FileUtil.forEachFile(f, true, (d, sf) ->
						{
							String ext = sf.isDirectory() ? BPFormatDir.EXT_DIR : FileUtil.getExt(sf.getName());
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
							String ext = chd.getExt();
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
						p.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
						List<BPXData> newdatalist = new ArrayList<BPXData>();
						for (FileStatItem item : stats)
						{
							BPXDataArray itemline = new BPXDataArray(new Object[] { item.label, item.count, item.size });
							newdatalist.add(itemline);
						}
						BPXYDataList newdata = new BPXYDataList(new Class<?>[] { String.class, Long.class, Long.class }, new String[] { BPLocaleConstCC.NAME.text(), BPLocaleConstCC.COUNT.text(), BPLocaleConstCC.SIZE.text() }, null, newdatalist,
								true);
						p.showData(ObjUtil.makeMap("_xydata", newdata), false);
						BPDialogSimple.showComponent(p, BPDialogSimple.COMMANDBAR_OK, null, UIUtil.wrapBPTitle(BPActionConstCommon.TXT_STATISTICS), SwingUtilities.getWindowAncestor(this));
					}
				}
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void onShowFilter(boolean ischain)
	{
		List<BPResource> ress = m_table.getBPTableModel().getDatas();
		if (ress == null || ress.size() == 0)
			return;
		int seltype = -1;
		{
			List<String> seltypes = Arrays.asList(
					new String[] { BPLocaleHelpers.translateByClass(BPTable.class, "Full Table"), BPLocaleHelpers.translateByClass(BPTable.class, "Selection Only"), BPLocaleHelpers.translateByClass(BPFilesPanel.class, "Full in Current Path") });
			String seltypestr = UIStd.select(seltypes, UIUtil.wrapBPTitles(BPActionConstCommon.TXT_SEL, BPLocaleConstCC.SOURCE), null, m_table.getSelectedData() != null ? 1 : 0);
			if (seltypestr != null)
				seltype = seltypes.indexOf(seltypestr);
		}
		if (seltype < 0)
			return;
		boolean islistsub = m_listsub;
		switch (seltype)
		{
			case 0:
				break;
			case 1:
				ress = m_table.getSelectedDatas();
				break;
			case 2:
				ress = new ArrayList<BPResource>();
				listResourceFS(m_tablefuncs.getBaseResource(), ress, true);
				islistsub = true;
				break;
		}
		List<BPTransformerRuleFilter<BPResource>> filters = new ArrayList<BPTransformerRuleFilter<BPResource>>();
		BPResource res0 = ress.get(0);
		List<BPTransformerFactory> facs = BPTransformerManager.getTransformerFacs(res0);
		for (BPTransformerFactory fac : facs)
		{
			if (fac.isRuleFilter())
				filters.add((BPTransformerRuleFilter<BPResource>) fac.createTransformer(BPTransformerFactory.TF_TOLIST));
		}
		BPFilterComponent<BPResource> fc;
		if (ischain)
		{
			BPPipedFilterDataListPanel<BPResource> p = new BPPipedFilterDataListPanel<>();
			p.setup(filters, null, ress, null);
			p.setPreferredSize(UIUtil.getPercentDimension(0.8f, 0.8f));
			fc = p;
		}
		else
		{
			BPFilterDataListPanel<BPResource> p = new BPFilterDataListPanel<>();
			p.setup(filters, null, ress, null);
			p.setPreferredSize(UIUtil.getPercentDimension(0.8f, 0.8f));
			fc = p;
		}
		BPDialogSimple dlg = BPDialogSimple.createWithComponent((Component) fc, BPDialogSimple.COMMANDBAR_OK_CANCEL, null);
		dlg.setTitle(UIUtil.wrapBPTitles(BPLocaleConstCC.FILTER));
		dlg.pack();
		dlg.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
		dlg.setModal(true);
		dlg.setVisible(true);
		if (dlg.getActionResult() == BPDialogSimple.COMMAND_OK)
		{
			List<BPResource> seldatas = fc.getResults();
			BPFilesPanel fp = new BPFilesPanel();
			fp.setupByFileList(m_tablefuncs.getBaseResource(), seldatas, islistsub);
			CommonUIOperations.showBPComponentInNewWindow(fp);
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
			BPResource path = null;
			try
			{
				if (e.datas != null && e.datas.length > 0)
					path = (BPResource) e.datas[0];
				String subkey = e.subkey;
				if ((subkey != null && subkey.equals(m_id)) || checkSubPath(path))
				{
					refresh();
				}
			}
			catch (Exception err)
			{
				Std.err(err);
			}
		}
	}

	protected boolean checkSubPath(BPResource path)
	{
		BPResourceFileSystem base = (BPResourceFileSystem) m_tablefuncs.getBaseResource();
		String basepath = base.getFileFullName();
		String tar = ((BPResourceFileSystem) path).getFileFullName();
		if (basepath.contains(tar))
		{
			if (tar.length() == basepath.length())
				return true;
			if (tar.length() < basepath.length())
				return false;
			String c = tar.substring(basepath.length(), basepath.length() + 1);
			if (c.equals("/") || c.equals(File.separator))
				return true;
		}
		return false;
	}

	public void setupByFileList(BPResource baseres, List<BPResource> filelist, boolean islistsub)
	{
		List<BPResource> children = filelist;
		m_tablefuncs.setBaseResource(baseres);
		m_listsub = islistsub;
		initTableColumn();
		m_actrefresh.setEnabled(false);
		m_acttogglelistsub.setEnabled(false);
		m_isonlylist = true;
		initList(children);
	}

	protected void setBaseResource(BPResource res)
	{
		List<BPResource> children = new ArrayList<BPResource>();
		m_tablefuncs.setBaseResource(res);
		boolean isdir = res.isFileSystem() && ((BPResourceFileSystem) res).isDirectory();

		if (isdir)
		{
			listResourceFS(res, children, m_listsub);
		}
		else if (m_con instanceof BPDataContainerFileSystem)
		{
			m_acttogglelistsub.setEnabled(false);
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

	protected void listResourceFS(BPResource res, List<BPResource> results, boolean recursive)
	{
		BPResourceFileSystem fres = (BPResourceFileSystem) res;
		BPResource[] subfs = fres.listResources();
		if (subfs != null)
		{
			for (BPResource subf : subfs)
			{
				if (subf.isFileSystem())
				{
					BPResourceFileSystem f = (BPResourceFileSystem) subf;
					if (checkEntry(f.getName(), f.isDirectory()))
					{
						if (!f.isFile())
						{
							results.add(f);
							if (recursive && f.isDirectory())
								listResourceFS(f, results, recursive);
						}
					}
				}
			}
			for (BPResource subf : subfs)
			{
				if (subf.isFileSystem())
				{
					BPResourceFileSystem f = (BPResourceFileSystem) subf;
					if (checkEntry(f.getName(), f.isDirectory()))
					{
						if (f.isFile())
							results.add(f);
					}
				}
			}
		}
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
		m_ec.clearResource();
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
		m_ec.setChannelID(channelid);
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
				m_ec.eventcontroller.dispatchEvent(BPEditorEvent.ACT_OPEN, ress.get(0));
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

	protected final static void onSyncEditorOuter(BPEventUISyncEditor e, BPFilesPanel editor)
	{
		editor.onSyncEditor(e);
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
					m_ec.syncstatus.blockSync(() ->
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
		if (e.getValueIsAdjusting())
		{
			if (m_ec.syncstatus.checkSyncAndNoBlock())
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
					m_ec.syncstatus.trigger(BPEventUISyncEditor.syncSelection(m_id, SYNCSELSTYPE_FILES, resstrs));
				}
				else
				{
					for (int i = 0; i < ress.size(); i++)
					{
						resstrs[i] = ress.get(i).getName();
					}
					m_ec.syncstatus.trigger(BPEventUISyncEditor.syncSelection(m_id, SYNCSELSTYPE_FILES, resstrs));
				}
			}
			if (m_table.getSelectedData() != null)
				m_ec.eventcontroller.dispatchEvent(BPEditorEvent.ACT_SELECT, m_table.getSelectedData());
		}
	}

	public BPEditorController getEditorController()
	{
		return m_ec;
	}
}