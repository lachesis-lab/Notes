package ru.lachesis.notes;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class MainActivity extends AppCompatActivity {

    public static final String ARG_NOTE_ID = "ru.lachesis.notes.note_id";
    public static final String ARG_NOTE = "ru.lachesis.notes.note";
    public static int mNotePos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        boolean isFirstTime = false;
        if (savedInstanceState == null) {
            isFirstTime = true;
        } else
            mNotePos = savedInstanceState.getInt(ARG_NOTE_ID, -1);

        setFragmentPosition(isFirstTime);
    }


    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.item_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_menu_edit) {
            setFragmentPositionByOrientation();
        } else if (item.getItemId() == R.id.item_menu_remove) {
            if (mNotePos != -1) {
                NoteDataSource noteDataSource = NoteDataSourceImpl.getInstance(getAssets());
                noteDataSource.remove(mNotePos);
                FragmentManager fragmentManager = getSupportFragmentManager();
                ViewHolderAdapter viewHolderAdapter = new ViewHolderAdapter((NotesListFragment) fragmentManager.findFragmentById(R.id.notes_list_fragment), noteDataSource);
                viewHolderAdapter.notifyItemRemoved(mNotePos);
            }
        } else {
            return super.onContextItemSelected(item);
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager fm = getSupportFragmentManager();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && mNotePos != -1) {
            mNotePos = -1;
            return;
        }
        while (fm.getBackStackEntryCount() > 0) {
//            if (fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName() == null)
            getSupportFragmentManager().popBackStackImmediate();

        }
        mNotePos = -1;
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_NOTE_ID, mNotePos);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.actionbar_item_edit) {
            setFragmentPositionByOrientation();
        } else if (item.getItemId() == R.id.actionbar_item_clear){

        } else
            showToast(item);

        return true;
    }

    private void setFragmentPosition(Boolean isFirstTime) {
        if (isFirstTime) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            NotesListFragment notesList = NotesListFragment.newInstance();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.notes_list_fragment, notesList);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.commit();
        }
        if (mNotePos != -1 && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            showFragmentCommonMode(mNotePos);
        } else showFragmentSeparatedMode(mNotePos);
    }

    private void setFragmentPositionByOrientation() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            transaction.replace(R.id.note_container, EditFragment.newInstance(mNotePos));
        else
            transaction.replace(R.id.notes_list_fragment, EditFragment.newInstance(mNotePos));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showFragmentCommonMode(int n) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.notes_list_fragment, NotesListFragment.newInstance());
        if (n != -1) {
            if (fragmentManager.findFragmentById(R.id.note_container) != null && fragmentManager.findFragmentById(R.id.note_container).getClass().getSimpleName().equals("EditFragment"))
                transaction.replace(R.id.note_container, EditFragment.newInstance(n));
            else if (fragmentManager.findFragmentById(R.id.notes_list_fragment) != null && fragmentManager.findFragmentById(R.id.notes_list_fragment).getClass().getSimpleName().equals("EditFragment"))
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
            if (fragmentManager.findFragmentById(R.id.notes_list_fragment) != null && fragmentManager.findFragmentById(R.id.notes_list_fragment).getClass().getSimpleName().equals("EditFragment"))
                transaction.replace(R.id.notes_list_fragment, EditFragment.newInstance(n));
            else if (fragmentManager.findFragmentById(R.id.note_container) != null && fragmentManager.findFragmentById(R.id.note_container).getClass().getSimpleName().equals("EditFragment"))
                transaction.replace(R.id.notes_list_fragment, EditFragment.newInstance(n));
            else transaction.replace(R.id.notes_list_fragment, NoteFragment.newInstance(n));
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (n != -1)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showToast(MenuItem item) {
        Toast toast = Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG);
        toast.show();
    }

/*
    @Override
    public void onSendData(Note note) {
        Note mEditableNote = note;
//        getSupportFragmentManager().findFragmentById(R.id.);
    }
*/
}