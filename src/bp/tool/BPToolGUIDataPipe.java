package bp.tool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.BPSetting;
import bp.config.UIConfigs;
import bp.data.BPDataConsumer;
import bp.data.BPDataEndpointFactory;
import bp.format.BPFormatText;
import bp.transform.BPTransformer;
import bp.transform.BPTransformerFactory;
import bp.transform.BPTransformerManager;
import bp.ui.actions.BPAction;
import bp.ui.actions.BPActionConstCommon;
import bp.ui.actions.BPActionHelpers;
import bp.ui.container.BPToolBarSQ;
import bp.ui.dialog.BPDialogSetting;
import bp.ui.scomp.BPCodePane;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPList;
import bp.ui.scomp.BPList.BPListModel;
import bp.ui.util.UIStd;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;
import bp.util.TextUtil;

public class BPToolGUIDataPipe extends BPToolGUIBase<BPToolGUIDataPipe.BPToolGUIContextDataPipe>
{
	public String getName()
	{
		return BPActionHelpers.getValue(BPActionConstCommon.TNAME_DPTOOL, null, null);
	}

	protected BPToolGUIContextDataPipe createToolContext()
	{
		return new BPToolGUIContextDataPipe();
	}

	protected static class BPToolGUIContextDataPipe implements BPToolGUIBase.BPToolGUIContext
	{
		protected JScrollPane m_scrollsrc;
		protected JScrollPane m_scrollpipes;
		protected BPCodePane m_txtsrc;
		protected BPList<BPDataConsumer<?>> m_lstpipes;
		protected BPLabel m_lbltype;
		protected Object m_src;
		protected String m_formatname;
		protected boolean m_isoutside;
		protected List<BPDataConsumer<?>> m_pipes;

