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
import bp.task.BPTask;
import bp.task.BPTaskFactory;
import bp.ui.form.BPForm;
import bp.ui.form.BPFormManager;
import bp.ui.form.BPFormPanelMap;
import bp.ui.scomp.BPList;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;
import bp.util.ObjUtil;
import bp.util.LogicUtil.WeakRefGo;
import bp.util.LogicUtil.WeakRefGoConsumer;

public class BPDialogNewTask extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3556973484245269229L;

	protected BPList<BPTaskFactory> m_lstfacs;
	protected BPForm<?> m_form;
	protected BPTask<?> m_task;

	protected WeakRefGoConsumer<BPForm<?>> m_initformcb;
	protected WeakRefGo<Predicate<BPTaskFactory>> m_filter;

	public BPDialogNewTask()
	{
	}

	protected void initUIComponents()
	{
		m_lstfacs = new BPList<BPTaskFactory>();
		m_lstfacs.setModel(new BPList.BPListModel<BPTaskFactory>());
		m_lstfacs.setCellRenderer(new BPList.BPListRenderer(BPDialogNewTask::transFacName));
		m_lstfacs.setListFont();

		JPanel leftpan = new JPanel();
		leftpan.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));
		leftpan.setLayout(new BorderLayout());
		leftpan.add(m_lstfacs, BorderLayout.CENTER);
		leftpan.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(150, 0)));

		setLayout(new BorderLayout());
		add(leftpan, BorderLayout.WEST);

		setCommandBarMode(COMMANDBAR_OK_CANCEL);
		setTitle("BlockP - New Task");
		setModal(true);
	}

	private static Object transFacName(Object facobj)
	{
		BPTaskFactory fac = (BPTaskFactory) facobj;
		return fac == null ? "" : fac.getName();
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(800, 600)));
		super.setPrefers();
	}

	public void setFilter(Predicate<BPTaskFactory> filter)
	{
		m_filter = new WeakRefGo<Predicate<BPTaskFactory>>(filter);
		initDatas();
	}

	protected void initDatas()
	{
		ServiceLoader<BPTaskFactory> facs = ClassUtil.getExtensionServices(BPTaskFactory.class);
		List<BPTaskFactory> datas = new ArrayList<BPTaskFactory>();
		WeakRefGo<Predicate<BPTaskFactory>> filter = m_filter;
		for (BPTaskFactory fac : facs)
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
		((BPList.BPListModel<BPTaskFactory>) m_lstfacs.getModel()).setDatas(datas);
		m_lstfacs.addListSelectionListener(this::onListSelectionChange);
	}

	public void setInitFormCallback(Consumer<BPForm<?>> cb)
	{
		m_initformcb = new WeakRefGoConsumer<BPForm<?>>(cb);
	}

	protected void onListSelectionChange(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			if (m_form != null)
				remove(m_form.getComponent());
			BPForm<?> form = ClassUtil.tryLoopSuperClass((cls) -> BPFormManager.getForm(cls.getName()), m_lstfacs.getSelectedValue().getTaskClass(), BPTask.class);
			if (form != null)
			{
				WeakRefGoConsumer<BPForm<?>> initformcb = m_initformcb;
				if (initformcb != null)
					initformcb.accept(form);
				add(form.getComponent(), BorderLayout.CENTER);
				m_form = form;
			}
			if (form instanceof BPFormPanelMap)
				form.showData(ObjUtil.makeMap("name", ""));
			validate();
			repaint();
		}
	}

	protected BPTask<?> createTask(Map<String, Object> data)
	{
		BPTask<?> rc = null;
		rc = m_lstfacs.getSelectedValue().create(data);
		return rc;
	}

	public BPTask<?> getTask()
	{
		return m_task;
	}

	public boolean doCallCommonAction(int command)
	{
		if (command == COMMAND_OK)
		{
			if (m_form.validateForm())
			{
				Map<String, Object> data = m_form.getFormData();
				m_task = createTask(data);
			}
			else
			{
				return true;
			}
		}
		return false;
	}
}