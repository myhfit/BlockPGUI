package bp.ui.scomp;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.util.UIUtil;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public class BPTextField extends JTextField
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2599782189790390690L;

	protected String m_text;
	protected UndoManager m_um;
	protected boolean m_nomeasuresize = false;

	public BPTextField()
	{
		super();
		setBorder(null);
		m_um = new BPTextFieldUndoManager();
		getDocument().addUndoableEditListener(m_um);
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "onUndoKey");
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "onRedoKey");
		getActionMap().put("onUndoKey", BPAction.build("").callback(this::onUndoKey).getAction());
		getActionMap().put("onRedoKey", BPAction.build("").callback(this::onRedoKey).getAction());
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

	public void transMonoFont()
	{
		setFont(UIUtil.monoFont(Font.PLAIN, getFont().getSize()));
	}

	public boolean isEmpty()
	{
		return getText().isEmpty();
	}

	public boolean isLong()
	{
		return ObjUtil.check(() -> Long.parseLong(getText().trim()));
	}

	public boolean isInt()
	{
		return ObjUtil.check(() -> Integer.parseInt(getText().trim()));
	}

	public boolean checkSTName()
	{
		String text = getNotEmptyText();
		return text != null ? TextUtil.checkSTName(text) : true;
	}

	public String getNotEmptyText()
	{
		String text = getText().trim();
		return text.isEmpty() ? null : text;
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
}
