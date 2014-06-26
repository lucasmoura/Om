package com.service.om;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;

import com.model.om.Row;
import com.model.om.Section;
import com.model.om.Song;
import com.om.R;
import com.view.om.AllSongsActivity;

import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;

public class MusicService extends Service
	implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener
	, AudioManager.OnAudioFocusChangeListener
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
	private final IBinder musicBind = new MusicBinder();
	private String songTitle;
	private boolean shuffle;
	private boolean paused;
	private boolean repeat;
	private Random rand;
	private AudioManager audioManager;
	private int duration;
	private boolean toSeek;
	private int current;
	
	private static final int NOTIFY_ID = 1;
	
	public void onCreate()
	{
		super.onCreate();
		songPosition = 0;
		duration = 0;
		shuffle = false;
		paused = false;
		repeat = false;
		toSeek = false;
		current = 0;
		rand = new Random();
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		initMusicPlayer();
		
	}
	
	public void initMusicPlayer()
	{
		player = new MediaPlayer();
		player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		//Media Player instance is prepared
		player.setOnPreparedListener(this);
		//Song has completed playback
		player.setOnErrorListener(this);
	}
	
	public MediaPlayer getMediaPlayer()
	{
		return player;
	}
	
	public void setSongList(ArrayList<Row> songs)
	{
		this.songs = songs;
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return this.musicBind;
	}
	
	@Override
	public boolean onUnbind(Intent intent)
	{
	
	  if(player.isPlaying())
		player.stop();
		
	  this.player.release();
	  return false;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) 
	{
		mp.reset();
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp)
	{
		mp.start();
		duration = mp.getDuration();
		
		if(toSeek)
		{
			seek(current);
			toSeek = false;
			current = 0;
		}	
		//setAllButtons(true);
		
		Intent notIntent = new Intent(this, AllSongsActivity.class);
		notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		PendingIntent pendingIntent = PendingIntent.getActivity
				(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		
		builder.setContentIntent(pendingIntent)
			.setSmallIcon(R.drawable.play)
			.setTicker(songTitle)
			.setOngoing(true)
			.setContentTitle("Playing")
			.setContentText(songTitle);
		
		Notification notification = builder.build();
		
		startForeground(NOTIFY_ID, notification);
			
	}
	
	public String getSongTitle()
	{
		return songTitle;
	}
	
	public void playSong()
	{
		int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		
		if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
		{
		
			System.out.println("Play Song Service");
			player.reset();
			
			//get song
			System.out.println("Position: "+songPosition);
			Row item = songs.get(this.songPosition);
			Song playSong = null;
			
			if(item instanceof Song)
			{
				playSong = (Song) item;
				songTitle = playSong.getName();
			}	
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
			
			paused = false;
			//setAllButtons(false);
			player.prepareAsync();
		}
		else
			player.pause();
		
	}
	


	public void setSong(int songIndex)
	{
		  this.songPosition=songIndex;
	}
	
	public int getPosition()
	{
		return player.getCurrentPosition();
	}
	
	public int getDuration()
	{
		return duration;
	}
	
	public boolean isPlaying()
	{
		return player.isPlaying();
	}
	
	public void pausePlayer()
	{
		player.pause();
		paused = true;
	}
	
	public void seek(int position)
	{
		player.seekTo(position);
	}
	
	public void go()
	{
		if(paused)
		{
			System.out.println("Pause");
			player.start();
		}
		else
			playSong();
	}
	
	public void playPrevious()
	{
		if(repeat)
		{
			playSong();
			return;
		}
		
		if(isPlaying())
		{
			if(player.getCurrentPosition()>10000)
			{
				playSong();
				return;
			}
		}
		
		songPosition--;
		
		if(songs.get(songPosition) instanceof Section)
			songPosition--;
		
		if(songPosition<0)
			songPosition = songs.size()-1;
		
		playSong();
	}
	
	public void playNext()
	{
		System.out.println("Play Next");
		
		if(repeat)
		{
			playSong();
			return;
		}	
		else if(shuffle)
		{
			int newSong = songPosition;
			int size = songs.size();
			
			while(newSong == songPosition)
				newSong = rand.nextInt(size);
			
			songPosition = newSong;
		}
		else
		{
			songPosition++;
			
			if(songPosition>=songs.size())
				songPosition = 0;
		}	
		
		if(songs.get(songPosition) instanceof Section)
			songPosition++;
		
		playSong();
		
	}
	
	public void setShuffle()
	{
		if(shuffle)
			shuffle = false;
		else
			shuffle = true;
	}
	
	public void setRepeat()
	{
		if(repeat)
			repeat = false;
		else
			repeat = true;
	}
	
	
	@Override
	public void onDestroy()
	{
		stopForeground(true);
	}

	@Override
	public void onAudioFocusChange(int focusChange) 
	{
		switch (focusChange) 
		{
        case AudioManager.AUDIOFOCUS_GAIN:
            // resume playback
            if (player == null) 
            	initMusicPlayer();
            else if (!player.isPlaying()) 
            	player.start();
            
            player.setVolume(1.0f, 1.0f);
            break;

        case AudioManager.AUDIOFOCUS_LOSS:
            // Lost focus for an unbounded amount of time: stop playback and release media player
            if (isPlaying())
            	player.stop();
            
            //player.release();
            //player = null;
            break;

        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            // Lost focus for a short time, but we have to stop
            // playback. We don't release the media player because playback
            // is likely to resume
            if (player.isPlaying()) 
            	player.pause();
            
            break;

        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
            // Lost focus for a short time, but it's ok to keep playing
            // at an attenuated level
            if (player.isPlaying())
            	player.setVolume(0.1f, 0.1f);
            
            break;
		}
		
	}

	public String getDefaultSongTitle() 
	{
		if(songs != null & songs.size() > 1)
			return songs.get(1).getName();
		
		return null;
		
	}

	public int getSongPosition()
	{
		return songPosition;
	}

	public void setToSeek(boolean toSeek, int current) 
	{
		this.toSeek = toSeek;
		this.current = current;
	}

}
