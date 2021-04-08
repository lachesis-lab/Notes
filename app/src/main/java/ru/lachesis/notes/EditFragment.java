package ru.lachesis.notes;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static android.text.InputType.TYPE_NULL;

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
    private int mNotePos;
    TextInputEditText mDateEdit;
    TextInputEditText mNameEdit;
    TextInputEditText mTextEdit;
    private Note mCurrentNote;
    private static Note mEditableNote;
    private NoteDataSource mDataSource;

    interface OnSaveDataListener {
        void onSaveData();
    }

    private OnSaveDataListener saveDataListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            saveDataListener = (OnSaveDataListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

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
            mNotePos = getArguments().getInt(MainActivity.ARG_NOTE_ID);
        }
        if (savedInstanceState != null)
            mEditableNote = savedInstanceState.getParcelable(MainActivity.ARG_NOTE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_edit, container, false);

//        mDataSource = NoteDataSourceImpl.getInstance(requireActivity().getAssets());
        mDataSource = NoteDataSourceFBImpl.getInstance(null);
        mCurrentNote = mDataSource.getItemAt(mNotePos);
        if (mEditableNote == null || mEditableNote.getNoteId() != mCurrentNote.getNoteId())
            mEditableNote = new Note(mCurrentNote);

        mDateEdit = view.findViewById(R.id.edit_note_date);
        mDateEdit.setInputType(TYPE_NULL);
        mNameEdit = view.findViewById(R.id.edit_note_name);
        mTextEdit = view.findViewById(R.id.edit_note_text);
        MaterialButton saveButton = view.findViewById(R.id.edit_save_button);

        mDateEdit.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {

                calendar.set(year, month, dayOfMonth);
                Date date1 = calendar.getTime();

                String dateStr = SimpleDateFormat.getDateInstance().format(date1);
                mDateEdit.setText(dateStr);
                mEditableNote.setNoteDate(date1);


            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

/*
        mNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEditableNote != null) {
                    mEditableNote.setNoteName(s.toString());
//                    fragmentSendDataListener.onSendData(mEditableNote);
                }
            }
        });

        mTextEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEditableNote != null) {
                    mEditableNote.setNoteText(s.toString());
//                    fragmentSendDataListener.onSendData(mEditableNote);
                }

            }
        });

*/
        saveButton.setOnClickListener(v -> {
            try {
                Date date = SimpleDateFormat.getDateInstance().parse(Objects.requireNonNull(mDateEdit.getText()).toString());
                mCurrentNote.setNoteName(mNameEdit.getText().toString());
                mCurrentNote.setNoteText(mTextEdit.getText().toString());
                mCurrentNote.setNoteDate(date);
                mDataSource.update(mCurrentNote);
                saveDataListener.onSaveData();
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                while (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStackImmediate();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mNotePos == -1) {
            requireActivity().finish();
            return;
        }
        if (mEditableNote != null) {
            mDateEdit.setText(mEditableNote.getStringNoteDate());
            mNameEdit.setText(mEditableNote.getNoteName());
            mTextEdit.setText(mEditableNote.getNoteText());
        } else {
            mDateEdit.setText(mCurrentNote.getStringNoteDate());
            mNameEdit.setText(mCurrentNote.getNoteName());
            mTextEdit.setText(mCurrentNote.getNoteText());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
//        if (mEditableNote!=null)
       outState.putParcelable(MainActivity.ARG_NOTE, mEditableNote);
    }
}