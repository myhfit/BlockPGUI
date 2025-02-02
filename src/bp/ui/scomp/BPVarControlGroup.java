package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import bp.ui.util.UIUtil;
import bp.util.MIF;
import bp.util.ObjUtil;

public class BPVarControlGroup extends BPVarControl<JPanel>
{
	protected JPanel createComponent()
	{
		return new JPanel();
	}

	public void clearControls()
	{
		m_comp.removeAll();
	}

	public void setup(Map<String, Object> v)
	{
		if (v == null)
		{
			clearControls();
		}
		m_comp.setBorder(null);
		MIF mif = new MIF(v);
		int w = ObjUtil.toInt(v.get("width"), 0);
		int h = ObjUtil.toInt(v.get("height"), 0);
		if (w > 0 && h > 0)
		{
			m_comp.setSize(w, h);
			m_comp.setPreferredSize(new Dimension(w, h));
		}
		mif.mifnull("bgcolor", c -> m_comp.setBackground(UIUtil.getColorFromHexText(ObjUtil.toString(c))));
		mif.mifnull("layout", l ->
		{
			int li = ObjUtil.toInt(l, 0);
			if (li == 0)
			{
				m_comp.setLayout(null);
			}
			else if (li == 1)
			{
				m_comp.setLayout(new BorderLayout());
			}
			else if (li == 2)
			{
				m_comp.setLayout(new FlowLayout(0, 0, 0));
			}
			else if (li == 3)
			{
				m_comp.setLayout(new BoxLayout(m_comp, BoxLayout.X_AXIS));
			}
			else if (li == 4)
			{
				m_comp.setLayout(new BoxLayout(m_comp, BoxLayout.Y_AXIS));
			}
		});
		mif.mifnull("border", b -> setBorder(m_comp, b, v.get("border-width"), v.get("border-color")));
	}

	public void setupChildren(List<Map<String, Object>> children)
	{
		clearControls();
		if (children == null)
			return;
		Map<String, BPVarBindable<?>> chdmap = new HashMap<String, BPVarBindable<?>>();
		for (int i = 0; i < children.size(); i++)
		{
			Map<String, Object> child = children.get(i);
			String key = m_key + ".children." + i;
			String chdtype = ObjUtil.toString(child.get("type"), null);
			BPVarBindable<?> sub = null;
			if ("group".equals(chdtype))
			{
				sub = new BPVarControlGroup();
			}
			else if ("label".equals(chdtype))
			{
				sub = new BPVarControlLabel();
			}
			else
			{
				sub = new BPVarControlC();
			}
			if (sub != null)
			{
				sub.setDataKey(key);
				sub.bind(m_dw);
				Object lb = child.get("layoutobj");
				if (lb != null)
					m_comp.add(sub.getComponent(), lb);
				else
					m_comp.add(sub.getComponent());
				chdmap.put(key, sub);
			}
		}
		for (BPVarBindable<?> chd : chdmap.values())
		{
			chd.updateData();
		}
	}

	@SuppressWarnings("unchecked")
	public boolean updateData()
	{
		Map<String, Object> obj = m_dw.get();
		Map<String, Object> v = null;
		if (obj != null)
		{
			v = ObjUtil.extract(obj, m_key);
		}
		setup(v);
		List<Map<String, Object>> children = (List<Map<String, Object>>) v.get("children");
		setupChildren(children);
		return false;
	}
}
