package com.flatironschool.javacs;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;

public class Search 
{
	private Jedis jedis;
	private WikiCrawler wc;
	private JedisIndex index;
	private WikiSearch searcher;
	String source;
	
	public Search() throws Exception
	{
		setUp();
	}

	/**
	 * @throws java.lang.Exception
	 * 		 
	 * */

	public void setUp() throws Exception 
	{
		// make a WikiCrawler
		
		jedis = JedisMaker.make();
		index = new JedisIndex(jedis);
		index.deleteAllKeys();
		index.deleteTermCounters();
		index.deleteURLSets();
		source = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		wc = new WikiCrawler(source, index);
		int stop = 0;
		do
		{
			wc.crawl(false);
			stop++;
		}
		while(stop < 20);

	}

	/**
	 * @throws java.lang.Exception
	 */

	public void tearDown() throws Exception 
	{
		jedis.close();
	}
	
	public ArrayList<Entry<String, Double>> singleSearch(String term) 
	{
		searcher = new WikiSearch(userSearch(term));
		return searcher.printToScreen();
	}
	
	public ArrayList<Entry<String, Double>> andSearch(String term) 
	{
		int space = term.indexOf(" ");
		String first = term.substring(0, space);
		String second = term.substring(space+1, term.length());
		
		WikiSearch searcher1 = new WikiSearch(userSearch(first));
		WikiSearch searcher2 = new WikiSearch(userSearch(second));
		
		WikiSearch and = searcher1.and(searcher2);
		return and.printToScreen();
	}
	
	public ArrayList<Entry<String, Double>> orSearch(String term)
	{
		int space = term.indexOf(" ");
		String first = term.substring(0, space);
		String second = term.substring(space+1, term.length());
		
		WikiSearch searcher1 = new WikiSearch(userSearch(first));
		WikiSearch searcher2 = new WikiSearch(userSearch(second));
		
		WikiSearch or = searcher1.or(searcher2);
		return or.printToScreen();
	}
	
	public Map<String, Double> userSearch(String term)
	{
		Map<String, Integer> intMap = index.getCounts(term);
		Map<String, Double> map = makeDoub(intMap);
		map = setRelevance(map, term);
		
		return map;
	}
	
	public Map<String, Double> makeDoub(Map<String, Integer> intMap)
	{
		Map<String, Double> doubMap = new HashMap<String, Double>();
		
		Set<String> iter = intMap.keySet();
		
		for(String url: iter)
		{
			doubMap.put(url, (double)intMap.get(url));
		}
		
		return doubMap;
	}
	
	public Map<String, Double> setRelevance(Map<String, Double> map, String term)
	{
		
		Set<String> iter = map.keySet();

		for(String url: iter)
		{
			double relevance = index.getTDIDF(url, term);
			map.put(url, relevance);
		}
		
		return map;
	}

}
