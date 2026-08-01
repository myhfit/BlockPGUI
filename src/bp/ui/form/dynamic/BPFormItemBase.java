package bp.ui.form.dynamic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.locale.BPLocaleConst;
import bp.locale.BPLocaleConstDirect;
import bp.locale.BPLocaleHelperDict;
import bp.locale.BPLocaleHelpers;
import bp.typeext.Traversable;
import bp.util.ClassUtil;
import bp.util.LogicUtil.WeakRefGo;
import bp.util.ObjUtil;

public abstract class BPFormItemBase<C extends Component> implements BPFormItem, Traversable
{
	public BPFormItemDef def;
	public String label;
	public C comp;
	public List<BPFormItem> children;
	public List<Object> formatters;
	public List<Object> formattersout;
	public List<Object> formatterslbl;
	protected Map<String, BPLocaleHelperDict<?>> m_lhs;

	protected WeakRefGo<BPFormContext> m_contextref;

	public BPFormItemDef getDefine()
	{
		return def;
	}

	public String getLabel()
	{
		return label;
	}

	public Component getComponent()
	{
		return comp;
	}

	public Iterable<Traversable> getChildren()
	{
		return children == null ? null : new ArrayList<Traversable>(children);
	}

	public void assembleFormValue(Map<String, Object> data)
	{
		if (comp != null)
		{
			String wkey = def.getWriteKey();
			if (wkey != null)
			{
				Object v = getComponentValue();
				if (formattersout != null)
					v = formatValue(v, formattersout);
				if (v != null || def.nowriteempty)
					data.put(wkey, v);
			}
		}
	}

	public Object getValue(Map<String, ?> data)
	{
		Object v = data.get(def.key);
		if (formatters != null)
			v = formatValue(v, formatters);
		v = normalizeRenderValue(v);
		return v;
	}

	public BPFormContext getContext()
	{
		return m_contextref.get();
	}

	@SuppressWarnings("unchecked")
	protected Object formatValue(Object v, List<Object> specformatters)
	{
		for (Object formatter : specformatters)
		{
			String f;
			Object p = null;
			if (formatter instanceof String)
				f = (String) formatter;
			else
			{
				Map<String, Object> fo = (Map<String, Object>) formatter;
				f = (String) fo.get("name");
				p = fo.get("options");
			}
			v = BPFormItemDataFormatters.valueOf(f).format(v, p);
		}
		return v;
	}

	public void initComponent(BPFormContext context)
	{
		if (def.defaultvalue != null)
			setComponentValue(normalizeRenderValue(context.controlSetValue(def.defaultvalue, this)));
	}

	@SuppressWarnings("unchecked")
	public BPFormItem setup(BPFormItemDef itemdef, BPFormContext context)
	{
		m_contextref = new WeakRefGo<BPFormContext>(context);
		def = itemdef;
		Map<String, Object> params = itemdef.params;
		if (params != null)
		{
			formatters = (List<Object>) params.get("formatters");
			formattersout = (List<Object>) params.get("formattersout");
			formatterslbl = (List<Object>) params.get("formatterslbl");
		}
		setupInner(itemdef, context);
		label = translate(itemdef, context);
		return this;
	}

	public BPLocaleHelperDict<?> getLocaleHelper(String key)
	{
		Map<String, BPLocaleHelperDict<?>> lhs = m_lhs;
		if (lhs == null)
		{
			lhs = new HashMap<String, BPLocaleHelperDict<?>>();
			m_lhs = lhs;
		}
		BPLocaleHelperDict<?> lh;
		if (!lhs.containsKey(key))
		{
			lh = new BPLocaleHelperDict.BPLocaleHelperDictClass(key);
			lhs.put(key, lh);
		}
		else
		{
			lh = lhs.get(key);
		}
		return lh;
	}

	protected abstract void setupInner(BPFormItemDef itemdef, BPFormContext context);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected String translate(BPFormItemDef itemdef, BPFormContext context)
	{
		String rc = def.label;
		String lbltr = def.lbltr;
		if ("ctx".equals(lbltr))
			lbltr = context.tr;
		if (lbltr != null)
		{
			String[] sp = lbltr.split(",");
			String sp0 = sp[0];
			switch (sp0)
			{
				case "c":
				{
					String packname = sp[1];
					BPLocaleConst lc = BPLocaleHelpers.findConst(packname, rc);
					if (lc != null)
						rc = lc.text();
					break;
				}
				case "d":
				{
					String clsname = sp[1];
					String prefix = sp.length > 2 ? sp[2] : null;
					Class<?> cls = ClassUtil.getEClass(clsname);
					if (cls != null)
					{
						BPLocaleConstDirect l = (BPLocaleConstDirect) ObjUtil.enumValueOf((Class) cls, "S");
						if (l != null)
							rc = BPLocaleHelpers.translate(l, rc, prefix);
					}
					break;
				}
				case "dc":
				{
					String clsname = sp[1];
					Class<?> cls = ClassUtil.getEClass(clsname);
					rc = BPLocaleHelpers.translateByClass(cls, rc);
					break;
				}
				case "lh":
				{
					String clsname = sp[1];
					BPLocaleHelperDict<?> lh = getLocaleHelper(clsname);
					if (lh != null)
						rc = lh.translate(rc);
					break;
				}
			}
		}
		if (rc != null && formatterslbl != null)
			rc = (String) formatValue(rc, formatterslbl);
		return rc;
	}

	protected Color getGridBorderColor(BPFormContext context)
	{
		return context.gridweakborder ? UIConfigs.COLOR_TEXTQUARTER() : UIConfigs.COLOR_TABLEGRID();
	}

	protected JPanel wrapSingleLineComponent(Component comp, BPFormContext context)
	{
		JPanel rc = new JPanel();
		rc.setBorder(new MatteBorder(0, 1, 0, 0, getGridBorderColor(context)));
		rc.setLayout(new BorderLayout());
		rc.add(comp, BorderLayout.CENTER);
		return rc;
	}

	protected Object normalizeRenderValue(Object v)
	{
		return v;
	}

	public boolean validateValue(BPFormContext context)
	{
		List<Map<String, Object>> checkers = def.getParam("checkers");
		if (checkers != null)
			return BPFormItemDataChecker.check(def.key, checkers, getComponentValue(), null, context);
		return true;
	}
}