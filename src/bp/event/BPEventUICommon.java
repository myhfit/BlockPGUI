package bp.event;

import bp.res.BPResourceFileSystem;

public class BPEventUICommon
{
	public static class BPEventUIRequestFile extends BPEventUI
	{
		public final static String EVENTKEY_REQUESTFILE = "E_UI_REQ_FILE";

		public final static String REQ_SAVE = "REQ_SAVE";

		public BPResourceFileSystem result;

		public BPEventUIRequestFile(String subkey)
		{
			this.key = EVENTKEY_REQUESTFILE;
			this.subkey = subkey;
			this.datas = new Object[] { null };
		}

		public BPResourceFileSystem getSelectedFile()
		{
			return result;
		}

		public void setSelectedFile(BPResourceFileSystem file)
		{
			result = file;
		}
	}
}
