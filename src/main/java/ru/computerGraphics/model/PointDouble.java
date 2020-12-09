package ru.computerGraphics.model;

public class PointDouble {
  public double x;
  public double y;

  public PointDouble(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public PointDouble(PointDouble pointDouble) {
    this.x = pointDouble.x;
    this.y = pointDouble.y;
  }
}
