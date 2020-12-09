package ru.computerGraphics.model;

import ru.computerGraphics.enums.Curves;

import java.awt.*;

public class CurveBetaData extends CurveData{
  private double beta1;
  private double beta2;

  public CurveBetaData(Curve curve, Curves name, Color color,
                       boolean drawLinesFrame, boolean drawPointsFrame, int scale, double beta1, double beta2) {
    super(curve, name, color, drawLinesFrame, drawPointsFrame, scale);

    this.beta1 = beta1;
    this.beta2 = beta2;
  }

  public double getBeta1() {
    return beta1;
  }

  public void setBeta1(double beta1) {
    this.beta1 = beta1;
  }

  public double getBeta2() {
    return beta2;
  }

  public void setBeta2(double beta2) {
    this.beta2 = beta2;
  }
}
