package bp.ui.console;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.console.BPConsole;
import bp.console.BPConsoleCLI;
import bp.ui.actions.BPAction;
import bp.ui.container.BPPanelContainerBase;
import bp.ui.container.BPToolBarSQ;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPConsolePane;
import bp.ui.scomp.BPMenuItem;
import bp.ui.scomp.BPToolVIconButton;
import bp.ui.util.UIUtil;
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
		Action actsel = BPAction.build("sel").tooltip("Select").vIcon(BPIconResV.MORE()).getAction();
		Action actnew = BPAction.build("new").tooltip("Create").vIcon(BPIconResV.ADD()).callback(this::onAdd).getAction();
		Action actstop = BPAction.build("stop").tooltip("Stop").vIcon(BPIconResV.STOP()).callback(this::onStop).getAction();
		Action actclose = BPAction.build("close").tooltip("Close").vIcon(BPIconResV.DEL()).callback(this::onClose).getAction();
		Action actclear = BPAction.build("clear").tooltip("Clear").vIcon(BPIconResV.KILL()).callback(this::onClear).getAction();
		BPToolVIconButton btnadd = new BPToolVIconButton(actnew, this);
		BPToolVIconButton btnsel = new BPToolVIconButton(actsel, this);
		BPToolVIconButton btnstop = new BPToolVIconButton(actstop, this);
		BPToolVIconButton btnclose = new BPToolVIconButton(actclose, this);
		BPToolVIconButton btnclear = new BPToolVIconButton(actclear, this);
		btnsel.addMouseListener(new UIUtil.BPMouseListener(this::onShowSelect, null, null, null, null));
		m_toolbar.addSeparator();
		m_toolbar.add(btnadd);
		m_toolbar.add(btnclose);
		m_toolbar.addSeparator();
		m_toolbar.add(btnstop);
		m_toolbar.addSeparator();
		m_toolbar.addSeparator();
		m_toolbar.add(btnclear);
		m_toolbar.add(btnsel);

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

	protected void onShowSelect(MouseEvent e)
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
		pop.show((Component) e.getSource(), e.getX(), e.getY());
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
