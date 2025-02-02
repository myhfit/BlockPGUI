package bp.ui.scomp;

import java.awt.Font;

import javax.swing.JCheckBox;

import bp.config.UIConfigs;
import bp.ui.util.UIUtil;

public class BPCheckBox extends JCheckBox
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6980427446662952881L;

	public BPCheckBox()
	{
	}

	public BPCheckBox(String text)
	{
		super(text);
	}

	public void setMonoFont()
	{
		setFont(UIUtil.monoFont(Font.PLAIN, UIConfigs.LISTFONT_SIZE()));
	}

	public void setLabelFont()
	{
		int fontsize = UIConfigs.TEXTFIELDFONT_SIZE();
		Font tfont = new Font(UIConfigs.LABEL_FONT_NAME(), Font.PLAIN, fontsize);
		setFont(tfont);
	}
}
