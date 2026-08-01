package bp.env;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPEnvEditorAssocOverride extends BPEnvCustom
{
	public final static String ENV_NAME_EA = "Editor Association";

	public final static String ENVKEY_OPEN_WITH_F = "OPEN_WITH(FILE)";
	public final static String ENVKEY_OPEN_WITH_D = "OPEN_WITH[DIR]";

	public String getName()
	{
		return ENV_NAME_EA;
	}

	protected List<String> setupRawKeys()
	{
		return new CopyOnWriteArrayList<String>(new String[] { ENVKEY_OPEN_WITH_F, ENVKEY_OPEN_WITH_D });
	}
}
