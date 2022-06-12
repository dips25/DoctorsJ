package com.example.doctorsj.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doctorsj.Models.Payments;
import com.example.doctorsj.R;

import java.util.List;

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.ViewHolder> {

    Context context;
    List<Payments> paymentsList;
    int resource;


    public PaymentsAdapter(@NonNull Context context , List<Payments> paymentsList) {

        this.context = context;
        this.paymentsList = paymentsList;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_completed , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Payments payments = paymentsList.get(position);

        if (payments != null) {

            holder.payment_name.setText(payments.getName());
            holder.payment_date.setText(payments.getDate());
            holder.payment_amount.setText("Rs. " + payments.getAmount());
        }

    }

    @Override
    public int getItemCount() {
        return paymentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView payment_name;
        TextView payment_amount;
        TextView payment_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            payment_name = (TextView) itemView.findViewById(R.id.payment_name);
            payment_amount = (TextView) itemView.findViewById(R.id.payment_amount);
            payment_date = (TextView) itemView.findViewById(R.id.payment_date);
        }
    }
}
