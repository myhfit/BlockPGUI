package bp.ui.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import bp.res.BPResource;
import bp.ui.actions.BPFileActions;
import bp.ui.scomp.BPTable;

public class BPTableFuncsResource extends BPTableFuncsBase<BPResource>
{
	protected BPFileActions m_acts = new BPFileActions();
	protected int m_channelid;

	public BPTableFuncsResource()
	{
		m_colnames = new String[] { "Name", "Type" };
		m_cols = new Class<?>[] { String.class, String.class };
	}

	public void setChannelID(int channelid)
	{
		m_channelid = channelid;
	}

	public Object getValue(BPResource res, int row, int col)
	{
		switch (col)
		{
			case 0:
				return res.getName();
			case 1:
				return res.getResType();
		}
		return "";
	}

	public List<Action> getActions(BPTable<BPResource> table, List<BPResource> datas, int[] rows, int r, int c)
	{
		List<Action> rc = new ArrayList<Action>();
		rc.add(m_acts.getPropertyAction(datas.toArray(new BPResource[datas.size()]), m_channelid));
		return rc;
	}
}