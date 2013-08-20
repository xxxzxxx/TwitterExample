package com.example.twitter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.twitter.util.Logger;

public class SplashActivity extends TwitterBaseActivity
{
	public abstract class PausingTask extends AsyncTask<Void, Void, Void>
	{

		protected final long maxPausingTimeMillis;

		public PausingTask(long maxPausingTimeMillis)
		{
			this.maxPausingTimeMillis = maxPausingTimeMillis;
		}

		protected abstract void doProcessFinished();

		protected abstract Void doProcess(Void... params);

		@Override
		protected Void doInBackground(Void... params)
		{
			final long started = System.currentTimeMillis();
			final Void result = doProcess(params);
			final long endl = System.currentTimeMillis();
			final long rest = maxPausingTimeMillis - (endl - started);
			if (rest > 0)
			{
				try
				{
					Thread.sleep(rest);
				} catch (final InterruptedException ex)
				{
					Logger.err(ex);
				}
			}
			doProcessFinished();
			return result;
		}
	}

	class SplashTask extends PausingTask
	{
		public SplashTask(long maxPausingTimeMillis)
		{
			super(maxPausingTimeMillis);
		}

		@Override
		protected Void doProcess(Void... params)
		{
			return null;
		}

		@Override
		protected void doProcessFinished()
		{
			final Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			SplashActivity.this.startActivityForResult(intent, SplashActivity.REQUEST_HOME);
			SplashActivity.this.finish();
		}
	}

	private static final int REQUEST_HOME = 0;
	private static final long MAX_PAUSE_TIME = 2000;

	private final SplashTask task = new SplashTask(SplashActivity.MAX_PAUSE_TIME);

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		task.execute();
	}
}
