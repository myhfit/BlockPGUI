package bp.ui.form.dynamic;

import bp.util.DateUtil;
import bp.util.ObjUtil;

public enum BPFormItemDataFormatters
{
	TO_LONG, TO_INT, TO_STR, FORMAT_TIME, PARSE_TIME, FMT;

	public Object format(Object v, Object params)
	{
		switch (this)
		{
			case FORMAT_TIME:
				return v != null ? DateUtil.formatTime((Long) v, params == null ? DateUtil.DEFAULT_FORMAT : (String) params) : null;
			case PARSE_TIME:
				return v != null ? DateUtil.parseTime((String) v, params == null ? DateUtil.DEFAULT_FORMAT : (String) params) : null;
			case TO_INT:
				return ObjUtil.toInt(v, params == null ? null : ObjUtil.toInt(params, null));
			case TO_LONG:
				return ObjUtil.toLong(v, params == null ? null : ObjUtil.toLong(params, null));
			case TO_STR:
				return ObjUtil.toString(v);
			case FMT:
				return String.format((String) params, v);
		}
		return null;
	}
}
