package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;

import bp.config.BPSetting;
import bp.config.UIConfigs;
import bp.data.BPDataConsumer.BPDataConsumerDataHolder;
import bp.transform.BPTransformerRuleFilter;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.dialog.BPDialogSetting;
import bp.ui.res.icon.BPIconResV;
import bp.ui.util.UIUtil;
import bp.util.LogicUtil.WeakRefGo;

public class BPFilterDataListPanel<DATA> extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1365576186555398780L;

	protected BPList<DATA> m_lstresults;
	protected JScrollPane m_scroll;
	protected JPanel m_pantop;
	protected BPToolBarSQ m_panact;
	protected BPToolBarSQ m_pansetting;
	protected BPComboBox<WeakRefGo<BPTransformerRuleFilter<DATA>>> m_cmbfilters;
	protected BPTextField m_txtrule;
	protected JPanel m_pandetail;
	protected BPAction m_actsetting;
	protected boolean m_detailhide;

	protected List<WeakRefGo<BPTransformerRuleFilter<DATA>>> m_filterrefs;
	protected WeakRefGo<BiFunction<DATA, JComponent, JComponent>> m_dbuicbref;

	protected List<DATA> m_results;
	protected List<DATA> m_srcs;

	public BPFilterDataListPanel()
	{
		m_srcs = new ArrayList<>();
		m_results = new ArrayList<>();
		m_filterrefs = new ArrayList<>();
		initUI();
	}

	protected void initUI()
	{
		removeAll();

		JPanel pantop2 = new JPanel();
		m_txtrule = new BPTextField();
		m_cmbfilters = new BPComboBox<>();
		m_panact = new BPToolBarSQ();
		m_pantop = new JPanel();
		m_lstresults = new BPList<DATA>();
		m_scroll = new JScrollPane();
		BPLabel lblrule = new BPLabel(" Rule: ");
		JPanel pancenter = new JPanel();
		m_pandetail = new JPanel();
		JPanel pantxt = new JPanel();
		m_pansetting = new BPToolBarSQ();

		m_actsetting = BPAction.build("settings").callback(this::onSettingRule).vIcon(BPIconResV.EDIT()).tooltip("Settings").getAction();
		m_panact.setActions(makeTopActions());
		m_panact.setPreferredSize(null);
		m_pansetting.setPreferredSize(null);

		lblrule.setLabelFont();
		m_cmbfilters.setListFont();
		m_lstresults.setListFont();
		m_txtrule.setLabelFont();
		pantop2.setBackground(UIConfigs.COLOR_TEXTBG());
		m_pansetting.setBackground(UIConfigs.COLOR_TEXTBG());
		m_cmbfilters.setRenderer(new BPList.BPListRenderer(this::getFilterName));
		m_cmbfilters.setModel(new BPComboBox.BPComboBoxModel<>());
		m_cmbfilters.getBPModel().setDatas(m_filterrefs);
		m_cmbfilters.replaceWBorder();
		m_scroll.setViewportView(m_lstresults);
		m_scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_pantop.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
		m_pandetail.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_WEAKBORDER()));
		pantxt.setBorder(new MatteBorder(1, 1, 1, 1, UIConfigs.COLOR_WEAKBORDER()));

		m_pandetail.setVisible(false);

		m_lstresults.setModel(new BPList.BPListModel<>());
		m_lstresults.getBPModel().setDatas(m_results);

		pantop2.setLayout(new BorderLayout());
		m_pantop.setLayout(new BorderLayout());
		pantxt.setLayout(new BorderLayout());
		pancenter.setLayout(new BorderLayout());
		m_pandetail.setLayout(new BorderLayout());
		setLayout(new BorderLayout());

		m_lstresults.addListSelectionListener(this::onResultSelect);
		m_cmbfilters.addItemListener(this::onFilterChanged);
		m_txtrule.addKeyListener(new UIUtil.BPKeyListener(null, this::onFilterKeyDown, null));

		pantop2.add(lblrule, BorderLayout.EAST);
		pantop2.add(m_pansetting, BorderLayout.WEST);
		pantxt.add(pantop2, BorderLayout.WEST);
		pantxt.add(m_txtrule, BorderLayout.CENTER);
		pancenter.add(m_scroll, BorderLayout.CENTER);
		pancenter.add(m_pandetail, BorderLayout.EAST);
		m_pantop.add(m_cmbfilters, BorderLayout.WEST);
		m_pantop.add(pantxt, BorderLayout.CENTER);
		m_pantop.add(m_panact, BorderLayout.EAST);
		add(m_pantop, BorderLayout.NORTH);
		add(pancenter, BorderLayout.CENTER);
	}

	protected Action[] makeTopActions()
	{
		BPAction acttoggledetail = BPAction.build("toggledetail").callback(this::toggleDetail).vIcon(BPIconResV.DROPDOWN()).tooltip("Toggle detail").getAction();
		BPAction actfilter = BPAction.build("run").callback(this::doFilter).vIcon(BPIconResV.START()).tooltip("Run(F5)").acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)).getAction();
		return new Action[] { actfilter, acttoggledetail };
	}

	protected void onFilterKeyDown(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			doFilter(null);
			m_lstresults.requestFocus();
		}
	}

	protected void onFilterChanged(ItemEvent e)
	{
		BPTransformerRuleFilter<DATA> tf = getSelectedFilter();
		if (tf.getSetting() != null)
			m_pansetting.setActions(new Action[] { m_actsetting });
		else
			m_pansetting.setActions(new Action[] {});
		m_pantop.updateUI();
	}

	protected void onSettingRule(ActionEvent e)
	{
		BPTransformerRuleFilter<DATA> tf = getSelectedFilter();
		BPSetting setting = tf.getSetting();
		if (setting != null)
		{
			BPDialogSetting dlg = new BPDialogSetting();
			dlg.setSetting(setting);
			dlg.setVisible(true);
			BPSetting r = dlg.getResult();
			if (r != null)
			{
				tf.setSetting(r);
				m_txtrule.setText(tf.getRule());
				tf.setRule(null);
			}
		}
	}

	protected void onResultSelect(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			DATA d = m_lstresults.getSelectedValue();
			JComponent comp0 = m_pandetail.getComponentCount() > 0 ? (JComponent) m_pandetail.getComponent(0) : null;
			JComponent comp = m_dbuicbref.exec(cb -> cb.apply(d, comp0));
			m_pandetail.removeAll();
			if (comp != null)
			{
				m_pandetail.add(comp);
				if (!m_detailhide)
					m_pandetail.setVisible(true);
			}
			else
			{
				m_pandetail.setVisible(false);
			}
			updateUI();
		}
	}

	@SuppressWarnings("unchecked")
	protected String getFilterName(Object filter)
	{
		if (filter == null)
			return "";
		return ((WeakRefGo<BPTransformerRuleFilter<DATA>>) filter).exec(f -> f.getInfo());
	}

	@SuppressWarnings("unchecked")
	public BPTransformerRuleFilter<DATA> getSelectedFilter()
	{
		return ((WeakRefGo<BPTransformerRuleFilter<DATA>>) m_cmbfilters.getSelectedItem()).get();
	}

	public void setDatas(Collection<DATA> srcs)
	{
		m_srcs.addAll(srcs);
	}

	public void setFilters(List<? extends BPTransformerRuleFilter<DATA>> filters)
	{
		m_filterrefs.clear();
		for (BPTransformerRuleFilter<DATA> filter : filters)
			m_filterrefs.add(new WeakRefGo<>(filter));
		m_cmbfilters.updateUI();
		if (filters.size() > 0)
			m_cmbfilters.setSelectedIndex(0);
	}

	public void setDataRenderer(Function<Object, ?> renderer)
	{
		m_lstresults.setCellRenderer(new BPList.BPListRendererWeakRef(renderer));
	}

	public void setDetailUIBuilder(BiFunction<DATA, JComponent, JComponent> cb)
	{
		m_dbuicbref = new WeakRefGo<>(cb);
	}

	public List<DATA> getResults()
	{
		return m_results;
	}

	protected void toggleDetail(ActionEvent e)
	{
		m_detailhide = m_pandetail.isVisible();
		m_pandetail.setVisible(!m_detailhide);

	}

	public void setLockEdit(boolean flag)
	{
		m_cmbfilters.setEnabled(!flag);
		m_pansetting.setVisible(!flag);
		m_txtrule.setEnabled(!flag);
		m_panact.setVisible(!flag);
	}

	public void setDetailVisible(boolean flag)
	{
		m_detailhide = !flag;
		m_pandetail.setVisible(flag);
	}

	protected void doFilter(ActionEvent e)
	{
		BPTransformerRuleFilter<DATA> f = getSelectedFilter();
		if (f == null)
			return;
		f.setRule(m_txtrule.getText());
		BPDataConsumerDataHolder<List<DATA>> out = new BPDataConsumerDataHolder<>();
		f.setOutput(out);
		f.accept(m_srcs);
		f.setOutput(null);
		List<DATA> datas = out.getData();
		m_results.clear();
		m_results.addAll(datas);
		m_lstresults.updateUI();
	}

	public DATA getSelectedData()
	{
		return m_lstresults.getSelectedValue();
	}

	public List<DATA> getSelectedDatas()
	{
		return m_lstresults.getSelectedValuesList();
	}

	public void setup(List<? extends BPTransformerRuleFilter<DATA>> filters, BiFunction<DATA, JComponent, JComponent> duicb, List<DATA> datas, Function<Object, ?> renderer)
	{
		setFilters(filters);
		setDetailUIBuilder(duicb);
		setDatas(datas);
		if (renderer != null)
			setDataRenderer(renderer);
	}

	public boolean mergeFilterAndRun(BPTransformerRuleFilter<DATA> f)
	{
		String clsname = f.getClass().getName();
		for (WeakRefGo<BPTransformerRuleFilter<DATA>> fref : m_filterrefs)
		{
			BPTransformerRuleFilter<DATA> f2 = fref.get();
			if (clsname.equals(f2.getClass().getName()))
			{
				m_cmbfilters.setSelectedItem(fref);
				m_txtrule.setText(f.getRule());
				f2.setRule(f.getRule());
				doFilter(null);
				return true;
			}
		}
		return false;
	}
}
