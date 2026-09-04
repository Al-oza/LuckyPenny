package com.luckypenny;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import javax.swing.JComponent;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.QuantityFormatter;

/** A Loot Tracker-style resource cell with a quantity overlay and hover details. */
final class ResourceIconBadge extends JComponent
{
    private static final int SIZE = 42;
    private static final int COUNT_HEIGHT = 13;
    private static final int ICON_MAX_SIZE = 30;
    private static final DecimalFormat QUANTITY_FORMAT = new DecimalFormat("#,##0.##");

    private final BufferedImage icon;
    private final String quantityText;

    ResourceIconBadge(BufferedImage icon, String itemName, double quantity, long totalGp)
    {
        this.icon = icon;
        this.quantityText = QUANTITY_FORMAT.format(quantity);
        setPreferredSize(new Dimension(SIZE, SIZE));
        setToolTipText(buildTooltip(itemName, quantityText, totalGp));

        if (icon instanceof AsyncBufferedImage)
        {
            ((AsyncBufferedImage) icon).onLoaded(this::repaint);
        }
    }

    private static String buildTooltip(String itemName, String quantity, long totalGp)
    {
        StringBuilder tooltip = new StringBuilder("<html>")
                .append(quantity).append(' ').append(itemName);
        if (totalGp > 0)
        {
            tooltip.append("<br>~")
                    .append(QuantityFormatter.formatNumber(totalGp))
                    .append(" gp saved");
        }
        return tooltip.append("</html>").toString();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ColorScheme.DARK_GRAY_COLOR);
        g2.fillRect(0, 0, SIZE, SIZE);
        g2.setColor(ColorScheme.DARKER_GRAY_COLOR);
        g2.drawRect(0, 0, SIZE - 1, SIZE - 1);

        if (icon != null)
        {
            double scale = Math.min(1.0, Math.min(
                    (double) ICON_MAX_SIZE / icon.getWidth(),
                    (double) ICON_MAX_SIZE / icon.getHeight()));
            int width = (int) Math.round(icon.getWidth() * scale);
            int height = (int) Math.round(icon.getHeight() * scale);
            int x = (SIZE - width) / 2;
            int y = COUNT_HEIGHT + (SIZE - COUNT_HEIGHT - height) / 2;
            g2.drawImage(icon, x, y, width, height, null);
        }

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 11f));
        FontMetrics metrics = g2.getFontMetrics();
        int x = 1;
        int y = metrics.getAscent();
        g2.setColor(Color.BLACK);
        g2.drawString(quantityText, x - 1, y);
        g2.drawString(quantityText, x + 1, y);
        g2.drawString(quantityText, x, y - 1);
        g2.drawString(quantityText, x, y + 1);
        g2.setColor(Color.YELLOW);
        g2.drawString(quantityText, x, y);
        g2.dispose();
    }
}
