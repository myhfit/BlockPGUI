package bp.ui.scomp;

import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.util.ObjUtil;

public class BPBytesCalcPane extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8469496314623086004L;

	protected BPTextField m_txtbin;
	protected BPTextField m_txtuint8;
	protected BPTextField m_txtuint16;
	protected BPTextField m_txtint32;
	protected BPTextField m_txtuint32;
	protected BPTextField m_txtint64;
	protected BPTextField m_txtuint64;
	protected BPTextField m_txtfloat32;
	protected BPTextField m_txtfloat64;
	protected BPTextField m_txtascii;
	protected BPTextField m_txtutf8;

	protected boolean m_bigendian = true;

	public BPBytesCalcPane()
	{
		m_txtbin = new BPTextField();
		m_txtuint8 = new BPTextField();
		m_txtuint16 = new BPTextField();
		m_txtint32 = new BPTextField();
		m_txtuint32 = new BPTextField();
		m_txtint64 = new BPTextField();
		m_txtuint64 = new BPTextField();
		m_txtfloat32 = new BPTextField();
		m_txtfloat64 = new BPTextField();
		m_txtascii = new BPTextField();
		m_txtutf8 = new BPTextField();

		initTFs(m_txtbin, m_txtuint8, m_txtuint16, m_txtint32, m_txtuint32, m_txtint64, m_txtuint64, m_txtfloat32, m_txtfloat64);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(makeLabel("Binary"));
		add(m_txtbin);
		addStrut();
		add(makeLabel("UInt8"));
		add(m_txtuint8);
		addStrut();
		add(makeLabel("UInt16"));
		add(m_txtuint16);
		addStrut();
		add(makeLabel("Int32"));
		add(m_txtint32);
		addStrut();
		add(makeLabel("UInt32"));
		add(m_txtuint32);
		addStrut();
		add(makeLabel("Int64"));
		add(m_txtint64);
		addStrut();
		add(makeLabel("UInt64"));
		add(m_txtuint64);
		addStrut();
		add(makeLabel("Float32"));
		add(m_txtfloat32);
		addStrut();
		add(makeLabel("Float64"));
		add(m_txtfloat64);
		add(Box.createVerticalGlue());
	}

	protected void addStrut()
	{
		add(Box.createVerticalStrut(8));
	}

	protected BPLabel makeLabel(String lbl)
	{
		BPLabel rc = new BPLabel(lbl, BPLabel.LEFT);
		rc.setMonoFont();
		return rc;
	}

	protected void initTFs(BPTextField... tfs)
	{
		for (BPTextField tf : tfs)
		{
			tf.setMonoFont();
			tf.setMaximumSize(new Dimension(4000, 20));
			tf.setBorder(new MatteBorder(1, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
		}
	}

	public void setBytes(byte[] bs)
	{
		if (bs == null)
			return;
		m_txtbin.setText(toBin(bs));
		m_txtuint8.setText(toUInt(bs, 1));
		m_txtuint16.setText(toUInt(bs, 2));
		m_txtuint32.setText(toUInt(bs, 4));
		m_txtuint64.setText(toUInt(bs, 8));
		m_txtint32.setText(toInt(bs, 4));
		m_txtint64.setText(toInt(bs, 8));
		m_txtfloat32.setText(toFloat(bs, 4));
		m_txtfloat64.setText(toFloat(bs, 8));
	}

	protected boolean isLE()
	{
		return m_bigendian;
	}

	public void setLE(boolean flag)
	{
		m_bigendian = flag;
	}

	protected final static Number toFloatNumber(byte[] bs, boolean isle, int len)
	{
		if (bs == null || bs.length < len)
			return null;
		ByteBuffer bb = ByteBuffer.wrap(bs);
		bb.order(isle ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
		if (len == 4)
			return bb.getFloat();
		if (len == 8)
			return bb.getDouble();
		return null;
	}

	protected final static Number toIntNumber(byte[] bs, boolean isle, int len)
	{
		if (bs == null || bs.length < len)
			return null;
		ByteBuffer bb = ByteBuffer.wrap(bs);
		bb.order(isle ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
		if (len == 1)
			return bb.get();
		if (len == 2)
			return bb.getShort();
		if (len == 4)
			return bb.getInt();
		if (len == 8)
			return bb.getLong();
		return null;
	}

	protected String toInt(byte[] bs, int len)
	{
		boolean isle = isLE();
		Number n = toIntNumber(bs, isle, len);
		if (n == null)
			return "";
		return ObjUtil.toString(n);
	}

	protected String toUInt(byte[] bs, int len)
	{
		boolean isle = isLE();
		Number n = toIntNumber(bs, isle, len);
		if (n == null)
			return "";
		if (len == 1)
			return Byte.toUnsignedInt(n.byteValue()) + "";
		else if (len == 2)
			return Short.toUnsignedInt(n.shortValue()) + "";
		else if (len == 4)
			return Integer.toUnsignedString(n.intValue());
		else if (len == 8)
			return Long.toUnsignedString(n.longValue());
		return "";
	}

	protected String toFloat(byte[] bs, int len)
	{
		boolean isle = isLE();
		Number n = toFloatNumber(bs, isle, len);
		if (n == null)
			return "";
		return ObjUtil.toString(n);
	}

	protected String toBin(byte[] bs)
	{
		if (bs == null || bs.length == 0)
			return "";
		byte b = bs[0];
		return Integer.toString(Byte.toUnsignedInt(b), 2);
	}
}
