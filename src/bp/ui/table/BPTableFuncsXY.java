package bp.ui.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.swing.Action;

import bp.BPCore;
import bp.config.BPSetting;
import bp.config.PredefinedDataPipes;
import bp.data.BPDataEndpointFactory;
import bp.data.BPDataPipes;
import bp.data.BPJSONContainerBase;
import bp.data.BPXData;
import bp.data.BPXYData;
import bp.format.BPFormatText;
import bp.format.BPFormatTreeData;
import bp.format.BPFormatUnknown;
import bp.format.BPFormatXYData;
import bp.locale.BPLocaleConstCC;
import bp.res.BPResource;
import bp.transform.BPTransformer;
import bp.transform.BPTransformerFactory;
import bp.transform.BPTransformerManager;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.dialog.BPDialogSetting;
import bp.ui.scomp.BPKVTable.KV;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;
import bp.util.ObjUtil;

public class BPTableFuncsXY extends BPTableFuncsBase<BPXData>
{
	protected BPXYData m_xydata;
	protected boolean m_readonly;
	protected boolean m_checkcol;
	protected boolean m_row_deletable;
	protected boolean m_row_insertable;

	public BPTableFuncsXY(BPXYData xydata)
	{
		m_xydata = xydata;
		if (xydata != null)
		{
			m_cols = xydata.getColumnClasses();
			m_colnames = xydata.getColumnNames();
			m_collabels = xydata.getColumnLabels();
		}
		else
		{
			m_cols = null;
			m_colnames = null;
			m_collabels = null;
		}
	}

	public BPXYData getRawData()
	{
		return m_xydata;
	}

	public void setReadonly(boolean flag)
	{
		m_readonly = flag;
	}

	public void setColCheck(boolean flag)
	{
		m_checkcol = flag;
	}

	public void setStructureEditable(boolean flag)
	{
		m_row_deletable = flag;
		m_row_insertable = flag;
	}

	public void setRowDeletable(boolean flag)
	{
		m_row_deletable = flag;
	}

	public Object getValue(BPXData o, int row, int col)
	{
		Object rc = null;
		if (o != null)
		{
			if (!m_checkcol)
				return o.getColValue(col);
			return o.length() > col ? o.getColValue(col) : null;
		}
		return rc;
	}

	public boolean isEditable(BPXData o, int row, int col)
	{
		return !m_readonly;
	}

	public void setValue(Object v, BPXData o, int row, int col)
	{
		Object rv = null;
		rv = v;
		if (rv != null)
		{
			if (m_cols != null)
				rv = ObjUtil.castObject(rv, m_cols[col], null);
		}
		if (!m_checkcol)
			o.setColValue(col, rv);
		else
			o.setColValueOrResize(col, rv);
	}

