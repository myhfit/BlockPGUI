package bp.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import bp.BPCore;
import bp.BPGUICore;
import bp.config.BPConfigAdv.BPConfigAdvBase;
import bp.res.BPResourceIO;
import bp.ui.shortcut.BPShortCutManager;
import bp.util.IOUtil;
import bp.util.JSONUtil;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public class ShortCuts extends BPConfigAdvBase
{
	protected Consumer<? extends BPConfigAdv> m_loader = this::loadConfig;
	protected Consumer<? extends BPConfigAdv> m_persister = this::saveConfig;

	private final static String CFG_FILENAME = ".bpscs";

	protected static List<ShortCutData> S_SCS = new ArrayList<ShortCutData>();

	public boolean canUserConfig()
	{
		return true;
	}

	@SuppressWarnings("unchecked")
	public <S extends BPConfigAdv> Consumer<S> getConfigLoader()
	{
		return (Consumer<S>) m_loader;
	}

	public <S extends BPConfigAdv> void setConfigLoader(Consumer<S> loader)
	{
		m_loader = loader;
	}

	@SuppressWarnings("unchecked")
	public <S extends BPConfigAdv> Consumer<S> getConfigPersister()
	{
		return (Consumer<S>) m_persister;
	}

	public <S extends BPConfigAdv> void setConfigPersister(Consumer<S> persister)
	{
		m_persister = persister;
	}

	protected Map<String, Object> createMap()
	{
		return new LinkedHashMap<String, Object>();
	}

	protected void loadConfig(BPConfigAdv config)
	{
		BPShortCutManager.init();
		byte[] bs = IOUtil.read(BPCore.getWorkspaceContext().getConfigRes(CFG_FILENAME));
		Map<String, String> pmap = new LinkedHashMap<String, String>();
		if (bs != null)
		{
			String str = TextUtil.toString(bs, "utf-8");
			pmap = TextUtil.getPlainMap(str, true);
		}
		m_map.clear();
		m_map.putAll(pmap);

		setPMap(pmap);
	}

	protected void setPMap(Map<String, ?> pmap)
	{
		List<ShortCutData> scs = new ArrayList<ShortCutData>();
		for (String key : pmap.keySet())
		{
			String v = (String) pmap.get(key);
			if (v != null)
			{
				v = v.trim();
				if (v.length() > 0)
				{
					Object vs = null;
					if (v.startsWith("["))
					{
						List<String> strs = JSONUtil.decode(v);
						vs = strs.toArray(new String[strs.size()]);
					}
					else if (v.startsWith("{"))
					{
						Map<String, Object> newvs = new LinkedHashMap<String, Object>();
						Map<String, Object> newps = JSONUtil.decode(v);
						newvs.put("key", newps.get("key"));
						for (String pkey : newps.keySet())
						{
							if (!("key".equals(pkey)))
							{
								newvs.put(pkey, newps.get(pkey));
							}
						}
						vs = newvs;
					}
					else
					{
						vs = TextUtil.splitEscapePlainText(v);
					}
					ShortCutData sc = new ShortCutData();
					sc.name = key;
					sc.values = vs;
					scs.add(sc);
				}
			}
		}
		S_SCS.clear();
		S_SCS.addAll(scs);
	}

	protected void saveConfig(BPConfigAdv config)
	{
		BPResourceIO cfgres = BPCore.getWorkspaceContext().getConfigRes(CFG_FILENAME, true);
		if (m_map.size() == 0)
		{
			if (cfgres == null)
				return;
		}
		else if (cfgres == null)
		{
			cfgres = BPCore.getWorkspaceContext().getConfigRes(CFG_FILENAME, false);
		}
		Map<String, String> tm = new LinkedHashMap<String, String>();
		List<ShortCutData> scs = new ArrayList<ShortCutData>(S_SCS);
		for (ShortCutData sc : scs)
			tm.put(sc.name, JSONUtil.encode(sc.values));
		byte[] bs = TextUtil.fromString(TextUtil.fromPlainMap(ObjUtil.toPlainMap(tm, true), null), "utf-8");
		if (bs != null)
		{
			IOUtil.write(cfgres, bs);
		}
	}

	public void setMappedData(Map<String, Object> data)
	{
		super.setMappedData(data);
		setPMap(data);
		BPGUICore.runOnMainFrame(mf -> mf.refreshShortCuts());
	}

	public Map<String, Object> getMappedData()
	{
		return new LinkedHashMap<String, Object>(m_map);
	}

	public static List<ShortCutData> getShortCuts()
	{
		return new ArrayList<ShortCutData>(S_SCS);
	}

	public static ShortCutData getShortCut(String name)
	{
		List<ShortCutData> scs = new ArrayList<ShortCutData>(S_SCS);
		for (ShortCutData sc : scs)
		{
			if (name.equals(sc.name))
				return sc;
		}
		return null;
	}

	public static class ShortCutData
	{
		public String name;
		public Object values;

		public ShortCutData()
		{

		}

		public ShortCutData(String name, Object values)
		{
			this.name = name;
			this.values = values;
		}
	}
}
