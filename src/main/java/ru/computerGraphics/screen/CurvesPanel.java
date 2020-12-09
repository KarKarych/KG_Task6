package ru.computerGraphics.screen;

import ru.computerGraphics.draw.Drawer2D;
import ru.computerGraphics.draw.DrawerController;
import ru.computerGraphics.enums.Curves;
import ru.computerGraphics.model.Curve;
import ru.computerGraphics.model.CurveBetaData;
import ru.computerGraphics.model.CurveData;
import ru.computerGraphics.model.CurvesList;
import ru.computerGraphics.update.UpdateLabel;

import javax.swing.*;
import java.awt.*;

import static ru.computerGraphics.db.LocalDataBase.*;
import static ru.computerGraphics.enums.Curves.BETA_SPLINE;
import static ru.computerGraphics.enums.Curves.NOT_SELECTED;

public class CurvesPanel extends JPanel implements RepaintListener {
  private JLabel label;
  private ScreenController activeCurveController;
  private CurvesList listOfCurves;
  private Color currentColor;
  private Curves currentCurve;
  private int currentCurveIndex;
  private int currentCanvasIndex;
  private boolean drawLinesCurrentFrame;
  private boolean drawPointsCurrentFrames;
  private boolean activity;

  public CurvesPanel() {
    initializeDBWithDefaultValues();

    listOfCurves = new CurvesList();
    listOfCurves.add(new CurveData(new Curve(), NOT_SELECTED, defaultCurveColor, true, true, 0));

    initializeDefaultValues();

    currentCanvasIndex = 0;
    currentCurveIndex = -1;

    drawLinesCurrentFrameStatic = true;
    drawPointsCurrentFramesStatic = true;

    activity = false;
  }

  private void initializeDefaultValues() {
    currentCurve = listOfCurves.get(currentCurveIndex).getName();
    currentColor = listOfCurves.get(currentCurveIndex).getColor();
    drawLinesCurrentFrame = listOfCurves.get(currentCurveIndex).isDrawLinesFrame();
    drawPointsCurrentFrames = listOfCurves.get(currentCurveIndex).isDrawPointsFrame();
  }

  private void initializeDBWithDefaultValues() {
    for (int i = 0; i < 6; i++) {
      listOfListsCurves.add(new CurvesList());
      activeCurveIndexes.add(-1);
      activityIndexes.add(false);
    }
  }

  private void enableController() {
    activeCurveController = new ScreenController(currentCurveIndex,
            listOfCurves, this);

    activeCurveController.addRepaintListener(this);

    addMouseListener(activeCurveController);
    addMouseMotionListener(activeCurveController);
    addMouseWheelListener(activeCurveController);
  }

  public void changeActivity() {
    if (currentCurve == NOT_SELECTED) return;

    this.activity = !activity;

    repaint();
  }

  public void changeDrawLinesFrame() {
    if (currentCurve == NOT_SELECTED) return;

    this.drawLinesCurrentFrame = !drawLinesCurrentFrame;

    listOfCurves.get(currentCurveIndex).setDrawLinesFrame(drawLinesCurrentFrame);

    repaint();
  }

  public void changeDrawLinesFrames() {
    if (currentCurve == NOT_SELECTED) return;

    drawLinesCurrentFrameStatic = !drawLinesCurrentFrameStatic;

    for (int i = 0; i < listOfCurves.size(); i++) {
      CurveData curveData = listOfCurves.get(i);
      curveData.setDrawLinesFrame(drawLinesCurrentFrameStatic);
    }

    repaint();
  }

  public void changeDrawPointsFrame() {
    if (currentCurve == NOT_SELECTED) return;

    this.drawPointsCurrentFrames = !drawPointsCurrentFrames;

    listOfCurves.get(currentCurveIndex).setDrawPointsFrame(drawPointsCurrentFrames);

    repaint();
  }

  public void changeDrawPointsFrames() {
    if (currentCurve == NOT_SELECTED) return;

    drawPointsCurrentFramesStatic = !drawPointsCurrentFramesStatic;

    for (int i = 0; i < listOfCurves.size(); i++) {
      CurveData curveData = listOfCurves.get(i);
      curveData.setDrawPointsFrame(drawPointsCurrentFramesStatic);
    }

    repaint();
  }

  public Color getCurrentColor() {
    return currentColor;
  }

  public void setCurrentColor(Color currentColor) {
    this.currentColor = currentColor;
    listOfCurves.get(currentCurveIndex).setColor(currentColor);

    repaint();
  }

