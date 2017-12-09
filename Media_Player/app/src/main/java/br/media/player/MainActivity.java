package br.media.player;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    private MediaPlayer mp;
    private ListView listView;
    private int currentPosition;

    private MusicCursorAdapter cursorAdapter;
    private Cursor cursor;

    private ImageButton btnStop;
    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnRewind;
    private TextView textViewTime;

    private boolean isPlaying = false;
    private boolean isStopped = false;

    private SeekBar seekbarMedia = null;
    private SeekBar seekbarVolume = null;
    private AudioManager audioManager = null;

    private Handler mSeekbarUpdateHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentResolver resolver = getContentResolver();
        String[] data = {MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media._ID};

        this.cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, data, null, null, null);

        initButtons();

        this.mp = new MediaPlayer();

        this.mSeekbarUpdateHandler = new Handler();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentPosition = position;
        cursor.moveToPosition(currentPosition);

        isPlaying();
        playSong();

        btnPlay.setBackgroundResource(R.drawable.btn_pause_normal);
    }


    private void initButtons() {

        textViewTime = (TextView) findViewById(R.id.textViewTime);
        btnStop = (ImageButton) findViewById(R.id.btn_stop);
        btnPlay = (ImageButton) findViewById(R.id.play_btn);
        btnForward = (ImageButton) findViewById(R.id.forward);
        btnRewind = (ImageButton) findViewById(R.id.rewind_btn);
        btnStop.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnRewind.setOnClickListener(this);

        buttonsState(false);

        cursorAdapter = new MusicCursorAdapter(this, cursor);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        seekbarVolume = (SeekBar)findViewById(R.id.seekBarVolume);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        seekbarVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekbarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        seekbarVolume.setOnSeekBarChangeListener(this);

        seekbarMedia = (SeekBar)findViewById(R.id.seekBarMedia);
        seekbarMedia.setOnSeekBarChangeListener(this);
    }


    public void isPlaying() {
        if (mp.isPlaying()) {
            mp.stop();
            mp.reset();
        }
    }

    public void playSong() {
        String idSong = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
        Uri playableUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, idSong);
        mp = MediaPlayer.create(this, playableUri);
        seekbarMedia.setMax(mp.getDuration());
        mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar,100);
        mp.start();
        buttonsState(true);
    }

    public void performItemClick(int cPosition) {
        listView.performItemClick(
                listView.getAdapter().getView(cPosition, null, null),
                currentPosition,
                listView.getAdapter().getItemId(cPosition)
        );
    }

    private Runnable mUpdateSeekbar = new Runnable() {
        @Override
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            textViewTime.setText(Util.timeConvert(currentDuration));
            seekbarMedia.setProgress(mp.getCurrentPosition());
            mSeekbarUpdateHandler.postDelayed(this, 100);

            if(currentDuration == totalDuration-1) {
                btnPlay.setBackgroundResource(R.drawable.btn_play_normal);
            }
        }
    };


    public void buttonsState(boolean state) {
        btnStop.setEnabled(state);
        btnPlay.setEnabled(state);
        btnForward.setEnabled(state);
        btnRewind.setEnabled(state);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.play_btn:
                play();
                break;

            case R.id.btn_stop:
                stop();
                break;

            case R.id.forward:
                forward();

                Toast.makeText(this, currentPosition + " ", Toast.LENGTH_SHORT).show();
                break;

            case R.id.rewind_btn:
                rewind();

                Toast.makeText(this, currentPosition + " ", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void play() {
        if (!mp.isPlaying()) {
            if(isStopped) {
                isStopped = false;
                playSong();
            } else {
                mp.start();
            }
            btnPlay.setBackgroundResource(R.drawable.btn_pause_normal);
        } else {
            mp.pause();
            btnPlay.setBackgroundResource(R.drawable.btn_play_normal);
        }
    }

    public void stop() {
        isPlaying();
        isStopped = true;
        mp.seekTo(0);
        seekbarMedia.setMax(0);
        btnPlay.setBackgroundResource(R.drawable.btn_play_normal);
    }

    public void forward() {
        if (cursor.isLast()) {
            currentPosition = 0;
            cursor.moveToPosition(currentPosition);

        } else {
            currentPosition += 1 ;
            cursor.moveToPosition(currentPosition);
        }

        performItemClick(currentPosition);
        isPlaying();
        playSong();
        btnPlay.setBackgroundResource(R.drawable.btn_pause_normal);
    }

    public void rewind() {

        if (currentPosition == 0) {
            currentPosition = cursor.getCount() - 1;
            cursor.moveToPosition(currentPosition);
        } else {
            currentPosition -= 1;
            cursor.moveToPosition(currentPosition);
        }

        performItemClick(currentPosition);

        isPlaying();
        playSong();
        btnPlay.setBackgroundResource(R.drawable.btn_pause_normal);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp.isPlaying()) {
            isPlaying = true;
            mp.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPlaying) {
            mp.start();
            isPlaying = false;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if(fromUser) {
            if(seekBar.equals(seekbarMedia)){
                mp.seekTo(progress);
                seekbarMedia.setProgress(progress);
            }

            if(seekBar.equals(seekbarVolume)){
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_PLAY_SOUND);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
