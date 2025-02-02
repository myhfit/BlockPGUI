package bp.ui.scomp;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import bp.data.BPDataWrapper;
import bp.ui.util.UIUtil;
import bp.util.MIF;
import bp.util.ObjUtil;

public abstract class BPVarControl<C extends JComponent> implements BPVarBindable<C>
{
	protected BPDataWrapper<Map<String, Object>> m_dw;

	protected C m_comp;

	protected String m_key;

	protected BPVarControl()
	{
		m_comp = createComponent();
	}

	protected abstract C createComponent();

	public void bind(BPDataWrapper<Map<String, Object>> wrapper)
	{
		m_dw = wrapper;
	}

	public void unbind()
	{
		m_dw = null;
	}

	public void setDataKey(String key)
	{
		m_key = key;
	}

	public void setup(Map<String, Object> v)
	{
		if (v == null)
			return;
		MIF mif = new MIF(v);
		m_comp.setBorder(null);
		int w = ObjUtil.toInt(v.get("width"), 0);
		int h = ObjUtil.toInt(v.get("height"), 0);
		if (w > 0 && h > 0)
		{
			m_comp.setSize(w, h);
			m_comp.setPreferredSize(new Dimension(w, h));
		}
		mif.mifnull("bgcolor", c -> m_comp.setBackground(UIUtil.getColorFromHexText(ObjUtil.toString(c))));
		mif.mifnull("border", b -> setBorder(m_comp, b, v.get("border-width"), v.get("border-color")));
	}

	public boolean updateData()
	{
		Map<String, Object> obj = m_dw.get();
		Map<String, Object> v = null;
		if (obj != null)
		{
			v = ObjUtil.extract(obj, m_key);
		}
		setup(v);
		return false;
	}

	public C getComponent()
	{
		return m_comp;
	}

	public static class BPVarControlC extends BPVarControl<JComponent>
	{
		protected JComponent createComponent()
		{
			return new JPanel();
		}
	}
}
