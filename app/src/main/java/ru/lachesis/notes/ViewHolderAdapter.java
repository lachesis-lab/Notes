package ru.lachesis.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewHolderAdapter extends RecyclerView.Adapter<ViewHolderAdapter.ViewHolder> {

    private final List<Note> mNoteList;
    private final LayoutInflater mInflater;
    private final NoteDataSource mDataSource;
    private OnClickListener mOnClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.viewholder_notes_list,parent,false);
        return new ViewHolder(view);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public ViewHolderAdapter(LayoutInflater inflater, NoteDataSource dataSource) {
        mDataSource = dataSource;
        mNoteList = mDataSource.getNoteData();
        mInflater = inflater;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Note noteCard = mDataSource.getItemAt(position);
        holder.populate(noteCard);

        holder.itemView.setOnClickListener((v) -> {
            if (mOnClickListener != null) {
                mOnClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    public interface OnClickListener {
        void onItemClick(View v,int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private static final AtomicInteger COUNTER = new AtomicInteger();
        final int mNoteId;
        final TextView mNoteName, mNoteDate, mNoteText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mNoteId = COUNTER.incrementAndGet();
            mNoteName = itemView.findViewById(R.id.note_name);
            mNoteText = itemView.findViewById(R.id.note_text);
            mNoteDate = itemView.findViewById(R.id.note_date);
        }

        public void populate(Note noteCard) {
            mNoteName.setText(noteCard.getNoteName());
            mNoteDate.setText(noteCard.getStringNoteDate());
        }
    }
}
