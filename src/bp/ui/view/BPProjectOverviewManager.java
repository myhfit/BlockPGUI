package bp.ui.view;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BPProjectOverviewManager
{
	public final static Map<String, BPProjectOverviewCompFactory<?>> S_FACS = new ConcurrentHashMap<String, BPProjectOverviewCompFactory<?>>();

	public final static void register(String prjtype, BPProjectOverviewCompFactory<?> fac)
	{
		S_FACS.put(prjtype, fac);
	}

	public final static BPProjectOverviewCompFactory<?> getFactory(String prjtype)
	{
		return S_FACS.get(prjtype);
	}
}
