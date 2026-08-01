package bp.ui.scomp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SortOrder;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;

import bp.config.UIConfigs;
import bp.data.BPXData;
import bp.data.BPXYData;
import bp.data.BPXYData.BPXYDataList;
import bp.locale.BPLocaleConstCC;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.actions.BPDataActionFactory.BPDataActionFactoryCommon;
import bp.ui.dialog.BPDialogFind;
import bp.ui.dialog.BPDialogFind.BPFindPs;
import bp.ui.dialog.BPDialogFind.BPReplacePs;
import bp.ui.table.BPTableFuncs;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.LogicUtil.WeakRefGo;
import bp.util.NumberUtil;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public class BPTable<T> extends JTable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7010566894709794989L;
	protected BPTableFuncs<T> m_tablefuncs;
	protected WeakRefGo<BPDialogFind> m_finddlgref;
	protected Function<BPFindPs, Boolean> m_findcb;
	protected boolean m_celleditorreadonly;

	public BPTable()
	{
		setRowHeight(UIConfigs.TABLE_ROWHEIGHT());
		putClientProperty("JTable.autoStartsEdit", false);
		m_finddlgref = new WeakRefGo<BPDialogFind>();
		initListener();
	}

	public BPTable(BPTableFuncs<T> tablefuncs)
	{
		super(new BPTableModel<T>(tablefuncs));
		setRowHeight(UIConfigs.TABLE_ROWHEIGHT());
		putClientProperty("JTable.autoStartsEdit", false);
		m_finddlgref = new WeakRefGo<BPDialogFind>();
		initListener();
		m_tablefuncs = tablefuncs;
	}

	@SuppressWarnings("unchecked")
	public TableRowSorter<BPTableModel<T>> initRowSorter()
	{
		TableRowSorter<BPTableModel<T>> sorter = (TableRowSorter<BPTableModel<T>>) getRowSorter();
		if (sorter == null)
		{
			sorter = new BPTableRowSorter<BPTableModel<T>>(getBPTableModel());
			setRowSorter(sorter);
		}
		else
		{
			BPTableModel<T> model = getBPTableModel();
			if (sorter.getModel() != model)
				sorter.setModel(model);
		}
		return sorter;
	}

	protected TableColumnModel createDefaultColumnModel()
	{
		return new BPTableColumnModel();
	}

	protected void initListener()
	{
		m_findcb = this::onFindCall;
		addMouseListener(new UIUtil.BPMouseListener(null, this::onMouse, this::onMouse, null, null));
		addKeyListener(new UIUtil.BPKeyListenerForPopup(this::onContextMenuKey));
		initTableHeaderContextMenu();
		setupInnerOPs();
	}

	public void initTableHeaderContextMenu()
	{
		getTableHeader().addMouseListener(new UIUtil.BPMouseListener(this::onHeaderMouse, this::onHeaderMouse, this::onHeaderMouse, null, null));
	}

	@SuppressWarnings("unchecked")
	public void setModel(TableModel model)
	{
		super.setModel(model);
		if (model instanceof BPTableModel)
		{
			m_tablefuncs = ((BPTableModel<T>) model).getTableFuncs();
		}
	}

	@SuppressWarnings("unchecked")
	protected void createDefaultRenderers()
	{
		super.createDefaultRenderers();
		defaultRenderersByColumnClass.put(Date.class, (UIDefaults.LazyValue) t -> new BPTableRendererDateTime());
	}

	public void setMonoFont()
	{
		Font tfont = new Font(UIConfigs.MONO_FONT_NAME(), Font.PLAIN, UIConfigs.TABLEFONT_SIZE() + UIConfigs.MONO_FONT_SIZEDELTA());
		setFont(tfont);
		getTableHeader().setFont(tfont);
		for (Object key : defaultEditorsByColumnClass.keySet())
			((DefaultCellEditor) defaultEditorsByColumnClass.get(key)).getComponent().setFont(tfont);
	}

	public void setTableFont()
	{
		Font tfont = new Font(UIConfigs.TABLE_FONT_NAME(), Font.PLAIN, UIConfigs.TABLEFONT_SIZE());
		setFont(tfont);
		getTableHeader().setFont(tfont);
		for (Object key : defaultEditorsByColumnClass.keySet())
			((DefaultCellEditor) defaultEditorsByColumnClass.get(key)).getComponent().setFont(tfont);
	}

	public void setCommonRenderAlign()
	{
		((DefaultTableCellRenderer) getDefaultRenderer(Float.class)).setHorizontalAlignment(JLabel.LEFT);
		((DefaultTableCellRenderer) getDefaultRenderer(Double.class)).setHorizontalAlignment(JLabel.LEFT);
		((DefaultTableCellRenderer) getDefaultRenderer(Long.class)).setHorizontalAlignment(JLabel.LEFT);
		((DefaultTableCellRenderer) getDefaultRenderer(Short.class)).setHorizontalAlignment(JLabel.LEFT);
		((DefaultTableCellRenderer) getDefaultRenderer(BigDecimal.class)).setHorizontalAlignment(JLabel.LEFT);
	}

	public List<T> getSelectedDatas()
	{
		List<T> rc = new ArrayList<T>();
		int[] rows = getSelectedRows();
		BPTableModel<T> model = getBPTableModel();
		if (rows != null)
		{
			for (int i = 0; i < rows.length; i++)
				rc.add(model.getRow(convertRowIndexToModel(rows[i])));
		}
		return rc;
	}

	public T getSelectedData()
	{
		T rc = null;
		int row = getSelectedRow();
		BPTableModel<T> model = getBPTableModel();
		if (row > -1)
			rc = model.getRow(convertRowIndexToModel(row));
		return rc;
	}

	public void setCellEditorReadonly(boolean flag)
	{
		m_celleditorreadonly = flag;
	}

	public Component prepareEditor(TableCellEditor editor, int row, int column)
	{
		Component rc = super.prepareEditor(editor, row, column);
		if (m_celleditorreadonly)
		{
			if (rc instanceof JTextComponent)
			{
				((JTextComponent) rc).setEditable(false);
			}
		}
		return rc;
	}

	public List<T> getDatasFromRows(int[] rows)
	{
		List<T> rc = null;
		if (rows != null)
		{
			rc = new ArrayList<T>(rows.length);
			BPTableModel<T> model = getBPTableModel();
			for (int i = 0; i < rows.length; i++)
				rc.add(model.getRow(rows[i]));
		}
		return rc;
	}

	public void refreshData()
	{
		((BPTableModel<?>) getModel()).fireTableDataChanged();
	}

	public void setSelectionRows(int[] sels)
	{
		clearSelection();
		int l = getModel().getRowCount();
		if (sels != null)
		{
			for (int i = 0; i < sels.length; i++)
			{
				int p = convertRowIndexToView(sels[i]);
				if (p >= l || p < 0)
					continue;
				if (i == 0)
				{
					setRowSelectionInterval(p, p);
				}
				else
				{
					addRowSelectionInterval(p, p);
				}
			}
		}
	}

	public int[] converRowIndecies(int[] sels, boolean toview)
	{
		int[] rc = new int[sels.length];
		for (int i = 0; i < sels.length; i++)
			rc[i] = toview ? convertRowIndexToView(sels[i]) : convertRowIndexToModel(sels[i]);
		return rc;
	}

	public int getSelectedModelColumn()
	{
		int rc = getSelectedColumn();
		if (rc > -1)
			return convertColumnIndexToModel(rc);
		return -1;
	}

	public int getSelectedModelRow()
	{
		int rc = getSelectedRow();
		if (rc > -1)
			return convertRowIndexToModel(rc);
		return -1;
	}

	public int[] getSelectedModelRows()
	{
		int[] rs = getSelectedRows();
		int[] rawrs = null;
		if (rs == null || rs.length > 0)
		{
			rawrs = new int[rs.length];
			for (int i = 0; i < rs.length; i++)
			{
				rawrs[i] = convertRowIndexToModel(rs[i]);
			}
		}
		return rawrs;
	}

	@SuppressWarnings("unchecked")
	public BPTableModel<T> getBPTableModel()
	{
		return (BPTableModel<T>) getModel();
	}

	public BPTableColumnModel getBPColumnModel()
	{
		return (BPTableColumnModel) getColumnModel();
	}

	@SuppressWarnings("unchecked")
	public BPTableModel<T> tryGetBPTableModel()
	{
		TableModel model = getModel();
		if (model instanceof BPTableModel)
			return (BPTableModel<T>) model;
		return null;
	}

	public void scrollTo(int row, int col)
	{
		scrollRectToVisible(getCellRect(row, col, true));
	}

	protected void trySelect(MouseEvent e)
	{
		int r = rowAtPoint(e.getPoint());
		int nr = convertRowIndexToModel(r);
		int[] rows = getSelectedModelRows();
		boolean flag = true;
		if (rows != null)
		{
			for (int i = 0; i < rows.length; i++)
			{
				if (rows[i] == nr)
				{
					flag = false;
					break;
				}
			}
		}
		if (flag)
		{
			int cdm = InputEvent.CTRL_DOWN_MASK;
			if ((e.getModifiersEx() & cdm) == cdm)
			{
				getSelectionModel().addSelectionInterval(r, r);
			}
			else
			{
				clearSelection();
				setRowSelectionInterval(r, r);
			}
		}
	}

	protected void onHeaderMouse(MouseEvent e)
	{
		if (e.isPopupTrigger())
		{
			JTableHeader header = getTableHeader();
			List<Action> acts = makeTableHeaderActions(header);
			if (acts != null && acts.size() > 0)
			{
				JPopupMenu pop = new JPopupMenu();
				JComponent[] comps = UIUtil.makeMenuItems(acts.toArray(new Action[acts.size()]));
				for (int i = 0; i < comps.length; i++)
					pop.add(comps[i]);
				pop.show(header, e.getX(), e.getY());
			}
		}
	}

	protected List<Action> makeTableHeaderActions(JTableHeader header)
	{
		List<Action> rc = new ArrayList<Action>();
		Action actfind = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUFINDREPLACE, this::onFind);
		Action actfilter = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUTABLECFILTER, this::onFilter);
		Action actgoto = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUGOTO, this::onGoto);
		Action actcloneraw = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUCLONERAW, this::onCloneEditor);
		Action acttogglelinenum = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUTOGGLELINENUM, this::onToggleLineNum);
		// Action actcloneseen =
		// BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUCLONESEEN,
		// this::onCloneSeenEditor);
		rc.add(actfind);
		rc.add(actfilter);
		rc.add(actgoto);
		rc.add(acttogglelinenum);
		rc.add(actcloneraw);
		// rc.add(actcloneseen);
		return rc;
	}

	protected void onContextMenuKey(KeyEvent e)
	{
		BPTableFuncs<T> funcs = m_tablefuncs;
		if (funcs != null)
		{
			int sr = getSelectedRow();
			int sc = getSelectedColumn();
			int[] rows = getSelectedModelRows();
			List<Action> acts = null;
			if (rows != null && rows.length > 0)
			{
				List<T> datas = getDatasFromRows(rows);
				acts = funcs.getActions(this, datas, rows, sr, sc);
			}
			else
			{
				acts = funcs.getEmptySelectionActions(this);
			}
			if (acts != null && acts.size() > 0)
			{
				JComponent[] items = UIUtil.makeMenuItems(acts.toArray(new Action[acts.size()]));
				JPopupMenu pop = new JPopupMenu();
				for (int i = 0; i < items.length; i++)
					pop.add(items[i]);
				Rectangle rect = super.getCellRect(sr, sc, true);
				pop.show(this, rect.x, rect.y + rect.height);
			}
		}
	}

	protected void onMouse(MouseEvent e)
	{
		int b = e.getButton();
		if (e.getID() == MouseEvent.MOUSE_PRESSED)
		{
			if (b == MouseEvent.BUTTON3)
			{
				trySelect(e);
			}
			else if (b == MouseEvent.BUTTON1 && e.getClickCount() == 2)
			{
				BPTableFuncs<T> funcs = m_tablefuncs;
				if (funcs != null)
				{
					int row = getSelectedModelRow();
					int col = getSelectedModelColumn();
					Action act = null;
					if (row != -1)
					{
						act = funcs.getOpenAction(this, getBPTableModel().getRow(row), row, col);
						if (act != null)
							act.actionPerformed(null);
					}
				}
			}
		}
		if (e.isPopupTrigger())
		{
			BPTableFuncs<T> funcs = m_tablefuncs;
			if (funcs != null)
			{
				Point pt = e.getPoint();
				int sr = rowAtPoint(pt);
				int sc = columnAtPoint(pt);
				int[] rows = getSelectedModelRows();
				List<Action> acts = null;
				if (rows != null && rows.length > 0)
				{
					acts = funcs.getActions(this, getDatasFromRows(rows), rows, sr, sc);
				}
				else
				{
					acts = funcs.getEmptySelectionActions(this);
				}
				if (acts != null && acts.size() > 0)
				{
					JComponent[] items = UIUtil.makeMenuItems(acts.toArray(new Action[acts.size()]));
					JPopupMenu pop = new JPopupMenu();
					for (int i = 0; i < items.length; i++)
						pop.add(items[i]);
					pop.show(this, e.getX(), e.getY());
				}
			}
		}
	}

	protected void setupInnerOPs()
	{
		InputMap im = getInputMap();
		im.put(BPActionConstCommon.CMPOP_FIND.accKey(), "find");
		im.put(BPActionConstCommon.CMPOP_GOTO.accKey(), "goto");
		im.put(BPActionConstCommon.CMPOP_FILTER.accKey(), "filter");

		ActionMap am = getActionMap();
		am.put("find", BPAction.build("find").callback(this::onFind).getAction());
		am.put("goto", BPAction.build("goto").callback(this::onGoto).getAction());
		am.put("filter", BPAction.build("filter").callback(this::onFilter).getAction());
	}

	@SuppressWarnings("unchecked")
	public void onFilter(ActionEvent e)
	{
		TableRowSorter<BPTableModel<T>> sorter = initRowSorter();
		BPRowFilter<T> filter = (BPRowFilter<T>) (RowFilter<?, ?>) sorter.getRowFilter();
		String cur = null;
		if (filter != null)
		{
			cur = filter.getFilterText();
		}
		String fstr = UIStd.input(cur == null ? "" : cur, BPLocaleConstCC.FILTER.text() + ":", null);
		if (fstr != null)
		{
			if (fstr != null && fstr.length() == 0)
				fstr = null;
			if (filter == null)
			{
				filter = new BPRowFilter<T>(fstr);
				sorter.setRowFilter(filter);
			}
			else
			{
				filter.setFilterText(fstr);
				sorter.sort();
			}
		}
	}

	public void onToggleLineNum(ActionEvent e)
	{
		BPTableModel<T> m = getBPTableModel();
		if (m != null)
		{
			boolean f = !m.isShowLineNum();
			m.setShowLineNum(f);
			Map<String, Integer> cws = getColumnsWidth();
			m.fireTableStructureChanged();
			setColumnsWidth(cws);
		}
	}

	protected Map<String, Integer> getColumnsWidth()
	{
		Map<String, Integer> rc = new HashMap<String, Integer>();
		TableColumnModel tcm = getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			TableColumn tc = tcm.getColumn(i);
			if (tc instanceof BPTableColumn)
				rc.put(((BPTableColumn) tc).getColumnName(), tc.getPreferredWidth());
		}
		return rc;
	}

	public void setColumnsWidth(Map<String, Integer> cws)
	{
		TableColumnModel tcm = getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			TableColumn tc = tcm.getColumn(i);
			if (tc instanceof BPTableColumn)
			{
				BPTableColumn btc = (BPTableColumn) tc;
				Integer w = cws.get(btc.getColumnName());
				if (w != null)
					tc.setPreferredWidth(w);
				else if (btc.isLineNumber())
					tc.setMinWidth(UIUtil.scale(60));
			}
		}
	}

	public void onCloneEditor(ActionEvent e)
	{
		BPDataActionFactoryCommon.cloneXYDataToNewEditor(cloneXYData(true), e);
	}

	public void onCloneSeenEditor(ActionEvent e)
	{
		BPDataActionFactoryCommon.cloneXYDataToNewEditor(cloneXYData(false), e);
	}

	public BPXYData cloneXYData(boolean raw)
	{
		BPXYDataList rc = null;
		BPTableModel<T> m = getBPTableModel();
		if (m != null)
		{
			BPTableFuncs<T> f = m.getTableFuncs();
			if (f != null)
			{
				String[] colnames = f.getColumnNames();
				int c = colnames.length;
				String[] collabels = new String[c];
				for (int i = 0; i < c; i++)
					collabels[i] = f.getColumnLabel(i);
				Class<?>[] colcls = f.getColumnClasses();
				List<T> datas = m.getDatas();
				rc = new BPXYDataList(colcls, colnames, collabels, null, true);
				for (int i = 0; i < datas.size(); i++)
				{
					T data = datas.get(i);
					Object[] arr = new Object[c];
					for (int j = 0; j < c; j++)
					{
						arr[j] = f.getValue(data, i, j);
					}
					rc.add(new BPXData.BPXDataArray(arr));
				}
			}
		}
		return rc;
	}

	public void onGoto(ActionEvent e)
	{
		String linestr = UIStd.input("", BPActionConstCommon.TXT_GOTOLINE.text() + ":", null);
		Integer line = ObjUtil.toInt(linestr, null);
		if (line != null && line > -1)
			scrollTo(line - 1, 0);
	}

	public void onFind(ActionEvent e)
	{
		BPDialogFind dlg = m_finddlgref.get();
		if (dlg != null)
			dlg.dispose();
		dlg = new BPDialogFind(this);
		dlg.setFindCallBack(m_findcb);
		m_finddlgref.setTarget(dlg);
		dlg.setVisible(true);
	}

	public void clearResource()
	{
		BPDialogFind dlg = m_finddlgref.get();
		if (dlg != null)
			dlg.dispose();
		BPTableModel<T> model = tryGetBPTableModel();
		if (model != null)
			model.setDatas(new ArrayList<T>());
	}

	public String getToolTipText(MouseEvent e)
	{
		BPTableFuncs<T> funcs = m_tablefuncs;
		if (funcs == null || !funcs.allowTooltip())
			return super.getToolTipText(e);

		Point p = e.getPoint();

		int ci = columnAtPoint(p);
		int ri = rowAtPoint(p);

		if ((ci != -1) && (ri != -1))
			return funcs.getTooltip(getBPTableModel().getRow(ri), ri, ci);

		return getToolTipText();
	}

	protected boolean onFindCall(BPFindPs ps)
	{
		if (!ps.isReplace())
			return find(ps, false);
		else
			return replace((BPReplacePs) ps);
	}

	public boolean replace(BPReplacePs rps)
	{
		if (rps.isreplaceall)
		{
			List<int[]> r = new ArrayList<int[]>();
			findToResult(rps, true, getSelectedRow(), getRowCount(), r);
			if (r.size() > 0)
			{
				ListSelectionModel selmodel = getSelectionModel();
				selmodel.clearSelection();
				for (int[] rc : r)
				{
					replaceData(rc[0], rc[1], rps.src, rps.replacestr, rps.iswholeword, rps.iscasesensitive);
					selmodel.addSelectionInterval(rc[0], rc[0]);
				}
				int[] rc = r.get(0);
				scrollTo(rc[0], rc[1]);
				return true;
			}
		}
		else
		{
			List<int[]> r = new ArrayList<int[]>();
			findToResult(rps, false, getSelectedRow(), getRowCount(), r);
			if (r.size() > 0)
			{
				int[] rc = r.get(0);
				replaceData(rc[0], rc[1], rps.src, rps.replacestr, rps.iswholeword, rps.iscasesensitive);
				selectAndLocate(rc[0], rc[1]);
				return true;
			}
		}
		return false;
	}

	public void replaceData(int r, int c, String src, String dest, boolean iswhole, boolean iscasesensitive)
	{
		BPTableModel<T> model = getBPTableModel();
		int r2 = convertRowIndexToModel(r);
		Object obj = model.getValueAt(r2, c);
		Object newobj = replaceCellData(obj, model.getColumnClass(c), src, dest, iswhole, iscasesensitive);
		if (newobj != null)
			model.setValueAt(newobj, r2, c);
	}

	protected Object replaceCellData(Object obj, Class<?> rcls, String src, String dest, boolean iswhole, boolean iscasesensitive)
	{
		if (obj instanceof String)
			return TextUtil.replaceText(((String) obj), src, dest, iswhole, iscasesensitive);
		else if (obj instanceof Number)
		{
			String numstr = ObjUtil.toString(obj);
			return numstr.replace(src, dest);
		}
		return null;
	}

	protected void selectAndLocate(int r, int c)
	{
		ListSelectionModel selmodel = getSelectionModel();
		selmodel.clearSelection();
		selmodel.setSelectionInterval(r, r);
		scrollTo(r, c);
	}

	protected void findToResult(BPFindPs ps, boolean findall, int startrow, int startcol, List<int[]> results)
	{
		String target = ps.src;
		boolean isforward = ps.isforward;
		boolean wholeword = ps.iswholeword;
		boolean casesensitive = ps.iscasesensitive;
		int si = startrow;
		int delta = isforward ? 1 : -1;
		int i = si + delta;
		int c = startcol;
		if (isforward)
		{
			if (i >= c)
				i = 0;
			if (i < 0)
				i = 0;
		}
		else
		{
			if (i < 0)
				i = c - 1;
			if (i < 0)
				i = 0;
		}
		if (i >= c)
			return;
		BPTableModel<T> model = getBPTableModel();
		int techc = 0;
		boolean showlinenum = model.isShowLineNum();
		for (; i != si; i += delta)
		{
			if (techc >= c)
				break;
			if (isforward)
			{
				if (i >= c)
					i = 0;
				if (i < 0)
					i = 0;
			}
			else
			{
				if (i < 0)
					i = c - 1;
				if (i < 0)
					i = 0;
			}
			int c2 = model.getColumnCount();
			int r = convertRowIndexToModel(i);
			for (int j = showlinenum ? 1 : 0; j < c2; j++)
			{
				String t = ObjUtil.toString(model.getValueAt(r, j));
				if (TextUtil.containsText(t, target, wholeword, !casesensitive))
				{
					results.add(new int[] { i, j });
					if (!findall)
						return;
				}
			}
			techc++;
		}
	}

	public boolean find(BPFindPs ps, boolean findall)
	{
		String target = ps.src;
		boolean isforward = ps.isforward;
		boolean wholeword = ps.iswholeword;
		boolean casesensitive = ps.iscasesensitive;
		// boolean onlysel = ps.onlyselection;
		int si = getSelectedRow();
		int delta = isforward ? 1 : -1;
		int i = si + delta;
		int c = getModel().getRowCount();
		if (isforward)
		{
			if (i >= c)
				i = 0;
			if (i < 0)
				i = 0;
		}
		else
		{
			if (i < 0)
				i = c - 1;
			if (i < 0)
				i = 0;
		}
		if (i >= c)
			return false;
		BPTableModel<T> model = getBPTableModel();
		int techc = 0;
		for (; i != si; i += delta)
		{
			if (techc >= c)
				break;
			if (isforward)
			{
				if (i >= c)
					i = 0;
				if (i < 0)
					i = 0;
			}
			else
			{
				if (i < 0)
					i = c - 1;
				if (i < 0)
					i = 0;
			}
			int c2 = model.getColumnCount();
			int r = convertRowIndexToModel(i);
			for (int j = 0; j < c2; j++)
			{
				String t = ObjUtil.toString(model.getValueAt(r, j));
				if (TextUtil.containsText(t, target, wholeword, !casesensitive))
				{
					ListSelectionModel selmodel = getSelectionModel();
					selmodel.clearSelection();
					selmodel.setSelectionInterval(i, i);
					scrollTo(i, j);
					return true;
				}
			}
			techc++;
		}
		return false;
	}

	public void setColumnWidthBatch(int w, int linenumw)
	{
		boolean isshowlinenum = getBPTableModel().isShowLineNum();
		TableColumnModel tcm = getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			if (isshowlinenum)
				tcm.getColumn(i).setPreferredWidth(isshowlinenum ? (i == 0 ? linenumw : w) : w);
			else
				tcm.getColumn(i).setPreferredWidth(w);
		}
	}

	public void installRowHeader(JScrollPane scroll)
	{
		scroll.setRowHeaderView(genRowHeader());
		scroll.getRowHeader().setPreferredSize(new Dimension(UIUtil.scale(48), 0));
	}

	public JTable genRowHeader()
	{
		JTable rc = new JTable();
		rc.setModel(new BPRowHeaderTableModel(this));
		{
			TableColumn tc = rc.getColumnModel().getColumn(0);
			tc.setCellRenderer(new BPRowHeaderRenderer(this));
		}
		return rc;
	}

	public static class BPTableRendererFileSize extends DefaultTableCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 6133614576207745283L;

		public BPTableRendererFileSize()
		{
			super();
			setHorizontalAlignment(BPTableRendererFileSize.RIGHT);
		}

		public void setValue(Object value)
		{
			if (value == null)
				setText("");
			else if (value instanceof String)
				setText("");
			else
				setText(NumberUtil.formatByteCount((Number) value));
		}
	}

	public static class BPTableRendererDateTime extends DefaultTableCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8426919503254677194L;

		protected DateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		public BPTableRendererDateTime()
		{
			super();
			setHorizontalAlignment(BPTableRendererFileSize.CENTER);
		}

		public void setValue(Object value)
		{
			if (value == null)
				setText("");
			else if (value instanceof Date)
				setText(sf.format(value));
			else if (value instanceof Long)
				setText(sf.format(value));
			else
				setText("");
		}
	}

	public static class BPTableRendererMultiline extends JEditorPane implements TableCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1121296378970157032L;
		protected boolean m_deciderowheight;
		protected Color m_pgselcolor;

		public BPTableRendererMultiline()
		{
			super();
			setFont(new Font(UIConfigs.TABLE_FONT_NAME(), Font.PLAIN, UIConfigs.TABLEFONT_SIZE()));
			m_pgselcolor = UIManager.getColor("Table.selectionBackground");
			putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		}

		public void setDecideRowHeight(boolean flag)
		{
			m_deciderowheight = flag;
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
		{
			setText(value == null ? "" : (String) value);
			setSize(table.getColumnModel().getColumn(col).getWidth(), getPreferredSize().height);
			if (m_deciderowheight)
			{
				if (table.getRowHeight(row) != getPreferredSize().height)
				{
					table.setRowHeight(row, getPreferredSize().height);
				}
			}
			if (isSelected)
			{
				setBackground(m_pgselcolor);
			}
			else
			{
				setBackground(UIConfigs.COLOR_TEXTBG());
			}

			return this;
		}

		protected EditorKit createDefaultEditorKit()
		{
			BPHTMLEditorKit rc = new BPHTMLEditorKit();
			return rc;
		}
	}

	public static class BPTableRendererCommonObj extends DefaultTableCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8174256871966808482L;

		protected DateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
		{
			Object v = value;
			if (v != null)
			{
				Class<?> c = v.getClass();
				if (c == Date.class)
					v = sf.format(v);
				else if (c.isArray())
					v = c.getComponentType().getName() + "[" + Array.getLength(v) + "]";
			}
			return super.getTableCellRendererComponent(table, v, isSelected, hasFocus, row, col);
		}
	}

	public static class BPTableRendererReplace extends DefaultTableCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 3611589077043983973L;

		protected TableCellRenderer m_cb = null;

		public BPTableRendererReplace(TableCellRenderer callback)
		{
			m_cb = callback;
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
		{
			Component comp = m_cb.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
			return comp != null ? comp : super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		}
	}

	public static class BPTableModel<T> extends AbstractTableModel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8297608681170410993L;

		protected List<T> m_datas;
		protected BPTableFuncs<T> m_funcs;
		protected boolean m_showlinenum;
		protected WeakReference<BiConsumer<String, Boolean>> m_statehandler;
		protected String m_id;

		public BPTableModel(BPTableFuncs<T> funcs)
		{
			m_funcs = funcs;
		}

		public void setOnStateChanged(BiConsumer<String, Boolean> handler)
		{
			if (handler != null)
			{
				m_statehandler = new WeakReference<BiConsumer<String, Boolean>>(handler);
			}
		}

		public void dispatchDataChanged()
		{
			WeakReference<BiConsumer<String, Boolean>> ref = m_statehandler;
			if (ref != null)
			{
				BiConsumer<String, Boolean> handler = ref.get();
				if (handler != null)
				{
					handler.accept(m_id, true);
				}
			}
		}

		public void setShowLineNum(boolean flag)
		{
			m_showlinenum = flag;
		}

		public boolean isShowLineNum()
		{
			return m_showlinenum;
		}

		public BPTableFuncs<T> getTableFuncs()
		{
			return m_funcs;
		}

		public void setDatas(List<T> datas)
		{
			m_datas = datas;
		}

		public List<T> getDatas()
		{
			return m_datas;
		}

		public int getRowCount()
		{
			return m_datas == null ? 0 : m_datas.size();
		}

		public T getRow(int row)
		{
			return m_datas == null ? null : m_datas.get(row);
		}

		public int getColumnCount()
		{
			return (m_showlinenum ? 1 : 0) + m_funcs.getColumnNames().length;
		}

		public String getColumnName(int col)
		{
			if (m_showlinenum)
			{
				if (col == 0)
					return "#";
				else
					return m_funcs.getColumnLabel(col - 1);
			}
			else
			{
				return m_funcs.getColumnLabel(col);
			}
		}

		public String getColumnRawName(int col)
		{
			if (m_showlinenum)
			{
				if (col == 0)
					return "#";
				else
					return m_funcs.getColumnName(col - 1);
			}
			else
			{
				return m_funcs.getColumnName(col);
			}
		}

		public Class<?> getColumnClass(int col)
		{
			if (m_showlinenum)
			{
				if (col == 0)
					return Integer.class;
				else
					return m_funcs.getColumnClass(col - 1);
			}
			else
			{
				return m_funcs.getColumnClass(col);
			}
		}

		public Object getValueAt(int row, int col)
		{
			if (m_datas == null)
				return "";
			if (row >= m_datas.size())
				return "";
			if (m_showlinenum)
			{
				if (col == 0)
					return row + 1;
				else
					return m_funcs.getValue(m_datas.get(row), row, col - 1);
			}
			else
			{
				return m_funcs.getValue(m_datas.get(row), row, col);
			}
		}

		public boolean isCellEditable(int row, int col)
		{
			if (m_showlinenum && col == 0)
				return false;
			return m_funcs.isEditable(m_datas.get(row), row, m_showlinenum ? col - 1 : col);
		}

		public void setValueAt(Object v, int row, int col)
		{
			m_funcs.setValue(v, m_datas.get(row), row, m_showlinenum ? col - 1 : col);
		}

		public void addAll(List<T> datas)
		{
			m_datas.addAll(datas);
			dispatchDataChanged();
		}

		public void add(T data)
		{
			m_datas.add(data);
			dispatchDataChanged();
		}

		public void delete(int[] rows)
		{
			for (int i = rows.length - 1; i >= 0; i--)
				m_datas.remove(rows[i]);
			dispatchDataChanged();
		}

		public void insert(int row, T data)
		{
			if (row >= m_datas.size())
				m_datas.add(data);
			else
				m_datas.add(row, data);
			dispatchDataChanged();
		}

		public void setID(String id)
		{
			m_id = id;
		}
	}

	public static class BPRowFilter<T> extends RowFilter<BPTableModel<T>, Integer>
	{
		private String m_str;

		public BPRowFilter(String str)
		{
			m_str = str;
		}

		public String getFilterText()
		{
			return m_str;
		}

		public boolean include(Entry<? extends BPTableModel<T>, ? extends Integer> value)
		{
			int c = value.getModel().getColumnCount();
			String fs = m_str;
			if (fs == null)
				return true;
			for (int i = 0; i < c; i++)
			{
				String v = value.getStringValue(i);
				if (v.indexOf(fs) > -1)
					return true;
			}
			return false;
		}

		public boolean setFilterText(String txt)
		{
			String oldv = m_str;
			String v = (txt != null && txt.length() == 0) ? null : txt;
			boolean changed = true;
			if (oldv != null)
				changed = !oldv.equals(v);
			else if (v != null)
				changed = !v.equals(oldv);
			m_str = v;
			return changed;
		}
	}

	public static class BPTableRowSorter<M extends TableModel> extends TableRowSorter<M>
	{
		public BPTableRowSorter()
		{
			super(null);
		}

		public BPTableRowSorter(M model)
		{
			super(model);
		}

		public void toggleSortOrder(int column)
		{
			checkColumn(column);
			if (isSortable(column))
			{
				List<SortKey> keys = new ArrayList<SortKey>(getSortKeys());
				SortKey sortKey;
				int sortIndex;
				for (sortIndex = keys.size() - 1; sortIndex >= 0; sortIndex--)
				{
					if (keys.get(sortIndex).getColumn() == column)
					{
						break;
					}
				}
				if (sortIndex == -1)
				{
					sortKey = new SortKey(column, SortOrder.ASCENDING);
					keys.add(0, sortKey);
				}
				else if (sortIndex == 0)
				{
					keys.set(0, toggle(keys.get(0)));
				}
				else
				{
					keys.remove(sortIndex);
					keys.add(0, new SortKey(column, SortOrder.ASCENDING));
				}
				if (keys.size() > getMaxSortKeys())
				{
					keys = keys.subList(0, getMaxSortKeys());
				}
				setSortKeys(keys);
			}
		}

		private SortKey toggle(SortKey key)
		{
			SortOrder so = key.getSortOrder();

			if (so == SortOrder.ASCENDING)
			{
				return new SortKey(key.getColumn(), SortOrder.DESCENDING);
			}
			else if (so == SortOrder.DESCENDING)
			{
				return new SortKey(key.getColumn(), SortOrder.UNSORTED);
			}
			return new SortKey(key.getColumn(), SortOrder.ASCENDING);
		}

		private void checkColumn(int column)
		{
			if (column < 0 || column >= getModelWrapper().getColumnCount())
			{
				throw new IndexOutOfBoundsException("column beyond range of TableModel");
			}
		}
	}

	public static class BPCellEditorReadonly extends DefaultCellEditor
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -6339096170907612537L;

		public BPCellEditorReadonly(JTextField textfield)
		{
			super(textfield);
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			JTextField comp = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
			comp.setEditable(false);
			return comp;
		}
	}

	@SuppressWarnings("unchecked")
	public void createDefaultColumnsFromModel()
	{
		List<String> cols = new ArrayList<String>();
		TableModel m0 = getModel();
		if (m0 instanceof BPTableModel)
		{
			BPTableModel<T> m = (BPTableModel<T>) m0;
			for (int i = 0; i < m.getColumnCount(); i++)
				cols.add(m.getColumnRawName(i));
			initColumnsFromModel(cols);
		}
		else
		{
			super.createDefaultColumnsFromModel();
		}
	}

	public void initColumnsFromModel(List<String> cols)
	{
		BPTableModel<T> m = getBPTableModel();
		if (m != null)
		{
			BPTableColumnModel cm = (BPTableColumnModel) getColumnModel();
			while (cm.getColumnCount() > 0)
				cm.removeColumn(cm.getColumn(0));

			int c = 0;
			for (String colname : cols)
			{
				TableColumn tc = null;
				if (!cm.isColumnHide(colname))
				{
					tc = cm.getCachedColumn(colname);
					if (tc == null)
					{
						for (int i = 0; i < m.getColumnCount(); i++)
						{
							if (colname.equals(m.getColumnRawName(i)))
							{
								BPTableColumn col = new BPTableColumn(i);
								col.setColumnName(colname);
								if (c == 0 && m.isShowLineNum())
									col.setIsLineNumber(true);
								tc = col;
								break;
							}
						}
					}
				}
				if (tc != null)
				{
					tc.setModelIndex(c);
					addColumn(tc);
				}
				c++;
			}
		}
	}

	public static class BPTableColumn extends TableColumn
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3595738572468140855L;

		protected String m_colname;
		protected boolean m_islinenum;
		protected int m_coldefaultwidth;
		protected boolean m_pwsetted;

		public BPTableColumn(int i)
		{
			super(i);
		}

		public void setIsLineNumber(boolean flag)
		{
			m_islinenum = flag;
		}

		public boolean isLineNumber()
		{
			return m_islinenum;
		}

		public void setColumnName(String colname)
		{
			m_colname = colname;
		}

		public String getColumnName()
		{
			return m_colname;
		}

		public void setModelIndex(int index)
		{
			super.setModelIndex(index);
		}

		public void setPreferredWidthOnce(int w)
		{
			if (!m_pwsetted)
			{
				m_pwsetted = true;
				setPreferredWidth(w);
			}
		}
	}

	public static class BPTableColumnModel extends DefaultTableColumnModel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 382606793781393866L;

		protected Map<String, BPTableColumn> m_colcache;
		protected Map<String, Boolean> m_hidecols;
		protected int m_calcw;

		public BPTableColumnModel()
		{
			m_hidecols = new HashMap<String, Boolean>();
			m_colcache = new HashMap<String, BPTableColumn>();
		}

		public void setColumnHide(String col, Boolean v)
		{
			if (v == null)
				m_hidecols.remove(col);
			else
				m_hidecols.put(col, v);
		}

		public boolean isColumnHide(String col)
		{
			Boolean v = m_hidecols.get(col);
			return v == null ? false : v;
		}

		public void saveCache()
		{
			m_colcache = new HashMap<String, BPTableColumn>();
			int c = getColumnCount();
			for (int i = 0; i < c; i++)
			{
				BPTableColumn tc = (BPTableColumn) getColumn(i);
				m_colcache.put(tc.getColumnName(), tc);
			}
		}

		public TableColumn getCachedColumn(String colname)
		{
			return m_colcache.get(colname);
		}

		public ColumnBuilder getColumnBuilder(int col)
		{
			return getColumnBuilder(getColumn(col));
		}

		public ColumnBuilder getColumnBuilder(TableColumn col)
		{
			return new ColumnBuilder(col, this);
		}

		public void applyDefaultColumnWidth(BPTableFuncs<?> f)
		{
			int c = getColumnCount();
			int d = 0;
			int s = 0;
			for (int i = 0; i < c; i++)
			{
				TableColumn tc = getColumn(i);
				if (tc instanceof BPTableColumn)
				{
					BPTableColumn btc = (BPTableColumn) tc;
					if (btc.isLineNumber())
					{
						d++;
						continue;
					}
					int ci = btc.getModelIndex();
					int w = f.getColumnWidth(ci - d);
					if (w <= 0)
					{
						if (w == -999999)
						{
							btc.setMinWidth(0);
						}
						else
						{
							if (w < 0)
							{
								btc.setMinWidth(UIUtil.scale(0 - w));
							}
							s += 0 - w;
//							btc.setPreferredWidth(10000);
						}
					}
					else
					{
						s += w;
						btc.setMaxWidth(UIUtil.scale(w));
						btc.setPreferredWidth(UIUtil.scale(w));
						btc.setMinWidth(UIUtil.scale(w));
					}
				}
			}
			m_calcw = s;
		}

		public int getCalcWidth()
		{
			return m_calcw;
		}

		public static class ColumnBuilder
		{
			protected TableColumn m_col;
			protected int m_oldw;
			protected BPTableColumnModel m_model;

			public ColumnBuilder(TableColumn col, BPTableColumnModel model)
			{
				m_col = col;
				m_oldw = col.getWidth();
				m_model = model;
			}

			public ColumnBuilder setWidth(int w)
			{
				m_col.setWidth(w);
				return this;
			}

			public ColumnBuilder setPreferredWidth(int w)
			{
				m_col.setPreferredWidth(w);
				return this;
			}

			public ColumnBuilder setMinWidth(int w)
			{
				m_col.setMinWidth(w);
				return this;
			}

			public ColumnBuilder setMaxWidth(int w)
			{
				m_col.setMaxWidth(w);
				return this;
			}

			public ColumnBuilder setResizable(boolean flag)
			{
				m_col.setResizable(false);
				return this;
			}

			public ColumnBuilder setCellRenderer(TableCellRenderer cellrenderer)
			{
				m_col.setCellRenderer(cellrenderer);
				return this;
			}

			public ColumnBuilder setCellEditor(TableCellEditor celleditor)
			{
				m_col.setCellEditor(celleditor);
				return this;
			}
		}
	}

	public class BPRowHeaderTableModel extends AbstractTableModel implements PropertyChangeListener, TableModelListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5720837858214339992L;
		protected WeakRefGo<JTable> m_tableref = new WeakRefGo<>();

		public BPRowHeaderTableModel(JTable table)
		{
			m_tableref = new WeakRefGo<JTable>(table);
			table.getModel().addTableModelListener(this);
			table.addPropertyChangeListener(this);
		}

		public void propertyChange(PropertyChangeEvent e)
		{
			String p = e.getPropertyName();
			if ("model".equals(p))
			{
				TableModel m = (TableModel) e.getOldValue();
				if (m != null)
					m.removeTableModelListener(this);
				m = (TableModel) e.getNewValue();
				if (m != null)
					m.addTableModelListener(this);
				fireTableDataChanged();
			}
		}

		public int getRowCount()
		{
			JTable t = m_tableref.get();
			return t == null ? 0 : t.getRowCount();
		}

		public int getColumnCount()
		{
			return 1;
		}

		public Object getValueAt(int row, int col)
		{
			return row + 1;
		}

		public void tableChanged(TableModelEvent e)
		{
			fireTableDataChanged();
		}
	}

	public class BPRowHeaderRenderer extends JLabel implements TableCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -586824365041953363L;

		protected int m_vsize = 4;

		public BPRowHeaderRenderer(JTable table)
		{
			JTableHeader header = table.getTableHeader();
			setOpaque(true);
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			setHorizontalAlignment(CENTER);
			setFont(header.getFont());
			setBackground(header.getBackground());
			setForeground(header.getForeground());
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (value == null)
			{
				setText("");
			}
			else
			{
				int v = (Integer) value;
				String vstr = Integer.toString(v);
				setText(vstr);
				int x = vstr.length();
				if (x > m_vsize)
				{
					m_vsize = x;
					UIUtil.laterUI(() ->
					{
						Component c = table.getParent();
						if (c != null)
						{
							c = c.getParent();
							if (c != null && c instanceof JScrollPane)
								((JScrollPane) c).getRowHeader().setPreferredSize(new Dimension(UIUtil.scale(12 * x), 0));
						}
					});
				}
			}
			return this;
		}
	}
}
