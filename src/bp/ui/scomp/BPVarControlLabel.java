package bp.ui.scomp;

import java.util.Map;

import bp.util.ObjUtil;

public class BPVarControlLabel extends BPVarControl<BPLabel>
{
	public void setup(Map<String, Object> v)
	{
		super.setup(v);
		String text = ObjUtil.toString(v.get("text"));
		if (text != null)
		{
			m_comp.setText(text);
		}
	}

	protected BPLabel createComponent()
	{
		BPLabel lbl=new BPLabel();
		lbl.setHorizontalAlignment(BPLabel.CENTER);
		lbl.setLabelFont();
		return lbl;
	}
}
