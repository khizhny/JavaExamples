package com.khizhny.ukrsibbanking;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.khizhny.ukrsibbanking.R;
import com.khizhny.ukrsibbanking.cSMS;


public class ListAdapter extends ArrayAdapter<cSMS> {

	// List context
    private final Context context;
    // List values
    private final List<cSMS> smsList;
	
	public ListAdapter(Context context, List<cSMS> smsList) {
		super(context, R.layout.activity_main, smsList);
		this.context = context;
		this.smsList = smsList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
        View rowView = inflater.inflate(R.layout.activity_main, parent, false);
       // Here will be sorting by date
        
        TextView smsTextView = (TextView) rowView.findViewById(R.id.smsText);
        smsTextView.setText(smsList.get(position).getBody());
        
        if (smsList.get(position).hasAccountStateBefore){
        	TextView accountBeforeView = (TextView) rowView.findViewById(R.id.accountBefore);
        	accountBeforeView.setText(smsList.get(position).getAccountStateBefore().toString());
        }
        if (smsList.get(position).hasAccountStateAfter){
        TextView accountAfterView = (TextView) rowView.findViewById(R.id.accountAfter);
        accountAfterView.setText(smsList.get(position).getAccountStateAfter().toString());
        }
        if (smsList.get(position).hasAccountDifference){
        TextView accountAfterView = (TextView) rowView.findViewById(R.id.accountDifference);
        accountAfterView.setText(smsList.get(position).getAccountDifferencePlus().toString());
        }   
        return rowView;
	}
	
}
