package inforuh.eventfinder.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import inforuh.eventfinder.R;
import inforuh.eventfinder.io.Event;
import inforuh.eventfinder.provider.Contract;
import inforuh.eventfinder.ui.DetailActivity;

/**
 * Created by tioammar
 * on 8/11/15.
 */
public class ExhibitionFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        EventAdapter.Listener {

    private EventAdapter exhibitionAdapter;

    public ExhibitionFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        String[] data = {
                "First Exhibition Event",
                "Second Exhibition Event",
                "Third Exhibition Event",
        };

        List<String> dummyData = new ArrayList<>(Arrays.asList(data));
        exhibitionAdapter =  new EventAdapter(getActivity(), dummyData, this);

        RecyclerView rv = (RecyclerView)view.findViewById(R.id.event_list);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.setAdapter(exhibitionAdapter);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = Contract.EventColumn.CATEGORY + " = ? ";
        String selectionArgs = "exhibition";
        String sortOrder = Contract.EventColumn.START_DATE + " DESC";

        return new CursorLoader(getActivity(),
                Contract.EventColumn.CONTENT_URI,
                Event.PROJECTION,
                selection,
                new String[]{selectionArgs},
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() < 1) return;
        exhibitionAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClickListener(Uri uri) {
        startActivity(new Intent(getActivity(), DetailActivity.class));
    }
}