		public void initUI(Container par, Object... params)
		{
			m_txtsrc = new BPCodePane();
			m_scrollsrc = new JScrollPane();
			m_scrollpipes = new JScrollPane();
			m_lstpipes = new BPList<BPDataConsumer<?>>();
			JPanel sp = new JPanel();
			sp.setLayout(new GridLayout(1, 2, 0, 0));
			JPanel psrc = new JPanel();
			JPanel ppipes = new JPanel();
			m_lbltype = new BPLabel();
			BPToolBarSQ toolbar = new BPToolBarSQ();
			Action actrun = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNRUN, this::onRunPipe);
			Action actaddtf = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNADD, BPActionConstCommon.ACT_BTNADD_ADDTF, this::onAddTransformer);
			Action actaddep = BPActionHelpers.getActionWithAlias(BPActionConstCommon.ACT_BTNADD, BPActionConstCommon.ACT_BTNADD_ADDEP, this::onAddEndpoint);
			Action actconfig = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNCONFIG, this::onConfigConsumer);
			Action actup = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNUP, this::onMoveUp);
			Action actdown = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNDOWN, this::onMoveDown);
			Action actdelitem = BPActionHelpers.getAction(BPActionConstCommon.ACT_BTNDEL, this::onDelItem);

			toolbar.setActions(new Action[] { actaddtf, actaddep, BPAction.separator(), actdelitem, BPAction.separator(), actup, actdown, BPAction.separator(), actconfig, actrun });

			m_lstpipes.setCellRenderer(new BPList.BPListRenderer(c -> ((BPDataConsumer<?>) c).getInfo()));
			m_scrollsrc.setViewportView(m_txtsrc);
			m_scrollpipes.setViewportView(m_lstpipes);
			m_txtsrc.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_lbltype.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()), new EmptyBorder(0, 2, 0, 0)));
			m_scrollsrc.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_scrollpipes.setBorder(new EmptyBorder(0, 0, 0, 0));
			sp.setBorder(new EmptyBorder(0, 0, 0, 0));
			toolbar.setBorderHorizontal(0);
			psrc.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_STRONGBORDER()));
			m_lbltype.setMinimumSize(new Dimension(0, UIUtil.scale(UIConfigs.BAR_HEIGHT_VICON())));
			m_lbltype.setPreferredSize(new Dimension(0, UIUtil.scale(UIConfigs.BAR_HEIGHT_VICON())));

			m_lbltype.setMonoFont();
			m_txtsrc.setMonoFont();
			m_lstpipes.setMonoFont();

			sp.add(psrc);
			sp.add(ppipes);
			psrc.setLayout(new BorderLayout());
			ppipes.setLayout(new BorderLayout());
			psrc.add(m_lbltype, BorderLayout.NORTH);
			psrc.add(m_scrollsrc, BorderLayout.CENTER);
			ppipes.add(toolbar, BorderLayout.NORTH);
			ppipes.add(m_scrollpipes, BorderLayout.CENTER);
			par.add(sp, BorderLayout.CENTER);
		}

		public void initDatas(Object... params)
		{
			m_pipes = new ArrayList<BPDataConsumer<?>>();
			BPListModel<BPDataConsumer<?>> model = new BPListModel<BPDataConsumer<?>>();
			model.setDatas(m_pipes);
			m_lstpipes.setModel(model);

			if (params == null || params.length == 0)
				setSource(null, BPFormatText.FORMAT_TEXT);
			else
			{
				Object p0 = params[0];
				if (p0 == null)
					setSource(null, BPFormatText.FORMAT_TEXT);
				else if (p0 instanceof String)
				{
					setSource(p0, BPFormatText.FORMAT_TEXT);
				}
				else if (p0 instanceof Image)
				{
					setSource(p0, params.length > 1 ? (String) params[1] : "PNG");
				}
				else if (p0 instanceof byte[])
				{
					setSource(p0, "byte[]", params.length > 1 ? (Boolean) params[1] : false);
				}
				else
				{
					setSource(null, BPFormatText.FORMAT_TEXT);
				}
			}
		}

		protected void setSource(Object src, String formatname)
		{
			setSource(src, formatname, false);
		}

		protected void setSource(Object src, String formatname, boolean hardref)
		{
			m_formatname = formatname;
			m_lbltype.setText("Source Type:" + formatname);
			if (src == null)
			{
				m_src = null;
				m_txtsrc.setText("");
				m_txtsrc.setEditable(true);
				m_isoutside = false;
			}
			else
			{
				if (src instanceof String)
				{
					m_src = tRef(src, hardref);
					m_txtsrc.setText((String) src);
					m_txtsrc.setEditable(false);
					m_isoutside = true;
				}
				else if (src instanceof Image)
				{
					m_src = tRef(src, hardref);
					Image img = (Image) src;
					m_txtsrc.setText(img.getWidth(null) + "x" + img.getHeight(null));
					m_txtsrc.setEditable(false);
					m_isoutside = true;
				}
				else if (src instanceof byte[])
				{
					m_src = tRef(src, hardref);
					m_txtsrc.setText(TextUtil.toString((byte[]) src, "utf-8"));
					m_txtsrc.setEditable(false);
					m_isoutside = true;
				}
			}
		}

		protected Object tRef(Object src, boolean hardref)
		{
			return hardref ? src : new WeakReference<Object>(src);
		}

		protected Object getSourceObject()
		{
			if (m_isoutside)
			{
				Object srcobj = m_src;
				if (srcobj != null)
				{
					if (srcobj instanceof WeakReference)
					{
						WeakReference<?> srcref = (WeakReference<?>) m_src;
						if (srcref != null)
							return srcref.get();
					}
					else
						return srcobj;
				}
				return null;
			}
			return m_txtsrc.getText();
		}

		public void clearResource()
		{
			m_src = null;
		}

		protected void onDelItem(ActionEvent e)
		{
			List<BPDataConsumer<?>> sels = m_lstpipes.getSelectedValuesList();
			m_pipes.removeAll(sels);
			m_lstpipes.updateUI();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected void onRunPipe(ActionEvent e)
		{
			Object source = getSourceObject();
			List<BPDataConsumer<?>> pipes = new ArrayList<BPDataConsumer<?>>(m_pipes);
			int l = pipes.size();
			if (l > 0)
			{
				BPDataConsumer<?> p0 = pipes.get(0);
				if (l > 0)
				{
					BPDataConsumer<?> cp = p0;
					boolean passable = true;
					boolean hasend = false;
					for (int i = 1; i < l; i++)
					{
						BPDataConsumer<?> p = pipes.get(i);
						if (cp.isTransformer())
						{
							((BPTransformer<?>) cp).setOutput(p);
						}
						else if (i < l - 1)
						{
							passable = false;
							break;
						}
						cp = p;
					}
					if (pipes.get(l - 1).isEndpoint())
						hasend = true;
					if (!passable)
					{
						UIStd.info_small("Pipe impassable");
						return;
					}
					if (!hasend)
					{
						UIStd.info_small("No endpoint");
						return;
					}
				}
				try
				{
					p0.runSegment(() -> ((BPDataConsumer) p0).accept(source));
				}
				catch (Exception e2)
				{
					UIStd.err(e2);
				}
			}
		}

		protected void onAddTransformer(ActionEvent e)
		{
			List<BPTransformerFactory> facs = BPTransformerManager.getTransformerFacs(null);
			BPTransformerFactory fac = UIStd.select(facs, UIUtil.wrapBPTitles(BPActionConstCommon.TXT_SEL, BPActionConstCommon.TXT_TF), obj -> ((BPTransformerFactory) obj).getName());
			if (fac != null)
			{
				List<String> fts = new ArrayList<String>(fac.getFunctionTypes());
				if (fts.size() > 0)
				{
					String ft = (fts.size() == 1) ? fts.get(0) : UIStd.select(fts, UIUtil.wrapBPTitles(BPActionConstCommon.TXT_SEL, BPActionConstCommon.TXT_FUNC), null);
					if (ft != null)
					{
						BPTransformer<?> tf = fac.createTransformer(ft);
						if (tf != null)
						{
							m_pipes.add(tf);
							m_lstpipes.updateUI();
						}
					}
				}
			}
		}

		protected void onAddEndpoint(ActionEvent e)
		{
			ServiceLoader<BPDataEndpointFactory> loader = ClassUtil.getServices(BPDataEndpointFactory.class);
			List<BPDataEndpointFactory> facs = new ArrayList<BPDataEndpointFactory>();
			for (BPDataEndpointFactory fac : loader)
				facs.add(fac);
			BPDataEndpointFactory fac = UIStd.select(facs, UIUtil.wrapBPTitles(BPActionConstCommon.TXT_SEL, BPActionConstCommon.TXT_TF), obj -> ((BPDataEndpointFactory) obj).getName());
			if (fac != null)
			{
				List<String> fts = fac.getSupportedFormats();
				String ft = UIStd.select(fts, UIUtil.wrapBPTitles(BPActionConstCommon.TXT_SEL, BPActionConstCommon.TXT_FORMAT), null);
				if (ft != null)
				{
					BPDataConsumer<?> dc = fac.create(ft);
					if (dc != null)
					{
						m_pipes.add(dc);
						m_lstpipes.updateUI();
					}
				}
			}
		}

		protected void onMoveDown(ActionEvent e)
		{
			moveItem(1);
		}

		protected void onMoveUp(ActionEvent e)
		{
			moveItem(-1);
		}

		protected void moveItem(int delta)
		{
			int l = m_pipes.size();

			int si = m_lstpipes.getSelectedIndex();
			if (si < 0)
				return;
			si = si + delta;
			if (si < 0 || si >= l)
				return;
			BPDataConsumer<?> c = m_lstpipes.getSelectedValue();
			m_pipes.remove(c);
			m_pipes.add(si, c);
			m_lstpipes.setSelectedIndex(si);
			m_lstpipes.updateUI();
		}

		protected void onConfigConsumer(ActionEvent e)
		{
			BPDataConsumer<?> c = m_lstpipes.getSelectedValue();
			if (c == null)
				return;
			BPSetting setting = c.getSetting();
			if (setting != null)
			{
				BPDialogSetting dlg = new BPDialogSetting();
				dlg.setSetting(setting);
				dlg.setVisible(true);
				setting = dlg.getResult();
				if (setting != null)
					c.setSetting(setting);
			}
		}
	}
}