package ru.lachesis.notes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Note implements Parcelable {
    private String mFNoteId;
    private int mNoteId;

    public String getFNoteId() {
        return mFNoteId;
    }

    public void setFNoteId(String fNoteId) {
        mFNoteId = fNoteId;
    }

    private String mNoteName;
    private Date mNoteDate;
    private String mNoteText;

    public Note () { }

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

    public Note(Note note) {
        mNoteId = note.getNoteId();
        mNoteName = note.mNoteName;
        mNoteDate = note.getNoteDate();
        mNoteText = note.getNoteText();
    }

    protected Note(Parcel in) {
        mNoteId = in.readInt();
        mNoteName = in.readString();
        mNoteText = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };


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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mNoteId);
        dest.writeString(mNoteName);
        dest.writeString(mNoteText);
    }

    public void setNoteId(int noteId) {
        mNoteId = noteId;
    }

    public void setNoteDate(Date noteDate) {
        mNoteDate = noteDate;
    }

    public void setNoteName(String noteName) {
        mNoteName = noteName;
    }

    public void setNoteText(String noteText) {
        mNoteText = noteText;
    }
    @Override
    public boolean equals(@Nullable Object obj) {
        boolean res = super.equals(obj);
        if (res) return res;
        if (obj != null) {
            Note otherNote = (Note) obj;
            if (otherNote.getNoteId() == mNoteId && otherNote.getNoteName().equals(mNoteName)
                    && otherNote.getNoteDate() == mNoteDate && otherNote.getNoteText().equals(mNoteText))
                return true;
        }
        return res;
    }
}
