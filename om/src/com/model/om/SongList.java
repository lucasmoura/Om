package com.model.om;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SongList 
{
	private ArrayList<Row> songList;
	private static SongList singleton = null;
	private ContentResolver musicResolver;
	private Uri musicUri;
	private Cursor musicCursor;
	private List<Object[]> alphabet;
	private HashMap<String, Integer> sections;
	
	public static SongList getInstance()
	{
		if(singleton == null)
			singleton = new SongList();
		
		return singleton;
	}
	
	private SongList()
	{
		this.songList = new ArrayList<Row>();
		this.alphabet = new ArrayList<Object[]>();
		this.sections = new HashMap<String, Integer>();
		this.musicUri = null;
		this.musicCursor = null;
	}
	
	public ArrayList<Row> getSongList()
	{
		return this.songList;
	}
	
	
	public void setMusicEnvironment(Context context)
	{	
		if(context == null)
			return;
		
		this.musicResolver = context.getContentResolver();
		this.musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		this.musicCursor = musicResolver.query(musicUri, null, null, null, null);
	}
	
	public void retrieveMusicFromDevice()
	{
		
		if(musicCursor!=null && musicCursor.moveToFirst())
		{
			
			int titleColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media.TITLE);
			int idColumn = musicCursor.getColumnIndex
				    (android.provider.MediaStore.Audio.Media._ID);
			int artistColumn = musicCursor.getColumnIndex
				    (android.provider.MediaStore.Audio.Media.ARTIST);
			
			do 
			{
			    long thisId = musicCursor.getLong(idColumn);
			    String thisTitle = musicCursor.getString(titleColumn);
			    String thisArtist = musicCursor.getString(artistColumn);
			    songList.add(new Song(thisId, thisTitle, thisArtist));
			    
			}while (musicCursor.moveToNext());
			  
		}
		
		setOrderToAlphabetical();
		parseMusicList();
	}
	
	public void parseMusicList()
	{
		int size = songList.size();
		int start = 0;
		int end = 0;
		int newElements = 0;
		String previousLetter = null;
		Object[] tmpIndexItem = null;
		
		System.out.println(size);
		
		for(int i =0; i<size; i++)
		{
			String firstLetter = this.songList.get(i).getName().substring(0,1);
			firstLetter = firstLetter.toUpperCase(Locale.UK);
			
			if(!Character.isLetter(firstLetter.charAt(0)))
				firstLetter="#";
			
			if(previousLetter != null && !firstLetter.equals(previousLetter))
			{
				end = (i+newElements) -1;
				tmpIndexItem = new Object[3];
				tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
				tmpIndexItem[1] = start;
				tmpIndexItem[2] = end;
				
				this.alphabet.add(tmpIndexItem);
				
				start = end + 1;
				newElements++;
			}
			
			if(!firstLetter.equals(previousLetter))
			{
				this.songList.add(i, new Section(firstLetter));
				size++;
				sections.put(firstLetter, start);
			}	
			
			previousLetter = firstLetter;
		}	
			
		if(previousLetter != null)
		{
			tmpIndexItem = new Object[3];
			tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
			tmpIndexItem[1] = start;
			tmpIndexItem[2] = songList.size();
			
			this.alphabet.add(tmpIndexItem);
		}
		
	}
	
	public void setOrderToAlphabetical()
	{
		Collections.sort(this.songList, new Comparator<Row>(){
			  public int compare(Row a, Row b){
			    return a.getName().compareTo(b.getName());
			  }
		});
	}
	
	public int getAlphabetSize()
	{
		return alphabet.size();
	}
	
	public Object[] getAlphabetElement(int index)
	{
		return alphabet.get(index);
	}
	
	public int getAlphabetPosition(String letter)
	{
		return sections.get(letter);
	}
}
