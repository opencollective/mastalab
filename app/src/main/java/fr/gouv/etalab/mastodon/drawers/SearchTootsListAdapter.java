package fr.gouv.etalab.mastodon.drawers;
/* Copyright 2017 Thomas Schneider
 *
 * This file is a part of Mastalab
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * Mastalab is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Mastalab; if not,
 * see <http://www.gnu.org/licenses>. */


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;

import fr.gouv.etalab.mastodon.R;
import fr.gouv.etalab.mastodon.activities.HashTagActivity;
import fr.gouv.etalab.mastodon.helper.Helper;
import fr.gouv.etalab.mastodon.sqlite.SearchDAO;
import fr.gouv.etalab.mastodon.sqlite.Sqlite;

import static fr.gouv.etalab.mastodon.helper.Helper.changeDrawableColor;


/**
 * Created by Thomas on 22/11/2017.
 * Adapter for search
 */
public class SearchTootsListAdapter extends BaseAdapter  {

    private List<String> searches;
    private LayoutInflater layoutInflater;
    private Context context;
    private SearchTootsListAdapter searchTootsListAdapter;
    private RelativeLayout textviewNoAction;

    public SearchTootsListAdapter(Context context, List<String> searches, RelativeLayout textviewNoAction){
        this.searches = searches;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.searchTootsListAdapter = this;
        this.textviewNoAction = textviewNoAction;
    }

    @Override
    public int getCount() {
        return searches.size();
    }

    @Override
    public Object getItem(int position) {
        return searches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final String search = searches.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.drawer_search, parent, false);
            holder = new ViewHolder();
            holder.search_title = convertView.findViewById(R.id.search_keyword);
            holder.search_container = convertView.findViewById(R.id.search_container);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SharedPreferences sharedpreferences = context.getSharedPreferences(Helper.APP_PREFS, android.content.Context.MODE_PRIVATE);
        int theme = sharedpreferences.getInt(Helper.SET_THEME, Helper.THEME_DARK);
        if( theme == Helper.THEME_LIGHT){
            holder.search_container.setBackgroundResource(R.color.mastodonC3__);
            changeDrawableColor(context, R.drawable.ic_keyboard_arrow_right,R.color.black);
        }else if(theme == Helper.THEME_DARK){
            holder.search_container.setBackgroundResource(R.color.mastodonC1_);
            changeDrawableColor(context, R.drawable.ic_keyboard_arrow_right,R.color.dark_text);
        }else if(theme == Helper.THEME_BLACK) {
            holder.search_container.setBackgroundResource(R.color.black_2);
            changeDrawableColor(context, R.drawable.ic_keyboard_arrow_right,R.color.dark_text);
        }
        Drawable next = ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_right);
        holder.search_title.setText(search);
        final float scale = context.getResources().getDisplayMetrics().density;
        assert next != null;
        next.setBounds(0,0,(int) (30  * scale + 0.5f),(int) (30  * scale + 0.5f));
        holder.search_title.setCompoundDrawables(null, null, next, null);

        holder.search_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HashTagActivity.class);
                Bundle b = new Bundle();
                b.putString("tag", search.trim());
                intent.putExtras(b);
                context.startActivity(intent);
            }
        });
        final SQLiteDatabase db = Sqlite.getInstance(context, Sqlite.DB_NAME, null, Sqlite.DB_VERSION).open();

        holder.search_container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.delete) + ": " + search);
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new SearchDAO(context, db).remove(search.trim());
                                searches.remove(search);
                                searchTootsListAdapter.notifyDataSetChanged();
                                if( searches.size() == 0 && textviewNoAction != null && textviewNoAction.getVisibility() == View.GONE)
                                    textviewNoAction.setVisibility(View.VISIBLE);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return false;
            }
        });
        return convertView;
    }


    private class ViewHolder {
        LinearLayout search_container;
        TextView search_title;
    }


}