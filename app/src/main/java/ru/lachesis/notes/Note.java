package ru.lachesis.notes;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note implements Serializable {
    private int mNoteId;
    private String mNoteName;
    private Date mNoteDate;
    private String mNoteText;

    public Note(String noteName, Date noteDate, String noteText) {
        mNoteName = noteName;
        mNoteDate = noteDate;
        mNoteText = noteText;
    }

    public Note(int noteId, String noteName, Date noteDate, String text) {
        mNoteId = noteId;
        mNoteName = noteName;
        mNoteDate = noteDate;
        mNoteText = text;
    }

    public int getNoteId() {
        return mNoteId;
    }

    public String getNoteName() {
        return mNoteName;
    }

    public Date getNoteDate() {
        return mNoteDate;
    }

    public String getNoteText() {
        return mNoteText;
    }

    public String getStringNoteDate() {
        return SimpleDateFormat.getDateInstance().format(mNoteDate);
    }


}
