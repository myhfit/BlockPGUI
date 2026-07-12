package bp.ui.form.dynamic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bp.data.BPMData;
import bp.util.DateUtil;
import bp.util.ObjUtil;

public class BPFormItemDataChecker implements BPMData
{
	protected String m_type;
	protected Map<String, Object> m_params;

	public void setMappedData(Map<String, Object> data)
	{
		m_params = new HashMap<String, Object>(data);
		m_type = (String) m_params.remove("ctype");
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("ctype", m_type);
		rc.putAll(m_params);
		return rc;
	}

	public boolean check(String key, Object value, Map<String, Object> data, BPFormContext context)
	{
		String ct = m_type;
		switch (ct)
		{
			case "isint":
			case "islong":
			case "isfloat":
			case "isdouble":
			case "istime":
			{
				String sv = (value != null && value instanceof String) ? ((String) value).trim() : null;
				switch (ct)
				{
					case "isint":
					{
						if (value instanceof String)
							return ObjUtil.check(() -> Integer.parseInt(sv));
						else if (value instanceof Number)
						{
							long l = ((Number) value).longValue();
							return l == ((int) l);
						}
						return true;
					}
					case "islong":
					{
						if (value instanceof String)
							return ObjUtil.check(() -> Integer.parseInt(sv));
						return true;
					}
					case "isfloat":
					{
						if (value instanceof String)
							return ObjUtil.check(() -> Float.parseFloat(sv));
						return true;
					}
					case "isdouble":
					{
						if (value instanceof String)
							return ObjUtil.check(() -> Double.parseDouble(sv));
						return true;
					}
					case "istime":
					{
						if (value instanceof String)
							return DateUtil.parseTime(sv) != -1;
						else if (value instanceof Date)
							return true;
						else if (value instanceof Long)
							return true;
						return false;
					}
				}
				break;
			}
			case "controller":
			{
				return context.controller.validateValue(key, context);
			}
		}
		return true;
	}

	public final static boolean check(String key, List<Map<String, Object>> params, Object value, Map<String, Object> data, BPFormContext context)
	{
		if (params == null || params.size() == 0)
			return true;
		BPFormItemDataChecker c = new BPFormItemDataChecker();
		for (Map<String, Object> param : params)
		{
			c.setMappedData(param);
			if (!c.check(key, value, data, context))
				return false;
		}
		return true;
	}
}
