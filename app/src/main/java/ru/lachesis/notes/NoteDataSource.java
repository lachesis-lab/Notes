package ru.lachesis.notes;

import android.content.Context;

import java.util.List;

public interface NoteDataSource {
    List<Note> getNoteData();
    Note getItemAt(int noteId);
    int getItemCounts();
}
