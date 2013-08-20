/**
 * Logger
 *
 * @license Dual licensed under the MIT or GPL Version 2 licenses.
 * @author xxxzxxx
 * Copyright 2013, Primitive, inc.
 * The MIT License (http://opensource.org/licenses/mit-license.php)
 * GPL Version 2 licenses (http://www.gnu.org/licenses/gpl-2.0.html)
 */
package com.example.twitter.util;

import android.os.Debug;
import android.util.Log;

import com.example.twitter.BuildConfig;

/**
 * Logger
 */
public class Logger
{
	public static class Level
	{
		public static final Comparison ComparisonMode = Comparison.Less;
		public static final Level Nothing = new Level(0);
		public static final Level Performance = new Level(10);
		public static final Level Error = new Level(20);
		public static final Level Warm = new Level(30);
		public static final Level Info = new Level(40);
		public static final Level Trace = new Level(50);
		public static final Level Debug = new Level(90);
		public static final Level All = new Level(100);
		private final int value;

		private Level(final int value)
		{
			this.value = value;
		}

		public static int comparison(Level compare, Level base)
		{
			int result = 0;
			if (Level.ComparisonMode == Comparison.Greater)
			{
				result = (compare.value < base.value) ? 0 : -1;
			} else if (Level.ComparisonMode == Comparison.Less)
			{
				result = (compare.value > base.value) ? 0 : -1;
			} else if (Level.ComparisonMode == Comparison.Equal)
			{
				result = (compare.value == base.value) ? 0 : -1;
			}
			return result;
		}

		public int comparison(Level base)
		{
			int result = 0;
			if (Level.ComparisonMode == Comparison.Greater)
			{
				result = (this.value < base.value) ? 0 : -1;
			} else if (Level.ComparisonMode == Comparison.Less)
			{
				result = (this.value > base.value) ? 0 : -1;
			} else if (Level.ComparisonMode == Comparison.Equal)
			{
				result = (this.value == base.value) ? 0 : -1;
			}
			return result;
		}
	}

	private static Level LogLevel = BuildConfig.DEBUG ? Level.All : Level.Error;

	public static long start()
	{
		final long started = System.nanoTime();
		if (Logger.LogLevel.comparison(Logger.Level.Trace) >= 0)
		{
			final StackTraceElement currentTack = Thread.currentThread().getStackTrace()[3];
			if (currentTack != null)
			{
				Log.i(currentTack.getClassName(), currentTack.getMethodName() + " start");
			}
		}
		return started;
	}

	public static long end()
	{
		final long end = System.nanoTime();
		if (Logger.LogLevel.comparison(Logger.Level.Trace) >= 0)
		{
			final StackTraceElement currentTack = Thread.currentThread().getStackTrace()[3];
			if (currentTack != null)
			{
				Log.i(currentTack.getClassName(), currentTack.getMethodName() + " end");
			}
		}
		return end;
	}

	public static long end(final long started)
	{
		final long end = System.nanoTime();
		if (Logger.LogLevel.comparison(Logger.Level.Trace) >= 0)
		{
			final StackTraceElement currentTack = Thread.currentThread().getStackTrace()[3];
			if (currentTack != null)
			{
				Log.i(currentTack.getClassName(), currentTack.getMethodName() + " end[" + (end - started) + "ns]");
			}
		}
		return end;
	}

	public static void info(Object obj)
	{
		if (Logger.LogLevel.comparison(Logger.Level.Info) >= 0)
		{
			final StackTraceElement currentTack = Thread.currentThread().getStackTrace()[3];
			if (currentTack != null)
			{
				Log.i(currentTack.getClassName(), obj != null ? obj.toString() : "obj is null");
			}
		}
	}

	public static void info(String msg)
	{
		if (Logger.LogLevel.comparison(Logger.Level.Info) >= 0)
		{
			final StackTraceElement currentTack = Thread.currentThread().getStackTrace()[3];
			if (currentTack != null)
			{
				Log.i(currentTack.getClassName(), msg);
			}
		}
	}

	public static void err(Throwable ex)
	{
		if (Logger.LogLevel.comparison(Logger.Level.Error) >= 0)
		{
			final StackTraceElement currentTack = Thread.currentThread().getStackTrace()[3];
			if (currentTack != null)
			{
				Log.e(currentTack.getClassName(), ex.getMessage(), ex);
			}
		}
	}

	public static void warm(String msg)
	{
		if (Logger.LogLevel.comparison(Logger.Level.Warm) >= 0)
		{
			final StackTraceElement currentTack = Thread.currentThread().getStackTrace()[3];
			if (currentTack != null)
			{
				Log.w(currentTack.getClassName(), msg);
			}
		}
	}

	public static void warm(Throwable ex)
	{
		if (Logger.LogLevel.comparison(Logger.Level.Warm) >= 0)
		{
			final StackTraceElement currentTack = Thread.currentThread().getStackTrace()[3];
			if (currentTack != null)
			{
				Log.w(currentTack.getClassName(), ex.getStackTrace().toString());
			}
		}
	}

	public static void debug(String msg)
	{
		if (Logger.LogLevel.comparison(Logger.Level.Debug) >= 0)
		{
			final StackTraceElement currentTack = Thread.currentThread().getStackTrace()[3];
			if (currentTack != null)
			{
				Log.d(currentTack.getClassName(), msg != null ? msg : "empty strings");
			}
		}
	}

	public static void debug(long l)
	{
		if (Logger.LogLevel.comparison(Logger.Level.Debug) >= 0)
		{
			final StackTraceElement currentTack = Thread.currentThread().getStackTrace()[3];
			if (currentTack != null)
			{
				Log.d(currentTack.getClassName(), "" + l);
			}
		}
	}

	public static void debug(Object obj)
	{
		if (Logger.LogLevel.comparison(Logger.Level.Debug) >= 0)
		{
			final StackTraceElement currentTack = Thread.currentThread().getStackTrace()[3];
			if (currentTack != null)
			{
				Log.d(currentTack.getClassName(), obj != null ? obj.toString() : "obj is null");
			}
		}
	}

	public static void times(final long started)
	{
		final long endl = System.nanoTime();
		if (Logger.LogLevel.comparison(Logger.Level.Performance) >= 0)
		{
			final StackTraceElement currentTack = Thread.currentThread().getStackTrace()[3];
			if (currentTack != null)
			{
				final String msg = "Process Time:" + (endl - started);
				Log.i(currentTack.getClassName(), msg);
			}
		}
	}

	public static void times(final long started, final long endl)
	{
		if (Logger.LogLevel.comparison(Logger.Level.Performance) >= 0)
		{
			final StackTraceElement currentTack = Thread.currentThread().getStackTrace()[3];
			if (currentTack != null)
			{
				final String msg = "Process Time:" + (endl - started);
				Log.i(currentTack.getClassName(), msg);
			}
		}
	}

	public static void heap()
	{
		if (Logger.LogLevel.comparison(Logger.Level.Performance) >= 0)
		{
			final StackTraceElement currentTack = Thread.currentThread().getStackTrace()[3];
			final String msg = "heap : Free=" + Long.toString(Debug.getNativeHeapFreeSize() / 1024) + "kb"
					+ "\n Allocated=" + Long.toString(Debug.getNativeHeapAllocatedSize() / 1024) + "kb" + "\n Size="
					+ Long.toString(Debug.getNativeHeapSize() / 1024) + "kb";
			if (currentTack != null)
			{
				Log.v(currentTack.getClassName(), msg);
			}
		}
	}
}