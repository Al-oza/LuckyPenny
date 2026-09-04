package com.luckypenny;

import java.util.Arrays;
import java.util.List;

public class ChargeItemDefinition
{
    private final String displayName;
    private final List<String> aliases;
    private final ChargeCost cost;
    private final Integer itemId;

    public ChargeItemDefinition(String displayName, ChargeCost cost, String... aliases)
    {
        this(displayName, cost, null, aliases);
    }

    public ChargeItemDefinition(String displayName, ChargeCost cost, Integer itemId, String... aliases)
    {
        this.displayName = displayName;
        this.cost = cost;
        this.itemId = itemId;
        this.aliases = Arrays.asList(aliases);
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public ChargeCost getCost()
    {
        return cost;
    }

    public Integer getItemId()
    {
        return itemId;
    }

    /**
     * Whether a name captured from the "Ghommal's lucky penny saves a
     * charge on your X." chat message refers to this item. Charge-count
     * suffixes like "(5)" on jewellery are ignored.
     */
    public boolean matches(String chatName)
    {
        String normalised = normalise(chatName);
        if (normalise(displayName).equals(normalised))
        {
            return true;
        }
        for (String alias : aliases)
        {
            if (normalise(alias).equals(normalised))
            {
                return true;
            }
        }
        return false;
    }

    static String normalise(String name)
    {
        return name.toLowerCase()
                .replaceAll("\\(\\d+\\)$", "")
                .trim();
    }
}
