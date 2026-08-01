package bp.ui.form;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import bp.config.BPSetting;
import bp.config.ShortCuts;
import bp.locale.BPLocaleHelpers;
import bp.typeext.KV;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.dialog.BPDialogSetting;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.shortcut.BPShortCut;
import bp.ui.shortcut.BPShortCutFactory;
import bp.ui.shortcut.BPShortCutManager;
import bp.ui.util.UIStd;
import bp.util.JSONUtil;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public class BPFormPanelShortCuts extends BPFormPanelMapOrdered
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4891410124214947199L;

	protected void initForm()
	{
		super.initForm();
	}

	protected List<Action> makeToolBarActions()
	{
		List<Action> rc = super.makeToolBarActions();
		BPAction actedit = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNEDIT, this::onEdit);
		rc.add(BPAction.separator());
		rc.add(BPAction.separator());
		rc.add(actedit);
		return rc;
	}

	protected void onEdit(ActionEvent e)
	{
		List<KV> kvs = m_tabkvs.getSelectedDatas();
		if (kvs.size() == 1)
		{
			KV kv = kvs.get(0);
			String name = kv.key;
			String v = (String) kv.value;
			if (v == null)
				v = "";
			BPSetting setting = null;
			String sckey = null;
			if (v.startsWith("{"))
			{
				Map<String, Object> vmap = JSONUtil.decode(v);
				sckey = (String) vmap.get("key");
				BPShortCut sc = BPShortCutManager.makeShortCut(new ShortCuts.ShortCutData(name, vmap));
				if (sc != null)
				{
					setting = sc.getSetting();
				}
				else
				{
					BPShortCutFactory fac = BPShortCutManager.getFactory(sckey);
					if (fac == null)
					{
						sckey = null;
					}
				}
			}
			else
			{
				String[] vs = v.split(",");
				if (vs.length > 0)
				{
					sckey = vs[0];
					String[] scparamarr = null;
					if (v.startsWith("["))
					{
						List<String> nvobjs = JSONUtil.decode(v);
						sckey = nvobjs.get(0);
						nvobjs.add(0, name);
						scparamarr = nvobjs.toArray(new String[nvobjs.size() + 1]);
					}
					else
					{
						scparamarr = TextUtil.splitEscapePlainText((name + "," + v));
					}
					BPShortCut sc = BPShortCutManager.makeShortCut(scparamarr);
					if (sc != null)
					{
						setting = sc.getSetting();
					}
					else
					{
						BPShortCutFactory fac = BPShortCutManager.getFactory(sckey);
						if (fac == null)
						{
							sckey = null;
						}
					}
				}
			}
			if (setting == null)
			{
				if (sckey == null)
					sckey = UIStd.select(BPShortCutManager.getFactoryKeys(), null, s -> BPLocaleHelpers.translateByClass(BPShortCut.class, (String) s));

				if (sckey != null)
				{
					setting = BPShortCutManager.getSetting(sckey);
				}
			}

			if (setting != null)
			{
				setting = BPDialogSetting.showSetting(setting);
				if (setting != null)
				{
					name = setting.get("name");
					BPShortCut sc = BPShortCutManager.makeShortCut(new ShortCuts.ShortCutData(name, ObjUtil.makeMap("key", sckey)));
					sc.setSetting(setting);
					Map<String, Object> ps = sc.getMappedDataWithKey();
					kv.key = name;
					kv.value = JSONUtil.encode(ps);
					m_tabkvs.getBPTableModel().fireTableDataChanged();
				}
			}
		}
	}

	protected KV showCreateSC()
	{
		String sckey = UIStd.select(BPShortCutManager.getFactoryKeys(), null, s -> BPLocaleHelpers.translateByClass(BPShortCut.class, (String) s));
		if (sckey != null)
		{
			BPSetting setting = BPShortCutManager.getSetting(sckey);
			setting = BPDialogSetting.showSetting(setting);
			if (setting != null)
			{
				String name = setting.get("name");
				BPShortCut sc = BPShortCutManager.makeShortCut(new String[] { name, sckey });
				sc.setSetting(setting);
				Map<String, Object> ps = sc.getMappedDataWithKey();
				KV kv = new KV();
				kv.key = name;
				kv.value = JSONUtil.encode(ps);
				return kv;
			}
		}
		return null;
	}

	protected void onAdd(ActionEvent e)
	{
		KV kv = showCreateSC();
		if (kv != null)
		{
			BPTableModel<KV> m = m_tabkvs.getBPTableModel();
			List<KV> kvs = m.getDatas();
			kvs.add(kv);
			int r = m_tabkvs.convertRowIndexToView(kvs.size()-1);
			m_tabkvs.getBPTableModel().fireTableDataChanged();
			m_tabkvs.getSelectionModel().setSelectionInterval(r, r);
			m_tabkvs.requestFocus();
		}
	}

	protected void onInsert(ActionEvent e)
	{
		int si = m_tabkvs.getSelectedRow();
		if (si < 0)
		{
			onAdd(e);
			return;
		}
		KV kv = showCreateSC();
		if (kv != null)
		{
			BPTableModel<KV> m = m_tabkvs.getBPTableModel();
			List<KV> kvs = m.getDatas();
			kvs.add(si, new KV());
			m.fireTableDataChanged();
			int r = m_tabkvs.convertRowIndexToView(si);
			m_tabkvs.getSelectionModel().setSelectionInterval(r, r);
			m_tabkvs.scrollTo(r, 0);
			m_tabkvs.requestFocus();
		}
	}
}
