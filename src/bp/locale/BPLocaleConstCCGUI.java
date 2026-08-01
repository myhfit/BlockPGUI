package bp.locale;

//Computer Common Dict GUI
public enum BPLocaleConstCCGUI implements BPLocaleConstDirect
{
	SUCCESS_COPY_FILES("Success Copy Files"),
	EXISTS_CONFIRM_SUFFIX(" Exists, Confirm overwrite"),
	ERR_WHEN_GENCOPYLIST("Error when generate copy filelist"),
	STATES,
	;

	public final static String PACK_COMPUTER_COMMONGUI = "c_cgui";

	private String m_value;

	public String getPackName()
	{
		return PACK_COMPUTER_COMMONGUI;
	}

	private BPLocaleConstCCGUI()
	{
	}

	private BPLocaleConstCCGUI(String v)
	{
		m_value = v;
	}

	public String getValue(int flag)
	{
		return m_value == null ? getNormalName() : m_value;
	}
}
