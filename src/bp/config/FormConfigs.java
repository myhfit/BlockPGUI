package bp.config;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import bp.config.BPConfigAdv.BPConfigAdvBase;
import bp.ui.form.BPFormManager;
import bp.ui.form.BPFormPanel;
import bp.ui.form.BPFormPanelDir;
import bp.ui.form.BPFormPanelEditorAssocs;
import bp.ui.form.BPFormPanelEnvs;
import bp.ui.form.BPFormPanelFactory;
import bp.ui.form.BPFormPanelFile;
import bp.ui.form.BPFormPanelFileProject;
import bp.ui.form.BPFormPanelFormatAssocs;
import bp.ui.form.BPFormPanelHotkeys;
import bp.ui.form.BPFormPanelList;
import bp.ui.form.BPFormPanelMap;
import bp.ui.form.BPFormPanelPredefinedDataPipes;
import bp.ui.form.BPFormPanelProject;
import bp.ui.form.BPFormPanelResourceBase;
import bp.ui.form.BPFormPanelSchedule;
import bp.ui.form.BPFormPanelScheduleFileSystem;
import bp.ui.form.BPFormPanelScript;
import bp.ui.form.BPFormPanelShortCuts;
import bp.ui.form.BPFormPanelString;
import bp.ui.form.BPFormPanelTask;
import bp.ui.form.BPFormPanelTaskCopyFiles;
import bp.ui.form.BPFormPanelTaskExec;
import bp.ui.form.BPFormPanelTaskPackFiles;
import bp.ui.form.BPFormPanelTaskReadTextFile;
import bp.ui.form.BPFormPanelTaskRemind;
import bp.ui.form.BPFormPanelTaskSerial;
import bp.ui.form.BPFormPanelTaskTimer;
import bp.ui.form.BPFormPanelTaskUnpackFiles;
import bp.ui.form.BPFormPanelTaskUserInput;
import bp.ui.form.BPFormPanelUIConfigs;
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
			regfunc.accept("bp.project.BPResourceProject", BPFormPanelProject.class);
			regfunc.accept("bp.project.BPResourceProjectFile", BPFormPanelFileProject.class);
			regfunc.accept("bp.project.BPResourceProjectMemory", BPFormPanelProject.class);
			regfunc.accept("bp.res.BPResourceVirtual", BPFormPanelResourceBase.class);
			regfunc.accept("bp.res.BPResourceFileLocal", BPFormPanelFile.class);
			regfunc.accept("bp.res.BPResourceDirLocal", BPFormPanelDir.class);
			regfunc.accept("bp.config.EditorAssocs", BPFormPanelEditorAssocs.class);
			regfunc.accept("bp.config.FormatAssocs", BPFormPanelFormatAssocs.class);
			regfunc.accept("bp.config.UIConfigs", BPFormPanelUIConfigs.class);
			regfunc.accept("bp.config.ShortCuts", BPFormPanelShortCuts.class);
			regfunc.accept("bp.config.Hotkeys", BPFormPanelHotkeys.class);
			regfunc.accept("bp.config.PredefinedDataPipes", BPFormPanelPredefinedDataPipes.class);
			regfunc.accept("bp.task.BPTaskBase", BPFormPanelTask.class);
			regfunc.accept("bp.task.BPTaskTimer", BPFormPanelTaskTimer.class);
			regfunc.accept("bp.task.BPTaskCopyFiles", BPFormPanelTaskCopyFiles.class);
			regfunc.accept("bp.task.BPTaskPackFiles", BPFormPanelTaskPackFiles.class);
			regfunc.accept("bp.task.BPTaskUnpackFiles", BPFormPanelTaskUnpackFiles.class);
			regfunc.accept("bp.task.BPTaskSerial", BPFormPanelTaskSerial.class);
			regfunc.accept("bp.task.BPTaskSerialPipe", BPFormPanelTaskSerial.class);
			regfunc.accept("bp.task.BPTaskExec", BPFormPanelTaskExec.class);
			regfunc.accept("bp.task.BPTaskReadTextFile", BPFormPanelTaskReadTextFile.class);
			regfunc.accept("bp.task.BPTaskRemind", BPFormPanelTaskRemind.class);
			regfunc.accept("bp.task.BPTaskUserInput", BPFormPanelTaskUserInput.class);
			regfunc.accept("bp.format.BPFormatManager", BPFormPanelFormatAssocs.class);
			regfunc.accept("bp.script.BPScriptBase", BPFormPanelScript.class);
			regfunc.accept("bp.env.BPEnvs", BPFormPanelEnvs.class);
			regfunc.accept("bp.env.BPEnv", BPFormPanelMap.class);
			regfunc.accept("bp.data.BPMData$BPMDataWMap", BPFormPanelMap.class);
			regfunc.accept("bp.data.BPYData$BPYDataList", BPFormPanelList.class);
			regfunc.accept("bp.schedule.BPScheduleBase", BPFormPanelSchedule.class);
			regfunc.accept("bp.schedule.BPScheduleFileSystem", BPFormPanelScheduleFileSystem.class);
			regfunc.accept("java.lang.String", BPFormPanelString.class);
		}
	}
}
