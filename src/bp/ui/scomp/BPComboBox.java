package bp.ui.scomp;

import java.awt.Component;
import java.awt.Font;
import java.util.List;
import java.util.function.Function;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicBorders.FieldBorder;

import bp.config.UIConfigs;
import bp.ui.util.UIUtil;

public class BPComboBox<E> extends JComboBox<E>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4849359944029741641L;

	public void setMonoFont()
	{
		setFont(UIUtil.monoFont(Font.PLAIN, UIConfigs.LISTFONT_SIZE()));
	}

	public void transMonoFont()
	{
		setFont(UIUtil.monoFont(Font.PLAIN, getFont().getSize()));
	}

	public void setListFont()
	{
		int fontsize = UIConfigs.LISTFONT_SIZE();
		Font tfont = new Font(UIConfigs.LIST_FONT_NAME(), Font.PLAIN, fontsize);
		setFont(tfont);
	}

	public boolean hasWBorder()
	{
		Border b = getBorder();
		if (b == null)
			return false;
		if (b instanceof EmptyBorder)
			return false;
		if (b instanceof FieldBorder)
			return true;
		return false;
	}
	
	public void replaceWBorder()
	{
		if (hasWBorder())
			setBorder(new MatteBorder(1, 1, 1, 1, UIConfigs.COLOR_STRONGBORDER()));
	}

	public void setText(String text)
	{
		JTextField comp = (JTextField) getEditor().getEditorComponent();
		comp.setText(text);
	}

	public String getText()
	{
		JTextField comp = (JTextField) getEditor().getEditorComponent();
		return comp.getText();
	}

	public BPComboBoxModel<E> getBPModel()
	{
		return (BPComboBoxModel<E>) getModel();
	}

	public static class BPComboBoxModel<T> extends DefaultComboBoxModel<T>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -297240821004165961L;

		protected List<T> m_datas;

		public BPComboBoxModel()
		{

		}

		public void setSelectedItem(Object item)
		{
			if (m_datas != null && m_datas.contains(item))
				super.setSelectedItem(item);
			else
				super.setSelectedItem(null);
		}

		public void setDatas(List<T> datas)
		{
			int s = 0;
			if (m_datas != null)
				s = m_datas.size();
			m_datas = datas;
			if (s > 0)
				fireIntervalRemoved(this, 0, s - 1);
			if (m_datas.size() > 0)
				fireIntervalAdded(this, 0, m_datas.size() - 1);
		}

		public List<T> getDatas()
		{
			return m_datas;
		}

		public int getSize()
		{
			return m_datas == null ? 0 : m_datas.size();
		}

		public T getElementAt(int index)
		{
			return m_datas == null ? null : m_datas.get(index);
		}
	}

	@SuppressWarnings("serial")
	public static class BPComboBoxRenderer extends DefaultListCellRenderer
	{
		protected Function<Object, Object> m_transfunc;

		public BPComboBoxRenderer(Function<Object, Object> transfunc)
		{
			m_transfunc = transfunc;
		}

		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			return super.getListCellRendererComponent(list, m_transfunc == null ? value : m_transfunc.apply(value), index, isSelected, cellHasFocus);
		}
	}
}
