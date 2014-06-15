package com.control.om;


import android.content.Context;
import android.widget.Adapter;

import com.adapter.om.SongAdapter;
import com.model.om.SongList;

public class SongListControl 
{
	private Context context;
	private SongAdapter adapter;
	
	public SongListControl(Context context)
	{
		this.context = context;
	}
	
	public void setUpSongList()
	{
		SongList.getInstance().setMusicEnvironment(context);
		SongList.getInstance().setOrderToAlphabetical();
	}
	
	public Adapter getSongList()
	{
		this.adapter = new SongAdapter(context, SongList.getInstance().getSongList());
		return this.adapter;
	}
}
