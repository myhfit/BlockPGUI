package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableRowSorter;

import bp.compare.BPDataComparator;
import bp.compare.BPDataComparatorXY;
import bp.compare.BPDataComparatorXY.BPDataCompareResultXY;
import bp.config.UIConfigs;
import bp.data.BPDataContainer;
import bp.data.BPXData;
import bp.data.BPXYContainer;
import bp.data.BPXYDContainer;
import bp.data.BPXYDData;
import bp.data.BPXYDDataBase;
import bp.data.BPXYData;
import bp.data.BPXYData.BPXYDataList;
import bp.data.BPXYHolder;
import bp.res.BPResource;
import bp.ui.BPViewer;
import bp.ui.actions.BPActionHolder;
import bp.ui.actions.BPXYDEditorActions;
import bp.ui.actions.BPXYDataCloneActions;
import bp.ui.compare.BPComparableGUI;
import bp.ui.container.BPToolBarSQ;
import bp.ui.editor.controller.BPEditorController;
import bp.ui.parallel.BPEventUISyncEditor;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPRowFilter;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.scomp.BPTextField;
import bp.ui.table.BPTableFuncsXY;
import bp.ui.util.UIUtil;
import bp.util.Std;

public class BPXYDEditor<CON extends BPXYContainer> extends JPanel implements BPEditor<JPanel>, BPViewer<CON>, BPComparableGUI<BPXYData, BPDataCompareResultXY>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9212809561160774333L;

	public final static String SYNCPOSTYPE_XY = "XY";
	public final static String SYNCSELSTYPE_XY = "XY";

	protected String m_id;
	protected int m_channelid;
	protected CON m_con;
	protected BiConsumer<List<BPXData>, Integer> m_adddatafunc;
	protected Consumer<BPXYDData> m_setupqueryfunc;
	protected WeakReference<Consumer<String>> m_dynainfo;

	protected JScrollPane m_scroll;
	protected BPTableFuncsXY m_funcs;
	protected BPTableModel<BPXData> m_model;
	protected BPTable<BPXData> m_table;
	protected BPToolBarSQ m_toolbar;
	protected BPTextField m_txtfilter;

	protected BPActionHolder m_acts;

	protected BPEditorController m_ec;

	protected WeakReference<BiConsumer<String, Boolean>> m_statehandler;

	public BPXYDEditor()
	{
		m_ec = new BPEditorController(this);
		m_ec.setNeedSaveEditable(true);
		m_adddatafunc = this::onAddData;
		m_setupqueryfunc = this::onSetupXY;
		m_ec.initStatusSync((BiConsumer<BPEventUISyncEditor,BPXYDEditor<?>>)BPXYDEditor::onSyncEditorOuter);
		init();
	}

	protected BPActionHolder createActionHolder()
	{
		return new BPXYDEditorActions(this);
	}

	protected void init()
	{
		m_acts = createActionHolder();
		m_scroll = new JScrollPane();
		m_table = new BPTable<BPXData>();
		m_toolbar = new BPToolBarSQ();
		m_txtfilter = new BPTextField();
		JPanel toppnl = new JPanel();

		m_toolbar.setBorder(new EmptyBorder(1, 1, 2, 1));
		m_toolbar.setActions(m_acts.getActions());
		m_txtfilter.setMonoFont();
		m_txtfilter.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(150, 8)));
		m_scroll.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_WEAKBORDER()));
		m_txtfilter.setBorder(new CompoundBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_WEAKBORDER()), new EmptyBorder(0, 2, 0, 2)));
		m_scroll.setViewportView(m_table);
		m_scroll.getHorizontalScrollBar().addAdjustmentListener(this::onScroll);
		m_scroll.getVerticalScrollBar().addAdjustmentListener(this::onScroll);
		m_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		m_table.setMonoFont();
		m_table.setCommonRenderAlign();
		m_table.getSelectionModel().addListSelectionListener(this::onSelectionChanged);
		m_table.installRowHeader(m_scroll);

		setLayout(new BorderLayout());
		toppnl.setLayout(new BorderLayout());
		toppnl.add(m_toolbar, BorderLayout.CENTER);
		toppnl.add(m_txtfilter, BorderLayout.EAST);
		add(m_scroll, BorderLayout.CENTER);
		add(toppnl, BorderLayout.NORTH);

		initListeners();
	}

	protected void initListeners()
	{
		m_txtfilter.getDocument().addDocumentListener(new UIUtil.BPDocumentChangedHandler(this::onFilterChanged));
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.TABLE;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public BPDataContainer createDataContainer(BPResource res)
	{
		return null;
	}

	@SuppressWarnings("unchecked")
	public void bind(CON con, boolean noread)
	{
		m_con = con;
		if (con == null)
		{
			BPXYHolder holder = new BPXYHolder();
			holder.setStructureEditable(true);
			holder.setTitle("tempxy");
			BPXYDataList xydata = new BPXYDataList(new Class[] { String.class }, new String[] { "column1" }, null, null, true);
			holder.setData(xydata);
			m_con = (CON) holder;
			holder.open();
			holder.readXYDataAsync().whenComplete(this::onCompleteXY);
		}
		else if (!noread && con.canOpen())
		{
			con.open();
			if (con instanceof BPXYDContainer)
				((BPXYDContainer) con).readXYDDataAsync(m_setupqueryfunc).whenComplete(this::onComplete);
			else
				con.readXYDataAsync().whenComplete(this::onCompleteXY);
		}
	}

	public void unbind()
	{
		CON con = m_con;
		m_con = null;
		if (con != null)
			con.close();
	}

	public CON getDataContainer()
	{
		return m_con;
	}

	public void focusEditor()
	{
	}

	public String getEditorInfo()
	{
		return null;
	}

	protected BPXYDData createSaveData(String[] colnames, Class<?>[] colclasses, String[] collabels, List<BPXData> datas)
	{
		BPXYDDataBase xydata = new BPXYDDataBase();
		xydata.setColumnNames(colnames);
		xydata.setColumnClasses(colclasses);
		xydata.setColumnLabels(collabels);
		xydata.setDatas(datas);
		return xydata;
	}

	protected BPXYDData createCloneData(String[] colnames, Class<?>[] colclasses, String[] collabels, List<BPXData> datas)
	{
		BPXYDDataBase xydata = new BPXYDDataBase();
		xydata.setColumnNames(colnames);
		xydata.setColumnClasses(colclasses);
		xydata.setColumnLabels(collabels);
		xydata.setDatas(datas);
		return xydata;
	}

	public void save()
	{
		m_con.open();
		try
		{
			BPXYDData xydata = createSaveData(m_funcs.getColumnNames(), m_funcs.getColumnClasses(), m_funcs.getColumnLabels(), m_model.getDatas());
			if (m_con instanceof BPXYDContainer)
				((BPXYDContainer) m_con).writeXYDData(xydata);
			else
				m_con.writeXYData(xydata);
			setSaved();
			m_ec.setNeedSave(false);
		}
		finally
		{
			m_con.close();
		}
	}

	protected void setSaved()
	{
		WeakReference<BiConsumer<String, Boolean>> statehandlerref = m_statehandler;
		if (statehandlerref != null)
		{
			BiConsumer<String, Boolean> statehandler = statehandlerref.get();
			if (statehandler != null)
			{
				statehandler.accept(m_id, false);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void onFilterChanged(DocumentEvent e)
	{
		String txt = m_txtfilter.getText();
		TableRowSorter<BPTableModel<BPXData>> sorter = m_table.initRowSorter();
		BPRowFilter<BPXData> filter = (BPRowFilter<BPXData>) (RowFilter<?, ?>) sorter.getRowFilter();
		if (filter == null)
		{
			filter = new BPTable.BPRowFilter<BPXData>(txt);
			sorter.setRowFilter(filter);
		}
		else
		{
			if (filter.setFilterText(txt))
				sorter.sort();
		}
	}

	public void reloadData()
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
		m_ec.setChannelID(channelid);
	}

	public int getChannelID()
	{
		return m_channelid;
	}

	public void setOnDynamicInfo(Consumer<String> info)
	{
		m_dynainfo = new WeakReference<Consumer<String>>(info);
	}

	protected void onCompleteXY(BPXYData data, Throwable e)
	{
		setXYData(new BPXYData.BPXYDataList(data, true));
	}

	protected void onComplete(BPXYDData data, Throwable e)
	{
		UIUtil.laterUI(() ->
		{
			WeakReference<BiConsumer<String, Boolean>> ref = m_statehandler;
			if (ref != null)
			{
				BiConsumer<String, Boolean> handler = ref.get();
				if (handler != null && m_id != null)
				{
					m_model.setID(m_id);
					m_model.setOnStateChanged(handler);
				}
			}
		});
		try
		{
			data.close();
		}
		catch (IOException e2)
		{
			Std.err(e2);
		}
	}

	protected void onSetupXY(BPXYDData data)
	{
		data.setDataListener(new WeakReference<BiConsumer<List<BPXData>, Integer>>(m_adddatafunc), null, null);
		setXYData(new BPXYData.BPXYDataList(data, true));
	}

	protected void onAddData(List<BPXData> datas, Integer pos)
	{
		UIUtil.laterUI(() ->
		{
			if (pos == null)
			{
				addXDatas(datas);
			}
		});
	}

	public void setXYData(BPXYData data)
	{
		m_funcs = new BPTableFuncsXY(data);
		m_funcs.setStructureEditable(true);
		m_model = new BPTableModel<BPXData>(m_funcs);
		m_model.setDatas(data.getDatas());
		m_table.setModel(m_model);
		m_table.initRowSorter();
		m_table.setColumnWidthBatch(UIUtil.scale(180), UIUtil.scale(60));

		postInitTable();
	}

	protected void postInitTable()
	{
	}

	public void addXDatas(List<BPXData> datas)
	{
		m_model.addAll(datas);
		m_model.fireTableDataChanged();
	}

	public void newLine(ActionEvent e)
	{
		int c = m_funcs.getColumnClasses().length;
		BPXData newline = new BPXData.BPXDataArray(new Object[c]);
		m_model.add(newline);
		m_model.fireTableDataChanged();
		int r = m_model.getRowCount() - 1;
		m_table.setRowSelectionInterval(r, r);
		m_table.scrollTo(m_model.getRowCount() - 1, 0);
	}

	public void delete(ActionEvent e)
	{
		int[] rs = m_table.getSelectedModelRows();
		if (rs != null && rs.length > 0)
		{
			m_model.delete(rs);
			m_model.fireTableDataChanged();
		}
	}

	public BPDataComparator<BPXYData, BPDataCompareResultXY> getComparator()
	{
		return new BPDataComparatorXY();
	}

	public BPXYData getCompareData()
	{
		return m_funcs.getRawData();
	}

	public void showClone(ActionEvent e)
	{
		BPXYDData xydata = createCloneData(m_funcs.getColumnNames(), m_funcs.getColumnClasses(), m_funcs.getColumnLabels(), m_model.getDatas());
		Action[] acts = BPXYDataCloneActions.getActions(xydata, null);
		if (acts != null && acts.length > 0)
		{
			JPopupMenu pop = new JPopupMenu();
			JComponent[] comps = UIUtil.makeMenuItems(acts);
			for (int i = 0; i < comps.length; i++)
				pop.add(comps[i]);
			JComponent source = (JComponent) e.getSource();
			pop.show(source.getParent(), source.getX(), source.getY() + source.getHeight());
		}
	}

	public void setOnStateChanged(BiConsumer<String, Boolean> handler)
	{
		m_statehandler = new WeakReference<BiConsumer<String, Boolean>>(handler);
	}

	public void clearResource()
	{
		m_ec.stopSync();
		if (m_con != null)
			unbind();
		if (m_model != null)
		{
			m_table.clearResource();
			m_funcs.clear();
			m_scroll.setViewportView(null);
			m_scroll.setRowHeader(null);
			m_table.setTableHeader(null);
			m_table.setColumnModel(new DefaultTableColumnModel());
			m_model = null;
		}
		removeAll();
	}

	protected void onScroll(AdjustmentEvent e)
	{
		if (m_ec.syncstatus.checkSyncAndNoBlock())
		{
			int[] xy = new int[] { m_scroll.getHorizontalScrollBar().getValue(), m_scroll.getVerticalScrollBar().getValue() };
			m_ec.syncstatus.trigger(BPEventUISyncEditor.syncPosition(m_id, SYNCPOSTYPE_XY, xy));
		}
	}

	protected void onSelectionChanged(ListSelectionEvent e)
	{
		if (m_ec.syncstatus.checkSyncAndNoBlock())
			m_ec.syncstatus.trigger(BPEventUISyncEditor.syncSelection(m_id, SYNCSELSTYPE_XY, m_table.converRowIndecies(m_table.getSelectedRows(), false)));
	}
	
	protected final static void onSyncEditorOuter(BPEventUISyncEditor e,BPXYDEditor<?> editor)
	{
		editor.onSyncEditor(e);
	}

	protected void onSyncEditor(BPEventUISyncEditor e)
	{
		if (BPEventUISyncEditor.SYNC_POS.equals(e.subkey))
		{
			if (SYNCPOSTYPE_XY.equals(e.getSyncDataType()))
			{
				String id = (String) e.datas[0];
				if (!m_id.equals(id))
				{
					int[] xy = e.getSyncData();
					m_ec.syncstatus.blockSync(() ->
					{
						m_scroll.getHorizontalScrollBar().setValue(xy[0]);
						m_scroll.getVerticalScrollBar().setValue(xy[1]);
					});
				}
			}
		}
		else if (BPEventUISyncEditor.SYNC_SELECTION.equals(e.subkey))
		{
			if (SYNCSELSTYPE_XY.equals(e.getSyncDataType()))
			{
				String id = (String) e.datas[0];
				if (!m_id.equals(id))
				{
					int[] sels = e.getSyncData();
					if (sels.length > 0)
					{
						m_ec.syncstatus.blockSync(() ->
						{
							m_table.setSelectionRows(sels);
							m_table.scrollRectToVisible(m_table.getCellRect(sels[0], 0, true));
						});
					}
				}
			}
		}
	}

	public BPEditorController getEditorController()
	{
		return m_ec;
	}
}
