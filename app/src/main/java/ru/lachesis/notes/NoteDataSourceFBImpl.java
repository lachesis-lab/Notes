package ru.lachesis.notes;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class NoteDataSourceFBImpl extends BaseNoteDataSourceImpl {

    //    private final Context mContext;
    private final static String NOTES_COLLECTION = "ru.lachesis.NotesCollection";
    private static final String TAG = "ru.lachesis.err";
    private final FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private final CollectionReference mCollection = mStore.collection(NOTES_COLLECTION);
//    private final LinkedList<Note> mNotes = new LinkedList<>();
    private volatile static NoteDataSourceFBImpl sInstance;
    private NoteDataSourceResponse mResponse;

    public static NoteDataSourceFBImpl getInstance(@Nullable NoteDataSourceResponse response) {
        NoteDataSourceFBImpl instance = sInstance;

        if (instance == null) {
            synchronized (NoteDataSourceFBImpl.class) {
                if (sInstance == null) {
                    instance = new NoteDataSourceFBImpl(response);
                    sInstance = instance;
                }
            }
        }
        return instance;
    }


    private NoteDataSourceFBImpl(@Nullable NoteDataSourceResponse response) {
        mResponse = response;
        fillNoteData();
    }


/*
    @Override
    public List<Note> getNoteData() {
        return mNotes;
    }
*/

    private void fillNoteData() {
//       mCollection.orderBy("noteDate", Query.Direction.ASCENDING).get().addOnCompleteListener(this::onCompleteRequest)
//                .addOnFailureListener(this::onFailedRequest);
        mCollection.orderBy("date", Query.Direction.ASCENDING).get().addOnCompleteListener(this::onCompleteRequest)
                .addOnFailureListener(this::onFailedRequest);

    }

    private void onCompleteRequest(Task<QuerySnapshot> task){
        if (task.isSuccessful())
            mNotes.clear();
            for (QueryDocumentSnapshot document: task.getResult()) {
                NoteFB noteFB= new NoteFB(document.getId(),document.getData());
                mNotes.add ((Note)noteFB);
            }
        if (mResponse != null)
            mResponse.initialized(this);
            Log.e("NOTES_COUNT", String.valueOf(mNotes.size()));
    }

    private void onFailedRequest(Exception e){
        Log.e (TAG, e.getMessage());
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
        String id = mNotes.get(position).getFNoteId();
        mCollection.document(id).delete();
        super.remove(position);
    }

    @Override
    public void add(Note note) {
        NoteFB noteFB = new NoteFB(note);
        Task<DocumentReference> task= mCollection.add(noteFB.getFields());
        String id = task.getResult().getId();//.addSnapshotListener(t-> noteFB.setFNoteId(t.getId()););
        noteFB.setFNoteId(id);
        super.add(noteFB);

    }

    @Override
    public void clear() {
        String id;
        for (Note note: mNotes){
            id = note.getFNoteId();
            mCollection.document(id).delete();
        }
        super.clear();

    }

/*
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
*/

    @Override
    public void update(@NonNull  Note note) {
        String id;
        for (int i =0; i< mNotes.size(); i++){
            if (mNotes.get(i).getNoteId()==note.getNoteId()) {
                NoteFB noteFB = new NoteFB(note);
                mNotes.set(i, noteFB);
                id = mNotes.get(i).getFNoteId();
                mCollection.document(id).set(noteFB.getFields());
                return;
            }
        }
    }

}
