package bp.schedule;

import java.util.Map;

import bp.ui.util.UIStd;
import bp.util.ObjUtil;
import bp.util.ScriptUtil;

public class BPScheduleTargetRemind implements BPScheduleTarget
{
	protected volatile String m_title;
	protected volatile String m_content;

	public void setup(String title, String content)
	{
		m_title = title;
		m_content = content;
	}

	public void accept(Long t, BPScheduleTargetParams ps)
	{
		String content = m_content;
		if (content == null && ps != null && ps.schedule != null)
		{
			content = "Remind:" + ps.schedule.getName();
		}
		else
		{
			if (ps.datas != null)
				content = ScriptUtil.transContent(content, ObjUtil.makeMap("datas", ps.datas));
		}
		UIStd.info(m_title, content);
	}

	public final static class BPScheduleTargetFactoryRemind implements BPScheduleTargetFactory
	{
		public String getName()
		{
			return "Remind";
		}

		public BPScheduleTarget create(Map<String, Object> params)
		{
			BPScheduleTargetRemind target = new BPScheduleTargetRemind();
			String title = null;
			String content = null;
			if (params != null)
			{
				title = (String) params.get("title");
				content = (String) params.get("content");
			}
			target.setup(title, content);
			return target;
		}
	}
}