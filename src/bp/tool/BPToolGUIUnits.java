package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.locale.BPLocaleHelpers;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.container.BPToolBarSQ;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPCodePane;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPComboBox.BPComboBoxModel;
import bp.ui.scomp.BPComboBox.BPComboBoxRenderer;
import bp.unit.BPUnit;
import bp.unit.BPUnits;
import bp.util.ClassUtil;
import bp.util.ObjUtil;

public class BPToolGUIUnits extends BPToolGUIBase<BPToolGUIUnits.BPToolGUIContextUnits>
{
	public String getName()
	{
		return BPActionConstCommon.TNAME_UNITS.text();
	}

	protected BPToolGUIContextUnits createToolContext()
	{
		return new BPToolGUIContextUnits();
	}

	protected static class BPToolGUIContextUnits implements BPToolGUIBase.BPToolGUIContext
	{
		protected BPCodePane m_src;
		protected BPCodePane m_dest;
		protected JScrollPane m_scrolll;
		protected JScrollPane m_scrollr;
		protected BPComboBox<BPUnits<?>> m_cmbunits;
		protected BPComboBox<BPUnit<?, ?>> m_cmbl;
		protected BPComboBox<BPUnit<?, ?>> m_cmbr;
		protected DecimalFormat m_df = new DecimalFormat("0.########################");

		public void initUI(Container par, Object... params)
		{
			m_src = new BPCodePane();
			m_dest = new BPCodePane();
			m_scrolll = new JScrollPane();
			m_scrollr = new JScrollPane();
			m_cmbunits = new BPComboBox<BPUnits<?>>();
			JPanel sp = new JPanel();
			sp.setLayout(new GridLayout(1, 2, 0, 0));
			JPanel pl = new JPanel();
			JPanel pr = new JPanel();
			m_cmbl = new BPComboBox<>();
			m_cmbr = new BPComboBox<>();
			BPToolBarSQ toolbar = new BPToolBarSQ();
			Action acttoright = BPAction.build(">").vIcon(BPIconResV.TORIGHT()).callback(this::onToRight).tooltip(BPLocaleHelpers.translateByClass(BPToolGUI.class, "Convert to right")).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)).getAction();
			Action acttoleft = BPAction.build("<").vIcon(BPIconResV.TOLEFT()).callback(this::onToLeft).tooltip(BPLocaleHelpers.translateByClass(BPToolGUI.class, "Convert to left")).acceleratorKey(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0)).getAction();
			toolbar.setBarHeight(UIConfigs.BAR_HEIGHT_COMBO());
			toolbar.setActions(new Action[] { acttoleft, BPAction.separator(), acttoright, BPAction.separator(), });
			toolbar.add(m_cmbunits);

			m_cmbunits.setListFont();
			m_src.setMonoFont();
			m_dest.setMonoFont();
			m_cmbl.setListFont();
			m_cmbr.setListFont();
			m_cmbl.setRenderer(new BPComboBoxRenderer(this::getUnitName));
			m_cmbr.setRenderer(new BPComboBoxRenderer(this::getUnitName));

			m_scrolll.setViewportView(m_src);
			m_scrollr.setViewportView(m_dest);
			m_scrolll.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_scrollr.setBorder(new EmptyBorder(0, 0, 0, 0));
			sp.setBorder(new EmptyBorder(0, 0, 0, 0));
			toolbar.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()), new EmptyBorder(1, 1, 1, 1)));
			pl.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));

			sp.add(pl);
			sp.add(pr);
			pl.setLayout(new BorderLayout());
			pr.setLayout(new BorderLayout());
			m_src.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_dest.setBorder(new EmptyBorder(0, 0, 0, 0));
			pl.add(m_cmbl, BorderLayout.NORTH);
			pr.add(m_cmbr, BorderLayout.NORTH);
			pl.add(m_scrolll, BorderLayout.CENTER);
			pr.add(m_scrollr, BorderLayout.CENTER);
			par.add(sp, BorderLayout.CENTER);
			par.add(toolbar, BorderLayout.NORTH);

			initCombo();
			m_src.resizeDoc();
			m_dest.resizeDoc();
		}

		public void initDatas(Object... params)
		{
			m_src.setText("1");
		}

		protected void initCombo()
		{
			List<BPUnits<?>> unitss = new ArrayList<BPUnits<?>>();
			BPComboBoxModel<BPUnits<?>> model = new BPComboBoxModel<BPUnits<?>>();
			for (BPUnits<?> units : ClassUtil.getServices(BPUnits.class))
				unitss.add(units);
			model.setDatas(unitss);
			m_cmbunits.addItemListener(this::unitsChanged);
			m_cmbunits.setModel(model);
			m_cmbunits.setRenderer(new BPComboBoxRenderer(this::getUnitsName));
			m_cmbunits.setSelectedIndex(0);
		}

		protected String getUnitsName(Object units)
		{
			return BPLocaleHelpers.translateByClass(BPUnits.class, ((BPUnits<?>) units).getUnitsName());
		}

		protected String getUnitName(Object unit)
		{
			BPUnit<?, ?> u = (BPUnit<?, ?>) unit;
			return BPLocaleHelpers.translateByClass(u.getClass(), u.name());
		}

		protected void unitsChanged(ItemEvent e)
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				BPUnits<? extends BPUnit<?, ?>> units = (BPUnits<?>) m_cmbunits.getSelectedItem();
				List<BPUnit<?, ?>> us = new ArrayList<>(units.getUnitValues());
				List<BPUnit<?, ?>> us2 = new ArrayList<>(us);
				BPComboBoxModel<BPUnit<?, ?>> ml = new BPComboBoxModel<>();
				BPComboBoxModel<BPUnit<?, ?>> mr = new BPComboBoxModel<>();
				ml.setDatas(us);
				mr.setDatas(us2);
				m_cmbl.setModel(ml);
				m_cmbr.setModel(mr);
				BPUnit<?, ?> u0 = units.getBaseUnit();
				m_cmbr.setSelectedItem(u0);
				m_cmbl.setSelectedIndex(u0.ordinal() > 0 ? 0 : 1);
				m_dest.setText("");
			}
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		protected void onToRight(ActionEvent e)
		{
			BPUnit srcu = (BPUnit) getUnit(true);
			setValue(false, srcu.convert(getUnit(false), getValue(true, srcu)));
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected void onToLeft(ActionEvent e)
		{
			BPUnit srcu = (BPUnit) getUnit(false);
			setValue(true, srcu.convert(getUnit(true), getValue(false, srcu)));
		}

		protected BPUnit<?, ?> getUnit(boolean isleft)
		{
			return (BPUnit<?, ?>) (isleft ? m_cmbl : m_cmbr).getSelectedItem();
		}

		protected <C> void setValue(boolean isleft, C v)
		{
			(isleft ? m_src : m_dest).setText((v instanceof BigDecimal) ? m_df.format(v) : ObjUtil.toString(v));
		}

		protected <C> C getValue(boolean isleft, BPUnit<?, C> unit)
		{
			return isleft ? unit.castValue(m_src.getText()) : unit.castValue(m_dest.getText());
		}
	}
}