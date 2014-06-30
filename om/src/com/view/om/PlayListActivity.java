package com.view.om;

import java.util.ArrayList;

import com.adapter.om.PlayListAdapter;
import com.om.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;


public class PlayListActivity extends Activity
{

	private ListView playlistListView;
	private RelativeLayout newPlaylist;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_list_activity);

		playlistListView = (ListView) findViewById(R.id.playlistList);
		newPlaylist = (RelativeLayout) findViewById(R.id.new_playlist_layout);
		
		displayPlaylist();
		
		newPlaylist.setOnClickListener(onClickListener);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void displayPlaylist() 
	{
		ArrayList<String> playlist = new ArrayList<String>();
		playlist.add("PlayList 01");
		playlist.add("PlayList 2");
		
		ListAdapter playlistAdapter = new PlayListAdapter(playlist);
		playlistListView.setAdapter(playlistAdapter);
		
	}
	
	final OnClickListener onClickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
			{
				case R.id.new_playlist_layout:
					System.out.println("New");
					break;
			}		
		}
		
	};


}
