package ru.lachesis.notes;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public abstract class BaseNoteDataSourceImpl implements NoteDataSource {
    protected LinkedList<Note> mNotes = new LinkedList<>();
    private HashSet<NoteDataSourceListener> mListeners = new HashSet<>();

    public void addNoteDataSourceListener(NoteDataSourceListener listener){
        mListeners.add(listener);
    }

    public void removeNoteDataSourceListener(NoteDataSourceListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public List<Note> getNoteData() {
        return mNotes;
    }
    @Override
    public Note getItemAt(int noteId) {
        return mNotes.get(noteId);
    }

    @Override
    public Note getItemById(int noteId) {
        for (Note note : mNotes) {
            if (note.getNoteId()==noteId)
                return note;
        }
        return null;
    }

    @Override
    public int getItemCounts() {
        return mNotes.size();
    }

    @Override
    public void remove(int position) {
        mNotes.remove(position);
        for (NoteDataSourceListener listener : mListeners) {
            listener.onItemRemoved(position);
        }
    }

    @Override
    public void add(Note note) {
        mNotes.add(note);
        for (NoteDataSourceListener listener : mListeners) {
            listener.onItemAdded(mNotes.size()-1);
        }    }

    @Override
    public void clear() {
        mNotes.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getNewId() {
        List<Integer> ids = new ArrayList<>();
        if (mNotes.size() == 0)
            return 0;
        else {
            for (Note note : mNotes)
                ids.add(note.getNoteId());
            return Collections.max(ids) + 1;
        }
    }

    @Override
    public void update(Note note) {
        for (int i =0; i< mNotes.size(); i++){
            if (mNotes.get(i).getNoteId()==note.getNoteId()) {
//                mNotes.get(i).setNoteDate(note.getNoteDate());
//                mNotes.get(i).setNoteName(note.getNoteName());
//                mNotes.get(i).setNoteText(note.getNoteText());
                mNotes.set(i, note);
                notifyUpdated(i);
                return;
            }
        }
    }

    protected final void notifyUpdated(int idx) {
        for (NoteDataSourceListener listener : mListeners) {
            listener.onItemUpdated(idx);
        }
    }

    protected final void notifyDataSetChanged() {
        for (NoteDataSourceListener listener : mListeners) {
            Log.e("NOTIFY1_NOTES","onDataSetChanged");
            listener.onDataSetChanged();
        }
    }

}
