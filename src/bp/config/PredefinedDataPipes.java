package bp.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import bp.BPCore;
import bp.config.BPConfigAdv.BPConfigAdvBase;
import bp.res.BPResourceIO;
import bp.util.IOUtil;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public class PredefinedDataPipes extends BPConfigAdvBase
{
	protected Consumer<? extends BPConfigAdv> m_loader = this::loadConfig;
	protected Consumer<? extends BPConfigAdv> m_persister = this::saveConfig;

	private final static String CFG_FILENAME = ".bppdps";

	protected static List<String[]> S_PDPS = new ArrayList<String[]>();

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
		List<String[]> pdps = new ArrayList<String[]>(S_PDPS);
		for (String[] pdp : pdps)
		{
			tm.put(pdp[0], pdp[1]);
		}
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
	}

	protected void setPMap(Map<String, ?> pmap)
	{
		List<String[]> pdps = new ArrayList<String[]>();
		for (String key : pmap.keySet())
		{
			String v = (String) pmap.get(key);
			if (v != null)
			{
				v = v.trim();
				if (v.length() > 0)
				{
					pdps.add(new String[] { key, v });
				}
			}
		}
		S_PDPS.clear();
		S_PDPS.addAll(pdps);
	}

	public Map<String, Object> getMappedData()
	{
		return new LinkedHashMap<String, Object>(m_map);
	}

	public static List<String[]> getDataPipes()
	{
		return new ArrayList<String[]>(S_PDPS);
	}
}