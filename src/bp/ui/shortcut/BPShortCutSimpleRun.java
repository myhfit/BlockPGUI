package bp.ui.shortcut;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.util.JSONUtil;
import bp.util.SystemUtil;
import bp.util.TextUtil;

public class BPShortCutSimpleRun extends BPShortCutBase
{
	public final static String SCKEY_SIMPLERUN = "Run";

	protected final static String SC_KEY_CMD = "cmd";
	protected final static String SC_KEY_DIR = "dir";
	protected final static String SC_KEY_ARGS = "args";

	@SuppressWarnings("unchecked")
	public boolean run()
	{
		String cmd = TextUtil.eds(getParam(SC_KEY_CMD));
		String workdir = TextUtil.eds(getParam(SC_KEY_DIR));
		String[] args = null;
		Object argobj = getParam(SC_KEY_ARGS);
		if (argobj != null)
		{
			if (argobj instanceof List)
			{
				List<String> arglist = (List<String>) argobj;
				args = arglist.toArray(new String[arglist.size()]);
			}
			else
			{
				String argstr = (String) argobj;
				args = argstr.split(" ");
			}
		}
		SystemUtil.runSimpleProcess(cmd, workdir, args, true);
		return true;
	}

	public BPSetting getSetting()
	{
		BPSettingBase rc = (BPSettingBase) super.getSetting();
		rc.addItem(BPSettingItem.create(SC_KEY_CMD, "Command", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.addItem(BPSettingItem.create(SC_KEY_DIR, "Work Dir", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.addItem(BPSettingItem.create(SC_KEY_ARGS, "Arguments", BPSettingItem.ITEM_TYPE_TEXT, null));

		Map<String, Object> ps = m_params == null ? new LinkedHashMap<String, Object>() : new LinkedHashMap<String, Object>(m_params);
		Object argobj = ps.get(SC_KEY_ARGS);
		if (argobj != null)
		{
			if (argobj instanceof List)
			{
				ps.put(SC_KEY_ARGS, JSONUtil.encode(argobj));
			}
		}
		rc.setAll(ps);
		return rc;
	}

	public void setSetting(BPSetting setting)
	{
		super.setSetting(setting);
		m_params = setParamsFromSetting(new LinkedHashMap<String, Object>(), setting, true, false, SC_KEY_CMD, SC_KEY_DIR);
		Object argobj = setting.get(SC_KEY_ARGS);
		if (argobj != null)
		{
			if (argobj instanceof String)
			{
				String argstr = TextUtil.eds((String) argobj);
				if (argstr != null)
				{
					if (argstr.startsWith("["))
						m_params.put(SC_KEY_ARGS, JSONUtil.decode(argstr));
					else
						m_params.put(SC_KEY_ARGS, argstr);
				}
			}
			else
			{
				m_params.put(SC_KEY_ARGS, argobj);
			}
		}
	}

	public String getShortCutKey()
	{
		return SCKEY_SIMPLERUN;
	}

	protected String[] getParamKeys()
	{
		return new String[] { SC_KEY_CMD, SC_KEY_DIR, SC_KEY_ARGS };
	}
}
