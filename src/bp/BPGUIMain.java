package bp;

import java.lang.management.ManagementFactory;
import java.util.List;

import bp.util.CommandLineArgs;
import bp.util.Std;

public class BPGUIMain
{
	public final static void main(String[] args)
	{
		String stdmode = System.getProperty("bp.util.Std");
		if ("debug".equals(stdmode))
		{
			Std.setStdMode(Std.STDMODE_DEBUG);
		}
		else if ("info".equals(stdmode))
		{
			Std.setStdMode(Std.STDMODE_INFO);
		}
		try
		{
			List<String> vmargs = ManagementFactory.getRuntimeMXBean().getInputArguments();
			for (String vmarg : vmargs)
			{
				if (vmarg.startsWith("-agentlib:jdwp"))
				{
					Std.setStdMode(Std.STDMODE_DEBUG);
					break;
				}
			}
		}
		catch (Error e)
		{
		}

		CommandLineArgs cliargs = new CommandLineArgs(args);
		BPGUICore.start(cliargs);
	}
}
