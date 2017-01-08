package com.jcimadras.jcimadras;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcimadras.jcimadras.Extras.Message;

public class Notify extends Service {

    private int c1 = 0,c2 = 0;


    public Notify() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DatabaseReference AnnivRef = FirebaseDatabase.getInstance().getReference("events");
        AnnivRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()) ++c1;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        AnnivRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (c2<=c1) ++c2;
                else {
                    Message.L("count","zdf");
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Notify.this);
                    builder.setSmallIcon(R.drawable.jcilogo);
                    builder.setContentTitle("New events added!!");
                    builder.setVibrate(new long[]{500, 500});
                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    builder.setSound(alarmSound);
                    builder.setLights(Color.BLUE, 3000, 3000);
                    Intent intent = new Intent(Notify.this, LogoDisplay.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(Notify.this, 0, intent, 0);
                    builder.setContentIntent(pendingIntent);
                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_logo));
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(1, builder.build());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return START_STICKY;
    }
}
