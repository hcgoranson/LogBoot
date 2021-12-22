package one.goranson.logboot.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.labels.BoldLabel;
import one.goranson.logboot.dto.LogItem;

public class UpdateLogDialog extends DialogWrapper {

  private final String logger;
  private final String level;
  private final JComboBox levelComboBox;

  public UpdateLogDialog(final String logger, final String level,
      final List<String> ranges) {
    super(true); // use current window as parent
    this.logger = logger;
    this.level = level;
    levelComboBox = new JComboBox(ranges.toArray());

    // Set selected item in the dropdown
    IntStream.range(0, levelComboBox.getItemCount())
        .filter(index -> level.equals(levelComboBox.getItemAt(index)) ? true : false)
        .findFirst()
        .ifPresent(value -> levelComboBox.setSelectedIndex(value));

    init();
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    var logPanel = new JPanel(new GridBagLayout());

    var gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    gridBagConstraints.insets = new Insets(10, 10, 10, 10);

    // Add the label of the logger name
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    logPanel.add(new BoldLabel(logger + ": "), gridBagConstraints);

    // Add the dropdown with the levels
    gridBagConstraints.gridx = 1;
    logPanel.add(levelComboBox, gridBagConstraints);

    // Set border for the panel
    var border =
        BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "✔️ Update log level");
    border.setTitleJustification(TitledBorder.CENTER);
    logPanel.setBorder(border);

    return logPanel;
  }

  public LogItem getLogItem() {
    return LogItem.builder()
        .logger(logger)
        .level(levelComboBox.getSelectedItem().toString())
        .build();
  }

}
