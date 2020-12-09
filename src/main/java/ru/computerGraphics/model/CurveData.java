package ru.computerGraphics.model;

import ru.computerGraphics.enums.Curves;

import java.awt.*;

public class CurveData {
  private final Curve curve;
  private final Curves name;
  private Color color;
  private int scale;
  private boolean drawLinesFrame;
  private boolean drawPointsFrame;

  public CurveData(Curve curve, Curves name, Color color, boolean drawLinesFrame, boolean drawPointsFrame, int scale) {
    this.curve = curve;
    this.name = name;
    this.color = color;
    this.drawLinesFrame = drawLinesFrame;
    this.drawPointsFrame = drawPointsFrame;
    this.scale = scale;
  }

  public Curve getCurve() {
    return curve;
  }

  public Curves getName() {
    return name;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public boolean isDrawLinesFrame() {
    return drawLinesFrame;
  }

  public void setDrawLinesFrame(boolean drawLinesFrame) {
    this.drawLinesFrame = drawLinesFrame;
  }

  public boolean isDrawPointsFrame() {
    return drawPointsFrame;
  }

  public void setDrawPointsFrame(boolean drawPointsFrame) {
    this.drawPointsFrame = drawPointsFrame;
  }

  public int getScale() {
    return scale;
  }

  public void setScale(int scale) {
    this.scale = scale;
  }
}
