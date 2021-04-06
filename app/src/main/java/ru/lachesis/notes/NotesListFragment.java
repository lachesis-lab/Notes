package ru.lachesis.notes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class NotesListFragment extends Fragment  {

    private static List<Note> mNotesList = new LinkedList<>();
    private NoteDataSource mDataSource;
    private RecyclerView mRecyclerView;
    private ViewHolderAdapter mViewHolderAdapter;
    private int mLastSelectedPosition = -1;

    private NoteDataSource.NoteDataSourceListener mListener = new NoteDataSource.NoteDataSourceListener() {
        public void onItemAdded(int idx) {
            if (mViewHolderAdapter != null) {
                mViewHolderAdapter.notifyItemInserted(idx);
            }
        }

        @Override
        public void onItemRemoved(int idx) {
            if (mViewHolderAdapter != null) {
                mViewHolderAdapter.notifyItemRemoved(idx);
            }
        }

        @Override
        public void onItemUpdated(int idx) {
            if (mViewHolderAdapter != null) {
                mViewHolderAdapter.notifyItemChanged(idx);
            }
        }

        @Override
        public void onDataSetChanged() {
            if (mViewHolderAdapter != null) {
                mViewHolderAdapter.notifyDataSetChanged();
            }
        }
    };

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



//        mDataSource = NoteDataSourceImpl.getInstance(requireActivity().getAssets());
//        mNotesList = mDataSource.getNoteData();
        mViewHolderAdapter = new ViewHolderAdapter(this);
//                NoteDataSourceImpl.getInstance(requireActivity().getAssets()));
        mViewHolderAdapter.setOnClickListener((v, position) -> {
            MainActivity.mNotePos = position;
            mLastSelectedPosition = position;
            if (Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) {
                showFragmentSeparatedMode(position);
            } else showFragmentCommonMode(position);

        });
//        mNotesList = mDataSource.getNoteData(dataSource -> mViewHolderAdapter.notifyDataSetChanged());

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        mDataSource = NoteDataSourceFBImpl.getInstance(new NoteDataSource.NoteDataSourceResponse() {
            @Override
            public void initialized(NoteDataSource dataSource) {
                mNotesList.clear();
                mNotesList = mDataSource.getNoteData();
                Log.e("INIT_COUNT_NOTES0",String.valueOf(mViewHolderAdapter.getItemCount()));
                mViewHolderAdapter.updateList(mNotesList);
                Log.e("observ_NOTES",String.valueOf(mViewHolderAdapter.hasObservers()));
//                mViewHolderAdapter.setDataSource(mDataSource);
//                mRecyclerView.setLayoutManager(layoutManager);
//                mRecyclerView.setAdapter(mViewHolderAdapter);
                mViewHolderAdapter.notifyDataSetChanged();
                mDataSource.addNoteDataSourceListener(mListener);
            }
        });
//        mNotesList = mDataSource.getNoteData();
//        mNotesList.clear();
//        mNotesList = mDataSource.getNoteData();
//        mDataSource.addNoteDataSourceListener(mListener);
//        mRecyclerView.setLayoutManager(layoutManager);
//        mViewHolderAdapter.updateList(mNotesList);
        mViewHolderAdapter.notifyDataSetChanged();
        mViewHolderAdapter.setDataSource(mDataSource);
        mRecyclerView.setAdapter(mViewHolderAdapter);
        mRecyclerView.setLayoutManager(layoutManager);
        return mRecyclerView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDataSource.removeNoteDataSourceListener(mListener);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.actionbar_item_clear){
            mDataSource.clear();
            mViewHolderAdapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.actionbar_item_add){
            int newId = mDataSource.getNewId();
            mDataSource.add(new Note(newId,"New note "+newId, Calendar.getInstance(Locale.getDefault()).getTime(),"new note text"));
            int pos = mDataSource.getItemCounts()-1;
            mViewHolderAdapter.notifyItemInserted(pos);
            mRecyclerView.scrollToPosition(pos);
        } else
            showToast(item);

        return true;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = requireActivity().getMenuInflater();
        menuInflater.inflate(R.menu.item_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_menu_edit) {
            setFragmentPositionByOrientation();
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

    private void showToast(MenuItem item) {
        Toast toast = Toast.makeText(requireActivity(), item.getTitle(), Toast.LENGTH_LONG);
        toast.show();
    }

    private void setFragmentPositionByOrientation() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            transaction.replace(R.id.note_container, EditFragment.newInstance(mLastSelectedPosition));
        else
            transaction.replace(R.id.notes_list_fragment, EditFragment.newInstance(mLastSelectedPosition));
        transaction.addToBackStack(null);
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

    void setLastSelectedPosition(int lastSelectedPosition) {
        mLastSelectedPosition = lastSelectedPosition;
        MainActivity.mNotePos = lastSelectedPosition;
    }

    void notifyAdapter() {
        mViewHolderAdapter.notifyDataSetChanged();
    }
}