package ru.lachesis.notes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class NotesListFragment extends Fragment {

    private static List<Note> mNotesList = new LinkedList<>();
    private NoteDataSource mDataSource;
    private RecyclerView mRecyclerView;
    private ViewHolderAdapter mViewHolderAdapter;
    private int mLastSelectedPosition = -1;

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
        setHasOptionsMenu(true);
    }

    //    private void initNotesList(String assetPath) { mNotesList = getNotes(assetPath); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_notes_list, container, false);
        mRecyclerView.setHasFixedSize(true);

        DividerItemDecoration decorator = new DividerItemDecoration(requireActivity(),
                LinearLayoutManager.VERTICAL);

        decorator.setDrawable(getResources().getDrawable(R.drawable.decoration));
        mRecyclerView.addItemDecoration(decorator);


        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mDataSource = NoteDataSourceImpl.getInstance(requireActivity().getAssets());
        NotesListFragment.mNotesList = mDataSource.getNoteData();

        mViewHolderAdapter = new ViewHolderAdapter(this,
                NoteDataSourceImpl.getInstance(requireActivity().getAssets()));
        mViewHolderAdapter.setOnClickListener((v, position) -> {
            MainActivity.mNotePos = position;
            if (Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) {
                showFragmentSeparatedMode(position);
            } else showFragmentCommonMode(position);

        });

        mRecyclerView.setAdapter(mViewHolderAdapter);
        return mRecyclerView;
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

/*
    private void initNotesList() {
        NoteDataSource dataSource = NoteDataSourceImpl.getInstance(requireActivity().getAssets());
        NotesListFragment.mNotesList = dataSource.getNoteData();
    }
*/
/*
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = requireActivity().getMenuInflater();
        menuInflater.inflate(R.menu.item_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_menu_edit) {
            if (mLastSelectedPosition != -1) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.notes_list_fragment,
                        EditFragment.newInstance(mLastSelectedPosition));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        } else if (item.getItemId() == R.id.item_menu_remove) {
            if (mLastSelectedPosition != -1) {
                mDataSource.remove(mLastSelectedPosition);
                mViewHolderAdapter.notifyItemRemoved(mLastSelectedPosition);
            }
        } else {
            return super.onContextItemSelected(item);
        }
        return true;
    }
*/

    void setLastSelectedPosition(int lastSelectedPosition) {
        mLastSelectedPosition = lastSelectedPosition;
    }
}