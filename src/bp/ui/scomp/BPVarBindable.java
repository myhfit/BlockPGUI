package bp.ui.scomp;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import bp.data.BPDataWrapper;
import bp.ui.util.UIUtil;
import bp.util.ObjUtil;

public interface BPVarBindable<C extends Component>
{
	void bind(BPDataWrapper<Map<String, Object>> wrapper);

	void unbind();

	void setDataKey(String key);

	boolean updateData();
	
	C getComponent();

	default void setBorder(JComponent comp, Object border, Object borderwidth, Object bordercolor)
	{
		int bs = ObjUtil.toInt(border, 0);
		Color bc = UIUtil.getColorFromHexText(ObjUtil.toString(bordercolor));
		if (bs == 1)
		{
			int bw = ObjUtil.toInt(borderwidth, 0);
			if (bw <= 0)
				return;
			if (bc == null)
				return;
			Border b = new LineBorder(bc, bw);
			comp.setBorder(b);
		}
	}
}
