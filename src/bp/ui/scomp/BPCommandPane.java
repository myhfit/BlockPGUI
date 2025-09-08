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
import bp.core.BPCommandHandler;
import bp.core.BPCommandHandlerCore;
import bp.data.BPCommand;
import bp.data.BPCommandResult;
import bp.remote.BPConnector;
import bp.ui.scomp.BPPopupComboList.BPPopupComboController;
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
	protected volatile BPConnector m_conn;
	protected BPPopupComboList m_popup;
	protected BPPopupComboController m_popupc;

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

		m_popupc = new BPPopupComboController(this::listCommands, null, null);
		m_popup = new BPPopupComboList();
		m_popup.bind(m_txt, m_popupc);

		setBackground(m_txt.getBackground());
		setLayout(new BorderLayout());

		add(m_lbl, BorderLayout.WEST);
		add(m_txt, BorderLayout.CENTER);

		m_txt.addFocusListener(this);
		m_txt.addKeyListener(new UIUtil.BPKeyListener(null, this::onKeyTyped, null));
	}

	@SuppressWarnings("unchecked")
	protected List<?> listCommands(String txt)
	{
		txt = txt.toUpperCase();
		List<String> cmds = null;
		if (m_conn == null)
		{
			BPCommandHandler h = BPCore.getCommandHandler();
			cmds = h.getCommandNames();
		}
		else
		{
			BPCommandResult r = m_conn.call(BPCommand.fromText(BPCommandHandlerCore.CN_CMDNAME_LIST));
			if (r != null)
				cmds = (List<String>) r.data;
		}

		List<String> rc = new ArrayList<String>();
		if (cmds != null)
		{
			for (String cmd : cmds)
			{
				if (cmd.toUpperCase().startsWith(txt))
					rc.add(cmd);
			}
			for (String cmd : cmds)
			{
				if (cmd.toUpperCase().contains(txt) && !rc.contains(cmd))
					rc.add(cmd);
			}
		}
		return rc;
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
				if (e.isControlDown())
					setCommandByHistory(kc == KeyEvent.VK_UP);
				break;
			}
		}
	}

	public void setConnector(BPConnector conn)
	{
		m_conn = conn;
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
			BPConnector conn = m_conn;
			BPCommandResult r = conn == null ? BPCore.callCommand(cmd) : conn.call(cmd);
			if (r != null && r.data != null)
			{
				UIStd.showData(r.data);
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
