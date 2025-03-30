package com.example.denik.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String text;      // Samotný text poznámky
    private boolean hasPhoto; // Informace, zda obsahuje fotku

    // Konstruktor (bez id, to generuje Room automaticky)
    public Note(String text, boolean hasPhoto) {
        this.text = text;
        this.hasPhoto = hasPhoto;
    }

    // Gettery a settery
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isHasPhoto() { return hasPhoto; }
    public void setHasPhoto(boolean hasPhoto) { this.hasPhoto = hasPhoto; }
}
