package com.control.om;

import java.util.ArrayList;

import android.content.Context;

import com.model.om.Song;
import com.model.om.SongList;

public class SongListControl 
{
	private Context context;
	
	public SongListControl(Context context)
	{
		this.context = context;
	}
	
	public void setUpSongList()
	{
		SongList.getInstance().setMusicEnvironment(context);
		SongList.getInstance().setOrderToAlphabetical();
	}
	
	public ArrayList<Song> getSongList()
	{
		return SongList.getInstance().getSongList();
	}
}
