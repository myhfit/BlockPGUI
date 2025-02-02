package bp.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import bp.config.BPConfigAdv.BPConfigAdvBase;
import bp.ui.editor.BPEditorManager;

public class EditorAssocs extends BPConfigAdvBase
{
	protected void loadConfig(BPConfigAdv config)
	{
		BPEditorManager.init();
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
		return null;
	}

	public <S extends BPConfigAdv> void setConfigPersister(Consumer<S> persister)
	{
	}

	public Map<String, Object> getMappedData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.putAll(BPEditorManager.getFactoryMap());
		return rc;
	}

	protected Map<String, Object> createMap()
	{
		return new HashMap<String,Object>();
	}

	public boolean canUserConfig()
	{
		return true;
	}
}