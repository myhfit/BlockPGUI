package bp.ui.scomp;

import java.awt.BorderLayout;
import java.util.function.Function;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class BPTextFieldPane extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3837932300160301944L;

	protected BPTextField m_txt;
	protected JPanel m_rightpan;
	protected BPToolSQButton m_morebtn;

	public BPTextFieldPane()
	{
		m_txt = new BPTextField();
		m_txt.setNoMeasureSize(true);
		setLayout(new BorderLayout());
		add(m_txt, BorderLayout.CENTER);
		setBorder(new EmptyBorder(0, 0, 0, 0));
	}

	public BPTextField getTextComponent()
	{
		return m_txt;
	}

	public BPToolSQButton addMoreBtnAuto(Function<String, String> cb)
	{
		if (m_rightpan == null)
		{
			m_rightpan = new JPanel();
			m_rightpan.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_rightpan.setLayout(new BorderLayout());
			add(m_rightpan, BorderLayout.EAST);
		}
		else
		{
			m_rightpan.removeAll();
		}
		m_morebtn = new BPToolSQButton("...", () ->
		{
			if (cb != null)
			{
				String rstr = cb.apply(m_txt.getText());
				if (rstr != null)
				{
					m_txt.setText(rstr);
				}
			}
		});
		m_rightpan.add(m_morebtn, BorderLayout.CENTER);
		return m_morebtn;
	}

	public BPToolSQButton addMoreBtn(Runnable cb)
	{
		if (m_rightpan == null)
		{
			m_rightpan = new JPanel();
			m_rightpan.setBorder(new EmptyBorder(0, 0, 0, 0));
			add(m_rightpan, BorderLayout.EAST);
		}
		else
		{
			m_rightpan.removeAll();
		}
		m_morebtn = new BPToolSQButton("...", cb);
		m_rightpan.add(m_morebtn);
		return m_morebtn;
	}

	public JPanel getRightPane()
	{
		return m_rightpan;
	}

	public void setEditable(boolean flag)
	{
		m_txt.setEditable(flag);
		m_rightpan.setVisible(flag);
	}
}
