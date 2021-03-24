package ru.lachesis.notes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment {

    public NoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NoteFragment.
     */
    public static NoteFragment newInstance(int noteId) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putInt(MainActivity.ARG_NOTE_ID, noteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            MainActivity.mNoteId = getArguments().getInt(MainActivity.ARG_NOTE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (MainActivity.mNoteId == -1) {
            requireActivity().finish();
            return;
        }
        TextView tvNoteText = view.findViewById(R.id.note_text);
        tvNoteText.setText(NotesListFragment.mNotesList.get(MainActivity.mNoteId).getNoteText());

        TextView tvNoteDate = view.findViewById(R.id.note_date);
        tvNoteDate.setText(NotesListFragment.mNotesList.get(MainActivity.mNoteId).getStringNoteDate());

        TextView tvNoteName = view.findViewById(R.id.note_name);
        tvNoteName.setText(NotesListFragment.mNotesList.get(MainActivity.mNoteId).getNoteName());

    }

}