package com.luckypenny;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static com.luckypenny.ChargeCost.of;

public class ChargeItemRegistry
{
    private static final List<ChargeItemDefinition> ITEMS = new ArrayList<>();

    private static final Map<String, Integer> DISPLAY_ITEM_IDS = Map.ofEntries(
            Map.entry("Celestial signet", 25545),
            Map.entry("Bonecrusher", 13116),
            Map.entry("Bonecrusher necklace", 22986),
            Map.entry("Celestial ring", 25541),
            Map.entry("Abyssal tentacle", 12006),
            Map.entry("Craw's bow", 22550),
            Map.entry("Webweaver bow", 27655),
            Map.entry("Thammaron's sceptre", 22555),
            Map.entry("Thammaron's sceptre (a)", 27788),
            Map.entry("Accursed sceptre", 27665),
            Map.entry("Accursed sceptre (a)", 27679),
            Map.entry("Viggora's chainmace", 22545),
            Map.entry("Ursine chainmace", 27660),
            Map.entry("Arclight", 19675),
            Map.entry("Bone staff", 28796),
            Map.entry("Eye of Ayak", 31113),
            Map.entry("Sanguinesti staff", 22323),
            Map.entry("Holy sanguinesti staff", 25731),
            Map.entry("Scythe of Vitur", 22325),
            Map.entry("Sanguine scythe of Vitur", 22664),
            Map.entry("Tonalztics of Ralos", 28922),
            Map.entry("Toxic blowpipe", 12926),
            Map.entry("Slayer's staff (e)", 21255),
            Map.entry("Warped sceptre", 28585),
            Map.entry("Trident of the seas", 11907),
            Map.entry("Trident of the swamp", 12899),
            Map.entry("Tumeken's shadow", 27275),
            Map.entry("Venator bow", 27610),
            Map.entry("Chronicle", 13660),
            Map.entry("Circlet of water", 26969),
            Map.entry("Blade of Saeldor", 23995),
            Map.entry("Bow of Faerdhinen", 25865),
            Map.entry("Crystal axe", 23673),
            Map.entry("Crystal felling axe", 28220),
            Map.entry("Crystal body", 23975),
            Map.entry("Crystal helm", 23971),
            Map.entry("Crystal legs", 23979),
            Map.entry("Crystal bow", 23983),
            Map.entry("Crystal halberd", 23987),
            Map.entry("Crystal harpoon", 23762),
            Map.entry("Crystal pickaxe", 23680),
            Map.entry("Crystal shield", 23991),
            Map.entry("Dizana's quiver", 28951),
            Map.entry("Echo boots", 28945),
            Map.entry("Horn of Plenty", 31241),
            Map.entry("Infernal axe", 13241),
            Map.entry("Infernal axe (or)", 25066),
            Map.entry("Infernal harpoon", 21031),
            Map.entry("Infernal harpoon (or)", 25059),
            Map.entry("Infernal pickaxe", 13243),
            Map.entry("Infernal pickaxe (or)", 25063),
            Map.entry("Alchemist's amulet", 29988),
            Map.entry("Amulet of chemistry", 21163),
            Map.entry("Amulet of bounty", 21160),
            Map.entry("Amulet of glory", 1704),
            Map.entry("Bracelet of ethereum", 21816),
            Map.entry("Bracelet of slaughter", 21183),
            Map.entry("Combat bracelet", 11126),
            Map.entry("Cowbell amulet", 33104),
            Map.entry("Dodgy necklace", 21143),
            Map.entry("Digsite pendant", 11194),
            Map.entry("Efaritay's aid", 21140),
            Map.entry("Expeditious bracelet", 21177),
            Map.entry("Flamtaer bracelet", 21180),
            Map.entry("Games necklace", 3857),
            Map.entry("Necklace of passage", 21146),
            Map.entry("Pendant of ates", 29893),
            Map.entry("Ring of dueling", 2560),
            Map.entry("Ring of endurance", 24736),
            Map.entry("Ring of recoil", 2550),
            Map.entry("Ring of suffering (r)", 20655),
            Map.entry("Ring of returning", 21132),
            Map.entry("Ring of wealth", 11982),
            Map.entry("Skills necklace", 11968),
            Map.entry("Xeric's talisman", 13393),
            Map.entry("Pharaoh's sceptre (uncharged)", 26945),
            Map.entry("Sailors' amulet", 32399),
            Map.entry("Serpentine helm", 12931),
            Map.entry("Strange old lockpick", 24738),
            Map.entry("Tome of Earth", 30064),
            Map.entry("Tome of Fire", 20714),
            Map.entry("Tome of Water", 25574),
            Map.entry("Blazing blowpipe", 28688)
    );

