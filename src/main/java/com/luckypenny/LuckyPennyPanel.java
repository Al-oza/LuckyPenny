package com.luckypenny;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.QuantityFormatter;

public class LuckyPennyPanel extends PluginPanel
{
    private static final DecimalFormat RESOURCE_QUANTITY_FORMAT = new DecimalFormat("#,##0.##");

    private final GameItemResolver resolver;
    private final GhommalLuckyPennyConfig config;
    private final JLabel totalChargesLabel = new JLabel();
    private final JLabel totalSavingsLabel = new JLabel();
    private final JPanel itemGrid = new JPanel();
    private final JPanel resourceList = new JPanel();

    LuckyPennyPanel(GameItemResolver resolver, GhommalLuckyPennyConfig config)
    {
        super(false);
        this.resolver = resolver;
        this.config = config;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JPanel buildHeader()
    {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel title = new JLabel("Total Savings");
        title.setFont(FontManager.getRunescapeBoldFont());
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        return header;
    }

    private JPanel buildBody()
    {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(ColorScheme.DARK_GRAY_COLOR);
        body.add(buildStatsCard(), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(ColorScheme.DARK_GRAY_COLOR);
        content.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        itemGrid.setBackground(ColorScheme.DARK_GRAY_COLOR);

        resourceList.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(ColorScheme.DARK_GRAY_COLOR);
        tabs.setForeground(Color.WHITE);
        JPanel chargeContent = new JPanel(new BorderLayout());
        chargeContent.setBackground(ColorScheme.DARK_GRAY_COLOR);
        chargeContent.add(itemGrid, BorderLayout.NORTH);
        JScrollPane chargeScroll = new JScrollPane(chargeContent);
        chargeScroll.setBorder(null);
        chargeScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chargeScroll.getViewport().setBackground(ColorScheme.DARK_GRAY_COLOR);
        tabs.addTab("Charges", chargeScroll);
        JPanel resourceContent = new JPanel(new BorderLayout());
        resourceContent.setBackground(ColorScheme.DARK_GRAY_COLOR);
        resourceContent.add(resourceList, BorderLayout.NORTH);
        JScrollPane resourceScroll = new JScrollPane(resourceContent);
        resourceScroll.setBorder(null);
        resourceScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resourceScroll.getViewport().setBackground(ColorScheme.DARK_GRAY_COLOR);
        tabs.addTab("Resources", resourceScroll);
        content.add(tabs, BorderLayout.CENTER);

        body.add(content, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildStatsCard()
    {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel gpIcon = new JLabel();
        BufferedImage coinsImage = resolver.getIcon("Polished buttons", 10_000_000);
        if (coinsImage != null)
        {
            gpIcon.setIcon(new ImageIcon(coinsImage));
        }
        card.add(gpIcon, BorderLayout.WEST);

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        totalChargesLabel.setForeground(Color.LIGHT_GRAY);
        totalSavingsLabel.setForeground(Color.LIGHT_GRAY);
        text.add(totalChargesLabel);
        text.add(totalSavingsLabel);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    /** Must be called on RuneLite's client thread. */
    void rebuild(Map<String, Integer> savedCounts)
    {
        List<ItemData> items = new ArrayList<>();
        Map<String, ResourceData> resources = new LinkedHashMap<>();
        int totalCharges = 0;
        long totalGp = 0;

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(savedCounts.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        for (Map.Entry<String, Integer> entry : sortedEntries)
        {
            String itemName = entry.getKey();
            int count = entry.getValue();
            ChargeItemDefinition definition = ChargeItemRegistry.match(itemName);
            ChargeCost cost = definition == null ? ChargeCost.UNKNOWN : definition.getCost();
            Integer itemId = definition == null ? null : definition.getItemId();
            boolean hasDirectGeValue = cost.hasDirectGeValue(resolver, config);

            if (config.hideUnpricedItems() && !hasDirectGeValue)
            {
                continue;
            }

            long perChargeGp = hasDirectGeValue ? cost.computeGp(resolver, config) : 0L;
            items.add(new ItemData(itemName, count, perChargeGp, hasDirectGeValue,
                    resolver.getIcon(itemName, count, count > 1, itemId)));
            totalCharges += count;
            totalGp += perChargeGp * count;

            for (ChargeCost.ResourceQty resource : cost.getResources(config))
            {
                ResourceData resourceData = resources.computeIfAbsent(
                        resource.getItemName(), ignored -> new ResourceData(
                                resource.getItemName(), resource.getItemId()));
                resourceData.quantity += resource.getQuantity() * count;
            }
        }

        for (ResourceData resource : resources.values())
        {
            resource.price = resolver.getPrice(resource.itemName, resource.itemId);
            int iconQuantity = resource.getDisplayQuantity();
            resource.icon = resolver.getIcon(resource.itemName, iconQuantity,
                    iconQuantity > 1, resource.itemId);
        }

        List<ResourceData> sortedResources = new ArrayList<>(resources.values());
        sortedResources.sort((first, second) ->
        {
            long firstGp = Math.round(first.quantity * first.price);
            long secondGp = Math.round(second.quantity * second.price);

            int byGp = Long.compare(secondGp, firstGp);
            return byGp != 0
                    ? byGp
                    : first.itemName.compareTo(second.itemName);
        });
        int finalTotalCharges = totalCharges;
        long finalTotalGp = totalGp;

        SwingUtilities.invokeLater(() ->
        {
            itemGrid.removeAll();
            resourceList.removeAll();

            for (ItemData item : items)
            {
                itemGrid.add(buildLootStyleSlot(item.icon, item.buildTooltip()));
            }

            fillLootStyleGrid(itemGrid, items.size());

            buildResourceGrid(sortedResources);

            totalChargesLabel.setText("Total charges saved: "
                    + QuantityFormatter.formatNumber(finalTotalCharges));
            totalSavingsLabel.setText("Total savings: "
                    + QuantityFormatter.formatNumber(finalTotalGp) + " gp");

            itemGrid.revalidate();
            itemGrid.repaint();
            resourceList.revalidate();
            resourceList.repaint();
        });
    }

    private void buildResourceGrid(List<ResourceData> resources)
    {
        setLootStyleGridLayout(resourceList, resources.size());

        for (ResourceData resource : resources)
        {
            resourceList.add(buildLootStyleSlot(resource.icon, resource.buildTooltip()));
        }

        fillLootStyleGrid(resourceList, resources.size());
    }

    private void setLootStyleGridLayout(JPanel grid, int itemCount)
    {
        final int itemsPerRow = 5;
        int rowCount = Math.max(1, (itemCount + itemsPerRow - 1) / itemsPerRow);
        grid.setLayout(new GridLayout(rowCount, itemsPerRow, 1, 1));
    }

    private void fillLootStyleGrid(JPanel grid, int itemCount)
    {
        setLootStyleGridLayout(grid, itemCount);
        final int itemsPerRow = 5;
        int totalSlots = Math.max(itemsPerRow,
                ((itemCount + itemsPerRow - 1) / itemsPerRow) * itemsPerRow);
        while (grid.getComponentCount() < totalSlots)
        {
            JPanel emptySlot = new JPanel();
            emptySlot.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            emptySlot.setPreferredSize(new Dimension(42, 42));
            grid.add(emptySlot);
        }
    }

    private JPanel buildLootStyleSlot(BufferedImage icon, String tooltip)
    {
        JPanel slot = new JPanel(new GridLayout(1, 1));
        slot.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        slot.setPreferredSize(new Dimension(42, 42));

        JLabel image = new JLabel();
        image.setHorizontalAlignment(JLabel.CENTER);
        image.setVerticalAlignment(JLabel.CENTER);
        image.setToolTipText(tooltip);
        if (icon instanceof AsyncBufferedImage)
        {
            ((AsyncBufferedImage) icon).addTo(image);
        }
        else if (icon != null)
        {
            image.setIcon(new ImageIcon(icon));
        }
        slot.add(image);
        return slot;
    }

    private static class ItemData
    {
        private final String itemName;
        private final int count;
        private final long perChargeGp;
        private final boolean hasDirectGeValue;
        private final BufferedImage icon;

        private ItemData(String itemName, int count, long perChargeGp,
                boolean hasDirectGeValue, BufferedImage icon)
        {
            this.itemName = itemName;
            this.count = count;
            this.perChargeGp = perChargeGp;
            this.hasDirectGeValue = hasDirectGeValue;
            this.icon = icon;
        }

        private String buildTooltip()
        {
            StringBuilder tooltip = new StringBuilder("<html>")
                    .append(itemName).append("<br>")
                    .append(count).append(count == 1 ? " charge saved" : " charges saved");
            if (hasDirectGeValue && perChargeGp > 0)
            {
                tooltip.append("<br>~")
                        .append(QuantityFormatter.formatNumber(perChargeGp * count))
                        .append(" gp saved");
            }
            return tooltip.append("</html>").toString();
        }

    }

    private static class ResourceData
    {
        private final String itemName;
        private final Integer itemId;
        private double quantity;
        private long price;
        private BufferedImage icon;

        private ResourceData(String itemName, Integer itemId)
        {
            this.itemName = itemName;
            this.itemId = itemId;
        }

        /** Rounded only for RuneLite's integer stack overlay. */
        private int getDisplayQuantity()
        {
            return (int) Math.max(1, Math.min(Math.round(quantity), Integer.MAX_VALUE));
        }

        private String buildTooltip()
        {
            StringBuilder tooltip = new StringBuilder("<html>")
                    .append(RESOURCE_QUANTITY_FORMAT.format(quantity))
                    .append(' ').append(itemName);
            if (price > 0)
            {
                tooltip.append("<br>~")
                        .append(QuantityFormatter.formatNumber(Math.round(quantity * price)))
                        .append(" gp saved");
            }
            return tooltip.append("</html>").toString();
        }

    }
}
