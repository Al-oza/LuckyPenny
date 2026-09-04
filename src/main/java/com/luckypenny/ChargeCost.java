package com.luckypenny;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/** Describes the resources saved when one item charge is prevented. */
public abstract class ChargeCost
{
    public static final ChargeCost UNKNOWN = new ChargeCost()
    {
        @Override
        public boolean isKnown()
        {
            return false;
        }

        @Override
        public List<ResourceQty> getResources(GhommalLuckyPennyConfig config)
        {
            return Collections.emptyList();
        }
    };

    public static final ChargeCost FREE = new ChargeCost()
    {
        @Override
        public boolean isKnown()
        {
            return true;
        }

        @Override
        public List<ResourceQty> getResources(GhommalLuckyPennyConfig config)
        {
            return Collections.emptyList();
        }
    };

    public abstract boolean isKnown();

    public abstract List<ResourceQty> getResources(GhommalLuckyPennyConfig config);

    /** True only when every resource has a directly available GE price. */
    public boolean hasDirectGeValue(GameItemResolver resolver, GhommalLuckyPennyConfig config)
    {
        List<ResourceQty> resources = getResources(config);
        if (!isKnown() || resources.isEmpty())
        {
            return false;
        }

        for (ResourceQty resource : resources)
        {
            if (resolver.getPrice(resource.getItemName(), resource.getItemId()) <= 0)
            {
                return false;
            }
        }
        return true;
    }

    public long computeGp(GameItemResolver resolver, GhommalLuckyPennyConfig config)
    {
        double total = 0;
        for (ResourceQty resource : getResources(config))
        {
            total += resource.getQuantity()
                    * resolver.getPrice(resource.getItemName(), resource.getItemId());
        }
        return Math.round(total);
    }

    public static ChargeCost resources(ResourceQty... resources)
    {
        List<ResourceQty> list = Collections.unmodifiableList(Arrays.asList(resources));
        return configured(config -> list);
    }

    public static ChargeCost amortised(String rechargeItemName, int chargesRestored)
    {
        return resources(of(rechargeItemName, 1.0 / chargesRestored));
    }

    public static ChargeCost configured(Function<GhommalLuckyPennyConfig, List<ResourceQty>> resourceProvider)
    {
        return new ChargeCost()
        {
            @Override
            public boolean isKnown()
            {
                return true;
            }

            @Override
            public List<ResourceQty> getResources(GhommalLuckyPennyConfig config)
            {
                return resourceProvider.apply(config);
            }
        };
    }

    public static ChargeCost infernalAxe()
    {
        return configured(config -> Collections.singletonList(
                of(config.infernalAxeRecharge().getItemName(), 1.0 / 5000)));
    }

    public static ChargeCost infernalHarpoon()
    {
        return configured(config -> Collections.singletonList(
                of(config.infernalHarpoonRecharge().getItemName(), 1.0 / 5000)));
    }

    public static ChargeCost infernalPickaxe()
    {
        return configured(config -> Collections.singletonList(
                of(config.infernalPickaxeRecharge().getItemName(), 1.0 / 5000)));
    }

    public static ChargeCost eyeOfAyak()
    {
        return configured(config ->
        {
            if (config.ayakCharge().usesDemonTear())
            {
                return Collections.singletonList(of("Demon tear", 1));
            }

            return Arrays.asList(
                    of("Chaos rune", 1),
                    of("Death rune", 2)
            );
        });
    }

    public static ChargeCost pharaohsSceptre()
    {
        return configured(config -> Collections.singletonList(of(
                config.pharaohsSceptreArtefact().getItemName(),
                (double) config.pharaohsSceptreArtefact().getQuantityPerRecharge()
                        / config.desertDiary().getChargesPerRecharge(),
                config.pharaohsSceptreArtefact().getItemId()
        )));
    }

    public static ResourceQty of(String itemName, double quantity)
    {
        return new ResourceQty(itemName, quantity, ResourceItemRegistry.getItemId(itemName));
    }

    public static ResourceQty of(String itemName, double quantity, int itemId)
    {
        return new ResourceQty(itemName, quantity, itemId);
    }

    public static class ResourceQty
    {
        private final String itemName;
        private final double quantity;
        private final Integer itemId;

        ResourceQty(String itemName, double quantity, Integer itemId)
        {
            this.itemName = itemName;
            this.quantity = quantity;
            this.itemId = itemId;
        }

        public String getItemName()
        {
            return itemName;
        }

        public double getQuantity()
        {
            return quantity;
        }

        public Integer getItemId()
        {
            return itemId;
        }
    }
}
