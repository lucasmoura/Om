package com.adapter.om;

import java.util.ArrayList;

import com.model.om.Song;
import com.om.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SongAdapter extends BaseAdapter
{
	private final ArrayList<Song> songs;
	private LayoutInflater songInf;
	
	public SongAdapter(Context context, ArrayList<Song> songs)
	{
		this.songs = songs;
		this.songInf = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() 
	{
		return songs.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LinearLayout songLayout = (LinearLayout)songInf.inflate
			      (R.layout.song, parent, false);
		
		  TextView songView = (TextView)songLayout.findViewById(R.id.song_title);
		  TextView artistView = (TextView)songLayout.findViewById(R.id.song_artist);
		  
		  Song currSong = songs.get(position);
		
		  songView.setText(currSong.getName());
		  artistView.setText(currSong.getArtist());
		 
		  songLayout.setTag(position);
		  return songLayout;
	}
	
}
