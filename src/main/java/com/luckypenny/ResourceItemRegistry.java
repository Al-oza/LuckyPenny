package com.luckypenny;

import java.util.Map;

/** Known item IDs for resources represented in the Resources tab. */
final class ResourceItemRegistry
{
    private static final Map<String, Integer> ITEM_IDS = Map.ofEntries(
            Map.entry("Demon tear", 31111),
            Map.entry("Air rune", 556),
            Map.entry("Abyssal whip", 4151),
            Map.entry("Amulet of bounty", 21160),
            Map.entry("Amulet of chemistry", 21163),
            Map.entry("Amulet of glory(6)", 11978),
            Map.entry("Ancient essence", 27616),
            Map.entry("Ancient shard", 19677),
            Map.entry("Blood rune", 565),
            Map.entry("Blood shard", 24777),
            Map.entry("Bracelet of slaughter", 21183),
            Map.entry("Burning amulet(5)", 21166),
            Map.entry("Burnt page", 20718),
            Map.entry("Chaos rune", 562),
            Map.entry("Combat bracelet(6)", 11972),
            Map.entry("Coins", 995),
            Map.entry("Cosmic rune", 564),
            Map.entry("Crystal shard", 23962),
            Map.entry("Death rune", 560),
            Map.entry("Dodgy necklace", 21143),
            Map.entry("Dragon axe", 6739),
            Map.entry("Dragon harpoon", 21028),
            Map.entry("Dragon pickaxe", 11920),
            Map.entry("Ecto-token", 4278),
            Map.entry("Earth rune", 557),
            Map.entry("Echo crystal", 28942),
            Map.entry("Efaritay's aid", 21140),
            Map.entry("Expeditious bracelet", 21177),
            Map.entry("Fire rune", 554),
            Map.entry("Flamtaer bracelet", 21180),
            Map.entry("Frozen tear", 29895),
            Map.entry("Games necklace(8)", 3853),
            Map.entry("Gryphon feather", 31235),
            Map.entry("Law rune", 563),
            Map.entry("Lizardman fang", 13391),
            Map.entry("Necklace of passage(5)", 21146),
            Map.entry("Revenant ether", 21820),
            Map.entry("Ring of dueling(8)", 2552),
            Map.entry("Ring of recoil", 2550),
            Map.entry("Ring of returning(5)", 21129),
            Map.entry("Ring of wealth (5)", 11980),
            Map.entry("Ruby necklace", 1660),
            Map.entry("Skills necklace(6)", 11968),
            Map.entry("Slayer's enchantment", 21257),
            Map.entry("Smouldering stone", 13233),
            Map.entry("Soaked page", 25578),
            Map.entry("Soul rune", 566),
            Map.entry("Stamina potion(4)", 12625),
            Map.entry("Stardust", 25527),
            Map.entry("Strange old lockpick (full)", 24740),
            Map.entry("Sunfire splinters", 28924),
            Map.entry("Teleport card", 13658),
            Map.entry("Soiled page", 30068),
            Map.entry("Vial of blood", 22446),
            Map.entry("Water rune", 555),
            Map.entry("Zulrah's scales", 12934)
    );

    private ResourceItemRegistry()
    {
    }

    static Integer getItemId(String itemName)
    {
        return ITEM_IDS.get(itemName);
    }
}
