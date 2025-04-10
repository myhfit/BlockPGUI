package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.data.BPBytesHolder;
import bp.data.BPDataContainerRandomAccess;
import bp.data.BPDataContainerRandomAccessBase.BPBlockCache;
import bp.ui.BPViewer;
import bp.ui.scomp.BPHexPane;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPTextPane;
import bp.util.Std;

public class BPRawEditor extends JPanel implements BPEditor<JPanel>, BPViewer<BPDataContainerRandomAccess>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5762517069495915877L;

	protected boolean m_needsave;
	protected String m_id;
	protected int m_channelid;
	protected BPDataContainerRandomAccess m_con;

	protected BPTextPane m_txtp;
	protected BPHexPane m_hexp;

	protected BiFunction<Long, Integer, byte[]> m_readcb;
	protected BiFunction<Long, Integer, byte[]> m_rawreadcb;
	protected BiConsumer<byte[], Integer> m_previewcb;

	protected BPBlockCache m_cache;

	protected JScrollPane m_scroll;

	public BPRawEditor()
	{
		m_txtp = new BPTextPane();
		m_hexp = new BPHexPane();
		m_scroll = new JScrollPane();
		JPanel panright = new JPanel();
		BPLabel lblrt = new BPLabel("PREVIEW");
		lblrt.setMonoFont();
		lblrt.setForeground(UIConfigs.COLOR_TEXTHALF());
		lblrt.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()), new EmptyBorder(0, 2, 0, 0)));
		panright.setBackground(UIConfigs.COLOR_TEXTBG());
		m_scroll.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));
		m_scroll.setViewportView(m_hexp);
		m_txtp.setPreferredSize(new Dimension(300, 200));
		m_txtp.setBorder(null);
		m_txtp.setEditable(false);

		int fontsize = UIConfigs.EDITORFONT_SIZE();
		Font tfont = new Font(UIConfigs.MONO_FONT_NAME(), Font.PLAIN, fontsize);
		m_hexp.setFont(tfont);
		m_txtp.setFont(tfont);
		lblrt.setFont(tfont);

		setLayout(new BorderLayout());
		panright.setLayout(new BorderLayout());
		panright.add(lblrt, BorderLayout.NORTH);
		panright.add(m_txtp, BorderLayout.CENTER);
		add(m_scroll, BorderLayout.CENTER);
		add(panright, BorderLayout.EAST);
		m_readcb = this::onRead;
		m_rawreadcb = this::onRawRead;
		m_previewcb = this::onPreview;
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
			m_cache = new BPBlockCache(4096, len, m_rawreadcb);
			m_hexp.setup(m_readcb, len, m_previewcb);
			con.close();
		}
	}

	public void setBytes(byte[] bs)
	{
		BPBytesHolder bh = new BPBytesHolder();
		bh.setData(bs);
		bind(bh);
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

	protected byte[] onRawRead(Long pos, Integer len)
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

	protected byte[] onRead(Long pos, Integer len)
	{
		return m_cache.get(pos, len);
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
		m_needsave = false;
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
	}

	public int getChannelID()
	{
		return m_channelid;
	}

	public void setOnDynamicInfo(Consumer<String> info)
	{
	}
}
