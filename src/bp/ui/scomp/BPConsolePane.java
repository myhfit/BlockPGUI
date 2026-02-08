package bp.ui.scomp;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;

import bp.console.BPConsole;
import bp.event.BPEventConsumerList;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.Std;

public class BPConsolePane extends BPCodePane
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1328914152525782442L;

	protected BPEventConsumerList<String> m_doenter;
	protected BPEventConsumerList<String> m_onentersync;

	protected boolean[] m_funckeys = new boolean[256];

	protected Runnable m_notifycb;
	protected Consumer<String> m_cononenter;

	protected BPConsole m_console;
	protected AtomicBoolean m_appendlock;

	public BPConsolePane()
	{
		addKeyListener(new UIUtil.BPKeyListener(null, this::onKeyPressed, null));
		initFunctionKeys();
		m_notifycb = this::onConsoleChanged;
		m_cononenter = this::onConsoleEnter;
		m_appendlock = new AtomicBoolean(false);

		m_doenter = new BPEventConsumerList<String>();
		m_onentersync = new BPEventConsumerList<String>();
	}

	protected void initFunctionKeys()
	{
		m_funckeys[KeyEvent.VK_ENTER] = true;
		m_funckeys[KeyEvent.VK_UP] = true;
		m_funckeys[KeyEvent.VK_DOWN] = true;
	}

	public void paste()
	{
		if (isEditable() && isEnabled())
		{
			Clipboard cl = getToolkit().getSystemClipboard();
			Transferable tf = cl.getContents(null);
			DataFlavor df = DataFlavor.stringFlavor;
			if (tf.isDataFlavorSupported(df))
			{
				try
				{
					String cmd = getCommandLine();
					String txt = (String) tf.getTransferData(df);
					if (txt != null && txt.length() > 0)
					{
						String[] lines = txt.split("\n");
						for (int i = 0; i < lines.length; i++)
						{
							String line = lines[i];
							if (i == 0)
								line = cmd + line;
							getConsoleDocument().append(line, false);
							if (i < lines.length - 1)
							{
								getConsoleDocument().append("\n", false);
								m_doenter.accept(line);
								m_onentersync.accept(line);
							}
						}
					}
				}
				catch (UnsupportedFlavorException | IOException e)
				{
					Std.debug(e.getStackTrace().toString());
				}
			}
		}
	}

	private void onKeyPressed(KeyEvent e)
	{
		int keycode = e.getKeyCode();
		if (keycode < 256 && m_funckeys[keycode])
		{
			pressFunctionKey(keycode, e);
		}
	}

	public void setupCodeBorder()
	{
		setBorder(new EmptyBorder(0, 0, 0, 0));
	}

	public void bindConsole(BPConsole console)
	{
		console.setNotify(m_notifycb);
		m_console = console;
		addOnEnter(m_cononenter);
	}

	public BPConsole getConsole()
	{
		return m_console;
	}

	protected void onConsoleEnter(String str)
	{
		m_console.getController().writeString(str.toCharArray());
	}

	protected void onConsoleChanged()
	{
		UIUtil.laterUICombine(() ->
		{
			String str = m_console.dlString();
			getConsoleDocument().append(str, true);
			reposCaret();
		}, m_appendlock);
	}

	public void reposCaret()
	{
		setCaretPosition(getViewText().length());
	}

	protected void pressFunctionKey(int keycode, KeyEvent e)
	{
		switch (keycode)
		{
			case KeyEvent.VK_ENTER:
			{
				e.consume();
				onEnter();
				break;
			}
			case KeyEvent.VK_UP:
			{
				switchHistory(true, e);
				break;
			}
			case KeyEvent.VK_DOWN:
			{
				switchHistory(false, e);
				break;
			}
		}
	}

	protected void switchHistory(boolean isup, KeyEvent e)
	{
		e.consume();
	}

	public void addOnEnter(Consumer<String> callback)
	{
		m_doenter.addConsumer(callback);
	}

	public void addOnEnterSync(Consumer<String> callback)
	{
		m_onentersync.addConsumer(callback);
	}

	protected void onEnter()
	{
		String cmd = getCommandLine();
		getConsoleDocument().append("\n", true);
		m_doenter.accept(cmd);
		m_onentersync.accept(cmd);
	}
	
	public void runClear()
	{
		getConsoleDocument().clear();
	}

	public void runCommandFromOutside(String cmd)
	{
		getConsoleDocument().append(cmd+"\n", true);
		m_doenter.accept(cmd);
	}

	public void clearResource()
	{
		BPConsole console = m_console;
		m_console = null;
		if(console!=null)
			console.stop();

		super.clearResource();
	}

	public ConsoleDocument getConsoleDocument()
	{
		return (ConsoleDocument) getDocument();
	}

	protected String getCommandLine()
	{
		String text = getViewText();
		int cpos = text.length();
		int rindex = getConsoleDocument().getRIndex();
		if (cpos >= rindex && rindex < text.length())
			return text.substring(rindex, cpos);
		return "";
	}

	protected EditorKit createEditorKit()
	{
		return new ConsoleEditorKit();
	}

	protected static class ConsoleEditorKit extends TextEditorKit
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2622722701087752146L;

		public Document createDefaultDocument()
		{
			return new ConsoleDocument();
		}
	}

	public static class ConsoleDocument extends DefaultStyledDocument
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -9046939366181754461L;

		protected int m_rindex = 0;
		protected String m_inputhint = ">";

		protected int m_limit;

		public ConsoleDocument()
		{
			m_limit = 10475760;
			setDocumentFilter(new ConsoleDocumentFilter());
		}

		public void setInputHint(String inputhint)
		{
			m_inputhint = inputhint;
		}

		public int getRIndex()
		{
			return m_rindex;
		}

		public void append(String text, boolean isout)
		{
			int l = getLength();
			try
			{
				insertString(l, text, null);
			}
			catch (BadLocationException e)
			{
				Std.err(e);
			}
			checkTooLong();
			l = getLength();
			if (isout)
				m_rindex = getLength();
		}

		public void clear()
		{
			boolean isout = m_rindex == getLength();
			trimLines();
			int l = getLength();
			if (isout || m_rindex >= l)
				m_rindex = l;
		}

		protected void trimLines()
		{
			Element[] es = getRootElements();
			Element root = es[0];
			int c = root.getElementCount();
			if (c > 1)
			{
				Element e = root.getElement(c - 2);
				ConsoleDocumentFilter filter = ((ConsoleDocumentFilter) getDocumentFilter());
				try
				{
					filter.blockremove = true;
					remove(0, e.getEndOffset());
				}
				catch (BadLocationException e1)
				{
					Std.err(e1);
				}
				finally
				{
					filter.blockremove = false;
				}
			}
		}

		protected void checkTooLong()
		{
			int limit = m_limit;
			if (limit >= 0)
			{
				int c = getLength();
				if (c > limit)
				{
					// find pos
					Element[] es = getRootElements();
					Element root = es[0];
					int ei = root.getElementIndex(c - limit);
					if (ei > 0)
						ei--;
					Element e = root.getElement(ei);
					ConsoleDocumentFilter filter = ((ConsoleDocumentFilter) getDocumentFilter());
					try
					{
						filter.blockremove = true;
						remove(0, e.getEndOffset());
					}
					catch (BadLocationException e1)
					{
						Std.err(e1);
					}
					finally
					{
						filter.blockremove = false;
					}
				}
			}
		}

		public void sendInputHint(boolean newline)
		{
			int l = getLength();
			try
			{
				insertString(l, newline ? ("\n" + m_inputhint) : m_inputhint, null);
			}
			catch (BadLocationException e)
			{
				UIStd.err(e);
			}
			m_rindex = getLength();
		}
	}

	protected static class ConsoleDocumentFilter extends DocumentFilter
	{
		public volatile boolean blockremove = false;

		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException
		{
			if (!blockremove)
			{
				int rindex = ((ConsoleDocument) fb.getDocument()).getRIndex();
				if (offset >= rindex)
					fb.remove(offset, length);
			}
			else
			{
				fb.remove(offset, length);
			}
		}

		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException
		{
			int rindex = ((ConsoleDocument) fb.getDocument()).getRIndex();
			if (offset >= rindex)
				fb.insertString(offset, string, attr);
		}

		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException
		{
			int rindex = ((ConsoleDocument) fb.getDocument()).getRIndex();
			if (offset >= rindex)
				fb.replace(offset, length, text, attrs);
		}
	}
}
