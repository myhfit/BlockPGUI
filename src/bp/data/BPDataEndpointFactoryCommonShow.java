package bp.data;

import java.util.List;

import bp.data.BPDataConsumer.BPDataConsumerCollector;
import bp.format.BPFormat;
import bp.format.BPFormatFeature;
import bp.format.BPFormatManager;
import bp.format.BPFormatText;
import bp.format.BPFormatUnknown;
import bp.ui.util.UIStd;
import bp.util.ObjUtil;

public class BPDataEndpointFactoryCommonShow implements BPDataEndpointFactory
{
	public String getName()
	{
		return "Show Common Data";
	}

	@SuppressWarnings("unchecked")
	public <D> BPDataConsumer<D> create(String formatname)
	{
		return (BPDataConsumer<D>) new BPDataConsumerCommonShow();
	}

	public List<String> getSupportedFormats()
	{
		List<String> rc = ObjUtil.makeList(BPFormatText.FORMAT_TEXT, BPFormatUnknown.FORMAT_NA);
		List<BPFormat> fs = BPFormatManager.getFormatsByFeature(BPFormatFeature.IMAGE);
		for (BPFormat f : fs)
			rc.add(f.getName());
		return rc;
	}

	public static class BPDataConsumerCommonShow extends BPDataConsumerCollector<Object>
	{
		public void finish()
		{
			super.finish();
			if (m_datas != null)
			{
				if (m_datas.size() == 1)
					UIStd.showStructuredCommonDatas(m_datas.size() == 1 ? m_datas.get(0) : m_datas, false);
			}
		}

		public String getInfo()
		{
			return "Show Common Data";
		}

		public boolean isEndpoint()
		{
			return true;
		}
	}
}