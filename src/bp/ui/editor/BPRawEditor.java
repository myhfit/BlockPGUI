package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.BPCore;
import bp.compare.BPDataComparator;
import bp.compare.BPDataComparatorRaw;
import bp.compare.BPDataComparatorRaw.BPDataCompareResultRaw;
import bp.config.UIConfigs;
import bp.data.BPBytesHolder;
import bp.data.BPDataContainerFactory;
import bp.data.BPDataContainerOverlay;
import bp.data.BPDataContainerRandomAccess;
import bp.data.BPDataContainerRandomAccessBase;
import bp.data.BPDataContainerRandomAccessBase.BPBlockCache;
import bp.data.BPDataContainerRandomAccessOverlay;
import bp.data.BPTreeDataContainer;
import bp.env.BPEnvCommon;
import bp.env.BPEnvEditors;
import bp.env.BPEnvManager;
import bp.format.BPFormatManager;
import bp.res.BPResource;
import bp.res.BPResourceDCRA;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceHolder;
import bp.tool.BPToolGUIDataPipe;
import bp.ui.BPViewer;
import bp.ui.actions.BPAction;
import bp.ui.compare.BPComparableGUI;
import bp.ui.container.BPToolBarSQ;
import bp.ui.dialog.BPDialogSimple;
import bp.ui.parallel.BPEventUISyncEditor;
import bp.ui.parallel.BPSyncGUI;
import bp.ui.parallel.BPSyncGUIController;
import bp.ui.parallel.BPSyncGUIControllerBase;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPBytesCalcPane;
import bp.ui.scomp.BPHexPane;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPSwitchPanel;
import bp.ui.scomp.BPTextPane;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;
import bp.util.LogicUtil;
import bp.util.ObjUtil;
import bp.util.Std;

