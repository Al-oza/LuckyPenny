package com.luckypenny;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

/**
 * Run this class's main() method from your IDE to launch a RuneLite client
 * with this plugin already loaded, for quick manual testing.
 */
public class LuckyPennyTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GhommalLuckyPennyPlugin.class);
		RuneLite.main(args);
	}
}
