package com.example.denik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_NOTE = 1001;
    private Button buttonAddNote;
    private RecyclerView recyclerViewNotes;
    private NotesAdapter notesAdapter;
    private ArrayList<String> notesList; // Jednoduché ukládání poznámek v paměti

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Hlavní layout

        // Iniciální propojení s prvky z XML
        buttonAddNote = findViewById(R.id.buttonAddNote);
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);

        // Vytvoření seznamu a adaptéru
        notesList = new ArrayList<>();
        notesAdapter = new NotesAdapter(notesList);

        // Nastavení RecyclerView
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotes.setAdapter(notesAdapter);

        // Tlačítko pro přidání nové poznámky
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                // startActivityForResult je starší API, ale pořád funkční
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
                    // Přidáme text do seznamu a aktualizujeme adaptér
                    notesList.add(newNote);
                    notesAdapter.notifyItemInserted(notesList.size() - 1);
                }
            }
        }
    }
}
