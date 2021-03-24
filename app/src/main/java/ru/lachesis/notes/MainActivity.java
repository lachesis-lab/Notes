package ru.lachesis.notes;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final String ARG_NOTE_ID = "ru.lachesis.notes.note_id";
    public static final String ARG_NOTE = "ru.lachesis.notes.note";
    public static int mNoteId = -1;
    public static List<Note> mNotesList = new ArrayList<>();
    public static Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null)
            initNotesList();
        if (mNoteId !=-1)
            currentNote = mNotesList.get(mNoteId);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navView = findViewById(R.id.nav_view);

        navView.setNavigationItemSelectedListener((item) -> {
/*
            if (item.getItemId() == R.id.settings) {
                Snackbar.make(drawer, "Настройки", BaseTransientBottomBar.LENGTH_SHORT);
                showToast(item);
            } else if (item.getItemId() == R.id.save) {
                Snackbar.make(drawer, "Сохранение", BaseTransientBottomBar.LENGTH_SHORT);
                showToast(item);
            }
*/
            Snackbar.make(drawer, getText(item.getItemId()), BaseTransientBottomBar.LENGTH_SHORT);
            showToast(item);

            drawer.closeDrawer(GravityCompat.START);
            return true;

        });


//        if (savedInstanceState !=null)
//            mNoteId = savedInstanceState.getInt(ARG_NOTE_ID,-1);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            NotesListFragment notesList = NotesListFragment.newInstance();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.notes_list_fragment, notesList);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//            if (mNoteId != -1)
//                transaction.addToBackStack(null);
            transaction.commit();
        }

        if (savedInstanceState != null) {
            mNoteId = savedInstanceState.getInt(ARG_NOTE_ID, -1);
            if (mNoteId != -1 && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                showFragmentCommonMode(mNoteId);
            } else showFragmentSeparatedMode(mNoteId);
        }
    }


    private void showFragmentCommonMode(int n) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.notes_list_fragment, NotesListFragment.newInstance());
        if (n != -1) {
            if (fragmentManager.findFragmentById(R.id.note_container)!=null && fragmentManager.findFragmentById(R.id.note_container).getClass().getSimpleName().equals("EditFragment"))
                transaction.replace(R.id.note_container, EditFragment.newInstance(n));
            else transaction.replace(R.id.note_container, NoteFragment.newInstance(n));
            //           transaction.addToBackStack(null);
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (n != -1)
            transaction.addToBackStack(null);
        transaction.commit();

    }


    private void showFragmentSeparatedMode(int n) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (n == -1)
            transaction.replace(R.id.notes_list_fragment, NotesListFragment.newInstance());
        else {
            if (fragmentManager.findFragmentById(R.id.notes_list_fragment)!=null && fragmentManager.findFragmentById(R.id.note_container).getClass().getSimpleName().equals("EditFragment"))
                transaction.replace(R.id.notes_list_fragment, EditFragment.newInstance(n));
            else transaction.replace(R.id.notes_list_fragment, NoteFragment.newInstance(n));
//            transaction.addToBackStack(null);
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (n != -1)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager fm = getSupportFragmentManager();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && mNoteId != -1) {
            mNoteId = -1;
            return;
        }
        while (fm.getBackStackEntryCount() > 0) {
            if (fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName() == null) {
                getSupportFragmentManager().popBackStackImmediate();
            }
        }
        mNoteId = -1;
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_NOTE_ID, mNoteId);
        outState.putParcelable(ARG_NOTE, currentNote);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.actionbar_item_edit) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                transaction.replace(R.id.note_container, EditFragment.newInstance(mNoteId));
            else
                transaction.replace(R.id.notes_list_fragment, EditFragment.newInstance(mNoteId));
            transaction.addToBackStack(null);
            transaction.commit();
        } else
            showToast(item);

        return true;
    }

    private void showToast(MenuItem item) {
        Toast toast = Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG);
        toast.show();
    }

    private void initNotesList() {
        NoteDataSource dataSource = new NoteDataSourceImpl(this);
        MainActivity.mNotesList = dataSource.getNoteData();
    }


}