package one.goranson.logboot;

import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.content.ContentFactory;
import one.goranson.logboot.ui.LogTableModel;
import one.goranson.logboot.service.LogService;
import one.goranson.logboot.ui.LogTableView;
import one.goranson.logboot.ui.CommandPanel;
import one.goranson.logboot.ui.LogTablePanel;

public class InitPlugin implements ToolWindowFactory {
  private final LogService logService;

  public InitPlugin() {
    this.logService = new LogService();
  }

  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    var contentFactory = ContentFactory.SERVICE.getInstance();
    var content = contentFactory.createContent(createContent(), "", false);
    toolWindow.getContentManager().addContent(content);
  }

  public JComponent createContent() {
    // The main plugin window
    var contentToolWindow = new SimpleToolWindowPanel(true, true);

    // Object holding the table data and generating header names
    var tableModel = new LogTableModel();

    // Building the table UI and handle mouse events
    var logTableView = new LogTableView(tableModel, logService);

    // The panel for the table
    var logTablePanel = new LogTablePanel(logTableView);

    // The panel for searching and entering the hostname
    var commandPanel = new CommandPanel(tableModel, logTableView, logService);

    // Combining all components together
    var horizontalSplitter = new OnePixelSplitter(true, 0.0f);
    horizontalSplitter.setBorder(BorderFactory.createEmptyBorder());
    horizontalSplitter.setDividerPositionStrategy(Splitter.DividerPositionStrategy.KEEP_FIRST_SIZE);
    horizontalSplitter.setResizeEnabled(false);
    horizontalSplitter.setFirstComponent(commandPanel);
    horizontalSplitter.setSecondComponent(logTablePanel);
    contentToolWindow.add(horizontalSplitter);

    return contentToolWindow;
  }
}
