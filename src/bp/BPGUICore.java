package bp;

import java.awt.Frame;
import java.awt.Window;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JDialog;

import bp.BPCore.BPPlatform;
import bp.config.EditorAssocs;
import bp.config.FormConfigs;
import bp.config.FormatAssocs;
import bp.config.Hotkeys;
import bp.config.PredefinedDataPipes;
import bp.config.ShortCuts;
import bp.config.UIConfigs;
import bp.env.BPEnvs;
import bp.event.BPEventBus;
import bp.ext.BPExtensionLoader;
import bp.ext.BPExtensionLoaderGUI;
import bp.ext.BPExtensionLoaderGUISwing;
import bp.ext.BPExtensionManager;
import bp.tool.BPTool;
import bp.tool.BPToolFactory;
import bp.tool.BPToolManager;
import bp.ui.frame.BPFrameComponent;
import bp.ui.frame.BPFrameHostIFC;
import bp.ui.frame.BPMainFrame;
import bp.ui.frame.BPMainFrameIFC;
import bp.ui.util.CommonUIOperations;
import bp.util.ClassUtil;
import bp.util.CommandLineArgs;
import bp.util.ObjUtil;
import bp.util.LogicUtil.WeakRefGo;

public class BPGUICore
{
	public final static UIConfigs CONFIGS_UI = new UIConfigs();
	public final static ShortCuts CONFIGS_SC = new ShortCuts();
	public final static Hotkeys CONFIGS_HK = new Hotkeys();
	public final static PredefinedDataPipes CONFIGS_PDPS = new PredefinedDataPipes();
	public final static FormConfigs CONFIGS_FORM = new FormConfigs();
	public final static EditorAssocs BINDINGS_EDITOR = new EditorAssocs();
	public final static BPEventBus EVENTS_UI = new BPEventBus();
	public final static Map<String, List<BPTool>> TOOL_MAP = new ConcurrentHashMap<String, List<BPTool>>();

	public static Boolean LAUNCHER_FLAG;

	public static String S_BP_TITLE = "BlockP";

	protected final static WeakRefGo<BPMainFrameIFC> S_MF = new WeakRefGo<BPMainFrameIFC>();
	
	private final static ThreadLocal<Boolean> S_ISPOPUP = new ThreadLocal<>();

	@SuppressWarnings("unchecked")
	public final static int start(CommandLineArgs cliargs)
	{
		BPCore.setPlatform(BPPlatform.GUI_SWING);
		BPCore.setCommandLineArgs(cliargs);
		BPCore.registerConfig(CONFIGS_UI);
		BPCore.registerConfig(CONFIGS_SC);
		BPCore.registerConfig(CONFIGS_HK);
		BPCore.registerConfig(CONFIGS_FORM);
		BPCore.registerConfig(new BPToolManager());
		BPCore.registerConfig(new FormatAssocs());
		BPCore.registerConfig(new BPEnvs());
		BPCore.registerConfig(BINDINGS_EDITOR);
		BPCore.registerConfig(CONFIGS_PDPS);
		BPCore.start(cliargs.contextpath);
		installTools();

		BPCore.S_ELIST.add(BPGUICore::safeExit);
		if ("tool".equals(cliargs.params.get("mfmode")))
		{
			String toolcls = cliargs.params.get("toolcls");
			String x = cliargs.params.get("x");
			String y = cliargs.params.get("y");
			String w = cliargs.params.get("w");
			String h = cliargs.params.get("h");
			int r = CommonUIOperations.showToolDialog(toolcls, ObjUtil.makeMap("x", x, "y", y, "w", w, "h", h));
			BPCore.stop();
			return r;
		}
		else
		{
			BPMainFrame mainf = new BPMainFrame();
			BPExtensionLoader[] loaders = BPExtensionManager.getLoadedExtensionLoaders();
			for (BPExtensionLoader loader : loaders)
			{
				if (loader.isUI() && BPExtensionLoaderGUISwing.UITYPE_SWING.equals(loader.getUIType()))
				{
					((BPExtensionLoaderGUI<BPMainFrameIFC>) loader).setup(mainf);
				}
			}
			String editor = cliargs.params.get("openeditor");
			if (editor != null && editor.length() > 0)
			{
				String[] editorargs = editor.split(",");
				String filename, format = null, fac = null;
				int c = editorargs.length;
				if (c > 0)
				{
					filename = editorargs[0];
					if (c > 1)
						format = editorargs[1];
					if (c > 2)
						fac = editorargs[2];
					mainf.openEditorByFileSystem(filename, format, fac, null);
				}
			}
			if ("standalone".equals(cliargs.params.get("mfmode")))
				mainf.enterStandaloneMode();
			S_MF.setTarget(mainf);
			CONFIGS_HK.refreshHotkeys();
			mainf.setVisible(true);
			Runtime.getRuntime().addShutdownHook(new Thread(BPGUICore::onShutdown));
			return 0;
		}
	}

	private final static void onShutdown()
	{
		BPCore.stop();
	}

