package com.example.twitter;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.twitter.util.Logger;

public class TwitterAuthActivity extends TwitterBaseActivity
{
	public static final String IntentConsumerKey = "IntentConsumerKey";
	public static final String IntentConsumerSecret = "IntentConsumerSecret";
	public static final String IntentUserId = "IntentUserId";
	public static final String IntentScreenName = "IntentScreenName";
	public static final String IntentAccessToken = "IntentAccessToken";
	public static final String IntentAccessTokenSecret = "IntentAccessTokenSecret";

	private static final String OAUTH_VERIFIER = "oauth_verifier";
	private static final int TOAST_DURATION = Toast.LENGTH_LONG;

	public static Intent createIntent(Context context, String consumerKey, String consumerSecret)
	{
		final Intent intent = new Intent(context, TwitterAuthActivity.class);
		intent.putExtra(TwitterAuthActivity.IntentConsumerKey, consumerKey);
		intent.putExtra(TwitterAuthActivity.IntentConsumerSecret, consumerSecret);
		return intent;
	}

	private String callback;
	private WebView webView;

	private final WebChromeClient wClient = new WebChromeClient()
	{
		@Override
		public void onProgressChanged(WebView view, int newProgress)
		{
			super.onProgressChanged(view, newProgress);
			setProgress(newProgress * 100);
		}
	};

	private final WebViewClient wvClient = new WebViewClient()
	{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			boolean result;
			if ((url != null) && (url.startsWith(TwitterAuthActivity.this.callback)))
			{
				final Uri uri = Uri.parse(url);
				final String oAuthVerifier = uri.getQueryParameter(TwitterAuthActivity.OAUTH_VERIFIER);
				new AccessTokenTask().execute(oAuthVerifier);
				result = true;
			} else
			{
				result = super.shouldOverrideUrlLoading(view, url);
			}
			return result;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setResult(Activity.RESULT_CANCELED);
		this.webView = new WebView(this);
		final WebSettings webSettings = webView.getSettings();
		webSettings.setSavePassword(false);
		this.webView.setVerticalScrollbarOverlay(true);
		this.webView.setWebChromeClient(wClient);
		this.webView.setWebViewClient(wvClient);
		this.setContentView(webView);

		this.callback = this.getString(R.string.twitter_callback_url);
		if (this.callback == null)
		{
			showToast(R.string.OAuthActivity_InvalidParameter, Toast.LENGTH_SHORT);
			this.finish();
		}

		final RequestTokenTask preTask = new RequestTokenTask();
		preTask.execute();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			return super.onKeyUp(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (this.webView.canGoBack())
			{
				this.webView.goBack();
			} else if (!this.webView.canGoBack())
			{
				finish();
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	public class RequestTokenTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected String doInBackground(Void... params)
		{
			String authorizationUrl = null;
			try
			{
				final RequestToken requestToken = twitter.getOAuthRequestToken(callback);
				if (requestToken != null)
				{
					authorizationUrl = requestToken.getAuthorizationURL();
				}
			} catch (final TwitterException ex)
			{
				Logger.err(ex);
			}
			return authorizationUrl;
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			setProgressBarIndeterminateVisibility(false);
			if (result != null)
			{
				webView.loadUrl(result);
			} else
			{
				finish();
			}
		}
	}

	public class AccessTokenTask extends AsyncTask<String, Void, AccessToken>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		@Override
		protected AccessToken doInBackground(String... params)
		{
			AccessToken accessToken = null;
			if (params != null)
			{
				try
				{
					accessToken = twitter.getOAuthAccessToken(params[0]);
				} catch (final TwitterException ex)
				{
					Logger.err(ex);
				}
			}
			return accessToken;
		}

		@Override
		protected void onPostExecute(AccessToken result)
		{
			super.onPostExecute(result);
			if (result != null)
			{
				authSucessful(result);
			} else
			{
				authFailed();
			}
		}
	}

	private void authFailed()
	{
		showToast(R.string.OAuthActivity_AuthFailed, TwitterAuthActivity.TOAST_DURATION);
	}

	public static int RESULT_AUTH_SUCCESS = Activity.RESULT_OK;

	private void authSucessful(AccessToken token)
	{
		showToast(R.string.OAuthActivity_AuthSuccess, TwitterAuthActivity.TOAST_DURATION);
		Logger.debug(token.getToken());
		Logger.debug(token.getTokenSecret());
		final Intent intent = new Intent();
		intent.putExtra(TwitterAuthActivity.IntentUserId, token.getUserId());
		intent.putExtra(TwitterAuthActivity.IntentScreenName, token.getScreenName());
		intent.putExtra(TwitterAuthActivity.IntentAccessToken, token.getToken());
		intent.putExtra(TwitterAuthActivity.IntentAccessTokenSecret, token.getTokenSecret());
		setResult(TwitterAuthActivity.RESULT_AUTH_SUCCESS, intent);
		finish();
	}
}