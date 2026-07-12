package bp.ui.form.dynamic;

import java.awt.Component;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bp.data.BPDataWrapper;
import bp.data.BPMData;
import bp.typeext.Traversable;
import bp.ui.form.BPFormPanel;
import bp.ui.form.dynamic.controller.BPFormController;
import bp.util.ClassUtil;
import bp.util.IOUtil;
import bp.util.JSONUtil;
import bp.util.ObjUtil;
import bp.util.Std;
import bp.util.TextUtil;

public class BPFormContext implements Traversable, BPMData
{
	public boolean readonly;
	public Boolean editable;
	public boolean fullctrl;
	public Integer labelwidth;
	public String tr;

	public List<BPFormItemDef> itemdefs;
	public Map<String, BPFormItemDef> itemdefmap;
	public List<BPFormItem> items;
	public boolean gridweakborder;
	public BPFormController controller;
	public int lineheight;

	public Map<String, Object> snapshot;
	public boolean noscroll;

	public BPFormContext()
	{
	}

	public Iterable<Traversable> getChildren()
	{
		return items == null ? null : new ArrayList<Traversable>(items);
	}

	public boolean visitSelf()
	{
		return false;
	}

	public boolean isEditable()
	{
		return (!readonly) && (editable == null ? true : editable);
	}

