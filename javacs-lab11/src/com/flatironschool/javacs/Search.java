package com.flatironschool.javacs;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class Search 
{
	private Jedis jedis;
	private WikiCrawler wc;
	private JedisIndex index;
	private WikiSearch searcher;
	
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
		String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		wc = new WikiCrawler(source, index);

		// for testing purposes, load up the queue
		WikiFetcher wf = new WikiFetcher();
		Elements paragraphs = wf.fetchWikipedia(source);
		wc.queueInternalLinks(paragraphs);
	}

	/**
	 * @throws java.lang.Exception
	 */

	public void tearDown() throws Exception 
	{
		jedis.close();
	}
	
	public ArrayList<String> userSearch(String term) //one word - fix this!
	{
		searcher = new WikiSearch(index.getCounts(term));
		
		
	}

}
