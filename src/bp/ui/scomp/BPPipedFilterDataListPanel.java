package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.res.BPResource;
import bp.res.BPResourceIO;
import bp.transform.BPTransformerRuleFilter;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.container.BPToolBarSQ;
import bp.ui.util.CommonUIOperations;
import bp.util.IOUtil;
import bp.util.JSONUtil;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public class BPPipedFilterDataListPanel<DATA> extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3497334300986405841L;

	protected JPanel m_leftpan;
	protected JPanel m_mainpan;

	protected List<DATA> m_srcs;
	protected List<? extends BPTransformerRuleFilter<DATA>> m_filters;
	protected Function<Object, ?> m_renderer;
	protected BiFunction<DATA, JComponent, JComponent> m_duicb;

	public BPPipedFilterDataListPanel()
	{
		m_leftpan = new JPanel();
		m_mainpan = new JPanel();
		JPanel mp = new JPanel();
		BPToolBarSQ toolbar = new BPToolBarSQ();
		BPAction actadd = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNADD, BPActionConstCommon.ACT_BTNADD_PUSH, this::onAdd);
		BPAction actdel = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNDEL, BPActionConstCommon.ACT_BTNDEL_POP, this::onBack);
		BPAction actsave = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNSAVE, this::onSave);
		toolbar.setBorderHorizontal(2);
		toolbar.setActions(new Action[] { actadd, actdel, BPAction.separator(), actsave });

		m_leftpan.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));
		m_leftpan.setPreferredSize(new Dimension(400, 0));
		m_leftpan.setBackground(UIConfigs.COLOR_TEXTBG());

		m_leftpan.setLayout(new BoxLayout(m_leftpan, BoxLayout.X_AXIS));
		m_mainpan.setLayout(new BorderLayout());
		mp.setLayout(new BorderLayout());
		setLayout(new BorderLayout());

		mp.add(m_leftpan, BorderLayout.WEST);
		mp.add(m_mainpan, BorderLayout.CENTER);
		add(toolbar, BorderLayout.NORTH);
		add(mp, BorderLayout.CENTER);
	}

	@SuppressWarnings("unchecked")
	protected void onSave(ActionEvent e)
	{
		BPFilterDataListPanel<DATA> p = (BPFilterDataListPanel<DATA>) m_mainpan.getComponent(0);
		if (p.getSelectedFilter() == null)
			return;
		List<BPTransformerRuleFilter<DATA>> filters = getFilters();
		List<Map<String, Object>> fobjs = new ArrayList<>();
		for (BPTransformerRuleFilter<DATA> f : filters)
		{
			fobjs.add(ObjUtil.objToMap(f));
		}
		String txt = JSONUtil.encode(fobjs);
		BPResource res = CommonUIOperations.selectResource((Window) this.getFocusCycleRootAncestor(), true);
		IOUtil.write((BPResourceIO) res, TextUtil.fromString(txt, "utf-8"));
	}

	@SuppressWarnings("unchecked")
	public List<BPTransformerRuleFilter<DATA>> getFilters()
	{
		List<BPTransformerRuleFilter<DATA>> rc = new ArrayList<BPTransformerRuleFilter<DATA>>();

		int c = m_leftpan.getComponentCount();
		for (int i = 0; i < c; i++)
		{
			BPFilterDataListPanel<DATA> p = (BPFilterDataListPanel<DATA>) m_leftpan.getComponent(i);
			rc.add(p.getSelectedFilter());
		}
		{
			BPFilterDataListPanel<DATA> p = (BPFilterDataListPanel<DATA>) m_mainpan.getComponent(0);
			rc.add(p.getSelectedFilter());
		}
		return rc;
	}

	@SuppressWarnings("unchecked")
	protected void onBack(ActionEvent e)
	{
		if (m_leftpan.getComponentCount() == 0)
			return;
		BPFilterDataListPanel<DATA> p = (BPFilterDataListPanel<DATA>) m_leftpan.getComponent(m_leftpan.getComponentCount() - 1);
		p.setBorder(null);

		m_leftpan.remove(p);
		m_mainpan.removeAll();
		m_mainpan.add(p, BorderLayout.CENTER);

		p.setMinimumSize(null);
		p.setPreferredSize(null);
		p.setLockEdit(false);
		p.setDetailVisible(true);
		updateUI();
	}

	protected void onAdd(ActionEvent e)
	{
		addFilterPanel(false);
	}

	public void addFilterPanel(boolean force)
	{
		BPFilterDataListPanel<DATA> plast = getCurrentFilterPanel();
		List<DATA> results = plast.getResults();
		if (!force && (results == null || results.size() == 0))
			return;

		BPFilterDataListPanel<DATA> p = new BPFilterDataListPanel<>();
		p.setup(m_filters, m_duicb, results, m_renderer);

		m_mainpan.removeAll();
		m_mainpan.add(p, BorderLayout.CENTER);

		if (m_leftpan.getComponentCount() > 0)
		{
			JComponent lastlp = (JComponent) m_leftpan.getComponent(m_leftpan.getComponentCount() - 1);
			lastlp.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));
		}
		plast.setMinimumSize(new Dimension(0, 0));
		plast.setPreferredSize(new Dimension(0, 0));
		plast.setLockEdit(true);
		plast.setDetailVisible(false);
		m_leftpan.add(plast);
		updateUI();
	}

	public void setup(List<? extends BPTransformerRuleFilter<DATA>> filters, BiFunction<DATA, JComponent, JComponent> duicb, List<DATA> datas, Function<Object, ?> renderer)
	{
		setFilters(filters);
		setDetailUIBuilder(duicb);
		setDatas(datas);
		setDataRenderer(renderer);

		createFirstPanel();
	}

	public void loadFilters(List<? extends BPTransformerRuleFilter<DATA>> filters)
	{
		for (int i = 0; i < filters.size(); i++)
		{
			getCurrentFilterPanel().mergeFilterAndRun(filters.get(i));
			if (i < filters.size() - 1)
				addFilterPanel(true);
		}
	}

	public void createFirstPanel()
	{
		BPFilterDataListPanel<DATA> p = new BPFilterDataListPanel<>();
		p.setup(m_filters, m_duicb, m_srcs, m_renderer);
		m_mainpan.add(p, BorderLayout.CENTER);
	}

	public void setDatas(Collection<DATA> srcs)
	{
		m_srcs = new ArrayList<>(srcs);
	}

	public void setFilters(List<? extends BPTransformerRuleFilter<DATA>> filters)
	{
		m_filters = filters;
	}

	public void setDataRenderer(Function<Object, ?> renderer)
	{
		m_renderer = renderer;
	}

	public void setDetailUIBuilder(BiFunction<DATA, JComponent, JComponent> cb)
	{
		m_duicb = cb;
	}

	@SuppressWarnings("unchecked")
	public BPFilterDataListPanel<DATA> getCurrentFilterPanel()
	{
		return (BPFilterDataListPanel<DATA>) m_mainpan.getComponent(0);
	}

	public List<DATA> getResults()
	{
		return getCurrentFilterPanel().getResults();
	}
}
