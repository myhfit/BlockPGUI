package bp.env;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPEnvEditorAssocOverride extends BPEnvBase
{
	public final static String ENV_NAME_EA = "Editor Association";

	public final static String ENVKEY_OPEN_WITH_F = "OPEN_WITH(FILE)";
	public final static String ENVKEY_OPEN_WITH_D = "OPEN_WITH[DIR]";

	private final List<String> m_rawkeys = new CopyOnWriteArrayList<String>(new String[] { ENVKEY_OPEN_WITH_F, ENVKEY_OPEN_WITH_D });

	public String getName()
	{
		return ENV_NAME_EA;
	}

	public boolean hasKey(String key)
	{
		return m_kvs.containsKey(key);
	}

	public boolean customKey()
	{
		return true;
	}

	public List<String> listRawKeys()
	{
		return new ArrayList<String>(m_rawkeys);
	}

	public boolean isRawKey(String key)
	{
		return m_rawkeys.contains(key);
	}

	public void addRawKey(String key)
	{
		if (!m_rawkeys.contains(key))
			m_rawkeys.add(key);
	}
}
