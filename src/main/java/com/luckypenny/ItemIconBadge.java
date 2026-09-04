package com.luckypenny;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.QuantityFormatter;

/**
 * An item icon with a yellow saved-charge count drawn in the top-left
 * corner, matching the look of in-game stack size numbers. Hovering shows
 * the item name, how many charges were saved, and (if the cost is known)
 * approximately how much gp that's worth.
 */
public class ItemIconBadge extends JComponent
{
    private static final int SIZE = 42;
    private static final int COUNT_HEIGHT = 13;
    private static final int ICON_MAX_SIZE = 30;

    private final BufferedImage icon;
    private final int count;

    ItemIconBadge(BufferedImage icon, int count, String itemName, long perChargeGp, boolean costKnown)
    {
        this.icon = icon;
        this.count = count;

        setPreferredSize(new Dimension(SIZE, SIZE));
        setToolTipText(buildTooltip(itemName, count, perChargeGp, costKnown));

        if (icon instanceof AsyncBufferedImage)
        {
            ((AsyncBufferedImage) icon).onLoaded(this::repaint);
        }
    }

    private static String buildTooltip(String itemName, int count, long perChargeGp, boolean costKnown)
    {
        StringBuilder sb = new StringBuilder("<html>").append(itemName).append("<br>")
                .append(count).append(count == 1 ? " charge saved" : " charges saved");

        if (costKnown && perChargeGp > 0)
        {
            long total = perChargeGp * count;
            sb.append("<br>~").append(QuantityFormatter.formatNumber(total)).append(" gp saved");
        }
        else if (!costKnown)
        {
            sb.append("<br>No GE price for the required resource");
        }

        return sb.append("</html>").toString();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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

        String text = String.valueOf(count);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 11f));
        FontMetrics fm = g2.getFontMetrics();
        int tx = 1;
        int ty = fm.getAscent();

        g2.setColor(ColorScheme.DARK_GRAY_COLOR);
        g2.fillRect(0, 0, fm.stringWidth(text) + 3, COUNT_HEIGHT);

        g2.setColor(Color.BLACK);
        g2.drawString(text, tx - 1, ty);
        g2.drawString(text, tx + 1, ty);
        g2.drawString(text, tx, ty - 1);
        g2.drawString(text, tx, ty + 1);

        g2.setColor(Color.YELLOW);
        g2.drawString(text, tx, ty);

        g2.dispose();
    }
}
