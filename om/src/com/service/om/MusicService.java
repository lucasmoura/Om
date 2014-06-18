package com.service.om;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;

import com.model.om.Row;
import com.model.om.Song;

import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;

public class MusicService extends Service
	implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
	MediaPlayer.OnCompletionListener
{
	
	public class MusicBinder extends Binder
	{
		public MusicService getService()
		{
			return MusicService.this;
		}
	}
	
	private MediaPlayer player;
	private ArrayList<Row> songs;
	private int songPosition;
	private IBinder musicBind;
	
	public void onCreate()
	{
		super.onCreate();
		this.songPosition = 0;
		this.player = new MediaPlayer();
		this.musicBind = new MusicBinder();
		
		initMusicPlayer();
		
	}
	
	public void initMusicPlayer()
	{
		player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		//Media Player instance is prepared
		player.setOnPreparedListener(this);
		//Song has completed playback
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
	}
	
	public void setSongList(ArrayList<Row> songs)
	{
		this.songs = songs;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return this.musicBind;
	}
	
	@Override
	public boolean onUnbind(Intent intent)
	{
	  this.player.stop();
	  this.player.release();
	  return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp)
	{
		mp.start();
	}
	
	public void playSong()
	{
		this.player.reset();
		
		//get song
		Row item = songs.get(this.songPosition);
		Song playSong = null;
		
		if(item instanceof Song)
			playSong = (Song) item;
		else
			return;
		
		
		//get id
		long currSong = playSong.getId();
		//set uri
		Uri trackUri = ContentUris.withAppendedId(
		  android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		  currSong);
		
		try
		{
			player.setDataSource(getApplicationContext(), trackUri);
		}
		catch(Exception e)
		{
			Log.e("MUSIC SERVICE", "Error setting data source", e);
		}
		
		player.prepareAsync();
		
	}
	
	public void setSong(int songIndex)
	{
		  this.songPosition=songIndex;
	}

}
