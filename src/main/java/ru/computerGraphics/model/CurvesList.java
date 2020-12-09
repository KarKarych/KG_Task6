package ru.computerGraphics.model;

import java.util.ArrayList;
import java.util.List;

public class CurvesList {
  private final List<CurveData> curvesList;

  public CurvesList() {
    this.curvesList = new ArrayList<>();
  }

  public CurvesList(CurvesList curvesList) {
    this.curvesList = new ArrayList<>(curvesList.curvesList);
  }

  public void add(CurveData element) {
    curvesList.add(element);
  }

  public CurveData get(int index) {
    return curvesList.get(index);
  }

  public int size() {
    return curvesList.size();
  }

  public void clear() {
    curvesList.clear();
  }

  public void remove(CurveData element) {
    curvesList.remove(element);
  }
}
