package bp.ui.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import bp.format.BPFormatUnknown;
import bp.util.ClassUtil;
import bp.util.LockUtil;
import bp.util.LogicUtil;

public class BPEditorManager
{
	private final static Map<String, List<BPEditorFactory>> S_FACS = new HashMap<String, List<BPEditorFactory>>();
	private final static List<BPEditorFactory> S_FACLIST = new ArrayList<BPEditorFactory>();

	private final static ReadWriteLock S_LOCK = new ReentrantReadWriteLock();

	public final static void init()
	{
		ServiceLoader<BPEditorFactory> facs = ClassUtil.getExtensionServices(BPEditorFactory.class);
		if (facs != null)
		{
			LockUtil.rwLock(S_LOCK, true, () ->
			{
				S_FACS.clear();
				S_FACLIST.clear();
				for (BPEditorFactory fac : facs)
				{
					String[] formats = fac.getFormats();
					for (String format : formats)
					{
						List<BPEditorFactory> tfacs = S_FACS.get(format);
						if (tfacs == null)
						{
							tfacs = new ArrayList<BPEditorFactory>();
							S_FACS.put(format, tfacs);
						}
						tfacs.add(fac);
					}
					S_FACLIST.add(fac);
				}
			});
		}
	}

	public final static BPEditorFactory getFactory(String formatkey)
	{
		List<BPEditorFactory> facs = getFactories(formatkey);
		if (facs != null)
		{
			for (int i = facs.size() - 1; i >= 0; i--)
			{
				BPEditorFactory fac = facs.get(i);
				if (fac.handleFormat(formatkey))
					return fac;
			}
			if (facs.size() > 0)
				return facs.get(facs.size() - 1);
		}
		return null;
	}

	public final static BPEditorFactory getFactory(String formatkey, String facname)
	{
		String fkey = formatkey != null ? formatkey : BPFormatUnknown.FORMAT_NA;
		List<BPEditorFactory> facs = getFactories(fkey);
		for (int i = facs.size() - 1; i >= 0; i--)
		{
			BPEditorFactory fac = facs.get(i);
			if (facname.equals(fac.getName()))
				return fac;
		}
		return null;
	}

	public final static List<BPEditorFactory> getFactories(String formatkey)
	{
		return LockUtil.rwLock(S_LOCK, false, () -> LogicUtil.IFVR(S_FACS.get(formatkey), (facs) -> new ArrayList<BPEditorFactory>(facs)));
	}

	public final static List<BPEditorFactory> getAllFactories()
	{
		return LockUtil.rwLock(S_LOCK, false, () -> new ArrayList<BPEditorFactory>(S_FACLIST));
	}

	public final static Map<String, List<BPEditorFactory>> getFactoryMap()
	{
		return LockUtil.rwLock(S_LOCK, false, () ->
		{
			Map<String, List<BPEditorFactory>> rc = new HashMap<String, List<BPEditorFactory>>();
			for (Entry<String, List<BPEditorFactory>> entry : S_FACS.entrySet())
			{
				rc.put(entry.getKey(), ((entry.getValue() == null) ? null : new ArrayList<BPEditorFactory>(entry.getValue())));
			}
			return rc;
		});
	}

	public final static void registerFactory(String formatkey, BPEditorFactory fac)
	{
		LockUtil.rwLock(S_LOCK, true, () ->
		{
			List<BPEditorFactory> facs = S_FACS.get(formatkey);
			if (facs == null)
			{
				facs = new ArrayList<BPEditorFactory>();
				S_FACS.put(formatkey, facs);
				facs.add(fac);
			}
			else
			{
				if (!facs.contains(fac))
					facs.add(fac);
			}
		});
	}

	public final static boolean unregisterFactory(String formatkey, BPEditorFactory fac)
	{
		return LockUtil.rwLock(S_LOCK, true, () ->
		{
			List<BPEditorFactory> facs = S_FACS.get(formatkey);
			if (facs != null && facs.contains(fac))
				return facs.remove(fac);
			return false;
		});
	}
}
