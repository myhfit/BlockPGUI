package bp.ui.shortcut;

import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.util.ProcessUtil;
import bp.util.SystemUtil;

public class BPShortCutSimpleRun extends BPShortCutBase
{
	public boolean run()
	{
		String[] params = m_params;
		String cmd = params[0];
		String workdir = null;
		String[] args = null;
		if (params.length > 1)
		{
			workdir = params[1];
			if (workdir != null && workdir.length() == 0)
				workdir = null;
		}
		if (params.length > 2)
		{
			String argstr = params[2];
			if (argstr != null && argstr.length() > 0)
			{
				if (argstr.startsWith("\"") && argstr.endsWith("\""))
					argstr = argstr.substring(1, argstr.length() - 1);
				args = ProcessUtil.fixCommandArgs(argstr);
			}
		}
		SystemUtil.runSimpleProcess(cmd, workdir, args, true);
		return true;
	}

	public BPSetting getSetting()
	{
		BPSettingBase rc = (BPSettingBase) super.getSetting();
		rc.addItem(BPSettingItem.create("cmd", "Command", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.addItem(BPSettingItem.create("dir", "Work Dir", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.addItem(BPSettingItem.create("args", "Arguments", BPSettingItem.ITEM_TYPE_TEXT, null));

		rc.set("cmd", getParamValue(0));
		rc.set("dir", getParamValue(1));
		rc.set("args", getParamValue(2));
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
		String args = setting.get("args");
		if (args == null)
			args = "";
		m_params = new String[] { cmd, dir, args };
	}
}
