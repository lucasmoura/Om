package com.view.om;

import java.util.ArrayList;

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

	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000;
	
	
	private boolean musicBound;
	private boolean isRepeat;
	private boolean isShuffle;
	
	private MusicService musicService;
	private SongListControl songListControl;
	private Intent playIntent;
	private Handler mHandler;
	private int currentSongIndex;
	
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	private TextView songTitleLabel;
	
	private SeekBar songProgressBar;
	private ArrayList<ImageButton> buttons;
	
	
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
		
		buttons = new ArrayList<ImageButton>();
		
		buttons.add(btnBackward);
		buttons.add(btnForward);
		buttons.add(btnNext);
		buttons.add(btnPlay);
		buttons.add(btnPrevious);
		buttons.add(btnRepeat);
		buttons.add(btnShuffle);
		
		songProgressBar.setOnSeekBarChangeListener(this);
		playIntent = null;
		
		isRepeat = false;
		isShuffle = false;
		
		Intent intent = getIntent();
		
		currentSongIndex = intent.getExtras().getInt("songIndex");
		
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
	        
	        //pass list
	        musicService.setSongList(songListControl.getSongList());
	        musicBound = true;
	        
	        musicService.setSong(currentSongIndex);
	        musicService.setSongLabel(songTitleLabel);
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
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
        super.onActivityResult(requestCode, resultCode, data);
        
        if(resultCode == 100)
        {
         	 int currentSongIndex = data.getExtras().getInt("songIndex");
         	 // play selected song
             musicService.setSong(currentSongIndex);
             playSong();
        }
 
    }
	
	final OnClickListener clickListener = new OnClickListener() 
	{
	    public void onClick(View v) 
	    {
	    	switch (v.getId())
	    	{
	    		case R.id.playButton:
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
	    			
	    	}
	    }
	};
	
	private void playSong()
	{
		System.out.println("PlaySong");
		
		if(isPlaying())
		{
			if(musicService !=null)
			{
				musicService.pausePlayer();
				// Changing button image to play button
				btnPlay.setImageResource(R.drawable.btn_play);
			}
		}
		else
		{
			// Resume song
			if(musicService!=null)
			{
				System.out.println("Go");
				musicService.go();
				System.out.println("Set Title");
				//songTitleLabel.setText(musicService.getSongTitle());
				// Changing button image to pause button
				btnPlay.setImageResource(R.drawable.btn_pause);
			}
		}
		
		songProgressBar.setProgress(0);
		songProgressBar.setMax(100);
		
		updateProgressBar();
	}
	
	private void playNext()
	{
		musicService.playNext();
	}
	
	private void playPrevious()
	{
		musicService.playPrevious();
	
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
			musicService.setShuffle();
		}
		else
		{
			// make repeat to true
			isShuffle= true;
			// make shuffle to false
			isRepeat = false;
			btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
			btnRepeat.setImageResource(R.drawable.btn_repeat);
			
			musicService.setShuffle();
		}
	}
	
	private void repeatSong()
	{
		if(isRepeat)
		{
			isRepeat = false;
			musicService.setRepeat();
			btnRepeat.setImageResource(R.drawable.btn_repeat);
		}
		else
		{
			// make repeat to true
			isRepeat = true;
			// make shuffle to false
			isShuffle = false;
			musicService.setRepeat();
			btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
			btnShuffle.setImageResource(R.drawable.btn_shuffle);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) 
	{
		 if (musicService != null || musicService.isPlaying())
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
			   int progress = (int)(Util.getProgressPercentage(currentDuration, totalDuration));
			   //Log.d("Progress", ""+progress);
			   songProgressBar.setProgress(progress);
			   
			   // Running this thread after 100 milliseconds
		       mHandler.postDelayed(this, 100);
		   }
	};
	
	@Override
	public void onDestroy()
	{
		stopService(playIntent);
		musicService=null;
		super.onDestroy();
	}
	
}
