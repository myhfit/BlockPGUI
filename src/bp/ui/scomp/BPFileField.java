package bp.ui.scomp;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.util.UIUtil;

public class BPFileField extends BPTextField
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8393152762115522612L;

	protected JPopupMenu m_popup;
	protected BPList<String> m_lstfiles;

	protected String m_lasttext;

	public BPFileField()
	{
		getDocument().addDocumentListener(new UIUtil.BPDocumentChangedHandler(this::onChanged));
		addComponentListener(new UIUtil.BPComponentListener(null, null, null, this::onHidden));
		setFocusTraversalKeysEnabled(false);
		addKeyListener(new UIUtil.BPKeyListener(null, this::onKeyDown, null));
		getInputMap(JComponent.WHEN_FOCUSED).remove(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "onUp");
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "onDown");
		getActionMap().put("onUp", BPAction.build("").callback(this::onUp).getAction());
		getActionMap().put("onDown", BPAction.build("").callback(this::onDown).getAction());
	}

	protected void onKeyDown(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_TAB)
		{
			if (m_lstfiles != null)
			{
				onListSelect(true);
			}
		}
	}

	protected void setTargetFile(String tar)
	{
		String text = getText();
		int vi = text.lastIndexOf(File.separator);
		if (vi > -1)
		{
			text = text.substring(0, vi + 1) + tar;
		}
		else
		{
			text = tar;
		}
		m_lasttext = text;
		setText(text);
		hidePopup();
	}

	protected void onListSelect(boolean flag)
	{
		int vi = m_lstfiles.getSelectedIndex();
		String tar = null;
		if (vi > -1)
		{
			tar = m_lstfiles.getSelectedValue();
		}
		else if (flag)
		{
			BPList.BPListModel<String> model = m_lstfiles.getBPModel();
			if (model.getSize() > 0)
			{
				tar = model.getElementAt(0);
			}
		}
		if (tar != null)
		{
			setTargetFile(tar);
		}
	}

	protected void onListMouseUp(MouseEvent e)
	{
		onListSelect(false);
	}

	protected void onHidden(ComponentEvent e)
	{
		hidePopup();
	}

	protected void onUp(ActionEvent e)
	{
		if (m_lstfiles != null)
		{
			int si = m_lstfiles.getSelectedIndex();
			si--;
			if (si < -1)
				si = -1;
			m_lstfiles.setSelectedIndex(si);
			m_lstfiles.ensureIndexIsVisible(si);
		}
	}

	protected void onDown(ActionEvent e)
	{
		int si = m_lstfiles.getSelectedIndex();
		si++;
		if (si >= m_lstfiles.getBPModel().getDatas().size())
			si = m_lstfiles.getBPModel().getDatas().size() - 1;
		m_lstfiles.setSelectedIndex(si);
		m_lstfiles.ensureIndexIsVisible(si);
	}

	protected void onChanged(DocumentEvent e)
	{
		String text = getText();
		if (text.length() == 0)
		{
			hidePopup();
			return;
		}
		else
		{
			if (refreshPopup(text))
				showPopup();
		}
	}

	protected void hidePopup()
	{
		if (m_popup != null)
			m_popup.setVisible(false);
		if (m_lstfiles != null)
			m_lstfiles.getBPModel().setDatas(new ArrayList<String>());
	}

	protected void showPopup()
	{
		m_popup.show(this.getParent(), this.getX(), this.getY() + this.getHeight());
	}

	protected boolean refreshPopup(String text)
	{
		if (!text.equals(m_lasttext))
		{
			m_lasttext = text;
			List<String> files = getFiles(text);
			if (files != null)
			{
				if (m_popup == null)
				{
					m_popup = new JPopupMenu();
					m_popup.setBorder(new MatteBorder(1, 1, 1, 1, UIConfigs.COLOR_WEAKBORDER()));
					m_popup.setPreferredSize(new Dimension(getWidth(), 250));
					m_popup.setFocusable(false);
					m_lstfiles = new BPList<String>();
					m_lstfiles.setModel(new BPList.BPListModel<String>());
					m_lstfiles.setListFont();
					m_lstfiles.addMouseListener(new UIUtil.BPMouseListener(null, null, this::onListMouseUp, null, null));
					JScrollPane scroll = new JScrollPane(m_lstfiles);
					scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
					m_popup.add(scroll);
				}
				m_lstfiles.setSelectedIndex(-1);
				m_lstfiles.getBPModel().setDatas(files);
				m_lstfiles.updateUI();
				return true;
			}
		}
		return false;
	}

	protected List<String> getFiles(String text)
	{
		List<String> rc = null;
		String par = null;
		String filter = null;
		if (!text.startsWith(" "))
		{
			String sp = File.separator;
			if (text.endsWith(sp))
			{
				par = text;
			}
			else
			{
				int vi = text.lastIndexOf(sp);
				if (vi > -1)
				{
					par = text.substring(0, vi + 1);
					filter = text.substring(vi + 1);
				}
			}
			if (par != null)
			{
				File f = new File(par);
				if (f.exists() && f.isDirectory())
				{
					rc = new ArrayList<String>();
					String[] fs;
					if (filter != null)
					{
						final String ff = filter;
						fs = f.list((xf, name) -> name.toUpperCase().startsWith(ff.toUpperCase()));
					}
					else
					{
						fs = f.list();
					}
					int c = 0;
					for (String fname : fs)
					{
						rc.add(fname);
						c++;
						if (c > 20)
							break;
					}
				}
			}
		}
		return rc;
	}
}
