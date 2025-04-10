package bp.ui.shortcut;

import java.util.Map;

import bp.config.BPSetting;
import bp.data.BPMData;

public interface BPShortCut extends BPMData
{
	String getName();

	String getShortCutKey();

	boolean run();

	default BPSetting getSetting()
	{
		return null;
	}

	void setSetting(BPSetting setting);

	void setup(String name, String[] params);

	void setup(String name, Map<String, Object> params);

	Map<String, Object> getMappedDataWithKey();
}
