package bp.ui.shortcut;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import bp.BPGUICore;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.format.BPFormat;
import bp.format.BPFormatManager;
import bp.ui.editor.BPEditorFactory;
import bp.ui.editor.BPEditorManager;
import bp.ui.util.CommonUIOperations;
import bp.util.TextUtil;

public class BPShortCutEditor extends BPShortCutBase
{
	public final static String SCKEY_EDITOR = "Editor";

	protected final static String SC_KEY_EDITORNAME = "editorname";
	protected final static String SC_KEY_FILENAME = "filename";
	protected final static String SC_KEY_FORMAT = "format";

	public boolean run()
	{
		String editorname = TextUtil.eds(getParam(SC_KEY_EDITORNAME));
		String filename = TextUtil.eds(getParam(SC_KEY_FILENAME));
		String format = TextUtil.eds(getParam(SC_KEY_FORMAT));

		if (editorname != null)
		{
			if (!BPGUICore.execOnMainFrame(mf -> mf.isVisible()))
				CommonUIOperations.openFileNewWindow(filename, format, editorname, null);
			else
				BPGUICore.runOnMainFrame(mf -> mf.openEditorByFileSystem(filename, format, editorname, null));
			return true;
		}
		return false;
	}

	public BPSetting getSetting()
	{
		BPSettingBase rc = (BPSettingBase) super.getSetting();
		List<BPEditorFactory> facs = BPEditorManager.getAllFactories();
		List<String> keys = new ArrayList<String>();
		for (BPEditorFactory fac : facs)
		{
			String facname = fac.getName();
			if (!keys.contains(facname))
				keys.add(facname);
		}
		List<BPFormat> formats = BPFormatManager.getFormats();
		List<String> formatstrs = new ArrayList<String>();
		for (BPFormat format : formats)
			formatstrs.add(format.getName());
		rc.addItem(BPSettingItem.create(SC_KEY_EDITORNAME, "Editor Name", BPSettingItem.ITEM_TYPE_SELECT, keys.toArray(new String[keys.size()])).setRequired(true));
		rc.addItem(BPSettingItem.create(SC_KEY_FILENAME, "File", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.addItem(BPSettingItem.create(SC_KEY_FORMAT, "Format", BPSettingItem.ITEM_TYPE_SELECT, formatstrs.toArray(new String[formatstrs.size()])));

		rc.setAll(m_params);
		return rc;
	}

	public void setSetting(BPSetting setting)
	{
		super.setSetting(setting);
		m_params = setParamsFromSetting(new LinkedHashMap<String, Object>(), setting, true, false, SC_KEY_EDITORNAME, SC_KEY_FILENAME, SC_KEY_FORMAT);
	}

	public String getShortCutKey()
	{
		return SCKEY_EDITOR;
	}

	protected String[] getParamKeys()
	{
		return new String[] { SC_KEY_EDITORNAME, SC_KEY_FILENAME, SC_KEY_FORMAT };
	}
}
