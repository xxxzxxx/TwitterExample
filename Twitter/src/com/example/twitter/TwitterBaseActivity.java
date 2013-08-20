package com.example.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.twitter.config.TwitterConfig;
import com.example.twitter.config.TwitterPreference;
import com.example.twitter.util.Logger;

public class TwitterBaseActivity extends SherlockFragmentActivity
{
	protected final TwitterConfig config = TwitterConfig.getInstance();
	protected TwitterFactory factory = new TwitterFactory(config.buildConfiguration());
	protected Twitter twitter = factory.getInstance();
	protected TwitterPreference preference = null;
	protected final int REQUEST_OAUTH = 1;
	protected String preferenceTag = null;
	protected final static String IntentPreferenceName = "preferenceName";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final long started = Logger.start();
		try
		{
			final Intent intent = getIntent();
			this.preferenceTag = intent.getStringExtra(TwitterBaseActivity.IntentPreferenceName);
			preference = new TwitterPreference(getSharedPreferences(this.preferenceTag != null ? this.preferenceTag
					: "currentPreference", Context.MODE_PRIVATE));
		} finally
		{
			Logger.end(started);
		}
	}

	protected void openTwAuthActivity()
	{
		final Intent intent = TwitterAuthActivity.createIntent(this, config.getConsumerKey(),
				config.getConsumerSecret());
		startActivityForResult(intent, REQUEST_OAUTH);
	}

	protected void showToast(int id, int duration)
	{
		Toast.makeText(this, getString(id), duration).show();
	}

	protected void openMainActivity()
	{
		final Intent intent = TwitterMainActivity.createIntent(this, this.preferenceTag);
		startActivity(intent);
	}

	protected void openMainActivity(String preferenceTag)
	{
		final Intent intent = TwitterMainActivity.createIntent(this, preferenceTag);
		startActivity(intent);
	}

}
