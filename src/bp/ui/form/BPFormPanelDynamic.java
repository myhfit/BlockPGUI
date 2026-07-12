package bp.ui.form;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.swing.JComponent;
import javax.swing.border.MatteBorder;

import bp.data.BPDataWrapper;
import bp.ui.form.dynamic.BPFormContext;
import bp.ui.form.dynamic.BPFormItem;
import bp.ui.form.dynamic.BPFormItemDef;

public abstract class BPFormPanelDynamic extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 799465799893680281L;

	protected BPFormContext m_formcontext;

	protected boolean needScroll()
	{
		return m_formcontext == null ? true : !m_formcontext.noscroll;
	}

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		m_formcontext.traverse((BiConsumer<BPFormItem, Map<String, Object>>) BPFormPanelDynamic::assembleItemData, rc);
		return rc;
	}

	protected static void assembleItemData(BPFormItem item, Map<String, Object> result)
	{
		Component c = item.getComponent();
		if (c == null)
			return;
		item.assembleFormValue(result);
	}

	protected void initForm()
	{
		if (initFormContext() == null)
			return;
		initContainer();
		BPFormContext context = m_formcontext;
		if (context == null)
			return;
		context.initUI(this, false);
	}

	protected boolean initOnStart()
	{
		return false;
	}

	protected boolean validateFormInner()
	{
		boolean rc = true;
		if (m_formcontext != null)
		{
			BPDataWrapper<Boolean> b = new BPDataWrapper<Boolean>(true);
			m_formcontext.traverse((BiConsumer<BPFormItem, BPDataWrapper<Boolean>>) this::validateItemAndTag, b);
			rc = b.get();
		}
		return rc;
	}

	protected void validateItemAndTag(BPFormItem item, BPDataWrapper<Boolean> b)
	{
		boolean f = item.validateValue(m_formcontext);
		boolean r = b.get();
		if (r && !f)
			b.set(f);
		{
			JComponent comp = (JComponent) item.getSTComponent().getParent();
			comp.setBorder(f ? new MatteBorder(0, 0, 1, 0, getGridBorder()) : new MatteBorder(1, 1, 1, 1, Color.RED));
		}
	}

	protected abstract BPFormContext initFormContext();

	public void showData(Map<String, ?> data, boolean editable)
	{
		m_formcontext.showData(data, editable, this);
		m_formcontext.editable = editable;
		m_formcontext.initByData(data);
		List<BPFormItem> items = new ArrayList<BPFormItem>(m_formcontext.items);
		for (BPFormItem item : items)
			item.traverse((BiConsumer<BPFormItem, Map<String, ?>>) this::showItemData, data);
	}

	protected void showItemData(BPFormItem item, Map<String, ?> data)
	{
		Component c = item.getComponent();
		JComponent comp = c instanceof JComponent ? (JComponent) c : null;
		BPFormItemDef def = item.getDefine();
		if (comp != null)
			setComponentValue(comp, data, def.key, m_formcontext.isEditable() && (!def.isReadOnlyOnEdit()), m -> m_formcontext.controlSetValue(item.getValue(data), item));
	}
}
