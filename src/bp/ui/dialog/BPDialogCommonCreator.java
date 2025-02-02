package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;

import bp.config.UIConfigs;
import bp.data.BPInstanceFactory;
import bp.ui.form.BPForm;
import bp.ui.form.BPFormManager;
import bp.ui.scomp.BPList;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;
import bp.util.LogicUtil.WeakRefGo;
import bp.util.LogicUtil.WeakRefGoConsumer;

public class BPDialogCommonCreator<T> extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4360241941110553592L;

	protected BPList<BPInstanceFactory<T>> m_lstfacs;
	protected BPForm<?> m_form;
	protected T m_result;

	protected WeakRefGo<Predicate<BPInstanceFactory<T>>> m_filter;
	protected Class<? extends BPInstanceFactory<T>> m_facintf;
	protected WeakRefGoConsumer<BPForm<?>> m_initformcb;

	public BPDialogCommonCreator()
	{
	}

	protected void initUIComponents()
	{
		m_lstfacs = new BPList<BPInstanceFactory<T>>();
		m_lstfacs.setModel(new BPList.BPListModel<BPInstanceFactory<T>>());
		m_lstfacs.setCellRenderer(new BPList.BPListRenderer(BPDialogCommonCreator::transFacName));
		m_lstfacs.setListFont();

		JPanel leftpan = new JPanel();
		leftpan.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));
		leftpan.setLayout(new BorderLayout());
		leftpan.add(m_lstfacs, BorderLayout.CENTER);
		leftpan.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(150, 0)));

		setLayout(new BorderLayout());
		add(leftpan, BorderLayout.WEST);

		setCommandBarMode(COMMANDBAR_OK_CANCEL);
		setTitle("BlockP - Creator");
		setModal(true);
	}

	private static Object transFacName(Object facobj)
	{
		BPInstanceFactory<?> fac = (BPInstanceFactory<?>) facobj;
		return fac == null ? "" : fac.getName();
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(800, 600)));
		super.setPrefers();
	}

	public void setFilter(Predicate<BPInstanceFactory<T>> filter)
	{
		m_filter = new WeakRefGo<Predicate<BPInstanceFactory<T>>>(filter);
		initDatas();
	}

	public void setFactoryInterface(Class<? extends BPInstanceFactory<T>> facintf)
	{
		m_facintf = facintf;
		initDatas();
	}

	public void setInitFormCallback(Consumer<BPForm<?>> cb)
	{
		m_initformcb = new WeakRefGoConsumer<BPForm<?>>(cb);
	}

	protected Class<? extends BPInstanceFactory<T>> getDefaultFactoryInterface()
	{
		return null;
	}

	protected void initDatas()
	{
		Class<? extends BPInstanceFactory<T>> facintf = m_facintf;
		if (facintf == null)
			facintf = getDefaultFactoryInterface();
		if (facintf == null)
			return;
		ServiceLoader<? extends BPInstanceFactory<T>> facs = ClassUtil.getExtensionServices(facintf);
		List<BPInstanceFactory<T>> datas = new ArrayList<BPInstanceFactory<T>>();
		WeakRefGo<Predicate<BPInstanceFactory<T>>> filter = m_filter;
		for (BPInstanceFactory<T> fac : facs)
		{
			if (filter != null)
			{
				Boolean f = filter.exec(func -> func.test(fac));
				if (f == null || f)
					datas.add(fac);
			}
			else
				datas.add(fac);
		}
		((BPList.BPListModel<BPInstanceFactory<T>>) m_lstfacs.getModel()).setDatas(datas);
		m_lstfacs.removeListSelectionListener(this::onListSelectionChange);
		m_lstfacs.addListSelectionListener(this::onListSelectionChange);
	}

	protected void onListSelectionChange(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			if (m_form != null)
				remove(m_form.getComponent());
			BPForm<?> form = ClassUtil.tryLoopSuperClass((cls) -> BPFormManager.getForm(cls.getName()), m_lstfacs.getSelectedValue().getInstanceClass(), Object.class);
			if (form != null)
			{
				WeakRefGoConsumer<BPForm<?>> initformcb = m_initformcb;
				if (initformcb != null)
					initformcb.accept(form);
				add(form.getComponent(), BorderLayout.CENTER);
				m_form = form;
			}
			validate();
			repaint();
		}
	}

	protected T create(Map<String, Object> data)
	{
		T rc = null;
		rc = m_lstfacs.getSelectedValue().create(data);
		return rc;
	}

	public T getResult()
	{
		return m_result;
	}

	public boolean doCallCommonAction(int command)
	{
		if (command == COMMAND_OK)
		{
			if (m_form.validateForm())
			{
				Map<String, Object> data = m_form.getFormData();
				m_result = create(data);

			}
			else
			{
				return true;
			}
		}
		return false;
	}
}