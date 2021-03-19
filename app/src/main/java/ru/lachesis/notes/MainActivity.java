package ru.lachesis.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static final String ARG_NOTE_ID = "ru.lachesis.notes.note_id";
    public static int mNoteId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState !=null)
            mNoteId = savedInstanceState.getInt(ARG_NOTE_ID,-1);
        setContentView(R.layout.activity_main);
        if (savedInstanceState==null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment notesList = NotesListFragment.newInstance();
            transaction.replace(R.id.notes_list_fragment, notesList);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        if  (savedInstanceState != null) {
            MainActivity.mNoteId = savedInstanceState.getInt(MainActivity.ARG_NOTE_ID, -1);
            if (MainActivity.mNoteId != -1 && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                showFragmentCommonMode(MainActivity.mNoteId);
            else showFragmentSeparatedMode(MainActivity.mNoteId);
        }

    }

    private void showFragmentCommonMode(int n) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.notes_list_fragment, NotesListFragment.newInstance());
        if (n!=-1)
            transaction.replace(R.id.note_container, NoteFragment.newInstance(n));
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();

    }


    private void showFragmentSeparatedMode(int n) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (n==-1)
            transaction.replace(R.id.notes_list_fragment, NotesListFragment.newInstance());
        else
            transaction.replace(R.id.notes_list_fragment, NoteFragment.newInstance(n));
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_NOTE_ID,mNoteId);

    }



}