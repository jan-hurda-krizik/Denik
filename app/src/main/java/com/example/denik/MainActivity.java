package com.example.denik;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_NOTE = 1001;

    private Button buttonAddNote;
    private TextView textViewNotes;
    private ArrayList<String> notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Zobrazí layout

        buttonAddNote = findViewById(R.id.buttonAddNote);
        textViewNotes = findViewById(R.id.textViewNotes);

        notesList = new ArrayList<>();

        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Otevřít AddNoteActivity
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            if (data != null) {
                String newNote = data.getStringExtra("new_note");
                if (newNote != null && !newNote.isEmpty()) {
                    notesList.add(newNote);
                    // Zobrazíme všechny poznámky jako text
                    textViewNotes.setText(notesList.toString());
                }
            }
        }
    }
}
