package bp.env;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPEnvEditors extends BPEnvCustom
{
	public final static String ENV_NAME_EDITORS = "Editors";

	public final static String ENVKEY_RAWEDITOR_FULLSCROLL = "RAWEDITOR_FULLSCROLL";

	public String getName()
	{
		return ENV_NAME_EDITORS;
	}

	protected List<String> setupRawKeys()
	{
		return new CopyOnWriteArrayList<String>(new String[] { ENVKEY_RAWEDITOR_FULLSCROLL });
	}
}