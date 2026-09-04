package com.luckypenny;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
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

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ClientThread clientThread;

    @Inject
    private LuckyPennyStorage storage;

    @Inject
    private GhommalLuckyPennyConfig config;

    private GameItemResolver itemResolver;
    private LuckyPennyPanel panel;
    private NavigationButton navButton;

    private final Map<String, Integer> savedCounts = new LinkedHashMap<>();

    @Provides
    GhommalLuckyPennyConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GhommalLuckyPennyConfig.class);
    }

    @Override
    protected void startUp()
    {
        itemResolver = new GameItemResolver(itemManager);
        panel = new LuckyPennyPanel(itemResolver, config);

        navButton = NavigationButton.builder()
                .tooltip("Ghommal's Lucky Penny")
                .icon(loadToolbarIcon())
                .priority(6)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);
        refreshPanel();
        storage.load(this::onSavedCountsLoaded);
    }

    @Override
    protected void shutDown()
    {
        if (navButton != null)
        {
            clientToolbar.removeNavigation(navButton);
        }

        storage.shutDown();
    }

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
        storage.save(savedCounts);
        refreshPanel();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (GhommalLuckyPennyConfig.GROUP.equals(event.getGroup()))
        {
            refreshPanel();
        }
    }

    private void onSavedCountsLoaded(Map<String, Integer> loadedCounts)
    {
        for (Map.Entry<String, Integer> entry : loadedCounts.entrySet())
        {
            savedCounts.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        storage.save(savedCounts);
        refreshPanel();
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
