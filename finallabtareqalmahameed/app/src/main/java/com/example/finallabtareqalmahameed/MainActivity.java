package com.example.finallabtareqalmahameed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText etEmpID, etEmpName, etEmpEmail, etMobileNo;
    private EditText editTextDate;

    FirebaseFirestore db;
    FirebaseStorage storage;
    ImageView imageView;
    Spinner spinner;
    RadioGroup radioGroup;
    RadioButton radioButton;
    private static final int REQUEST_IMAGE_PICK = 1;

    private static final String TAG = "MainActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextDate = findViewById(R.id.editTextDate);
        Button buttonEditDate = findViewById(R.id.button4);
        etEmpID = findViewById(R.id.etID);
        etEmpName = findViewById(R.id.etName);
        etEmpEmail = findViewById(R.id.etEmail);
        etMobileNo = findViewById(R.id.etMobileNo);
        db = FirebaseFirestore.getInstance();
        spinner = findViewById(R.id.spinner);
        storage = FirebaseStorage.getInstance();
        imageView = findViewById(R.id.imageView);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            }
        });

        buttonEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        // Spinner setup...
        String[] options = {"Amman", "Karak", "Zarqa"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = options[position];
                // Use selectedOption as needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Called when no option is selected
            }
        });

        // RadioGroup setup...
        radioGroup = findViewById(R.id.radioGroup);
        RadioButton maleRadioButton = findViewById(R.id.radioButton);
        RadioButton femaleRadioButton = findViewById(R.id.radioButton2);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton) {
                    // Male radio button selected
                } else if (checkedId == R.id.radioButton2) {
                    // Female radio button selected
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String selectedDate = sdf.format(calendar.getTime());

                editTextDate.setText(selectedDate);
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    public void insertDB(View view) {
        String ID = etEmpID.getText().toString();
        String Name = etEmpName.getText().toString();
        String Email = etEmpEmail.getText().toString();
        String mobileNo = etMobileNo.getText().toString();
        String selectedCity = spinner.getSelectedItem().toString();
        String EditTextDate=editTextDate.getText().toString();

        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String gender = selectedRadioButton.getText().toString();

        Map<String, Object> employee = new HashMap<>();
        employee.put("ID", ID);
        employee.put("Name", Name);
        employee.put("Email", Email);
        employee.put("mobileNo", mobileNo);
        employee.put("Address", selectedCity);
        employee.put("gender", gender);
        employee.put("Date",EditTextDate);

        db.collection("Employee").document(ID)
                .set(employee)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        clearFields();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void clearFields() {
        etEmpID.setText("");
        etEmpName.setText("");
        etEmpEmail.setText("");
        etMobileNo.setText("");
        editTextDate.setText("");
        radioGroup.clearCheck();

    }

    public void next(View view) {
        Intent intent = new Intent(MainActivity.this, RUD.class);
        startActivity(intent);
    }

    public void next_notefi(View view) {
        Intent intent = new Intent(MainActivity.this, notefcation.class);
        startActivity(intent);
    }

    public void set(View view) {
        StorageReference storageRef = storage.getReference();
        StorageReference saveRef = storageRef.child("image/Avatar.png");

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = saveRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle upload failure
                Toast.makeText(MainActivity.this, "Upload failed.", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle upload success
                Toast.makeText(MainActivity.this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
