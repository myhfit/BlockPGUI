package bp.ui.table;

import bp.locale.BPLocaleConstCC;
import bp.locale.BPLocaleConstCoreDict;
import bp.locale.BPLocaleHelpers;
import bp.task.BPTask;

public class BPTableFuncsTask extends BPTableFuncsBase<BPTask<?>>
{
	public BPTableFuncsTask()
	{
		m_collabels = new String[] { BPLocaleConstCC.NAME.text(), BPLocaleConstCC.STATUS.text(), BPLocaleConstCC.PROGRESS.text() };
		m_colnames = new String[] { "Name", "Status", "Progress" };
		m_cols = new Class<?>[] { String.class, String.class, Float.class };
	}

	public String[] getColumnNames()
	{
		return m_colnames;
	}

	public Class<?>[] getColumnClasses()
	{
		return m_cols;
	}

	public Object getValue(BPTask<?> task, int row, int col)
	{
		Object rc = null;
		switch (col)
		{
			case 0:
			{
				return task.toString();
			}
			case 1:
			{
				return BPLocaleHelpers.translate(BPLocaleConstCoreDict.S, nvl(task.getStatus()), "TASK_STATUS_");
			}
			case 2:
			{
				return task.getProgress();
			}
		}
		return rc;
	}
}