  public void setCurrentColors(Color currentColor) {
    for (int i = 0; i < listOfCurves.size(); i++) {
      CurveData data = listOfCurves.get(i);
      data.setColor(currentColor);
    }

    this.currentColor = currentColor;

    repaint();
  }

  public int getCurrentCurveIndex() {
    activeCurveController.refreshCurrent();

    return currentCurveIndex;
  }

  @UpdateLabel
  public void setCurrentCurveIndex(int currentCurveIndex) {
    this.currentCurveIndex = currentCurveIndex;

    initializeDefaultValues();
  }

  @UpdateLabel
  public void setCurrentCurve(Curves curve) {
    if (currentCurve == NOT_SELECTED) {
      currentCurveIndex = 0;
      currentCurve = curve;
      listOfCurves.clear();
      listOfCurves.add(new CurveData(new Curve(), currentCurve, defaultCurveColor, true, true, 0));

      initializeDefaultValues();

      if (activeCurveController == null) {
        enableController();
      } else {
        activeCurveController.setCurve(currentCurveIndex, listOfCurves);
      }

      return;
    }

    boolean flag = false;
    for (int i = 0; i < listOfCurves.size(); i++) {
      CurveData curveCurveData = listOfCurves.get(i);

      if (curveCurveData.getName() == curve) {
        currentCurveIndex = i;
        flag = true;
      }
    }

    this.currentCurve = curve;

    if (flag) {
      initializeDefaultValues();
      activeCurveController.setCurve(currentCurveIndex, listOfCurves);
    } else {
      addNewCurrentCurve();
    }
  }

  public void updateLabelText() {
    switch (currentCurveIndex) {
      case 9 -> label.setForeground(Color.RED);
      case -1 -> label.setForeground(new Color(213, 2, 30));
      default -> label.setForeground(Color.BLACK);
    }

    if (currentCurveIndex + 1 == 0) {
      label.setText(("Тип кривой: Не выбрано. Выберете кривую или загрузите её.  Номер холста: %1$d   ").
              formatted(currentCanvasIndex + 1));
    } else {
      label.setText(("Тип кривой: %2$s.  Номер текущей кривой: %3$d.  Номер холста: %1$d   ").
              formatted((currentCanvasIndex + 1), currentCurve.getName(), currentCurveIndex + 1));
    }

    repaint();
  }

  @UpdateLabel
  public void changeCurrentCurve(int currentCurveNumber) {
    if (currentCurve == NOT_SELECTED) return;

    int tempCurveNumber = currentCurveIndex + currentCurveNumber;

    if (tempCurveNumber == -1) {
      this.currentCurveIndex = listOfCurves.size() - 1;
    } else if (tempCurveNumber == listOfCurves.size()) {
      this.currentCurveIndex = 0;
    } else currentCurveIndex = tempCurveNumber;

    initializeDefaultValues();

    activeCurveController.setCurve(currentCurveIndex, listOfCurves);
  }

  @UpdateLabel
  public void addNewCurrentCurve() {
    if (currentCurve == NOT_SELECTED) return;

    if (listOfCurves.size() < maxCurves) {
      currentCurveIndex = listOfCurves.size();
      if (currentCurve.equals(BETA_SPLINE)) {
        listOfCurves.add(new CurveBetaData(new Curve(), currentCurve, defaultCurveColor,
                true, true, 0, 1, 0));
      } else {
        listOfCurves.add(new CurveData(new Curve(), currentCurve, defaultCurveColor,
                true, true, 0));
      }

      initializeDefaultValues();
    }

    activeCurveController.setCurve(currentCurveIndex, listOfCurves);
  }

  @UpdateLabel
  public void deleteCurrentCurve() {
    if (currentCurve == NOT_SELECTED) return;

    listOfCurves.remove(listOfCurves.get(currentCurveIndex));

    if (currentCurveIndex - 1 != -1) {
      currentCurveIndex -= 1;
    } else {
      if (listOfCurves.size() == 0) {
        setDefaultValues();
        return;
      }
    }

    initializeDefaultValues();

    activeCurveController.setCurve(currentCurveIndex, listOfCurves);
  }

  private void setDefaultValues() {
    listOfCurves.clear();
    currentCurve = NOT_SELECTED;
    currentCurveIndex = -1;
    drawLinesCurrentFrame = true;
    drawPointsCurrentFrames = true;
  }

