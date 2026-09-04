package com.luckypenny;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.runelite.client.game.ItemManager;
import net.runelite.http.api.item.ItemPrice;

public class GameItemResolver
{
    private final ItemManager itemManager;
    private final Map<String, Integer> nameToId = new ConcurrentHashMap<>();

    public GameItemResolver(ItemManager itemManager)
    {
        this.itemManager = itemManager;
    }

    /**
     * Finds the item id for an exact (case-insensitive) item name, falling
     * back to the best search match. Returns null if nothing is found -
     * this can happen for untradeable items that ItemManager#search does
     * not index; */
    public Integer resolveItemId(String name)
    {
        return nameToId.computeIfAbsent(name.toLowerCase(), n ->
        {
            List<ItemPrice> results = itemManager.search(name);
            if (results == null || results.isEmpty())
            {
                return null;
            }

            for (ItemPrice price : results)
            {
                if (price.getName().equalsIgnoreCase(name))
                {
                    return price.getId();
                }
            }

            return results.get(0).getId();
        });
    }

    public long getPrice(String name, Integer knownItemId)
    {
        Integer id = knownItemId == null ? resolveItemId(name) : knownItemId;
        return id == null ? 0L : itemManager.getItemPrice(id);
    }

    public BufferedImage getIcon(String name, int quantity)
    {
        return getIcon(name, quantity, null);
    }
    public BufferedImage getIcon(String name, int quantity, Integer knownItemId)
    {
        return getIcon(name, quantity, quantity > 1, knownItemId);
    }
    public BufferedImage getIcon(String name, int quantity, boolean showStackSize, Integer knownItemId)
    {
        Integer id = knownItemId == null ? resolveItemId(name) : knownItemId;
        return id == null ? null : itemManager.getImage(id, quantity, showStackSize);
    }

}
