package ru.lachesis.notes;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NoteDataSourceImpl implements NoteDataSource {

//    private final Context mContext;
    private List<Note> mNotes = new ArrayList<>();
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
        mNotes = fillNoteData(manager);
    }
    @Override
    public List<Note> getNoteData() {
        return mNotes;
    }

    private List<Note> fillNoteData(AssetManager manager) {
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
        return mNotes;
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


    @Override
    public Note getItemAt(int noteId) {
        return mNotes.get(noteId);
    }

    @Override
    public int getItemCounts() {
        return mNotes.size();
    }
}
