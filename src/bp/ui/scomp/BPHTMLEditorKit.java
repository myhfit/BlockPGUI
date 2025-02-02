package bp.ui.scomp;

import java.awt.Font;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import javax.swing.text.html.StyleSheet;

import bp.config.UIConfigs;

public class BPHTMLEditorKit extends HTMLEditorKit
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5500231500860822658L;

	// private static final ViewFactory defaultFactory = new BPHTMLFactory();

	public Document createDefaultDocument()
	{
		StyleSheet styles = getStyleSheet();
		StyleSheet ss = new BPHTMLStyleSheet();

		ss.addStyleSheet(styles);

		HTMLDocument doc = new HTMLDocument(ss);
		doc.setParser(getParser());
		doc.setAsynchronousLoadPriority(4);
		doc.setTokenThreshold(100);
		return doc;
	}

	// public ViewFactory getViewFactory()
	// {
	// return defaultFactory;
	// }

	public static class BPHTMLStyleSheet extends StyleSheet
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3647608399432236020L;

		protected float m_fscale = 1f;

		public BPHTMLStyleSheet()
		{
			m_fscale = (float) UIConfigs.FONT_SCALE();
		}

		public Font getFont(String family, int style, int size)
		{
			int fsize = size;
			if (m_fscale != 1f)
			{
				fsize = Math.round(fsize * m_fscale);
			}
			return super.getFont(family, style, fsize);
		}
	}

	public static class BPHTMLFactory extends HTMLFactory
	{
		public View create(Element elem)
		{
			AttributeSet attrs = elem.getAttributes();
			Object elementName = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
			Object o = (elementName != null) ? null : attrs.getAttribute(StyleConstants.NameAttribute);
			if (o instanceof HTML.Tag)
			{
				HTML.Tag kind = (HTML.Tag) o;
				if (kind == HTML.Tag.IMG)
				{
					return new BPImageView(elem);
				}
			}
			return super.create(elem);
		}
	}

	public static class BPImageView extends ImageView
	{
		public BPImageView(Element elem)
		{
			super(elem);
		}
	}
}
