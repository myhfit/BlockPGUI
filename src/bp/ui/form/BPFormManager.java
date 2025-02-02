package bp.ui.form;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bp.util.Std;

public class BPFormManager
{
	protected final static Map<String, Class<? extends BPForm<?>>> S_FORMMAP = new ConcurrentHashMap<String, Class<? extends BPForm<?>>>();

	public final static BPForm<?> getForm(String key)
	{
		BPForm<?> rc = null;
		Class<? extends BPForm<?>> cls = S_FORMMAP.get(key);
		if (cls != null)
		{
			try
			{
				rc = cls.getConstructor().newInstance();
			}
			catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
			{
				Std.err(e);
			}
		}
		return rc;
	}

	public final static boolean hasForm(String key)
	{
		return S_FORMMAP.get(key) != null;
	}

	public final static boolean containsKey(String key)
	{
		return S_FORMMAP.containsKey(key);
	}

	public final static void registerForm(String key, Class<? extends BPForm<?>> formclass)
	{
		S_FORMMAP.put(key, formclass);
	}
}
