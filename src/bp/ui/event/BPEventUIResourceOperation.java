package bp.ui.event;

import java.util.List;
import java.util.Map;

import bp.event.BPEventUI;
import bp.res.BPResource;

public class BPEventUIResourceOperation extends BPEventUI
{
	public final static String EVENTKEY_RES_OP = "E_UI_RES_OP";

	public final static String RES_ACTION = "RES_ACTION";

	public BPEventUIResourceOperation(String subkey, Object[] datas, Map<String, Object> context)
	{
		this.key = EVENTKEY_RES_OP;
		this.subkey = subkey;
		this.datas = datas;
		this.context = context;
	}

	public BPEventUIResourceOperation(String subkey, BPResource res, Map<String, Object> context)
	{
		this(subkey, new Object[] { new BPResource[] { res } }, context);
	}

	public BPEventUIResourceOperation(String subkey, BPResource[] ress, Map<String, Object> context)
	{
		this(subkey, new Object[] { ress }, context);
	}

	public BPEventUIResourceOperation(String subkey, List<BPResource> ress, Map<String, Object> context)
	{
		this(subkey, new Object[] { ress.toArray(new BPResource[ress.size()]) }, context);
	}

	public BPResource getSelectedResource()
	{
		BPResource rc = null;
		Object obj0 = datas[0];
		if (obj0 == null)
			rc = null;
		if (obj0 instanceof BPResource[])
		{
			BPResource[] resources = (BPResource[]) datas[0];
			if (resources != null && resources.length > 0)
			{
				rc = resources[resources.length - 1];
			}
		}
		return rc;
	}

	public BPResource[] getSelectedResources()
	{
		BPResource[] rc = null;
		Object obj0 = datas[0];
		if (obj0 == null)
			rc = null;
		if (obj0 instanceof BPResource[])
		{
			BPResource[] resources = (BPResource[]) datas[0];
			if (resources != null && resources.length > 0)
			{
				rc = new BPResource[resources.length];
				System.arraycopy(resources, 0, rc, 0, rc.length);
			}
		}
		return rc;
	}

	public Object[] getOpenNodeParams()
	{
		Object[] ps = new Object[2];
		if (datas.length > 1)
			ps[0] = datas[1];
		if (datas.length > 2)
			ps[1] = datas[2];
		return ps;
	}

	public Object[] getActionParams()
	{
		Object ps = datas[2];
		Object[] rc = null;
		if (ps != null && ps instanceof Object[])
		{
			rc = (Object[]) ps;
		}
		else
		{
			rc = new Object[] { ps };
		}
		return rc;
	}

	public String getActionName()
	{
		return (String) datas[1];
	}

	public static BPEventUIResourceOperation makeActionEvent(String actionname, BPResource res, Map<String, Object> context, Object... actionparams)
	{
		return new BPEventUIResourceOperation(RES_ACTION, new Object[] { new BPResource[] { res }, actionname, actionparams }, context);
	}
}