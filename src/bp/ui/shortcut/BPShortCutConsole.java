package bp.ui.shortcut;

import java.util.LinkedHashMap;

import bp.BPGUICore;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.util.TextUtil;

public class BPShortCutConsole extends BPShortCutBase
{
	protected final static String SC_KEY_CMD = "cmd";
	protected final static String SC_KEY_DIR = "dir";
	protected final static String SC_KEY_ENCODING = "encoding";

	public final static String SCKEY_CONSOLE = "Console";

	public boolean run()
	{
		String cmd = getParam(SC_KEY_CMD);
		String dir = TextUtil.eds(getParam(SC_KEY_DIR));
		String encoding = TextUtil.eds(getParam(SC_KEY_ENCODING));
		BPGUICore.runOnMainFrame(mf -> mf.createEditorByFileSystem(null, null, "Console", null, cmd, dir, encoding));
		return true;
	}

	public BPSetting getSetting()
	{
		BPSettingBase rc = (BPSettingBase) super.getSetting();
		rc.addItem(BPSettingItem.create(SC_KEY_CMD, "Command", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.addItem(BPSettingItem.create(SC_KEY_DIR, "Work Dir", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.addItem(BPSettingItem.create(SC_KEY_ENCODING, "Encoding", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.setAll(m_params);
		return rc;
	}

	public void setSetting(BPSetting setting)
	{
		super.setSetting(setting);
		m_params = setParamsFromSetting(new LinkedHashMap<String, Object>(), setting, true, false, SC_KEY_CMD, SC_KEY_DIR, SC_KEY_ENCODING);
	}

	public String getShortCutKey()
	{
		return SCKEY_CONSOLE;
	}

	protected String[] getParamKeys()
	{
		return new String[] { SC_KEY_CMD, SC_KEY_DIR, SC_KEY_ENCODING };
	}
}