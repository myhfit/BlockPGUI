package bp.env;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bp.BPCore;
import bp.util.BPPDUtil;
import bp.util.IOUtil;

public class BPEnvActions extends BPEnvBase
{
	public final static String ENV_NAME_ACTIONS = "Actions";

	private final List<String> m_rawkeys = new ArrayList<String>();

	public String getName()
	{
		return ENV_NAME_ACTIONS;
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

	public boolean customSL()
	{
		return true;
	}

	public void load()
	{
		byte[] bs = IOUtil.read(BPCore.getWorkspaceContext().getConfigRes(".bpactions.bppd"));
		if (bs != null)
		{
			Map<String, Object> mo = BPPDUtil.read(bs);
			m_kvs.clear();
			for (String k : mo.keySet())
			{
				setEnv(k, (String) mo.get(k));
			}
		}
	}

	public void save()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BPPDUtil.write(bos, m_kvs);
		IOUtil.write(BPCore.getWorkspaceContext().getConfigRes(".bpactions.bppd", false), bos.toByteArray());
	}
}
