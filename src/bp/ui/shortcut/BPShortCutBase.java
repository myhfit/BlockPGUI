package bp.ui.shortcut;

import java.util.LinkedHashMap;
import java.util.Map;

import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.util.TextUtil;

public abstract class BPShortCutBase implements BPShortCut
{
	protected String m_name;
	protected Map<String, Object> m_params;

	public void setup(String name, String[] params)
	{
		m_name = name;
		if (params != null)
		{
			int l = params.length;
			Map<String, Object> ps = new LinkedHashMap<String, Object>();
			String[] keys = getParamKeys();
			l = Math.min(l, keys == null ? 0 : keys.length);
			for (int i = 0; i < l; i++)
			{
				String k = keys[i];
				if (k != null)
				{
					String v = params[i];
					if (v != null && v.length() > 0)
						ps.put(k, v);
				}
			}
			m_params = ps;
		}
	}

	protected abstract String[] getParamKeys();

	public void setup(String name, Map<String, Object> params)
	{
		m_name = name;
		setMappedData(params);
	}

	public String getName()
	{
		return m_name;
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

	protected final static Map<String, Object> setParamsFromSetting(Map<String, Object> params, BPSetting setting, boolean noempty, boolean writewhennull, String... keys)
	{
		for (String k : keys)
		{
			String v = setting.get(k);
			if (noempty)
				v = TextUtil.eds(v);
			if (writewhennull || v != null)
				params.put(k, v);
		}
		return params;
	}

	public <T> T getParam(String key)
	{
		return getParam(key, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T getParam(String key, T defaultvalue)
	{
		T rc = null;
		if (m_params != null)
		{
			rc = (T) m_params.get(key);
			if (rc == null)
				rc = defaultvalue;
		}
		return rc;
	}

	public Map<String, Object> getMappedData()
	{
		return new LinkedHashMap<String, Object>(m_params);
	}

	public Map<String, Object> getMappedDataWithKey()
	{
		Map<String, Object> rc = new LinkedHashMap<String, Object>();
		rc.put("key", getShortCutKey());
		rc.putAll(m_params);
		return rc;
	}

	public void setMappedData(Map<String, Object> data)
	{
		Map<String, Object> ps = new LinkedHashMap<String, Object>(data);
		ps.remove("key");
		m_params = ps;
	}
}