	protected final static void installTools()
	{
		List<BPToolFactory> facs = BPToolManager.getFactories();
		Map<String, List<BPTool>> toolmap = new HashMap<String, List<BPTool>>();

		{
			BiConsumer<String, BPTool> cb = (name, tool) ->
			{
				List<BPTool> tools = toolmap.get(name);
				if (tools == null)
				{
					tools = new ArrayList<BPTool>();
					toolmap.put(name, tools);
				}
				tools.add(tool);
			};
			for (int i = 0; i < facs.size(); i++)
				facs.get(i).install(cb, BPPlatform.GUI_SWING);
		}

		TOOL_MAP.clear();
		TOOL_MAP.putAll(toolmap);
	}

	public final static BPMainFrameIFC getMainFrame()
	{
		return S_MF.get();
	}

	public final static <V> V execOnMainFrame(Function<BPMainFrameIFC, V> seg)
	{
		return S_MF.exec(seg);
	}

	public final static boolean checkMainFrameVisible()
	{
		BPMainFrameIFC mf = S_MF.get();
		return mf == null ? false : mf.isVisible();
	}

	public final static void runOnMainFrame(Consumer<BPMainFrameIFC> seg)
	{
		S_MF.run(seg);
	}

	public final static <T> void runOnMainFrame(BiConsumer<BPMainFrameIFC, T> seg, T params)
	{
		S_MF.run(seg, params);
	}
	
	public final static void runOnMainFrameDynamic(String method, Object... params)
	{
		S_MF.runDynamic(BPMainFrameIFC.class, method, params);
	}

	public final static <V> V execOnCurrentFrame(Function<BPFrameHostIFC, V> seg)
	{
		Frame f0 = getCurrentFrame();
		if (f0 != null && f0 instanceof BPFrameHostIFC)
			return seg.apply((BPFrameHostIFC) f0);
		return null;
	}

	public final static boolean checkCurrentFrameVisible()
	{
		Frame f0 = getCurrentFrame();
		return f0 != null && f0.isVisible();
	}

	public final static void runOnCurrentFrame(Consumer<BPFrameHostIFC> seg)
	{
		Frame f0 = getCurrentFrame();
		if (f0 != null && f0 instanceof BPFrameHostIFC)
			seg.accept((BPFrameHostIFC) f0);
	}

	public final static void runOnCurrentFrameDynamic(String method, Object... params)
	{
		Frame f0 = getCurrentFrame();
		if (f0 != null && f0 instanceof BPFrameHostIFC)
			ClassUtil.tryCallSimpleMethod(BPFrameHostIFC.class, method, f0, params);
	}

	public final static void runOnCurrentFrameWithCreation(Consumer<BPFrameHostIFC> seg)
	{
		Frame f0 = getCurrentFrame();
		if (f0 != null && f0 instanceof BPFrameHostIFC)
			seg.accept((BPFrameHostIFC) f0);
		else
		{
			BPFrameComponent f = new BPFrameComponent();
			seg.accept(f);
			f.setVisible(true);
		}
	}

	public final static boolean closeSubWindows()
	{
		Window[] ws = Window.getWindows();
		for (Window w : ws)
		{
			if (w instanceof JDialog)
			{
				w.dispose();
			}
		}
		for (Window w : ws)
		{
			if (!(w instanceof BPFrameHostIFC))
				w.dispose();
		}
		for (Window w : ws)
		{
			if (w instanceof BPFrameHostIFC && !((BPFrameHostIFC) w).isMainFrame())
			{
				w.dispose();
			}
		}
		return true;
	}

	public final static void safeExit()
	{
		closeSubWindows();
		BPMainFrameIFC mf = S_MF.get();
		if (mf != null)
			mf.dispose();
	}
	
	public final static void inPopup(Runnable r)
	{
		S_ISPOPUP.set(true);
		try
		{
			r.run();
		}
		finally
		{
			S_ISPOPUP.remove();
		}
	}
	
	public final static boolean isInPopup()
	{
		return Boolean.TRUE.equals(S_ISPOPUP.get());
	}
	
	public final static Window getCurrentWindow()
	{
		Window[] ws = Window.getWindows();
		boolean ispopup = Boolean.TRUE.equals(S_ISPOPUP.get());
		if (ws != null && ws.length > 0)
		{
			for (Window w : ws)
			{
				if (w.isVisible() && (ispopup || w.isActive()))
					return w;
			}
			for (Window w : ws)
			{
				if (w.isVisible())
					return w;
			}
		}
		return null;
	}

	public final static Frame getCurrentFrame()
	{
		Frame[] fs = Frame.getFrames();
		boolean ispopup = Boolean.TRUE.equals(S_ISPOPUP.get());
		if (fs != null && fs.length > 0)
		{
			for (Frame f : fs)
			{
				if (f.isVisible() && (ispopup || f.isActive()))
					return f;
			}
			for (Frame f : fs)
			{
				if (f.isVisible())
					return f;
			}
		}
		return null;
	}
}
