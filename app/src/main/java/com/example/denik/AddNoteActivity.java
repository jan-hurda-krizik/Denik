package com.example.denik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNoteActivity extends AppCompatActivity {

    private EditText editTextNote;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextNote = findViewById(R.id.editTextNote);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noteText = editTextNote.getText().toString().trim();
                // Pošleme text zpět do MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("new_note", noteText);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
