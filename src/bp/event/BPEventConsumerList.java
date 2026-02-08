package bp.event;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BPEventConsumerList<T> implements Consumer<T>
{
	protected List<WeakReference<Consumer<T>>> m_cs;

	public BPEventConsumerList()
	{
		m_cs = new ArrayList<WeakReference<Consumer<T>>>();
	}

	public void addConsumer(Consumer<T> cb)
	{
		m_cs.add(new WeakReference<>(cb));
	}

	public void accept(T data)
	{
		List<Integer> ds = new ArrayList<Integer>();
		List<WeakReference<Consumer<T>>> cs = m_cs;
		for (int i = 0; i < cs.size(); i++)
		{
			Consumer<T> c = cs.get(i).get();
			if (c == null)
				ds.add(i);
			else
				c.accept(data);
		}
		if (ds.size() > 0)
		{
			for (int i = ds.size() - 1; i >= 0; i--)
			{
				cs.remove((int) (Integer) ds.get(i));
			}
		}
	}
}
