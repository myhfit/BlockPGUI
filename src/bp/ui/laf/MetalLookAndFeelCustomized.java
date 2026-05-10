package bp.ui.laf;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.UIDefaults;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

@SuppressWarnings("serial")
public abstract class MetalLookAndFeelCustomized extends MetalLookAndFeel
{
	protected void initComponentDefaults(UIDefaults table)
	{
		super.initComponentDefaults(table);
		table.put("CheckBox.icon", new BPCheckBoxIcon());
	}

//	public void initialize()
//	{
//		super.initialize();
//		if ("WLToolkit".equals(System.getProperty("awt.toolkit.name")))
//			putAATextInfo(UIManager.getLookAndFeelDefaults());
//	}
//
//	@SuppressWarnings("unchecked")
//	private void putAATextInfo(UIDefaults defaults)
//	{
//		Std.info("aaa");
//		defaults.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		Object desktopHints = Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
//		if (desktopHints == null)
//			desktopHints = getFallbackHints();
//		if (desktopHints != null && desktopHints instanceof Map)
//		{
//			Map<Object, Object> hints = (Map<Object, Object>) desktopHints;
//			Object aaHint = hints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
//			if (aaHint != null && aaHint != RenderingHints.VALUE_TEXT_ANTIALIAS_OFF && aaHint != RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT)
//			{
//				defaults.put(RenderingHints.KEY_TEXT_ANTIALIASING, aaHint);
//				defaults.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, hints.get(RenderingHints.KEY_TEXT_LCD_CONTRAST));
//			}
//		}
////		try
////		{
////			Object key = Class.forName("sun.swing.SwingUtilities2").getField("AA_TEXT_PROPERTY_KEY").get(null);
////			Object value = Class.forName("sun.swing.SwingUtilities2$AATextInfo").getMethod("getAATextInfo", new Class[] { boolean.class }).invoke(null, new Object[] { Boolean.valueOf(true) });
////			if (value == null)
////				value = getFallbackHints();
////			defaults.put(key, value);
////		}
////		catch (Exception ex)
////		{
////			ex.printStackTrace();
////			Std.info(getDescription());
////		}
//	}
//
//	private Map<Object, Object> getFallbackHints()
//	{
//		Std.info("bbb");
//		if (System.getProperty("awt.useSystemAAFontSettings") != null)
//			return null;
//		Std.info("bbb2");
//		Map<Object, Object> rc = new HashMap<>();
//		Object hint = null;
//		if (SystemUtil.getOS() == SystemOS.Linux)
//		{
//			Toolkit toolkit = Toolkit.getDefaultToolkit();
//			if (toolkit.getDesktopProperty("gnome.Xft/Antialias") == null && toolkit.getDesktopProperty("fontconfig/Antialias") == null)
//				hint = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
//			Std.info("bbb3");
//			if (hint != null)
//			{
//				rc = new HashMap<>();
//				rc.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//				rc.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.KEY_TEXT_LCD_CONTRAST);
//				Std.info("bbb3");
//			}
//		}
//		return rc;
//	}

	private static class BPCheckBoxIcon implements Icon, UIResource, Serializable
	{
		protected int getControlSize()
		{
			return 13;
		}

		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			ButtonModel model = ((JCheckBox) c).getModel();
			int controlSize = getControlSize();

			if (model.isEnabled())
			{
				drawBorder(g, x, y, controlSize, controlSize);
				g.setColor(c.getForeground());
			}
			else
			{
				g.setColor(MetalLookAndFeel.getControlShadow());
				g.drawRect(x, y, controlSize - 2, controlSize - 2);
			}

			if (model.isSelected())
			{
				drawCheck(c, g, x, y);
			}

		}

		static void drawBorder(Graphics g, int x, int y, int w, int h)
		{
			g.translate(x, y);

			g.setColor(getWhite());
			g.fillRect(1, 1, w - 2, h - 2);
			g.setColor(MetalLookAndFeel.getControlDarkShadow());
			g.drawRect(0, 0, w - 1, h - 1);
			g.translate(-x, -y);
		}

		protected void drawCheck(Component c, Graphics g, int x, int y)
		{
			int controlSize = getControlSize();
			int csx = controlSize - 3;
			int csy1 = controlSize - 6;
			int csy2 = controlSize - 4;
			int csy3 = controlSize - 3;
			int[] xPoints = { 3, 5, 5, csx, csx, 5, 5, 3 };
			int[] yPoints = { 5, 5, csy1, 2, 4, csy2, csy3, csy3 };
			g.translate(x, y);
			g.fillPolygon(xPoints, yPoints, 8);
			g.translate(-x, -y);
		}

		public int getIconWidth()
		{
			return getControlSize();
		}

		public int getIconHeight()
		{
			return getControlSize();
		}
	}
}
