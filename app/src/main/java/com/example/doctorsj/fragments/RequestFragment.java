package com.example.doctorsj.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.doctorsj.Models.Patients;
import com.example.doctorsj.R;
import com.example.doctorsj.activities.DatePickerActivity;
import com.example.doctorsj.adapters.RequestAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequestFragment extends Fragment {

    private static final String TAG = RequestFragment.class.getSimpleName();

    RequestAdapter requestAdapter;
    RecyclerView request_recycler;
    Button change_date_button;
    TextView date_text;
    DatePicker datePicker;
    Button button_set_date;
    String dateFormat;
    String timeFormat;
    String timestamp;
    BottomNavigationView bottomNavigationView;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_request, container, false);

        bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_request);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllRequests();
            }
        });

        change_date_button = (Button) view.findViewById(R.id.change_date_button);
        date_text = (TextView) view.findViewById(R.id.date_text);

        change_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                builder.setView(R.layout.activity_date_picker);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                datePicker = (DatePicker) alertDialog.findViewById(R.id.doc_date_picker);

                button_set_date = (Button) alertDialog.findViewById(R.id.button_set_date);

                button_set_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth() + 1;
                        int year = datePicker.getYear();
                        String date = day + "/" + month + "/" + year;

                        date_text.setText(date);

                        FirebaseFirestore.getInstance().collection("Doctors")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("Patients")
                                .whereEqualTo("date" , date)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                        List<Patients> patientsList = queryDocumentSnapshots.toObjects(Patients.class);
                                        requestAdapter = new RequestAdapter(getContext() , (ArrayList<Patients>) patientsList);
                                        request_recycler.setAdapter(requestAdapter);
                                        requestAdapter.notifyDataSetChanged();

                                    }
                                });




                    }
                });

            }

        });


        request_recycler = (RecyclerView) view.findViewById(R.id.requestrecycler);
        request_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        request_recycler.setHasFixedSize(true);

        getAllRequests();
        return view;
    }

    public void getAllRequests() {

        FirebaseFirestore.getInstance().collection("Doctors")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Patients")
                .whereEqualTo("status" , "accepted")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            Log.d(TAG, "onEvent: " + error.getMessage());

                            return;
                        }

                        List<Patients> patientsList = value.toObjects(Patients.class);
                        requestAdapter = new RequestAdapter(getContext() , (ArrayList<Patients>) patientsList);
                        request_recycler.setAdapter(requestAdapter);
                        requestAdapter.notifyDataSetChanged();

                    }
                });
    }
}