	public List<Action> getActions(BPTable<BPXData> table, List<BPXData> datas, int[] rows, int r, int c)
	{
		List<Action> rc = new ArrayList<Action>();
		if (rows != null && rows.length > 0)
		{
			BPAction actview = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUVIEW, e -> view(table, datas, rows));
			BPAction actedit = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUEDIT, e -> edit(table, datas, rows));
			BPAction actviewcell = BPActionHelpers.getActionWithAlias(BPActionConstCommon.CTX_MNUVIEW, BPActionConstCommon.CTX_MNUVIEW_CELL, e -> viewcell(table, datas, rows, r, c));
			BPAction acteditcell = BPActionHelpers.getActionWithAlias(BPActionConstCommon.CTX_MNUEDIT, BPActionConstCommon.CTX_MNUEDIT_CELL, e -> editcell(table, datas, rows, r, c));
			rc.add(actview);
			rc.add(actedit);
			rc.add(BPAction.separator());
			rc.add(actviewcell);
			rc.add(acteditcell);
			if (m_row_deletable || m_row_insertable)
			{
				rc.add(BPAction.separator());
				if (m_row_insertable)
				{
					BPAction actinsertp = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUINSERTPREV, e -> insert(table, datas, rows, false));
					BPAction actinsertn = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUINSERTNEXT, e -> insert(table, datas, rows, true));
					rc.add(actinsertp);
					rc.add(actinsertn);
				}
				if (m_row_deletable)
				{
					BPAction actdel = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUDEL, e -> delete(table, datas, rows));
					rc.add(actdel);
				}
			}
			if (datas.size() > 0)
			{
				BPTableModel<BPXData> m = table.getBPTableModel();
				Object o;
				if (m.isShowLineNum())
				{
					if (c > 0)
						o = datas.get(0).getColValue(c - 1);
					else
						o = rows[0] + 1;
				}
				else
				{
					o = datas.get(0).getColValue(c);
				}
				if (o != null)
				{
					rc.add(BPAction.separator());

					{
						BPAction acttrans = BPActionHelpers.getAction(BPActionConstCommon.XYTBL_CTX_MNUTRANSCELL, null);
						List<Action> actsub = new ArrayList<Action>();
						assembleTransformer(actsub, o, BPTransformerFactory.TF_TOSTRING, BPFormatText.FORMAT_TEXT, ">" + BPLocaleConstCC.TEXT.text());
						actsub.add(BPAction.separator());
						assembleTransformer(actsub, o, BPTransformerFactory.TF_TOBYTEARRAY, BPFormatUnknown.FORMAT_NA, ">" + BPLocaleConstCC.BYTEARR.text());
						acttrans.putValue(BPAction.SUB_ACTIONS, actsub.toArray(new Action[actsub.size()]));
						rc.add(acttrans);
					}
				}

				{
					if (o == null)
						rc.add(BPAction.separator());
					BPAction acttrans = BPActionHelpers.getAction(BPActionConstCommon.XYTBL_CTX_MNUTRANSROW, null);
					List<Action> actsub = new ArrayList<Action>();
					BPXData row = datas.get(0);
					Map<String, Object> rowmap = m_xydata.toMap(row);
					assembleTransformer(actsub, rowmap, BPTransformerFactory.TF_TOMAP, BPFormatTreeData.FORMAT_TREEDATA, ">" + BPLocaleConstCC.TEXT.text());
					acttrans.putValue(BPAction.SUB_ACTIONS, actsub.toArray(new Action[actsub.size()]));
					rc.add(acttrans);
				}

				{
					BPAction acttrans = BPActionHelpers.getAction(BPActionConstCommon.XYTBL_CTX_MNUTRANSSEL, null);
					List<Action> actsub = new ArrayList<Action>();
					BPXYData xy2 = m_xydata.reList(datas);
					assembleTransformer(actsub, xy2, BPTransformerFactory.TF_TOXY, BPFormatXYData.FORMAT_XYDATA, ">" + BPActionConstCommon.TXT_DATA.text());
					acttrans.putValue(BPAction.SUB_ACTIONS, actsub.toArray(new Action[actsub.size()]));
					rc.add(acttrans);
				}
				if (o != null)
				{
					rc.add(BPAction.separator());
					{
						BPAction actpdps = BPActionHelpers.getAction(BPActionConstCommon.TXT_DATAPIPES, null);
						List<Action> actsub = new ArrayList<Action>();
						List<String[]> pdps = PredefinedDataPipes.getDataPipes();
						for (String[] pdp : pdps)
						{
							String dpsrc = pdp[1];
							BPAction actpdp = BPAction.build(pdp[0]).callback(e ->
							{
								BPResource res = BPCore.getFileContext().getRes(dpsrc);
								BPJSONContainerBase<BPDataPipes> con = new BPJSONContainerBase<BPDataPipes>();
								con.bind(res);
								BPDataPipes dp = con.readMData(false);
								try
								{
									dp.run(o);
								}
								catch (Exception e2)
								{
									UIStd.err(e2);
								}
							}).getAction();
							actsub.add(actpdp);
						}
						actpdps.putValue(BPAction.SUB_ACTIONS, actsub.toArray(new Action[actsub.size()]));
						rc.add(actpdps);
					}
				}
			}

			if (m_customacts != null)
				ObjUtil.mergeList(BPAction::separator, rc, m_customacts);
		}
		return rc;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void assembleTransformer(List<Action> actpar, Object o, String functiontype_in, String formatname_out, String tarfix)
	{
		Map<String, BPTransformer<?>> ts = BPTransformerManager.getTransformer(o, functiontype_in);
		if (ts != null && ts.size() > 0)
		{
			for (String tkey : ts.keySet())
			{
				BPTransformer t = ts.get(tkey);
				BPAction acttt = BPAction.build(tkey + tarfix).getAction();
				actpar.add(acttt);
				List<Action> actss = new ArrayList<Action>();
				ServiceLoader<BPDataEndpointFactory> facs = ClassUtil.getServices(BPDataEndpointFactory.class);
				for (BPDataEndpointFactory fac : facs)
				{
					if (fac.canHandle(formatname_out))
					{
						actss.add(BPAction.build(fac.getName()).callback(e ->
						{
							if (t.needSettingUI())
							{
								BPSetting setting = BPDialogSetting.showSetting(t.getSetting());
								if (setting == null)
									return;
								t.setSetting(setting);
							}
							t.setOutput(fac.create(formatname_out));
							t.runSegment(() -> t.accept(o));
						}).getAction());
					}
				}
				acttt.putValue(BPAction.SUB_ACTIONS, actss.toArray(new Action[actss.size()]));
			}
		}
	}

	protected void delete(BPTable<BPXData> table, List<BPXData> datas, int[] rows)
	{
		if(rows.length==0)
			return;
		BPTableModel<BPXData> model = table.getBPTableModel();
		model.delete(rows);
		model.fireTableDataChanged();
	}

	protected void insert(BPTable<BPXData> table, List<BPXData> datas, int[] rows, boolean next)
	{
		if (rows.length == 0)
			return;
		BPTableModel<BPXData> model = table.getBPTableModel();
		int si;
		if (next)
			si = rows[rows.length - 1] + 1;
		else
			si = rows[0];
		model.insert(si, datas.get(0).cloneX(false));
		model.fireTableDataChanged();
	}

	protected void viewcell(BPTable<BPXData> table, List<BPXData> datas, int[] rows, int sr, int sc)
	{
		if (rows == null || rows.length == 0)
			return;
		int c = table.convertColumnIndexToModel(sc);
		int r = table.convertRowIndexToModel(sr);
		BPTableModel<?> m = table.getBPTableModel();
		BPXData sdata = table.getBPTableModel().getDatas().get(r);
		Object v;
		boolean showlinenum = m.isShowLineNum();
		if (c == 0 && showlinenum)
			v = r + 1;
		else
			v = sdata.getColValue(c - (showlinenum ? 1 : 0));
		UIStd.textarea(ObjUtil.toString(v, ""), BPActionConstCommon.CTX_MNUVIEW_CELL.text());
	}

	protected void editcell(BPTable<BPXData> table, List<BPXData> datas, int[] rows, int sr, int sc)
	{
		if (rows == null || rows.length == 0)
			return;
		int c = table.convertColumnIndexToModel(sc);
		int r = table.convertRowIndexToModel(sr);
		BPTableModel<?> m = table.getBPTableModel();
		BPXData sdata = table.getBPTableModel().getDatas().get(r);
		Object v;
		boolean showlinenum = m.isShowLineNum();
		if (c == 0 && showlinenum)
		{
			v = r + 1;
			UIStd.textarea(ObjUtil.toString(v, ""), BPActionConstCommon.CTX_MNUVIEW_CELL.text());
		}
		else
		{
			v = sdata.getColValue(c - (showlinenum ? 1 : 0));
			String newv = UIStd.textarea(ObjUtil.toString(v, ""), BPActionConstCommon.CTX_MNUEDIT_CELL.text(), true);
			if (newv != null)
				sdata.setColValue(c - (showlinenum ? 1 : 0), newv);
		}
	}

	protected void view(BPTable<BPXData> table, List<BPXData> datas, int[] rows)
	{
		if (rows == null || rows.length == 0)
			return;
		List<Object[]> props = new ArrayList<Object[]>();
		BPXData xdata = datas.get(0);
		BPTableModel<BPXData> model = table.getBPTableModel();
		BPTableFuncs<BPXData> funcs = model.getTableFuncs();
		int c = funcs.getColumnNames().length;
		for (int i = 0; i < c; i++)
		{
			String label = funcs.getColumnLabel(i);
			Object v = funcs.getValue(xdata, rows[0], i);
			props.add(new Object[] { label, v });
		}
		UIStd.kv(props, UIUtil.assembleLocaleTexts(BPActionConstCommon.TXT_VIEW, BPActionConstCommon.TXT_DATA), true);
	}

	protected void edit(BPTable<BPXData> table, List<BPXData> datas, int[] rows)
	{
		if (rows == null || rows.length == 0)
			return;
		List<Object[]> props = new ArrayList<Object[]>();
		BPXData xdata = datas.get(0);
		BPTableModel<BPXData> model = table.getBPTableModel();
		BPTableFuncs<BPXData> funcs = model.getTableFuncs();
		int c = funcs.getColumnNames().length;
		for (int i = 0; i < c; i++)
		{
			String label = funcs.getColumnLabel(i);
			Object v = funcs.getValue(xdata, rows[0], i);
			props.add(new Object[] { label, v });
		}
		List<KV> kvs = UIStd.kv(props, UIUtil.assembleLocaleTexts(BPActionConstCommon.TXT_EDIT, BPActionConstCommon.TXT_DATA), false);
		if (kvs != null)
		{
			for (int i = 0; i < c; i++)
			{
				xdata.setColValue(i, kvs.get(i).value);
			}
			model.fireTableDataChanged();
		}
	}

	public void clear()
	{
		m_xydata = null;
	}
}
