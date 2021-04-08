package ru.lachesis.notes;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class NoteDataSourceFBImpl extends BaseNoteDataSourceImpl {

    //    private final Context mContext;
    private final static String NOTES_COLLECTION = "ru.lachesis.NotesCollection0";
    private static final String TAG = "ru.lachesis.err";
    private final FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private final CollectionReference mCollection = mStore.collection(NOTES_COLLECTION);
    //    private final LinkedList<Note> mNotes = new LinkedList<>();
    private volatile static NoteDataSourceFBImpl sInstance;
    private final NoteDataSourceResponse mResponse;

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

        Query query = mCollection.orderBy("id", Query.Direction.ASCENDING);
//        query.addSnapshotListener(this::snapShotListener);


//        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
//                .setQuery(query, Note.class)
//                .build();
//       mCollection.orderBy("noteDate", Query.Direction.ASCENDING).get().addOnCompleteListener(this::onCompleteRequest)
//                .addOnFailureListener(this::onFailedRequest);
//        mCollection.orderBy("id", Query.Direction.ASCENDING)
                query.get()
                .addOnSuccessListener(this::onSuccessRequest)
                .addOnFailureListener(this::onFailedRequest);

    }

    private void snapShotListener(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
        if (e != null) return;
        List<Note> notes =  queryDocumentSnapshots.toObjects(Note.class);
        mNotes = new LinkedList<>(notes);
        if (mResponse != null)
            mResponse.initialized(this);
        Log.e("NOTES_COUNT_list", String.valueOf(mNotes.size()));
    }

    private void onSuccessRequest(QuerySnapshot queryDocumentSnapshots) {
        mNotes.clear();
        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
        for (DocumentSnapshot document : docs) {
            NoteFB noteFB = new NoteFB(document.getId(), Objects.requireNonNull(document.getData()));
            mNotes.add((Note) noteFB);
        }
        if (mResponse != null)
            mResponse.initialized(this);
        Log.e("NOTES_COUNT", String.valueOf(mNotes.size()));

    }

    private void onCompleteRequest(Task<QuerySnapshot> task) {
        if (task.isSuccessful())
            mNotes.clear();
        for (QueryDocumentSnapshot document : task.getResult()) {
            NoteFB noteFB = new NoteFB(document.getId(), document.getData());
            mNotes.add((Note) noteFB);
        }
        if (mResponse != null)
            mResponse.initialized(NoteDataSourceFBImpl.this);
        Log.e("NOTES_COUNT", String.valueOf(mNotes.size()));
    }

    private void onFailedRequest(Exception e) {
        Log.e(TAG, e.getMessage());
    }


    @Override
    public Note getItemAt(int noteId) {
        return mNotes.get(noteId);
    }


    @Override
    public Note getItemById(int noteId) {
        for (Note note : mNotes) {
            if (note.getNoteId() == noteId)
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
        Map<String, Object> doc = noteFB.getFields();
        DocumentReference refDoc = mCollection.document();
        refDoc.set(doc);
        noteFB.setFNoteId(refDoc.getId());
        super.add(noteFB);
        if (noteFB.getFNoteId() != null)
            Log.e("NEW_NOTE_ID", noteFB.getFNoteId());
        else
            Log.e("NEW_NOTE_ID", "NULL");

    }

    @Override
    public void clear() {
        String id;
        for (Note note : mNotes) {
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
    public void update(@NonNull Note note) {
        String id;
        for (int i = 0; i < mNotes.size(); i++) {
            if (mNotes.get(i).getNoteId() == note.getNoteId()) {
                NoteFB noteFB = new NoteFB(note);
                mNotes.set(i, noteFB);
                id = mNotes.get(i).getFNoteId();
                mCollection.document(id).set(noteFB.getFields());
                return;
            }
        }
    }

}
