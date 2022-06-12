package com.example.doctorsj;

import static android.app.Notification.DEFAULT_VIBRATE;
import static androidx.core.app.NotificationCompat.DEFAULT_SOUND;
import static com.example.doctorsj.DoctorsJ.CHANNEL_ID;
import static com.google.firebase.firestore.DocumentChange.Type.MODIFIED;
import static com.google.firebase.firestore.DocumentChange.Type.REMOVED;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.doctorsj.Models.Patients;
import com.example.doctorsj.activities.MapsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientsService extends Service {

    private static final String TAG = PatientsService.class.getSimpleName();

    Notification notification;
    public static boolean isRunning;
    ListenerRegistration listenerRegistration;
    NotificationManager notificationManager;

    public PatientsService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(PatientsService.this , MapsActivity.class);
        intent.putExtra("changetoggle" , "changetoggle");
        PendingIntent pendingIntent = PendingIntent.getActivity(PatientsService.this , 0 , intent , 0);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

            notification = new Notification.Builder(PatientsService.this , CHANNEL_ID)
                    .setContentTitle("Service is On")
                    .setContentText("Tap to change")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build();

            startForeground(3 , notification);

        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        isRunning = true;


      listenerRegistration =   FirebaseFirestore.getInstance()
                .collection("Doctors")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Patients")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            return;
                        }

                        for (DocumentChange documentChange : value.getDocumentChanges()) {

                            switch (documentChange.getType()) {

                                case ADDED:

                                    String date = new SimpleDateFormat("dd/mm/yyyy").format(new Date());
                                    Log.d(TAG, "onEvent: " + date);


                                    Intent intent1 = new Intent(PatientsService.this , MapsActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("source" , "request");
                                    intent1.putExtras(bundle);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(PatientsService.this , 0 , intent1 , 0);


                                    DocumentSnapshot documentSnapshot = documentChange.getDocument();


                                        notification = new NotificationCompat.Builder(PatientsService.this , CHANNEL_ID)
                                                .setContentTitle("New Patient Request")
                                                .setContentText("Tap to view")
                                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                .setContentIntent(pendingIntent)
                                                .setAutoCancel(false)
                                                .build();



                                    notificationManager.notify(2 , notification);

                                    break;


                                case MODIFIED:

                                    Log.d(TAG, "onEvent: " + documentChange.getDocument());

                                    break;
                                case REMOVED:

                                    Log.d(TAG, "onEvent: " + documentChange.getDocument());
                                    break;

                                    }


                            }
                        }






                });

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        listenerRegistration.remove();

    }
}