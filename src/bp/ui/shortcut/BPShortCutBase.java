package bp.ui.shortcut;

import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;

public abstract class BPShortCutBase implements BPShortCut
{
	protected String m_name;
	protected String[] m_params;

	public void setup(String name, String[] params)
	{
		m_name = name;
		setParams(params);
	}

	public String getName()
	{
		return m_name;
	}

	public void setParams(String[] params)
	{
		m_params = params;
	}

	public String[] getParams()
	{
		return m_params;
	}

	public BPSetting getSetting()
	{
		BPSettingBase rc = new BPSettingBase();
		rc.addItem(BPSettingItem.create("name", "Name", BPSettingItem.ITEM_TYPE_TEXT, null).setRequired(true));

		rc.set("name", m_name);
		return rc;
	}

	public void setSetting(BPSetting setting)
	{
		m_name = setting.get("name");
	}

	public String getParamValue(int i)
	{
		if (m_params == null)
			return null;
		if (m_params.length <= i)
			return null;
		return m_params[i];
	}
}
