package profiler_app;

import java.awt.*;
import javax.swing.border.Border;

public class DropShadowBorder implements Border {
    private Color shadowColor;
    private int shadowSize;
    private float shadowOpacity;
    private int cornerRadius;
    private boolean showTopShadow;
    private boolean showLeftShadow;
    private boolean showBottomShadow;
    private boolean showRightShadow;

    public DropShadowBorder(Color shadowColor, int shadowSize, float shadowOpacity, int cornerRadius,
                            boolean showTopShadow, boolean showLeftShadow, boolean showBottomShadow, boolean showRightShadow) {
        this.shadowColor = shadowColor;
        this.shadowSize = shadowSize;
        this.shadowOpacity = shadowOpacity;
        this.cornerRadius = cornerRadius;
        this.showTopShadow = showTopShadow;
        this.showLeftShadow = showLeftShadow;
        this.showBottomShadow = showBottomShadow;
        this.showRightShadow = showRightShadow;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color color = new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(),
                (int) (shadowColor.getAlpha() * shadowOpacity));
        int offset = shadowSize;
        if (showTopShadow) g2.fillRect(x, y, width, offset);
        if (showLeftShadow) g2.fillRect(x, y, offset, height);
        if (showBottomShadow) g2.fillRect(x, y + height - offset, width, offset);
        if (showRightShadow) g2.fillRect(x + width - offset, y, offset, height);
        g2.setColor(color);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(showTopShadow ? shadowSize : 0, showLeftShadow ? shadowSize : 0,
                showBottomShadow ? shadowSize : 0, showRightShadow ? shadowSize : 0);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}