package bp.ui.laf;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

public class WhiteMetalTheme extends DefaultMetalTheme
{
	private final static float h1 = 0.553f;
	private final static float h2 = 0.753f;
	private final static float h3 = 0.953f;
	private final static float b1 = 0.5f;
	private final static float b2 = 0.7f;
	private final static float b3 = 0.9f;

	private final static ColorUIResource p1 = new ColorUIResource(b1, b1, b1);
	private final static ColorUIResource p2 = new ColorUIResource(b2, b2, b2);
	private final static ColorUIResource p3 = new ColorUIResource(b3 - 0.1f, b3, b3 + 0.1f);
	private final static ColorUIResource menubg = new ColorUIResource(b3 - 0.1f, b3, b3 + 0.1f);
	private final static ColorUIResource s1 = new ColorUIResource(h1, h1, h1);
	private final static ColorUIResource s2 = new ColorUIResource(h2, h2, h2);
	private final static ColorUIResource s3 = new ColorUIResource(h3, h3, h3);

	public final static ColorUIResource b = new ColorUIResource(0f, 0f, 0f);
	public final static ColorUIResource w = new ColorUIResource(1f, 1f, 1f);

	public String getName()
	{
		return "White";
	}

	public ColorUIResource getMenuSelectedBackground()
	{
		return menubg;
	}

	protected ColorUIResource getPrimary1()
	{
		return p1;
	}

	protected ColorUIResource getPrimary2()
	{
		return p2;
	}

	protected ColorUIResource getPrimary3()
	{
		return p3;
	}

	protected ColorUIResource getSecondary1()
	{
		return s1;
	}

	protected ColorUIResource getSecondary2()
	{
		return s2;
	}

	protected ColorUIResource getSecondary3()
	{
		return s3;
	}

	protected ColorUIResource getWhite()
	{
		return w;
	}

	protected ColorUIResource getBlack()
	{
		return b;
	}
}
