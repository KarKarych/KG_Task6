package ru.computerGraphics.enums;

public enum Curves {
  CUBIC_SPLINE("Кубический сплайн"),
  BEZIER_CURVE("Кривая Безье"),
  B_SPLINE("B-сплайн"),
  BETA_SPLINE("Beta-сплайн"),
  BEZIER_CURVE_2("Безье сплайн"),
  NOT_SELECTED("Не выбрано");

  private final String name;

  Curves(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
