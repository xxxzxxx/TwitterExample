package com.example.twitter.config;

import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterConfig
{
	private static final String ConsumerKey = "4F0DnsHNuCznZhDCogEQ";
	private static final String ConsumerSecret = "rCZhcJOuztRviIvxaEkXOMDQ8q2fLQWw2wqGT5vpx8M";

	public final String consumerKey;
	public final String consumerSecret;

	public TwitterConfig(String consumerKey, String consumerSecret)
	{
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
	}

	private static TwitterConfig Instance = new TwitterConfig(TwitterConfig.ConsumerKey, TwitterConfig.ConsumerSecret);

	public static TwitterConfig getInstance()
	{
		return TwitterConfig.Instance;
	}

	public Configuration buildConfiguration()
	{
		final ConfigurationBuilder confbuilder = new ConfigurationBuilder();
		confbuilder.setOAuthConsumerKey(TwitterConfig.ConsumerKey);
		confbuilder.setOAuthConsumerSecret(TwitterConfig.ConsumerSecret);
		return confbuilder.build();
	}

	public String getConsumerKey()
	{
		return consumerKey;
	}

	public String getConsumerSecret()
	{
		return consumerSecret;
	}

}
