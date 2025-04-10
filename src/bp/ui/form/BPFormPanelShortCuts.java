package bp.ui.form;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

import javax.swing.Action;

import bp.config.BPSetting;
import bp.config.ShortCuts;
import bp.ui.actions.BPAction;
import bp.ui.dialog.BPDialogSelectData;
import bp.ui.dialog.BPDialogSetting;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPKVTable.KV;
import bp.ui.shortcut.BPShortCut;
import bp.ui.shortcut.BPShortCutFactory;
import bp.ui.shortcut.BPShortCutManager;
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
		BPAction actedit = BPAction.build("Edit").callback(this::onEdit).vIcon(BPIconResV.EDIT()).getAction();
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
				{
					List<String> keys = BPShortCutManager.getFactoryKeys();
					BPDialogSelectData<String> dlg = new BPDialogSelectData<String>();
					dlg.setSource(keys);
					dlg.setVisible(true);
					sckey = dlg.getSelectData();
				}
				if (sckey != null)
				{
					setting = BPShortCutManager.getSetting(sckey);
				}
			}

			if (setting != null)
			{
				BPDialogSetting dlg = new BPDialogSetting();
				dlg.setSetting(setting);
				dlg.setVisible(true);
				setting = dlg.getResult();
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

	protected void onAdd(ActionEvent e)
	{
		String sckey = null;
		List<String> keys = BPShortCutManager.getFactoryKeys();

		{
			BPDialogSelectData<String> dlg = new BPDialogSelectData<String>();
			dlg.setSource(keys);
			dlg.setVisible(true);
			sckey = dlg.getSelectData();
		}

		if (sckey != null)
		{
			BPSetting setting = BPShortCutManager.getSetting(sckey);
			BPDialogSetting dlg = new BPDialogSetting();
			dlg.setSetting(setting);
			dlg.setVisible(true);
			setting = dlg.getResult();
			if (setting != null)
			{
				String name = setting.get("name");
				BPShortCut sc = BPShortCutManager.makeShortCut(new String[] { name, sckey });
				sc.setSetting(setting);
				Map<String, Object> ps = sc.getMappedDataWithKey();
				KV kv = new KV();
				kv.key = name;
				kv.value = JSONUtil.encode(ps);

				m_tabkvs.getBPTableModel().getDatas().add(kv);
				m_tabkvs.getBPTableModel().fireTableDataChanged();
			}
		}
	}
}