    private static void add(String name, ChargeCost cost, String... aliases)
    {
        ITEMS.add(new ChargeItemDefinition(name, cost, DISPLAY_ITEM_IDS.get(name), aliases));
    }

    static
    {
        add("Sanguinesti staff", ChargeCost.resources(of("Blood rune", 2)));
        add("Holy sanguinesti staff", ChargeCost.resources(of("Blood rune", 2)));
        add("Trident of the seas", ChargeCost.resources(of("Fire rune", 5), of("Chaos rune", 1), of("Coins", 10)));
        add("Trident of the swamp", ChargeCost.resources(of("Fire rune", 5), of("Chaos rune", 1), of("Death rune", 1), of("Zulrah's scales", 1)));
        add("Tumeken's shadow", ChargeCost.resources(of("Chaos rune", 5), of("Soul rune", 2)));
        add("Warped sceptre", ChargeCost.resources(of("Earth rune", 5), of("Chaos rune", 2)));
        // Eye of Ayak can alternatively be charged with 1x Demon tear instead of runes - this uses the rune cost as the default estimate.
        add("Eye of Ayak", ChargeCost.resources(of("Chaos rune", 1), of("Death rune", 2)));
        add("Toxic blowpipe", ChargeCost.resources(of("Zulrah's scales", 2.0 / 3.0)));
        add("Blazing blowpipe", ChargeCost.resources(of("Zulrah's scales", 2.0 / 3.0)));
        add("Slayer's staff (e)", ChargeCost.amortised("Slayer's enchantment", 2500));
        add("Amulet of eternal glory", ChargeCost.FREE);
        add("Ash sanctifier", ChargeCost.amortised("Death rune", 10));
        add("Amulet of blood fury", ChargeCost.amortised("Blood shard", 10_000));
        add("Scythe of Vitur", ChargeCost.resources(of("Blood rune", 2), of("Vial of blood", 0.01)));
        add("Sanguine scythe of Vitur", ChargeCost.resources(of("Blood rune", 2), of("Vial of blood", 0.01)));
        add("Serpentine helm", ChargeCost.resources(of("Zulrah's scales", 1)));
        add("Craw's bow", ChargeCost.resources(of("Revenant ether", 1)));
        add("Webweaver bow", ChargeCost.resources(of("Revenant ether", 1)));
        add("Accursed sceptre", ChargeCost.resources(of("Revenant ether", 1)));
        add("Accursed sceptre (a)", ChargeCost.resources(of("Revenant ether", 1)));
        add("Thammaron's sceptre", ChargeCost.resources(of("Revenant ether", 1)));
        add("Thammaron's sceptre (a)", ChargeCost.resources(of("Revenant ether", 1)));
        add("Viggora's chainmace", ChargeCost.resources(of("Revenant ether", 1)));
        add("Ursine chainmace", ChargeCost.resources(of("Revenant ether", 1)));
        add("Tome of Fire", ChargeCost.amortised("Burnt page", 20));
        add("Tome of Water", ChargeCost.amortised("Soaked page", 20));
        add("Tome of Earth", ChargeCost.amortised("Soiled page", 20));
        add("Ring of suffering (r)", ChargeCost.amortised("Ring of recoil", 40));
        add("Abyssal tentacle", ChargeCost.amortised("Abyssal whip", 10_000));
        add("Abyssal tentacle (or)", ChargeCost.amortised("Abyssal whip", 10_000));
        add("Bone staff", ChargeCost.resources(of("Chaos rune", 1)));
        add("Tonalztics of Ralos", ChargeCost.resources(of("Sunfire splinters",1)));
        add("Venator bow", ChargeCost.resources(of("Ancient essence",1)));
        add("Chronicle", ChargeCost.resources(of("Teleport card",1)));
        add("Circlet of water", ChargeCost.resources(of("Water rune",5)));
        add("Dizana's quiver", ChargeCost.resources(of("Sunfire splinters",1)));
        add("Bonecrusher", ChargeCost.amortised("Ecto-token", 25));
        add("Bonecrusher necklace", ChargeCost.amortised("Ecto-token", 25));
        add("Celestial ring", ChargeCost.resources(of("Stardust", 1)));
        add("Celestial signet", ChargeCost.resources(of("Stardust", 1)));
        add("Arclight", ChargeCost.amortised("Ancient shard", 333));
        add("Blade of Saeldor", ChargeCost.amortised("Crystal shard", 100));
        add("Bow of Faerdhinen", ChargeCost.amortised("Crystal shard", 100));
        add("Crystal axe", ChargeCost.amortised("Crystal shard", 100));
        add("Crystal felling axe", ChargeCost.amortised("Crystal shard", 100));
        add("Crystal body", ChargeCost.amortised("Crystal shard", 100));
        add("Crystal helm", ChargeCost.amortised("Crystal shard", 100));
        add("Crystal legs", ChargeCost.amortised("Crystal shard", 100));
        add("Crystal bow", ChargeCost.amortised("Crystal shard", 100));
        add("Crystal halberd", ChargeCost.amortised("Crystal shard", 100));
        add("Crystal harpoon", ChargeCost.amortised("Crystal shard", 100));
        add("Crystal pickaxe", ChargeCost.amortised("Crystal shard", 100));
        add("Crystal shield", ChargeCost.resources(of("Crystal shard", 40.0 / 2_500)));
        add("Amulet of glory", ChargeCost.amortised("Amulet of glory(6)", 6), "Amulet of glory (t)");
        add("Echo boots", ChargeCost.amortised("Echo crystal", 6_000));
        add("Horn of Plenty", ChargeCost.resources(of("Gryphon feather",1)));
        add("Infernal axe", ChargeCost.infernalAxe());
        add("Infernal axe (or)", ChargeCost.infernalAxe());
        add("Infernal harpoon", ChargeCost.infernalHarpoon());
        add("Infernal harpoon (or)", ChargeCost.infernalHarpoon());
        add("Infernal pickaxe", ChargeCost.infernalPickaxe());
        add("Infernal pickaxe (or)", ChargeCost.infernalPickaxe());
        add("Alchemist's amulet", ChargeCost.amortised("Amulet of chemistry", 10));
        add("Amulet of chemistry", ChargeCost.amortised("Amulet of chemistry", 5));
        add("Amulet of bounty", ChargeCost.amortised("Amulet of bounty", 10));
        add("Bracelet of ethereum", ChargeCost.resources(of("Revenant ether", 1)));
        add("Bracelet of slaughter", ChargeCost.amortised("Bracelet of slaughter", 30));
        add("Burning amulet", ChargeCost.amortised("Burning amulet(5)", 5));
        add("Combat bracelet", ChargeCost.amortised("Combat bracelet(6)", 6));
        add("Cowbell amulet", ChargeCost.resources(of("Air rune", 1)));
        add("Digsite pendant", ChargeCost.resources(of("Ruby necklace", 1.0 / 5), of("Fire rune", 1), of("Cosmic rune", 1.0 / 5)));
        add("Dodgy necklace", ChargeCost.amortised("Dodgy necklace", 10));
        add("Efaritay's aid", ChargeCost.amortised("Efaritay's aid", 200));
        add("Expeditious bracelet", ChargeCost.amortised("Expeditious bracelet", 30));
        add("Flamtaer bracelet", ChargeCost.amortised("Flamtaer bracelet", 80));
        add("Games necklace", ChargeCost.amortised("Games necklace(8)", 8));
        add("Necklace of passage", ChargeCost.amortised("Necklace of passage(5)", 5));
        add("Pendant of ates", ChargeCost.resources(of("Frozen tear", 1)));
        add("Ring of dueling", ChargeCost.amortised("Ring of dueling(8)", 8));
        add("Ring of endurance", ChargeCost.amortised("Stamina potion(4)", 4));
        add("Ring of recoil", ChargeCost.amortised("Ring of recoil", 40));
        add("Ring of returning", ChargeCost.amortised("Ring of returning(5)", 5));
        add("Ring of wealth", ChargeCost.amortised("Ring of wealth (5)", 5));
        add("Skills necklace", ChargeCost.amortised("Skills necklace(6)", 6));
        add("Xeric's talisman", ChargeCost.resources(of("Lizardman fang", 1)));
        add("Pharaoh's sceptre (uncharged)", ChargeCost.pharaohsSceptre(), "Pharaoh's sceptre");
        add("Sailors' amulet", ChargeCost.resources(of("Law rune", 0.1), of("Water rune", 1)));
        add("Strange old lockpick", ChargeCost.amortised("Strange old lockpick (full)", 50));
    }

    /** Finds the registered definition matching a name captured from chat, or null. */
    public static ChargeItemDefinition match(String chatName)
    {
        for (ChargeItemDefinition def : ITEMS)
        {
            if (def.matches(chatName))
            {
                return def;
            }
        }
        return null;
    }
}