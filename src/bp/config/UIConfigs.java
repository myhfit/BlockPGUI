package bp.config;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import bp.BPCore;
import bp.config.BPConfigAdv.BPConfigAdvBase;
import bp.res.BPResourceIO;
import bp.ui.util.UIUtil;
import bp.util.IOUtil;
import bp.util.MIF;
import bp.util.ObjUtil;
import bp.util.Std;
import bp.util.TextUtil;

public class UIConfigs extends BPConfigAdvBase
{
	private static Color S_COLOR_WEAKBORDER = new Color(128, 128, 128, 51);
	private static Color S_COLOR_STRONGBORDER = new Color(128, 128, 128);
	private static Color S_COLOR_TABLEGRID = new Color(128, 128, 128, 128);
	private static Color S_COLOR_TEXTBG = new Color(255, 255, 255);
	private static Color S_COLOR_TEXTFG = new Color(0, 0, 0);
	private static Color S_COLOR_TEXTHALF = new Color(128, 128, 128);
	private static Color S_COLOR_TEXTQUARTER = new Color(192, 192, 192);
	private static Color S_COLOR_PANELBG = new Color(128, 128, 128, 51);
	private static int S_STARTSCREEN_W = 800;
	private static int S_STARTSCREEN_H = 600;
	private static double S_UI_SCALE = 1f;
	private static double S_GC_SCALE = 1f;
	private static double S_FS_SCALE = 1f;
	private static int S_BUTTON_SIZE = 16;
	private static int S_MENUFONT_SIZE = 11;
	private static int S_EDITORFONT_SIZE = 11;
	private static int S_TREEFONT_SIZE = 10;
	private static int S_TABLEFONT_SIZE = 10;
	private static int S_TEXTFIELDFONT_SIZE = 10;
	private static int S_LISTFONT_SIZE = 10;
	private static int S_TEXTFIELD_HEIGHT = 20;
	private static int S_TREE_ROWHEIGHT = 16;
	private static int S_TABLE_ROWHEIGHT = 16;
	private static String S_MONO_FONT_NAME = "Monospaced";
	private static String S_LABEL_FONT_NAME = "SansSerif";
	private static String S_LIST_FONT_NAME = "SansSerif";
	private static String S_TREE_FONT_NAME = "SansSerif";
	private static String S_TABLE_FONT_NAME = "SansSerif";
	private static String S_MENU_FONT_NAME = "SansSerif";

	private static int S_MONOFONT_SIZEDELTA = 0;

	private static boolean S_SHOW_VMINFO = false;
	private static String S_LAFCLSNAME = null;

	private static int S_TAB_SIZE = 8;

	private static int S_DIVIDER_SIZE = 1;
	private static boolean S_DOUBLE_BUFFER = false;

	private static boolean S_SYSTRAY = false;
	private static boolean S_MIN2TRAY = false;

	protected Consumer<? extends BPConfigAdv> m_loader = this::loadConfig;
	protected Consumer<? extends BPConfigAdv> m_persister = this::saveConfig;

	public boolean canUserConfig()
	{
		return true;
	}

