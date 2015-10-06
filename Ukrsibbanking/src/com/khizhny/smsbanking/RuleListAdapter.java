package com.khizhny.smsbanking;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
public class RuleListAdapter extends ArrayAdapter<Rule> {

    private final Context context;
    private final List<Rule> ruleList;
    private TextView ruleNameView;
    private ImageView imageView;
	
	public RuleListAdapter(Context context, List<Rule> ruleList) {
		super(context, R.layout.activity_rule_list_row, ruleList);
		this.context = context;
		this.ruleList = ruleList;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = vi.inflate(R.layout.activity_rule_list_row, parent, false);
		}

		ruleNameView = (TextView) rowView.findViewById(R.id.ruleName);
        ruleNameView.setText(ruleList.get(position).getName());
        
        imageView = (ImageView) rowView.findViewById(R.id.rule_image);
        imageView.setImageResource(ruleList.get(position).getRuleTypeDrawable());
      return rowView;
	}
	
}