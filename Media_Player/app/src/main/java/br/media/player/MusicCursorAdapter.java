package br.media.player;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


/**
 * Created by pc on 07/10/2017.
 */

public class MusicCursorAdapter extends CursorAdapter{

    private Context context;
    private LayoutInflater layoutInflater;

    public MusicCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = layoutInflater.inflate(R.layout.list_music,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        long songDuration = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));

        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewNameMusic);
        textViewTitle.setText(songName);

        TextView textViewDuration = (TextView) view.findViewById(R.id.textViewDuration);
        textViewDuration.setText(Util.timeConvert(songDuration));
    }
}
