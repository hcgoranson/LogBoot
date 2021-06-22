package one.goranson.logboot.ui;

import java.awt.*;
import javax.swing.*;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SideBorder;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.table.TableView;
import one.goranson.logboot.dto.LogItem;

public class LogTablePanel extends NonOpaquePanel {
  private final TableView<LogItem> resultsTable;

  public LogTablePanel(TableView<LogItem> resultsTable) {
    this.resultsTable = resultsTable;
    this.init();
  }

  private void init() {
    this.setBorder(BorderFactory.createEmptyBorder());
    this.setBorder(IdeBorderFactory.createBorder(SideBorder.TOP | SideBorder.RIGHT));
    var scrollPanel = new JPanel();
    scrollPanel.setBorder(BorderFactory.createEmptyBorder());
    scrollPanel.setLayout(new BorderLayout());
    scrollPanel.add(ScrollPaneFactory.createScrollPane(this.resultsTable), BorderLayout.CENTER);
    this.setLayout(new BorderLayout());
    this.add(scrollPanel, BorderLayout.CENTER);
  }
}
