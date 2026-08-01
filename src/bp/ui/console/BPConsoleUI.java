package bp.ui.console;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.console.BPConsole;
import bp.console.BPConsoleCLI;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.container.BPPanelContainerBase;
import bp.ui.container.BPToolBarSQ;
import bp.ui.scomp.BPConsolePane;
import bp.ui.scomp.BPMenuItem;
import bp.util.SystemUtil;

public class BPConsoleUI extends BPPanelContainerBase
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4711838154853279008L;

	protected BPToolBarSQ m_toolbar;
	protected List<BPConsolePanel> m_consoles;
	protected int m_seli;
	protected String m_en;

	public BPConsoleUI()
	{
		init();
	}

	protected void init()
	{
		m_seli = -1;
		m_consoles = new ArrayList<BPConsolePanel>();

		m_toolbar = new BPToolBarSQ(true);
		m_toolbar.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));
		Action actsel = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNSEL, this::onShowSelect);
		Action actnew = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNADD, this::onAdd);
		Action actstop = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNSTOP, this::onStop);
		Action actclose = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNDEL, BPActionConstCommon.ACT_BTNCLOSE, this::onClose);
		Action actclear = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNCLOSETAB, BPActionConstCommon.ACT_BTNCLEAR, this::onClear);
		Action[] acts = new Action[] { BPAction.separator(), actnew, actclose, BPAction.separator(), actstop, BPAction.separator(), BPAction.separator(), actclear, actsel };
		m_toolbar.setActions(acts, this);

		setLayout(new BorderLayout());
		add(m_toolbar, BorderLayout.WEST);
	}

	protected BPConsolePanel getSelectedConsolePanel()
	{
		int seli = m_seli;
		List<BPConsolePanel> consoles = m_consoles;
		if (seli != -1)
			return consoles.get(seli);
		return null;
	}

	protected void onShowSelect(ActionEvent e)
	{
		List<BPConsolePanel> consoles = new ArrayList<BPConsolePanel>(m_consoles);
		if (consoles.size() == 0)
			return;
		JPopupMenu pop = new JPopupMenu();
		for (BPConsolePanel con : consoles)
		{
			BPConsole cc = con.getConsole();
			String name = cc.getName();
			BPMenuItem mnu = new BPMenuItem(BPAction.build(name).callback((ae) -> switchConsole(con)).getAction());
			pop.add(mnu);
		}
		Component c=(Component) e.getSource();
		pop.show(c,0,c.getHeight());
	}

	protected void onAdd(ActionEvent e)
	{
		onAddCLI();
	}

	protected void onStop(ActionEvent e)
	{
		BPConsolePanel con = getSelectedConsolePanel();
		if (con != null)
			con.getConsole().stop();
	}

	protected void onClear(ActionEvent e)
	{
		BPConsolePanel con = getSelectedConsolePanel();
		if (con != null)
		{
			BPConsolePane cp = ((BPConsolePane) con.getTextPanel());
			cp.getConsoleDocument().clear();
			cp.reposCaret();
		}
	}

	protected void onClose(ActionEvent e)
	{
		int seli = m_seli;
		List<BPConsolePanel> consoles = m_consoles;
		if (seli != -1)
		{
			BPConsolePanel c2 = consoles.get(seli);
			c2.clearResource();
			consoles.remove(seli);
			remove(c2);
			seli--;
			if (seli == -1)
				seli = 0;
			if (seli >= consoles.size())
				seli = consoles.size() - 1;
			m_seli = seli;
			if (seli != -1)
				showConsole(consoles.get(seli));
		}
		validate();
		updateUI();
	}

	protected void onAddCLI()
	{
		BPConsoleCLI cc = new BPConsoleCLI();
		if (m_en != null)
			cc.setEncoding(m_en);
		BPConsolePane c2 = new BPConsolePane();
		c2.bindConsole(cc);
		cc.setCommand(SystemUtil.getShellName());
		cc.start();
		addConsole(c2);
	}

	protected void addConsole(BPConsolePane c2)
	{
		BPConsolePanel con = new BPConsolePanel();
		con.setTextPane(c2);
		m_consoles.add(con);
		showConsole(con);
		validate();
	}

	protected void switchConsole(BPConsolePanel con)
	{
		showConsole(con);
		validate();
		updateUI();
	}

	protected void showConsole(BPConsolePanel con)
	{
		List<BPConsolePanel> cons = new ArrayList<BPConsolePanel>(m_consoles);
		int seli = m_seli;
		for (int i = 0; i < cons.size(); i++)
		{
			BPConsolePanel c = cons.get(i);
			if (i == seli)
			{
				if (c.isVisible())
				{
					remove(c);
					c.setVisible(false);
				}
			}
			if (con == c)
			{
				c.setVisible(true);
				add(c, BorderLayout.CENTER);
				m_seli = i;
			}
		}
	}
}
