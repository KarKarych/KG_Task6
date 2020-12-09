package ru.computerGraphics.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.computerGraphics.enums.Curves;
import ru.computerGraphics.model.*;
import ru.computerGraphics.screen.CurvesPanel;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.computerGraphics.enums.Curves.BETA_SPLINE;

public class JsonLoader {
  public String getJson(int canvasIndex, CurvesPanel curvesPanel) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode main = mapper.createObjectNode();

    main.put("active_curve_index", curvesPanel.getCurrentCurveIndex());
    main.put("active_curves", curvesPanel.isActive());

    CurvesList curvesList = curvesPanel.getCurvesList(canvasIndex);
    for (int i = 0; i < curvesList.size(); i++) {
      ObjectNode curveNode = mapper.createObjectNode();

      ObjectNode currentColorNode = mapper.createObjectNode();
      currentColorNode.put("red", curvesList.get(i).getColor().getRed());
      currentColorNode.put("green", curvesList.get(i).getColor().getGreen());
      currentColorNode.put("blue", curvesList.get(i).getColor().getBlue());
      currentColorNode.put("alpha", curvesList.get(i).getColor().getAlpha());
      curveNode.set("color_curve", currentColorNode);

      curveNode.put("type_curve", curvesList.get(i).getName().ordinal());
      curveNode.put("active_lines_frame", curvesList.get(i).isDrawLinesFrame());
      curveNode.put("active_points_frame", curvesList.get(i).isDrawPointsFrame());
      curveNode.put("scale", curvesList.get(i).getScale());

      if (curvesList.get(i).getName().equals(BETA_SPLINE)) {
        curveNode.put("beta_1", ((CurveBetaData) curvesList.get(i)).getBeta1());
        curveNode.put("beta_2", ((CurveBetaData) curvesList.get(i)).getBeta2());
      }

      ArrayNode points = mapper.createArrayNode();

      for (PointDouble point : curvesList.get(i).getCurve().getPointDoubles()) {
        ObjectNode pointNode = mapper.createObjectNode();
        pointNode.put("x", point.x);
        pointNode.put("y", point.y);

        points.add(pointNode);
      }

      curveNode.set("points", points);
      main.set(("curve_" + i), curveNode);
    }

    String jsonString = null;
    try {
      jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(main);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return jsonString;
  }

  public void loadFromJson(int canvasIndex, CurvesPanel curvesPanel, List<String> lines) {
    StringBuilder jsonBuilder = new StringBuilder();

    for (String line : lines) {
      jsonBuilder.append(line);
    }

    ObjectMapper mapper = new ObjectMapper();
    JsonNode main = null;

    try {
      main = mapper.readTree(jsonBuilder.toString());
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }

    assert main != null;

    CurvesList curvesList = new CurvesList();
    for (int i = 0; i < 10; i++) {
      List<PointDouble> points = new ArrayList<>();
      JsonNode pointsNode = main.get("curve_" + i);

      if (pointsNode == null) {
        break;
      }

      for (JsonNode pointNode : pointsNode.get("points")) {
        points.add(new PointDouble(pointNode.get("x").asInt(), pointNode.get("y").asInt()));
      }

      Color currentColor = new Color(pointsNode.get("color_curve").get("red").asInt(),
              pointsNode.get("color_curve").get("green").asInt(),
              pointsNode.get("color_curve").get("blue").asInt(),
              pointsNode.get("color_curve").get("alpha").asInt());

      if (Curves.values()[pointsNode.get("type_curve").asInt()].equals(BETA_SPLINE)) {
        curvesList.add(new CurveBetaData(new Curve(points),
                Curves.values()[pointsNode.get("type_curve").asInt()],
                currentColor,
                pointsNode.get("active_lines_frame").asBoolean(),
                pointsNode.get("active_points_frame").asBoolean(),
                pointsNode.get("scale").asInt(),
                pointsNode.get("beta_1").asDouble(),
                pointsNode.get("beta_2").asDouble()));
      } else {
        curvesList.add(new CurveData(new Curve(points),
                Curves.values()[pointsNode.get("type_curve").asInt()],
                currentColor,
                pointsNode.get("active_lines_frame").asBoolean(),
                pointsNode.get("active_points_frame").asBoolean(),
                pointsNode.get("scale").asInt()));
      }
    }

    int activeCurveIndex = main.get("active_curve_index").asInt();
    boolean activeCurves = main.get("active_curves").asBoolean();

    curvesPanel.setData(curvesList, activeCurveIndex, canvasIndex, activeCurves);
  }
}
