package bp.ui.table;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.swing.Action;

import bp.BPCore;
import bp.config.PredefinedDataPipes;
import bp.data.BPDataEndpointFactory;
import bp.data.BPDataPipes;
import bp.data.BPJSONContainerBase;
import bp.data.BPXData;
import bp.data.BPXYData;
import bp.format.BPFormatText;
import bp.format.BPFormatUnknown;
import bp.res.BPResource;
import bp.transform.BPTransformer;
import bp.transform.BPTransformerFactory;
import bp.transform.BPTransformerManager;
import bp.ui.actions.BPAction;
import bp.ui.scomp.BPKVTable.KV;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.util.UIStd;
import bp.util.ClassUtil;
import bp.util.ObjUtil;

public class BPTableFuncsXY extends BPTableFuncsBase<BPXData>
{
	protected BPXYData m_xydata;
	protected boolean m_deletable_user;

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

	public void setUserDeletable(boolean flag)
	{
		m_deletable_user = flag;
	}

	public Object getValue(BPXData o, int row, int col)
	{
		Object rc = null;
		if (o != null)
		{
			return o.getColValue(col);
		}
		return rc;
	}

	public boolean isEditable(BPXData o, int row, int col)
	{
		return true;
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
		o.setColValue(col, rv);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Action> getActions(BPTable<BPXData> table, List<BPXData> datas, int[] rows, int r, int c)
	{
		List<Action> rc = new ArrayList<Action>();
		if (rows != null && rows.length > 0)
		{
			BPAction actview = BPAction.build("View").mnemonicKey(KeyEvent.VK_V).callback((e) -> view(table, datas, rows)).getAction();
			BPAction actedit = BPAction.build("Edit").mnemonicKey(KeyEvent.VK_E).callback((e) -> edit(table, datas, rows)).getAction();
			BPAction actviewcell = BPAction.build("View Cell").callback((e) -> viewcell(table, datas, rows, r, c)).getAction();
			BPAction acteditcell = BPAction.build("Edit Cell").callback((e) -> editcell(table, datas, rows, r, c)).getAction();
			BPAction actdel = BPAction.build("Delete").mnemonicKey(KeyEvent.VK_D).callback((e) -> delete(table, datas, rows)).getAction();
			rc.add(actview);
			rc.add(actedit);
			rc.add(BPAction.separator());
			rc.add(actviewcell);
			rc.add(acteditcell);
			if (m_deletable_user)
			{
				rc.add(BPAction.separator());
				rc.add(actdel);
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
						BPAction acttrans = BPAction.build("Transform Cell").getAction();
						List<Action> actsub = new ArrayList<Action>();
						Map<String, BPTransformer<?>> ts = BPTransformerManager.getTransformer(o, BPTransformerFactory.TF_TOSTRING);
						if (ts != null && ts.size() > 0)
						{
							for (String tkey : ts.keySet())
							{
								BPTransformer t = ts.get(tkey);
								BPAction acttt = BPAction.build(tkey + ">Text").getAction();
								actsub.add(acttt);
								List<Action> actss = new ArrayList<Action>();
								ServiceLoader<BPDataEndpointFactory> facs = ClassUtil.getServices(BPDataEndpointFactory.class);
								for (BPDataEndpointFactory fac : facs)
								{
									if (fac.canHandle(BPFormatText.FORMAT_TEXT))
									{
										actss.add(BPAction.build(fac.getName()).callback(e ->
										{
											t.setOutput(fac.create(BPFormatText.FORMAT_TEXT));
											t.runSegment(() -> t.accept(o));
										}).getAction());
									}
								}
								acttt.putValue(BPAction.SUB_ACTIONS, actss.toArray(new Action[actss.size()]));
							}
						}

						ts = BPTransformerManager.getTransformer(o, BPTransformerFactory.TF_TOBYTEARRAY);
						if (ts != null && ts.size() > 0)
						{
							for (String tkey : ts.keySet())
							{
								BPTransformer t = ts.get(tkey);
								BPAction acttt = BPAction.build(tkey + ">byte[]").getAction();
								actsub.add(acttt);
								List<Action> actss = new ArrayList<Action>();
								ServiceLoader<BPDataEndpointFactory> facs = ClassUtil.getServices(BPDataEndpointFactory.class);
								for (BPDataEndpointFactory fac : facs)
								{
									if (fac.canHandle(BPFormatUnknown.FORMAT_NA))
									{
										actss.add(BPAction.build(fac.getName()).callback(e ->
										{
											t.setOutput(fac.create(BPFormatUnknown.FORMAT_NA));
											t.runSegment(() -> t.accept(o));
										}).getAction());
									}
								}
								acttt.putValue(BPAction.SUB_ACTIONS, actss.toArray(new Action[actss.size()]));
							}
						}

						acttrans.putValue(BPAction.SUB_ACTIONS, actsub.toArray(new Action[actsub.size()]));
						rc.add(acttrans);
					}

					{
						BPAction actpdps = BPAction.build("DataPipes").getAction();
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
		}
		return rc;
	}

	protected void delete(BPTable<BPXData> table, List<BPXData> datas, int[] rows)
	{
		BPTableModel<BPXData> model = table.getBPTableModel();
		model.delete(rows);
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
		UIStd.textarea(ObjUtil.toString(v, ""), "View Cell");
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
			UIStd.textarea(ObjUtil.toString(v, ""), "View Cell");
		}
		else
		{
			v = sdata.getColValue(c - (showlinenum ? 1 : 0));
			String newv = UIStd.textarea(ObjUtil.toString(v, ""), "Edit Cell", true);
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
			String label = funcs.getColumnName(i);
			Object v = funcs.getValue(xdata, rows[0], i);
			props.add(new Object[] { label, v });
		}
		UIStd.kv(props, "View Data", true);
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
			String label = funcs.getColumnName(i);
			Object v = funcs.getValue(xdata, rows[0], i);
			props.add(new Object[] { label, v });
		}
		List<KV> kvs = UIStd.kv(props, "Edit Data", false);
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
