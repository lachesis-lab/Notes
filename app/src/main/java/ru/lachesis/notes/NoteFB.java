package ru.lachesis.notes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NoteFB extends Note  {

    public static final String FIELD_ID="id";
    public static final String FIELD_NAME="name";
    public static final String FIELD_DATE="date";
    public static final String FIELD_TEXT="text";

    public NoteFB(String fNoteId, int noteId, String noteName, Date noteDate, String text) {
        super(noteId, noteName, noteDate, text);
        setFNoteId(fNoteId);
    }

    public NoteFB(String fNoteId, Map<String,Object> fields){
        this(fNoteId, ((Long) fields.get(FIELD_ID)).intValue(),(String) fields.get(FIELD_NAME),null,(String) fields.get(FIELD_TEXT));
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();//new SimpleDateFormat();
        Date noteDate;
        try {
            noteDate = dateFormat.parse((String) fields.get(FIELD_NAME));
        } catch (ParseException e) {
            noteDate = Calendar.getInstance(Locale.getDefault()).getTime();
        }
        setNoteDate(noteDate);
    }

    public  NoteFB(Note note) {
        this(note.getFNoteId(),note.getNoteId(),note.getNoteName(),note.getNoteDate(),note.getNoteText());
    }

    public Map<String,Object> getFields(){
        HashMap<String,Object> fields = new HashMap<>();
        fields.put(FIELD_ID,getNoteId());
        fields.put(FIELD_NAME,getNoteName());
        fields.put(FIELD_DATE,getNoteDate());
        fields.put(FIELD_TEXT,getNoteText());
        return Collections.unmodifiableMap(fields);

    }

}

