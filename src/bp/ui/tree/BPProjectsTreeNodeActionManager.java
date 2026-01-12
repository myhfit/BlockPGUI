package bp.ui.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.Action;

import bp.res.BPResource;
import bp.ui.scomp.BPTree;
import bp.util.ClassUtil;
import bp.util.LockUtil;

public class BPProjectsTreeNodeActionManager
{
	public final static Map<String, BPProjectsTreeNodeActionFactory> S_FACS = new ConcurrentHashMap<String, BPProjectsTreeNodeActionFactory>();

	protected final static ReadWriteLock S_LOCK = new ReentrantReadWriteLock();

	private static volatile boolean inited = false;

	public final static void init()
	{
		S_FACS.clear();
		LockUtil.rwLock(S_LOCK, true, () ->
		{
			if (!inited)
			{
				inited = true;
				ServiceLoader<BPProjectsTreeNodeActionFactory> facs = ClassUtil.getExtensionServices(BPProjectsTreeNodeActionFactory.class);
				for (BPProjectsTreeNodeActionFactory fac : facs)
				{
					fac.register(BPProjectsTreeNodeActionManager::register);
				}
			}
		});
	}

	public final static void register(String classname, BPProjectsTreeNodeActionFactory fac)
	{
		S_FACS.put(classname, fac);
	}

	public final static BPProjectsTreeNodeActionFactory getFactory(BPResource res)
	{
		if (!inited)
		{
			init();
		}
		return LockUtil.rwLock(S_LOCK, false, () ->
		{
			if (res == null)
				return null;
			return S_FACS.get(res.getClass().getName());
		});
	}

	public final static List<Action> getActions(BPTreeComponent<BPTree> tree, BPResource res, int channelid)
	{
		List<Action> rc = null;
		if (res != null)
		{
			rc = new ArrayList<Action>();
			BPProjectsTreeNodeActionFactory fac = getFactory(res);
			if (fac != null)
			{
				return fac.getActions(tree, res, channelid);
			}
		}
		return rc;
	}

	public static boolean callOpen(BPTree tree, BPResource res, int channelid)
	{
		BPProjectsTreeNodeActionFactory fac = getFactory(res);
		if (fac != null)
			return fac.callOpen(tree, res, channelid);
		return false;
	}
}
