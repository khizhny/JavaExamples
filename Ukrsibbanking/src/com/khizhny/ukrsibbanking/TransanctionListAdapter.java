package com.khizhny.ukrsibbanking;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TransanctionListAdapter extends ArrayAdapter<cSMS> {

    private final Context context;
    private final List<cSMS> smsList;
	
	public TransanctionListAdapter(Context context, List<cSMS> smsList) {
		super(context, R.layout.activity_main_list_row, smsList);
		this.context = context;
		this.smsList = smsList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.activity_main_list_row, parent, false);
			//rowView.setBackgroundColor(Color.WHITE);
		}

        TextView smsTextView = (TextView) rowView.findViewById(R.id.smsBody);
        smsTextView.setText(smsList.get(position).getBody());
        
        if (smsList.get(position).hasAccountStateBefore){
        	TextView accountBeforeView = (TextView) rowView.findViewById(R.id.accountBefore);
        	accountBeforeView.setText(smsList.get(position).getAccountStateBeforeAsString());
        }
       
        if (smsList.get(position).hasTransanctionDate){
        	TextView dateView = (TextView) rowView.findViewById(R.id.transanction_date);
        	dateView.setText(smsList.get(position).getTransanctionDateAsString("dd.MM.yyyy"));
        }
       
        if (smsList.get(position).hasAccountStateAfter){
	        TextView accountAfterView = (TextView) rowView.findViewById(R.id.accountAfter);
	        accountAfterView.setText(smsList.get(position).getAccountStateAfterAsString());
        }
        
        if (smsList.get(position).hasAccountDifference){
        	TextView accountDifferenceView = (TextView) rowView.findViewById(R.id.accountDifference);
        	accountDifferenceView.setText(smsList.get(position).getAccountDifferenceAsString());
	        switch (smsList.get(position).getAccountDifferencePlus().signum()) {
	        	case -1:
	        		accountDifferenceView.setTextColor(Color.RED);     
	        		break;
	        	case 0:
	        		accountDifferenceView.setTextColor(Color.GRAY);
	        		break;
	        	case 1:
	            	accountDifferenceView.setTextColor(Color.GREEN);        	
	        }        
        }
        
        ImageView iconView = (ImageView) rowView.findViewById(R.id.transanctionIcon);
        iconView.setImageResource(smsList.get(position).transanctionType);
        return rowView;
	}
	
}
