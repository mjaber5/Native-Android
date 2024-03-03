package com.example.finallabtareqalmahameed;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class RUD extends AppCompatActivity {
    EditText etRetID;
    EditText tvName, tvAddress, tvEmail, MobileNo, gender; // تعريف MobileNo كـ EditText
    FirebaseFirestore db;
    ImageView imageView;
    FirebaseStorage storage; // تعريف متغير storage للوصول إلى Firebase Storage

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rud);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance(); // تهيئة Firebase Storage
        etRetID = findViewById(R.id.etRetID);
        tvName = findViewById(R.id.etname);
        tvAddress = findViewById(R.id.etAddress);
        tvEmail = findViewById(R.id.etEmail);
        MobileNo = findViewById(R.id.MobileNo); // تعريف MobileNo كـ EditText
        gender = findViewById(R.id.gender);
        imageView = findViewById(R.id.imageView);

        createNotificationChannel(); // استدعاء دالة إنشاء قناة الإشعار في onCreate
    }

    public void updateDoc(View view) {
        final String ID = etRetID.getText().toString();
        String name = tvName.getText().toString();
        String address = tvAddress.getText().toString();
        String email = tvEmail.getText().toString();
        String mobileNo = MobileNo.getText().toString();
        String Gender = gender.getText().toString();

        Map<String, Object> updates = new HashMap<>();
        updates.put("Name", name);
        updates.put("Address", address);
        updates.put("Email", email);
        updates.put("mobileNo", mobileNo);
        updates.put("gender", Gender);

        db.collection("Employee").document(ID)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RUD.this, "تم تحديث الوثيقة بنجاح", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RUD.this, "حدث خطأ أثناء تحديث الوثيقة", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void deleteDoc(View view) {
        final String ID = etRetID.getText().toString();
        DocumentReference docRef = db.collection("Employee").document(ID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("Name");
                        sendNotification(name + " تم حذفها");
                    }
                }
            }
        });

        db.collection("Employee")
                .document(ID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RUD.this, "تم حذف الوثيقة بنجاح!", Toast.LENGTH_LONG).show();
                        addNotification(); // استدعاء دالة الإشعار في حالة نجاح التحديث

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "حدث خطأ أثناء حذف الوثيقة", e);
                    }
                });
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, RUD.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("إشعار")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            CharSequence name = getString(R.string.project_id);
            String description = getString(R.string.project_id);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void addNotification() {
        Intent intent = new Intent(this, notefcation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("your information")
                .setContentText("Delete successfully ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    public void retrieveDDoc(View view) {
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child("image/Avatar.png");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(RUD.this, "Image retrieval failed.", Toast.LENGTH_SHORT).show();
            }
        });
        String ID = etRetID.getText().toString();
        DocumentReference docRef = db.collection("Employee").document(ID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        tvName.setText("" + document.get("Name"));
                        tvAddress.setText("" + document.get("Address"));
                        tvEmail.setText("" + document.get("Email"));
                        MobileNo.setText("" + document.get("mobileNo")); // استخدام mobileNo كـ EditText
                        gender.setText("" + document.get("gender"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with", task.getException());
                }
            }
        });
    }
}
