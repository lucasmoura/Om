package com.adapter.om;

import java.util.ArrayList;

import com.model.om.Row;
import com.model.om.Song;
import com.model.om.Section;
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
	private final ArrayList<Row> songs;
	
	public SongAdapter(ArrayList<Row> songs)
	{
		this.songs = songs;
	}
	
	@Override
	public int getCount() 
	{
		return songs.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return songs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		View view = convertView;
		
	
		if(getItemViewType(position) == 0)
		{
			
				LayoutInflater inflater = (LayoutInflater) 
						parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = (LinearLayout)inflater.inflate
					      (R.layout.song, parent, false);
			
			
			  TextView songView = (TextView)view.findViewById(R.id.song_title);
			  TextView artistView = (TextView)view.findViewById(R.id.song_artist);
			  
			  Song currSong = (Song) songs.get(position);
			
			  songView.setText(currSong.getName());
			  artistView.setText(currSong.getArtist());
			 
			  view.setTag(position);
			  
		}
		else
		{
			 
			LayoutInflater inflater = (LayoutInflater) 
					parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = (LinearLayout) inflater.inflate(R.layout.alphabet_song_section, parent, false);
			
			Section section = (Section) getItem(position); 
			TextView textView = (TextView) view.findViewById(R.id.alphabet_section); 
			textView.setText(section.getName());
		}
		
		return view;
	}
	
	public int getItemViewType(int position)
	{
		if(getItem(position) instanceof Section)
			return 1;
		else
			return 0;
	}
	
}
