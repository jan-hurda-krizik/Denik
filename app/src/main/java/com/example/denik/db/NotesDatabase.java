package com.example.denik.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {

    // Základ: databáze s entitou Note
    public abstract NoteDao noteDao();

    private static NotesDatabase instance;

    public static synchronized NotesDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            NotesDatabase.class,
                            "notes_database"
                    )
                    .allowMainThreadQueries() // pro ukázku - reálně raději asynchronní
                    .build();
        }
        return instance;
    }
}
