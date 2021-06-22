package one.goranson.logboot.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ListTableModel;
import lombok.Setter;
import one.goranson.logboot.dto.LogItem;
import one.goranson.logboot.service.LogService;

public class LogTableView extends TableView<LogItem> {

  private final LogService logService;
  @Setter
  private CommandPanel commandPanel;

  public LogTableView(ListTableModel<LogItem> model, LogService logService) {
    super(model);
    this.init();
    this.logService = logService;
  }

  private void init() {
    this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.setCellSelectionEnabled(true);
    this.setStriped(true);
    this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    this.setAutoCreateRowSorter(true);
    this.getEmptyText().setText("Enter hostname and fetch logs!");
    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == MouseEvent.BUTTON1) {
          JTable source = (JTable) mouseEvent.getSource();
          int rowAtPoint = source.rowAtPoint(mouseEvent.getPoint());
          var logger = (String) LogTableView.this.getValueAt(rowAtPoint, 0);
          var level = (String) LogTableView.this.getValueAt(rowAtPoint, 1);
          var dialog = new UpdateLogDialog(logger, level);
          dialog.show();
          if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            var logItem = dialog.getLogItem();
            try {
              logService.updateLog(commandPanel.getHost(), logItem);
              commandPanel.searchLogItems(false);
            } catch (Exception e) {
              commandPanel.getErrorLabel().setErrorText("Failed to update log due to: " + e.getMessage(), Color.red);
              e.printStackTrace();
            }
          }
        }
        super.mousePressed(mouseEvent);
      }
    });
  }
}
