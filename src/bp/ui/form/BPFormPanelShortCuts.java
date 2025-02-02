package bp.ui.form;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Action;

import bp.config.BPSetting;
import bp.ui.actions.BPAction;
import bp.ui.dialog.BPDialogSelectData;
import bp.ui.dialog.BPDialogSetting;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPKVTable.KV;
import bp.ui.shortcut.BPShortCut;
import bp.ui.shortcut.BPShortCutFactory;
import bp.ui.shortcut.BPShortCutManager;
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
			String[] vs = v.split(",");
			if (vs.length > 0)
			{
				sckey = vs[0];
				BPShortCut sc = BPShortCutManager.makeShortCut((name + "," + v).split(","));
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
				setting = dlg.getSetting();
				if (setting != null)
				{
					name = setting.get("name");
					BPShortCut sc = BPShortCutManager.makeShortCut(new String[] { name, sckey });
					sc.setSetting(setting);
					String[] ps = sc.getParams();
					kv.key = name;
					kv.value = sckey + "," + TextUtil.join(ps, ",");

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
			setting = dlg.getSetting();
			if (setting != null)
			{
				String name = setting.get("name");
				BPShortCut sc = BPShortCutManager.makeShortCut(new String[] { name, sckey });
				sc.setSetting(setting);
				String[] ps = sc.getParams();
				KV kv = new KV();
				kv.key = name;
				kv.value = sckey + "," + TextUtil.join(ps, ",");

				m_tabkvs.getBPTableModel().getDatas().add(kv);
				m_tabkvs.getBPTableModel().fireTableDataChanged();
			}
		}
	}
}
