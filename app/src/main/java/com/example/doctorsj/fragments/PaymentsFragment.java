package com.example.doctorsj.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.doctorsj.Models.Payments;
import com.example.doctorsj.R;
import com.example.doctorsj.adapters.PaymentsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class PaymentsFragment extends Fragment {

    RecyclerView payments_list;
    BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_payments, container, false);

        bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav);
        bottomNavigationView.getMenu().getItem(3).setChecked(true);

        payments_list = (RecyclerView) view.findViewById(R.id.payments_list);
        payments_list.setLayoutManager(new LinearLayoutManager(getContext()));
        payments_list.setHasFixedSize(true);
        getAllPayments();
        return view;
    }

    public void getAllPayments() {

        FirebaseFirestore.getInstance().collection("Doctors")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Payments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            return;
                        }

                        if (!value.isEmpty()) {

                            List<Payments> paymentsList = value.toObjects(Payments.class);

                            PaymentsAdapter paymentsAdapter = new PaymentsAdapter(getContext() , paymentsList);
                            payments_list.setAdapter(paymentsAdapter);
                            paymentsAdapter.notifyDataSetChanged();

                        }

                    }
                });
    }
}