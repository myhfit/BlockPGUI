package bp.ui.shortcut;

import bp.config.BPSetting;

public interface BPShortCut
{
	String getName();

	boolean run();

	void setParams(String[] params);

	String[] getParams();

	default BPSetting getSetting()
	{
		return null;
	}
	
	void setSetting(BPSetting setting);

	void setup(String name, String[] params);
}
