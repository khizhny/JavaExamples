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

public class TransanctionListAdapter extends ArrayAdapter<Transaction> {

    private final Context context;
    private final List<Transaction> smsList;
	private boolean hideCurrency;
	private boolean inverseRate;
	
	public TransanctionListAdapter(Context context, List<Transaction> smsList, boolean hideCurrency,boolean inverseRate) {
		super(context, R.layout.activity_main_list_row, smsList);
		this.context = context;
		this.smsList = smsList;
		this.hideCurrency=hideCurrency;
		this.inverseRate=inverseRate;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.activity_main_list_row, parent, false);
		}
		TextView smsTextView;		
        
		smsTextView = (TextView) rowView.findViewById(R.id.smsBody);
        smsTextView.setText(smsList.get(position).getBody());
        if (smsList.get(position).getBody().equals("<No messages about transaction(s)>"))
        {
        	smsTextView.setBackgroundColor(Color.parseColor("#F7C1EF")); // pink
        }else{
        	smsTextView.setBackgroundColor(0);
        }
        
        TextView accountBeforeView = (TextView) rowView.findViewById(R.id.accountBefore);
        if (smsList.get(position).hasAccountStateBefore){
        	accountBeforeView.setText(smsList.get(position).getAccountStateBeforeAsString(hideCurrency));
        } else {
        	accountBeforeView.setText("");
        }
        TextView dateView = (TextView) rowView.findViewById(R.id.transanction_date);
        if (smsList.get(position).hasTransanctionDate){        	
        	dateView.setText(smsList.get(position).getTransanctionDateAsString("dd.MM.yyyy"));
        }else {
        	dateView.setText("");
        }
        TextView accountAfterView = (TextView) rowView.findViewById(R.id.accountAfter);
        if (smsList.get(position).hasAccountStateAfter){
	        accountAfterView.setText(smsList.get(position).getAccountStateAfterAsString(hideCurrency));
        }else {
        	accountAfterView.setText("");
        }
        
        TextView accountDifferenceView = (TextView) rowView.findViewById(R.id.accountDifference);
        if (smsList.get(position).hasAccountDifference){        	
        	accountDifferenceView.setText(smsList.get(position).getAccountDifferenceAsString(hideCurrency,inverseRate));
	        switch (smsList.get(position).getAccountDifference().signum()) {
	        	case -1:
	        		accountDifferenceView.setTextColor(Color.RED);     
	        		break;
	        	case 0:
	        		accountDifferenceView.setTextColor(Color.GRAY);
	        		break;
	        	case 1:
	            	accountDifferenceView.setTextColor(Color.GREEN);        	
	        }        
        } else {
        	accountDifferenceView.setText("");
        }
        
        ImageView iconView = (ImageView) rowView.findViewById(R.id.transanctionIcon);
        iconView.setImageResource(smsList.get(position).transanctionType);
        return rowView;
	}
	
}
