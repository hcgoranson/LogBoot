package one.goranson.logboot.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.table.TableCellRenderer;
import org.jetbrains.annotations.Nullable;
import com.intellij.ui.components.labels.BoldLabel;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import one.goranson.logboot.dto.LogItem;

public class LogTableModel extends ListTableModel<LogItem> {

  private static final String[] COLUMNS = {"Logger", "Level"};

  public LogTableModel() {
    super(generateColumnInfo(), new ArrayList<>());
  }

  private static ColumnInfo<LogItem, String>[] generateColumnInfo() {
    var columnInfos = new ColumnInfo[COLUMNS.length];
    var index = new AtomicInteger();
    // Create a column object for each defined column header
    Arrays.stream(COLUMNS).forEach(eachColumn -> {
          columnInfos[index.get()] = new ColumnInfo<LogItem, String>(eachColumn) {
            @Nullable
            @Override
            public String valueOf(LogItem logItem) {
              switch (eachColumn) {
                case "Logger":
                  return logItem.getLogger();
                case "Level":
                  return logItem.getLevel();
                default:
                  return "Not Available";
              }
            }

            @Override
            public TableCellRenderer getCustomizedRenderer(LogItem o, TableCellRenderer renderer) {
              switch (eachColumn) {
                case "Level":
                  return (table, value, isSelected, hasFocus, row, column) -> new BoldLabel(value.toString());
                default:
                  return super.getCustomizedRenderer(o, renderer);
              }
            }

          };
          index.getAndIncrement();
        }
    );
    return columnInfos;
  }
}
