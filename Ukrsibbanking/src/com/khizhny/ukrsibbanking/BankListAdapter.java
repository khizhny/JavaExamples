package com.khizhny.ukrsibbanking;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
public class BankListAdapter extends ArrayAdapter<cBanks> {

    private final Context context;
    private final List<cBanks> bankList;
    
	
	public BankListAdapter(Context context, List<cBanks> bankList) {
		super(context, R.layout.activity_bank_list, bankList);
		this.context = context;
		this.bankList = bankList;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = vi.inflate(R.layout.activity_bank_list, parent, false);
            //RadioButton RadioButtonView = (RadioButton)rowView.findViewById(R.id.active);
		}

		TextView bankNameView = (TextView) rowView.findViewById(R.id.bankName);
        bankNameView.setText(bankList.get(position).getName());
        
        TextView bankPhoneView = (TextView) rowView.findViewById(R.id.bankPhone);
        bankPhoneView.setText(bankList.get(position).getPhone());
        
        RadioButton RadioButtonView = (RadioButton) rowView.findViewById(R.id.active);
       // RadioButtonView.setActivated(bankList.get(position).isActive());
        RadioButtonView.setChecked(bankList.get(position).isActive());
        RadioButtonView.setFocusable(false);
        RadioButtonView.setClickable(false);
        RadioButtonView.setTag(position);
        return rowView;
	}
	
}