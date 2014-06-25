package com.view.om;


import com.control.om.SongListControl;
import com.om.R;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;



public class AllSongsActivity extends ActionBarActivity
{

	public ListView songDisplay;
	public SongListControl songListControl;
	private static float sideIndexX;
	private static float sideIndexY;
	private int sideIndexHeight;
	private int indexListSize;
	private int alphabetSize;
	private GestureDetector mGestureDetector;
	
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.all_songs);
        
        mGestureDetector = new GestureDetector(this, new SideIndexGestureListener());
        
        songListControl = new SongListControl(getApplicationContext());
        songDisplay = (ListView)findViewById(R.id.songList);
        
        displayAllSongs();
        displayAlphabeticIndex();
        
    }
    
    private void displayAlphabeticIndex()
    {
    	LinearLayout alphabeticList = (LinearLayout) findViewById(R.id.alphabeticSideIndex);
    	alphabeticList.removeAllViews();
    	
    	indexListSize = alphabetSize;
    	if(indexListSize<1)
    		return;
    	
    	int indexMaxSize = (int) Math.floor(alphabeticList.getHeight()/20);
    	int tmpIndexListSize = indexListSize;
    	
    	while(tmpIndexListSize > indexMaxSize)
    		tmpIndexListSize /= 2;
    	
    	double delta;
    	
    	if(tmpIndexListSize>0)
    		delta = indexListSize/tmpIndexListSize;
    	else
    		delta =1;
    	
    	TextView tmpTextView;
    	
    	for(double i = 1; i<=indexListSize; i = i+delta)
    	{
    		Object[] tmpIndexItem = this.songListControl.getAlphabetElement((int) i -1);
    		String tmpLetter = tmpIndexItem[0].toString();
    		
    		tmpTextView = new TextView(this);
    		tmpTextView.setText(tmpLetter);
    		tmpTextView.setGravity(Gravity.CENTER);
    		tmpTextView.setTextSize(15);
    		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
    				(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
    		tmpTextView.setLayoutParams(params);
    		alphabeticList.addView(tmpTextView);
    	}
    	
    	sideIndexHeight = alphabeticList.getHeight();
    	
    	alphabeticList.setOnTouchListener(new OnTouchListener() {
			
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
    
    private void displayAllSongs()
    {
    	this.songListControl.setUpSongs();
    	this.alphabetSize = this.songListControl.getAlphabetSize();
    	ListAdapter adapter = (ListAdapter) this.songListControl.getSongListAdapter();
    	this.songDisplay.setAdapter(adapter);
    	
    	
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
    		
    		songDisplay.setSelection(subItemPosition);
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
    
	    	case R.id.action_end:
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
    
    public void songPicked(View view)
    {
    	
    	Intent in = new Intent(getApplicationContext(),
				AudioPlayer.class);
		// Sending songIndex to PlayerActivity
		in.putExtra("songIndex", Integer.parseInt(view.getTag().toString()) );
		setResult(100, in);
		// Closing PlayListView
		finish();
		startActivity(in);
    }

}
