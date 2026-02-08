package bp.ui.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenu;

import bp.BPGUICore;
import bp.config.ShortCuts;
import bp.config.ShortCuts.ShortCutData;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.dialog.BPDialogForm;
import bp.ui.scomp.BPMenu;
import bp.ui.scomp.BPMenuItem.BPMenuItemInTray;
import bp.ui.shortcut.BPShortCut;
import bp.ui.shortcut.BPShortCutManager;

public class BlockPMenus
{
	public final static void refreshShortcuts(JMenu mnuscs, Runnable refreshcb)
	{
		JMenu mnushortcuts = mnuscs;
		mnushortcuts.removeAll();
		List<ShortCutData> scs = ShortCuts.getShortCuts();
		Map<String, JMenu> mnumap = new HashMap<String, JMenu>();
		for (ShortCutData sc : scs)
		{
			String scname = sc.name;
			String mnuname = scname;
			JMenu mnupar = mnushortcuts;
			if (scname.indexOf(">") != -1)
			{
				String[] strs = scname.split(">");
				String pathstr = null;
				for (int i = 0; i < strs.length - 1; i++)
				{
					String s = strs[i];
					if (pathstr == null)
						pathstr = s;
					else
						pathstr += ">" + s;
					JMenu mnunode = mnumap.get(pathstr);
					if (mnunode == null)
					{
						mnunode = new BPMenu(s);
						mnupar.add(mnunode);
						mnumap.put(pathstr, mnunode);
					}
					mnupar = mnunode;
				}
				mnuname = strs[strs.length - 1];
			}
			Action act = BPAction.build(mnuname).callback(e ->
			{
				ShortCutData sc2 = ShortCuts.getShortCut(scname);
				BPShortCut bsc = BPShortCutManager.makeShortCut(sc2);
				if (bsc != null)
					bsc.run();
			}).getAction();
			act.putValue("scname", scname);
			mnupar.add(act);
		}
		mnushortcuts.addSeparator();
		Action actsetting = BPActionHelpers.getAction(BPActionConstCommon.MF_MNUSCSEDITSCS, e ->
		{
			BPDialogForm dlg = new BPDialogForm();
			dlg.setup(ShortCuts.class.getName(), BPGUICore.CONFIGS_SC);
			dlg.setTitle("BlockP - ShortCuts");
			dlg.setVisible(true);
			Map<String, Object> formdata = dlg.getFormData();
			if (formdata != null)
			{
				BPGUICore.CONFIGS_SC.setMappedData(formdata);
				BPGUICore.CONFIGS_SC.save();
				if (refreshcb != null)
					refreshcb.run();
			}
		});
		mnushortcuts.add(actsetting);
	}

	public final static void refreshPopupShortcuts(JMenu popscs)
	{
		JMenu mnuscs = popscs;
		if (mnuscs == null)
			return;
		mnuscs.removeAll();

		List<ShortCutData> scs = ShortCuts.getShortCuts();
		Map<String, JMenu> mnumap = new HashMap<String, JMenu>();
		for (ShortCutData sc : scs)
		{
			String scname = sc.name;
			String mnuname = scname;
			JMenu mnupar = mnuscs;
			if (scname.indexOf(">") != -1)
			{
				String[] strs = scname.split(">");
				String pathstr = null;
				for (int i = 0; i < strs.length - 1; i++)
				{
					String s = strs[i];
					if (pathstr == null)
						pathstr = s;
					else
						pathstr += ">" + s;
					JMenu mnunode = mnumap.get(pathstr);
					if (mnunode == null)
					{
						mnunode = new BPMenu(s);
						mnupar.add(mnunode);
						mnumap.put(pathstr, mnunode);
					}
					mnupar = mnunode;
				}
				mnuname = strs[strs.length - 1];
			}
			BPMenuItemInTray newmnu = new BPMenuItemInTray(mnuname);
			newmnu.addActionListener(e ->
			{
				ShortCutData sc2 = ShortCuts.getShortCut(scname);
				BPShortCut bsc = BPShortCutManager.makeShortCut(sc2);
				if (bsc != null)
					bsc.run();
			});
			mnupar.add(newmnu);
		}
	}
}
