package ru.computerGraphics.screen;

import com.google.inject.Guice;
import ru.computerGraphics.io.JsonLoader;
import ru.computerGraphics.update.UpdateLabelModule;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static ru.computerGraphics.db.LocalDataBase.activeCurveIndexes;
import static ru.computerGraphics.enums.Curves.*;

public class Frame extends JFrame {
  private final Container container;
  private CurvesPanel curvesPanel;

  public Frame() {
    setTitle("Task 6");
    setPreferredSize(new Dimension(1366, 768));

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    container = getContentPane();

    initializePanel();
    initializeMenu();

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void initializePanel() {
    curvesPanel = Guice.createInjector(new UpdateLabelModule()).getInstance(CurvesPanel.class);
    add(curvesPanel);
  }

  private void initializeMenu() {
    JMenuBar jMenuBar = new JMenuBar();
    jMenuBar.add(initializeFileMenu());
    jMenuBar.add(initializeEditMenu());
    jMenuBar.add(Box.createHorizontalGlue());
    jMenuBar.add(initializeLabelMenu());
    setJMenuBar(jMenuBar);
  }

  private JMenu initializeFileMenu() {
    JMenu menuIO = new JMenu("Файл");
    JMenuItem saveCurvesJson = new JMenuItem("Сохранить холст (.json)");
    JMenuItem saveCurvesPng = new JMenuItem("Сохранить холст (.png)");
    JMenuItem openCurves = new JMenuItem("Загрузить холст");
    JMenuItem about = new JMenuItem("Горячие клавиши");

    openCurves.setAccelerator(KeyStroke.getKeyStroke('D', InputEvent.CTRL_DOWN_MASK));
    saveCurvesJson.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
    saveCurvesPng.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK));
    about.setAccelerator(KeyStroke.getKeyStroke('H', InputEvent.CTRL_DOWN_MASK));

    menuIO.add(saveCurvesJson);
    menuIO.add(saveCurvesPng);
    menuIO.add(openCurves);
    menuIO.add(about);

    addAboutListener(about);
    addIOListeners(saveCurvesJson, saveCurvesPng, openCurves);