	public void loadConfig(String relpath, Class<?> leaf, Class<?> root)
	{
		Set<String> checker = new HashSet<String>();
		loadConfig(ClassUtil.tryLoopSuperClass(cls -> (cls != Object.class) ? getConfig(relpath, cls.getName().replace('.', '/'), checker) : null, leaf, root));
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> getConfig(String relpath, String key, Set<String> checker)
	{
		Map<String, Object> rc = null;
		try (InputStream in = ClassUtil.getExtensionClassLoader().getResourceAsStream("bp/ui/form/config/" + key + ".json"))
		{
			if (in != null)
				rc = JSONUtil.decode(TextUtil.toString(IOUtil.read(in), "utf-8"));
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		checker.add(key);
		if (rc != null)
		{
			String par = (String) rc.get("parent");
			if (par != null)
			{
				Map<String, Object> parcfg = getConfig(relpath, par.replace('.', '/'), checker);
				if (parcfg != null)
				{
					List<Map<String, Object>> items = null;
					for (String k : rc.keySet())
					{
						if ("items".equals(k))
						{
							items = (List<Map<String, Object>>) rc.get(k);
							continue;
						}
						else
						{
							parcfg.put(k, rc.get(k));
						}
					}
					if (items != null)
						parcfg.put("items", mergeItems((List<Map<String, Object>>) parcfg.get("items"), items));
					rc = parcfg;
				}
			}
		}
		return rc;
	}

	protected final static List<Map<String, Object>> mergeItems(List<Map<String, Object>> sources, List<Map<String, Object>> mods)
	{
		List<Map<String, Object>> rc = new ArrayList<Map<String, Object>>();
		Map<String, Map<String, Object>> itemmap = new HashMap<String, Map<String, Object>>();
		for (Map<String, Object> s : sources)
		{
			rc.add(s);
			String key = (String) s.get("key");
			if (key != null)
				itemmap.put(key, s);
		}
		for (Map<String, Object> mod : mods)
		{
			String m = (String) mod.get("modtype");
			if (m != null)
			{
				String key = (String) mod.get("key");
				Map<String, Object> old = itemmap.get(key);
				if (old != null)
				{
					switch (m)
					{
						case "update":
						{
							Map<String, Object> newmap = new HashMap<String, Object>(old);
							newmap.putAll(mod);
							newmap.remove("modtype");
							rc.set(rc.indexOf(old), newmap);
							break;
						}
						case "replace":
						{
							mod.remove("modtype");
							rc.set(rc.indexOf(old), mod);
							break;
						}
						case "remove":
						{
							rc.remove(old);
							break;
						}
						default:
						{
							mod.remove("modtype");
							rc.add(mod);
						}
					}
				}
			}
			else
			{
				rc.add(mod);
			}
		}
		return rc;
	}

	@SuppressWarnings("unchecked")
	protected void loadConfig(Map<String, Object> config)
	{
		if (config == null)
			return;
		setMappedData(config);
		List<Map<String, Object>> itemcfgs = (List<Map<String, Object>>) config.get("items");
		List<BPFormItemDef> defs = new ArrayList<BPFormItemDef>();
		Map<String, BPFormItemDef> defmap = new HashMap<String, BPFormItemDef>();
		for (Map<String, Object> cfg : itemcfgs)
		{
			BPFormItemDef def = BPFormItemDef.createByConfig(cfg);
			defs.add(def);
			defmap.put(def.key, def);
		}
		itemdefmap = defmap;
		itemdefs = defs;
	}

	public void addItemDef(BPFormItemDef def)
	{
		itemdefs.add(def);
		itemdefmap.put(def.key, def);
	}

	public void createItemDefs(String[] keys, String itemtype, boolean readonly)
	{
		itemdefs = new ArrayList<BPFormItemDef>();
		itemdefmap = new HashMap<>();
		for (String key : keys)
			addItemDef(BPFormItemDef.createSimple(key, itemtype, readonly));
	}

	public void setMappedData(Map<String, Object> data)
	{
		readonly = ObjUtil.toBool(data.get("readonly"), false);
		noscroll = ObjUtil.toBool(data.get("noscroll"), false);
		labelwidth = ObjUtil.toInt(data.get("labelwidth"), null);
		tr = (String) data.get("tr");
		String ctlcls = (String) data.get("controller");
		if (ctlcls != null)
		{
			ctlcls = BPFormController.class.getPackage().getName() + "." + ctlcls;
			controller = ClassUtil.createObject(ClassUtil.getEClass(ctlcls), BPFormController.class, null, null);
		}
	}

	public BPFormItem findItem(String key)
	{
		BPDataWrapper<BPFormItem> w = new BPDataWrapper<BPFormItem>(null);
		traverseFind((n, m) ->
		{
			if (key.equals(((BPFormItem) n).getDefine().key))
			{
				m.set((BPFormItem) n);
				return true;
			}
			return false;
		}, w);
		return w.get();
	}

	public void initByData(Map<String, ?> data)
	{
		if (controller != null)
			controller.initSnapshot(data, this);
	}

	public void showData(Map<String, ?> data, boolean editable, BPFormPanel fp)
	{
		if (controller != null)
		{
			if (controller.showData(data, editable, this))
				initUI(fp, true);
		}
	}

	public void initUI(BPFormPanel fp,boolean needclear)
	{
		if (needclear)
			fp.clearForm();
		if (labelwidth != null)
			fp.setLabelWidth(labelwidth);
		List<BPFormItem> newitems = new ArrayList<BPFormItem>();
		if (itemdefs != null)
		{
			for (BPFormItemDef itemdef : itemdefs)
			{
				BPFormItem item = createItem(itemdef, this,fp);
				newitems.add(item);
			}
			items = newitems;

			for (BPFormItem item : newitems)
				item.initComponent(this);
		}
	}

	protected BPFormItem createItem(BPFormItemDef itemdef, BPFormContext context,BPFormPanel fp)
	{
		BPFormItem rc = BPFormItems.createItem(itemdef, context);
		Component comp = rc.getSTComponent();
		if (comp != null)
		{
			if (rc.noLabel())
				fp.doAddLineComponents(null, itemdef.hasLineBorder(), rc.getLineHeight(context), comp);
			else
				fp.addLine(new String[] { rc.getLabel() }, new Component[] { comp }, rc.getDefine().required, null);
		}
		return rc;
	}

	public Object controlSetValue(Object v, BPFormItem item)
	{
		return controller.controlSetValue(v, this, item);
	}

	public Object controlGetValue(Object v, BPFormItem item)
	{
		return controller.controlGetValue(v, this, item);
	}
}
