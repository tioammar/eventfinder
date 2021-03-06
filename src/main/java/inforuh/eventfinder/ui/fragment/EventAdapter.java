/*
 * Copyright 2015 Aditya Amirullah. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package inforuh.eventfinder.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import inforuh.eventfinder.Config;
import inforuh.eventfinder.R;
import inforuh.eventfinder.io.Event;
import inforuh.eventfinder.provider.Contract;

/**
 * Created by tioammar
 * on 8/11/15.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    public interface Listener {
        void onClickListener(Uri uri);
    }

    private Cursor cursor;
    private Context context;
    private Listener listener;
    private View emptyView;

    public EventAdapter(Context context, Listener listener, View view) {
        // constructor
        this.context = context;
        this.listener = listener;
        emptyView = view;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final ImageView eventImage;
        public final TextView eventTitle;
        public final TextView eventDate;
        public final TextView eventCategory;

        public MyViewHolder(View itemView) {
            super(itemView);
            eventImage = (ImageView) itemView.findViewById(R.id.event_image);
            eventCategory = (TextView) itemView.findViewById(R.id.event_category);
            eventTitle = (TextView) itemView.findViewById(R.id.event_title);
            eventDate = (TextView) itemView.findViewById(R.id.event_date);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            cursor.moveToPosition(position);
            final int eventId = cursor.getInt(Event.ID);

            listener.onClickListener(Contract.EventColumn.buildEventUri(
                    Integer.toString(eventId)));
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (viewGroup instanceof RecyclerView) {
            int layoutId = R.layout.main_item;
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
            return new MyViewHolder(view);
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
       if (cursor.moveToPosition(i)) {
           viewHolder.eventCategory.setText(cursor.getString(Event.CATEGORY).toUpperCase());
           viewHolder.eventTitle.setText(cursor.getString(Event.TITLE).toUpperCase());

           String startDate = cursor.getString(Event.START_DATE);
           Date startDateFormat = Config.parseDate(startDate, TimeZone.getDefault());
           Calendar startCal = Calendar.getInstance();
           startCal.setTime(startDateFormat);

           int startMonth = startCal.get(Calendar.MONTH);
           int startDateInWeek = startCal.get(Calendar.DAY_OF_MONTH);
           int startYear = startCal.get(Calendar.YEAR);

           String formattedStartDate;
           if (context.getResources().getConfiguration().locale.getDisplayName()
                   .equals(Locale.US.getDisplayName())) {
               formattedStartDate = Config.formatMonth(context, startMonth) + " " +
                       Config.formatDate(context, startDateInWeek) + ", " + startYear;
           } else {
               formattedStartDate = Config.formatDate(context, startDateInWeek) + " " +
                       Config.formatMonth(context, startMonth) + " " + startYear;
           }
           viewHolder.eventDate.setText(formattedStartDate);

           Glide.with(viewHolder.eventImage.getContext())
                   .load(cursor.getString(Event.IMAGE))
                   .centerCrop()
                   .diskCacheStrategy(DiskCacheStrategy.ALL)
                   .into(viewHolder.eventImage);
           viewHolder.eventImage.setContentDescription(cursor.getString(Event.TITLE));
       }
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public void swapCursor(Cursor c){
        if (c == null) return;
        cursor = c;
        notifyDataSetChanged();
        emptyView.setVisibility(cursor.getCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
