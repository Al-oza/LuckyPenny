package com.luckypenny;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(GhommalLuckyPennyConfig.GROUP)
public interface GhommalLuckyPennyConfig extends Config
{
	String GROUP = "ghommalluckypenny";
	String INFERNAL_SECTION = "infernalToolCharging";
	String SCEPTRE_SECTION = "pharaohsSceptreCharging";
	String AYAK_SECTION = "ayakCharging";

	@ConfigSection(
			name = "Infernal Tool Charging",
			description = "Choose the resource used for each tool's 5,000-charge recharge",
			position = 1
	)
	@SuppressWarnings("unused")
	String infernalSection = INFERNAL_SECTION;

	@ConfigSection(
			name = "Pharaoh's Sceptre Charging",
			description = "Choose the recharge artefact and Desert Diary tier",
			position = 5
	)
	@SuppressWarnings("unused")
	String sceptreSection = SCEPTRE_SECTION;

	@ConfigSection(
			name = "Eye of Ayak Charging",
			description = "Choose the method for charging the powered staff",
			position = 8
	)
	@SuppressWarnings("unused")
	String ayakSection = AYAK_SECTION;

	@ConfigItem(
			keyName = "hideUnpricedItems",
			name = "Hide unpriced items",
			description = "Hide items whose saved charge cannot be given a direct Grand Exchange value"
	)
	default boolean hideUnpricedItems()
	{
		return false;
	}

	@ConfigItem(
			keyName = "infernalAxeRecharge",
			name = "Axe",
			description = "Resource used to recharge your infernal axe",
			section = INFERNAL_SECTION,
			position = 2
	)
	default InfernalAxeRecharge infernalAxeRecharge()
	{
		return InfernalAxeRecharge.DRAGON_AXE;
	}

	@ConfigItem(
			keyName = "infernalHarpoonRecharge",
			name = "Harpoon",
			description = "Resource used to recharge your infernal harpoon",
			section = INFERNAL_SECTION,
			position = 3
	)
	default InfernalHarpoonRecharge infernalHarpoonRecharge()
	{
		return InfernalHarpoonRecharge.DRAGON_HARPOON;
	}

	@ConfigItem(
			keyName = "infernalPickaxeRecharge",
			name = "Pickaxe",
			description = "Resource used to recharge your infernal pickaxe",
			section = INFERNAL_SECTION,
			position = 4
	)
	default InfernalPickaxeRecharge infernalPickaxeRecharge()
	{
		return InfernalPickaxeRecharge.DRAGON_PICKAXE;
	}

	@ConfigItem(
			keyName = "pharaohsSceptreArtefact",
			name = "Artefact",
			description = "Artefact used for each Pharaoh's sceptre recharge",
			section = SCEPTRE_SECTION,
			position = 6
	)
	default PharaohsSceptreArtefact pharaohsSceptreArtefact()
	{
		return PharaohsSceptreArtefact.STONE_SEAL;
	}

	@ConfigItem(
			keyName = "desertDiary",
			name = "Desert Diary",
			description = "Charges restored by a full Pharaoh's sceptre recharge",
			section = SCEPTRE_SECTION,
			position = 7
	)
	default DesertDiary desertDiary()
	{
		return DesertDiary.NONE;
	}

	@ConfigItem(
			keyName = "ayakCharge",
			name = "Ayak Charge Method",
			description = "Method of charging the Eye of Ayak.",
			section = AYAK_SECTION,
			position = 9
	)
	default AyakCharge ayakCharge()
	{
		return AyakCharge.RUNES;
	}

	enum InfernalAxeRecharge
	{
		DRAGON_AXE("Dragon axe"),
		SMOULDERING_STONE("Smouldering stone");

		private final String itemName;

		InfernalAxeRecharge(String itemName)
		{
			this.itemName = itemName;
		}

		String getItemName()
		{
			return itemName;
		}

		@Override
		public String toString()
		{
			return itemName;
		}
	}

	enum InfernalHarpoonRecharge
	{
		DRAGON_HARPOON("Dragon harpoon"),
		SMOULDERING_STONE("Smouldering stone");

		private final String itemName;

		InfernalHarpoonRecharge(String itemName)
		{
			this.itemName = itemName;
		}

		String getItemName()
		{
			return itemName;
		}

		@Override
		public String toString()
		{
			return itemName;
		}
	}

	enum InfernalPickaxeRecharge
	{
		DRAGON_PICKAXE("Dragon pickaxe"),
		SMOULDERING_STONE("Smouldering stone");

		private final String itemName;

		InfernalPickaxeRecharge(String itemName)
		{
			this.itemName = itemName;
		}

		String getItemName()
		{
			return itemName;
		}

		@Override
		public String toString()
		{
			return itemName;
		}
	}

	enum AyakCharge
	{
		RUNES("Runes"),
		DEMON_TEAR("Demon tear");

		private final String displayName;

		AyakCharge(String displayName)
		{
			this.displayName = displayName;
		}

		boolean usesDemonTear()
		{
			return this == DEMON_TEAR;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	enum PharaohsSceptreArtefact
	{
		// These legacy Pyramid Plunder item IDs have no generated ItemID constants.
		IVORY_COMB("Ivory comb", 24, 9026),
		POTTERY_SCARAB("Pottery scarab", 24, 9032),
		POTTERY_STATUETTE("Pottery statuette", 24, 9036),
		STONE_SEAL("Stone seal", 12, 9042),
		STONE_SCARAB("Stone scarab", 12, 9030),
		STONE_STATUETTE("Stone statuette", 12, 9038),
		GOLD_SEAL("Gold seal", 6, 9040),
		GOLDEN_SCARAB("Golden scarab", 6, 9028),
		GOLDEN_STATUETTE("Golden statuette", 6, 9034);

		private final String itemName;
		private final int quantityPerRecharge;
		private final int itemId;

		PharaohsSceptreArtefact(String itemName, int quantityPerRecharge, int itemId)
		{
			this.itemName = itemName;
			this.quantityPerRecharge = quantityPerRecharge;
			this.itemId = itemId;
		}

		String getItemName()
		{
			return itemName;
		}

		int getQuantityPerRecharge()
		{
			return quantityPerRecharge;
		}

		int getItemId()
		{
			return itemId;
		}

		@Override
		public String toString()
		{
			return itemName;
		}
	}

	enum DesertDiary
	{
		NONE("None", 3),
		EASY("Easy", 10),
		MEDIUM("Medium", 25),
		HARD("Hard", 50),
		ELITE("Elite", 100);

		private final String displayName;
		private final int chargesPerRecharge;

		DesertDiary(String displayName, int chargesPerRecharge)
		{
			this.displayName = displayName;
			this.chargesPerRecharge = chargesPerRecharge;
		}

		int getChargesPerRecharge()
		{
			return chargesPerRecharge;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

}
