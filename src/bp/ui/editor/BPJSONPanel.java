package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.BPConfig;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.config.UIConfigs;
import bp.data.BPTextContainerBase;
import bp.format.BPFormat;
import bp.format.BPFormatJSON;
import bp.res.BPResource;
import bp.ui.scomp.BPEditorPane;
import bp.ui.scomp.BPTree;
import bp.ui.scomp.BPTree.BPTreeModel;
import bp.ui.tree.BPTreeCellRendererObject;
import bp.ui.tree.BPTreeComponent;
import bp.ui.tree.BPTreeComponentBase;
import bp.ui.tree.BPTreeFuncsObject;
import bp.ui.util.UIUtil;
import bp.util.JSONUtil;
import bp.util.LogicUtil;
import bp.util.ObjUtil;
import bp.util.Std;
import bp.util.TextUtil;

public class BPJSONPanel extends BPCodeAndPreviewPanel<BPTreeComponent<BPTree>>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1019165525463664456L;

	protected BPTreeComponentBase m_tree;
	protected Consumer<BPEditorPane> m_changedhandler;
	protected AtomicBoolean m_changed;
	protected boolean m_canpreview;
	protected JScrollPane m_scroll2;
	protected JPanel m_sp;

	public BPJSONPanel()
	{
	}

	protected void init()
	{
		m_changed = new AtomicBoolean(false);
		m_sp = new JPanel();
		m_scroll2 = new JScrollPane();
		m_tree = new BPTreeComponentBase();
		m_scroll = new JScrollPane();
		m_txt = createTextPane();
		m_changedhandler = this::onTextChanged;

		m_tree.setRootVisible(false);
		m_tree.setTreeFont();
		m_tree.setCellRenderer(new BPTreeCellRendererObject());
		m_scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_txt.setOnPosChanged(this::onPosChanged);
		m_scroll2.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_WEAKBORDER()));

		setLayout(new BorderLayout());
		m_scroll.setViewportView(m_txt);
		m_scroll2.setViewportView(m_tree);
		m_sp.setLayout(new GridLayout(1, 2, 0, 0));
		m_sp.add(m_scroll);
		m_sp.add(m_scroll2);
		add(m_sp, BorderLayout.CENTER);

		m_txt.setChangedHandler(m_changedhandler);

		initActions();
		initListeners();

		m_canpreview = true;

		initPreview();
	}

	protected void initActions()
	{
		super.initActions();
		List<Action> acts = new CopyOnWriteArrayList<Action>(m_acts);
		m_acts = acts.toArray(new Action[acts.size()]);
	}

	protected Object getInitPreviewData()
	{
		return null;
	}

	public void setPreviewOnlyMode()
	{
		super.setPreviewOnlyMode();
		m_sp.remove(m_scroll);
	}

	public BPTreeComponent<BPTree> getPreviewComponent()
	{
		return m_tree;
	}

	protected Object transPreviewData(String text, boolean errout)
	{
		try
		{
			return JSONUtil.decode(text);
		}
		catch (Exception e)
		{
			if (errout)
				Std.err(e);
		}
		return null;
	}

	public void setPreviewData(Object data)
	{
		m_tree.setModel(new BPTreeModel(new BPTreeFuncsObject(data)));
	}

	protected void onTextChanged(BPEditorPane txt)
	{
		if (m_canpreview)
		{
			m_changed.set(true);
			UIUtil.laterUI(() ->
			{
				if (m_changed.compareAndSet(true, false))
				{
					setPreviewValue(txt.getText());
				}
			});
		}
	}

	public void toggleRightPanel()
	{
		if (m_ispmode)
			return;
		boolean canpreview = !m_canpreview;
		m_canpreview = canpreview;
		if (canpreview)
		{
			m_sp.add(m_scroll2);
			onTextChanged(m_txt);
		}
		else
		{
			m_sp.remove(m_scroll2);
		}
		m_sp.validate();
	}

	public void clearResource()
	{
		m_tree.clearResource();
		m_sp.removeAll();
	}

	public static class BPEditorFactoryJSON implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatJSON.FORMAT_JSON };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPJSONPanel();
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			BPJSONPanel pnl = (BPJSONPanel) editor;
			BPTextContainerBase con = new BPTextContainerBase();
			if (options != null)
			{
				LogicUtil.VLF(((String) options.get("encoding")), TextUtil::checkNotEmpty, con::setEncoding);
				if (ObjUtil.toBool(options.get("previewonly"), false))
					pnl.setPreviewOnlyMode();
			}
			con.bind(res);
			pnl.bind(con);
		}

		public String getName()
		{
			return "JSON Editor";
		}

		public boolean handleFormat(String formatkey)
		{
			return true;
		}

		public BPSetting getSetting(String formatkey)
		{
			BPSettingBase rc = new BPSettingBase();
			rc.addItem(BPSettingItem.create("encoding", "Encoding", BPSettingItem.ITEM_TYPE_TEXT, null));
			rc.addItem(BPSettingItem.create("previewonly", "Preview Only", BPSettingItem.ITEM_TYPE_SELECT, new String[] { "false", "true" }));
			return rc;
		}
	}
}
