package com.kumar.user.mediaplayerandroid;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MyCustomAdapter myCustomAdapter;
    SeekBar seekBar1;
    MediaPlayer mediaPlayer;
    int seekValue;
    ListView ls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar1= (SeekBar) findViewById(R.id.seekBar);
        CheckPermissions();
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekValue=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekValue);

            }
        });
         ls= (ListView) findViewById(R.id.lsView);
        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song_info song_info=SongsList.get(position);
                mediaPlayer=new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(song_info.Path);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    seekBar1.setMax(mediaPlayer.getDuration());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        MyThread myThread=new MyThread();
        myThread.start();
    }
    ArrayList<Song_info> SongsList=new ArrayList<Song_info>();

//------------------online media---------------
//   public ArrayList<Song_info> getAllSongs(){
//        SongsList.clear();
//        SongsList.add(new Song_info("http://sound41.songsbling.link/telugu/greeku-veerudu-2013/03%20-%20O%20Naadu%20Washington%20-%20Greeku%20Veerudu%20-%20%5Bsongspk.city%5D.mp3","Nagarjuna","ManiSharma","GreekuVeerudu"));
//        SongsList.add(new Song_info("http://server6.mp3quran.net/thubti/002.mp3","Bakara","bakar","quran"));
//        SongsList.add(new Song_info("http://server6.mp3quran.net/thubti/003.mp3","Al-Imran","bakar","quran"));
//        SongsList.add(new Song_info("http://server6.mp3quran.net/thubti/004.mp3","An-Nisa'","bakar","quran"));
//        SongsList.add(new Song_info("http://server6.mp3quran.net/thubti/005.mp3","Al-Ma'idah","bakar","quran"));
//        SongsList.add(new Song_info("http://server6.mp3quran.net/thubti/006.mp3","Al-An'am","bakar","quran"));
//        SongsList.add(new Song_info("http://server6.mp3quran.net/thubti/007.mp3","Al-A'raf","bakar","quran"));
//        return SongsList;
//    }
//    --------Local Media Start Below Line


    public ArrayList<Song_info> getAllSongs(){
        Uri allsongsuri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection=MediaStore.Audio.Media.IS_MUSIC + "!=0";
        Cursor cursor=getContentResolver().query(allsongsuri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                do{
                    String song_name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String fullpath=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String album_name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String artist_name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    SongsList.add(new Song_info(fullpath,song_name,album_name,artist_name));
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }
        return SongsList;
    }
class MyThread extends Thread{
    public void run(){
    while (true){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer!=null) {
                    seekBar1.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        });
    }
    }
}

    public void buPlay(View view) {
        mediaPlayer.start();
    }

    public void buStop(View view) {
        mediaPlayer.stop();
    }

    public void buPause(View view) {
        mediaPlayer.pause();
    }

    private class MyCustomAdapter extends BaseAdapter{
        ArrayList<Song_info> fullsongpath;

        public MyCustomAdapter(ArrayList<Song_info> fullsongpath) {
            this.fullsongpath = fullsongpath;
        }

        @Override
        public int getCount() {
            return fullsongpath.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater=getLayoutInflater();
            View myView=layoutInflater.inflate(R.layout.list_items,null);
            Song_info s=fullsongpath.get(position);
            TextView tview= (TextView) myView.findViewById(R.id.Sname);
            tview.setText(s.song_name);
            TextView tview1= (TextView) myView.findViewById(R.id.Sdesc);
            tview1.setText(s.artist_name);
            return myView;
        }

    }
    void CheckPermissions(){
        if (Build.VERSION.SDK_INT>=23){
            if (ActivityCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
        LoadSong();
    }
    final private int REQUEST_CODE_ASK_PERMISSIONS=123;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    LoadSong();
                }
                else {
                    Toast.makeText(this, "Denial the Permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
    void LoadSong(){
        myCustomAdapter=new MyCustomAdapter(getAllSongs());
        ls.setAdapter(myCustomAdapter);

    }
}






