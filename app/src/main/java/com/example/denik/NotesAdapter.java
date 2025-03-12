package com.example.denik;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private final List<String> notesList;

    public NotesAdapter(List<String> notesList) {
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Použijeme vestavěný jednoduchý layout pro každou položku
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        String note = notesList.get(position);
        holder.textView.setText(note);
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public NoteViewHolder(@NonNull View itemView) {

