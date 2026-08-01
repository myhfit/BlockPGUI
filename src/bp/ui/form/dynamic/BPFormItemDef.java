package bp.ui.form.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bp.data.BPMData;
import bp.typeext.Traversable;
import bp.util.ObjUtil;

public class BPFormItemDef implements Traversable, BPMData
{
	public String key;
	public Object writekey;
	public Object readkey;
	public String label;
	public String lbltr;
	public String itemtype;
	public boolean nowriteempty;
	public boolean required;
	public boolean readonly;
	public Boolean readonly_edit;
	public Map<String, Object> params;
	public List<BPFormItemDef> children;
	public Object defaultvalue;

	protected final static Object NULL_KEY = new Object();

	public boolean convertEmptyToNull()
	{
		if (params == null)
			return true;
		return !ObjUtil.toBool(params.get("canempty"), false);
	}

	public Iterable<Traversable> getChildren()
	{
		return new ArrayList<Traversable>(children);
	}

	public String getLabel()
	{
		return label == null ? key : label;
	}

	@SuppressWarnings("unchecked")
	public static BPFormItemDef createByConfig(Map<String, Object> cfg)
	{
		BPFormItemDef rc = new BPFormItemDef();
		rc.setMappedData(cfg);
		List<Map<String, Object>> chdcfgs = (List<Map<String, Object>>) cfg.get("children");
		if (chdcfgs != null)
		{
			List<BPFormItemDef> chds = new ArrayList<BPFormItemDef>();
			for (Map<String, Object> chdcfg : chdcfgs)
				chds.add(createByConfig(chdcfg));
			rc.children = chds;
		}
		return rc;
	}

	public Map<String, Object> getMappedData()
	{
		return new HashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	public void setMappedData(Map<String, Object> data)
	{
		itemtype = (String) data.get("itemtype");
		key = (String) data.get("key");
		if (data.containsKey("writekey"))
		{
			writekey = data.get("writekey");
			if (writekey == null)
				writekey = NULL_KEY;
		}
		if (data.containsKey("readkey"))
		{
			readkey = data.get("readkey");
			if (readkey == null)
				readkey = NULL_KEY;
		}
		nowriteempty = ObjUtil.toBool(data.get("nowriteempty"), false);
		readkey = (String) data.get("readkey");
		label = (String) data.get("label");
		lbltr = (String) data.get("lbltr");
		required = ObjUtil.toBool(data.get("required"), false);
		readonly = ObjUtil.toBool(data.get("readonly"), false);
		readonly_edit = ObjUtil.toBool(data.get("readonly_edit"), null);
		params = (Map<String, Object>) data.get("params");
		defaultvalue = data.get("defaultvalue");
	}

	public String getWriteKey()
	{
		return writekey != null ? (writekey == NULL_KEY ? null : (String) writekey) : key;
	}

	public String getReadKey()
	{
		return readkey != null ? (readkey == NULL_KEY ? null : (String) readkey) : key;
	}

	@SuppressWarnings("unchecked")
	public <T> T getParam(String key)
	{
		return params == null ? null : (T) params.get(key);
	}

	public boolean isReadOnlyOnEdit()
	{
		return readonly_edit != null ? readonly_edit : readonly;
	}

	public boolean hasLineBorder()
	{
		return !ObjUtil.toBool(getParam("nolineborder"), false);
	}

	public final static BPFormItemDef createSimple(String key, String itemtype, boolean readonly)
	{
		BPFormItemDef rc = new BPFormItemDef();
		rc.key = key;
		rc.itemtype = itemtype;
		rc.readonly = readonly;
		rc.label = key;
		return rc;
	}
}