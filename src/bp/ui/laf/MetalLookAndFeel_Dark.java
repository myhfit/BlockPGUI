package bp.ui.laf;

import javax.swing.plaf.metal.MetalLookAndFeel;

import bp.ui.util.SystemUIUtil;

@SuppressWarnings("serial")
public class MetalLookAndFeel_Dark extends MetalLookAndFeel 
{
	public MetalLookAndFeel_Dark()
	{
		super();
		setCurrentTheme(new DarkMetalTheme());
		SystemUIUtil.setByDarkTheme(true);
	}
}
