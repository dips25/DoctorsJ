package com.example.doctorsj.fragments;

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

import com.example.doctorsj.Models.Patients;
import com.example.doctorsj.R;
import com.example.doctorsj.adapters.PendingAdapter;
import com.example.doctorsj.adapters.RequestAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PendingFragment extends Fragment {

    private static final String TAG = PendingFragment.class.getSimpleName();

    RecyclerView pending_recycler;
    BottomNavigationView bottomNavigationView;
    SwipeRefreshLayout swipeRefreshLayout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pending, container, false);

        bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_pending);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getAllRequests();

            }
        });

        pending_recycler = (RecyclerView) view.findViewById(R.id.pending_recycler);
        pending_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        pending_recycler.setHasFixedSize(true);

        getAllRequests();

        return view;
    }

    public void getAllRequests() {


        FirebaseFirestore.getInstance().collection("Doctors")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Patients")
                .whereEqualTo("status" , "pending")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            Log.d(TAG, "onEvent: " + error.getMessage());

                            return;
                        }

                        if (!value.isEmpty()) {

                            ArrayList<Patients> patientsArrayList = (ArrayList<Patients>) value.toObjects(Patients.class);
                            PendingAdapter pendingAdapter = new PendingAdapter(getContext() , patientsArrayList);
                            pending_recycler.setAdapter(pendingAdapter);
                            pendingAdapter.notifyDataSetChanged();
                        }



                    }
                });
    }
}