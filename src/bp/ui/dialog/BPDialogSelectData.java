package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;

import bp.config.UIConfigs;
import bp.ui.scomp.BPList;
import bp.ui.scomp.BPTextField;
import bp.ui.util.UIUtil;
import bp.util.ObjUtil;

public class BPDialogSelectData<T> extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 550281267497605256L;

	protected List<T> m_datas;
	protected BPList<T> m_lst;
	protected BPTextField m_txtfilter;

	protected Function<T, String> m_transfunc;

	protected T m_result;

	public boolean doCallCommonAction(int command)
	{
		if (command == COMMAND_OK)
		{
			m_result = m_lst.getSelectedValue();
			if (m_result == null)
			{
				return true;
			}
		}
		return false;
	}

	protected void initUIComponents()
	{
		m_txtfilter = new BPTextField();
		m_txtfilter.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
		m_txtfilter.setLabelFont();
		m_txtfilter.getDocument().addDocumentListener(new UIUtil.BPDocumentChangedHandler(this::onFilterChanged));
		m_txtfilter.addKeyListener(new UIUtil.BPKeyListener(null, this::onTextDown, null));
		m_txtfilter.setVisible(false);

		m_lst = new BPList<T>();
		m_lst.setModel(new BPList.BPListModel<T>());
		m_lst.setCellRenderer(new BPList.BPListRenderer(this::onTrans));
		m_lst.setListFont();
		m_lst.addMouseListener(new UIUtil.BPMouseListener(this::onListClick, null, null, null, null));

		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(m_lst);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

		JPanel mainp = new JPanel();
		setLayout(new BorderLayout());
		mainp.setLayout(new BorderLayout());
		add(mainp);
		mainp.add(m_txtfilter, BorderLayout.NORTH);
		mainp.add(scroll, BorderLayout.CENTER);

		setCommandBarMode(COMMANDBAR_OK_CANCEL);
		setTitle("BlockP - Select Data");
		setModal(true);
	}

	protected void onTextDown(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_UP)
		{
			moveList(-1);
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			moveList(1);
		}
	}

	protected void moveList(int delta)
	{
		int size = m_lst.getModel().getSize();
		if (size > 0)
		{
			int si = m_lst.getSelectedIndex();
			int newsi = -1;
			if (si < 0)
			{
				newsi = 0;
			}
			else
			{
				newsi = si + delta;
				if (newsi >= size)
					newsi = size - 1;
				if (newsi < 0)
					newsi = 0;
			}
			m_lst.setSelectedIndex(newsi);
			m_lst.ensureIndexIsVisible(newsi);
		}
	}

	public void setFilterVisible(boolean flag)
	{
		m_txtfilter.setVisible(flag);
	}

	protected void onFilterChanged(DocumentEvent e)
	{
		if (m_datas == null)
			return;
		String filterstr = m_txtfilter.getText();
		if (filterstr.trim().length() == 0)
		{
			m_lst.getBPModel().setDatas(m_datas == null ? new ArrayList<T>() : m_datas);
		}
		else
		{
			List<T> fdatas = new ArrayList<T>();
			List<T> datas = m_datas;
			for (T data : datas)
			{
				String str = onTrans(data);
				if (str.toLowerCase().contains(filterstr.toLowerCase()))
				{
					fdatas.add(data);
				}
			}
			m_lst.getBPModel().setDatas(fdatas);
		}
	}

	protected void onListClick(MouseEvent e)
	{
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
		{
			callCommonAction(COMMAND_OK);
		}
	}

	public void setSource(List<T> datas)
	{
		m_datas = datas;
		initDatas();
	}

	@SuppressWarnings("unchecked")
	protected String onTrans(Object data)
	{
		if (m_transfunc != null)
			return m_transfunc.apply((T) data);
		return ObjUtil.toString(data);
	}

	public void setTransFunc(Function<T, String> transfunc)
	{
		m_transfunc = transfunc;
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(600, 600)));
		super.setPrefers();
	}

	protected void initDatas()
	{
		m_lst.getBPModel().setDatas(m_datas == null ? new ArrayList<T>() : m_datas);
	}

	public T getSelectData()
	{
		return m_result;
	}
}
