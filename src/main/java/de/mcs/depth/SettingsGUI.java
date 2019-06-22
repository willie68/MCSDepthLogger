package de.mcs.depth;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SystemColor;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;

public class SettingsGUI extends JDialog {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private final JPanel contentPanel = new JPanel();
  private UploadSettingsPanel uploadPanel;
  private MapSettingsPanel mapPanel;

  private CommonSettingsPanel commonPanel;

  private LoggerSettingsPanel loggerPanel;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      SettingsGUI dialog = new SettingsGUI();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  private SettingsGUI() {
  }

  /**
   * Create the dialog.
   */
  public SettingsGUI(JFrame frame) {
    super(frame);
    initGUI(frame);
  }

  @Action
  public void ok() {
    this.setVisible(false);
    ProgramConfig.getInstance().setTrackFolder(commonPanel.getRouteFolder());
    ProgramConfig.getInstance().setMeasureSystem(commonPanel.getMeasureSystem());
    ProgramConfig.getInstance().setAutoAdapt(commonPanel.isAutoAdapt());

    ProgramConfig.getInstance().setUploadURL(uploadPanel.getUploadUrl());
    ProgramConfig.getInstance().setUploadUsername(uploadPanel.getUsername());
    ProgramConfig.getInstance().setUploadPassword(uploadPanel.getPassword());
    ProgramConfig.getInstance().setUploadBatch(uploadPanel.getBatchFile());
    ProgramConfig.getInstance().setInternalProcessing(uploadPanel.getInternalProcessing());

    ProgramConfig.getInstance().setShowMapIn(mapPanel.getShowMapIn());
    ProgramConfig.getInstance().setShowMapProgram(mapPanel.getShowMapProgram());
    ProgramConfig.getInstance().setMaxMapPoints(mapPanel.getMaxMapPoints());

    ProgramConfig.getInstance().setBootloaderVersion(loggerPanel.getBootloaderVersion());
  }

  private void initValues() {
    commonPanel.setRouteFolder(ProgramConfig.getInstance().getTrackFolder());
    commonPanel.setMeasureSystem(ProgramConfig.getInstance().getMeasureSystem().name());
    commonPanel.setAutoAdapt(ProgramConfig.getInstance().isAutoAdapt());

    uploadPanel.setUploadUrl(ProgramConfig.getInstance().getUploadURL());
    uploadPanel.setUsername(ProgramConfig.getInstance().getUploadUsername());
    uploadPanel.setPassword(ProgramConfig.getInstance().getUploadPassword());
    uploadPanel.setBatchFile(ProgramConfig.getInstance().getUploadBatch());
    uploadPanel.setInternalProcessing(ProgramConfig.getInstance().isInternalProcessing());

    mapPanel.setShowMapIn(ProgramConfig.getInstance().getShowMapIn());
    mapPanel.setShowMapProgram(ProgramConfig.getInstance().getShowMapProgram());
    mapPanel.setMaxMapPoints(ProgramConfig.getInstance().getMaxMapPoints());

    loggerPanel.setBootloaderVersion(ProgramConfig.getInstance().getBootloaderVersion());
  }

  @Action
  public void cancel() {
    this.setVisible(false);
  }

  private void initGUI(JFrame frame) {
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    try {
      setTitle(getTranslatedString("settingsGUI.title"));

      int screenHeight = frame.getHeight();
      int screenWidth = frame.getWidth();

      setSize(600, 500);
      setLocation((screenWidth / 2) - (getWidth() / 2) + frame.getX(),
          (screenHeight / 2) - (getHeight() / 2) + frame.getY());

      getContentPane().setLayout(new BorderLayout());
      contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
      getContentPane().add(contentPanel, BorderLayout.CENTER);
      contentPanel.setLayout(new BorderLayout(0, 0));
      {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(4, 4, 4, 4));
        contentPanel.add(panel, BorderLayout.NORTH);
        panel.setLayout(new BorderLayout(0, 0));
        {
          JLabel lblNewLabel = new JLabel("");
          lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
          lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
          lblNewLabel.setIcon(new ImageIcon(ConfigGUI.class.getResource("/de/mcs/depth/resources/ico/logo.png")));
          panel.add(lblNewLabel, BorderLayout.WEST);
        }
        {
          JTextArea textPane = new JTextArea();
          textPane.setFont(new Font("Tahoma", textPane.getFont().getStyle(), textPane.getFont().getSize()));
          textPane.setBackground(SystemColor.control);
          textPane.setLineWrap(true);
          textPane.setWrapStyleWord(true);
          textPane.setName("description");
          textPane.setEditable(false);
          panel.add(textPane, BorderLayout.CENTER);
        }
      }
      getContentPane().add(contentPanel, BorderLayout.CENTER);
      {
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        createCommonTab(tabbedPane);
        createUploadTab(tabbedPane);
        createMapTab(tabbedPane);
        createLoggerTab(tabbedPane);

      }
      {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        {
          JButton okButton = new JButton("OK");
          okButton.setName("OK");
          okButton.setAction(getAppActionMap().get("ok"));
          buttonPane.add(okButton);
          getRootPane().setDefaultButton(okButton);
        }
        {
          JButton cancelButton = new JButton("Cancel");
          cancelButton.setAction(getAppActionMap().get("cancel"));
          cancelButton.setName("Cancel");
          buttonPane.add(cancelButton);
        }
        Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(getContentPane());
        initValues();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @param tabbedPane
   * @return
   */
  private void createCommonTab(JTabbedPane tabbedPane) {
    commonPanel = new CommonSettingsPanel();
    commonPanel.setName("common");
    tabbedPane.addTab(getTranslatedString("commonPanel.tab"), null, commonPanel, null);
  }

  /**
   * @param tabbedPane
   * @return
   */
  private void createLoggerTab(JTabbedPane tabbedPane) {
    loggerPanel = new LoggerSettingsPanel();
    loggerPanel.setName("logger");
    tabbedPane.addTab(getTranslatedString("loggerPanel.tab"), null, loggerPanel, null);
  }

  /**
   * @param tabbedPane
   * @return
   */
  private void createMapTab(JTabbedPane tabbedPane) {
    mapPanel = new MapSettingsPanel();
    mapPanel.setName("upload");
    tabbedPane.addTab(getTranslatedString("mapPanel.tab"), null, mapPanel, null);
  }

  private void createUploadTab(JTabbedPane tabbedPane) {
    uploadPanel = new UploadSettingsPanel();
    uploadPanel.setName("upload");
    tabbedPane.addTab(getTranslatedString("uploadPanel.tab"), null, uploadPanel, null);
  }

  /**
   * Returns the action map used by this application. Actions defined using the
   * Action annotation are returned by this method
   */
  private ApplicationActionMap getAppActionMap() {
    return Application.getInstance().getContext().getActionMap(this);
  }

  private String getTranslatedString(final String key, Object... object) {
    String string = Application.getInstance().getContext().getResourceMap(getClass()).getString(key, object);
    return string;
  }
}
