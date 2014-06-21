package com.view.om;


import com.control.om.SongListControl;
import com.model.om.MusicController;
import com.om.R;
import com.service.om.MusicService;
import com.service.om.MusicService.MusicBinder;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;

import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
//import android.view.MenuItem;
//import android.view.View;

public class AllSongsActivity extends ActionBarActivity implements MediaPlayerControl
{

	public ListView songDisplay;
	public SongListControl songListControl;
	private static float sideIndexX;
	private static float sideIndexY;
	private int sideIndexHeight;
	private int indexListSize;
	private int alphabetSize;
	private GestureDetector mGestureDetector;
	private MusicService musicService;
	private Intent playIntent;
	private MusicController musicController;
	private boolean musicBound;
	private boolean paused;
	private boolean playbackPaused;
	
	class SideIndexGestureListener extends GestureDetector.SimpleOnGestureListener
	{
		public boolean onScrool(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
		{
			sideIndexX = sideIndexX - distanceX;
			sideIndexY = sideIndexY - distanceY;
			
			if(sideIndexX>=0 && sideIndexY>=0)
				displayListItem();
			
			return super.onScroll(e1, e2, distanceX, distanceY);
			
		}
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_songs);
        
        mGestureDetector = new GestureDetector(this, new SideIndexGestureListener());
        
        songListControl = new SongListControl(getApplicationContext());
        songDisplay = (ListView)findViewById(R.id.songList);
        musicBound = false;
        paused = false;
        playbackPaused = false;
        
        displayAllSongs();
        setController();
        
    }
    
  //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){
     
      @Override
      public void onServiceConnected(ComponentName name, IBinder service) {
        MusicBinder binder = (MusicBinder)service;
        //get service
        musicService = binder.getService();
        //pass list
        musicService.setSongList(songListControl.getSongList());
        musicBound = true;
      }
     
      @Override
      public void onServiceDisconnected(ComponentName name) {
        musicBound = false;
      }
    };
    
    private void displayAllSongs()
    {
    	this.songListControl.setUpSongList();
    	this.alphabetSize = this.songListControl.getAlphabetSize();
    	ListAdapter adapter = (ListAdapter) this.songListControl.getSongListAdapter();
    	this.songDisplay.setAdapter(adapter);
    	
    	LinearLayout sideList = (LinearLayout) findViewById(R.id.alphabeticSideIndex);
    	sideList.removeAllViews();
    	
    	this.indexListSize = this.alphabetSize;
    	if(indexListSize<1)
    		return;
    	
    	int indexMaxSize = (int) Math.floor(sideList.getHeight()/20);
    	int tmpIndexListSize = indexListSize;
    	
    	while(tmpIndexListSize > indexMaxSize)
    		tmpIndexListSize /= 2;
    	
    	double delta;
    	
    	if(tmpIndexListSize>0)
    		delta = indexListSize/tmpIndexListSize;
    	else
    		delta =1;
    	
    	TextView tmpTV;
    	
    	for(double i = 1; i<=indexListSize; i = i+delta)
    	{
    		Object[] tmpIndexItem = this.songListControl.getAlphabetElement((int) i -1);
    		String tmpLetter = tmpIndexItem[0].toString();
    		
    		tmpTV = new TextView(this);
    		tmpTV.setText(tmpLetter);
    		tmpTV.setGravity(Gravity.CENTER);
    		tmpTV.setTextSize(15);
    		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
    				(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
    		tmpTV.setLayoutParams(params);
    		sideList.addView(tmpTV);
    	}
    	
    	sideIndexHeight = sideList.getHeight();
    	
    	sideList.setOnTouchListener(new OnTouchListener() {
			
    		@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				sideIndexX = event.getX();
				sideIndexY = event.getY();
				
				displayListItem();
				
				return false;
			}
		});
    	
    }
    
    public void displayListItem()
    {
    	LinearLayout sideIndex = (LinearLayout) findViewById(R.id.alphabeticSideIndex);
    	sideIndexHeight = sideIndex.getHeight();
    	
    	double pixelPerIndexItem = (double) sideIndexHeight/indexListSize;
    	int itemPosition = (int) (sideIndexY/pixelPerIndexItem);
    	
    	if(itemPosition< this.alphabetSize)
    	{
    		Object[] indexItem = this.songListControl.getAlphabetElement(itemPosition);
    		int subItemPosition = this.songListControl.getAlphabetPosition((String) indexItem[0]);
    		
    		ListView list = (ListView)findViewById(R.id.songList);
    		list.setSelection(subItemPosition);
    	}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_songs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	switch (item.getItemId()) 
    	{
    		
    		case R.id.action_shuffle:
    			musicService.setShuffle();
    			break;
    
	    	case R.id.action_end:    		
	    	  stopService(this.playIntent);
	    	  this.musicService=null;
	    	  System.exit(0);
	    	  break;
    	}
    	return super.onOptionsItemSelected(item);
       
    }

    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
    	if(mGestureDetector.onTouchEvent(event))
    		return true;
    	else
    		return false;
    }
    
    @Override
    protected void onStart()
    {
    	super.onStart();
    	if(this.playIntent == null)
    	{
    		playIntent = new Intent(this, MusicService.class);
    		bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
    		startService(playIntent);
    	}
    }
    
    public void songPicked(View view)
    {
    	musicService.setSong(Integer.parseInt(view.getTag().toString()));
    	musicService.playSong();
    	  
    	if(playbackPaused)
      	{
      		setController();
      		playbackPaused = false;
      	}
    	  
    	musicController.show(0);
    }

    private void setController()
    {
    	musicController = new MusicController(this);
    	
    	musicController.setPrevNextListeners(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				playNext();
			}
		}, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				playPrevious();
				
			}
		});
    	
    	musicController.setMediaPlayer(this);
    	musicController.setAnchorView(findViewById(R.id.songList));
    	musicController.setEnabled(true);
    }
    
    private void playNext()
    {
    	musicService.playNext();
    	
    	if(playbackPaused)
    	{
    		setController();
    		playbackPaused = false;
    	}
    	
    	musicController.show(0);
    }
    
    private void playPrevious()
    {
    	musicService.playerPrevious();
    	
    	if(playbackPaused)
    	{
    		setController();
    		playbackPaused = false;
    	}
    	
    	musicController.show(0);
    }
    
	@Override
	public void start() 
	{
		musicService.go();
	}

	@Override
	public void pause() 
	{
		playbackPaused = true;
		musicService.pausePlayer();
	}

	@Override
	public int getDuration() 
	{
		if(musicService != null && musicBound && musicService.isPlaying())
			return musicService.getDuration();
		
		return 0;
	}

	@Override
	public int getCurrentPosition() 
	{
		if(musicService != null && musicBound && musicService.isPlaying())
			return musicService.getPosition();
		
		return 0;
	}

	@Override
	public void seekTo(int pos) 
	{
		musicService.seek(pos);
	}

	@Override
	public boolean isPlaying() 
	{
		if(musicService != null && musicBound)
			return musicService.isPlaying();
		
		return false;
	}

	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canPause() 
	{
		return true;
	}

	@Override
	public boolean canSeekBackward()
	{
		return true;
	}

	@Override
	public boolean canSeekForward() 
	{
		return true;
	}

	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		paused = true;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		if(paused)
		{
			setController();
			paused = false;
		}
	}
	
	@Override
	protected void onStop()
	{
		musicController.hide();
		super.onStop();
	}

}
