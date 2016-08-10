package com.flatironschool.javacs;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

import redis.clients.jedis.Jedis;


public class JedisMaker {

	/**
	 * Make a Jedis object and authenticate it.
	 *
	 * @return
	 * @throws IOException
	 */
	public static Jedis make() throws IOException {
		// assemble the file name
		String slash = File.separator;
		String filename = "resources" + slash + "redis_url.txt";

		URL fileURL = JedisMaker.class.getClassLoader().getResource(filename);
    String filepath = URLDecoder.decode(fileURL.getFile(), "UTF-8");
		StringBuilder sb = new StringBuilder();

		BufferedReader br;
		try {
                    br = new BufferedReader(new FileReader(filepath));
		} catch (FileNotFoundException e1) {
			System.out.println("File not found: " + filename);
			return null;
		}

		while (true) {
			String line = br.readLine();
			if (line == null) break;
			sb.append(line);
		}
		br.close();

		URI uri;
		try {
			uri = new URI(sb.toString());
		} catch (URISyntaxException e) {
			System.out.println("Reading file: " + filename);
			System.out.println("It looks like this file does not contain a valid URI.");
			return null;
		}
		String host = uri.getHost();
		int port = uri.getPort();

		String[] array = uri.getAuthority().split("[:@]");
		String auth = array[1];

		Jedis jedis = new Jedis(host, port);

		try {
			jedis.auth(auth);
		} catch (Exception e) {
			System.out.println("Trying to connect to " + host);
			System.out.println("on port " + port);
			System.out.println("with authcode " + auth);
			System.out.println("Got exception " + e);
			return null;
		}
		return jedis;
	}
}
