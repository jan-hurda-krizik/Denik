package com.example.denik.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    long insertNote(Note note);

    @Query("SELECT * FROM notes")
    List<Note> getAllNotes();
}
