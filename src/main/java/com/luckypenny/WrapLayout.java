package com.luckypenny;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

/**
 * A FlowLayout that actually wraps correctly inside a scroll pane / resizable
 * panel (standard Swing's FlowLayout reports an incorrect preferred size in
 * that situation). Adapted from Rob Camick's public-domain WrapLayout
 * (https://tips4java.wordpress.com/2008/11/06/wrap-layout/).
 */
public class WrapLayout extends FlowLayout
{
    public WrapLayout(int align, int hgap, int vgap)
    {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target)
    {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target)
    {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    private Dimension layoutSize(Container target, boolean preferred)
    {
        synchronized (target.getTreeLock())
        {
            int targetWidth = target.getSize().width;
            Container container = target;

            while (container.getSize().width == 0 && container.getParent() != null)
            {
                container = container.getParent();
            }

            targetWidth = container.getSize().width;
            if (targetWidth == 0)
            {
                targetWidth = Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGap;

            int rowWidth = 0;
            int rowHeight = 0;
            int totalWidth = 0;
            int totalHeight = insets.top + insets.bottom;
            int componentCount = target.getComponentCount();

            for (int i = 0; i < componentCount; i++)
            {
                java.awt.Component m = target.getComponent(i);
                if (!m.isVisible())
                {
                    continue;
                }

                Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                if (rowWidth + d.width > maxWidth && rowWidth > 0)
                {
                    totalHeight += rowHeight + vgap;
                    totalWidth = Math.max(totalWidth, rowWidth);
                    rowWidth = 0;
                    rowHeight = 0;
                }

                rowWidth += d.width + hgap;
                rowHeight = Math.max(rowHeight, d.height);
            }

            totalHeight += rowHeight + vgap;
            totalWidth = Math.max(totalWidth, rowWidth);

            Dimension size = new Dimension(totalWidth + horizontalInsetsAndGap, totalHeight);
            return size;
        }
    }
}