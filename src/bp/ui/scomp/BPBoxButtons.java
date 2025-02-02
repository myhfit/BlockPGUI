package bp.ui.scomp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;

import bp.config.UIConfigs;
import bp.ui.res.icon.BPIconResV;

public class BPBoxButtons<T> extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -309492357072600154L;

	protected Function<T, String> m_renderer;
	protected Consumer<T> m_clickhandler;
	protected Consumer<T> m_callback;
	protected List<T> m_datas;
	protected boolean m_showselect = true;
	protected boolean m_showdelete = true;

	public BPBoxButtons(int axis)
	{
		BoxLayout l = new BoxLayout(this, axis);
		setLayout(l);
		setBorder(new EmptyBorder(0, 0, 0, 0));
	}

	public void setRenderer(Function<T, String> renderer)
	{
		m_renderer = renderer;
	}

	public void setCallback(Consumer<T> callback)
	{
		m_callback = callback;
	}

	public void setDatas(List<T> datas)
	{
		m_datas = datas;
		initDatas();
	}

	public List<T> getDatas()
	{
		return m_datas;
	}

	public void addData(T data)
	{
		m_datas.add(data);
		initDatas();
	}

	public void refresh()
	{
		initDatas();
	}

	protected void initDatas()
	{
		removeAll();
		for (T data : m_datas)
		{
			JComponent btn = createButton(data);
			JComponent btndel = createDelButton(data);
			JComponent btnsel = createSelectComponent(data);
			btn.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_TABLEGRID()));
			JPanel pan = new JPanel();
			pan.setLayout(new BorderLayout());
			pan.add(btn, BorderLayout.CENTER);
			if (btndel != null && m_showdelete)
				pan.add(btndel, BorderLayout.EAST);
			if (btnsel != null && m_showselect)
				pan.add(btnsel, BorderLayout.WEST);
			pan.setMaximumSize(new Dimension((int) (5000f * UIConfigs.UI_SCALE()), UIConfigs.BUTTON_SIZE() + 2));
			pan.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_TABLEGRID()));
			add(pan);
		}
		validate();
		repaint();
	}

	private JComponent createSelectComponent(T data)
	{
		JCheckBox box = new JCheckBox();
		box.addChangeListener(this::onCheckChanged);
		return box;
	}

	protected void onCheckChanged(ChangeEvent e)
	{

	}

	protected JComponent createDelButton(T data)
	{
		BPToolVIconButton btn = new BPToolVIconButton(BPIconResV.DEL(), (e) -> removeData(data));
		return btn;
	}

	public void removeData(T data)
	{
		for (int i = m_datas.size() - 1; i >= 0; i--)
		{
			if (m_datas.get(i) == data)
			{
				m_datas.remove(i);
			}
		}
		initDatas();
	}

	protected JComponent createButton(T data)
	{
		String lbl = null;
		if (m_renderer != null)
			lbl = m_renderer.apply(data);
		else
			lbl = data.toString();
		BPToolSQButton btn = new BPToolSQButton(lbl, () ->
		{
			onClick(data);
		});
		btn.setText(lbl);
		return btn;
	}

	protected void onClick(T data)
	{
		Consumer<T> handler = m_clickhandler;
		if (handler != null)
		{
			handler.accept(data);
		}
	}

	public void setClickHandler(Consumer<T> handler)
	{
		m_clickhandler = handler;
	}

	public void setShowSelect(boolean flag)
	{
		m_showselect = flag;
	}

	public void setShowDelete(boolean flag)
	{
		m_showdelete = flag;
	}
}