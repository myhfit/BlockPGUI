package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import bp.BPCore;
import bp.data.BPCommand;
import bp.data.BPCommandResult;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;

public class BPCommandPane extends JPanel implements FocusListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 635052657465161988L;

	protected BPLabel m_lbl;
	protected BPTextField m_txt;
	protected List<String> m_cmdhis = new ArrayList<String>();
	protected int m_cmdpos = 0;

	public BPCommandPane()
	{
		m_lbl = new BPLabel();
		m_lbl.setBorder(new EmptyBorder(0, 2, 0, 0));
		m_lbl.setText("CMD:");
		m_lbl.setMonoFont();
		m_lbl.setFont(UIUtil.deltaFont(m_lbl.getFont(), 1));

		m_txt = new BPTextField();
		m_txt.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_txt.setMonoFont();
		m_lbl.setFont(UIUtil.deltaFont(m_txt.getFont(), 1));

		setBackground(m_txt.getBackground());
		setLayout(new BorderLayout());

		add(m_lbl, BorderLayout.WEST);
		add(m_txt, BorderLayout.CENTER);

		m_txt.addFocusListener(this);
		m_txt.addKeyListener(new UIUtil.BPKeyListener(null, this::onKeyTyped, null));
	}

	public void focusGained(FocusEvent e)
	{
	}

	protected void onKeyTyped(KeyEvent e)
	{
		int kc = e.getKeyCode();
		switch (kc)
		{
			case KeyEvent.VK_ESCAPE:
			{
				setVisible(false);
				break;
			}
			case KeyEvent.VK_ENTER:
			{
				runCommand();
				break;
			}
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
			{
				setCommandByHistory(kc == KeyEvent.VK_UP);
				break;
			}
		}
	}

	protected void setCommandByHistory(boolean isup)
	{
		int delta = isup ? -1 : 1;
		int pos = m_cmdpos;
		int np = pos + delta;
		int s = m_cmdhis.size();
		if ((pos < 0 && delta < 0) || (pos >= s && delta > 0))
			return;
		if (np < 0 || np >= s)
		{
			m_txt.setText("");
			m_cmdpos = np;
			return;
		}
		m_cmdpos = np;
		String newcmd = m_cmdhis.get(np);
		m_txt.setText(newcmd);
	}

	protected void runCommand()
	{
		String cmdtext = m_txt.getText().trim();
		BPCommand cmd = BPCommand.fromText(cmdtext);
		if (cmd != null)
		{
			if (m_cmdpos >= m_cmdhis.size() - 1)
				m_cmdpos = m_cmdhis.size() + 1;
			m_cmdhis.add(cmdtext);
			m_txt.setText("");
			BPCommandResult r = BPCore.callCommand(cmd);
			if (r != null && r.success)
			{
				if (r.data != null)
					UIStd.info(r.data + "");
			}
		}
	}

	public void focusLost(FocusEvent e)
	{
		setVisible(false);
	}

	public void focus()
	{
		m_txt.requestFocusInWindow();
	}
}
