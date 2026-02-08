package bp.ui.actions;

import bp.locale.BPLocaleConst;
import bp.locale.BPLocaleVerb;

public interface BPActionConst extends BPLocaleConst
{
	public final static int VERB_NAME = 0;
	public final static int VERB_TOOLTIP = 0x10000000;
	public final static int VERB_VICON = 0x20000000;
	public final static int VERB_ACCKEY = 0x30000000;
	public final static int VERB_MNEKEY = 0x40000000;

	default int nameK()
	{
		return ordinal() | VERB_NAME;
	}

	default int tooltipK()
	{
		return ordinal() | VERB_TOOLTIP;
	}

	default int viconK()
	{
		return ordinal() | VERB_VICON;
	}

	default int acckeyK()
	{
		return ordinal() | VERB_ACCKEY;
	}

	default int mnukeyK()
	{
		return ordinal() | VERB_MNEKEY;
	}

	public enum BPActionVerb implements BPLocaleVerb
	{
		NAME(VERB_NAME), TOOLTIP(VERB_TOOLTIP), VICON(VERB_VICON), ACCKEY(VERB_ACCKEY), MNEKEY(VERB_MNEKEY);

		protected int value;

		BPActionVerb(int v)
		{
			value = v;
		}

		public int getValue()
		{
			return value;
		}
	}
}
