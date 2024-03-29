package one.goranson.logboot.ui;

import static com.google.common.base.Throwables.getRootCause;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.ConnectException;
import java.nio.channels.UnresolvedAddressException;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLException;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.icons.AllIcons;
import com.intellij.ide.plugins.newui.ListPluginComponent;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.ErrorLabel;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.SideBorder;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.panels.NonOpaquePanel;
import lombok.extern.slf4j.Slf4j;
import one.goranson.logboot.dto.LogItem;
import one.goranson.logboot.exception.CommunicationException;
import one.goranson.logboot.service.LogService;

@Slf4j
public class CommandPanel extends NonOpaquePanel {
  private SearchTextField filterField;
  private SearchTextField hostField;
  private ErrorLabel errorLabel;
  private final LogTableView logTableView;
  private final LogTableModel logTableModel;
  private final LogService logService;

  public CommandPanel(LogTableModel tableModel, LogTableView logItemTableView,
      LogService logService) {
    this.logTableModel = tableModel;
    this.logTableView = logItemTableView;
    this.logService = logService;
    // Spaghetti wiring to be able to fetch the current host
    logItemTableView.setCommandPanel(this);
    this.init();
  }

  private void init() {
    this.setLayout(new FlowLayout(FlowLayout.LEFT));
    this.setBorder(BorderFactory.createEmptyBorder());
    this.setBorder(
        IdeBorderFactory.createBorder(SideBorder.TOP | SideBorder.RIGHT | SideBorder.BOTTOM));

    errorLabel = new ErrorLabel();
    this.add(createFilterField());
    this.add(createHostnameField());
    this.add(errorLabel);

    var toolbar = this.createToolbar();
    toolbar.setTargetComponent(this);
    this.add(toolbar.getComponent());
  }

  public String getHost() {
    return hostField.getText();
  }

  public ErrorLabel getErrorLabel() {
    return errorLabel;
  }

  private SearchTextField createHostnameField() {
    var hostField = new SearchTextField();
    hostField.setText("localhost:8080");
    var titleText = "Hostname";
    var titleSearchEmptyText = hostField.getTextEditor().getEmptyText();
    titleSearchEmptyText.appendText(titleText,
        new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, ListPluginComponent.GRAY_COLOR));
    hostField.addKeyboardListener(this.keyAdapterSearchWithoutCache());

    this.hostField = hostField;
    return this.hostField;
  }

  private SearchTextField createFilterField() {
    this.filterField = new SearchTextField();
    var titleText = "Search";
    var titleSearchEmptyText = this.filterField.getTextEditor().getEmptyText();
    titleSearchEmptyText.appendText(titleText,
        new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, ListPluginComponent.GRAY_COLOR));
    this.filterField.addKeyboardListener(this.keyAdapterSearch());
    return this.filterField;
  }

  @NotNull
  private ActionToolbar createToolbar() {
    DefaultActionGroup actionGroup = new DefaultActionGroup();
    actionGroup.add(new RefreshAction());
    return ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actionGroup, true);
  }

  @NotNull
  private KeyAdapter keyAdapterSearch() {
    return new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        searchLogItems(true);
      }
    };
  }

  @NotNull
  private KeyAdapter keyAdapterSearchWithoutCache() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (KeyEvent.VK_ENTER == e.getKeyCode()) {
          CommandPanel.this.searchLogItems(false);
        }
      }
    };
  }

  public void searchLogItems(boolean userCache) {
    List<LogItem> logItems = new ArrayList<>();
    errorLabel.setText("");
    try {
      logItems = logService.fetchLogs(hostField.getText(), filterField.getText().trim(), userCache);
      if (logItems.isEmpty()) {
        this.logTableView.getEmptyText().setText("No results found for your search criteria");
      }
    } catch (Exception exception) {
      handleSearchException(exception);
    }
    this.logTableModel.setItems(logItems);
    this.logTableView.updateColumnSizes();
  }

  private void handleSearchException(Exception exception) {
    exception.printStackTrace();
    var errorMessage = "Something bad happen \uD83D\uDE14 (error message: "
                       + exception.getMessage() + ").";
    var rootException = getRootCause(exception);
    if (rootException instanceof UnresolvedAddressException) {
      errorMessage = "\uD83D\uDD8A️ Could not fetch logs, please enter a valid hostname";
    } else if (rootException instanceof IllegalArgumentException) {
      errorMessage = "\uD83D\uDD8A️ Could not fetch logs, " + rootException.getMessage();
    } else if (exception.getCause() != null &&
               exception.getCause().getCause() instanceof SSLException) {
      errorMessage = "\uD83D\uDD12 Could not fetch logs, problem with https? (error message: "
                     + exception.getCause().getCause().getMessage() + ").";
    } else if (exception.getCause() instanceof ConnectException) {
      errorMessage =
          "\uD83E\uDD37 Could not fetch logs, check that the hostname is correct and the server "
          + "is running on the given port";
    } else if (exception.getCause() instanceof CommunicationException) {
      errorMessage = exception.getCause().getMessage();
    } else {
      errorMessage = "Failed to perform the operation: " + exception.getMessage();
    }
    this.logTableView.getEmptyText().setText(errorMessage);
  }

  public class RefreshAction extends DumbAwareAction {
    protected RefreshAction() {
      super("Fetch logs without cache", "Fetch logs without cache", AllIcons.Actions.Refresh);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
      CommandPanel.this.searchLogItems(false);
    }
  }

}