	protected void loadConfig(BPConfigAdv config)
	{
		Std.info("UIConfigs::Loading");
		BPResourceIO cfgfile = BPCore.getWorkspaceContext().getConfigRes(".bpuicfgs", true);
		byte[] bs = IOUtil.read(cfgfile);
		Map<String, String> pmap = new HashMap<String, String>();
		if (bs != null)
		{
			String str = TextUtil.toString(bs, "utf-8");
			pmap = TextUtil.getPlainMap(str);
		}
		MIF mif = new MIF(pmap);
		mif.mif("TAB_SIZE", (v) -> S_TAB_SIZE = Integer.valueOf((String) v)).mif("DIVIDER_SIZE", (v) -> S_DIVIDER_SIZE = Integer.valueOf((String) v));
		mif.mifnull("DOUBLE_BUFFER", v -> S_DOUBLE_BUFFER = "true".equals(v));
		mif.mifnull("SHOW_VMINFO", v -> S_SHOW_VMINFO = "true".equals(v));
		mif.mifnull("LAF_CLASSNAME", v -> S_LAFCLSNAME = ObjUtil.toString(v));
		mif.mifnull("MONO_FONT_NAME", v -> S_MONO_FONT_NAME = ObjUtil.toString(v));
		mif.mifnull("LABEL_FONT_NAME", v -> S_LABEL_FONT_NAME = ObjUtil.toString(v));
		mif.mifnull("LIST_FONT_NAME", v -> S_LIST_FONT_NAME = ObjUtil.toString(v));
		mif.mifnull("TREE_FONT_NAME", v -> S_TREE_FONT_NAME = ObjUtil.toString(v));
		mif.mifnull("TABLE_FONT_NAME", v -> S_TABLE_FONT_NAME = ObjUtil.toString(v));
		mif.mifnull("MENU_FONT_NAME", v -> S_MENU_FONT_NAME = ObjUtil.toString(v));
		mif.mifnull("MONO_FONT_SIZEDELTA", v -> S_MONOFONT_SIZEDELTA = ObjUtil.toInt(v, 0));
		mif.mifnull("SYSTEM_TRAY", v -> S_SYSTRAY = "true".equals(v));
		mif.mifnull("MIN_TO_TRAY", v -> S_MIN2TRAY = "true".equals(v));
		try
		{
			LookAndFeel laf = UIManager.getLookAndFeel();
			if (S_LAFCLSNAME != null && S_LAFCLSNAME.length() > 0)
			{
				if (!S_LAFCLSNAME.equals(laf.getClass().getName()))
					UIManager.setLookAndFeel(S_LAFCLSNAME);
			}
			else if (!laf.getClass().getName().equals(UIManager.getSystemLookAndFeelClassName()))
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			Std.err(e);
		}

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		S_STARTSCREEN_W = (int) ((float) d.getWidth() * 0.9f);
		S_STARTSCREEN_H = (int) ((float) d.getHeight() * 0.9f);

		mif.mifnull("MENU_FONT_NAME", null, (v) ->
		{
			JMenuItem mnutest = new JMenuItem();
			Font ftest = mnutest.getFont();
			S_MENU_FONT_NAME = ftest.getFontName();
		});

		JTextArea testtext = new JTextArea();
		S_EDITORFONT_SIZE = testtext.getFont().getSize();
		if (S_EDITORFONT_SIZE < 13)
			S_EDITORFONT_SIZE = 13;
		S_TREEFONT_SIZE = S_EDITORFONT_SIZE - 2;
		S_TABLEFONT_SIZE = S_EDITORFONT_SIZE - 2;
		S_COLOR_TEXTBG = testtext.getBackground();
		S_COLOR_TEXTFG = testtext.getForeground();
		S_COLOR_TEXTHALF = UIUtil.mix(S_COLOR_TEXTBG, S_COLOR_TEXTFG, 255);
		S_COLOR_TEXTQUARTER = UIUtil.mix(S_COLOR_TEXTBG, S_COLOR_TEXTHALF, 255);
		S_COLOR_WEAKBORDER = UIUtil.mix(S_COLOR_TEXTBG, S_COLOR_TEXTFG, 51);
		testtext = null;

		JPanel testpnl = new JPanel();
		S_COLOR_PANELBG = testpnl.getBackground();
		testpnl = null;

		String javaver = System.getProperty("java.specification.version");
		if (javaver.startsWith("1.") && Integer.parseInt(javaver.substring(2)) <= 8)
		{
			S_UI_SCALE = Toolkit.getDefaultToolkit().getScreenResolution() / 96f;
			String uiscale = System.getProperty("bp.config.UIConfigs.uiscale");
			String fsscale = System.getProperty("bp.config.UIConfigs.fsscale");
			JTextArea testtext2 = new JTextArea();
			S_EDITORFONT_SIZE = testtext2.getFont().getSize() + 2;
			if (fsscale != null)
			{
				S_FS_SCALE = Float.parseFloat(fsscale);
			}
			if (uiscale != null)
			{
				S_UI_SCALE *= Float.parseFloat(uiscale);
				S_FS_SCALE *= S_UI_SCALE;
				S_EDITORFONT_SIZE = (int) (testtext2.getFont().getSize() * S_FS_SCALE) + 1;
			}
			S_BUTTON_SIZE = (int) (16f * S_UI_SCALE);
			S_TREEFONT_SIZE = S_EDITORFONT_SIZE - 2;
			S_MENUFONT_SIZE = S_EDITORFONT_SIZE - 3;
			S_TABLEFONT_SIZE = S_EDITORFONT_SIZE - 2;
			S_TEXTFIELDFONT_SIZE = S_TREEFONT_SIZE;
			S_LISTFONT_SIZE = S_TREEFONT_SIZE;
		}
		else
		{
			S_FS_SCALE = Toolkit.getDefaultToolkit().getScreenResolution() / 96f;

			String uiscale = System.getProperty("bp.config.UIConfigs.uiscale");
			String fsscale = System.getProperty("bp.config.UIConfigs.fsscale");
			if (fsscale != null)
			{
				S_FS_SCALE = Float.parseFloat(fsscale);
			}
			if (uiscale != null)
			{
				S_UI_SCALE *= Float.parseFloat(uiscale);
				S_FS_SCALE *= Float.parseFloat(uiscale);
				S_EDITORFONT_SIZE = (int) ((float) S_EDITORFONT_SIZE * (float) S_FS_SCALE);
				S_TREEFONT_SIZE = S_EDITORFONT_SIZE - 2;
				S_TABLEFONT_SIZE = S_EDITORFONT_SIZE - 2;
				S_MENUFONT_SIZE = S_EDITORFONT_SIZE - 2;
				S_TEXTFIELDFONT_SIZE = S_TREEFONT_SIZE;
				S_LISTFONT_SIZE = S_TREEFONT_SIZE;
			}
			else
			{
				if (S_FS_SCALE != 1d)
					S_EDITORFONT_SIZE = (int) ((float) S_EDITORFONT_SIZE * (float) S_FS_SCALE);
				S_TREEFONT_SIZE = S_FS_SCALE == 1f ? S_EDITORFONT_SIZE - 1 : S_EDITORFONT_SIZE - 2;
				S_TABLEFONT_SIZE = S_FS_SCALE == 1f ? S_EDITORFONT_SIZE - 1 : S_EDITORFONT_SIZE - 2;
				S_MENUFONT_SIZE = S_FS_SCALE == 1f ? S_EDITORFONT_SIZE - 1 : S_EDITORFONT_SIZE - 2;
				S_TEXTFIELDFONT_SIZE = S_TREEFONT_SIZE;
				S_LISTFONT_SIZE = S_TREEFONT_SIZE;
			}
			S_BUTTON_SIZE = (int) (16f * S_UI_SCALE);
		}
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = env.getDefaultScreenDevice().getDefaultConfiguration();
		AffineTransform transform = gc.getDefaultTransform();
		double f = transform.getScaleY();
		S_GC_SCALE = f;

		Font testf = new Font(UIConfigs.MONO_FONT_NAME(), Font.PLAIN, S_TEXTFIELDFONT_SIZE);
		Image img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		S_TEXTFIELD_HEIGHT = g.getFontMetrics(testf).getHeight() + 2;
		testf = new Font(UIConfigs.TREE_FONT_NAME(), Font.PLAIN, S_TREEFONT_SIZE);
		S_TREE_ROWHEIGHT = g.getFontMetrics(testf).getHeight();
		testf = new Font(UIConfigs.TABLE_FONT_NAME(), Font.PLAIN, S_TABLEFONT_SIZE);
		S_TABLE_ROWHEIGHT = g.getFontMetrics(testf).getHeight();
		g.dispose();
		img = null;
		testf = null;

		Std.debug("uiscale:" + S_UI_SCALE);
		Std.debug("fsscale:" + S_FS_SCALE);
		Std.debug("gcscale:" + f);
		Std.info("UIConfigs::Loaded");

		config.puts("_TAB_SIZE", 8, "_DIVIDER_SIZE", 1, "_SHOW_VMINFO", false, "_DOUBLE_BUFFER", false, "_LAF_CLASSNAME", "<AUTO>");
		config.puts("_MONO_FONT_NAME", "monospaced", "_LABEL_FONT_NAME", "SansSerif", "_LIST_FONT_NAME", "SansSerif", "_TREE_FONT_NAME", "SansSerif", "_TABLE_FONT_NAME", "SansSerif", "_MENU_FONT_NAME", "SansSerif", "_MONO_FONT_SIZEDELTA", 0);
		config.puts("_SYSTEM_TRAY", false, "_MIN_TO_TRAY", false);
		config.putAll(pmap);
	};

