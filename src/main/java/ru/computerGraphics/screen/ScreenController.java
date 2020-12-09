package ru.computerGraphics.screen;

import ru.computerGraphics.enums.Curves;
import ru.computerGraphics.model.Curve;
import ru.computerGraphics.model.CurvesList;
import ru.computerGraphics.model.PointDouble;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class ScreenController implements MouseListener, MouseMotionListener, MouseWheelListener {
  private final CurvesPanel curvesPanel;
  private final Set<RepaintListener> listeners = new HashSet<>();
  private CurvesList listOfCurves;
  private Integer currentCurveIndex;
  private Curve curve;
  private PointDouble last;
  private int scale;
  private boolean leftButton;
  private boolean rightButton;
  private boolean middleButton;

  public ScreenController(int currentCurveIndex, CurvesList listOfCurves, CurvesPanel curvesPanel) {
    this.curve = listOfCurves.get(currentCurveIndex).getCurve();
    this.listOfCurves = listOfCurves;
    this.curvesPanel = curvesPanel;
    this.scale = listOfCurves.get(currentCurveIndex).getScale();
    this.currentCurveIndex = currentCurveIndex;
  }

  private PointDouble applyFirst(Point point) {
    double x = (double) curvesPanel.getWidth() / 2 - point.x;
    double y = (double) curvesPanel.getHeight() / 2 - point.y;

    return new PointDouble(x, y);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    boolean leftButton = SwingUtilities.isLeftMouseButton(e);
    boolean rightButton = SwingUtilities.isRightMouseButton(e);
    boolean middleButton = SwingUtilities.isMiddleMouseButton(e);

    if (e.getClickCount() == 1 && leftButton) {
      if (listOfCurves.get(currentCurveIndex).getName().equals(Curves.BEZIER_CURVE)) {
        if (listOfCurves.get(currentCurveIndex).getCurve().getPointDoubles().size() < 14) {
          curve.addPoint(new PointDouble(applyFirst(e.getPoint())));
        }
      } else {
        curve.addPoint(new PointDouble(applyFirst(e.getPoint())));
      }
    }

    if (e.getClickCount() == 1 && middleButton) {
      curve.selectPoint(applyFirst(e.getPoint()));
      curve.deleteSelected();
    }

    if (e.getClickCount() == 1 && rightButton) {
      for (int i = 0; i < listOfCurves.size(); i++) {
        if (listOfCurves.get(i).getCurve().selectPoint(applyFirst(e.getPoint()))) {
          curve = listOfCurves.get(i).getCurve();
          curvesPanel.setCurrentCurveIndex(i);
          curve.unselect();
          break;
        }
      }
    }

    onRepaint();
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (SwingUtilities.isLeftMouseButton(e)) {
      curve.selectPoint(applyFirst(e.getPoint()));
      leftButton = true;
    }

    if (SwingUtilities.isRightMouseButton(e)) {
      rightButton = true;
    }

    if (SwingUtilities.isMiddleMouseButton(e)) {
      middleButton = true;
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (SwingUtilities.isLeftMouseButton(e)) {
      leftButton = false;
    }

    if (SwingUtilities.isRightMouseButton(e)) {
      rightButton = false;
    }

    if (SwingUtilities.isMiddleMouseButton(e)) {
      middleButton = false;
    }

    if (!leftButton && !rightButton) {
      curve.unselect();
      last = null;
      onRepaint();
    }
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    if (e.getWheelRotation() < 0) {
      scale += e.getScrollAmount() / 3;

      if (scale < -50) {
        scale = -50;
      } else if (scale > 40) {
        scale = 40;
      } else {
        for (PointDouble point : curve.getPointDoubles()) {
          point.x = 1.05 * point.x;
          point.y = 1.05 * point.y;
        }
      }
    } else {
      scale -= e.getScrollAmount() / 3;

      if (scale < -50) {
        scale = -50;
      } else if (scale > 40) {
        scale = 40;
      } else {
        for (PointDouble point : curve.getPointDoubles()) {
          point.x = 0.95 * point.x;
          point.y = 0.95 * point.y;
        }
      }
    }

    onRepaint();
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    PointDouble current = applyFirst(e.getPoint());

    if (leftButton && !rightButton && !middleButton && last != null) {
      curve.moveSelectedPoint(current.x - last.x, current.y - last.y);
    }

    if (!leftButton && rightButton && !middleButton && last != null) {
      curve.moveAllPoints(current.x - last.x, current.y - last.y);
    }

    last = current;
    onRepaint();
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseMoved(MouseEvent e) {

  }

  public void addRepaintListener(RepaintListener listener) {
    listeners.add(listener);
  }

  protected void onRepaint() {
    for (RepaintListener cl : listeners)
      cl.shouldRepaint();
  }

  public void setCurve(int currentCurveIndex, CurvesList listOfCurves) {
    if (this.currentCurveIndex < this.listOfCurves.size()) {
      this.listOfCurves.get(this.currentCurveIndex).setScale(scale);
    }

    this.currentCurveIndex = currentCurveIndex;
    this.curve = listOfCurves.get(currentCurveIndex).getCurve();
    this.scale = listOfCurves.get(currentCurveIndex).getScale();
    this.listOfCurves = listOfCurves;
  }

  public void refreshCurrent() {
    if (currentCurveIndex < listOfCurves.size()) {
      listOfCurves.get(currentCurveIndex).setScale(scale);
    }
  }
}
