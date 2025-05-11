package bp.ui.scomp;

import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.Reader;

import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.PlainView;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import bp.ui.actions.BPAction;
import bp.ui.dialog.BPDialogFindText;
import bp.util.TextUtil;

public class BPTextPane extends BPEditorPane
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9104661390457285947L;

	protected BPDialogFindText m_sdlg = null;

	public BPTextPane()
	{
		setMonoFont();
		setEditorKit(createEditorKit());

		getInputMap().put(KeyStroke.getKeyStroke("control F"), "find");
		getActionMap().put("find", BPAction.build("find").callback(this::onFind).getAction());
	}

	protected void onFind(ActionEvent e)
	{
		BPDialogFindText dlg = m_sdlg;
		if (dlg != null)
		{
			if (dlg.isVisible())
			{
				return;
			}
			else
			{
				dlg = null;
			}
		}
		if (dlg == null)
		{
			dlg = new BPDialogFindText(this);
			String sel = getSelectedText();
			if (sel != null && sel.length() > 0)
				dlg.setFindText(sel);
			m_sdlg = dlg;
			dlg.setVisible(true);
		}
	}

	protected EditorKit createEditorKit()
	{
		return new TextEditorKit();
	}

	public void setTabSize(int size)
	{
		Document doc = getDocument();
		if (doc != null)
		{
			int old = getTabSize();
			doc.putProperty(PlainDocument.tabSizeAttribute, Integer.valueOf(size));
			firePropertyChange("tabSize", old, size);
		}
	}

	public int getTabSize()
	{
		int size = 8;
		Document doc = getDocument();
		if (doc != null)
		{
			Integer i = (Integer) doc.getProperty(PlainDocument.tabSizeAttribute);
			if (i != null)
			{
				size = i.intValue();
			}
		}
		return size;
	}

	public void clearUndos()
	{
		m_um.discardAllEdits();
	}

	public void clearResource()
	{
		if (m_sdlg != null)
		{
			if (m_sdlg.isVisible())
			{
				m_sdlg.dispose();
			}
			m_sdlg = null;
		}
		m_state_changed = null;
		setEditorKit(createEditorKit());
		setText("");
	}

	public void find(String target, boolean isforward, boolean wholeword, boolean casesensitive, boolean onlysel)
	{
		int pos = isforward ? getSelectionEnd() : getSelectionStart();
		if (pos < 0)
			pos = getCaretPosition();
		if (!isforward)
			pos -= target.length();
		if (pos < 0)
			pos = 0;

		String text = getViewText();
		int si = findPos(target, text, pos, isforward, wholeword, casesensitive);
		if (si > -1)
		{
			setSelectionStart(si);
			setSelectionEnd(si + target.length());
		}
	}

	protected int findPos(String target, String text, int st, boolean isforward, boolean wholeword, boolean casesensitive)
	{
		if (isforward)
		{
			if (!casesensitive)
				return TextUtil.indexOfIgnoreCase(text, target, st);
			return text.indexOf(target, st);
		}
		else
		{
			if (!casesensitive)
				return TextUtil.lastIndexOfIgnoreCase(text, target, st);
			return text.lastIndexOf(target, st);
		}
	}

	public void replace(String src, String dest, boolean isforward, boolean wholeword, boolean casesensitive, boolean onlysel)
	{
	}

	public void replaceAll(String src, String dest, boolean isforward, boolean wholeword, boolean casesensitive, boolean onlysel)
	{

	}

	public void setSaved()
	{
		m_um.flagSave();
	}

	protected static class TextEditorKit extends StyledEditorKit
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8653373647124115043L;

		private static final ViewFactory defaultFactory = new TextViewFactory();

		public TextEditorKit()
		{

		}

		public ViewFactory getViewFactory()
		{
			return defaultFactory;
		}

		public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException
		{
			char[] buff = new char[4096];
			int nch;
			boolean lastWasCR = false;
			boolean isCRLF = false;
			boolean isCR = false;
			int last;
			boolean wasEmpty = (doc.getLength() == 0);
			AttributeSet attr = getInputAttributes();
			StringBuilder sb = new StringBuilder();

			while ((nch = in.read(buff, 0, buff.length)) != -1)
			{
				last = 0;
				for (int counter = 0; counter < nch; counter++)
				{
					switch (buff[counter])
					{
						case '\r':
							if (lastWasCR)
							{
								isCR = true;
								if (counter == 0)
								{
									sb.insert(pos, "\n");
									pos++;
								}
								else
								{
									buff[counter - 1] = '\n';
								}
							}
							else
							{
								lastWasCR = true;
							}
							break;
						case '\n':
							if (lastWasCR)
							{
								if (counter > (last + 1))
								{
									sb.insert(pos, new String(buff, last, counter - last - 1));
									pos += (counter - last - 1);
								}
								lastWasCR = false;
								last = counter;
								isCRLF = true;
							}
							break;
						default:
							if (lastWasCR)
							{
								isCR = true;
								if (counter == 0)
								{
									sb.insert(pos, "\n");
									pos++;
								}
								else
								{
									buff[counter - 1] = '\n';
								}
								lastWasCR = false;
							}
							break;
					}
				}
				if (last < nch)
				{
					if (lastWasCR)
					{
						if (last < (nch - 1))
						{
							sb.insert(pos, new String(buff, last, nch - last - 1));
							pos += (nch - last - 1);
						}
					}
					else
					{
						sb.insert(pos, new String(buff, last, nch - last));
						pos += (nch - last);
					}
				}
			}
			if (lastWasCR)
			{
				sb.insert(pos, "\n");
				isCR = true;
			}
			doc.insertString(0, sb.toString(), attr);
			if (wasEmpty)
			{
				if (isCRLF)
				{
					doc.putProperty(EndOfLineStringProperty, "\r\n");
				}
				else if (isCR)
				{
					doc.putProperty(EndOfLineStringProperty, "\r");
				}
				else
				{
					doc.putProperty(EndOfLineStringProperty, "\n");
				}
			}
		}
	}

	protected static class TextViewFactory implements ViewFactory
	{
		public View create(Element elem)
		{
			return new TextView(elem);
		}
	}

	static class TextView extends PlainView
	{
		protected Element longLine;
		
		public TextView(Element elem)
		{
			super(elem);
		}

		public int getLineHeight()
		{
			return metrics.getHeight();
		}
	}

	public void resizeDoc()
	{
		int c = getDocument().getDefaultRootElement().getElementCount();
		Border b = getBorder();
		if (b instanceof BPCodeBorder)
		{
			int cw = (c + "").length();
			GraphicsConfiguration gc = getGraphicsConfiguration();
			if (gc != null)
			{
				AffineTransform tr = gc.getDefaultTransform();
				float scale = (float) tr.getScaleY();
				((BPCodeBorder) b).setupBorder(getFontMetrics(getFont()), cw < 3 ? 3 : cw, scale);
			}
		}
	}
}