	protected void saveConfig(BPConfigAdv config)
	{
		byte[] bs = TextUtil.fromString(TextUtil.fromPlainMap(ObjUtil.toPlainMap(m_map), null), "utf-8");
		if (bs != null)
		{
			Std.info("UIConfigs::Saving");
			if (IOUtil.write(BPCore.getWorkspaceContext().getConfigRes(".bpuicfgs", false), bs))
				Std.info("UIConfigs::Saved");
		}
	}

	@SuppressWarnings("unchecked")
	public <S extends BPConfigAdv> Consumer<S> getConfigLoader()
	{
		return (Consumer<S>) m_loader;
	}

	public <S extends BPConfigAdv> void setConfigLoader(Consumer<S> loader)
	{
		m_loader = loader;
	}

	@SuppressWarnings("unchecked")
	public <S extends BPConfigAdv> Consumer<S> getConfigPersister()
	{
		return (Consumer<S>) m_persister;
	}

	public <S extends BPConfigAdv> void setConfigPersister(Consumer<S> persister)
	{
		m_persister = persister;
	}

	protected Map<String, Object> createMap()
	{
		return new HashMap<String, Object>();
	}

	public final static Color COLOR_WEAKBORDER()
	{
		return S_COLOR_WEAKBORDER;
	}

