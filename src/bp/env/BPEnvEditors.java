package bp.env;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPEnvEditors extends BPEnvBase
{
	public final static String ENV_NAME_EDITORS = "Editors";

	public final static String ENVKEY_RAWEDITOR_FULLSCROLL = "RAWEDITOR_FULLSCROLL";

	private final List<String> m_rawkeys = new CopyOnWriteArrayList<String>(new String[] { ENVKEY_RAWEDITOR_FULLSCROLL });

	public String getName()
	{
		return ENV_NAME_EDITORS;
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
