package bp.ui.scomp;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.util.UIUtil;
import bp.util.LogicUtil.WeakRefGo;
import bp.util.ObjUtil;

public class BPPopupComboList extends JPopupMenu
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5791797085202514722L;

	protected BPList<Object> m_lst;

	protected String m_lasttext;
	protected WeakRefGo<JTextComponent> m_txtref;
	protected WeakRefGo<BPPopupComboController> m_controllerref;

	protected int m_tarmode = 0;

	public final static int TARGETMODE_TABANDENTERSUBMIT = 0;
	public final static int TARGETMODE_ENTERSUBMIT = 1;

	public BPPopupComboList()
	{
		setBorder(new MatteBorder(1, 1, 1, 1, UIConfigs.COLOR_WEAKBORDER()));
		setFocusable(false);
		m_lst = new BPList<Object>();
		m_lst.setModel(new BPList.BPListModel<Object>());
		m_lst.addMouseListener(new UIUtil.BPMouseListener(null, this::onListMouseDown, null, null, null));
		JScrollPane scroll = new JScrollPane(m_lst);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		add(scroll);
		m_controllerref = new WeakRefGo<BPPopupComboController>();
	}

	public void setTargetMode(int mode)
	{
		m_tarmode = mode;
	}

	public void bind(JTextComponent txt, BPPopupComboController controller)
	{
		m_txtref = new WeakRefGo<JTextComponent>(txt);
		m_controllerref = new WeakRefGo<BPPopupComboController>(controller);
		m_lst.setFont(txt.getFont());
		m_lst.setCellRenderer(new BPList.BPListRenderer(controller.transfunc));
		txt.getDocument().addDocumentListener(new UIUtil.BPDocumentChangedHandler(this::onChanged));
		txt.addComponentListener(new UIUtil.BPComponentListener(null, null, null, this::onHidden));
		txt.setFocusTraversalKeysEnabled(false);
		txt.addKeyListener(new UIUtil.BPKeyListener(null, this::onKeyDown, null));
		if (m_tarmode == TARGETMODE_TABANDENTERSUBMIT)
		{
			txt.getInputMap(JComponent.WHEN_FOCUSED).remove(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
		}
		txt.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "onUp");
		txt.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "onDown");
		txt.getActionMap().put("onUp", BPAction.build("").callback(this::onUp).getAction());
		txt.getActionMap().put("onDown", BPAction.build("").callback(this::onDown).getAction());
	}

	protected void onKeyDown(KeyEvent e)
	{
		int tarmode = m_tarmode;
		if (e.getKeyCode() == KeyEvent.VK_TAB && tarmode == TARGETMODE_TABANDENTERSUBMIT)
		{
			if (m_lst != null)
			{
				onListSelect(true, false);
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			if (m_lst != null)
			{
				onListSelect(true, true);
			}
		}
	}

	protected void onListSelect(boolean flag, boolean needsubmit)
	{
		int vi = m_lst.getSelectedIndex();
		Object tar = null;
		if (vi > -1)
		{
			tar = m_lst.getSelectedValue();
		}
		else if (flag)
		{
			BPList.BPListModel<Object> model = m_lst.getBPModel();
			if (model.getSize() > 0)
			{
				tar = model.getElementAt(0);
			}
		}
		if (tar != null)
		{
			setTarget(tar, needsubmit);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setTarget(Object tar, boolean needsubmit)
	{
		String txt = transData(tar);
		m_lasttext = txt;
		final String txt2 = txt;
		m_txtref.run(t -> t.setText(txt2));
		hidePopup();
		if (needsubmit)
		{
			m_controllerref.run(c -> c.blockpopup = true);
			try
			{
				Consumer submitfunc = m_controllerref.exec(c -> c.submitfunc);
				if (submitfunc != null)
					submitfunc.accept(tar);
			}
			finally
			{
				m_controllerref.run(c -> c.blockpopup = false);
			}
		}
	}

	protected String transData(Object v)
	{
		Function<Object, String> f = m_controllerref.exec(c -> c.transfunc);
		if (f != null)
			return f.apply(v);
		return ObjUtil.toString(v);
	}

	protected void onListMouseDown(MouseEvent e)
	{
		onListSelect(false, true);
	}

	protected void onHidden(ComponentEvent e)
	{
		hidePopup();
	}

	protected void onUp(ActionEvent e)
	{
		if (m_lst != null)
		{
			int si = m_lst.getSelectedIndex();
			si--;
			if (si < -1)
				si = -1;
			m_lst.setSelectedIndex(si);
			m_lst.ensureIndexIsVisible(si);
		}
	}

	protected void onDown(ActionEvent e)
	{
		int si = m_lst.getSelectedIndex();
		si++;
		if (si >= m_lst.getBPModel().getDatas().size())
			si = m_lst.getBPModel().getDatas().size() - 1;
		m_lst.setSelectedIndex(si);
		m_lst.ensureIndexIsVisible(si);
	}

	protected void onChanged(DocumentEvent e)
	{
		if (m_controllerref.exec(c -> c.blockpopup))
			return;
		String txt = m_txtref.exec(t -> t.getText());
		if (txt.length() == 0)
		{
			m_lasttext = "";
			hidePopup();
			return;
		}
		else
		{
			if (refreshPopup(txt))
				showPopup();
		}
	}

	protected boolean refreshPopup(String txt)
	{
		if (!txt.equals(m_lasttext))
		{
			m_lasttext = txt;
			List<Object> datas = getDatas(txt);
			if (datas != null)
			{
				m_lst.setSelectedIndex(-1);
				m_lst.getBPModel().setDatas(datas);
				m_lst.updateUI();
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	protected List<Object> getDatas(String txt)
	{
		return (List<Object>) m_controllerref.exec(c -> c.listfunc.apply(txt));
	}

	protected void hidePopup()
	{
		setVisible(false);
		m_lst.getBPModel().setDatas(new ArrayList<Object>());
	}

	protected void showPopup()
	{
		m_txtref.run(t ->
		{
			setPreferredSize(new Dimension(t.getWidth(), 250));
			show(t.getParent(), t.getX(), t.getY() + t.getHeight() - 1);
		});
	}

	public static class BPPopupComboController
	{
		public Function<String, List<?>> listfunc;
		public Function<Object, String> transfunc;
		public Consumer<?> submitfunc;
		public boolean blockpopup;

		public BPPopupComboController()
		{

		}

		public BPPopupComboController(Function<String, List<?>> listfunc, Function<Object, String> transfunc, Consumer<?> submitfunc)
		{
			this.listfunc = listfunc;
			this.transfunc = transfunc;
			this.submitfunc = submitfunc;
		}
	}
}
