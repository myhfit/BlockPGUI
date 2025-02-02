package bp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import bp.BPCore.BPPlatform;
import bp.config.EditorAssocs;
import bp.config.FormConfigs;
import bp.config.FormatAssocs;
import bp.config.ShortCuts;
import bp.config.UIConfigs;
import bp.env.BPEnvs;
import bp.event.BPEventBus;
import bp.ext.BPExtensionLoader;
import bp.ext.BPExtensionLoaderGUISwing;
import bp.ext.BPExtensionManager;
import bp.tool.BPTool;
import bp.tool.BPToolFactory;
import bp.tool.BPToolManager;
import bp.ui.frame.BPMainFrame;
import bp.ui.frame.BPMainFrameIFC;
import bp.util.CommandLineArgs;
import bp.util.LogicUtil.WeakRefGo;

public class BPGUICore
{
	public final static UIConfigs CONFIGS_UI = new UIConfigs();
	public final static ShortCuts CONFIGS_SC = new ShortCuts();
	public final static FormConfigs CONFIGS_FORM = new FormConfigs();
	public final static EditorAssocs BINDINGS_EDITOR = new EditorAssocs();
	public final static BPEventBus EVENTS_UI = new BPEventBus();
	public final static Map<String, List<BPTool>> TOOL_MAP = new ConcurrentHashMap<String, List<BPTool>>();

	public static String S_BP_TITLE = "BlockP";

	protected final static WeakRefGo<BPMainFrameIFC> S_MF = new WeakRefGo<BPMainFrameIFC>();

	public final static void start(CommandLineArgs cliargs)
	{
		BPCore.setPlatform(BPPlatform.GUI_SWING);
		BPCore.setCommandLineArgs(cliargs);
		BPCore.registerConfig(CONFIGS_UI);
		BPCore.registerConfig(CONFIGS_SC);
		BPCore.registerConfig(CONFIGS_FORM);
		BPCore.registerConfig(new BPToolManager());
		BPCore.registerConfig(new FormatAssocs());
		BPCore.registerConfig(new BPEnvs());
		BPCore.registerConfig(BINDINGS_EDITOR);
		BPCore.start(cliargs.contextpath);
		installTools();

		BPMainFrame mainf = new BPMainFrame();
		BPExtensionLoader[] loaders = BPExtensionManager.getExtensionLoaders();
		for (BPExtensionLoader loader : loaders)
		{
			if (loader.isUI() && BPExtensionLoaderGUISwing.UITYPE_SWING.equals(loader.getUIType()))
			{
				((BPExtensionLoaderGUISwing) loader).setup(mainf);
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
		{
			mainf.enterStandaloneMode();
		}
		S_MF.setTarget(mainf);
		mainf.setVisible(true);

		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				BPCore.stop();
			}
		});
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
			for (BPToolFactory fac : facs)
			{
				fac.install(cb, BPPlatform.GUI_SWING);
			}
		}

		TOOL_MAP.clear();
		TOOL_MAP.putAll(toolmap);
	}

	public final static <V> V execOnMainFrame(Function<BPMainFrameIFC, V> seg)
	{
		return S_MF.exec(seg);
	}

	public final static void runOnMainFrame(Consumer<BPMainFrameIFC> seg)
	{
		S_MF.run(seg);
	}
}
