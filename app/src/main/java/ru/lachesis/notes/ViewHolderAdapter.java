package ru.lachesis.notes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewHolderAdapter extends RecyclerView.Adapter<ViewHolderAdapter.ViewHolder> {

    private List<Note> mNoteList;
    private final LayoutInflater mInflater;
    private NoteDataSource mDataSource;
    private OnClickListener mOnClickListener;
    private final NotesListFragment mFragment;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.viewholder_notes_list,parent,false);
        Log.e("CREATE_VH_NOTES",String.valueOf(mNoteList.size()));
        return new ViewHolder(view);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public ViewHolderAdapter(NotesListFragment fragment) {
  //      mDataSource = dataSource;
        mFragment = fragment;
        mInflater = fragment.getLayoutInflater();
        setHasStableIds(true);
    }
    public void updateList(List<Note> list) {
        mNoteList = list;
        this.notifyDataSetChanged();
        Log.e("UPDATE_NOTES",String.valueOf(mNoteList.size()));
    }


    public void setDataSource(NoteDataSource dataSource){
        mDataSource = dataSource;
        mNoteList = mDataSource.getNoteData();
        this.notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Note noteCard = mDataSource.getItemAt(position);
        holder.populate(mFragment, noteCard);
        Log.e("NOTES_COUNT1", String.valueOf(mNoteList.size()));
        holder.itemView.setOnClickListener((v) -> {
            if (mOnClickListener != null) {
                mOnClickListener.onItemClick(v, position);
            }
        });

    }
    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.clear(mFragment);

    }
    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    public interface OnClickListener {
        void onItemClick(View v,int position);
    }

    @Override
    public long getItemId(int position) {
        return mDataSource.getItemAt(position).getNoteId();
//        return super.getItemId(position);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private static final AtomicInteger COUNTER = new AtomicInteger();
        final int mNotePos;
        final TextView mNoteName, mNoteDate, mNoteText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mNotePos = COUNTER.incrementAndGet();
            mNoteName = itemView.findViewById(R.id.note_name);
            mNoteText = itemView.findViewById(R.id.note_text);
            mNoteDate = itemView.findViewById(R.id.note_date);
        }

        public void populate(NotesListFragment fragment, Note noteCard) {
            mNoteName.setText(noteCard.getNoteName());
            mNoteDate.setText(noteCard.getStringNoteDate());
            itemView.setOnLongClickListener((v) -> {
                fragment.setLastSelectedPosition(getLayoutPosition());
                return false;
            });
            fragment.registerForContextMenu(itemView);
        }

        public void clear(Fragment fragment) {
            itemView.setOnLongClickListener(null);
            fragment.unregisterForContextMenu(itemView);
        }

    }

}
