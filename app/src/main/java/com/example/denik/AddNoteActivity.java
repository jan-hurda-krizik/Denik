package com.example.denik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class AddNoteActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CAMERA = 2001;

    private EditText editTextNote;
    private Button buttonCamera;
    private ImageView imageViewPhoto;
    private Button buttonSave;

    private Bitmap capturedImage = null; // Sem uložíme mini-náhled

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextNote = findViewById(R.id.editTextNote);
        buttonCamera = findViewById(R.id.buttonCamera);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        buttonSave = findViewById(R.id.buttonSave);

        // Kliknutí na "Vyfotit" → spustíme fotoaparát
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
                }
            }
        });

        // Kliknutí na "Uložit"
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noteText = editTextNote.getText().toString().trim();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("new_note", noteText);

                // Pokud fotka existuje, pošleme jen boolean příznak
                boolean hasPhoto = (capturedImage != null);
                resultIntent.putExtra("has_photo", hasPhoto);

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    // Vrátí se mini-náhled z fotoaparátu
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    capturedImage = photo;
                    imageViewPhoto.setImageBitmap(photo);
                }
            }
        }
    }
}
