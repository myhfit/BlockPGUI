package bp.ui.laf;

import bp.ui.util.SystemUIUtil;

@SuppressWarnings("serial")
public class MetalLookAndFeel_Dark extends MetalLookAndFeelCustomized
{
	public MetalLookAndFeel_Dark()
	{
		super();
		setCurrentTheme(new DarkMetalTheme());
		SystemUIUtil.setByDarkTheme(true);
	}
}
