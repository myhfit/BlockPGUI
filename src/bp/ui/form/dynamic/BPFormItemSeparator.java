package bp.ui.form.dynamic;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.ui.actions.BPAction;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPToolVIconButton;

public class BPFormItemSeparator extends BPFormItemWrapped<BPLabel, JPanel>
{
	public int getLineHeight(BPFormContext context)
	{
		return context.lineheight + 2;
	}

	public Object getComponentValue()
	{
		return null;
	}

	public void setComponentValue(Object v)
	{
	}

	public boolean noLabel()
	{
		return true;
	}

	protected void setupInner(BPFormItemDef itemdef, BPFormContext context)
	{
		BPLabel lbl = new BPLabel(label, SwingConstants.CENTER);
		JPanel cpan = new JPanel();
		FlowLayout fl = new FlowLayout();
		JPanel tpan = new JPanel();

		lbl.setVerticalAlignment(SwingConstants.CENTER);
		lbl.setFont(new Font(UIConfigs.LABEL_FONT_NAME(), Font.PLAIN, UIConfigs.TEXTFIELDFONT_SIZE() + 1));

		fl.setVgap(0);
		fl.setAlignment(FlowLayout.CENTER);
		cpan.setLayout(fl);

		tpan.setMinimumSize(new Dimension(0, context.lineheight + 2));
		tpan.setLayout(new BorderLayout());

		cpan.add(lbl);
		List<Map<String, Object>> acts = itemdef.getParam("actions");
		if (acts != null)
		{
			List<Action> actions = makeActions(acts);
			for (Action act : actions)
			{
				BPToolVIconButton lblact = new BPToolVIconButton(act);
				cpan.add(lblact);
			}
		}

		tpan.add(cpan, BorderLayout.CENTER);
		tpan.setBorder(new MatteBorder(1, 0, 0, 0, UIConfigs.COLOR_TEXTBG()));
		comp = lbl;
		stcomp = tpan;
	}

	protected String translate(BPFormItemDef itemdef, BPFormContext context)
	{
		String rc = super.translate(itemdef, context);
		comp.setText(rc);
		return rc;
	}

	protected List<Action> makeActions(List<Map<String, Object>> actpss)
	{
		List<Action> rc = new ArrayList<Action>();
		for (Map<String, Object> actps : actpss)
		{
			BPAction act = new BPAction((String) actps.get("label"));
			act.setConsumer(this::onAction);
			act.setCommand((String) actps.get("name"));
			rc.add(act);
		}
		return rc;
	}

	protected void onAction(ActionEvent e)
	{
		BPFormContext context = getContext();
		if (context != null)
			context.controller.callAction(e.getActionCommand(), context);
	}
}
