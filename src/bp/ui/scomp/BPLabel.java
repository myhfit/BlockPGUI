package bp.ui.scomp;

import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;

import bp.config.UIConfigs;
import bp.ui.util.UIUtil;

public class BPLabel extends JLabel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8895053864247072058L;

	protected boolean m_bbordered = false;
	protected Border m_oldborder = null;

	public BPLabel()
	{

	}

	public BPLabel(String text)
	{
		super(text);
	}

	public BPLabel(String text, int align)
	{
		super(text, align);
	}

	public BPLabel(Icon icon, int align)
	{
		super(icon, align);
	}

	public BPLabel(String text, Icon icon, int align)
	{
		super(text, icon, align);
	}

	public void setMonoFont()
	{
		setFont(UIUtil.monoFont(Font.PLAIN, UIConfigs.TEXTFIELDFONT_SIZE()));
	}

	public void transMonoFont()
	{
		setFont(UIUtil.monoFont(Font.PLAIN, getFont().getSize()));
	}

	public void setLabelFont()
	{
		int fontsize = UIConfigs.TEXTFIELDFONT_SIZE();
		Font tfont = new Font(UIConfigs.LABEL_FONT_NAME(), Font.PLAIN, fontsize);
		setFont(tfont);
	}

	public void setFloatLabel()
	{
		addMouseListener(new UIUtil.BPMouseListener(null, null, null, (e) -> setBBorder(true), (e) -> setBBorder(false)));
	}

	protected void setBBorder(boolean flag)
	{
		if (m_bbordered == flag)
			return;
		m_bbordered = flag;
		if (flag)
		{
			m_oldborder = getBorder();
			setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
		}
		else
		{
			Border oldborder = m_oldborder;
			m_oldborder = null;
			setBorder(oldborder);
		}
	}
}
