package com.example.twitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.twitter.util.Logger;

public class WelcomeActivity extends TwitterBaseActivity implements OnClickListener
{
	private Button button = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final long started = Logger.start();
		setContentView(R.layout.activity_welcome);
		button = (Button) this.findViewById(R.id.action_start_oauth);
		button.setOnClickListener(this);
		if (preference.hasAccessToken())
		{
			openMainActivity();
		}
		Logger.end(started);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		final long started = Logger.start();
		if (requestCode == REQUEST_OAUTH && resultCode == Activity.RESULT_OK)
		{
			Logger.info("is equals REQUEST_OAUTH");
			preference.setUserId(intent.getLongExtra(TwitterAuthActivity.IntentUserId, 0L));
			preference.setScreenName(intent.getStringExtra(TwitterAuthActivity.IntentScreenName));
			preference.setAccessToken(intent.getStringExtra(TwitterAuthActivity.IntentAccessToken));
			preference.setAccessTokenSecret(intent.getStringExtra(TwitterAuthActivity.IntentAccessTokenSecret));
			preference.commit();
			openMainActivity();
		} else
		{
			Logger.info("is not equals REQUEST_OAUTH");
		}
		Logger.end(started);
	}

	@Override
	public void onClick(View view)
	{
		final long started = Logger.start();
		if (!preference.hasAccessToken())
		{
			openTwAuthActivity();
		} else
		{
			openMainActivity();
		}
		Logger.end(started);
	}
}