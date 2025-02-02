package bp.data;

import java.awt.Window;
import java.util.List;

import bp.config.BPConfigSimple;
import bp.data.BPDataConsumer.BPDataConsumerByteArrayCollector;
import bp.data.BPDataConsumer.BPDataConsumerTextCollector;
import bp.format.BPFormatText;
import bp.format.BPFormatUnknown;
import bp.res.BPResource;
import bp.res.BPResourceFactory;
import bp.res.BPResourceIO;
import bp.ui.util.CommonUIOperations;
import bp.util.ObjUtil;

public class BPDataEndpointFactorySaveResource implements BPDataEndpointFactory
{
	public String getName()
	{
		return "Save Resource...";
	}

	@SuppressWarnings("unchecked")
	public <D> BPDataConsumer<D> create(String formatname)
	{
		if (BPFormatText.FORMAT_TEXT.equals(formatname))
			return (BPDataConsumer<D>) new BPDataConsumerSaveTextResource();
		else if (BPFormatUnknown.FORMAT_NA.equals(formatname))
			return (BPDataConsumer<D>) new BPDataConsumerSaveRawResource();
		return null;
	}

	public List<String> getSupportedFormats()
	{
		return ObjUtil.makeList(BPFormatText.FORMAT_TEXT, BPFormatUnknown.FORMAT_NA);
	}

	public static class BPDataConsumerSaveTextResource extends BPDataConsumerTextCollector
	{
		public void finish()
		{
			super.finish();
			BPResource res = CommonUIOperations.selectResource(Window.getWindows()[0]);
			if (res != null && (res.isIO() || res.isFactory()))
			{
				if (res.isFactory())
					res = ((BPResourceFactory) res).makeResource(BPConfigSimple.fromData(ObjUtil.makeMap("format", BPFormatText.FORMAT_TEXT)));
				BPDataConsumerResourceWriter dcrw = new BPDataConsumerResourceWriter((BPResourceIO) res);
				dcrw.runSegment(() -> dcrw.accept(m_text));
			}
		}

		public String getInfo()
		{
			return "Save to Resource(Text)";
		}
	}

	public static class BPDataConsumerSaveRawResource extends BPDataConsumerByteArrayCollector
	{
		public void finish()
		{
			super.finish();
			BPResource res = CommonUIOperations.selectResource(Window.getWindows()[0]);
			if (res != null && (res.isIO() || res.isFactory()))
			{
				if (res.isFactory())
					res = ((BPResourceFactory) res).makeResource(BPConfigSimple.fromData(ObjUtil.makeMap("format", BPFormatUnknown.FORMAT_NA)));
				BPDataConsumerResourceWriter dcrw = new BPDataConsumerResourceWriter((BPResourceIO) res);
				dcrw.runSegment(() -> dcrw.accept(m_bs));
			}
		}

		public String getInfo()
		{
			return "Save to Resource(byte[])";
		}
	}
}
