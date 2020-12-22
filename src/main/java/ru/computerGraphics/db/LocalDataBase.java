package ru.computerGraphics.db;

import ru.computerGraphics.model.CurvesList;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LocalDataBase {
  public final static int radiusOfCircle = 4;
  public final static int diameterOfCircle = radiusOfCircle * 2;
  public final static int radiusOfCircleSquare = 2 * radiusOfCircle * radiusOfCircle;
  public final static int maxCurves = 10;
  public final static Color vertexColor = new Color(127, 122, 122, 140);
  public final static Color defaultCurveColor = new Color(234, 116, 42);
  public final static Color frameEdgeColor = new Color(7, 7, 7, 45);
  public static List<CurvesList> listOfListsCurves = new ArrayList<>();
  public static List<Integer> activeCurveIndexes = new ArrayList<>();
  public static List<Boolean> activityIndexes = new ArrayList<>();
  public static List<Boolean> tempActiveLines = new ArrayList<>();
  public static List<Boolean> tempActivePoints = new ArrayList<>();
  public static boolean drawLinesCurrentFrameStatic;
  public static boolean drawPointsCurrentFramesStatic;

  public static void enableHints(Graphics2D graphics2D) {
    graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    graphics2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
  }
}
