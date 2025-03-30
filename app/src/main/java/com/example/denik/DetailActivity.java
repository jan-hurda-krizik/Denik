package com.example.denik;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_TEXT = "extra_note_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView textViewDetail = findViewById(R.id.textViewDetail);

        // Přečteme text z Intentu
        String noteText = getIntent().getStringExtra(EXTRA_NOTE_TEXT);
        if (noteText != null) {
            textViewDetail.setText(noteText);
        }
    }
}
