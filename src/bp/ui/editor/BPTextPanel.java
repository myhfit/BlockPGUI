package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Caret;

import bp.config.BPSetting;
import bp.data.BPTextContainer;
import bp.format.BPFormatText;
import bp.processor.BPDataProcessor;
import bp.processor.BPDataProcessorManager;
import bp.processor.BPResourceProcessor;
import bp.res.BPResource;
import bp.res.BPResourceByteArray;
import bp.res.BPResourceHolder;
import bp.ui.actions.BPAction;
import bp.ui.dialog.BPDialogCommon;
import bp.ui.dialog.BPDialogSetting;
import bp.ui.scomp.BPTextPane;
import bp.ui.util.UIUtil;
import bp.util.TextUtil;

public class BPTextPanel extends JPanel implements BPTextEditor<JPanel, BPTextContainer>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 865294091856038835L;

	protected JScrollPane m_scroll;
	protected BPTextPane m_txt;

	protected BPTextContainer m_con = null;
	protected WeakReference<Consumer<String>> m_dynainfo = null;

	protected Action[] m_acts;
	protected Action actcopy;
	protected Action actcut;
	protected Action actpaste;
	protected Action actprocessor;

	protected int m_channelid;

	public BPTextPanel()
	{
		init();
	}

	protected void init()
	{
		m_scroll = new JScrollPane();
		m_txt = createTextPane();
		m_scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_scroll.getViewport().add(m_txt);
		m_txt.setOnPosChanged(this::onPosChanged);
		initActions();
		setLayout(new BorderLayout());
		add(m_scroll, BorderLayout.CENTER);
		initListeners();
	}

	protected void initListeners()
	{
		if (m_txt != null)
		{
			m_txt.addMouseListener(new UIUtil.BPMouseListenerForPopup(this::onContextMenu));
			m_txt.addKeyListener(new UIUtil.BPKeyListener(null, this::onCommonKeyDown, null));
		}
	}

	protected void initActions()
	{
		actcopy = BPAction.build("Copy").callback(this::onCopy).mnemonicKey(KeyEvent.VK_C).getAction();
		actcut = BPAction.build("Cut").callback(this::onCut).mnemonicKey(KeyEvent.VK_T).getAction();
		actpaste = BPAction.build("Paste").callback(this::onPaste).mnemonicKey(KeyEvent.VK_P).getAction();
		actprocessor = BPAction.build("Processor").getAction();
		List<Action> actps = new ArrayList<Action>();
		List<BPDataProcessor<?, ?>> ps = BPDataProcessorManager.getDataProcessors(BPFormatText.FORMAT_TEXT);
		for (BPDataProcessor<?, ?> p : ps)
		{
			if (p instanceof BPResourceProcessor)
			{
				String pname = p.getName();
				Action actp = BPAction.build(p.getUILabel()).callback((e) -> callResourceProcessor(pname)).getAction();
				actps.add(actp);
			}
		}
		actprocessor.putValue(BPAction.SUB_ACTIONS, actps.toArray(new Action[actps.size()]));
		m_acts = new Action[] { actcopy, actcut, actpaste, actprocessor };
	}

	protected BPTextPane createTextPane()
	{
		return new BPTextPane();
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void loadText(String text)
	{
		m_txt.setText(text);
		m_txt.clearUndos();
		m_txt.setCaretPosition(0);
	}

	protected void onPosChanged(int row, int col)
	{
		sendTextPosDynamicInfo(row, col);
	}

	protected void sendTextPosDynamicInfo(int row, int col)
	{
		sendDynamicInfo(row + ":" + col);
	}

	protected void sendDynamicInfo(String info)
	{
		WeakReference<Consumer<String>> dynainfo = m_dynainfo;
		if (dynainfo != null)
		{
			Consumer<String> cb = dynainfo.get();
			if (cb != null)
			{
				cb.accept(info);
			}
		}
	}

	public void bind(BPTextContainer con, boolean noread)
	{
		m_con = con;
		if (!noread && m_con.canOpen())
		{
			m_con.open();
			setTextContainerValue(m_con.readAllText());
			m_txt.clearUndos();
			m_txt.setSaved();
			m_con.close();
			m_txt.setCaretPosition(0);
		}
	}

	protected void setTextContainerValue(String text)
	{
		m_txt.setText(m_con.readAllText());
	}

	public void unbind()
	{
		m_con.close();
		m_con = null;
	}

	public void clearResource()
	{
		BPTextPane txt = m_txt;
		m_txt = null;
		if (txt != null)
			txt.clearResource();
	}

	public void save()
	{
		m_con.open();
		boolean flag = m_con.writeAllText(m_txt.getText());
		m_con.close();
		if (flag)
		{
			m_txt.setSaved();
		}
	}

	protected void onContextMenu(MouseEvent e)
	{
		JComponent source = (JComponent) e.getSource();
		showContextMenu(source, e.getX(), e.getY());
	}

	protected void showContextMenu(Component c, int x, int y)
	{
		if (m_acts != null && m_acts.length > 0)
		{
			JPopupMenu pop = new JPopupMenu();
			JComponent[] comps = UIUtil.makeMenuItems(m_acts);
			for (JComponent comp : comps)
			{
				pop.add(comp);
			}
			pop.show(c, x, y);
		}
	}

	protected void onCommonKeyDown(KeyEvent e)
	{
		int keycode = e.getKeyCode();
		switch (keycode)
		{
			case KeyEvent.VK_CONTEXT_MENU:
			{
				Point pt = null;
				{
					Caret c = m_txt.getCaret();
					if (c != null)
						pt = c.getMagicCaretPosition();
				}
				if (pt != null)
				{
					showContextMenu(m_txt, pt.x, pt.y);
				}
				break;
			}
		}
	}

	public void reloadData()
	{
		m_con.open();
		m_txt.setText(m_con.readAllText());
		m_con.close();
	}

	public boolean needSave()
	{
		return m_txt.needSave();
	}

	public void setNeedSave(boolean flag)
	{
		m_txt.setNeedSave(flag);
	}

	public BPTextPane getTextPanel()
	{
		return m_txt;
	}

	public void setChannelID(int channelid)
	{
		m_channelid = channelid;
	}

	public int getChannelID()
	{
		return m_channelid;
	}

	public void setID(String id)
	{
		BPTextPane txt = m_txt;
		if (txt != null)
			txt.setID(id);
	}

	public String getID()
	{
		return m_txt.getID();
	}

	public BPTextContainer getDataContainer()
	{
		return m_con;
	}

	public void focusEditor()
	{
		m_txt.requestFocusInWindow();
	}

	public String getEditorInfo()
	{
		String en = m_con == null ? null : m_con.getEncoding();
		return en == null ? null : en.toUpperCase();
	}

	protected void onCopy(ActionEvent event)
	{
		m_txt.copy();
	}

	protected void callResourceProcessor(String pname)
	{
		String str;
		boolean issel;
		if (m_txt.getSelectionEnd() == 0)
		{
			str = m_txt.getText();
			issel = false;
		}
		else
		{
			str = m_txt.getSelectedText();
			issel = true;
		}
		BPResourceHolder src = new BPResourceByteArray(TextUtil.fromString(str, "utf-8"), null, BPFormatText.FORMAT_TEXT, null, null, true);
		str = null;
		BPResourceHolder out = new BPResourceHolder.BPResourceHolderW(null, null, BPFormatText.MIME_TEXT, null, null, true);
		BPResourceProcessor<BPResource, BPResource> p = BPDataProcessorManager.getDataProcessorV(pname);
		BPSetting setting = p.getSetting(null);
		boolean outtext = p.canOutput(BPFormatText.FORMAT_TEXT);
		if (setting != null)
		{
			if (outtext)
			{
				setting.set("OUTPUT", out);
			}
			if (p.needSettingUI())
			{
				BPDialogSetting dlg = new BPDialogSetting();
				dlg.setSetting(setting);
				dlg.setVisible(true);
				if (dlg.getActionResult() != BPDialogCommon.COMMAND_OK)
					return;
				setting = dlg.getResult();
			}
		}
		if (outtext)
		{
			out = (BPResourceHolder) p.process(src, setting);
			String newtxt = out.getData();
			if (issel)
			{
				m_txt.replaceSelection(newtxt);
			}
			else
			{
				m_txt.setText(newtxt);
			}
		}
		else
			p.process(src, setting);
	}

	protected void onCut(ActionEvent event)
	{
		m_txt.cut();
	}

	protected void onPaste(ActionEvent event)
	{
		m_txt.paste();
	}

	public void setOnDynamicInfo(Consumer<String> info)
	{
		m_dynainfo = new WeakReference<Consumer<String>>(info);
	}

	public void setOnStateChanged(BiConsumer<String, Boolean> handler)
	{
		if (m_txt != null)
		{
			m_txt.setOnStateChanged(handler);
		}
	}

	public Action[] getEditMenuActions()
	{
		return m_acts;
	}

	public String[] getExts()
	{
		return new String[] { ".txt" };
	}
}
