package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Predicate;

import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;

import bp.config.UIConfigs;
import bp.schedule.BPSchedule;
import bp.schedule.BPScheduleFactory;
import bp.ui.form.BPForm;
import bp.ui.form.BPFormManager;
import bp.ui.scomp.BPList;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;
import bp.util.LogicUtil.WeakRefGo;

public class BPDialogNewSchedule extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7948870912711775646L;
	
	protected BPList<BPScheduleFactory> m_lstfacs;
	protected BPForm<?> m_form;
	protected BPSchedule m_schedule;

	private WeakRefGo<Predicate<BPScheduleFactory>> m_filter;

	public BPDialogNewSchedule()
	{
	}

	protected void initUIComponents()
	{
		m_lstfacs = new BPList<BPScheduleFactory>();
		m_lstfacs.setModel(new BPList.BPListModel<BPScheduleFactory>());
		m_lstfacs.setCellRenderer(new BPList.BPListRenderer(BPDialogNewSchedule::transFacName));
		m_lstfacs.setListFont();

		JPanel leftpan = new JPanel();
		leftpan.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));
		leftpan.setLayout(new BorderLayout());
		leftpan.add(m_lstfacs, BorderLayout.CENTER);
		leftpan.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(150, 0)));

		setLayout(new BorderLayout());
		add(leftpan, BorderLayout.WEST);

		setCommandBarMode(COMMANDBAR_OK_CANCEL);
		setTitle("New Schedule");
		setModal(true);
	}

	private static Object transFacName(Object facobj)
	{
		BPScheduleFactory fac = (BPScheduleFactory) facobj;
		return fac == null ? "" : fac.getName();
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(800, 600)));
		super.setPrefers();
	}

	public void setFilter(Predicate<BPScheduleFactory> filter)
	{
		m_filter = new WeakRefGo<Predicate<BPScheduleFactory>>(filter);
		initDatas();
	}

	protected void initDatas()
	{
		ServiceLoader<BPScheduleFactory> facs = ClassUtil.getExtensionServices(BPScheduleFactory.class);
		List<BPScheduleFactory> datas = new ArrayList<BPScheduleFactory>();
		WeakRefGo<Predicate<BPScheduleFactory>> filter = m_filter;
		for (BPScheduleFactory fac : facs)
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
		((BPList.BPListModel<BPScheduleFactory>) m_lstfacs.getModel()).setDatas(datas);
		m_lstfacs.addListSelectionListener(this::onListSelectionChange);
	}

	protected void onListSelectionChange(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			if (m_form != null)
				remove(m_form.getComponent());
			m_form = ClassUtil.tryLoopSuperClass((cls) -> BPFormManager.getForm(cls.getName()), m_lstfacs.getSelectedValue().getScheduleClass(), BPSchedule.class);
			if (m_form != null)
			{
				add(m_form.getComponent(), BorderLayout.CENTER);
			}
			validate();
			repaint();
		}
	}

	protected BPSchedule createSchedule(Map<String, Object> data)
	{
		BPSchedule rc = null;
		rc = m_lstfacs.getSelectedValue().create(data);
		return rc;
	}

	public BPSchedule getSchedule()
	{
		return m_schedule;
	}

	public boolean doCallCommonAction(int command)
	{
		if (command == COMMAND_OK)
		{
			if (m_form.validateForm())
			{
				Map<String, Object> data = m_form.getFormData();
				m_schedule = createSchedule(data);

			}
			else
			{
				return true;
			}
		}
		return false;
	}
}