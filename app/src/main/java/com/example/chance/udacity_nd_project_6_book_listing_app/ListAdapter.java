package com.example.chance.udacity_nd_project_6_book_listing_app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by chance on 8/6/17.
 */

public class ListAdapter extends BaseAdapter{

    private ArrayList<Book> list;
    private Context context;
    private ViewHolder viewHolder;

    public ListAdapter(Context context, ArrayList<Book> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Book getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
            Book currentBook = getItem(position);

        viewHolder.bookTitle = (TextView) view.findViewById(R.id.book_title);
        viewHolder.bookTitle.setText(currentBook.getmBookTitle());
        viewHolder.bookAuthor = (TextView) view.findViewById(R.id.book_author);
        viewHolder.bookAuthor.setText(currentBook.getmBookAuthor());

        return view;
    }

    static class ViewHolder {
        private TextView bookTitle;
        private TextView bookAuthor;

        public ViewHolder(@NonNull View view) {
            this.bookTitle = (TextView) view
                    .findViewById(R.id.book_title);
            this.bookAuthor = (TextView) view
                    .findViewById(R.id.book_author);
        }
    }

}
