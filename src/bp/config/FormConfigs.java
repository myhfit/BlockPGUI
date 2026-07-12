package bp.config;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import bp.config.BPConfigAdv.BPConfigAdvBase;
import bp.ui.form.BPFormManager;
import bp.ui.form.BPFormPanel;
import bp.ui.form.BPFormPanelDynamicByConfig;
import bp.ui.form.BPFormPanelDynamicByController;
import bp.ui.form.BPFormPanelEnvs;
import bp.ui.form.BPFormPanelFactory;
import bp.ui.form.BPFormPanelHotkeys;
import bp.ui.form.BPFormPanelList;
import bp.ui.form.BPFormPanelMap;
import bp.ui.form.BPFormPanelMapOrdered;
import bp.ui.form.BPFormPanelPredefinedDataPipes;
import bp.ui.form.BPFormPanelResourceVE;
import bp.ui.form.BPFormPanelScript;
import bp.ui.form.BPFormPanelShortCuts;
import bp.ui.form.BPFormPanelTaskSerial;
import bp.ui.form.BPFormPanelUIConfigs;
import bp.ui.form.BPFormPanelXYData;
import bp.util.ClassUtil;

public class FormConfigs extends BPConfigAdvBase
{
	protected void loadConfig(BPConfigAdv config)
	{
		ServiceLoader<BPFormPanelFactory> facs = ClassUtil.getExtensionServices(BPFormPanelFactory.class);
		if (facs != null)
		{
			for (BPFormPanelFactory fac : facs)
			{
				fac.register(BPFormManager::registerForm);
			}
		}
	}

	protected void saveConfig(BPConfigAdv config)
	{
	}

	public <S extends BPConfigAdv> Consumer<S> getConfigLoader()
	{
		return this::loadConfig;
	}

	public <S extends BPConfigAdv> void setConfigLoader(Consumer<S> loader)
	{
	}

	public <S extends BPConfigAdv> Consumer<S> getConfigPersister()
	{
		return this::saveConfig;
	}

	public <S extends BPConfigAdv> void setConfigPersister(Consumer<S> persister)
	{
	}

	protected Map<String, Object> createMap()
	{
		return new HashMap<String, Object>();
	}

	public final static class BPFormPanelFactoryGUIMain implements BPFormPanelFactory
	{
		public void register(BiConsumer<String, Class<? extends BPFormPanel>> regfunc)
		{
			regfunc.accept("bp.project.BPResourceProject", BPFormPanelDynamicByConfig.class);
			regfunc.accept("bp.project.BPResourceProjectFile", BPFormPanelDynamicByConfig.class);
			regfunc.accept("bp.project.BPResourceProjectMemory", BPFormPanelDynamicByConfig.class);
			regfunc.accept("bp.res.BPResourceVirtual", BPFormPanelDynamicByConfig.class);
			regfunc.accept("bp.res.BPResourceVirtual$BPResourceVirtualEntity", BPFormPanelResourceVE.class);
			regfunc.accept("bp.res.BPResourceFileLocal", BPFormPanelDynamicByConfig.class);
			regfunc.accept("bp.res.BPResourceDirLocal", BPFormPanelDynamicByConfig.class);
			regfunc.accept("bp.config.EditorAssocs", BPFormPanelDynamicByController.class);
			regfunc.accept("bp.config.FormatAssocs", BPFormPanelDynamicByController.class);
			regfunc.accept("bp.config.UIConfigs", BPFormPanelUIConfigs.class);
			regfunc.accept("bp.config.ShortCuts", BPFormPanelShortCuts.class);
			regfunc.accept("bp.config.Hotkeys", BPFormPanelHotkeys.class);
			regfunc.accept("bp.config.PredefinedDataPipes", BPFormPanelPredefinedDataPipes.class);
			regfunc.accept("bp.task.BPTaskBase", BPFormPanelDynamicByConfig.class);
			regfunc.accept("bp.task.BPTaskSerial", BPFormPanelTaskSerial.class);
			regfunc.accept("bp.task.BPTaskSerialPipe", BPFormPanelTaskSerial.class);
			regfunc.accept("bp.script.BPScriptBase", BPFormPanelScript.class);
			regfunc.accept("bp.env.BPEnvs", BPFormPanelEnvs.class);
			regfunc.accept("bp.env.BPEnv", BPFormPanelMap.class);
			regfunc.accept("bp.env.BPEnvBase", BPFormPanelMap.class);
			regfunc.accept("bp.env.BPEnvBase.BPEnvSub", BPFormPanelMap.class);
			regfunc.accept("bp.data.BPMData$BPMDataWMap", BPFormPanelMap.class);
			regfunc.accept("bp.data.BPMData$BPMDataWMapOrdered", BPFormPanelMapOrdered.class);
			regfunc.accept("bp.data.BPYData$BPYDataList", BPFormPanelList.class);
			regfunc.accept("bp.data.BPXYData", BPFormPanelXYData.class);
			regfunc.accept("bp.data.BPXYData$BPXYDataList", BPFormPanelXYData.class);
			regfunc.accept("bp.schedule.BPScheduleBase", BPFormPanelDynamicByConfig.class);
			regfunc.accept("java.lang.String", BPFormPanelDynamicByConfig.class);
		}
	}
}
