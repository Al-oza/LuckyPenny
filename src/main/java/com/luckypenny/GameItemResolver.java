package com.luckypenny;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.runelite.client.game.ItemManager;
import net.runelite.http.api.item.ItemPrice;

/**
 * Resolves OSRS item names (as they appear in chat / on the wiki) to a
 * concrete item id, icon and current Grand Exchange price, using
 * RuneLite's own item search rather than hardcoded numeric item ids or
 * ItemID constants. This keeps ChargeItemRegistry readable (it just
 * refers to items by name) and avoids the risk of a wrong/renamed id.
 */
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
     * not index; see the README for how to handle those.
     */
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

    /** Current Grand Exchange price, or 0 if the item can't be resolved/priced. */
    public long getPrice(String name)
    {
        return getPrice(name, null);
    }

    /** Current GE price using a known game item id when one is available. */
    public long getPrice(String name, Integer knownItemId)
    {
        Integer id = knownItemId == null ? resolveItemId(name) : knownItemId;
        return id == null ? 0L : itemManager.getItemPrice(id);
    }

    /** Item sprite for use in the panel, or null if it can't be resolved. */
    public BufferedImage getIcon(String name)
    {
        return getIcon(name, 1);
    }

    /** Item sprite with a stack amount, allowing RuneLite to select stack variants. */
    public BufferedImage getIcon(String name, int quantity)
    {
        return getIcon(name, quantity, null);
    }

    /** Item sprite using a known game item id when one is available. */
    public BufferedImage getIcon(String name, int quantity, Integer knownItemId)
    {
        return getIcon(name, quantity, quantity > 1, knownItemId);
    }

    /** Item sprite with explicit stack-size rendering behaviour. */
    public BufferedImage getIcon(String name, int quantity, boolean showStackSize, Integer knownItemId)
    {
        Integer id = knownItemId == null ? resolveItemId(name) : knownItemId;
        // Charge badges render their own count, while resources use RuneLite's
        // native stack-size text when their saved amount is a whole number.
        return id == null ? null : itemManager.getImage(id, quantity, showStackSize);
    }

}