public class BPRawEditor extends JPanel implements BPEditor<JPanel>, BPViewer<BPDataContainerRandomAccess>, BPSyncGUI, BPComparableGUI<BPDataContainerRandomAccess, BPDataCompareResultRaw>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5762517069495915877L;

	public final static String SYNCPOSTYPE_RAW = "RAW";
	public final static String SYNCSELSTYPE_RAW = "RAW";

	protected boolean m_needsave;
	protected String m_id;
	protected int m_channelid;
	protected boolean m_fullscroll;
	protected BPDataContainerRandomAccess m_con;

	protected BPTextPane m_txtp;
	protected BPBytesCalcPane m_calcp;
	protected BPHexPane m_hexp;
	protected JScrollPane m_scroll;
	protected JPanel m_panright;

	protected BiFunction<Long, Integer, byte[]> m_readcb;
	protected BiFunction<Long, Integer, byte[]> m_rawreadcb;
	protected BiConsumer<byte[], Integer> m_previewcb;
	protected WeakReference<Consumer<String>> m_dynainfo;
	protected BiConsumer<String, Boolean> m_statechangedcb;
	protected Consumer<BPEventUISyncEditor> m_synccb;
	
	protected BPSyncGUIController m_syncobj;

	protected BPBlockCache m_cache;

	protected boolean m_nopreview = false;

	public BPRawEditor()
	{
		m_fullscroll = ObjUtil.toBool(BPEnvManager.getEnvValue(BPEnvEditors.ENV_NAME_EDITORS, BPEnvEditors.ENVKEY_RAWEDITOR_FULLSCROLL), false);
		m_txtp = new BPTextPane();
		m_hexp = new BPHexPane();
		m_scroll = new JScrollPane();
		m_calcp = new BPBytesCalcPane();
		m_panright = new JPanel();
		JPanel panright1 = new JPanel();
		JPanel panright2 = new JPanel();
		JPanel panright11 = new JPanel();
		BPLabel lblrrt = new BPLabel("PREVIEW");
		BPLabel lblrlt = new BPLabel("CALC");
		BPToolBarSQ tb = new BPToolBarSQ(true);
		BPSwitchPanel swrlt = new BPSwitchPanel();

		lblrlt.setForeground(UIConfigs.COLOR_TEXTHALF());
		lblrrt.setForeground(UIConfigs.COLOR_TEXTHALF());
		lblrrt.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()), new EmptyBorder(0, 2, 0, 0)));
		panright11.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()), new EmptyBorder(0, 2, 0, 0)));

		tb.setActions(makeToolBarActions());
		tb.setBorderVertical(0);

		m_calcp.setBackground(UIConfigs.COLOR_TEXTBG());
		panright1.setBackground(UIConfigs.COLOR_TEXTBG());
		panright2.setBackground(UIConfigs.COLOR_TEXTBG());
		panright11.setBackground(UIConfigs.COLOR_TEXTBG());
		swrlt.setBackground(UIConfigs.COLOR_TEXTBG());
		m_scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_scroll.setViewportView(m_hexp);
		m_panright.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_WEAKBORDER()));
		m_txtp.setPreferredSize(new Dimension(300, 200));
		m_txtp.setBorder(null);
		m_txtp.setEditable(false);
		m_calcp.setPreferredSize(new Dimension(200, 200));
		m_calcp.setBorder(null);
		panright2.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));

		int fontsize = UIConfigs.EDITORFONT_SIZE();
		Font tfont = new Font(UIConfigs.MONO_FONT_NAME(), Font.PLAIN, fontsize);
		m_hexp.setFont(tfont);
		m_txtp.setFont(tfont);
		lblrlt.setFont(tfont);
		lblrrt.setFont(tfont);
		swrlt.setup(new Object[] { "BE", "LE" }, null, (i, c) -> c.setFont(tfont));
		swrlt.setSelectedIndex(0);
		m_hexp.setFullScroll(m_fullscroll);

		setLayout(new BorderLayout());
		m_panright.setLayout(new BorderLayout());
		panright1.setLayout(new BorderLayout());
		panright2.setLayout(new BorderLayout());
		panright11.setLayout(new BorderLayout());

		panright11.add(lblrlt, BorderLayout.WEST);
		panright11.add(swrlt, BorderLayout.EAST);
		panright1.add(panright11, BorderLayout.NORTH);
		panright1.add(m_calcp, BorderLayout.CENTER);
		panright2.add(lblrrt, BorderLayout.NORTH);
		panright2.add(m_txtp, BorderLayout.CENTER);
		m_panright.add(panright2, BorderLayout.WEST);
		m_panright.add(panright1, BorderLayout.EAST);
		add(m_scroll, BorderLayout.CENTER);
		add(m_panright, BorderLayout.EAST);
		add(tb, BorderLayout.WEST);
		m_readcb = this::onRead;
		m_rawreadcb = this::onRawRead;
		m_previewcb = this::onPreview;
		m_synccb = this::onSyncEditor;
		m_syncobj = new BPSyncGUIControllerBase(m_synccb);

		m_hexp.setContextActions(makeContextActions());
		m_hexp.getScrollBar().addAdjustmentListener(this::onScroll);
	}

	protected Action[] makeToolBarActions()
	{
		BPAction acttest = BPAction.build("teststruct").tooltip("Test Structure").callback(this::onTestStructure).vIcon(BPIconResV.PATHTREE()).getAction();
		List<Action> rc = ObjUtil.makeList(BPAction.separator(), acttest);
		Action[] extacts = BPEditorActionManager.getBarActions(this);
		if (extacts != null)
			for (Action act : extacts)
				rc.add(act);
		return rc.toArray(new Action[rc.size()]);
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.CUSTOMCOMP;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void bind(BPDataContainerRandomAccess con, boolean noread)
	{
		m_con = con;
		if (!noread && con.canOpen())
		{
			con.open();
			long len = con.length();
			m_cache = new BPBlockCache(ObjUtil.toInt(BPEnvManager.getEnv(BPEnvCommon.ENV_NAME_COMMON).getValue(BPEnvCommon.ENVKEY_RAWIO_BLOCKSIZE), 4096), len, m_rawreadcb);
			m_hexp.setup(m_readcb, len, m_previewcb, this::onPosCB);
			con.close();
		}
	}

	public void rebind(BPDataContainerRandomAccess con)
	{
		if (con.isOverlay() && m_con.isOverlay())
		{
			((BPDataContainerRandomAccessOverlay) con).copyFrom((BPDataContainerRandomAccessOverlay) m_con);
			bind(con, true);
		}
	}

	public BPDataContainerRandomAccess createDataContainer(BPResource res)
	{
		BPDataContainerRandomAccessOverlay con = new BPDataContainerRandomAccessOverlay();
		if (res.isFileSystem() && ((BPResourceFileSystem) res).isFile())
		{
			con.setSource(new BPDataContainerRandomAccessBase());
		}
		else if (res instanceof BPResourceHolder && ((BPResourceHolder) res).isHold(byte[].class))
		{
			con.setSource(new BPDataContainerRandomAccessBase());
		}
		con.bind(res);
		con.initOverlay();
		return con;
	}

	public void setBytes(byte[] bs)
	{
		BPBytesHolder bh = new BPBytesHolder();
		bh.setData(bs);
		bind(bh);
	}

	protected void onTestStructure(ActionEvent e)
	{
		BPTreeDataEditor<BPTreeDataContainer> editor = new BPTreeDataEditor<>();
		String format = BPFormatManager.getFormatByExt(m_con.getResource().getExt()).getName();
		BPDataContainerFactory fac = ClassUtil.findService(BPDataContainerFactory.class, f -> f.canHandle(format));
		if (fac == null)
		{
			UIStd.info("Can't create DataContainer");
			return;
		}
		BPTreeDataContainer tcon = fac.createContainer(null);
		BPResource resori = m_con.getResource();
		String tempid = BPCore.genID(BPCore.getFileContext());
		BPResourceDCRA res = new BPResourceDCRA(m_con, null, resori.getExt(), tempid, tempid, true);
		tcon.bind(res);
		editor.bind(tcon);
		BPDialogSimple dlg = BPDialogSimple.createWithComponent(editor, BPDialogSimple.COMMANDBAR_OKESCAPE, null);
		dlg.setPreferredSize(UIUtil.getPercentDimension(0.6f, 0.6f));
		dlg.setModal(true);
		dlg.setTitle("Test Structure");
		dlg.pack();
		dlg.setLocationRelativeTo(this.getFocusCycleRootAncestor());
		dlg.setVisible(true);
	}

	protected void onPreview(byte[] bs, Integer linesize)
	{
		StringBuilder sb = new StringBuilder();
		int i = 0;
		while (i < bs.length)
		{
			if (sb.length() != 0)
				sb.append("\n");
			String txt = "";
			try
			{
				txt = pLine(bs, i, linesize);
				sb.append(txt);
			}
			catch (Exception e)
			{
			}
			i += linesize;
		}
		m_txtp.setText(sb.toString());
		m_txtp.clearUndos();
	}

	protected String pLine(byte[] bs, int s, int l)
	{
		boolean flag = false;
		int e = s + l;
		for (int i = s; i < e; i++)
		{
			if (bs[i] == '\n' || bs[i] == '\r')
			{
				flag = true;
				break;
			}
		}
		if (!flag)
		{
			return new String(bs, s, l);
		}
		else
		{
			byte[] nb = new byte[l];
			for (int i = s, j = 0; i < e; i++, j++)
			{
				if (bs[i] != '\n' && bs[i] != '\r')
				{
					nb[j] = bs[i];
				}
			}
			return new String(nb, 0, l);
		}
	}

	protected byte[] onRawRead(long pos, Integer len)
	{
		byte[] bs = new byte[len];
		try
		{
			m_con.open();
			m_con.read(pos, bs, 0, len);
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		finally
		{
			m_con.close();
		}
		return bs;
	}

	protected byte[] onRead(long pos, Integer len)
	{
		return m_cache.get(pos, len);
	}

	protected void doInsert(long pos, byte[] bs)
	{
		try
		{
			m_con.open();
			m_con.replace(pos, bs, 0, 0, bs.length);
			changeNeedSave(true);
			m_cache.reload(4096, m_con.length());
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		finally
		{
			m_con.close();
		}
	}

	protected void doOverwrite(long pos, byte[] bs)
	{
		try
		{
			m_con.open();
			m_con.overwrite(pos, bs, 0, bs.length);
			changeNeedSave(true);
			m_cache.reload(4096, m_con.length());
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		finally
		{
			m_con.close();
		}
	}

	protected void doReplace(long pos, byte[] bs, int orilen)
	{
		try
		{
			m_con.open();
			m_con.replace(pos, bs, 0, orilen, bs.length);
			changeNeedSave(true);
			m_cache.reload(4096, m_con.length());
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		finally
		{
			m_con.close();
		}
	}

	protected List<Action> makeContextActions()
	{
		Action actoverwritebs = BPAction.build("Overwrite Bytes...").callback(this::onOverwrite).getAction();
		Action actinsertbs = BPAction.build("Insert Bytes...").callback(this::onInsert).getAction();
		Action actreplacebs = BPAction.build("Replace Bytes...").callback(this::onReplace).getAction();
		Action actdp = BPAction.build("Data Pipe...").callback(this::onSendToDataPipe).getAction();

		return new CopyOnWriteArrayList<>(new Action[] { actoverwritebs, actinsertbs, actreplacebs, BPAction.separator(), actdp });
	}

	protected void onSendToDataPipe(ActionEvent e)
	{
		byte[] bs = m_hexp.getSelectedBytes();
		BPToolGUIDataPipe tool = new BPToolGUIDataPipe();
		tool.showTool(bs, true);
	}

	protected void onInsert(ActionEvent e)
	{
		long selstart, selend;
		{
			long[] sels = m_hexp.getSelection();
			selstart = sels[0];
			selend = sels[1];
		}
		if (selstart < 0)
			return;
		long orilen = selend - selstart + 1;
		if (orilen < 0 || orilen > Integer.MAX_VALUE)
			return;
		String hex = UIStd.input("", "Data(hex):", "Input replace data");
		if (hex == null)
			return;
		byte[] bs = new byte[hex.length() / 2];
		hex = hex.toUpperCase();
		for (int i = 0; i < hex.length(); i += 2)
		{
			int c0 = hex.charAt(i);
			int c1 = hex.charAt(i + 1);
			int d0 = c0 > '9' ? c0 - 'A' + 10 : c0 - '0';
			int d1 = c1 > '9' ? c1 - 'A' + 10 : c1 - '0';
			bs[i >> 1] = (byte) (((byte) d0 << 4) + ((byte) d1));
		}
		doInsert(selstart, bs);
		m_hexp.updateLen(m_con.length());
		m_hexp.updateView();
	}

	protected void onReplace(ActionEvent e)
	{
		long selstart, selend;
		{
			long[] sels = m_hexp.getSelection();
			selstart = sels[0];
			selend = sels[1];
		}
		if (selstart < 0)
			return;
		long orilen = selend - selstart + 1;
		if (orilen < 0 || orilen > Integer.MAX_VALUE)
			return;
		String hex = UIStd.input("", "Data(hex):", "Input replace data");
		if (hex == null)
			return;
		byte[] bs = new byte[hex.length() / 2];
		hex = hex.toUpperCase();
		for (int i = 0; i < hex.length(); i += 2)
		{
			int c0 = hex.charAt(i);
			int c1 = hex.charAt(i + 1);
			int d0 = c0 > '9' ? c0 - 'A' + 10 : c0 - '0';
			int d1 = c1 > '9' ? c1 - 'A' + 10 : c1 - '0';
			bs[i >> 1] = (byte) (((byte) d0 << 4) + ((byte) d1));
		}
		doReplace(selstart, bs, (int) orilen);
		m_hexp.updateLen(m_con.length());
		m_hexp.updateView();
	}

	protected void onOverwrite(ActionEvent e)
	{
		long selstart = m_hexp.getSelection()[0];
		if (selstart < 0)
			return;
		String hex = UIStd.input("", "Data(hex):", "Input overwrite data");
		if (hex == null)
			return;
		byte[] bs = new byte[hex.length() / 2];
		hex = hex.toUpperCase();
		for (int i = 0; i < hex.length(); i += 2)
		{
			int c0 = hex.charAt(i);
			int c1 = hex.charAt(i + 1);
			int d0 = c0 > '9' ? c0 - 'A' + 10 : c0 - '0';
			int d1 = c1 > '9' ? c1 - 'A' + 10 : c1 - '0';
			bs[i >> 1] = (byte) (((byte) d0 << 4) + ((byte) d1));
		}
		doOverwrite(selstart, bs);
		m_hexp.updateView();
	}

	public void unbind()
	{
		m_con.close();
		m_con = null;
	}

	public BPDataContainerRandomAccess getDataContainer()
	{
		return m_con;
	}

	public void focusEditor()
	{
	}

	public String getEditorInfo()
	{
		return null;
	}

	public void save()
	{
		BPDataContainerRandomAccess con = m_con;
		if (con != null)
		{
			con.open();
			try
			{
				if (con.isOverlay())
					((BPDataContainerOverlay<?>) con).writeOverlayToSource();
				else
					con.writeAll(con.readAll());
			}
			finally
			{
				con.close();
			}
			changeNeedSave(false);
		}
	}

	public void reloadData()
	{
	}

	public boolean needSave()
	{
		return m_needsave;
	}

	public void setNeedSave(boolean needsave)
	{
		m_needsave = needsave;
	}

	protected void changeNeedSave(boolean needsave)
	{
		if (m_needsave != needsave)
		{
			m_needsave = needsave;
			LogicUtil.IFVU(m_statechangedcb, cb -> cb.accept(m_id, m_needsave));
		}
	}

	public Action[] getActBarActions()
	{
		return new Action[0];
	}

	public void setID(String id)
	{
		m_id = id;
	}

	public String getID()
	{
		return m_id;
	}

	public void setChannelID(int channelid)
	{
		m_channelid = channelid;
		m_syncobj.setChannelID(channelid);
	}

	public int getChannelID()
	{
		return m_channelid;
	}

	protected void onPosCB(long start, long end)
	{
		m_calcp.setBytes(m_hexp.getSelectedBytes());
		sendDynamicInfo(start >= 0 && end >= 0 ? (BPHexPane.getHEXStr(start) + " - " + BPHexPane.getHEXStr(end)) : "");

		if (m_syncobj.checkSyncAndNoBlock())
			m_syncobj.trigger(BPEventUISyncEditor.syncSelection(m_id, SYNCSELSTYPE_RAW, new long[] { start, end }));
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

	public void setOnStateChanged(BiConsumer<String, Boolean> handler)
	{
		m_statechangedcb = handler;
	}

	public void setOnDynamicInfo(Consumer<String> info)
	{
		m_dynainfo = new WeakReference<Consumer<String>>(info);
	}

	public void toggleRightPanel()
	{
		boolean nopreview = !m_nopreview;
		m_nopreview = nopreview;
		m_panright.setVisible(!nopreview);

		validate();
		updateUI();
	}

	protected void onScroll(AdjustmentEvent e)
	{
		if (m_syncobj.checkSyncAndNoBlock() && (m_fullscroll || !e.getValueIsAdjusting()))
			m_syncobj.trigger(BPEventUISyncEditor.syncPosition(m_id, SYNCPOSTYPE_RAW, m_hexp.getScrollBar().getValue()));
	}

	public void startSyncStatus()
	{
		m_syncobj.startSync();

		if (!m_nopreview)
			toggleRightPanel();
	}

	protected void onSyncEditor(BPEventUISyncEditor e)
	{
		if (BPEventUISyncEditor.SYNC_POS.equals(e.subkey))
		{
			if (SYNCPOSTYPE_RAW.equals(e.getSyncDataType()))
			{
				String id = (String) e.datas[0];
				if (!m_id.equals(id))
				{
					m_syncobj.blockSync(() -> m_hexp.getScrollBar().setValue(e.getSyncData()));
				}
			}
		}
		else if (BPEventUISyncEditor.SYNC_SELECTION.equals(e.subkey))
		{
			if (SYNCSELSTYPE_RAW.equals(e.getSyncDataType()))
			{
				String id = (String) e.datas[0];
				if (!m_id.equals(id))
				{
					long[] sels = e.getSyncData();
					m_syncobj.blockSync(() ->
					{
						m_hexp.setSelection(sels[0], sels[1]);
						m_hexp.repaint();
					});
				}
			}
		}
	}
	
	public BPSyncGUIController getSyncStatusController()
	{
		return m_syncobj;
	}

	public BPDataComparator<BPDataContainerRandomAccess, BPDataCompareResultRaw> getComparator()
	{
		return new BPDataComparatorRaw();
	}

	public BPDataContainerRandomAccess getCompareData()
	{
		return m_con;
	}
}