  @UpdateLabel
  public void switchCanvasTo(int newCanvas) {
    int previousCanvasIndex = currentCanvasIndex;
    currentCanvasIndex = newCanvas;

    fillDB(listOfCurves, currentCurveIndex, previousCanvasIndex, activity);

    if (newCanvas >= 0 && newCanvas <= 5) {
      listOfCurves = listOfListsCurves.get(newCanvas);
      currentCurveIndex = activeCurveIndexes.get(newCanvas);
      activity = activityIndexes.get(newCanvas);

      if (currentCurveIndex != -1) {
        initializeDefaultValues();

        if (activeCurveController == null) {
          enableController();
        } else {
          activeCurveController.setCurve(currentCurveIndex, listOfCurves);
        }
      }
    }
  }

  public void changeCurrentCanvas(int currentCurveNumber) {
    int tempCanvasNumber = currentCanvasIndex + currentCurveNumber;

    if (tempCanvasNumber == -1) {
      switchCanvasTo(listOfListsCurves.size() - 1);
    } else if (tempCanvasNumber == listOfListsCurves.size()) {
      switchCanvasTo(0);
    } else switchCanvasTo(tempCanvasNumber);
  }

  @UpdateLabel
  public void deleteAllCurves() {
    if (currentCurve == NOT_SELECTED) return;

    setDefaultValues();
  }

  public int getCurrentCanvasIndex() {
    return currentCanvasIndex;
  }

  public CurvesList getCurvesList(int canvasIndex) {
    if (canvasIndex == currentCanvasIndex) {
      return listOfCurves;
    }

    if (canvasIndex >= 0 && canvasIndex <= 5) {
      return listOfListsCurves.get(canvasIndex);
    } else {
      return null;
    }
  }

  @UpdateLabel
  public void setData(CurvesList curvesList, int activeCurveIndex, int canvasIndex, boolean activeCurves) {
    if (canvasIndex == currentCanvasIndex) {
      listOfCurves = curvesList;
      currentCurveIndex = activeCurveIndex;
      initializeDefaultValues();
      activity = activeCurves;

      if (activeCurveController == null) {
        enableController();
      } else {
        activeCurveController.setCurve(currentCurveIndex, listOfCurves);
      }

      return;
    }

    fillDB(curvesList, activeCurveIndex, canvasIndex, activity);

    switchCanvasTo(canvasIndex);
  }

  private void fillDB(CurvesList curvesList, int activeCurveIndex, int canvasIndex, boolean activity) {
    listOfListsCurves.set(canvasIndex, new CurvesList(curvesList));
    activeCurveIndexes.set(canvasIndex, activeCurveIndex);
    activityIndexes.set(canvasIndex, activity);
  }

  @UpdateLabel
  public void addLabel(JLabel label) {
    this.label = label;
  }

  public boolean isActive() {
    return activity;
  }

  public void setActivity(boolean activity) {
    this.activity = activity;
  }

  public void turnOnFrames() {
    for (int i = 0; i < listOfCurves.size(); i++) {
      CurveData curveData = listOfCurves.get(i);
      curveData.setDrawLinesFrame(tempActiveLines.get(i));
      curveData.setDrawPointsFrame(tempActivePoints.get(i));
    }
  }

  public void turnOffFrames() {
    tempActiveLines.clear();
    tempActivePoints.clear();

    for (int i = 0; i < listOfCurves.size(); i++) {
      CurveData curveData = listOfCurves.get(i);
      tempActiveLines.add(curveData.isDrawLinesFrame());
      tempActivePoints.add(curveData.isDrawPointsFrame());
    }

    boolean temp1 = drawPointsCurrentFramesStatic;
    boolean temp2 = drawLinesCurrentFrameStatic;

    drawPointsCurrentFramesStatic = true;
    drawLinesCurrentFrameStatic = true;

    changeDrawLinesFrames();
    changeDrawPointsFrames();

    drawPointsCurrentFramesStatic = temp1;
    drawLinesCurrentFrameStatic = temp2;
  }

  public void setBetas(double beta1, double beta2) {
    if (currentCurve == NOT_SELECTED) return;

    if (currentCurve.equals(BETA_SPLINE)) {
      ((CurveBetaData) listOfCurves.get(currentCurveIndex)).setBeta1(beta1);
      ((CurveBetaData) listOfCurves.get(currentCurveIndex)).setBeta2(beta2);
    }

    repaint();
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    enableHints(g2d);
    DrawerController drawerController = new DrawerController(new Drawer2D(g2d, getWidth(), getHeight()),
            listOfCurves, currentCurveIndex);
    drawerController.drawCurves(activity);
  }

  @Override
  public void shouldRepaint() {
    repaint();
  }
}
