package ru.lachesis.notes;

import android.app.DatePickerDialog;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static List<Note> mNotesList = new ArrayList<>();

    // TODO: Rename and change types of parameters

    public NotesListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static NotesListFragment newInstance() {
        return new NotesListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
    }

    private void initNotesList(String assetPath) {
        mNotesList = getNotes(assetPath);
    }

    private ArrayList<Note> getNotes(String assetPath) {
        ArrayList<Note> notes = new ArrayList<>();
        try {
            AssetManager manager = Objects.requireNonNull(getActivity()).getAssets();
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

                notes.add(new Note(noteId, noteName, noteDate, noteText));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return notes;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String mAssetPath = "Notes";
        initNotesList(mAssetPath);
        container = requireActivity().findViewById(R.id.notes_list_fragment);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_notes_list, container, false);
        for (int i = 0; i < mNotesList.size(); i++) {
            LinearLayout llNote = new LinearLayout(getContext());
            TextView textViewDate = new TextView(getContext());
//            String date = SimpleDateFormat.getDateInstance().format(mNotesList.get(i).getNoteDate());
            String date = mNotesList.get(i).getStringNoteDate();
            textViewDate.setText(date);
            textViewDate.setTextSize(16);
            textViewDate.setPadding(5, 2, 5, 0);
            llNote.addView(textViewDate);

            llNote.setOrientation(LinearLayout.VERTICAL);
            TextView textViewName = new TextView(getContext());
            textViewName.setText(mNotesList.get(i).getNoteName());
            textViewName.setPadding(5, 2, 5, 0);
            textViewName.setTextSize(24);
            llNote.addView(textViewName);

            final int n = i;
            textViewDate.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {

                    calendar.set(year, month, dayOfMonth);
                    Date date1 = calendar.getTime();

                    String dateStr = SimpleDateFormat.getDateInstance().format(date1);
                    textViewDate.setText(dateStr);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            });

            textViewName.setOnClickListener(v -> {

                MainActivity.mNoteId = n;
                if (Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) {
                    showFragmentSeparatedMode(n);
                } else showFragmentCommonMode(n);
//                requireActivity().recreate();
//                MainActivity.mNoteIdPrev = n;
            });

            view.addView(llNote);
        }
        return view;
    }

    private void showFragmentCommonMode(int n) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.notes_list_fragment, this);
        if (n != -1) {
            transaction.replace(R.id.note_container, NoteFragment.newInstance(n));
            transaction.addToBackStack(null);
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }


    public void showFragmentSeparatedMode(int n) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (n == -1)
            transaction.replace(R.id.notes_list_fragment, this);
        else {
            transaction.replace(R.id.notes_list_fragment, NoteFragment.newInstance(n));
            transaction.addToBackStack(null);
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }


/*
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if  (savedInstanceState != null) {
            MainActivity.mNoteId = savedInstanceState.getInt(MainActivity.ARG_NOTE_ID, -1);
            if (MainActivity.mNoteId != -1 && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                showFragmentCommonMode(MainActivity.mNoteId);
            else showFragmentSeparatedMode(MainActivity.mNoteId);
        }
    }
*/

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//    }
//

}