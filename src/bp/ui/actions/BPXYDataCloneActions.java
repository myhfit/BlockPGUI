package bp.ui.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.swing.Action;

import bp.data.BPXYData;
import bp.util.ClassUtil;
import bp.util.ObjUtil;

public class BPXYDataCloneActions
{
	public final static Action[] getActions(BPXYData data, Runnable loaddatafunc)
	{
		List<Action[]> rc = new ArrayList<Action[]>();
		ServiceLoader<BPDataActionFactory> facs = ClassUtil.getExtensionServices(BPDataActionFactory.class);
		for (BPDataActionFactory fac : facs)
			rc.add(fac.getAction(data, BPDataActionFactory.ACTIONNAME_CLONEDATA, loaddatafunc));
		return ObjUtil.mergeArrays(rc, Action.class);
	}
}
