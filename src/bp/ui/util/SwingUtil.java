package bp.ui.util;

import java.awt.GraphicsEnvironment;

import sun.awt.DisplayChangedListener;

public class SwingUtil
{
	private static volatile boolean s_bi;

	public final static void refreshDisplay(GraphicsEnvironment ge)
	{
		if (s_bi)
			return;
		try
		{
			if (ge instanceof DisplayChangedListener)
				((DisplayChangedListener) ge).displayChanged();
		}
		catch (IllegalAccessError e)
		{
			s_bi = true;
		}
	}
}
