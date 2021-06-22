package one.goranson.logboot.ui;

import java.awt.*;
import javax.swing.*;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.ui.DialogWrapper;
import one.goranson.logboot.dto.LogItem;

public class UpdateLogDialog extends DialogWrapper {

  private final String logger;
  private final String level;
  private final JTextField levelTextField;

  public UpdateLogDialog(String logger, String level) {
    super(true); // use current window as parent
    this.logger = logger;
    this.level = level;
    this.levelTextField = new JTextField(level);
    init();
    setTitle("Update log level");
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    var logPanel = new JPanel(new GridBagLayout());

    var gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    gridBagConstraints.insets = new Insets(10, 10, 10, 10);

    // add components to the panel
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    logPanel.add(new JLabel(logger + ": "), gridBagConstraints);

    gridBagConstraints.gridx = 1;
    logPanel.add(levelTextField, gridBagConstraints);

    // set border for the panel
    logPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(), ""));

    return logPanel;
  }

  public LogItem getLogItem() {
    return LogItem.builder()
        .logger(logger)
        .level(levelTextField.getText())
        .build();
  }

}
