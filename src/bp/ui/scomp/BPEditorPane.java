package bp.ui.scomp;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import bp.config.UIConfigs;
import bp.ui.util.UIUtil;
import bp.util.Std;

public class BPEditorPane extends JEditorPane implements DocumentListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8171373517151920673L;

	protected KeyStroke undo = KeyStroke.getKeyStroke("control Z");
	protected KeyStroke redo = KeyStroke.getKeyStroke("control Y");

	private static final String REDO_KEY = "redo";
	private static final String UNDO_KEY = "undo";

	protected BPUndoManager m_um;

	protected String m_id;

	protected BiConsumer<String, Boolean> m_state_changed = null;
	protected BiConsumer<Integer, Integer> m_pos_changed = null;

	protected boolean m_hascodeborder = false;
	protected BPCodeLinePanel m_linepanel;

	protected WeakReference<Consumer<? super BPEditorPane>> m_changedfunc;

	public BPEditorPane()
	{
		this(true);
		addCaretListener(this::onCaretUpdate);
		addMouseListener(new UIUtil.BPMouseListener(this::onClick, null, null, null, null));
	}

	public String getID()
	{
		return m_id;
	}

	public void setBorder(Border border)
	{
		if (border != null && border instanceof BPCodeBorder)
			m_hascodeborder = true;
		else
			m_hascodeborder = false;
		super.setBorder(border);
	}

	public void setLinePanel(BPCodeLinePanel linepanel)
	{
		m_linepanel = linepanel;
	}

	public void setChangedHandler(Consumer<? super BPEditorPane> callback)
	{
		m_changedfunc = new WeakReference<Consumer<? super BPEditorPane>>(callback);
	}

	public void setID(String id)
	{
		m_id = id;
	}

	protected void onClick(MouseEvent e)
	{
		if (m_hascodeborder)
		{
			BPCodeBorder border = (BPCodeBorder) getBorder();
			border.clicked(e);
		}
	}

	protected void onMousePressed(MouseEvent e)
	{
		if (m_hascodeborder)
		{
			BPCodeBorder border = (BPCodeBorder) getBorder();
			border.mousePressed(e);
		}
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		if (m_linepanel != null)
			m_linepanel.repaint();
	}

	public void setOnStateChanged(BiConsumer<String, Boolean> handler)
	{
		m_state_changed = handler;
	}

	public void setOnPosChanged(BiConsumer<Integer, Integer> handler)
	{
		m_pos_changed = handler;
	}

	public void setMonoFont()
	{
		setFont(UIUtil.monoFont(Font.PLAIN, UIConfigs.EDITORFONT_SIZE()));
	}

	public String getViewText()// not writer's text
	{
		Document doc = getDocument();
		String txt;
		try
		{
			txt = doc.getText(0, doc.getLength());
		}
		catch (BadLocationException e)
		{
			txt = null;
		}
		return txt;
	}

	@SuppressWarnings("serial")
	public BPEditorPane(boolean canundo)
	{
		if (canundo)
		{
			getActionMap().put(UNDO_KEY, new AbstractAction(UNDO_KEY)
			{
				@Override
				public void actionPerformed(ActionEvent evt)
				{
					try
					{
						if (m_um.canUndo())
						{
							m_um.undo();
						}
					}
					catch (CannotUndoException ignore)
					{
					}
				}
			});
			getActionMap().put(REDO_KEY, new AbstractAction(REDO_KEY)
			{
				@Override
				public void actionPerformed(ActionEvent evt)
				{
					try
					{
						if (m_um.canRedo())
						{
							m_um.redo();
						}
					}
					catch (CannotRedoException ignore)
					{
					}
				}
			});
			getInputMap().put(undo, UNDO_KEY);
			getInputMap().put(redo, REDO_KEY);
		}
	}

	public void setDocument(Document doc)
	{
		m_um = new BPUndoManager();
		doc.addDocumentListener(this);
		doc.addUndoableEditListener(m_um);
		super.setDocument(doc);
		m_um.flagSave();
		onChanged();
	}

	public boolean needSave()
	{
		return !m_um.getSaveFlag();
	}

	public void setNeedSave(boolean flag)
	{
		m_um.setSaveFlag(!flag);
	}

	protected class BPUndoManager extends UndoManager
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1450513480592230662L;

		protected boolean saveflag = false;

		public void flagSave()
		{
			if (!saveflag)
			{
				saveflag = true;
				if (m_state_changed != null)
					m_state_changed.accept(m_id, !saveflag);
			}
		}

		public void setSaveFlag(boolean flag)
		{
			saveflag = flag;
			if (m_state_changed != null)
				m_state_changed.accept(m_id, !saveflag);
		}

		public boolean getSaveFlag()
		{
			return saveflag;
		}

		public void undoableEditHappened(UndoableEditEvent e)
		{
			super.undoableEditHappened(e);
			if (saveflag)
			{
				saveflag = false;
				if (m_state_changed != null)
					m_state_changed.accept(m_id, !saveflag);
			}
		}
	}

	public void resizeDoc()
	{
	}

	public void onChanged()
	{
		if (m_changedfunc != null)
		{
			Consumer<? super BPEditorPane> callback = m_changedfunc.get();
			if (callback != null)
				callback.accept(this);
		}
	}

	public void insertUpdate(DocumentEvent e)
	{
		resizeDoc();
		onChanged();
	}

	public void removeUpdate(DocumentEvent e)
	{
		resizeDoc();
		onChanged();
	}

	public void changedUpdate(DocumentEvent e)
	{
		onChanged();
	}

	public int[] getPos(int cpos)
	{
		Document doc = getDocument();
		Element roote = doc.getDefaultRootElement();
		int l = roote.getElementIndex(cpos);
		Element linee = roote.getElement(l);
		int linestart = linee.getStartOffset();
		return new int[] { l + 1, cpos - linestart + 1 };
	}

	public void insertOrReplace(String newtext)
	{
		boolean isinsert = getSelectionEnd() == 0;
		if (isinsert)
		{
			int pos = getCaretPosition();
			Document doc = getDocument();
			try
			{
				doc.insertString(pos, newtext, null);
			}
			catch (BadLocationException e)
			{
				Std.err(e);
			}
		}
		else
		{
			int starti = getSelectionStart();
			int endi = getSelectionEnd();
			Document doc = getDocument();
			try
			{
				doc.remove(starti, endi - starti);
				doc.insertString(starti, newtext, null);
			}
			catch (BadLocationException e)
			{
				Std.err(e);
			}
		}
	}

	protected void onCaretUpdate(CaretEvent e)
	{
		if (hasFocus())
		{
			if (m_pos_changed != null)
			{
				int c = e.getDot();
				int[] pt = getPos(c);
				m_pos_changed.accept(pt[0], pt[1]);
			}
		}
	}
}
