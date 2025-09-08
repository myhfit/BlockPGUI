package bp.ui.parallel;

import bp.event.BPEventUI;

public class BPEventUISyncEditor extends BPEventUI
{
	public final static String EVENTKEY_SYNC_EDITOR = "E_UI_SYNC_EDITOR";
	public final static String SYNC_POS = "syncpos";
	public final static String SYNC_SELECTION = "syncselection";

	public BPEventUISyncEditor(String subkey, Object[] datas)
	{
		this.key = EVENTKEY_SYNC_EDITOR;
		this.subkey = subkey;
		this.datas = datas;
	}

	@SuppressWarnings("unchecked")
	public <T> T getSyncData()
	{
		return (T) datas[2];
	}

	public String getSyncDataType()
	{
		return (String) datas[1];
	}

	public static BPEventUISyncEditor syncPosition(String id, String postype, Object pos)
	{
		return new BPEventUISyncEditor(SYNC_POS, new Object[] { id, postype, pos });
	}

	public static BPEventUISyncEditor syncSelection(String id, String seltype, Object sel)
	{
		return new BPEventUISyncEditor(SYNC_SELECTION, new Object[] { id, seltype, sel });
	}
}
