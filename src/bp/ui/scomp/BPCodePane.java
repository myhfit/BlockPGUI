package bp.ui.scomp;

import bp.config.UIConfigs;

public class BPCodePane extends BPTextPane
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9104661390457285947L;

	public BPCodePane()
	{
		setTabSize(UIConfigs.TAB_SIZE());
	}

	public void setupCodeBorder()
	{
		BPCodeBorder b = new BPCodeBorder(this);
		setBorder(b);
	}
}
