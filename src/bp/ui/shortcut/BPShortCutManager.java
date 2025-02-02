package bp.ui.shortcut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import bp.config.BPSetting;
import bp.util.ClassUtil;

public class BPShortCutManager
{
	private final static Map<String, BPShortCutFactory> S_FACS = new ConcurrentHashMap<String, BPShortCutFactory>();

	public final static void init()
	{
		Map<String, BPShortCutFactory> newfacs = new HashMap<String, BPShortCutFactory>();
		ServiceLoader<BPShortCutFactory> facs = ClassUtil.getExtensionServices(BPShortCutFactory.class);
		for (BPShortCutFactory fac : facs)
		{
			fac.register((key, f) -> newfacs.put(key, f));
		}
		S_FACS.clear();
		S_FACS.putAll(newfacs);
	}

	public final static BPShortCutFactory getFactory(String key)
	{
		return S_FACS.get(key);
	}

	public final static BPSetting getSetting(String key)
	{
		BPSetting rc = null;
		BPShortCutFactory fac = S_FACS.get(key);
		if (fac != null)
		{
			rc = fac.getSetting(key);
		}
		return rc;
	}

	public final static boolean runShortCut(String[] sc)
	{
		BPShortCut bsc = makeShortCut(sc);
		if (bsc != null)
		{
			return bsc.run();
		}
		return false;
	}

	public final static BPShortCut makeShortCut(String[] sc)
	{
		String name = sc[0];
		String fackey = sc[1];
		BPShortCutFactory fac = S_FACS.get(fackey);
		BPShortCut rc = null;
		if (fac != null)
		{
			String[] params = new String[sc.length - 2];
			if (sc.length > 2)
			{
				System.arraycopy(sc, 2, params, 0, sc.length - 2);
			}
			rc = fac.makeShortCut(name, fackey, params);
		}
		return rc;
	}

	public final static List<String> getFactoryKeys()
	{
		return new ArrayList<String>(S_FACS.keySet());
	}
}