    return menuIO;
  }

  private void addAboutListener(JMenuItem about) {
    about.addActionListener(actionEvent -> JOptionPane.showMessageDialog(null,
            "Щелчок левой кнопкой мыши добавляет опорную точку.\n" +
                    "Щелчок средней кнопкой мыши удаляет опорную точку, на которой находится курсор.\n" +
                    "Зажатая левая кнопка мыши на опорной точке делает доступным перемещение\n" +
                    "опорной точки, при этом выбранная опорная точка станет темнее.\n" +
                    "Зажатая правая кнопка мыши позволяет перемещать всю кривую.\n" +
                    "Щелчок правой кнопкой мыши по неактивной кривой переключает внимание на неё.\n" +
                    "Вращение колёсика мыши изменяет масштаб активной кривой"));
  }

  private void addIOListeners(JMenuItem saveCurvesJson, JMenuItem saveCurvesPng, JMenuItem openCurves) {
    saveCurvesJson.addActionListener(actionEvent -> {
      JFileChooser save = new JFileChooser();
      save.setCurrentDirectory(new File("./examples/json"));
      save.addChoosableFileFilter(new FileNameExtensionFilter("Файлы с кривыми (*.json)", "json"));
      save.setAcceptAllFileFilterUsed(false);
      save.setSelectedFile(new File("curves_"));
      save.setDialogTitle("Сохранить кривые в файл");
      save.setApproveButtonText("Save");

      if (save.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        URI uri = URI.create(save.getSelectedFile().toURI().toString().
                replaceAll("\\.json", "") + ".json");

        try {
          Integer[] options = {1, 2, 3, 4, 5, 6};

          Integer canvasIndex = (Integer) JOptionPane.showInputDialog(null,
                  "Выберете номер холста, кривые из которого необходимо записать в файл",
                  "Выбор холста",
                  JOptionPane.QUESTION_MESSAGE,
                  null,
                  options,
                  options[curvesPanel.getCurrentCanvasIndex()]);

          if (canvasIndex == null) {
            JOptionPane.showMessageDialog(null, "Холст не выбран", "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
          } else {
            canvasIndex -= 1;

            if (canvasIndex == curvesPanel.getCurrentCanvasIndex() && curvesPanel.getCurrentCurveIndex() != -1 ||
                    canvasIndex != curvesPanel.getCurrentCanvasIndex() && activeCurveIndexes.get(canvasIndex) != -1) {
              JsonLoader jsonLoader = new JsonLoader();
              Files.write(Paths.get(uri), jsonLoader.getJson(canvasIndex, curvesPanel).getBytes());
            } else {
              JOptionPane.showMessageDialog(null, "На холсте ничего нет", "Ошибка",
                      JOptionPane.ERROR_MESSAGE);

            }
          }
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }
      }
    });

    openCurves.addActionListener(actionEvent -> {
      JFileChooser open = new JFileChooser();
      open.setCurrentDirectory(new File("./examples/json"));
      open.addChoosableFileFilter(new FileNameExtensionFilter("Файлы с кривыми (*.json)", "json"));
      open.setDialogTitle("Открыть кривые из файла");
      open.setApproveButtonText("Open");
      open.setAcceptAllFileFilterUsed(false);

      if (open.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        try {
          Integer[] options = {1, 2, 3, 4, 5, 6};

          Integer canvasIndex = (Integer) JOptionPane.showInputDialog(null,
                  "Введите номер холста, на который будут загружены кривые из файла",
                  "Выбор холста",
                  JOptionPane.QUESTION_MESSAGE,
                  null,
                  options,
                  options[curvesPanel.getCurrentCanvasIndex()]);

          if (canvasIndex != null) {
            JsonLoader jsonLoader = new JsonLoader();

            jsonLoader.loadFromJson(canvasIndex - 1, curvesPanel,
                    Files.readAllLines(Paths.get(
                            URI.create(open.getSelectedFile().toURI().toString().
                                    replaceAll("\\.json", "") + ".json"))));

          }
        } catch (NoSuchFileException noSuchFileException) {
          JOptionPane.showMessageDialog(null, "Файла с данным названием не существует");
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }
      }
    });

    saveCurvesPng.addActionListener(e -> {
      JFileChooser save = new JFileChooser();
      save.setCurrentDirectory(new File("./examples/png"));
      save.addChoosableFileFilter(new FileNameExtensionFilter("Картинки с кривыми (*.png)", "png"));
      save.setAcceptAllFileFilterUsed(false);
      save.setSelectedFile(new File("curves_"));
      save.setDialogTitle("Сохранить кривые в файл");
      save.setApproveButtonText("Save");

      if (save.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        try {
          BufferedImage bi = new BufferedImage(curvesPanel.getWidth(), curvesPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
          Graphics2D g2d = (Graphics2D) bi.getGraphics();

          boolean removeFrames = JOptionPane.showConfirmDialog(null,
                  "Убрать весь каркас при сохранении снимка?") == JOptionPane.YES_OPTION;

          boolean disableActivity = JOptionPane.showConfirmDialog(null,
                  "Выключить отображение активных/неактивных кривых?") == JOptionPane.YES_OPTION;

          boolean changeActivity = false;

          if (removeFrames) {
            curvesPanel.turnOffFrames();
          }

          if (disableActivity) {
            changeActivity = curvesPanel.isActive();

            curvesPanel.setActivity(true);
          }

          curvesPanel.paint(g2d);

          if (removeFrames) {
            curvesPanel.turnOnFrames();
          }

          if (disableActivity) {
            curvesPanel.setActivity(changeActivity);
          }

          ImageIO.write(bi, "png", new File(save.getSelectedFile().toString().
                  replaceAll("\\.png", "") + ".png"));
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }
      }
    });
  }

  private JMenu initializeEditMenu() {
    JMenu menuActions = new JMenu("Редактирование");
    JMenuItem changeCurrentColor = new JMenuItem("Поменять цвет текущей кривой");
    JMenuItem changeCurrentColors = new JMenuItem("Поменять цвет текущих кривых");
    JMenuItem deleteCurrentCurve = new JMenuItem("Удалить текущую кривую");
    JMenuItem deleteAllCurves = new JMenuItem("Удалить все кривые");
    JMenuItem drawLinesCurrentFrame = new JMenuItem("Изменить видимость линий текущей кривой");
    JMenuItem drawPointsCurrentFrame = new JMenuItem("Изменить видимость опорных точек текущей кривой");
    JMenuItem drawLinesAllFrames = new JMenuItem("Изменить видимость линий всех кривых на холсте");
    JMenuItem drawPointsAllFrames = new JMenuItem("Изменить видимость опорных точек всех кривых на холсте");
    JMenuItem activity = new JMenuItem("Изменить активность/неактивность кривых");

    changeCurrentColor.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK));
    changeCurrentColors.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.SHIFT_DOWN_MASK));
    deleteCurrentCurve.setAccelerator(KeyStroke.getKeyStroke('Q', InputEvent.CTRL_DOWN_MASK));
    deleteAllCurves.setAccelerator(KeyStroke.getKeyStroke('W', InputEvent.CTRL_DOWN_MASK));
    drawLinesCurrentFrame.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_DOWN_MASK));
    drawPointsCurrentFrame.setAccelerator(KeyStroke.getKeyStroke('B', InputEvent.CTRL_DOWN_MASK));
    drawLinesAllFrames.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.SHIFT_DOWN_MASK));
    drawPointsAllFrames.setAccelerator(KeyStroke.getKeyStroke('B', InputEvent.SHIFT_DOWN_MASK));
    activity.setAccelerator(KeyStroke.getKeyStroke('G', InputEvent.CTRL_DOWN_MASK));

    menuActions.add(initializeCanvasesMenu());
    menuActions.add(initializeCurvesMenu());

    JMenu changeColor = new JMenu("Изменить цвет");
    changeColor.add(changeCurrentColor);
    changeColor.add(changeCurrentColors);
    menuActions.add(changeColor);

    JMenu deleteCurves = new JMenu("Удаление кривых");
    deleteCurves.add(deleteCurrentCurve);
    deleteCurves.add(deleteAllCurves);
    menuActions.add(deleteCurves);

    JMenu changeVisibility = new JMenu("Изменить видимость");
    changeVisibility.add(drawLinesCurrentFrame);
    changeVisibility.add(drawLinesAllFrames);
    changeVisibility.add(drawPointsCurrentFrame);
    changeVisibility.add(drawPointsAllFrames);
    changeVisibility.add(activity);
    menuActions.add(changeVisibility);

    addActionsListeners(changeCurrentColor, changeCurrentColors);
    deleteCurrentCurve.addActionListener(actionEvent -> curvesPanel.deleteCurrentCurve());
    deleteAllCurves.addActionListener(actionEvent -> curvesPanel.deleteAllCurves());
    drawLinesAllFrames.addActionListener(actionEvent -> curvesPanel.changeDrawLinesFrames());
    drawPointsAllFrames.addActionListener(actionEvent -> curvesPanel.changeDrawPointsFrames());
    drawLinesCurrentFrame.addActionListener(actionEvent -> curvesPanel.changeDrawLinesFrame());
    drawPointsCurrentFrame.addActionListener(actionEvent -> curvesPanel.changeDrawPointsFrame());
    activity.addActionListener(actionEvent -> curvesPanel.changeActivity());

    return menuActions;
  }

  private JMenu initializeCanvasesMenu() {
    JMenu choiceCanvas = new JMenu("Выбрать холст");
    JMenuItem canvas1 = new JMenuItem("Холст 1");
    JMenuItem canvas2 = new JMenuItem("Холст 2");
    JMenuItem canvas3 = new JMenuItem("Холст 3");
    JMenuItem canvas4 = new JMenuItem("Холст 4");
    JMenuItem canvas5 = new JMenuItem("Холст 5");
    JMenuItem canvas6 = new JMenuItem("Холст 6");
    JMenuItem goBackCanvas = new JMenuItem("Перейти к предыдущему холсту");
    JMenuItem goAheadCanvas = new JMenuItem("Перейти к следующему холсту");

    List<JMenuItem> menuCanvasesItems = new ArrayList<>() {{
      add(canvas1);
      add(canvas2);
      add(canvas3);
      add(canvas4);
      add(canvas5);
      add(canvas6);
      add(goBackCanvas);
      add(goAheadCanvas);
    }};

    for (int index = 1; index <= 6; index++) {
      menuCanvasesItems.get(index - 1).setAccelerator(KeyStroke.getKeyStroke(48 + index, InputEvent.SHIFT_DOWN_MASK));
      choiceCanvas.add(menuCanvasesItems.get(index - 1));
    }

    choiceCanvas.add(goBackCanvas);
    choiceCanvas.add(goAheadCanvas);
    goBackCanvas.setAccelerator(KeyStroke.getKeyStroke('Z', InputEvent.SHIFT_DOWN_MASK));
    goAheadCanvas.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.SHIFT_DOWN_MASK));

    addCanvasListeners(menuCanvasesItems);

    return choiceCanvas;
  }

  private JMenu initializeCurvesMenu() {
    JMenu choiceToDraw = new JMenu("Выбрать тип кривой для рисования");
    JMenuItem curve1 = new JMenuItem("Кубический сплайн");
    JMenuItem curve2 = new JMenuItem("Кривая Безье");
    JMenuItem curve21 = new JMenuItem("Безье сплайн");
    JMenuItem curve3 = new JMenuItem("B-сплайн");
    JMenuItem curve4 = new JMenuItem("Beta-сплайн");
    JMenuItem editBetas = new JMenuItem("Изменить Beta значения для Beta сплайна");
    JMenuItem drawNewCurve = new JMenuItem("Начать рисовать новую кривую текущего типа");
    JMenuItem goBack = new JMenuItem("Перейти к предыдущей кривой");
    JMenuItem goAhead = new JMenuItem("Перейти к следующей кривой");

    curve1.setAccelerator(KeyStroke.getKeyStroke('1'));
    curve2.setAccelerator(KeyStroke.getKeyStroke('2'));
    curve21.setAccelerator(KeyStroke.getKeyStroke('3'));
    curve3.setAccelerator(KeyStroke.getKeyStroke('4'));
    curve4.setAccelerator(KeyStroke.getKeyStroke('5'));
    drawNewCurve.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_DOWN_MASK));
    goBack.setAccelerator(KeyStroke.getKeyStroke('Z', InputEvent.CTRL_DOWN_MASK));
    goAhead.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK));
    editBetas.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_DOWN_MASK));

    choiceToDraw.add(curve1);
    choiceToDraw.add(curve2);
    choiceToDraw.add(curve21);
    choiceToDraw.add(curve3);
    choiceToDraw.add(curve4);
    choiceToDraw.add(editBetas);
    choiceToDraw.add(drawNewCurve);
    choiceToDraw.add(goBack);
    choiceToDraw.add(goAhead);

    addDrawListeners(new ArrayList<>() {{
      add(curve1);
      add(curve2);
      add(curve21);
      add(curve3);
      add(curve4);
      add(goBack);
      add(goAhead);
      add(drawNewCurve);
      add(editBetas);
    }});

    return choiceToDraw;
  }

  private void addCanvasListeners(List<JMenuItem> menuCanvasesItems) {
    for (int index = 0; index < menuCanvasesItems.size() - 2; index++) {
      int finalizeIndex = index;
      menuCanvasesItems.get(index).addActionListener(actionEvent -> curvesPanel.switchCanvasTo(finalizeIndex));
    }

    menuCanvasesItems.get(6).addActionListener(actionEvent -> curvesPanel.changeCurrentCanvas(-1));
    menuCanvasesItems.get(7).addActionListener(actionEvent -> curvesPanel.changeCurrentCanvas(1));
  }

  private void addDrawListeners(List<JMenuItem> menuCurvesItems) {
    menuCurvesItems.get(0).addActionListener(actionEvent -> curvesPanel.setCurrentCurve(CUBIC_SPLINE));
    menuCurvesItems.get(1).addActionListener(actionEvent -> curvesPanel.setCurrentCurve(BEZIER_CURVE));
    menuCurvesItems.get(2).addActionListener(actionEvent -> curvesPanel.setCurrentCurve(BEZIER_CURVE_2));
    menuCurvesItems.get(3).addActionListener(actionEvent -> curvesPanel.setCurrentCurve(B_SPLINE));
    menuCurvesItems.get(4).addActionListener(actionEvent -> curvesPanel.setCurrentCurve(BETA_SPLINE));
    menuCurvesItems.get(8).addActionListener(actionEvent -> {
      String betas = JOptionPane.showInputDialog(null, "Введите значения beta1 и beta2 через пробел");
      try {
        if (betas != null) {
          double beta1 = Double.parseDouble(betas.split(" ")[0]);
          double beta2 = Double.parseDouble(betas.split(" ")[1]);

          curvesPanel.setBetas(beta1, beta2);
        }
      } catch (NumberFormatException ignored) {
      }
    });

    menuCurvesItems.get(5).addActionListener(actionEvent -> curvesPanel.changeCurrentCurve(-1));
    menuCurvesItems.get(6).addActionListener(actionEvent -> curvesPanel.changeCurrentCurve(1));
    menuCurvesItems.get(7).addActionListener(actionEvent -> curvesPanel.addNewCurrentCurve());
  }

  private void addActionsListeners(JMenuItem changeCurrentColor, JMenuItem changeCurrentColors) {
    changeCurrentColor.addActionListener(actionEvent -> {
      if (curvesPanel.getCurrentCurveIndex() == -1) {
        JOptionPane.showMessageDialog(null, "На холсте ничего нет",
                "Ошибка", JOptionPane.ERROR_MESSAGE);
      } else {
        Color tempColor = JColorChooser.showDialog(this, "Выбрать цвет", curvesPanel.getCurrentColor());

        if (tempColor != null) {
          curvesPanel.setCurrentColor(tempColor);
          container.repaint();
        }
      }
    });

    changeCurrentColors.addActionListener(actionEvent -> {
      if (curvesPanel.getCurrentCurveIndex() == -1) {
        JOptionPane.showMessageDialog(null, "На холсте ничего нет",
                "Ошибка", JOptionPane.ERROR_MESSAGE);
      } else {
        Color tempColor = JColorChooser.showDialog(this, "Выбрать цвет", curvesPanel.getCurrentColor());

        if (tempColor != null) {
          curvesPanel.setCurrentColors(tempColor);
          container.repaint();
        }
      }
    });
  }

  private JLabel initializeLabelMenu() {
    JLabel label = new JLabel();
    curvesPanel.addLabel(label);

    return label;
  }
}
