package com.view.om;


import com.control.om.SongListControl;
import com.om.R;
import com.om.util.Util;
import com.service.om.MusicService;
import com.service.om.MusicService.MusicBinder;

import android.support.v7.app.ActionBarActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;


public class AudioPlayer extends ActionBarActivity  implements SeekBar.OnSeekBarChangeListener

{
	
	//Buttons available on the player
	private ImageButton btnPlay;
	private ImageButton btnRepeat;
	private ImageButton btnShuffle;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;

	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000;
	
	private final int NEW_SONG = 1;
	private final int NOW_PLAYING = 2;
	private final int CONTINUE = 3;
	private final int PLAY = 4;
	
	
	private boolean musicBound;
	private boolean isRepeat;
	private boolean isShuffle;
	private int onEnterIntent;
	
	private MusicService musicService;
	private SongListControl songListControl;
	private Intent playIntent;
	private Handler mHandler;
	private int currentSongIndex;
	
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	private TextView songTitleLabel;
	
	private SeekBar songProgressBar;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		
		mHandler = new Handler();
		
		songListControl = new SongListControl(getApplicationContext());
		songListControl.setUpSongs();
		
		btnPlay = (ImageButton) findViewById(R.id.playButton);
		btnForward = (ImageButton) findViewById(R.id.forwardButton);
		btnBackward = (ImageButton) findViewById(R.id.backwardButton);
		btnNext = (ImageButton) findViewById(R.id.nextButton);
		btnPrevious = (ImageButton) findViewById(R.id.previousButton);
		btnRepeat = (ImageButton) findViewById(R.id.repeatButton);
		btnShuffle = (ImageButton) findViewById(R.id.shuffleButton);
		btnPlaylist = (ImageButton) findViewById(R.id.playlistButton);
		
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		
		btnPlay.setOnClickListener(clickListener);
		btnRepeat.setOnClickListener(clickListener);
		btnForward.setOnClickListener(clickListener);
		btnNext.setOnClickListener(clickListener);
		btnBackward.setOnClickListener(clickListener);
		btnShuffle.setOnClickListener(clickListener);
		btnPrevious.setOnClickListener(clickListener);
		btnPlaylist.setOnClickListener(clickListener);
		
		songProgressBar.setOnSeekBarChangeListener(this);
		playIntent = null;
		
		isRepeat = false;
		isShuffle = false;
		
		Intent intent = getIntent();
		
