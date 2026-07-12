package bp.ui.form.dynamic;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPComboBox.BPComboBoxModel;
import bp.util.ObjUtil;

public class BPFormItemComboBox extends BPFormItemWrapped<BPComboBox<Object>, JPanel>
{
	public boolean transvalue;
	public boolean ctlrender;
	public List<Object> source;

	protected Object normalizeRenderValue(Object v)
	{
		BPFormContext context = getContext();
		if (transvalue)
			v = context == null ? null : context.controller.decodeValue(def.key, v, context);
		return v;
	}

	public boolean validateValue(BPFormContext context)
	{
		if (comp.getSelectedIndex() == -1 && def.required)
			return false;
		return super.validateValue(context);
	}

	public Object getComponentValue()
	{
		Object v = comp.getSelectedItem();
		BPFormContext context = getContext();
		if (transvalue)
			v = context == null ? null : context.controller.encodeValue(def.key, v, context);
		return v;
	}

	public void setComponentValue(Object v)
	{
		BPFormContext context = getContext();
		if (transvalue)
			v = context == null ? null : context.controller.decodeValue(def.key, v, context);
		comp.setSelectedItem(v);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setupInner(BPFormItemDef itemdef, BPFormContext context)
	{
		comp = new BPComboBox<>();
		comp.setBorder(new MatteBorder(0, 1, 0, 0, getGridBorderColor(context)));
		comp.setEnabled(!itemdef.readonly);
		comp.setEditable(ObjUtil.toBool(itemdef.getParam("caninput"), false));
		List<Object> datasource = itemdef.getParam("source");
		if (datasource == null)
			datasource = (List) context.controller.listData(itemdef.key, context);
		BPComboBoxModel<Object> m = new BPComboBoxModel<>();
		m.setDatas(datasource);
		comp.setModel(m);
		transvalue = ObjUtil.toBool(itemdef.getParam("transvalue"), false);
		ctlrender = ObjUtil.toBool(itemdef.getParam("ctlrender"), false);
		if (ctlrender)
			comp.setRenderer(new BPComboBox.BPComboBoxRenderer(this::onRender));
		comp.setListFont();
		stcomp = wrapSingleLineComponent(comp, context);
	}

	protected String onRender(Object v)
	{
		BPFormContext context = getContext();
		return context == null ? null : context.controller.render(def.key, v, context);
	}
}
