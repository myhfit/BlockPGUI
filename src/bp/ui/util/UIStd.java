package bp.ui.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;

import bp.BPCore;
import bp.BPGUICore;
import bp.config.UIConfigs;
import bp.event.BPEvent;
import bp.event.BPEventCoreUI;
import bp.locale.BPLocaleConstCC;
import bp.task.BPTask;
import bp.typeext.KV;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.dialog.BPDialogBlock;
import bp.ui.dialog.BPDialogCommon;
import bp.ui.dialog.BPDialogSimple;
import bp.ui.scomp.BPCommonDataChainPanel;
import bp.ui.scomp.BPFileField;
import bp.ui.scomp.BPHTMLEditorKit;
import bp.ui.scomp.BPKVTable;
import bp.ui.scomp.BPKVTable.BPKVTableFuncs.BPKVTableFuncsEditable;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPList;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.scomp.BPTextField;
import bp.ui.scomp.BPTextPane;
import bp.ui.util.UIUtil.BPMouseListener;
import bp.util.ObjUtil;
import bp.util.LogicUtil.WeakRefGo;

public class UIStd
{
	public final static void err(Throwable e)
	{
		Throwable rawe = e;
		if (rawe instanceof RuntimeException)
		{
			Throwable r2 = rawe.getCause();
			if (r2 != null && r2 != e)
			{
				rawe = r2;
			}
		}
		textarea(rawe.getClass().getSimpleName() + ":" + rawe.getMessage(), UIUtil.wrapBPTitle(BPLocaleConstCC.ERR), false);
	}

	public final static void info_small(String message)
	{
		info_small(message, null);
	}

	public final static void info_small(String message, String title)
	{
		String dlgtitle = BPGUICore.S_BP_TITLE + " - " + (title == null ? BPActionConstCommon.TXT_INFO.text() : title);
		BPLabel lbl = new BPLabel(message);
		lbl.setLabelFont();
		lbl.setHorizontalAlignment(BPLabel.CENTER);
		lbl.setBorder(new EmptyBorder(6, 4, 6, 4));
		BPDialogSimple dlg = BPDialogSimple.createWithComponent(lbl, BPDialogCommon.COMMANDBAR_OKESCAPE, null);
		dlg.setTitle(title);
		dlg.setMinimumSize(UIUtil.scaleUIDimension(new Dimension(200, 60)));
		dlg.pack();
		Frame[] fs = Frame.getFrames();
		if (fs != null && fs.length > 0)
			dlg.setLocationRelativeTo(fs[0]);
		dlg.setTitle(dlgtitle);
		dlg.setModal(true);
		dlg.setVisible(true);
		dlg.dispose();
		dlg = null;
	}

	public final static void info(String message)
	{
		info(null, message);
	}

	public final static void info(String title, String message)
	{
		textarea(message, BPGUICore.S_BP_TITLE + " - " + (title == null ? BPActionConstCommon.TXT_INFO.text() : title), false);
	}

	public final static void showStructuredCommonDatas(Object data, boolean modal)
	{
		showStructuredCommonDatas(data, modal, null, null);
	}
	