		if(intent.getExtras() != null)
		{
			try
			{
				currentSongIndex = intent.getExtras().getInt("songIndex");
			}
			catch(Exception e)
			{
				
			}
			
			onEnterIntent = intent.getExtras().getInt("onEnter");
			
		}	
			
		
     	 // play selected song
         
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.audio_player, menu);
		return true;
	}
	
	private ServiceConnection musicConnection = new ServiceConnection()
	{
	     
	      @Override
	      public void onServiceConnected(ComponentName name, IBinder service) 
	      {
	        MusicBinder binder = (MusicBinder)service;
	        //get service
	        
	        musicService = binder.getService();
	        musicService.getMediaPlayer().setOnCompletionListener(onCompletion);
	        
	        //pass list
	        musicService.setSongList(songListControl.getSongList());
	        musicBound = true;
	        
	        musicService.setSong(currentSongIndex);
	        //musicService.setButtons(buttons);
	        playSong();
	      }
	     
	      @Override
	      public void onServiceDisconnected(ComponentName name) {
	        musicBound = false;
	      }
	    };

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
    protected void onStart()
    {
    	super.onStart();
    	if(playIntent == null)
    	{
    		playIntent = new Intent(this, MusicService.class);
    		bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
    		startService(playIntent);
    	}
    	
    }
	
	final OnCompletionListener onCompletion = new OnCompletionListener()
	{
		
		@Override
		public void onCompletion(MediaPlayer mp) 
		{
			System.out.println("On Completion");
			
			if(mp.getCurrentPosition()>0)
				playNext();
		}

	};
	
	final OnClickListener clickListener = new OnClickListener() 
	{
	    public void onClick(View v) 
	    {
	    	switch (v.getId())
	    	{
	    		case R.id.playButton:
	    			
	    			if(onEnterIntent == CONTINUE)
	    				onEnterIntent = PLAY;
	    				
	    			playSong();
	    			break;
	    			
	    		case R.id.nextButton:
	    			playNext();
	    			break;
	    		
	    		case R.id.previousButton:
	    			playPrevious();
	    			break;
	    			
	    		case R.id.forwardButton:
	    			forward();
	    			break;
	    			
	    		case R.id.backwardButton:
	    			backward();
	    			break;
	    		
	    		case R.id.shuffleButton:
	    			shuffle();
	    			break;
	    			
	    		case R.id.repeatButton:
	    			repeatSong();
	    			break;
	    			
	    		case R.id.playlistButton:
	    			displayAllSongs();
	    			break;
	    			
	    	}
	    }
	};
	
	
	private void setUpProgressBar()
	{
		songProgressBar.setProgress(0);
		songProgressBar.setMax(musicService.getDuration());
		
		updateProgressBar();
	}
	
	private void playSong()
	{
		
		
		switch (onEnterIntent)
		{
			case NEW_SONG:
				System.out.println("New Song");
				musicService.playSong();
				songTitleLabel.setText(musicService.getSongTitle());
				btnPlay.setImageResource(R.drawable.btn_pause);
				onEnterIntent = CONTINUE;
				setUpProgressBar();
				break;
			
			case NOW_PLAYING:
				System.out.println("Now Playing");
				parseAudioPreferences();
				break;
			
			case PLAY:
				
				System.out.println("Play Song Audio Player");
				if(musicService != null && isPlaying())
				{
					musicService.pausePlayer();
					btnPlay.setImageResource(R.drawable.btn_play);
				}
				else
				{
					musicService.go();
					songTitleLabel.setText(musicService.getSongTitle());
					btnPlay.setImageResource(R.drawable.btn_pause);
				}
				
				setUpProgressBar();
				
				break;
				
		}
		
		
	}

	private void playNext()
	{
		musicService.playNext();
		songTitleLabel.setText(musicService.getSongTitle());
		btnPlay.setImageResource(R.drawable.btn_pause);
	}
	
	private void playPrevious()
	{
		musicService.playPrevious();
		songTitleLabel.setText(musicService.getSongTitle());
		btnPlay.setImageResource(R.drawable.btn_pause);
	
	}
	
	private boolean isPlaying()
	{
		if(musicService != null && musicBound)
			return musicService.isPlaying();
		
		return false;
	}
	
	private void forward()
	{
		int currentPosition = musicService.getPosition();
		System.out.println(currentPosition + seekForwardTime);
		// check if seekForward time is lesser than song duration
		if(currentPosition + seekForwardTime <= musicService.getDuration())
			musicService.seek(currentPosition + seekForwardTime);
		else
			musicService.seek(musicService.getDuration());
	}
	
	private void backward()
	{
		int currentPosition = musicService.getPosition();
		// check if seekBackward time is greater than 0 sec
		if(currentPosition - seekBackwardTime >= 0){
			// forward song
			musicService.seek(currentPosition - seekBackwardTime);
		}else{
			// backward to starting position
			musicService.seek(0);
		}
	}
	
	private void shuffle()
	{
		if(isShuffle)
		{
			isShuffle = false;
			btnShuffle.setImageResource(R.drawable.btn_shuffle);
			musicService.setShuffle(false);
		}
		else
		{
			// make repeat to true
			isShuffle= true;
			// make shuffle to false
			isRepeat = false;
			btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
			btnRepeat.setImageResource(R.drawable.btn_repeat);
			musicService.setRepeat(false);
			musicService.setShuffle(true);
		}
	}
	
	private void repeatSong()
	{
		if(isRepeat)
		{
			isRepeat = false;
			musicService.setRepeat(false);
			btnRepeat.setImageResource(R.drawable.btn_repeat);
		}
		else
		{
			// make repeat to true
			isRepeat = true;
			// make shuffle to false
			isShuffle = false;
			musicService.setRepeat(true);
			musicService.setShuffle(false);
			btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
			btnShuffle.setImageResource(R.drawable.btn_shuffle);
		}
	}
	
	private void displayAllSongs() 
	{
		Intent intent = new Intent(getApplicationContext(),
				AllSongsActivity.class);
		startActivity(intent);
		
		
	}
	
	private void setAudioPreferences()
	{
		SharedPreferences audioPreferences = this.getSharedPreferences("audio_preferences",
				Context.MODE_PRIVATE);
		Editor editor = audioPreferences.edit();
		
		editor.putString("title", songTitleLabel.getText().toString());
		editor.putInt("songIndex", musicService.getSongPosition());
		
		if(isRepeat)
			editor.putBoolean("repeat", true);
		else
			editor.putBoolean("repeat", false);
		
		if(isShuffle)
			editor.putBoolean("shuffle", true);
		else
			editor.putBoolean("shuffle", false);
		
		if(isPlaying())
		{	
			editor.putInt("position", musicService.getPosition());
			editor.putInt("duration", musicService.getDuration());
		}	
		
		editor.apply();
	}
	
	private void parseAudioPreferences()
	{
		System.out.println("Parse Preferences");
		SharedPreferences audioPreferences = this.getSharedPreferences("audio_preferences",
				Context.MODE_PRIVATE);
		
		String defaultTitle = null;
		
		if(musicService != null)
			defaultTitle = musicService.getDefaultSongTitle();
		
		if(defaultTitle == null)
			defaultTitle = "";
		
		String title = audioPreferences.getString("title", defaultTitle);
		boolean repeat = audioPreferences.getBoolean("repeat", false);
		boolean shuffle = audioPreferences.getBoolean("shuffle", false);
		int current = audioPreferences.getInt("position", 1);
		int duration = audioPreferences.getInt("duration", 0);
		int index = audioPreferences.getInt("songIndex", 1);
		
		setComponentsViaAudioPreferences(title, repeat, shuffle, current, duration,index );
		
	}
	
	private void setComponentsViaAudioPreferences(String title, boolean repeat, 
			boolean shuffle, int current, int duration, int index)
	{
		System.out.println(title);
		System.out.println("Duration: "+duration);
		System.out.println("Positio: "+current);
		System.out.println("Index: "+index);
		songTitleLabel.setText(title);
		
		if(repeat)
		{
			isRepeat = false;
			repeatSong();
		}
		
		if(shuffle)
		{
			isShuffle = false;
			shuffle();
		}
		
		if(isPlaying())
		{
			btnPlay.setImageResource(R.drawable.btn_pause);
			musicService.setSong(index);
			setUpProgressBar();
			onEnterIntent = CONTINUE;
		}	
		else
		{
			btnPlay.setImageResource(R.drawable.btn_play);
			//musicService.seek(current);
			songProgressBar.setProgress((int)
					(Util.getProgressPercentage(current, duration)));
			//updateProgressBar();
			System.out.println(index);
			currentSongIndex = index;
			musicService.setSong(currentSongIndex);
			musicService.setToSeek(true, current);
		    // Displaying Total Duration time
		    songTotalDurationLabel.setText(""+Util.milliSecondsToTimer(duration));
		    // Displaying time completed playing
		    songCurrentDurationLabel.setText(""+Util.milliSecondsToTimer(current));
			onEnterIntent = PLAY;
			System.out.println("OnEnterIntent: "+onEnterIntent);
		}
			
			
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) 
	{
		 if (musicService != null && musicService.isPlaying())
		 {
			    if (fromUser)
			     musicService.seek(progress);
		 }
		 
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) 
	{
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) 
	{
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = musicService.getDuration();
		int currentPosition = Util.progressToTimer(seekBar.getProgress(), totalDuration);
		
		// forward or backward to certain seconds
		musicService.seek(currentPosition);
		
		// update timer progress again
		updateProgressBar();
		
	}
	
	public void updateProgressBar() 
	{
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }
	
	private Runnable mUpdateTimeTask = new Runnable()
	{
		   public void run() 
		   {
			   long totalDuration = musicService.getDuration();
			   long currentDuration = musicService.getPosition();
			  
			   // Displaying Total Duration time
			   songTotalDurationLabel.setText(""+Util.milliSecondsToTimer(totalDuration));
			   // Displaying time completed playing
			   songCurrentDurationLabel.setText(""+Util.milliSecondsToTimer(currentDuration));
			   
			   // Updating progress bar
			   //int progress = (int)(Util.getProgressPercentage(currentDuration, totalDuration));
			   //Log.d("Progress", ""+progress);
			   songProgressBar.setMax((int)totalDuration);
			   songProgressBar.setProgress((int)currentDuration);
			   
			   // Running this thread after 100 milliseconds
		       mHandler.postDelayed(this, 100);
		   }
	};
	
	@Override
	public void onRestart()
	{
		System.out.println("OnRestart");
		parseAudioPreferences();
		super.onResume();
	}
	
	@Override
	public void onStop()
	{
		System.out.println("OnStop");
		setAudioPreferences();
		super.onStop();
	}
	
	@Override
	public void onDestroy()
	{
		System.out.println("OnDestroy");
		mHandler.removeCallbacks(mUpdateTimeTask);
	    unbindService(musicConnection);
		stopService(playIntent);
		musicService=null;
		super.onDestroy();
	}
	
}
