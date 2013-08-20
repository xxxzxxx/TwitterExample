package com.example.twitter.config;

import twitter4j.auth.AccessToken;
import android.content.SharedPreferences;

public class TwitterPreference
{

	private final SharedPreferences preferences;
	private final SharedPreferences.Editor editor;

	private static String AccessToken = "AccessToken";
	private static String AccessTokenSecret = "AccessTokenSecret";
	private static String UserId = "UserId";
	private static String ScreenName = "ScreenName";
	private static String Empty = "";

	public TwitterPreference(SharedPreferences preferences)
	{
		this.preferences = preferences;
		this.editor = preferences.edit();
	}

	public AccessToken buildAccessToken()
	{
		return new AccessToken(getAccessToken(), getAccessTokenSecret());
	}

	public String getAccessToken()
	{
		return preferences.getString(TwitterPreference.AccessToken, TwitterPreference.Empty);
	}

	public String getAccessTokenSecret()
	{
		return preferences.getString(TwitterPreference.AccessTokenSecret, TwitterPreference.Empty);
	}

	public Long getUserId()
	{
		return preferences.getLong(TwitterPreference.UserId, 0L);
	}

	public String getScreenName()
	{
		return preferences.getString(TwitterPreference.ScreenName, TwitterPreference.Empty);
	}

	public void setAccessToken(String accessToken)
	{
		editor.putString(TwitterPreference.AccessToken, accessToken);
	}

	public void setAccessTokenSecret(String accessTokenSecret)
	{
		editor.putString(TwitterPreference.AccessTokenSecret, accessTokenSecret);
	}

	public void setUserId(Long userId)
	{
		editor.putLong(TwitterPreference.UserId, userId);
	}

	public void setScreenName(String screenName)
	{
		editor.putString(TwitterPreference.ScreenName, screenName);
	}

	public boolean hasAccessToken()
	{
		return getAccessToken().length() > 0;
	}

	public boolean hasAccessTokenSecret()
	{
		return getAccessTokenSecret().length() > 0;
	}

	public boolean save(AccessToken token)
	{
		setAccessToken(token.getToken());
		setAccessTokenSecret(token.getTokenSecret());
		setUserId(token.getUserId());
		setScreenName(token.getScreenName());
		return commit();
	}

	public boolean clear()
	{
		final String[] keys = new String[]
		{ TwitterPreference.AccessToken, TwitterPreference.AccessTokenSecret, TwitterPreference.UserId,
				TwitterPreference.ScreenName, };
		for (final String key : keys)
		{
			editor.remove(key);
		}
		return editor.commit();
	}

	public boolean commit()
	{
		return editor.commit();
	}
}
