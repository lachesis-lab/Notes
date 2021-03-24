package ru.lachesis.notes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesListFragment extends Fragment {

    public static List<Note> mNotesList = new ArrayList<>();

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

    //    private void initNotesList(String assetPath) { mNotesList = getNotes(assetPath); }
    private void initNotesList() {
        NoteDataSource dataSource = new NoteDataSourceImpl(requireActivity());
        mNotesList = dataSource.getNoteData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initNotesList();
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_notes_list, container, false);
        recyclerView.setHasFixedSize(true);

        DividerItemDecoration decorator = new DividerItemDecoration(requireActivity(),
                LinearLayoutManager.VERTICAL);

        decorator.setDrawable(getResources().getDrawable(R.drawable.decoration));
        recyclerView.addItemDecoration(decorator);


        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        recyclerView.setLayoutManager(layoutManager);

        ViewHolderAdapter viewHolderAdapter = new ViewHolderAdapter(inflater,
                new NoteDataSourceImpl(requireActivity()));
        viewHolderAdapter.setOnClickListener((v, position) -> {
            MainActivity.mNoteId = position;
            if (Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) {
                showFragmentSeparatedMode(position);
            } else showFragmentCommonMode(position);

        });


        recyclerView.setAdapter(viewHolderAdapter);
        return recyclerView;
    }

    /*

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        String mAssetPath = "Notes";
        initNotesList();
//        initNotesList(mAssetPath);
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
*/

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

}