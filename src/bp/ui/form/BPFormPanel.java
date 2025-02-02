package bp.ui.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.JTextComponent;

import bp.config.UIConfigs;
import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextFieldPane;
import bp.ui.scomp.BPToolVIconButton;
import bp.util.ObjUtil;

public abstract class BPFormPanel extends JPanel implements BPForm<JPanel>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4842230114536718439L;

	protected int m_labelwidth;
	protected JScrollPane m_sp;
	protected JPanel m_form;

	protected int m_lineheight;

	protected List<Supplier<Boolean>> m_checks;

	protected boolean m_gridweakborder;

	public BPFormPanel()
	{
		boolean needscroll = needScroll();
		m_lineheight = (UIConfigs.TEXTFIELD_HEIGHT());
		m_form = new JPanel();
		m_form.setLayout(new BoxLayout(m_form, BoxLayout.Y_AXIS));
		m_form.setBorder(null);
		setLayout(new BorderLayout());
		if (needscroll)
		{
			m_sp = new JScrollPane();
			m_sp.setViewportView(m_form);
			m_sp.setBorder(new EmptyBorder(0, 0, 0, 0));
			add(m_sp, BorderLayout.CENTER);
		}
		else
		{
			add(m_form, BorderLayout.CENTER);
		}
		m_labelwidth = (int) (100 * UIConfigs.UI_SCALE());
		m_checks = new ArrayList<Supplier<Boolean>>();
		initForm();
		completeForm();
	}

	protected boolean needScroll()
	{
		return true;
	}

	public void setGridWeakBorder(boolean flag)
	{
		m_gridweakborder = flag;
	}

	protected abstract void initForm();

	public final boolean validateForm()
	{
		boolean rc = true;
		if (m_checks != null)
		{
			for (int i = 0; i < m_checks.size(); i++)
			{
				Supplier<Boolean> check = m_checks.get(i);
				if (check != null)
				{
					boolean f = check.get();
					JComponent comp = ((JComponent) m_form.getComponent(i));
					comp.setBorder(f ? new MatteBorder(0, 0, 1, 0, getGridBorder()) : new MatteBorder(1, 1, 1, 1, Color.RED));
					rc = rc && f;
				}
			}
		}
		rc = rc && validateFormInner();
		return rc;
	}

	protected Color getGridBorder()
	{
		return m_gridweakborder ? UIConfigs.COLOR_TEXTQUARTER() : UIConfigs.COLOR_TABLEGRID();
	}

	protected boolean validateFormInner()
	{
		return true;
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.FORM;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void addLineComponents(JLabel[] jlbs, Component[] comps, Supplier<Boolean> check)
	{
		Component[] tpans = new Component[comps.length];
		for (int i = 0; i < comps.length; i++)
		{
			JLabel lbl = jlbs[i];
			lbl.setPreferredSize(new Dimension(m_labelwidth, m_lineheight));
			lbl.setMinimumSize(new Dimension(m_labelwidth, m_lineheight));
			JPanel tpan = new JPanel();
			tpan.setLayout(new BorderLayout());
			tpan.add(lbl, BorderLayout.WEST);
			tpan.add(comps[i], BorderLayout.CENTER);
			if (i < comps.length - 1)
				tpan.setBorder(new MatteBorder(0, 0, 0, 1, getGridBorder()));
			tpans[i] = tpan;
		}
		doAddLineComponents(check, tpans);
	}

	protected void doAddLineComponents(Supplier<Boolean> check, Component... comps)
	{
		doAddLineComponents(check, true, m_lineheight, comps);
	}

	protected void doAddLineComponents(Supplier<Boolean> check, boolean needborder, int height, Component... comps)
	{
		JPanel linepan = new JPanel();
		linepan.setLayout(new BoxLayout(linepan, BoxLayout.X_AXIS));
		if (height > 0)
			linepan.setMaximumSize(new Dimension((int) (5000f * UIConfigs.UI_SCALE()), height));
		if (needborder)
			linepan.setBorder(new MatteBorder(0, 0, 1, 0, getGridBorder()));
		else
			linepan.setBorder(new EmptyBorder(0, 0, 0, 0));
		for (Component comp : comps)
			linepan.add(comp);
		m_form.add(linepan);
		m_checks.add(check);
	}

	public void addLine(String[] lbls, Component[] comps)
	{
		addLine(lbls, comps, false, null);
	}

	public void addSeparator(String str)
	{
		addSeparator(str, null);
	}

	public void addSeparator(String str, Action[] actions)
	{
		BPLabel lbl = new BPLabel(str, SwingConstants.CENTER);
		JPanel cpan = new JPanel();
		FlowLayout fl = new FlowLayout();
		JPanel tpan = new JPanel();

		lbl.setVerticalAlignment(SwingConstants.CENTER);
		lbl.setFont(new Font(UIConfigs.LABEL_FONT_NAME(), Font.PLAIN, UIConfigs.TEXTFIELDFONT_SIZE() + 1));

		fl.setVgap(0);
		fl.setAlignment(FlowLayout.CENTER);
		cpan.setLayout(fl);

		tpan.setMinimumSize(new Dimension(0, m_lineheight + 2));
		tpan.setLayout(new BorderLayout());

		cpan.add(lbl);

		if (actions != null)
		{
			for (Action act : actions)
			{
				BPToolVIconButton lblact = new BPToolVIconButton(act);
				cpan.add(lblact);
			}
		}

		tpan.add(cpan, BorderLayout.CENTER);
		tpan.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_TEXTBG()));
		doAddLineComponents(null, true, m_lineheight + 2, tpan);
	}

	public void addLine(String[] lbls, Component[] comps, Supplier<Boolean> check)
	{
		addLine(lbls, comps, true, check);
	}

	public void addLine(String[] lbls, Component[] comps, boolean required, Supplier<Boolean> check)
	{
		JLabel[] jbls = new JLabel[comps.length];
		for (int i = 0; i < comps.length; i++)
		{
			JLabel lbl = makeLineLabel(lbls[i], required);
			jbls[i] = lbl;
		}
		addLineComponents(jbls, comps, check);
	}

	protected JLabel makeLineLabel(String lblstr, boolean required)
	{
		JLabel lbl = new JLabel(lblstr);
		lbl.setFont(new Font(UIConfigs.LABEL_FONT_NAME(), required ? Font.BOLD : Font.PLAIN, UIConfigs.TEXTFIELDFONT_SIZE()));
		lbl.setBorder(new EmptyBorder(0, 2, 0, 0));
		lbl.setToolTipText(lblstr);
		return lbl;
	}

	protected BPTextField makeSingleLineTextField()
	{
		BPTextField rc = new BPTextField();
		rc.setMonoFont();
		rc.setBorder(new MatteBorder(0, 1, 0, 0, getGridBorder()));
		rc.setNoMeasureSize(true);
		return rc;
	}

	protected BPCheckBox makeCheckBox()
	{
		BPCheckBox rc = new BPCheckBox();
		rc.setLabelFont();
		return rc;
	}

	protected <T> BPComboBox<T> makeComboBox(Function<Object, Object> render)
	{
		BPComboBox<T> rc = new BPComboBox<T>();
		rc.setBorder(new MatteBorder(0, 1, 0, 0, getGridBorder()));
		rc.setModel(new BPComboBox.BPComboBoxModel<T>());
		if (render != null)
		{
			rc.setRenderer(new BPComboBox.BPComboBoxRenderer(render));
		}
		rc.setListFont();
		return rc;
	}

	protected JPanel wrapSingleLineComponent(Component comp)
	{
		JPanel rc = new JPanel();
		rc.setBorder(new MatteBorder(0, 1, 0, 0, getGridBorder()));
		rc.setLayout(new BorderLayout());
		rc.add(comp, BorderLayout.CENTER);
		return rc;
	}

	protected BPTextFieldPane makeSingleLineTextFieldPanel(Function<String, String> cb)
	{
		BPTextFieldPane rc = new BPTextFieldPane();
		BPTextField txt = rc.getTextComponent();
		rc.addMoreBtnAuto(cb).setPreferredSize(new Dimension(m_lineheight, m_lineheight));
		txt.setMonoFont();
		txt.setBorder(new MatteBorder(0, 1, 0, 1, getGridBorder()));
		return rc;
	}

	protected void completeForm()
	{

	}

	protected final static boolean checkEmpty(String str)
	{
		return str.trim().length() == 0;
	}

	protected void setComponentValue(JComponent comp, Map<String, ?> data, String key, boolean editable)
	{
		setComponentValue(comp, data, key, editable, null);
	}

	protected void setComponentValue(JComponent comp, Map<String, ?> data, String key, boolean editable, Function<Map<String, ?>, ?> getdatafunc)
	{
		String dkey = "_" + key;
		Object value = ((getdatafunc == null) ? data.get(key) : getdatafunc.apply(data));
		if (comp instanceof JTextComponent)
		{
			JTextComponent tcomp = (JTextComponent) comp;
			tcomp.setText(ObjUtil.toString(value, ""));
			if (data.containsKey("_" + key))
				tcomp.setToolTipText("Default:" + ObjUtil.toString(data.get(dkey), ""));
			tcomp.setEditable(editable);
		}
		else if (comp instanceof AbstractButton)
		{
			AbstractButton bcomp = (AbstractButton) comp;
			Boolean bvalue = ObjUtil.toBool(value, null);
			if (bvalue != null)
				bcomp.setSelected(bvalue);
			if (data.containsKey("_" + key))
				bcomp.setToolTipText("Default:" + ObjUtil.toString(data.get(dkey), ""));
			bcomp.setEnabled(editable);
		}
		else if (comp instanceof JComboBox)
		{
			JComboBox<?> cb = (JComboBox<?>) comp;
			cb.setSelectedItem(value);
			cb.setEnabled(editable);
		}
	}

	protected final static void notEmptyText(String text, Consumer<String> transfunc)
	{
		String t = text.trim();
		if (!t.isEmpty())
			transfunc.accept(t);
	}
}