	public final static Color COLOR_STRONGBORDER()
	{
		return S_COLOR_STRONGBORDER;
	}

	public final static Color COLOR_TABLEGRID()
	{
		return S_COLOR_TABLEGRID;
	}

	public final static Color COLOR_TEXTBG()
	{
		return S_COLOR_TEXTBG;
	}

	public final static Color COLOR_TEXTFG()
	{
		return S_COLOR_TEXTFG;
	}

	public final static Color COLOR_TEXTHALF()
	{
		return S_COLOR_TEXTHALF;
	}

	public final static Color COLOR_TEXTQUARTER()
	{
		return S_COLOR_TEXTQUARTER;
	}

	public final static Color COLOR_PANELBG()
	{
		return S_COLOR_PANELBG;
	}

	public final static double UI_SCALE()
	{
		return S_UI_SCALE;
	}

	public final static double GC_SCALE()
	{
		return S_GC_SCALE;
	}

	public final static double FONT_SCALE()
	{
		return S_FS_SCALE;
	}

	public final static int EDITORFONT_SIZE()
	{
		return S_EDITORFONT_SIZE;
	}

	public final static int TEXTFIELDFONT_SIZE()
	{
		return S_TEXTFIELDFONT_SIZE;
	}

	public final static int LISTFONT_SIZE()
	{
		return S_LISTFONT_SIZE;
	}

	public final static int MENUFONT_SIZE()
	{
		return S_MENUFONT_SIZE;
	}

	public final static int TREEFONT_SIZE()
	{
		return S_TREEFONT_SIZE;
	}

	public final static int TABLEFONT_SIZE()
	{
		return S_TABLEFONT_SIZE;
	}

	public final static int BUTTON_SIZE()
	{
		return S_BUTTON_SIZE;
	}

	public final static int TEXTFIELD_HEIGHT()
	{
		return S_TEXTFIELD_HEIGHT;
	}

	public final static int TREE_ROWHEIGHT()
	{
		return S_TREE_ROWHEIGHT;
	}

	public final static int TABLE_ROWHEIGHT()
	{
		return S_TABLE_ROWHEIGHT;
	}

	public final static int[] START_SCREENSIZE()
	{
		return new int[] { S_STARTSCREEN_W, S_STARTSCREEN_H };
	}

	public final static String MONO_FONT_NAME()
	{
		return S_MONO_FONT_NAME;
	}

	public final static int MONO_FONT_SIZEDELTA()
	{
		return S_MONOFONT_SIZEDELTA;
	}

	public final static String LABEL_FONT_NAME()
	{
		return S_LABEL_FONT_NAME;
	}

	public final static String MENU_FONT_NAME()
	{
		return S_MENU_FONT_NAME;
	}

	public final static String LIST_FONT_NAME()
	{
		return S_LIST_FONT_NAME;
	}

	public final static String TREE_FONT_NAME()
	{
		return S_TREE_FONT_NAME;
	}

	public final static String TABLE_FONT_NAME()
	{
		return S_TABLE_FONT_NAME;
	}

	public final static int DIVIDER_SIZE()
	{
		return S_DIVIDER_SIZE;
	}

	public final static int TAB_SIZE()
	{
		return S_TAB_SIZE;
	}

	public final static boolean SHOW_VMINFO()
	{
		return S_SHOW_VMINFO;
	}

	public final static boolean DOUBLE_BUFFER()
	{
		return S_DOUBLE_BUFFER;
	}

	public final static boolean SYSTEM_TRAY()
	{
		return S_SYSTRAY;
	}

	public final static boolean MIN_TO_TRAY()
	{
		return S_MIN2TRAY;
	}

	public final static int BAR_HEIGHT_VICON()
	{
		return 20;
	}

	public final static int BAR_HEIGHT_COMBO()
	{
		return 24;
	}

	public final static int BAR_HEIGHT_VERTICAL()
	{
		return 20;
	}
}
