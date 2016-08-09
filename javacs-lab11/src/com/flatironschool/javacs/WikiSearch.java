package com.flatironschool.javacs;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.ArrayList;

import redis.clients.jedis.Jedis;


/**
 * Represents the results of a search query.
 *
 */
public class WikiSearch {

	// map from URLs that contain the term(s) to relevance score
	private Map<String, Double> map;

	/**
	 * Constructor.
	 *
	 * @param map
	 */
	public WikiSearch(Map<String, Double> intMap) 
	{
		this.map = intMap;
		/*
		Set<String> iter = intMap.keySet();
		for(String url: iter)
		{
			this.map.put(url, (double)intMap.get(url));
		}
		*/
	}

	/**
	 * Looks up the relevance of a given URL.
	 *
	 * @param url
	 * @return
	 */
	public Double getRelevance(String url) {
		Double relevance = map.get(url);
		return relevance==null ? 0: relevance;
	}
	
	public void setRelevance()
	{
		//Iterator<> iter =  map.entrySet().iterator();
		Set<String> iter = map.keySet();
		for(String url: iter)
		{
			double relevance = index.
		}
	}

	/**
	 * Prints the contents in order of term frequency.
	 *
	 * @param map
	 */
	private  void print() {
		List<Entry<String, Double>> entries = sort();
		for (Entry<String, Double> entry: entries) {
			System.out.println(entry);
		}
	}

	/**
	 * Computes the union of two search results.
	 *
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch or(WikiSearch that)
	{
        Map<String, Double> union = new HashMap<String, Double>();
        Set<String> keys = map.keySet();
        for(String current: keys)
        {
        	union.put(current, totalRelevance(getRelevance(current), that.getRelevance(current)));
        }

        keys = that.map.keySet();
        for(String current: keys)
        {
        	if(!union.containsKey(current))
        		union.put(current, that.getRelevance(current));
        }

        return new WikiSearch(union);
	}

	/**
	 * Computes the intersection of two search results.
	 *
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch and(WikiSearch that)
	{
		Map<String, Double> inter = new HashMap<String, Double>();
        Set<String> keys = map.keySet();
        for(String current: keys)
        {
        	if(that.map.containsKey(current))
        		inter.put(current, totalRelevance(getRelevance(current), that.getRelevance(current)));
        }

        return new WikiSearch(inter);
	}

	/**
	 * Computes the intersection of two search results.
	 *
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch minus(WikiSearch that)
	{
		Map<String, Double> inter = new HashMap<String, Double>();
        Set<String> keys = map.keySet();
        for(String current: keys)
        {
        	if(!(that.map.containsKey(current)))
        		inter.put(current, totalRelevance(getRelevance(current), that.getRelevance(current)));
        }

        return new WikiSearch(inter);
	}

	/**
	 * Computes the relevance of a search with multiple terms.
	 *
	 * @param rel1: relevance score for the first search
	 * @param rel2: relevance score for the second search
	 * @return
	 */
	protected double totalRelevance(Double rel1, Double rel2) {
		// simple starting place: relevance is the sum of the term frequencies.
		return rel1 + rel2;
	}

	/**
	 * Sort the results by relevance.
	 *
	 * @return List of entries with URL and relevance.
	 */
	public List<Entry<String, Double>> sort()
	{
		List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>();
		for(Entry<String, Double> entry : map.entrySet())
		{
			list.add(entry);
		}

		Comparator<Entry<String, Double>> compare = new Comparator<Entry<String, Double>>() {
	            @Override
	            public int compare(Entry<String, Double> first, Entry<String, Double> second)
	            {
	            	if (first.getValue() < second.getValue())
	            	{
	                    return -1;
	                }
	                if (first.getValue() > second.getValue())
	                {
	                    return 1;
	                }
	                return 0;
	            }
	        };

	        Collections.sort(list, compare);
        return list;
	}

	/**
	 * Performs a search and makes a WikiSearch object.
	 *
	 * @param term
	 * @param index
	 * @return
	 */
	public static WikiSearch search(String term, JedisIndex index) 
	{
		Map<String, Integer> intMap = index.getCounts(term);
		Map<String, Double> doubMap = new HashMap<String, Double>();
		
		Set<String> iter = intMap.keySet();
		for(String url: iter)
		{
			doubMap.put(url, (double)intMap.get(url));
		}
		
		
		return new WikiSearch(doubMap);
	}

	public static void main(String[] args) throws IOException 
	{
		// make a JedisIndex
		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis);

		
		
		
		// search for the first term
		String term1 = "java";
		System.out.println("Query: " + term1);
		WikiSearch search1 = search(term1, index);
		search1.print();

		// search for the second term
		String term2 = "programming";
		System.out.println("Query: " + term2);
		WikiSearch search2 = search(term2, index);
		search2.print();

		// compute the intersection of the searches
		System.out.println("Query: " + term1 + " AND " + term2);
		WikiSearch intersection = search1.and(search2);
		intersection.print();
	}
}
