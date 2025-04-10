package bp.ui.shortcut;

import java.util.Map;
import java.util.function.BiConsumer;

import bp.config.BPSetting;

public interface BPShortCutFactory
{
	void register(BiConsumer<String, BPShortCutFactory> regfunc);

	BPShortCut createShortCut(String key);

	default BPShortCut makeShortCut(String name, String key, String[] params)
	{
		BPShortCut rc = createShortCut(key);
		if (rc != null)
			rc.setup(name, params);
		return rc;
	}

	default BPShortCut makeShortCut(String name, Map<String, Object> params)
	{
		String key = (String) params.get("key");
		BPShortCut rc = createShortCut(key);
		if (rc != null)
			rc.setup(name, params);
		return rc;
	}

	default BPSetting getSetting(String key)
	{
		BPShortCut sc = createShortCut(key);
		BPSetting rc = null;
		if (sc != null)
			rc = sc.getSetting();
		return rc;
	}
}
