package ru.lachesis.notes;

import android.content.Context;

import java.util.List;

public interface NoteDataSource {

    interface NoteDataSourceResponse {
        void initialized(NoteDataSource dataSource);
    }
    interface NoteDataSourceListener {
        void onItemAdded(int id);
        void onItemRemoved(int id);
        void onItemUpdated(int id);
        void onDataSetChanged();
    }

    void addNoteDataSourceListener(NoteDataSourceListener listener);
    void removeNoteDataSourceListener(NoteDataSourceListener listener);

    List<Note> getNoteData();
    Note getItemAt(int noteId);
    Note getItemById(int noteId);
    int getItemCounts();

    void remove(int mLastSelectedPosition);
    void add(Note note);
    void clear();

    int getNewId();

    void update(Note mCurrentNote);
}
