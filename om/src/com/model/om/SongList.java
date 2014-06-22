package com.model.om;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SongList 
{
	private ArrayList<Row> songList;
	private List<Object[]> alphabet;
	private HashMap<String, Integer> sections;
	
	public SongList()
	{
		this.songList = new ArrayList<Row>();
		this.alphabet = new ArrayList<Object[]>();
		this.sections = new HashMap<String, Integer>();
		
	}
	
	public ArrayList<Row> getSongList()
	{
		return songList;
	}
	
	public void parseMusicList(ArrayList<Row> songs)
	{
		int size = songs.size();
		int start = 0;
		int end = 0;
		String previousLetter = null;
		Object[] tmpIndexItem = null;
		
		for(int i =0; i<size; i++)
		{
			String firstLetter = songs.get(i).getName().substring(0,1);
			firstLetter = firstLetter.toUpperCase(Locale.UK);
			
			if(!Character.isLetter(firstLetter.charAt(0)))
				firstLetter="#";
			
			if(previousLetter != null && !firstLetter.equals(previousLetter))
			{
				end = songList.size()-1;
				tmpIndexItem = new Object[3];
				tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
				tmpIndexItem[1] = start;
				tmpIndexItem[2] = end;
				
				alphabet.add(tmpIndexItem);
				
				start = end+1;
			}
			
			if(!firstLetter.equals(previousLetter))
			{
				songList.add(new Section(firstLetter));
				sections.put(firstLetter, start);
			}	
			
			songList.add(songs.get(i));
			
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
	
	public void setOrderToAlphabetical(ArrayList<Row> songs)
	{
		Collections.sort(songs, new Comparator<Row>(){
			  public int compare(Row a, Row b)
			  {
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
