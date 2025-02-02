package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class BPListView<T> extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5509258516018215055L;

	protected JScrollPane m_scroll;
	protected BPListViewItemBuilder<T> m_itembuilder;

	protected BPListViewModel<T> m_model;
	protected List<T> m_datas;

	public BPListView()
	{
		m_itembuilder = new BPListViewItemBuilderBase<T>();
		
		m_scroll=new JScrollPane();
		
		setLayout(new BorderLayout());
		add(m_scroll, BorderLayout.CENTER);
	}

	public void setItemBuilder(BPListViewItemBuilder<T> builder)
	{
		m_itembuilder = builder;
	}

	public void setModel(BPListViewModel<T> model)
	{
		m_model = model;
		rebuildItems();
	}

	public void rebuildItems()
	{

	}

	public void clearResources()
	{
		m_datas = null;
	}

	public static interface BPListViewItemBuilder<T>
	{
		Component build(T data, Map<String, Object> viewitems);
	}

	public static class BPListViewItemBuilderBase<T> implements BPListViewItemBuilder<T>
	{
		public Component build(T data, Map<String, Object> viewitems)
		{
			return null;
		}
	}

	@FunctionalInterface
	public static interface BPListViewModel<T>
	{
		Map<String, Object> getViewItems(T data);
	}
}
