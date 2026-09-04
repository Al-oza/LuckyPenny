package com.luckypenny;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

@PluginDescriptor(
        name = "Lucky Penny Tracker",
        description = "Tracks charges and resources saved by Ghommal's lucky penny",
        tags = {"penny", "charges", "combat achievements", "savings", "ghommal"}
)
public class GhommalLuckyPennyPlugin extends Plugin
{
    private static final Pattern SAVE_PATTERN =
            Pattern.compile("^Ghommal's luck saves a charge on your (.+)\\.$");
    private static final String SAVED_COUNTS_KEY = "savedCounts";
    private static final Type SAVED_COUNTS_TYPE =
            new TypeToken<LinkedHashMap<String, Integer>>() { }.getType();

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ConfigManager configManager;

    @Inject
    private Gson gson;

    @Inject
    private GhommalLuckyPennyConfig config;

    private LuckyPennyPanel panel;
    private NavigationButton navButton;

    private final Map<String, Integer> savedCounts = new LinkedHashMap<>();

    @SuppressWarnings("unused")
    @Provides
    GhommalLuckyPennyConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GhommalLuckyPennyConfig.class);
    }

    @Override
    protected void startUp()
    {
        GameItemResolver itemResolver = new GameItemResolver(itemManager);
        panel = new LuckyPennyPanel(itemResolver, config, this::resetSavedCounts);

        navButton = NavigationButton.builder()
                .tooltip("Ghommal's Lucky Penny")
                .icon(loadToolbarIcon())
                .priority(6)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);
        loadSavedCounts();
        refreshPanel();
    }

    @Override
    protected void shutDown()
    {
        if (navButton != null)
        {
            clientToolbar.removeNavigation(navButton);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() != ChatMessageType.GAMEMESSAGE
                && event.getType() != ChatMessageType.SPAM)
        {
            return;
        }

        String message = Text.removeTags(event.getMessage());
        Matcher matcher = SAVE_PATTERN.matcher(message);
        if (!matcher.find())
        {
            return;
        }

        String rawItemName = matcher.group(1).trim();
        ChargeItemDefinition definition = ChargeItemRegistry.match(rawItemName);
        String key = definition != null ? definition.getDisplayName() : rawItemName;

        savedCounts.merge(key, 1, Integer::sum);
        saveSavedCounts();
        refreshPanel();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (GhommalLuckyPennyConfig.GROUP.equals(event.getGroup()))
        {
            refreshPanel();
        }
    }

    private void loadSavedCounts()
    {
        String savedCountsJson = configManager.getConfiguration(
                GhommalLuckyPennyConfig.GROUP,
                SAVED_COUNTS_KEY
        );
        if (savedCountsJson == null || savedCountsJson.isEmpty())
        {
            return;
        }

        try
        {
            Map<String, Integer> loadedCounts = gson.fromJson(savedCountsJson, SAVED_COUNTS_TYPE);
            if (loadedCounts == null)
            {
                return;
            }

            for (Map.Entry<String, Integer> entry : loadedCounts.entrySet())
            {
                Integer count = entry.getValue();
                if (entry.getKey() != null && count != null && count > 0)
                {
                    savedCounts.put(entry.getKey(), count);
                }
            }
        }
        catch (JsonParseException ignored)
        {
            // Ignore malformed saved data so the tracker can continue recording new savings.
        }
    }

    private void saveSavedCounts()
    {
        configManager.setConfiguration(
                GhommalLuckyPennyConfig.GROUP,
                SAVED_COUNTS_KEY,
                gson.toJson(savedCounts, SAVED_COUNTS_TYPE)
        );
    }

    private void resetSavedCounts()
    {
        clientThread.invoke(() ->
        {
            savedCounts.clear();
            saveSavedCounts();
            refreshPanel();
        });
    }

    private void refreshPanel()
    {
        if (panel != null)
        {
            clientThread.invoke(() -> panel.rebuild(savedCounts));
        }
    }

    private static BufferedImage loadToolbarIcon()
    {
        return ImageUtil.loadImageResource(GhommalLuckyPennyPlugin.class, "icon.png");
    }
}
