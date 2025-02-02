package bp.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;

import bp.BPCore;
import bp.config.UIConfigs;
import bp.project.BPResourceProject;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceOverlay;
import bp.ui.actions.BPCommonDialogActions;
import bp.ui.scomp.BPList;
import bp.ui.scomp.BPList.BPListModel;
import bp.ui.scomp.BPTextField;
import bp.ui.util.UIUtil;

public class BPDialogLocateProjectItem extends BPDialogCommon
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8481624091962507592L;

	protected BPList<BPResource> m_lstres;

	protected BPTextField m_txtres;

	protected BPResource m_res;

	protected volatile String m_keyword;

	protected List<BPResource> m_prjitems;

	protected void initUIComponents()
	{
		JPanel mainpan = new JPanel();
		JPanel cmdpan = new JPanel();
		m_txtres = new BPTextField();
		m_lstres = new BPList<BPResource>();

		m_txtres.setBorder(new EmptyBorder(0, 2, 0, 0));
		m_txtres.setMonoFont();
		m_txtres.getDocument().addDocumentListener(new UIUtil.BPDocumentChangedHandler(this::onResNameChanged));

		m_txtres.addKeyListener(new UIUtil.BPKeyListener(null, this::onTextDown, null));

		m_lstres.setMonoFont();
		m_lstres.setModel(new BPList.BPListModel<BPResource>());
		m_lstres.setCellRenderer(new FSCacheDataListRenderer());
		m_lstres.addMouseListener(new UIUtil.BPMouseListener(this::onListMouseClick, null, null, null, null));

		JScrollPane sp = new JScrollPane();
		sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		sp.setViewportView(m_lstres);
		mainpan.setLayout(new BorderLayout());
		mainpan.add(sp, BorderLayout.CENTER);
		mainpan.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(600, 0)));
		cmdpan.setLayout(new BorderLayout());
		cmdpan.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_STRONGBORDER()));
		cmdpan.add(m_txtres, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(mainpan, BorderLayout.CENTER);
		add(cmdpan, BorderLayout.NORTH);

		BPCommonDialogActions m_acts = new BPCommonDialogActions(this);
		m_acts.actionok.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		setCommandBar(new Action[] { m_acts.actionok, m_acts.actioncancel });

		setTitle("BlockP - Select Project Item");
		setModal(true);
	}

	public void setDefaultResourceKey(String key)
	{
		m_txtres.setText(key);
	}

	public void doSearch()
	{
		onResNameChanged(null);
	}

	protected void onListMouseClick(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
		{
			callCommonAction(COMMAND_OK);
		}
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
		int size = m_lstres.getModel().getSize();
		if (size > 0)
		{
			int si = m_lstres.getSelectedIndex();
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
			m_lstres.setSelectedIndex(newsi);
		}
	}

	public void filterItems(String keyword)
	{
		List<BPResource> items = new ArrayList<BPResource>();
		List<BPResource> prjitems = m_prjitems;
		for (BPResource prjitem : prjitems)
		{
			if (keyword.length() == 0 || prjitem.getName().toLowerCase().contains(keyword))
			{
				items.add(prjitem);
			}
		}
		((BPListModel<BPResource>) m_lstres.getModel()).setDatas(items);
		m_lstres.updateUI();
	}

	protected void onResNameChanged(DocumentEvent e)
	{
		String txt = m_txtres.getText().trim();
		filterItems(txt);
	}

	protected void setPrefers()
	{
		setPreferredSize(UIUtil.scaleUIDimension(new Dimension(800, 600)));
		super.setPrefers();
	}

	protected void initDatas()
	{
		List<BPResource> prjitems = new ArrayList<BPResource>();
		BPResourceProject[] prjs = BPCore.getProjectsContext().listProject();
		for (BPResourceProject prj : prjs)
		{
			prjitems.addAll(prj.getProjectFunctionItems());
		}
		m_prjitems = prjitems;
	}

	public boolean doCallCommonAction(int command)
	{
		if (command == COMMAND_OK)
		{
			BPResource selres = m_lstres.getSelectedValue();
			if (selres != null)
			{
				m_res = selres;
			}
			else
			{
				return true;
			}
		}
		return false;
	}

	public BPResource getSelectedResource()
	{
		return m_res;
	}

	@SuppressWarnings("serial")
	protected static class FSCacheDataListRenderer extends DefaultListCellRenderer
	{
		protected JLabel lbl;
		protected JLabel lbl2;
		protected JPanel pan;

		public FSCacheDataListRenderer()
		{
		}

		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			if (lbl == null)
			{
				lbl = new JLabel();
				lbl2 = new JLabel();
				pan = new JPanel();
				pan.setBorder(new EmptyBorder(0, 2, 0, 2));
				pan.setLayout(new BorderLayout());
				pan.add(lbl, BorderLayout.WEST);
				pan.add(lbl2, BorderLayout.EAST);
				lbl.setFont(list.getFont());
				lbl2.setFont(list.getFont());
			}
			if (isSelected)
			{
				Color bg = list.getSelectionBackground();
				Color fg = list.getSelectionForeground();
				pan.setBackground(bg);
				lbl.setBackground(bg);
				lbl.setForeground(fg);
				lbl2.setBackground(bg);
				lbl2.setForeground(fg);
			}
			else
			{
				Color bg = list.getBackground();
				Color fg = list.getForeground();
				pan.setBackground(bg);
				lbl.setBackground(bg);
				lbl.setForeground(fg);
				lbl2.setBackground(bg);
				lbl2.setForeground(UIConfigs.COLOR_TEXTHALF());
			}
			BPResource res = (BPResource) value;
			String p = null;
			if (res.isOverlay())
				p = ((BPResourceFileSystem) ((BPResourceOverlay) res).getRawResource()).getFileFullName();
			else if (res.isFileSystem())
				p = ((BPResourceFileSystem) res).getFileFullName();
			String txt = res.getName();
			lbl.setText(txt);
			lbl2.setText((p == null ? "" : " " + p));
			return pan;
		}
	}
}