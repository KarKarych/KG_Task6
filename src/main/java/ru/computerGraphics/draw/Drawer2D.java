package ru.computerGraphics.draw;


import ru.computerGraphics.model.PointDouble;

import java.awt.*;

import static ru.computerGraphics.db.LocalDataBase.*;

public class Drawer2D {
  private final Graphics2D g2d;
  private final int xCenter;
  private final int yCenter;

  public Drawer2D(Graphics2D g2d, int width, int height) {
    this.g2d = g2d;
    this.xCenter = width / 2;
    this.yCenter = height / 2;
  }

  public void drawLineFrameActive(PointDouble pointDoubleCurrent, PointDouble pointDoubleNext) {
    drawLineFrame(applySecond(pointDoubleCurrent), applySecond(pointDoubleNext), true);
  }

  public void drawLineFrameInactive(PointDouble pointDoubleCurrent, PointDouble pointDoubleNext) {
    drawLineFrame(applySecond(pointDoubleCurrent), applySecond(pointDoubleNext), false);
  }

  public void drawLineFrame(PointDouble pointDoubleCurrent, PointDouble pointDoubleNext, boolean isActive) {
    if (isActive) {
      g2d.setColor(frameEdgeColor);
      g2d.setStroke(new BasicStroke(1.15F));
    } else {
      Color color = new Color(frameEdgeColor.getRed(),
              frameEdgeColor.getGreen(),
              frameEdgeColor.getBlue(), 15);
      g2d.setColor(color);
      g2d.setStroke(new BasicStroke(1F));
    }

    g2d.drawLine((int) pointDoubleCurrent.x, (int) pointDoubleCurrent.y, (int) pointDoubleNext.x, (int) pointDoubleNext.y);
  }

  public void drawVertexFrame(PointDouble p, boolean isActive, boolean isPressed) {
    Color colorPressed = new Color(vertexColor.getRed(), vertexColor.getGreen(), vertexColor.getBlue(), 255);
    p = applySecond(p);
    if (isActive) {
      g2d.setColor(isPressed ? colorPressed : vertexColor);
    } else {
      Color color = new Color(vertexColor.getRed(),
              vertexColor.getGreen(),
              vertexColor.getBlue(), 50);

      g2d.setColor(isPressed ? colorPressed : color);
    }


    g2d.fillOval((int) p.x - radiusOfCircle, (int) p.y - radiusOfCircle,
            diameterOfCircle, diameterOfCircle);
  }

  public void drawLineCurveActive(PointDouble p1, PointDouble p2, Color currentColor) {
    drawLineCurve(applySecond(p1), applySecond(p2), currentColor, true);
  }

  public void drawLineCurveInactive(PointDouble p1, PointDouble p2, Color currentColor) {
    drawLineCurve(applySecond(p1), applySecond(p2), currentColor, false);
  }

  public void drawLineCurve(PointDouble p1, PointDouble p2, Color currentColor, boolean isActive) {
    if (isActive) {
      g2d.setColor(currentColor);
    } else {
      Color color = new Color(currentColor.getRed(),
              currentColor.getGreen(),
              currentColor.getBlue(), 64);

      g2d.setColor(color);
    }

    g2d.setStroke(new BasicStroke(1.2F));
    g2d.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
  }

  private PointDouble applySecond(PointDouble pointDouble) {
    double x = xCenter - pointDouble.x;
    double y = yCenter - pointDouble.y;

    return new PointDouble(x, y);
  }
}
