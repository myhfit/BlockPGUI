package bp.ui.shortcut;

import java.util.ArrayList;
import java.util.List;

import bp.BPGUICore;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.format.BPFormat;
import bp.format.BPFormatManager;
import bp.ui.editor.BPEditorFactory;
import bp.ui.editor.BPEditorManager;

public class BPShortCutEditor extends BPShortCutBase
{
	public boolean run()
	{
		String[] params = m_params;
		String editorname = params[0];
		String filename = null;
		String format = null;
		if (params.length > 1)
		{
			filename = params[1];
			if (filename != null && filename.length() == 0)
				filename = null;
		}
		if (params.length > 2)
		{
			format = params[2];
			if (format != null && format.length() == 0)
				format = null;
		}
		String pfilename = filename;
		String pformat = format;
		BPGUICore.runOnMainFrame(mf -> mf.openEditorByFileSystem(pfilename, pformat, editorname, null));
		return true;
	}

	public BPSetting getSetting()
	{
		BPSettingBase rc = (BPSettingBase) super.getSetting();
		List<BPEditorFactory> facs = BPEditorManager.getAllFactories();
		List<String> keys = new ArrayList<String>();
		for (BPEditorFactory fac : facs)
		{
			String facname=fac.getName();
			if(!keys.contains(facname))
				keys.add(facname);
		}
		List<BPFormat> formats = BPFormatManager.getFormats();
		List<String> formatstrs = new ArrayList<String>();
		for (BPFormat format : formats)
			formatstrs.add(format.getName());
		rc.addItem(BPSettingItem.create("editorname", "Editor Name", BPSettingItem.ITEM_TYPE_SELECT, keys.toArray(new String[keys.size()])).setRequired(true));
		rc.addItem(BPSettingItem.create("filename", "File", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.addItem(BPSettingItem.create("format", "Format", BPSettingItem.ITEM_TYPE_SELECT, formatstrs.toArray(new String[formatstrs.size()])));

		rc.set("editorname", getParamValue(0));
		rc.set("filename", getParamValue(1));
		rc.set("format", getParamValue(2));
		return rc;
	}

	public void setSetting(BPSetting setting)
	{
		super.setSetting(setting);
		String editorname = setting.get("editorname");
		if (editorname == null)
			editorname = "";
		String filename = setting.get("filename");
		if (filename == null)
			filename = "";
		String format = setting.get("format");
		if (format == null)
			format = "";
		m_params = new String[] { editorname, filename, format };
	}
}
