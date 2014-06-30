package com.adapter.om;

import java.util.ArrayList;

import com.om.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlayListAdapter extends BaseAdapter
{
	private ArrayList<String> playlist;
	
	public PlayListAdapter(ArrayList<String> playlist)
	{
		this.playlist = playlist;
	}

	@Override
	public int getCount() 
	{
		return playlist.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return playlist.get(position);
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
		
		LayoutInflater inflater = (LayoutInflater) 
				parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = (LinearLayout)inflater.inflate
			      (R.layout.playlist, parent, false);
		
		TextView playlistView = (TextView)view.findViewById(R.id.playListName);
		playlistView.setText(playlist.get(position));
		
		view.setTag(position);
		
		return view;
	}
}
