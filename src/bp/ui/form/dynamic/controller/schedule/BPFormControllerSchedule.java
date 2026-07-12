package bp.ui.form.dynamic.controller.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import bp.schedule.BPScheduleTargetFactory;
import bp.ui.form.dynamic.BPFormContext;
import bp.ui.form.dynamic.controller.BPFormController;
import bp.ui.form.dynamic.controller.BPFormControllerCommon;
import bp.util.ClassUtil;

public class BPFormControllerSchedule extends BPFormControllerCommon implements BPFormController
{
	public List<?> listData(String key, BPFormContext context)
	{
		switch (key)
		{
			case "targetfac":
			{
				List<String> facnames = new ArrayList<String>();
				ServiceLoader<BPScheduleTargetFactory> facs = ClassUtil.getServices(BPScheduleTargetFactory.class);
				for (BPScheduleTargetFactory fac : facs)
					facnames.add(fac.getName());
				return facnames;
			}
		}
		return null;
	}
}
