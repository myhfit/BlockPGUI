package bp.ui.shortcut;

import bp.BPGUICore;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;

public class BPShortCutConsole extends BPShortCutBase
{
	public boolean run()
	{
		String[] params = m_params;
		String cmd = params[0];
		String dir = null;
		String encoding = null;
		if (params.length > 1)
		{
			dir = params[1];
			if (dir != null && dir.length() == 0)
				dir = null;
		}
		if (params.length > 2)
		{
			encoding = params[2];
			if (encoding != null && encoding.length() == 0)
				encoding = null;
		}
		String pcmd = cmd;
		String pdir = dir;
		String pen = encoding;
		BPGUICore.runOnMainFrame(mf -> mf.createEditorByFileSystem(null, null, "Console", null, pcmd, pdir, pen));
		return true;
	}

	public BPSetting getSetting()
	{
		BPSettingBase rc = (BPSettingBase) super.getSetting();
		rc.addItem(BPSettingItem.create("cmd", "Command", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.addItem(BPSettingItem.create("dir", "Work Dir", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.addItem(BPSettingItem.create("encoding", "Encoding", BPSettingItem.ITEM_TYPE_TEXT, null));

		rc.set("cmd", getParamValue(0));
		rc.set("dir", getParamValue(1));
		rc.set("encoding", getParamValue(2));
		return rc;
	}

	public void setSetting(BPSetting setting)
	{
		super.setSetting(setting);
		String cmd = setting.get("cmd");
		if (cmd == null)
			cmd = "";
		String dir = setting.get("dir");
		if (dir == null)
			dir = "";
		String encoding = setting.get("encoding");
		if (encoding == null)
			encoding = "";
		m_params = new String[] { cmd, dir, encoding };
	}
}