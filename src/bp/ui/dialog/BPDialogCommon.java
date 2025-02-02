package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPCommonDialogActions;

public abstract class BPDialogCommon extends BPDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2824481043912870940L;

	public final static int COMMANDBAR_OK = 0;
	public final static int COMMANDBAR_OK_CANCEL = 1;
	public final static int COMMANDBAR_OK_CANCEL_APPLY = 2;
	public final static int COMMANDBAR_YES_NO = 3;
	public final static int COMMANDBAR_YES_NO_CANCEL = 4;

	public final static int COMMAND_OK = 1;
	public final static int COMMAND_CANCEL = 2;
	public final static int COMMAND_YES = 3;
	public final static int COMMAND_NO = 4;
	public final static int COMMAND_APPLY = 5;

	public final static int COMMANDBAR_OKENTER = 10;
	public final static int COMMANDBAR_OKENTER_CANCEL = 11;
	public final static int COMMANDBAR_CANCEL = 12;
	public final static int COMMANDBAR_OKESCAPE = 13;

	protected int m_actionresult = 0;

	public BPDialogCommon()
	{
		super();
	}

	public BPDialogCommon(Frame owner)
	{
		super(owner);
	}

	public BPDialogCommon(Dialog owner)
	{
		super(owner);
	}

	public BPDialogCommon(Window owner)
	{
		super(owner);
	}

	protected void setCommandBar(Action[] actions)
	{
		LayoutManager l = getContentPane().getLayout();
		if (l != null && l instanceof BorderLayout)
		{
			Component comp = ((BorderLayout) l).getLayoutComponent(BorderLayout.SOUTH);
			if (comp != null)
				getContentPane().remove(comp);
		}

		if (actions != null && actions.length > 0)
		{
			JPanel cmdbar = new JPanel();
			FlowLayout layout = new FlowLayout(FlowLayout.CENTER);
			layout.setVgap(0);
			cmdbar.setLayout(layout);
			cmdbar.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_STRONGBORDER()), new EmptyBorder(2, 0, 2, 0)));
			for (Action act : actions)
			{
				if (Boolean.TRUE.equals(act.getValue(BPAction.IS_SEPARATOR)))
				{
					cmdbar.add(Box.createRigidArea(new Dimension(4, 4)));
				}
				else
				{
					JButton btn = new JButton(act);
					btn.setFont(new Font(UIConfigs.LABEL_FONT_NAME(), Font.PLAIN, UIConfigs.TEXTFIELDFONT_SIZE()));
					KeyStroke ks = (KeyStroke) act.getValue(Action.ACCELERATOR_KEY);
					if (ks != null)
						btn.registerKeyboardAction(act, ks, JComponent.WHEN_IN_FOCUSED_WINDOW);
					cmdbar.add(btn);
				}
			}
			getContentPane().add(cmdbar, BorderLayout.SOUTH);
		}
	}

	protected void setCommandBarMode(int commonmode)
	{
		BPCommonDialogActions dlgacts = new BPCommonDialogActions(this);
		Action[] acts = null;
		switch (commonmode)
		{
			case COMMANDBAR_OK:
			case COMMANDBAR_OKENTER:
			case COMMANDBAR_OKESCAPE:
			{
				if (commonmode == COMMANDBAR_OKENTER)
					dlgacts.actionok.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
				else if (commonmode == COMMANDBAR_OKESCAPE)
					dlgacts.actionok.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
				acts = new Action[] { dlgacts.actionok };
				break;
			}
			case COMMANDBAR_OK_CANCEL:
			case COMMANDBAR_OKENTER_CANCEL:
			{
				if (commonmode == COMMANDBAR_OKENTER_CANCEL)
					dlgacts.actionok.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
				acts = new Action[] { dlgacts.actionok, dlgacts.actioncancel };
				break;
			}
			case COMMANDBAR_OK_CANCEL_APPLY:
			{
				acts = new Action[] { dlgacts.actionok, dlgacts.actioncancel, dlgacts.actionapply };
				break;
			}
			case COMMANDBAR_YES_NO:
			{
				acts = new Action[] { dlgacts.actionyes, dlgacts.actionno };
				break;
			}
			case COMMANDBAR_YES_NO_CANCEL:
			{
				acts = new Action[] { dlgacts.actionyes, dlgacts.actionno, dlgacts.actioncancel };
				break;
			}
			case COMMANDBAR_CANCEL:
			{
				acts = new Action[] { dlgacts.actioncancel };
				break;
			}
		}
		if (acts != null)
			setCommandBar(acts);
	}

	public void callCommonAction(int command)
	{
		boolean stop = doCallCommonAction(command);
		if (!stop)
		{
			m_actionresult = command;
			close();
		}
	}

	public void addHiddenEscape()
	{
		((JComponent) getContentPane()).registerKeyboardAction(e -> callCommonAction(COMMAND_CANCEL), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	public abstract boolean doCallCommonAction(int command);

	public int getActionResult()
	{
		return m_actionresult;
	}
}
