package com.example.denik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddNoteActivity extends AppCompatActivity {

    private EditText editTextNote;
    private Button buttonSaveNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextNote = findViewById(R.id.editTextNote);
        buttonSaveNote = findViewById(R.id.buttonSaveNote);

        buttonSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noteText = editTextNote.getText().toString().trim();
                // Přenést hodnotu zpět do MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("new_note", noteText);
                setResult(RESULT_OK, resultIntent);
                finish(); // Ukončí aktivitu a vrátí se zpět
            }
        });
    }
}