	public final static void showStructuredCommonDatas(Object data, boolean modal, String title, Map<String, Object> options)
	{
		BPCommonDataChainPanel p = new BPCommonDataChainPanel();
		data = CommonDataUIProcs.preMappingData(data);
		p.setMode(CommonDataUIProcs.testDataMode(data));
		if (options != null)
			p.setOptions(options);
		p.setData(data);
		BPDialogSimple dlg = BPDialogSimple.createWithComponent(p, BPDialogSimple.COMMANDBAR_OKESCAPE, null);
		dlg.setModal(modal);
		p.initByData();
		dlg.setTitle(title == null ? BPGUICore.S_BP_TITLE : title);
		Integer w = options == null ? null : (Integer) options.get("dlg.width");
		if (w != null)
			dlg.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(w, (Integer) options.get("dlg.height"))));
		else
			dlg.setPreferredSize(UIUtil.getPercentDimension(0.6f, 0.8f));
		dlg.pack();
		dlg.setLocationRelativeTo(null);
		dlg.setVisible(true);
	}

	public final static void showData(Object data)
	{
		if (data == null)
			info(null);
		if (data instanceof Collection)
			showStructuredCommonDatas(data, true);
		else if (data instanceof Map)
			showStructuredCommonDatas(data, true);
		else
		{
			info(data.toString());
		}
	}

	public final static String input(String text, String prompt, String title)
	{
		final String[] rc = new String[1];
		JPanel panc = new JPanel();
		panc.setLayout(new BorderLayout());
		panc.setBackground(UIConfigs.COLOR_TEXTBG());
		BPLabel lbl = new BPLabel(prompt);
		BPTextField tf = new BPTextField();
		lbl.setLabelFont();
		lbl.setOpaque(true);
		lbl.setBackground(UIUtil.mix(UIConfigs.COLOR_WEAKBORDER(), UIConfigs.COLOR_WEAKBORDER().getAlpha() / 2));
		lbl.setBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()), new EmptyBorder(0, 2, 0, 2)));
		tf.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(200, UIConfigs.TEXTFIELD_HEIGHT())));
		tf.setMonoFont();
		tf.setText(text);
		tf.selectAll();

		panc.add(lbl, BorderLayout.WEST);
		panc.add(tf, BorderLayout.CENTER);
		int cb = (int) (UIConfigs.UI_SCALE() * 4f);
		panc.setBorder(new CompoundBorder(new EmptyBorder(cb, cb, cb, cb), new MatteBorder(1, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER())));
		Function<Integer, Boolean> dlgcallback = t ->
		{
			if (t == BPDialogCommon.COMMAND_OK)
				rc[0]=tf.getText();
			return false;
		};
		BPDialogSimple dlg = BPDialogSimple.createWithComponent(panc, BPDialogCommon.COMMANDBAR_OKENTER_CANCEL, dlgcallback);
		dlg.setTitle(title != null ? title : BPLocaleConstCC.INPUT.text());
		dlg.pack();
		dlg.setLocationRelativeTo(null);
		dlg.setModal(true);
		dlg.setVisible(true);
		dlg.dispose();
		return rc[0];
	}

	public final static String inputPath(String text, String prompt, String title)
	{
		final String[] rc = new String[1];
		JPanel panc = new JPanel();
		panc.setLayout(new BorderLayout());
		panc.setBackground(UIConfigs.COLOR_TEXTBG());
		BPLabel lbl = new BPLabel(prompt);
		BPFileField tf = new BPFileField();
		lbl.setLabelFont();
		lbl.setOpaque(true);
		lbl.setBackground(UIUtil.mix(UIConfigs.COLOR_WEAKBORDER(), UIConfigs.COLOR_WEAKBORDER().getAlpha() / 2));
		lbl.setBorder(new CompoundBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()), new EmptyBorder(0, 2, 0, 2)));
		tf.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(500, UIConfigs.TEXTFIELD_HEIGHT())));
		tf.setMonoFont();
		tf.initText(text);
		tf.selectAll();

		panc.add(lbl, BorderLayout.WEST);
		panc.add(tf, BorderLayout.CENTER);
		int cb = (int) (UIConfigs.UI_SCALE() * 4f);
		panc.setBorder(new CompoundBorder(new EmptyBorder(cb, cb, cb, cb), new MatteBorder(1, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER())));
		Function<Integer, Boolean> dlgcallback = (t) ->
		{
			if (t == BPDialogCommon.COMMAND_OK)
				rc[0] = tf.getText();
			return false;
		};
		BPDialogSimple dlg = BPDialogSimple.createWithComponent(panc, BPDialogCommon.COMMANDBAR_OKENTER_CANCEL, dlgcallback);
		dlg.setTitle(title);
		dlg.pack();
		dlg.setLocationRelativeTo(null);
		dlg.setModal(true);
		dlg.setVisible(true);
		dlg.dispose();
		return rc[0];
	}

	public final static String textarea(String text, String title)
	{
		return textarea(text, title, false, false);
	}

	public final static String textarea(String text, String title, boolean editable)
	{
		return textarea(text, title, editable, false);
	}

	public final static String textarea(String text, String title, boolean editable, boolean html)
	{
		final String[] rc = new String[1];
		JPanel pnl = new JPanel();
		JScrollPane scroll = new JScrollPane();
		final BPTextPane ta = new BPTextPane();
		if (html)
			ta.setEditorKit(new BPHTMLEditorKit());
		scroll.setViewportView(ta);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		ta.setBorder(new EmptyBorder(0, 0, 0, 0));
		ta.setMonoFont();
		ta.setText(text);
		ta.setEditable(editable);
		Function<Integer, Boolean> cb = (t) ->
		{
			if (t == BPDialogCommon.COMMAND_OK)
				rc[0] = ta.getText();
			return false;
		};
		pnl.setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2), new MatteBorder(1, 1, 1, 1, UIConfigs.COLOR_WEAKBORDER())));
		pnl.setLayout(new BorderLayout());
		pnl.add(scroll, BorderLayout.CENTER);
		BPDialogSimple dlg = BPDialogSimple.createWithComponent(pnl, editable ? BPDialogCommon.COMMANDBAR_OK_CANCEL : BPDialogCommon.COMMANDBAR_OKESCAPE, cb);
		dlg.setTitle(title);
		dlg.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(800, 600)));
		dlg.pack();
		Frame[] fs = Frame.getFrames();
		if (fs != null && fs.length > 0)
			dlg.setLocationRelativeTo(fs[0]);
		dlg.setModal(true);
		dlg.setVisible(true);

		ta.setText(null);
		scroll.setViewportView(null);
		scroll = null;
		dlg.dispose();
		dlg = null;
		return rc[0];
	}

	public final static List<KV> kv(List<Object[]> props, String title, boolean readonly)
	{
		List<KV> kvs = new ArrayList<KV>();
		for (Object[] prop : props)
		{
			KV kv = new KV();
			kv.key = (String) prop[0];
			kv.value = prop[1];
			kvs.add(kv);
		}
		JScrollPane scroll = new JScrollPane();
		BPKVTable ntable = new BPKVTable();
		BPKVTableFuncsEditable funcs = new BPKVTable.BPKVTableFuncs.BPKVTableFuncsEditable();
		BPTableModel<KV> model = new BPTableModel<KV>(funcs);
		model.setDatas(kvs);
		ntable.setCellEditorReadonly(readonly);
		ntable.setModel(model);
		ntable.setTableFont();
		ntable.initRowSorter();
		scroll.setViewportView(ntable);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		BPDialogSimple dlg = BPDialogSimple.createWithComponent(scroll, readonly ? -1 : BPDialogSimple.COMMANDBAR_OK_CANCEL, null);
		if (readonly)
		{
			dlg.addHiddenEscape();
			dlg.setAlwaysOnTop(true);
		}
		else
			dlg.setModal(true);
		dlg.setTitle(title);
		dlg.pack();
		dlg.setLocationRelativeTo(null);
		dlg.setVisible(true);
		if (readonly)
			return null;
		else
			return dlg.getActionResult() == BPDialogSimple.COMMAND_OK ? kvs : null;
	}

	public final static <T> void viewList(List<T> datas, String title, Function<Object, ?> renderer)
	{
		JScrollPane scroll = new JScrollPane();
		BPList<T> nlist = new BPList<T>();
		BPList.BPListModel<T> model = new BPList.BPListModel<T>();
		nlist.setModel(model);
		nlist.setListFont();
		model.setDatas(datas);
		if (renderer != null)
			nlist.setCellRenderer(new BPList.BPListRendererT<T>(renderer));
		scroll.setViewportView(nlist);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		BPDialogSimple dlg = BPDialogSimple.createWithComponent(scroll, BPDialogCommon.COMMANDBAR_OKESCAPE, null);
		if (title != null)
			dlg.setTitle(title);
		dlg.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(800, 600)));
		dlg.setModal(true);
		dlg.pack();
		dlg.setLocationRelativeTo(null);
		dlg.setVisible(true);
	}

	public final static <T> T select(List<T> datas, String title, Function<? super T, ?> renderer)
	{
		return select(datas, title, renderer, -1, false);
	}

	public final static <T> T select(List<T> datas, String title, Function<? super T, ?> renderer, int selectedindex)
	{
		return select(datas, title, renderer, selectedindex, false);
	}

	public final static <T> T select(List<T> datas, String title, Function<? super T, ?> renderer, int selectedindex, boolean showfilter)
	{
		T rc = null;
		JScrollPane scroll = new JScrollPane();
		BPList<T> nlist = new BPList<T>();
		BPList.BPListModel<T> model = new BPList.BPListModel<T>();
		nlist.setModel(model);
		nlist.setListFont();
		model.setDatas(datas);
		if (renderer != null)
			nlist.setCellRenderer(new BPList.BPListRendererT<T>(renderer));
		scroll.setViewportView(nlist);
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		if (selectedindex > -1)
		{
			nlist.setSelectedIndex(selectedindex);
			nlist.ensureIndexIsVisible(selectedindex);
		}
		JPanel pnlmain=new JPanel();
		pnlmain.setLayout(new BorderLayout());
		pnlmain.add(scroll,BorderLayout.CENTER);
		
		if(showfilter)
		{
			BPTextField txtfilter = new BPTextField();
			Consumer<DocumentEvent> filterc = e ->
			{
				if (datas == null)
					return;
				String filterstr = txtfilter.getText();
				if (filterstr.trim().length() == 0)
				{
					nlist.getBPModel().setDatas(datas);
				}
				else
				{
					List<T> fdatas = new ArrayList<T>();
					for (T data : datas)
					{
						Object obj = renderer != null ? renderer.apply(data) : data;
						String str = ObjUtil.toString(obj);
						if (str == null)
							str = "";
						if (str.toLowerCase().contains(filterstr.toLowerCase()))
							fdatas.add(data);
					}
					nlist.getBPModel().setDatas(fdatas);
				}
			};
			Consumer<KeyEvent> ke = e ->
			{
				int d = 0;
				if (e.getKeyCode() == KeyEvent.VK_UP)
					d = -1;
				else if (e.getKeyCode() == KeyEvent.VK_DOWN)
					d = 1;
				if (d != 0)
				{
					int size = nlist.getModel().getSize();
					if (size > 0)
					{
						int si = nlist.getSelectedIndex();
						int newsi = -1;
						if (si < 0)
						{
							newsi = 0;
						}
						else
						{
							newsi = si + d;
							if (newsi >= size)
								newsi = size - 1;
							if (newsi < 0)
								newsi = 0;
						}
						nlist.setSelectedIndex(newsi);
						nlist.ensureIndexIsVisible(newsi);
					}
				}
			};
			txtfilter.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
			txtfilter.setLabelFont();
			txtfilter.getDocument().addDocumentListener(new UIUtil.BPDocumentChangedHandler(filterc));
			txtfilter.addKeyListener(new UIUtil.BPKeyListener(null, ke, null));
			txtfilter.setVisible(true);
			pnlmain.add(txtfilter, BorderLayout.NORTH);
		}
		
		BPDialogSimple dlg = BPDialogSimple.createWithComponent(pnlmain, BPDialogCommon.COMMANDBAR_OKENTER_CANCEL, null);
		nlist.addMouseListener(new BPMouseListener(e ->
		{
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
				dlg.callCommonAction(BPDialogCommon.COMMAND_OK);
		}, null, null, null, null));
		dlg.setTitle(title == null ? UIUtil.wrapBPTitles(BPActionConstCommon.TXT_SEL, BPActionConstCommon.TXT_DATA) : title);
		dlg.setPreferredSize(UIUtil.scaleUIDimension(new Dimension(600, 600)));
		dlg.setModal(true);
		dlg.pack();
		dlg.setLocationRelativeTo(null);
		dlg.setVisible(true);
		if (dlg.getActionResult() == BPDialogCommon.COMMAND_OK)
			rc = nlist.getSelectedValue();
		return rc;
	}

	@SuppressWarnings("unchecked")
	public final static <V> BPTask<V> blockTask(String taskid, String title)
	{
		if (taskid == null)
			return null;
		List<BPTask<?>> tasks = BPCore.getWorkspaceContext().getWorkLoadManager().listTasks();
		BPTask<V> task = null;
		for (BPTask<?> t : tasks)
		{
			if (t.getID().equals(taskid))
			{
				task = (BPTask<V>) t;
				break;
			}
		}
		if (task != null)
		{
			CompletableFuture<BPTask<V>> f = new CompletableFuture<BPTask<V>>();
			Consumer<BPEvent> cb = (e) ->
			{
				BPEventCoreUI ec = (BPEventCoreUI) e;
				if (taskid.equals(ec.subkey))
					f.complete((BPTask<V>) ec.datas[0]);
			};
			BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_CHANGETASKEND, cb);
			UIUtil.block(() -> f, title);
		}
		return task;
	}

	@SuppressWarnings("unchecked")
	public final static <V> BPTask<V> blockTaskWithProgress(String taskid, String title)
	{
		if (taskid == null)
			return null;
		List<BPTask<?>> tasks = BPCore.getWorkspaceContext().getWorkLoadManager().listTasks();
		BPTask<V> task = null;
		for (BPTask<?> t : tasks)
		{
			if (t.getID().equals(taskid))
			{
				task = (BPTask<V>) t;
				break;
			}
		}
		if (task != null)
		{
			CompletableFuture<BPTask<V>> f = new CompletableFuture<BPTask<V>>();
			WeakRefGo<BPDialogBlock<?>> dlgref = new WeakRefGo<BPDialogBlock<?>>();
			AtomicReference<String> strref = new AtomicReference<>();
			Consumer<String> cbrefresh = (str) -> dlgref.run(dlg -> dlg.refreshText(str));
			UIUtil.LaterUIUpdateSegment<String> uiseg = new UIUtil.LaterUIUpdateSegment<String>(cbrefresh, strref);
			Consumer<BPEvent> cb = (e) ->
			{
				BPEventCoreUI ec = (BPEventCoreUI) e;
				if (taskid.equals(ec.subkey))
					f.complete((BPTask<V>) ec.datas[0]);
			};
			Consumer<BPEvent> cb2 = (e) ->
			{
				BPEventCoreUI ec = (BPEventCoreUI) e;
				if (taskid.equals(ec.subkey))
				{
					BPTask<V> t = (BPTask<V>) ec.datas[0];
					String pgtext = t.getProgressText();
					uiseg.updateObject(pgtext);
				}
			};
			BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_CHANGETASKEND, cb);
			BPCore.EVENTS_CORE.on(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_CHANGETASKSTATUS, cb2);
			UIUtil.block(() -> f, title, true, true, dlg -> dlgref.setTarget(dlg));
			BPCore.EVENTS_CORE.off(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_CHANGETASKEND, cb);
			BPCore.EVENTS_CORE.off(BPCore.getCoreUIChannelID(), BPEventCoreUI.EVENTKEY_COREUI_CHANGETASKSTATUS, cb2);
		}
		return task;
	}

	public final static boolean confirm(Component parent, String title, String message)
	{
		if (title == null)
			title = BPGUICore.S_BP_TITLE;
		JPanel pnl = new JPanel();
		BPLabel lbl = new BPLabel(message);
		lbl.setLabelFont();
		lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
		lbl.setFont(UIUtil.deltaFont(lbl.getFont(), 2));
		lbl.setHorizontalAlignment(BPLabel.CENTER);
		lbl.setVerticalAlignment(BPLabel.CENTER);
		pnl.setLayout(new BorderLayout());
		pnl.add(lbl, BorderLayout.CENTER);
		BPDialogSimple dlg = BPDialogSimple.createWithComponent(pnl, BPDialogCommon.COMMANDBAR_OKENTER_CANCEL, null);
		dlg.setTitle(title);
		dlg.setMinimumSize(UIUtil.scaleUIDimension(new Dimension(400, 200)));
		dlg.pack();
		Frame[] fs = Frame.getFrames();
		if (fs != null && fs.length > 0)
			dlg.setLocationRelativeTo(fs[0]);
		dlg.setModal(true);
		dlg.setVisible(true);
		boolean rc = dlg.getActionResult() == BPDialogSimple.COMMAND_OK;
		dlg.dispose();
		dlg = null;
		return rc;
	}

	public final static void wrapSeg(Runnable seg)
	{
		try
		{
			seg.run();
		}
		catch (Exception e2)
		{
			UIStd.err(e2);
		}
	}

	public final static void wrapSegE(ERunnable seg)
	{
		try
		{
			seg.run();
		}
		catch (Exception e2)
		{
			UIStd.err(e2);
		}
	}

	public static interface ERunnable
	{
		public void run() throws Exception;
	}
}
