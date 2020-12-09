package ru.computerGraphics.model;

import java.util.ArrayList;
import java.util.List;

import static ru.computerGraphics.db.LocalDataBase.radiusOfCircleSquare;

public class Curve {
  private final List<PointDouble> pointDoubles;
  private PointDouble selectedPointDouble;

  public Curve() {
    this.pointDoubles = new ArrayList<>();
  }

  public Curve(List<PointDouble> pointDoubles) {
    this.pointDoubles = pointDoubles;
  }

  public List<PointDouble> getPointDoubles() {
    return pointDoubles;
  }

  public void addPoint(PointDouble pointDouble) {
    pointDoubles.add(pointDouble);
  }

  public boolean selectPoint(PointDouble selectedPointDouble) {
    double x = selectedPointDouble.x;
    double y = selectedPointDouble.y;

    for (PointDouble pointDouble : pointDoubles) {
      double xCurve = pointDouble.x;
      double yCurve = pointDouble.y;

      if (Math.pow(x - xCurve, 2) + Math.pow(y - yCurve, 2) < radiusOfCircleSquare) {
        this.selectedPointDouble = pointDouble;
        return true;
      }
    }

    return false;
  }

  public void deleteSelected() {
    pointDoubles.remove(selectedPointDouble);
  }

  public void unselect() {
    selectedPointDouble = null;
  }

  public void moveSelectedPoint(double dx, double dy) {
    if (selectedPointDouble != null) {
      selectedPointDouble.x = selectedPointDouble.x + dx;
      selectedPointDouble.y = selectedPointDouble.y + dy;
    }
  }

  public void moveAllPoints(double dx, double dy) {
    for (PointDouble pointDouble : pointDoubles) {
      pointDouble.x = pointDouble.x + dx;
      pointDouble.y = pointDouble.y + dy;
    }
  }

  public PointDouble getSelectedPointDouble() {
    return selectedPointDouble;
  }
}
