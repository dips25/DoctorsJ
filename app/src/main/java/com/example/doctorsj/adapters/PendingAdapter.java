package com.example.doctorsj.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doctorsj.Models.Patients;
import com.example.doctorsj.R;
import com.example.doctorsj.activities.PatMapsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.ViewHolder> {

    private static final String TAG = PendingAdapter.class.getSimpleName();

    Context context;
    ArrayList<Patients> patientsArrayList;

    public PendingAdapter(Context context, ArrayList<Patients> patientsArrayList) {
        this.context = context;
        this.patientsArrayList = patientsArrayList;
    }

    @NonNull
    @Override
    public PendingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_patients , parent , false);
        return new PendingAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PendingAdapter.ViewHolder holder, int position) {

        final Patients patients = patientsArrayList.get(position);

        holder.pat_completed.setVisibility(View.GONE);

        holder.pat_date.setText(patients.getDate());
        holder.pat_name.setText(patients.getName());
        holder.pat_addess.setText(patients.getFulladdress());

        if (patients.getProfilepic() != null) {

            Glide.with(context)
                    .load(patients.getProfilepic())
                    .placeholder(R.drawable.ic_user)
                    .into(holder.item_profileimage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context , PatMapsActivity.class);
                intent.putExtra("id" , patients.getId());
                intent.putExtra("latitude" , patients.getLatitude());
                intent.putExtra("longitude" , patients.getLongitude());
                intent.putExtra("fulladdress" , patients.getFulladdress());
                intent.putExtra("name" , patients.getName());
                intent.putExtra("date" , patients.getDate());
                intent.putExtra("profileimage" , patients.getProfilepic());
                intent.putExtra("time" , patients.getTime());
                intent.putExtra("convertedtime" , patients.getConvertedtime());
                intent.putExtra("status" , patients.getStatus());
                intent.putExtra("timestamp" , patients.getTimestamp());
                context.startActivity(intent);

            }
        });


        holder.pat_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.pat_accept.setVisibility(View.GONE);
                holder.itemView.setVisibility(View.GONE);

               final Map map = new HashMap();

                SharedPreferences sharedPreferences = context.getSharedPreferences("profiledetails" , Context.MODE_PRIVATE);



                map.put("id" , FirebaseAuth.getInstance().getCurrentUser().getUid());
                map.put("name" , sharedPreferences.getString("name" , ""));
                map.put("contact" , sharedPreferences.getString("contact" , ""));
                map.put("profilepic" , sharedPreferences.getString("profilepic" , ""));
                map.put("fulladdress" , sharedPreferences.getString("fulladdress" , ""));
                map.put("status" , "accepted");
                map.put("latitude" , sharedPreferences.getFloat("latitude" , 0));
                map.put("longitude" , sharedPreferences.getFloat("longitude" , 0));
                map.put("date" , patients.getDate());
                map.put("time" , patients.getTime());
                map.put("convertedtime" , patients.getConvertedtime());
                map.put("timestamp" , String.valueOf(System.currentTimeMillis()));



                FirebaseFirestore.getInstance().collection("Doctors")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("Patients")
                        .document(patients.getId())
                        .update("status" , "accepted")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                FirebaseFirestore.getInstance().collection("Patients")
                                        .document(patients.getId())
                                        .collection("Doctors")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .set(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Log.d(TAG, "onFailure: " + e.getMessage());

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailureDoc: " + e.getMessage());

                    }
                });

            }
        });

        holder.pat_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.itemView.setVisibility(View.GONE);

                FirebaseFirestore.getInstance().collection("Doctors")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("Patients")
                        .document(patients.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailureDelDoc: "+  e.getMessage());

                    }
                });

                FirebaseFirestore.getInstance().collection("Patients")
                        .document(patients.getId())
                        .collection("Doctors")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailureDelPat: " + e.getMessage());

                    }
                });

            }
        });


    }

    @Override
    public int getItemCount() {
        return patientsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView pat_name , pat_addess , pat_accept , pat_decline , pat_date , pat_completed;
        CircleImageView item_profileimage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pat_date = (TextView) itemView.findViewById(R.id.pat_date);
            pat_name = (TextView) itemView.findViewById(R.id.pat_name);
            pat_addess = (TextView) itemView.findViewById(R.id.pat_full_address);
            pat_accept = (TextView) itemView.findViewById(R.id.pat_accept);
            pat_decline = (TextView) itemView.findViewById(R.id.pat_decline);
            item_profileimage = (CircleImageView) itemView.findViewById(R.id.item_profileimage);
            pat_completed = (TextView) itemView.findViewById(R.id.pat_completed);


        }
    }
}
