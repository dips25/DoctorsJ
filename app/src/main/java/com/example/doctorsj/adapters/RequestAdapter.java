package com.example.doctorsj.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doctorsj.Models.Patients;
import com.example.doctorsj.R;
import com.example.doctorsj.activities.PatMapsActivity;
import com.example.doctorsj.fragments.RequestFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private static final String TAG = RequestAdapter.class.getSimpleName();

    Context context;
    ArrayList<Patients> patientsArrayList;

    public RequestAdapter (Context context , ArrayList<Patients> patientsArrayList) {

        this.context = context;
        this.patientsArrayList = patientsArrayList;


    }
    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_patients , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {

        final Patients patients = patientsArrayList.get(position);

        holder.pat_date.setText(patients.getDate());
        holder.pat_name.setText(patients.getName());
        holder.pat_addess.setText(patients.getFulladdress());
        holder.pat_accept.setVisibility(View.GONE);
        holder.pat_pending.setVisibility(View.GONE);

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

        holder.pat_completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map map = new HashMap();
                map.put("name" , context.getSharedPreferences("profiledetails" , Context.MODE_PRIVATE).getString("name" , ""));
                map.put("date" , patients.getDate());
                map.put("amount" , 500);

                Map map1 = new HashMap();
                map1.put("name" , patients.getName());
                map1.put("date" , patients.getDate());
                map1.put("amount" , 500);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(R.layout.payment_completed_layout);
                builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        FirebaseFirestore.getInstance().collection("Patients")
                                .document(patients.getId())
                                .collection("Doctors")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .update("status" , "completed")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

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

                           }
                       });

                        FirebaseFirestore.getInstance().collection("Patients")
                                .document(patients.getId())
                                .collection("Payments")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .set(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(context, context.getString(R.string.check_up_completed), Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                        FirebaseFirestore.getInstance().collection("Doctors")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("Payments")
                                .document(patients.getId())
                                .set(map1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        holder.itemView.setVisibility(View.GONE);



                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        }).create().show();
            }
        });





    }

    @Override
    public int getItemCount() {
        return patientsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView pat_name , pat_addess , pat_accept , pat_decline , pat_date , pat_pending , pat_completed;
        CircleImageView item_profileimage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pat_date = (TextView) itemView.findViewById(R.id.pat_date);
            pat_name = (TextView) itemView.findViewById(R.id.pat_name);
            pat_addess = (TextView) itemView.findViewById(R.id.pat_full_address);
            pat_accept = (TextView) itemView.findViewById(R.id.pat_accept);
            pat_decline = (TextView) itemView.findViewById(R.id.pat_decline);
            item_profileimage = (CircleImageView) itemView.findViewById(R.id.item_profileimage);
            pat_pending = (TextView) itemView.findViewById(R.id.pat_pending);
            pat_completed = (TextView) itemView.findViewById(R.id.pat_completed);
        }
    }
}
