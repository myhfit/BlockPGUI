package bp.ui.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.swing.Action;

import bp.data.BPXYData;
import bp.util.ClassUtil;

public class BPXYDataCloneActions
{
	public final static Action[] getActions(BPXYData data, Runnable loaddatafunc)
	{
		List<Action> rc = new ArrayList<Action>();

		ServiceLoader<BPDataActionFactory> facs = ClassUtil.getExtensionServices(BPDataActionFactory.class);
		for (BPDataActionFactory fac : facs)
		{
			Action[] acts = fac.getAction(data, BPDataActionFactory.ACTIONNAME_CLONEDATA, loaddatafunc);
			if (acts != null)
			{
				for (Action act : acts)
				{
					rc.add(act);
				}
			}
		}
		return rc.toArray(new Action[rc.size()]);
	}
}
