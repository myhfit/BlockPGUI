package bp.ui.laf;

import javax.swing.plaf.metal.MetalLookAndFeel;

@SuppressWarnings("serial")
public class MetalLookAndFeel_White extends MetalLookAndFeel
{
	public MetalLookAndFeel_White()
	{
		super();
		setCurrentTheme(new WhiteMetalTheme());
	}
}
