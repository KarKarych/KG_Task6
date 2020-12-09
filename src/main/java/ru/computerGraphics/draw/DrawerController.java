package ru.computerGraphics.draw;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import ru.computerGraphics.model.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DrawerController {
  private final Drawer2D drawer2D;
  private final CurvesList curvesList;
  private final int curveIndex;

  private double beta1;
  private double beta2;

  public DrawerController(Drawer2D drawer2D, CurvesList curvesList, int curveIndex) {
    this.curveIndex = curveIndex;
    this.curvesList = curvesList;
    this.drawer2D = drawer2D;
  }

  public void drawCurves(boolean activity) {
    for (int i = 0; i < curvesList.size(); i++) {
      CurveData curveData = curvesList.get(i);

      switch (curveData.getName()) {
        case CUBIC_SPLINE -> {
          if (curveData.getCurve().getPointDoubles().size() > 3)
            drawCubicSplineBook(curveData.getCurve(), activity || i == curveIndex, curveData.getColor());
        }
        case BEZIER_CURVE -> {
          if (curveData.getCurve().getPointDoubles().size() > 2)
            drawBezierCurve(curveData.getCurve(), activity || i == curveIndex, curveData.getColor());
        }
        case BEZIER_CURVE_2 -> {
          if (curveData.getCurve().getPointDoubles().size() > 2)
            drawBezierCurveBook(curveData.getCurve(), activity || i == curveIndex, curveData.getColor());
        }
        case B_SPLINE -> {
          if (curveData.getCurve().getPointDoubles().size() > 3)
            drawBSpline(curveData.getCurve(), activity || i == curveIndex, curveData.getColor());
        }
        case BETA_SPLINE -> {
          beta1 = ((CurveBetaData) curveData).getBeta1();
          beta2 = ((CurveBetaData) curveData).getBeta2();

          if (curveData.getCurve().getPointDoubles().size() > 3)
            drawBetaSpline(curveData.getCurve(), activity || i == curveIndex, curveData.getColor());
        }
      }
    }

    drawFrames();
  }

  private void drawFrames() {
    for (int j = 0; j < curvesList.size(); j++) {
      boolean isActive = curveIndex == j;

      Curve curve = curvesList.get(j).getCurve();
      boolean drawLinesFrame = curvesList.get(j).isDrawLinesFrame();
      boolean drawPointsFrame = curvesList.get(j).isDrawPointsFrame();

      if (curve.getPointDoubles() == null || curve.getPointDoubles().size() == 0) continue;

      List<PointDouble> pointDoubles = curve.getPointDoubles();

      if (pointDoubles.size() == 1) {
        drawer2D.drawVertexFrame(pointDoubles.get(0), drawPointsFrame && isActive,
                curve.getSelectedPointDouble() != null &&
                        curve.getSelectedPointDouble().equals(pointDoubles.get(0)));
      } else {
        if (curvesList.get(j).isDrawLinesFrame()) {
          for (int i = 0; i < pointDoubles.size() - 1; i++) {
            PointDouble pointDoubleCurrent = pointDoubles.get(i);
            PointDouble pointDoubleNext = pointDoubles.get(i + 1);

            if (drawLinesFrame && isActive) {
              drawer2D.drawLineFrameActive(pointDoubleCurrent,
                      pointDoubleNext);
            } else {
              drawer2D.drawLineFrameInactive(pointDoubleCurrent,
                      pointDoubleNext);
            }
          }
        }

        if (curvesList.get(j).isDrawPointsFrame()) {
          for (PointDouble pointDoubleCurrent : pointDoubles) {
            drawer2D.drawVertexFrame(pointDoubleCurrent, drawPointsFrame && isActive,
                    curve.getSelectedPointDouble() != null &&
                            curve.getSelectedPointDouble().equals(pointDoubleCurrent));
          }
        }
      }
    }
  }

  private void drawBetaSpline(Curve curve, boolean currentCurve, Color currentColor) {
    double xA, yA, xB, yB, xC, yC, xD, yD, x2 = 0, y2 = 0, x1, y1;

    boolean first = true;
    List<PointDouble> pointDoubles = curve.getPointDoubles();

    for (int i = 1; i < pointDoubles.size() - 2; i++) {
      xA = pointDoubles.get(i - 1).x;
      xB = pointDoubles.get(i).x;
      xC = pointDoubles.get(i + 1).x;
      xD = pointDoubles.get(i + 2).x;

      yA = pointDoubles.get(i - 1).y;
      yB = pointDoubles.get(i).y;
      yC = pointDoubles.get(i + 1).y;
      yD = pointDoubles.get(i + 2).y;

      for (int j = 0; j < 50; j++) {
        double t = (double) j / 50;

        double s = 1.0 - t;
        double t2 = t * t;
        double t3 = t2 * t;

        double b12 = beta1 * beta1;
        double b13 = b12 * beta1;


        double delta = 2.0 * b13 + 4.0 * b12 + 4.0 * beta1 + beta2 + 2.0;

        double d = 1.0 / delta;

        double b0 = 2 * b13 * d * s * s * s;

        double b1 = d * (2 * b13 * t * (t2 - 3 * t + 3) +
                2 * b12 * (t3 - 3 * t2 + 2) +
                2 * beta1 * (t3 - 3 * t + 2) +
                beta2 * (2 * t3 - 3 * t2 + 1));

        double b2 = d * (2 * b12 * t2 * (-t + 3) + 2 * beta1 * t * (-t2 + 3) +
                beta2 * t2 * (-2 * t + 3) + 2 * (-t3 + 1));

        double b3 = 2 * t3 * d;


        x1 = x2;
        y1 = y2;

        x2 = b0 * xA + b1 * xB + b2 * xC + b3 * xD;
        y2 = b0 * yA + b1 * yB + b2 * yC + b3 * yD;

        if (first) {
          first = false;
        } else {
          PointDouble p1 = new PointDouble(x1, y1);
          PointDouble p2 = new PointDouble(x2, y2);

          if (currentCurve) {
            drawer2D.drawLineCurveActive(p1, p2, currentColor);
          } else {
            drawer2D.drawLineCurveInactive(p1, p2, currentColor);
          }
        }
      }
    }
  }

  private void drawBezierCurveBook(Curve curve, boolean currentCurve, Color currentColor) {
    double xA, yA, xB, yB, xC, yC, xD, yD, s, t, t2, t3, x1, y1;
    double x2 = 0;
    double y2 = 0;
    boolean first = true;

    List<PointDouble> pointDoubles = curve.getPointDoubles();
    for (int i = 1; i < pointDoubles.size() - 2; i += 3) {
      xA = pointDoubles.get(i - 1).x;
      xB = pointDoubles.get(i).x;
      xC = pointDoubles.get(i + 1).x;
      xD = pointDoubles.get(i + 2).x;

      yA = pointDoubles.get(i - 1).y;
      yB = pointDoubles.get(i).y;
      yC = pointDoubles.get(i + 1).y;
      yD = pointDoubles.get(i + 2).y;

      for (int j = 0; j <= 50; j++) {
        t = (double) j / 50;

        x1 = x2;
        y1 = y2;

        s = 1 - t;
        t2 = t * t;
        t3 = t2 * t;

        x2 = ((xA * s + 3 * t * xB) * s + 3 * t2 * xC) * s + t3 * xD;
        y2 = ((yA * s + 3 * t * yB) * s + 3 * t2 * yC) * s + t3 * yD;

        if (first) {
          first = false;
        } else {
          PointDouble p1 = new PointDouble(x1, y1);
          PointDouble p2 = new PointDouble(x2, y2);
          if (currentCurve) {
            drawer2D.drawLineCurveActive(p1, p2, currentColor);
          } else {
            drawer2D.drawLineCurveInactive(p1, p2, currentColor);
          }
        }
      }
    }
  }

  private void drawBezierCurve(Curve curve, boolean currentCurve, Color currentColor) {
    double x1, y1, x2 = 0, y2 = 0;
    double step = 0.001;

    boolean first = true;

    List<PointDouble> pointDoubles = curve.getPointDoubles();
    for (double t = 0; t < 1; t += step) {
      x1 = x2;
      y1 = y2;

      x2 = 0;
      y2 = 0;

      for (int i = 0; i < pointDoubles.size(); i++) {
        double b = binom(i, pointDoubles.size() - 1, t);
        x2 += pointDoubles.get(i).x * b;
        y2 += pointDoubles.get(i).y * b;
      }

      if (first) {
        first = false;
      } else {
        PointDouble p1 = new PointDouble(x1, y1);
        PointDouble p2 = new PointDouble(x2, y2);

        if (currentCurve) {
          drawer2D.drawLineCurveActive(p1, p2, currentColor);
        } else {
          drawer2D.drawLineCurveInactive(p1, p2, currentColor);
        }
      }
    }
  }

  private double binom(int i, int n, double t) {
    return ((double) factorial(n) / (factorial(i) * factorial(n - i))) *
            Math.pow(t, i) * Math.pow(1 - t, n - i);
  }

  private int factorial(int n) {
    int res = 1;
    for (int i = 1; i <= n; i++)
      res *= i;
    return res;
  }

  private void drawBSpline(Curve curve, boolean currentCurve, Color currentColor) {
    double xA, yA, xB, yB, xC, yC, xD, yD, a0, a1, a2, a3,
            b0, b1, b2, b3, x2 = 0, y2 = 0, x1, y1;

    boolean first = true;

    List<PointDouble> pointDoubles = curve.getPointDoubles();
    for (int i = 1; i < pointDoubles.size() - 2; i++) {
      xA = pointDoubles.get(i - 1).x;
      xB = pointDoubles.get(i).x;
      xC = pointDoubles.get(i + 1).x;
      xD = pointDoubles.get(i + 2).x;

      yA = pointDoubles.get(i - 1).y;
      yB = pointDoubles.get(i).y;
      yC = pointDoubles.get(i + 1).y;
      yD = pointDoubles.get(i + 2).y;

      a3 = (-xA + 3 * (xB - xC) + xD) / 6;
      a2 = (xA - 2 * xB + xC) / 2;
      a1 = (xC - xA) / 2;
      a0 = (xA + 4 * xB + xC) / 6;

      b3 = (-yA + 3 * (yB - yC) + yD) / 6;
      b2 = (yA - 2 * yB + yC) / 2;
      b1 = (yC - yA) / 2;
      b0 = (yA + 4 * yB + yC) / 6;

      for (int j = 0; j <= 50; j++) {
        double t = (double) j / 50;

        x1 = x2;
        y1 = y2;

        x2 = ((a3 * t + a2) * t + a1) * t + a0;
        y2 = ((b3 * t + b2) * t + b1) * t + b0;

        if (first) {
          first = false;
        } else {
          PointDouble p1 = new PointDouble(x1, y1);
          PointDouble p2 = new PointDouble(x2, y2);

          if (currentCurve) {
            drawer2D.drawLineCurveActive(p1, p2, currentColor);
          } else {
            drawer2D.drawLineCurveInactive(p1, p2, currentColor);
          }
        }
      }
    }
  }

  private void drawCubicSplineBook(Curve curve, boolean currentCurve, Color currentColor) {
    List<PointDouble> pointDoubles = curve.getPointDoubles();

    pointDoubles.sort(Comparator.comparingDouble(p -> p.x));

    ArrayList<Double> xTemp = new ArrayList<>();
    ArrayList<Double> yTemp = new ArrayList<>();

    for (PointDouble pointDouble : pointDoubles) {
      if (!xTemp.contains(pointDouble.x) && !xTemp.contains(pointDouble.y)) {
        xTemp.add(pointDouble.x);
        yTemp.add(pointDouble.y);
      }
    }

    double[] x = new double[xTemp.size()];
    double[] y = new double[yTemp.size()];

    for (int i = 0; i < x.length; i++) {
      x[i] = xTemp.get(i);
      y[i] = yTemp.get(i);
    }

    SplineInterpolator splineInterpolator = new SplineInterpolator();
    PolynomialSplineFunction function = splineInterpolator.interpolate(x, y);

    for (int xDraw = (int) Math.ceil(function.getKnots()[0]); xDraw < function.getKnots()[function.getKnots().length - 1] - 1; xDraw++) {
      PointDouble p1 = new PointDouble(xDraw, function.value(xDraw));
      PointDouble p2 = new PointDouble(xDraw + 1, function.value(xDraw + 1));

      if (currentCurve) {
        drawer2D.drawLineCurveActive(p1, p2, currentColor);
      } else {
        drawer2D.drawLineCurveInactive(p1, p2, currentColor);
      }
    }
  }

  /*private void drawBSplineBook(Curve curve, boolean currentCurve, Color currentColor) {
      double xA, yA, xB, yB, xC, yC, xD, yD, s, t, t2, t3, x1, y1;
      double x2 = 0;
      double y2 = 0;
      boolean first = true;

      List<PointDouble> pointDoubles = curve.getPointDoubles();
      for (int i = 1; i < pointDoubles.size() - 2; i++) {
        xA = pointDoubles.get(i - 1).x;
        xB = pointDoubles.get(i).x;
        xC = pointDoubles.get(i + 1).x;
        xD = pointDoubles.get(i + 2).x;

        yA = pointDoubles.get(i - 1).y;
        yB = pointDoubles.get(i).y;
        yC = pointDoubles.get(i + 1).y;
        yD = pointDoubles.get(i + 2).y;

        for (int j = 0; j <= 50; j++) {
          t = (double) j / 50;

          x1 = x2;
          y1 = y2;

          s = 1 - t;
          t2 = t * t;
          t3 = t2 * t;

          double b3 = -3 * t3 + 3 * t2 + 3 * t + 1;
          x2 = (s * s * s * xA +
                  (3 * t3 - 6 * t2 + 4) * xB +
                  b3 * xC +
                  t3 * xD) / 6.0;
          y2 = (s * s * s * yA +
                  (3 * t3 - 6 * t2 + 4) * yB +
                  b3 * yC +
                  t3 * yD) / 6.0;

          if (first) {
            first = false;
          } else {
            PointDouble p1 = new PointDouble(x1, y1);
            PointDouble p2 = new PointDouble(x2, y2);
            if (currentCurve) {
              drawer2D.drawLineCurveActive(p1, p2, currentColor);
            } else {
              drawer2D.drawLineCurveInactive(p1, p2, currentColor);
            }
          }
        }
      }
    }*/
}
