package ru.lachesis.notes;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFragment extends Fragment {
/*

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
*/

    // TODO: Rename and change types of parameters
    private int mNoteId;
    TextInputEditText mDateEdit;
    TextInputEditText mNameEdit;
    TextInputEditText mTextEdit;

    public EditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param noteId Parameter 1.
     * @return A new instance of fragment EditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditFragment newInstance(int noteId) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putInt(MainActivity.ARG_NOTE_ID, noteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNoteId = getArguments().getInt(MainActivity.ARG_NOTE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view= (ViewGroup) inflater.inflate(R.layout.fragment_edit, container, false);
        mDateEdit = view.findViewById(R.id.edit_note_date);
        mNameEdit = view.findViewById(R.id.edit_note_name);
        mTextEdit = view.findViewById(R.id.edit_note_text);
        mDateEdit.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {

                calendar.set(year, month, dayOfMonth);
                Date date1 = calendar.getTime();

                String dateStr = SimpleDateFormat.getDateInstance().format(date1);
                mDateEdit.setText(dateStr);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mNoteId ==-1) {
            requireActivity().finish();
            return;
        }

        mDateEdit.setText(NotesListFragment.mNotesList.get(MainActivity.mNoteId).getStringNoteDate());
        mNameEdit.setText(NotesListFragment.mNotesList.get(MainActivity.mNoteId).getNoteName());
        mTextEdit.setText(NotesListFragment.mNotesList.get(MainActivity.mNoteId).getNoteText());

    }
}