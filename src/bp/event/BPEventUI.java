package bp.event;

import java.util.Map;

public class BPEventUI extends BPEvent
{
	public String subkey;
	public Object[] datas;
	public Map<String, Object> context;

	public final static String FIELD_ROUTABLE_CONTAINERID = "rconid";

	public String getRoutableContainerID()
	{
		return context == null ? null : (String) context.get(FIELD_ROUTABLE_CONTAINERID);
	}
}
