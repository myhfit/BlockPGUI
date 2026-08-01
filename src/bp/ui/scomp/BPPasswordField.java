package bp.ui.scomp;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.util.UIUtil;

public class BPPasswordField extends JPasswordField
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2599782189790390690L;

	protected String m_text;
	protected UndoManager m_um;
	protected boolean m_nomeasuresize = false;
	protected char m_ec;

	public BPPasswordField()
	{
		super();
		setBorder(null);
		m_um = new BPTextFieldUndoManager();
		getDocument().addUndoableEditListener(m_um);
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "onUndoKey");
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "onRedoKey");
		getActionMap().put("onUndoKey", BPAction.build("").callback(this::onUndoKey).getAction());
		getActionMap().put("onRedoKey", BPAction.build("").callback(this::onRedoKey).getAction());
		UIUtil.setupContextMenu(this, this::getContextMenuActions);
	}

	public void setNoMeasureSize(boolean flag)
	{
		m_nomeasuresize = flag;
	}

	public Dimension getPreferredSize()
	{
		if (m_nomeasuresize)
			return new Dimension(0, 0);
		return super.getPreferredSize();
	}

	protected void onUndoKey(ActionEvent e)
	{
		m_um.undo();
	}

	protected void onRedoKey(ActionEvent e)
	{
		m_um.redo();
	}

	public void cut()
	{
		if (echoCharIsSet())
			return;
		Object b = getClientProperty("JPasswordField.cutCopyAllowed");
		if (b != Boolean.TRUE)
		{
			putClientProperty("JPasswordField.cutCopyAllowed", Boolean.TRUE);
			super.cut();
			putClientProperty("JPasswordField.cutCopyAllowed", b);
		}
		else
		{
			super.cut();
		}
	}

	public void copy()
	{
		if (echoCharIsSet())
			return;
		Object b = getClientProperty("JPasswordField.cutCopyAllowed");
		if (b != Boolean.TRUE)
		{
			putClientProperty("JPasswordField.cutCopyAllowed", Boolean.TRUE);
			super.copy();
			putClientProperty("JPasswordField.cutCopyAllowed", b);
		}
		else
		{
			super.copy();
		}
	}

	public void setMonoFont()
	{
		setFont(UIUtil.monoFont(Font.PLAIN, UIConfigs.TEXTFIELDFONT_SIZE()));
	}

	public void setLabelFont()
	{
		Font f = new Font(UIConfigs.LABEL_FONT_NAME(), Font.PLAIN, UIConfigs.TEXTFIELDFONT_SIZE());
		setFont(f);
	}

	public void setTableFont()
	{
		Font f = new Font(UIConfigs.TABLE_FONT_NAME(), Font.PLAIN, UIConfigs.TEXTFIELDFONT_SIZE());
		setFont(f);
	}

	public void toggleEchoChar()
	{
		char c = getEchoChar();
		if (c == '\0')
		{
			setEchoChar(m_ec);
			m_ec = 0;
		}
		else
		{
			m_ec = c;
			setEchoChar('\0');
		}
	}

	public void transMonoFont()
	{
		setFont(UIUtil.monoFont(Font.PLAIN, getFont().getSize()));
	}

	public boolean isEmpty()
	{
		return getPassword().length == 0;
	}

	public String getNotEmptyText()
	{
		char[] chs = getPassword();
		return chs.length == 0 ? null : new String(chs);
	}

	public void clearResource()
	{
		m_um.discardAllEdits();
	}

	public String getPasswordText()
	{
		return new String(getPassword());
	}

	protected static class BPTextFieldUndoManager extends UndoManager
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8068559465041135580L;

		public void undo()
		{
			if (super.canUndo())
				super.undo();
		}

		public void redo()
		{
			if (super.canRedo())
				super.redo();
		}
	}

	protected <C extends BPPasswordField> Action[] getContextMenuActions(C comp, Object source)
	{
		Action actcopy = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUCOPY, e -> copy());
		Action actcut = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUCUT, e -> cut());
		Action actpaste = BPActionHelpers.getAction(BPActionConstCommon.CTX_MNUPASTE, e -> paste());
		return new Action[] { actcopy, actcut, actpaste };
	}
}
