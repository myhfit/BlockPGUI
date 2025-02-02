package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;

import bp.config.UIConfigs;
import bp.data.BPMData;
import bp.data.BPYData;
import bp.ui.form.BPForm;
import bp.ui.form.BPFormManager;
import bp.ui.form.BPFormResultable;
import bp.ui.scomp.BPList;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;
import bp.util.LogicUtil.WeakRefGoConsumer;
import bp.util.LogicUtil.WeakRefGoFunction;

public class BPDialogCommonCategoryView<T, V> extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4508633101586885688L;
	private List<T> m_cats;
	private WeakReference<Function<T, Object>> m_rendererref;
	private WeakRefGoFunction<T, V> m_transfuncref;
	protected BPList<T> m_lstfacs;
	protected BPForm<?> m_form;
	protected V m_result;
	protected WeakRefGoConsumer<BPForm<?>> m_initformcb;
	protected boolean m_editable;

	public boolean doCallCommonAction(int command)
	{
		if (command == COMMAND_OK)
		{
			BPForm<?> form = m_form;
			if (form instanceof BPFormResultable)
			{
				m_result = ((BPFormResultable<?>) form).getResult();
			}
		}
		return false;
	}

	protected void initUIComponents()
	{
		m_lstfacs = new BPList<T>();
		m_lstfacs.setModel(new BPList.BPListModel<T>());
		m_lstfacs.setCellRenderer(new BPList.BPListRenderer(this::transCatName));
		m_lstfacs.setListFont();

		JPanel leftpan = new JPanel();
		leftpan.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));
		leftpan.setLayout(new BorderLayout());
		leftpan.add(m_lstfacs, BorderLayout.CENTER);
		leftpan.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(150, 0)));

		setLayout(new BorderLayout());
		add(leftpan, BorderLayout.WEST);

		setCommandBarMode(COMMANDBAR_OK_CANCEL);
		setModal(true);
	}

	public void setCommandBarMode(int commonmode)
	{
		super.setCommandBarMode(commonmode);
	}

	@SuppressWarnings("unchecked")
	protected Object transCatName(Object data)
	{
		WeakReference<Function<T, Object>> rendererref = m_rendererref;
		if (rendererref != null)
		{
			Function<T, Object> renderer = rendererref.get();
			if (renderer != null)
				return renderer.apply((T) data);
		}
		return data;
	}

	public void setup(List<T> cats, Function<T, Object> renderer, Function<T, V> transfunc, boolean editable)
	{
		m_cats = cats;
		m_rendererref = new WeakReference<Function<T, Object>>(renderer);
		m_transfuncref = new WeakRefGoFunction<T, V>(transfunc);
		m_editable = editable;
		initDatas();
	}

	protected void initDatas()
	{
		if (m_cats == null)
			return;
		((BPList.BPListModel<T>) m_lstfacs.getModel()).setDatas(m_cats);
		m_lstfacs.removeListSelectionListener(this::onListSelectionChange);
		m_lstfacs.addListSelectionListener(this::onListSelectionChange);
	}

	protected void onListSelectionChange(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			if (m_form != null)
				remove(m_form.getComponent());
			V value = m_transfuncref.apply(m_lstfacs.getSelectedValue());
			BPForm<?> form = ClassUtil.tryLoopSuperClass((cls) -> BPFormManager.getForm(cls.getName()), value.getClass(), Object.class);
			if (form != null)
			{
				WeakRefGoConsumer<BPForm<?>> initformcb = m_initformcb;
				if (initformcb != null)
					initformcb.accept(form);
				add(form.getComponent(), BorderLayout.CENTER);
				m_form = form;
				if (value instanceof BPMData)
				{
					form.showData(((BPMData) value).getMappedData(), m_editable);
				}
				else if (value instanceof BPYData)
				{
					form.showData(((BPYData) value).getMappedData(), m_editable);
				}
			}
			validate();
			repaint();
		}
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(800, 600)));
		super.setPrefers();
	}

	public void setInitFormCallback(Consumer<BPForm<?>> cb)
	{
		m_initformcb = new WeakRefGoConsumer<BPForm<?>>(cb);
	}
}
