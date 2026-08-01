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
		BPShortCut rc = createShortCut((String) params.get("key"));
		if (rc != null)
			rc.setup(name, params);
		return rc;
	}

	default BPSetting getSetting(String key)
	{
		BPShortCut sc = createShortCut(key);
		return sc != null ? sc.getSetting() : null;
	}

	default boolean canExpand(String key)
	{
		return false;
	}
}
