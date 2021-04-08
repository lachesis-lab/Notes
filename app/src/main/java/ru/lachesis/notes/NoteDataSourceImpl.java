package ru.lachesis.notes;


import org.json.JSONException;
import android.content.res.AssetManager;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class NoteDataSourceImpl extends BaseNoteDataSourceImpl {

    //    private final Context mContext;
//    private final LinkedList<Note> mNotes = new LinkedList<>();
    private volatile static NoteDataSourceImpl sInstance;

    public static NoteDataSourceImpl getInstance(AssetManager manager) {
        NoteDataSourceImpl instance = sInstance;
        if (instance == null) {
            synchronized (NoteDataSourceImpl.class) {
                if (sInstance == null) {
                    instance = new NoteDataSourceImpl(manager);
                    sInstance = instance;
                }
            }
        }
        return instance;
    }

//
//    private NoteDataSourceImpl(Context context) {
//        mContext = context;
//        mNotes = fillNoteData();
//    }

    private NoteDataSourceImpl(AssetManager manager) {
        fillNoteData(manager);
    }


/*
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
    public void remove(int mLastSelectedPosition) {
        mNotes.remove(mLastSelectedPosition);
    }

    @Override
    public void add(Note note) {
        mNotes.add(note);
    }

    @Override
    public void clear() {
        mNotes.clear();
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
                mNotes.add(i, note);
                return;
            }
        }
    }

*/
    private void fillNoteData(AssetManager manager) {
        try {
//            AssetManager manager = Objects.requireNonNull(mContext.getAssets());
            String assetPath = "Notes";
            String[] files = manager.list(assetPath);
            for (String file : files) {
                String jsString = readJSONFromAsset(assetPath + "/" + file, manager);
                if (jsString == null) continue;
                JSONObject js = new JSONObject(jsString);
                int noteId = js.getInt("noteId");
                String noteName = js.getString("noteName");
                DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();//new SimpleDateFormat();
                //dateFormat.applyPattern("dd.mm.yyyy");
                Date noteDate;
                try {
                    noteDate = dateFormat.parse(js.getString("noteDate"));
                } catch (ParseException e) {
                    noteDate = Calendar.getInstance(Locale.getDefault()).getTime();
                }

                String noteText = js.getString("noteText");

                mNotes.add(new Note(noteId, noteName, noteDate, noteText));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    private String readJSONFromAsset(String filename, AssetManager manager) {
        String json;
        try {
            InputStream is = manager.open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
