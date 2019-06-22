/**
 * MCS Media Computer Software
 * Copyright (C) 2014 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: MapGUI.java
 * EMail: W.Klaas@gmx.de
 * Created: 26.01.2014 Willie
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package de.mcs.depth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.UrlTileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import de.mcs.depth.logger.DefaultSentenceFilter;
import de.mcs.depth.logger.LoggerFile;
import de.mcs.depth.logger.ProgressCallback;
import de.mcs.depth.logger.datagramms.Datagram;
import de.mcs.depth.logger.datagramms.NMEAConstants.DEPTH_UNIT;
import de.mcs.depth.logger.datagramms.nmea.NMEADepthSentence;
import de.mcs.depth.logger.datagramms.nmea.NMEAGGASentence;
import de.mcs.depth.logger.datagramms.nmea.NMEAOSMAccSentence;
import de.mcs.depth.logger.datagramms.nmea.NMEAOSMGyrSentence;
import de.mcs.depth.logger.datagramms.nmea.NMEAOSMVccSentence;
import de.mcs.depth.logger.datagramms.nmea.NMEARMCSentence;
import de.mcs.depth.mapviewer.EndWaypoint;
import de.mcs.depth.mapviewer.MyWaypointRenderer;
import de.mcs.depth.mapviewer.RoutePainter;
import de.mcs.depth.mapviewer.SelectionAdapter;
import de.mcs.depth.mapviewer.SelectionPainter;
import de.mcs.depth.mapviewer.StartWaypoint;
import de.mcs.depth.mapviewer.ThisWaypoint;
import de.mcs.depth.mapviewer.TileServerInfo;
import de.mcs.depth.utils.LocalResponseCache;
import de.mcs.depth.utils.LoggingTool;
import de.mcs.depth.utils.MeasureConverter;
import de.mcs.utils.Files;
import de.mcs.utils.Location;
import de.mcs.utils.LocationUtils;
import de.mcs.utils.NMEAUtils;
import de.mcs.utils.RouteConverterUtils;
import de.mcs.utils.StartProcess;
import de.mcs.utils.URLUtilities;

/**
 * @author Willie
 * 
 */
public class MapGUI extends JFrame {

  private static Logger logger = Logger.getLogger(MapGUI.class);

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private static int MAX_ACC_POINTS = 1000;
  /**
   * 
   */
  private static final String MAP_FRAME = "mapFrame";
  private JXMapViewer mapViewer;
  private JSplitPane splitPane;
  private List<GeoPosition> track;
  private XYSeries voltageSeries;
  private XYSeries accSeriesX;
  private XYSeries accSeriesY;
  private XYSeries accSeriesZ;
  private XYSeries gyrSeriesX;
  private XYSeries gyrSeriesY;
  private XYSeries gyrSeriesZ;
  private XYSeries heightSeries;
  private XYSeries depthSeries;
  private List<TileFactory> factories;
  private JPanel chartPanel;
  private JComboBox<String> jcbMapProvider;
  private JCheckBox jcbAcc;
  private JCheckBox jcbVcc;
  private JCheckBox jcbDepth;
  private JCheckBox jcbGyr;
  private XYPlot xyPlot;
  private JFreeChart chart;
  private ValueAxis axisDepth;
  private ValueAxis axisGyro;
  private ValueAxis axisAcc;
  private NumberAxis axisVcc;
  private JPopupMenu overlaysMenu;
  private JButton overlaysButton;
  private JCheckBoxMenuItem jcbSportMark;
  private JCheckBoxMenuItem jcbSeaMark;
  private UrlTileFactoryInfo mySeamark;
  private UrlTileFactoryInfo mySportMark;
  private JLabel zoomLabel;
  private JLabel copyrightLabel;
  private String providerURL;
  private JXMapViewer thumbMap;
  private JLabel dateLabel;
  private XYLineAndShapeRenderer vccRenderer;
  private XYLineAndShapeRenderer accRenderer;
  private XYLineAndShapeRenderer gyrRenderer;
  private XYLineAndShapeRenderer depthRenderer;
  private ChartPanel chartPanel2;
  private ArrayList<Datagram> gpsList;
  protected ThisWaypoint marker;
  private WaypointPainter<Waypoint> waypointPainter;
  private Set<Waypoint> waypoints;
  private XYSeries speedSeries;
  private XYLineAndShapeRenderer speedRenderer;
  private NumberAxis axisSpeed;
  private JCheckBox jcbSpeed;
  private JLabel jblPosition;
  private JLabel jblSpeed;
  private JLabel jblDateTime;

  private JLabel taskLabel;

  private ApplicationActionMap actions;

  /**
   * Create the panel.
   */
  public MapGUI() {
    factories = new ArrayList<TileFactory>();
    actions = Application.getInstance().getContext().getActionMap(this);

    // Setup local file cache
    File cacheDir = new File(ProgramConfig.getInstance().getCacheFolder());
    if (!cacheDir.exists()) {
      cacheDir.mkdirs();
    }

    createMapViewer(cacheDir);
    createThumbMap();

    createInfoPanel();

    addLicenseLabel();

    splitPane = new JSplitPane();
    splitPane.setLastDividerLocation(100);
    splitPane.setOneTouchExpandable(true);
    splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

    createChartPanel();

    splitPane.setRightComponent(chartPanel);
    splitPane.setLeftComponent(mapViewer);

    getContentPane().add(splitPane);
    splitPane.setDividerLocation(100);

    JToolBar toolBar = getToolBar();
    getContentPane().add(toolBar, BorderLayout.NORTH);

    this.setBounds(ProgramConfig.getInstance().getFrameData(MAP_FRAME));

    addWindowListener(new WindowAdapter() {

      @Override
      public void windowOpened(WindowEvent e) {
        if ((track != null) && (track.size() > 0)) {
          mapViewer.zoomToBestFit(new HashSet<GeoPosition>(track), 0.7);
        }
      }

      @Override
      public void windowClosing(WindowEvent arg0) {
        ProgramConfig.getInstance().setMapHeight(splitPane.getDividerLocation());
        ProgramConfig.getInstance().saveFrameData(MAP_FRAME, getBounds());
        ProgramConfig.getInstance().setMapProvider(jcbMapProvider.getSelectedItem().toString());
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
       */
      @Override
      public void windowClosed(WindowEvent arg0) {
        ProgramConfig.getInstance().setMapHeight(splitPane.getDividerLocation());
        ProgramConfig.getInstance().saveFrameData(MAP_FRAME, getBounds());
        ProgramConfig.getInstance().setMapProvider(jcbMapProvider.getSelectedItem().toString());
      }

    });

    setTitle(getTranslatedString("mapGUI.title"));
    try {
      String iconPath = getTranslatedString("mapGUI.icon");
      setIconImage(ImageIO.read(getClass().getResource(iconPath)));
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    Application.getInstance().getContext().getResourceMap(getClass()).injectComponents(getContentPane());

    splitPane.setDividerLocation(ProgramConfig.getInstance().getMapHeight());

    showZoomLevel();
    showCopyRight();
  }

  private void createInfoPanel() {
    final JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    panel.setVisible(true);

    JPanel floatingPanel = new JPanel(new BorderLayout());
    floatingPanel.add(createInfoToolbar(panel), BorderLayout.NORTH);

    jblPosition = new JLabel();
    jblPosition.setOpaque(false);
    panel.add(jblPosition);

    jblDateTime = new JLabel();
    jblDateTime.setOpaque(false);
    panel.add(jblDateTime);

    jblSpeed = new JLabel();
    jblSpeed.setOpaque(false);
    panel.add(jblSpeed);

    panel.setVisible(true);

    floatingPanel.add(panel, BorderLayout.CENTER);

    GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    mapViewer.add(floatingPanel, gridBagConstraints);
  }

  /**
   * @param panel
   * @return
   */
  private JToolBar createInfoToolbar(final JPanel panel) {
    JToolBar toolbar = new JToolBar();
    toolbar.setFloatable(false);

    JButton jbUp = new JButton();
    jbUp.setName("popup.up");
    jbUp.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        panel.setVisible(false);
      }
    });
    toolbar.add(jbUp);

    JButton jbDown = new JButton();
    jbDown.setName("popup.down");
    jbDown.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        panel.setVisible(true);
      }
    });
    toolbar.add(jbDown);

    // toolbar.addSeparator(new Dimension(4, 9));
    toolbar.addSeparator();

    JLabel lbTitle = new JLabel();
    lbTitle.setName("popup.title");
    toolbar.add(lbTitle);
    return toolbar;
  }

  private void addLicenseLabel() {
    GridBagConstraints gridBagConstraints;
    copyrightLabel = new JLabel();
    copyrightLabel.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        if (Desktop.isDesktopSupported()) {
          Desktop desktop = Desktop.getDesktop();
          try {
            desktop.browse(new URI(providerURL));
          } catch (IOException | URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
        }
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    mapViewer.add(copyrightLabel, gridBagConstraints);
  }

  private void createThumbMap() {
    thumbMap = new JXMapViewer();
    thumbMap.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    thumbMap.setMinimumSize(new java.awt.Dimension(100, 100));
    thumbMap.setPreferredSize(new java.awt.Dimension(100, 100));
    thumbMap.setLayout(new java.awt.GridBagLayout());
    GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    mapViewer.add(thumbMap, gridBagConstraints);
    thumbMap.setOverlayPainter(new Painter<JXMapViewer>() {
      @Override
      public void paint(Graphics2D g, JXMapViewer map, int width, int height) {
        // get the viewport rect of the main map
        Rectangle mainMapBounds = mapViewer.getViewportBounds();

        // convert to Point2Ds
        Point2D upperLeft2D = mainMapBounds.getLocation();
        Point2D lowerRight2D = new Point2D.Double(upperLeft2D.getX() + mainMapBounds.getWidth(),
            upperLeft2D.getY() + mainMapBounds.getHeight());

        // convert to GeoPostions
        GeoPosition upperLeft = mapViewer.getTileFactory().pixelToGeo(upperLeft2D, mapViewer.getZoom());
        GeoPosition lowerRight = mapViewer.getTileFactory().pixelToGeo(lowerRight2D, mapViewer.getZoom());

        // convert to Point2Ds on the mini-map
        upperLeft2D = map.getTileFactory().geoToPixel(upperLeft, map.getZoom());
        lowerRight2D = map.getTileFactory().geoToPixel(lowerRight, map.getZoom());

        g = (Graphics2D) g.create();
        Rectangle rect = map.getViewportBounds();
        // p("rect = " + rect);
        g.translate(-rect.x, -rect.y);
        // Point2D centerpos =
        // map.getTileFactory().geoToPixel(mapCenterPosition, map.getZoom());
        // p("center pos = " + centerpos);
        g.setPaint(Color.RED);
        // g.drawRect((int)centerpos.getX()-30,(int)centerpos.getY()-30,60,60);
        g.drawRect((int) upperLeft2D.getX(), (int) upperLeft2D.getY(), (int) (lowerRight2D.getX() - upperLeft2D.getX()),
            (int) (lowerRight2D.getY() - upperLeft2D.getY()));
        g.setPaint(new Color(255, 0, 0, 50));
        g.fillRect((int) upperLeft2D.getX(), (int) upperLeft2D.getY(), (int) (lowerRight2D.getX() - upperLeft2D.getX()),
            (int) (lowerRight2D.getY() - upperLeft2D.getY()));
        // g.drawOval((int)lowerRight2D.getX(),(int)lowerRight2D.getY(),1,1);
        g.dispose();
      }
    });
  }

  /**
   * @param cacheDir
   * @return
   */
  private void createMapViewer(File cacheDir) {
    LocalResponseCache.installResponseCache(cacheDir, false);
    mySeamark = new UrlTileFactoryInfo("OpenSeaMap Seamarks", 19, "http://t1.openseamap.org/seamark");

    mySportMark = new UrlTileFactoryInfo("OpenSeaMap Sportmarks", 19, "http://t1.openseamap.org/sport");

    TileFactoryInfo osmInfo = new OSMTileFactoryInfo();
    factories.add(new DefaultTileFactory(osmInfo));

    TileFactoryInfo veInfo = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP);
    factories.add(new DefaultTileFactory(veInfo));

    // OSeaMTileFactoryInfo myInfo = new OSeaMTileFactoryInfo();
    // factories.add(new DefaultTileFactory(myInfo));

    List<TileServerInfo> tileServers = ProgramConfig.getInstance().getTileServerInfos();
    for (TileServerInfo tileServerInfo : tileServers) {
      UrlTileFactoryInfo tileInfo = new UrlTileFactoryInfo(tileServerInfo.getName(), tileServerInfo.getMaxZoom(),
          tileServerInfo.getBaseUrl());
      factories.add(new DefaultTileFactory(tileInfo));
    }

    mapViewer = new JXMapViewer();
    mapViewer.setLayout(new java.awt.GridBagLayout());
    mapViewer.setTileFactory(factories.get(0));
    mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

    GeoPosition hattingen = new GeoPosition(51.407344, 7.187611);

    // Set the focus
    mapViewer.setZoom(7);
    mapViewer.setAddressLocation(hattingen);

    // Add interactions
    MouseInputListener mia = new PanMouseInputListener(mapViewer);
    mapViewer.addMouseListener(mia);
    mapViewer.addMouseMotionListener(mia);
    mapViewer.addMouseListener(new CenterMapListener(mapViewer));
    mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
    mapViewer.addKeyListener(new PanKeyListener(mapViewer));

    mapViewer.addPropertyChangeListener("zoom", new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        showZoomLevel();
      }
    });

    mapViewer.addPropertyChangeListener("task", new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        showTaskSize();
      }
    });

    // Add a selection painter
    SelectionAdapter sa = new SelectionAdapter(mapViewer);
    SelectionPainter sp = new SelectionPainter(sa);
    mapViewer.addMouseListener(sa);
    mapViewer.addMouseMotionListener(sa);
    mapViewer.setOverlayPainter(sp);
    mapViewer.addMouseListener(new MouseAdapter() {

      /*
       * (non-Javadoc)
       * 
       * @see
       * java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
       */
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
          Point point = e.getPoint();
          GeoPosition position = mapViewer.convertPointToGeoPosition(point);
          showInfoLocation(position);
        }
        super.mouseClicked(e);
      }
    });
    ;
    mapViewer.addPropertyChangeListener("center", new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        Point2D mapCenter = (Point2D) evt.getNewValue();
        TileFactory tf = mapViewer.getTileFactory();
        GeoPosition mapPos = tf.pixelToGeo(mapCenter, mapViewer.getZoom());
        thumbMap.setCenterPosition(mapPos);
      }
    });
  }

  protected void showInfoLocation(GeoPosition position) {
    jblPosition.setText(getTranslatedString("info.location.text", position.getLatitude(), position.getLongitude()));
  }

  protected void showInfoSpeed(double speed) {
    String text = getTranslatedString("info.speed.text", 0.0);
    double speedConverted = MeasureConverter.convertSpeedNautical(ProgramConfig.getInstance().getMeasureSystem(),
        speed);
    text = getTransMeasureString("info.speed.text", speedConverted);
    jblSpeed.setText(text);
  }

  protected void showInfoDateTIme(Date dateTime) {
    jblDateTime.setText(getTranslatedString("info.datetime.text", dateTime, dateTime, dateTime));
  }

  /**
   * @return
   * 
   */
  private void createChartPanel() {

    XYSeriesCollection voltageDataset = new XYSeriesCollection();
    voltageSeries = new XYSeries(getTranslatedString("axis.y.voltage"));
    voltageDataset.addSeries(voltageSeries);

    chart = ChartFactory.createXYLineChart(getTranslatedString("xygraph.title"), getTranslatedString("axis.x.time"),
        getTranslatedString("axis.y.depth"), voltageDataset);
    chart.setTitle("");
    xyPlot = chart.getXYPlot();

    XYSeriesCollection accDataset = new XYSeriesCollection();
    accSeriesX = new XYSeries("Acc-X");
    accDataset.addSeries(accSeriesX);

    accSeriesY = new XYSeries("Acc-Y");
    accDataset.addSeries(accSeriesY);

    accSeriesZ = new XYSeries("Acc-Z");
    accDataset.addSeries(accSeriesZ);

    xyPlot.setDataset(1, accDataset);

    XYSeriesCollection gyrDataset = new XYSeriesCollection();
    gyrSeriesX = new XYSeries("Gyr-X");
    gyrDataset.addSeries(gyrSeriesX);

    gyrSeriesY = new XYSeries("Gyr-Y");
    gyrDataset.addSeries(gyrSeriesY);

    gyrSeriesZ = new XYSeries("Gyr-Z");
    gyrDataset.addSeries(gyrSeriesZ);

    xyPlot.setDataset(2, gyrDataset);

    XYSeriesCollection depthDataset = new XYSeriesCollection();

    depthSeries = new XYSeries(getTransMeasureString("axis.depth.text"));
    depthDataset.addSeries(depthSeries);

    heightSeries = new XYSeries(getTransMeasureString("axis.height.text"));
    depthDataset.addSeries(heightSeries);
    xyPlot.setDataset(3, depthDataset);

    XYSeriesCollection speedDataset = new XYSeriesCollection();

    speedSeries = new XYSeries(getTransMeasureString("axis.speed.text"));
    speedDataset.addSeries(speedSeries);

    xyPlot.setDataset(4, speedDataset);

    vccRenderer = new XYLineAndShapeRenderer(true, false);
    xyPlot.setRenderer(0, vccRenderer);

    accRenderer = new XYLineAndShapeRenderer(true, false);
    xyPlot.setRenderer(1, accRenderer);

    gyrRenderer = new XYLineAndShapeRenderer(true, false);
    xyPlot.setRenderer(2, gyrRenderer);

    depthRenderer = new XYLineAndShapeRenderer(true, false);
    xyPlot.setRenderer(3, depthRenderer);

    speedRenderer = new XYLineAndShapeRenderer(true, false);
    xyPlot.setRenderer(4, speedRenderer);

    axisVcc = new NumberAxis("Vcc");
    xyPlot.setRangeAxis(0, axisVcc);

    axisAcc = new NumberAxis("Acc");
    xyPlot.setRangeAxis(1, axisAcc);

    axisGyro = new NumberAxis("Gyro");
    xyPlot.setRangeAxis(2, axisGyro);

    axisDepth = new NumberAxis(getTransMeasureString("axis.depth.text"));
    xyPlot.setRangeAxis(3, axisDepth);

    axisSpeed = new NumberAxis(getTransMeasureString("axis.speed.text"));
    xyPlot.setRangeAxis(4, axisSpeed);

    xyPlot.mapDatasetToRangeAxis(1, 1);
    xyPlot.mapDatasetToRangeAxis(2, 2);
    xyPlot.mapDatasetToRangeAxis(3, 3);
    xyPlot.mapDatasetToRangeAxis(4, 4);

    chartPanel = new JPanel();
    chartPanel.setLayout(new BorderLayout());
    chartPanel2 = new ChartPanel(chart);
    chartPanel2.addChartMouseListener(new ChartMouseListener() {

      @Override
      public void chartMouseClicked(ChartMouseEvent event) {
        Point2D p = event.getTrigger().getPoint();
        Rectangle2D plotArea = chartPanel2.getScreenDataArea();
        XYPlot plot = (XYPlot) chart.getPlot(); // your plot
        double chartX = plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());

        long dataPosition = Math.round(chartX);
        Datagram lastDatagram = null;
        Location location = null;
        for (Datagram datagram : gpsList) {
          if (lastDatagram == null) {
            lastDatagram = datagram;
          }
          if (datagram.getLineNumber() < dataPosition) {
            lastDatagram = datagram;
          }
        }
        if (lastDatagram != null) {
          if (lastDatagram instanceof NMEAGGASentence) {
            NMEAGGASentence sentence = (NMEAGGASentence) lastDatagram;
            location = sentence.getLocation();
          }
          if (lastDatagram instanceof NMEARMCSentence) {
            NMEARMCSentence sentence = (NMEARMCSentence) lastDatagram;
            location = sentence.getLocation();
            double speed = sentence.getSpeed();
            showInfoSpeed(speed);
            Date dateTime = sentence.getDateTime();
            showInfoDateTIme(dateTime);
          }

          if (marker != null) {
            if (waypoints.contains(marker)) {
              waypoints.remove(marker);
            }

          }
          if (location != null) {
            GeoPosition position = LocationUtils.convertLocation2GeoPosition(location);
            showInfoLocation(position);
            marker = new ThisWaypoint(position);
            waypoints.add(marker);
          }

          waypointPainter.setWaypoints(waypoints);

          mapViewer.repaint();
        }
      }

      @Override
      public void chartMouseMoved(ChartMouseEvent event) {
      }
    });
    chartPanel.add(chartPanel2, BorderLayout.CENTER);
    chartPanel.add(getChartToolbar(), BorderLayout.WEST);
  }

  private JToolBar getChartToolbar() {
    ActionListener actionListener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        checkVisibility();
      }
    };
    JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
    toolbar.setRollover(true);
    jcbGyr = new JCheckBox("Gyro");
    jcbGyr.setName("jcbGyr");
    jcbGyr.addActionListener(actionListener);
    toolbar.add(jcbGyr);

    jcbAcc = new JCheckBox("Acc");
    jcbAcc.setName("jcbAcc");
    jcbAcc.addActionListener(actionListener);
    toolbar.add(jcbAcc);

    jcbVcc = new JCheckBox("Vcc");
    jcbVcc.setName("jcbVcc");
    jcbVcc.addActionListener(actionListener);
    toolbar.add(jcbVcc);

    jcbDepth = new JCheckBox("Depth");
    jcbDepth.setName("jcbDepth");
    jcbDepth.addActionListener(actionListener);
    toolbar.add(jcbDepth);

    jcbSpeed = new JCheckBox();
    jcbSpeed.setName("jcbSpeed");
    jcbSpeed.addActionListener(actionListener);
    toolbar.add(jcbSpeed);

    return toolbar;
  }

  private void checkVisibility() {

    vccRenderer.setSeriesVisible(0, jcbVcc.isSelected(), true);
    vccRenderer.setSeriesVisibleInLegend(0, jcbVcc.isSelected(), true);

    accRenderer.setSeriesVisible(0, jcbAcc.isSelected(), true);
    accRenderer.setSeriesVisibleInLegend(0, jcbAcc.isSelected(), true);
    accRenderer.setSeriesVisible(1, jcbAcc.isSelected(), true);
    accRenderer.setSeriesVisibleInLegend(1, jcbAcc.isSelected(), true);
    accRenderer.setSeriesVisible(2, jcbAcc.isSelected(), true);
    accRenderer.setSeriesVisibleInLegend(2, jcbAcc.isSelected(), true);

    gyrRenderer.setSeriesVisible(0, jcbGyr.isSelected(), true);
    gyrRenderer.setSeriesVisibleInLegend(0, jcbGyr.isSelected(), true);
    gyrRenderer.setSeriesVisible(1, jcbGyr.isSelected(), true);
    gyrRenderer.setSeriesVisibleInLegend(1, jcbGyr.isSelected(), true);
    gyrRenderer.setSeriesVisible(2, jcbGyr.isSelected(), true);
    gyrRenderer.setSeriesVisibleInLegend(2, jcbGyr.isSelected(), true);

    depthRenderer.setSeriesVisible(0, jcbDepth.isSelected(), true);
    depthRenderer.setSeriesVisibleInLegend(0, jcbDepth.isSelected(), true);
    depthRenderer.setSeriesVisible(1, jcbDepth.isSelected(), true);
    depthRenderer.setSeriesVisibleInLegend(1, jcbDepth.isSelected(), true);

    speedRenderer.setSeriesVisible(0, jcbSpeed.isSelected(), true);
    speedRenderer.setSeriesVisibleInLegend(0, jcbSpeed.isSelected(), true);

    axisGyro.setVisible(jcbGyr.isSelected());
    axisAcc.setVisible(jcbAcc.isSelected());
    axisDepth.setVisible(jcbDepth.isSelected());
    axisVcc.setVisible(jcbVcc.isSelected());
    axisSpeed.setVisible(jcbSpeed.isSelected());

  }

  private JToolBar getToolBar() {
    JToolBar toolBar = new JToolBar();
    toolBar.setRollover(true);

    toolBar.add(actions.get("trackInView"));
    toolBar.add(actions.get("zoomIn"));
    toolBar.add(actions.get("zoomOut"));

    toolBar.addSeparator();

    String mapProvider = ProgramConfig.getInstance().getMapProvider();
    int index = 0;
    String[] tfLabels = new String[factories.size()];
    for (int i = 0; i < factories.size(); i++) {
      tfLabels[i] = factories.get(i).getInfo().getName();
      if (tfLabels[i].equalsIgnoreCase(mapProvider)) {
        index = i;
      }
    }

    jcbMapProvider = new JComboBox<String>(tfLabels);
    jcbMapProvider.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        TileFactory factory = factories.get(jcbMapProvider.getSelectedIndex());
        mapViewer.setTileFactory(factory);
        thumbMap.setTileFactory(factory);
        thumbMap.setZoom(factory.getInfo().getDefaultZoomLevel() + 3);
        thumbMap.setCenterPosition(new GeoPosition(0, 0));

        checkOverlays();
        showCopyRight();
      }
    });
    jcbMapProvider.setPreferredSize(new Dimension(140, 20));
    jcbMapProvider.setMinimumSize(new Dimension(140, 20));
    jcbMapProvider.setMaximumSize(new Dimension(140, 20));
    toolBar.add(jcbMapProvider);

    overlaysMenu = new JPopupMenu();
    jcbSeaMark = new JCheckBoxMenuItem(getTranslatedString("overlays.standart.seamark"));
    jcbSeaMark.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        checkOverlays();
      }
    });
    overlaysMenu.add(jcbSeaMark);

    jcbSportMark = new JCheckBoxMenuItem(getTranslatedString("overlays.standart.sport"));
    jcbSportMark.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        checkOverlays();
      }
    });
    overlaysMenu.add(jcbSportMark);

    overlaysButton = new JButton();
    overlaysButton.setName("overlaysButton");
    overlaysButton.setAction(new AbstractAction("ovl") {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e) {
        overlaysMenu.show(overlaysButton, 0, overlaysButton.getHeight());
      }
    });
    toolBar.add(overlaysButton);

    toolBar.addSeparator(new Dimension(10, 30));

    zoomLabel = new JLabel();
    toolBar.add(zoomLabel);

    toolBar.addSeparator(new Dimension(10, 30));

    taskLabel = new JLabel();
    toolBar.add(taskLabel);

    toolBar.addSeparator(new Dimension(10, 30));

    dateLabel = new JLabel();
    toolBar.add(dateLabel);

    toolBar.addSeparator(new Dimension(10, 30));

    toolBar.add(new JSeparator(JSeparator.VERTICAL));
    toolBar.add(actions.get("exitFrame"));

    jcbMapProvider.setSelectedIndex(index);

    return toolBar;
  }

  protected void showCopyRight() {
    TileFactory factory = factories.get(jcbMapProvider.getSelectedIndex());
    String infoName = factory.getInfo().getName();
    infoName = infoName.replaceAll(" ", "_");
    String key = String.format("provider.%s.copyright", infoName);
    copyrightLabel.setText(getTranslatedString(key));
    key = String.format("provider.%s.url", infoName);
    providerURL = getTranslatedString(key);
  }

  private void checkOverlays() {
    TileFactory factory = factories.get(jcbMapProvider.getSelectedIndex());
    if (jcbSeaMark.isSelected()) {
      factory.getInfo().addOverlayTile(mySeamark);
    } else {
      factory.getInfo().removeOverlayTile(mySeamark);
    }
    if (jcbSportMark.isSelected()) {
      factory.getInfo().addOverlayTile(mySportMark);
    } else {
      factory.getInfo().removeOverlayTile(mySportMark);
    }
    mapViewer.repaint();
  }

  @Action
  public void exitFrame() {
    ProgramConfig.getInstance().setMapHeight(splitPane.getDividerLocation());
    ProgramConfig.getInstance().saveFrameData(MAP_FRAME, getBounds());
    ProgramConfig.getInstance().setMapProvider(jcbMapProvider.getSelectedItem().toString());
    setVisible(false);
  }

  @Action
  public void zoomIn() {
    mapViewer.setZoom(mapViewer.getZoom() - 1);
    showZoomLevel();
  }

  /**
   * 
   */
  private void showZoomLevel() {
    thumbMap.setZoom(mapViewer.getZoom() + 5);
    zoomLabel.setText(getTranslatedString("zoom.label.text", mapViewer.getZoom()));
  }

  /**
   * 
   */
  private void showTaskSize() {
    int queueSize = mapViewer.getTileFactory().getQueueSize();
    taskLabel.setText(String.format("Task: %d", queueSize));
  }

  @Action
  public void zoomOut() {
    mapViewer.setZoom(mapViewer.getZoom() + 1);
    showZoomLevel();
  }

  @Action
  public void trackInView() {
    mapViewer.zoomToBestFit(new HashSet<GeoPosition>(track), 0.7);
    showZoomLevel();
  }

  @Action
  public void printTrack() {
    // here we must print the desired content
  }

  /**
   * @param datagrams
   */
  public void setData(List<Datagram> datagrams) {
    // this.datagramms = datagrams;
    track = new ArrayList<GeoPosition>();
    gpsList = new ArrayList<Datagram>();

    Location oldLocation = null;
    waypoints = new HashSet<Waypoint>();
    GeoPosition position = null;

    int count = datagrams.size();
    int maxAccPoints = count / MAX_ACC_POINTS;
    int index = 0;
    int lastVcc = 0;
    int lastAcc = 0;
    int lastGyr = 0;
    int lastDepth = 0;
    int lastSpeed = 0;
    Date startDate = null;
    Date endDate = null;

    if (maxAccPoints == 0) {
      maxAccPoints = 1;
    }
    for (Datagram datagram : datagrams) {
      int posIndex = index / maxAccPoints;

      Location location = null;
      if (datagram instanceof NMEAGGASentence) {
        NMEAGGASentence sentence = (NMEAGGASentence) datagram;
        location = sentence.getLocation();
        if (startDate == null) {
          startDate = sentence.getTime();
        }
        endDate = sentence.getTime();
      }
      if (datagram instanceof NMEARMCSentence) {
        NMEARMCSentence sentence = (NMEARMCSentence) datagram;
        location = sentence.getLocation();
        try {
          if (startDate == null) {
            startDate = sentence.getDateTime();
          }
          endDate = sentence.getDateTime();
        } catch (Exception e) {
          logger.debug("Error reading date.", e);
        }
        if ((lastSpeed == 0) || (posIndex > lastSpeed)) {
          double speed = sentence.getSpeed();
          speedSeries.add(sentence.getLineNumber(),
              MeasureConverter.convertSpeedNautical(ProgramConfig.getInstance().getMeasureSystem(), speed));
          lastSpeed = posIndex;
        }
      }
      if (location != null) {
        if (oldLocation != null) {
          double distance = Math.abs(LocationUtils.distanceTo(oldLocation, location));
          if (distance > 0.0001) {
            position = LocationUtils.convertLocation2GeoPosition(location);
            track.add(position);
            gpsList.add(datagram);
            oldLocation = location;
          }
        } else {
          position = LocationUtils.convertLocation2GeoPosition(location);
          track.add(position);
          gpsList.add(datagram);
          waypoints.add(new StartWaypoint(position));
          oldLocation = location;
        }
      }
      if (datagram instanceof NMEADepthSentence) {
        if ((lastDepth == 0) || (posIndex > lastDepth)) {
          NMEADepthSentence sentence = (NMEADepthSentence) datagram;
          double depth = sentence.getDepth(DEPTH_UNIT.METER);
          depthSeries.add(sentence.getLineNumber(), depth);
          lastDepth = posIndex;
        }
      }
      if (datagram instanceof NMEAOSMVccSentence) {
        if ((lastVcc == 0) || (posIndex > lastVcc)) {
          NMEAOSMVccSentence sentence = (NMEAOSMVccSentence) datagram;
          double voltage = sentence.getVoltage() / 1000.0;
          voltageSeries.add(sentence.getLineNumber(), voltage);
          lastVcc = posIndex;
        }
      }
      if (datagram instanceof NMEAOSMAccSentence) {
        if ((lastAcc == 0) || (posIndex > lastAcc)) {
          NMEAOSMAccSentence sentence = (NMEAOSMAccSentence) datagram;
          accSeriesX.add(sentence.getLineNumber(), sentence.getAccX());
          accSeriesY.add(sentence.getLineNumber(), sentence.getAccY());
          accSeriesZ.add(sentence.getLineNumber(), sentence.getAccZ());
          lastAcc = posIndex;
        }
      }
      if (datagram instanceof NMEAOSMGyrSentence) {
        if ((lastGyr == 0) || (posIndex > lastGyr)) {
          NMEAOSMGyrSentence sentence = (NMEAOSMGyrSentence) datagram;
          gyrSeriesX.add(sentence.getLineNumber(), sentence.getGyroX());
          gyrSeriesY.add(sentence.getLineNumber(), sentence.getGyroY());
          gyrSeriesZ.add(sentence.getLineNumber(), sentence.getGyroZ());
          lastGyr = posIndex;
        }
      }
      index++;
    }

    if (position != null) {
      waypoints.add(new EndWaypoint(position));
    }

    chartPanel.repaint();
    RoutePainter routePainter = new RoutePainter(track);

    waypointPainter = new WaypointPainter<Waypoint>();
    waypointPainter.setRenderer(new MyWaypointRenderer());
    waypointPainter.setWaypoints(waypoints);

    CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(routePainter, waypointPainter);
    mapViewer.setOverlayPainter(painter);

    // mapViewer.zoomToBestFit(new HashSet<GeoPosition>(track), 0.7);

    if (startDate != null) {
      DateFormat format = DateFormat.getDateTimeInstance();
      dateLabel.setText(getTranslatedString("date.label.text", format.format(startDate), format.format(endDate)));
    }
    checkOptions();
  }

  private void checkOptions() {
    jcbAcc.setVisible(!accSeriesX.isEmpty() || !accSeriesY.isEmpty() || !accSeriesZ.isEmpty());
    jcbGyr.setVisible(!gyrSeriesX.isEmpty() || !gyrSeriesY.isEmpty() || !gyrSeriesZ.isEmpty());
    jcbVcc.setVisible(!voltageSeries.isEmpty());
    jcbDepth.setVisible(!depthSeries.isEmpty());
    jcbSpeed.setVisible(!speedSeries.isEmpty());

    axisAcc.setVisible(!accSeriesX.isEmpty() || !accSeriesY.isEmpty() || !accSeriesZ.isEmpty());
    axisGyro.setVisible(!gyrSeriesX.isEmpty() || !gyrSeriesY.isEmpty() || !gyrSeriesZ.isEmpty());
    axisVcc.setVisible(!voltageSeries.isEmpty());
    axisDepth.setVisible(!depthSeries.isEmpty());
    axisSpeed.setVisible(!speedSeries.isEmpty());

    jcbAcc.setSelected(!accSeriesX.isEmpty() || !accSeriesY.isEmpty() || !accSeriesZ.isEmpty());
    jcbGyr.setSelected(!gyrSeriesX.isEmpty() || !gyrSeriesY.isEmpty() || !gyrSeriesZ.isEmpty());
    jcbVcc.setSelected(!voltageSeries.isEmpty());
    jcbDepth.setSelected(!depthSeries.isEmpty());
    jcbSpeed.setSelected(!speedSeries.isEmpty());
  }

  public static void showNmeaFile(File dataFile, ProgressCallback callback) {
    try {
      String showMapIn = ProgramConfig.getInstance().getShowMapIn();
      if (showMapIn.equalsIgnoreCase(Constants.SHOW_IN_TYPE.BROWSER.name())) {

        File htmlFile = Files.changeExtension(dataFile, ".html");
        if (htmlFile.exists()) {
          htmlFile.delete();
        }
        RouteConverterUtils.convert(dataFile, htmlFile);

        URLUtilities.openUrl(htmlFile.toURI().toURL().toString());
      } else if (showMapIn.equalsIgnoreCase(Constants.SHOW_IN_TYPE.PROGRAM.name())) {

        String showMapProgram = ProgramConfig.getInstance().getShowMapProgram();
        List<String> command = new ArrayList<>();
        command.add(showMapProgram);
        command.add(dataFile.getCanonicalPath());
        StartProcess.startJava(command, false, ".");
      } else if (showMapIn.equalsIgnoreCase(Constants.SHOW_IN_TYPE.INTERNAL.name())) {
        List<Datagram> datagrams = NMEAUtils.readNMEAData(dataFile, callback);
        final MapGUI mapGui = new MapGUI();
        mapGui.setData(datagrams);
        // mapGui.setModal(true);
        mapGui.setVisible(true);
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            if (mapGui != null) {
              mapGui.toFront();
            }
          }
        });
      }
    } catch (IOException | URISyntaxException | ParseException e) {
      logger.error(e);
    }
  }

  public static void showDataFile(File dataFile, ProgressCallback callback) {
    try {
      LoggerFile loggerFile = new LoggerFile(dataFile);
      loggerFile.setProgressCallback(callback);

      loggerFile.setSentenceFilter(DefaultSentenceFilter.GPSECHOSENTENCEFILTER).parseFile();
      LoggingTool.showParseResults(logger, dataFile, loggerFile);

      List<Datagram> datagrams = loggerFile.getDatagrams();

      String showMapIn = ProgramConfig.getInstance().getShowMapIn();
      if (showMapIn.equalsIgnoreCase(Constants.SHOW_IN_TYPE.BROWSER.name())) {
        File nmeaFile = Files.changeExtension(dataFile, ".nmea");
        NMEAUtils.writeNmeaFile(nmeaFile, datagrams);

        File htmlFile = Files.changeExtension(dataFile, ".html");
        if (htmlFile.exists()) {
          htmlFile.delete();
        }
        RouteConverterUtils.convert(nmeaFile, htmlFile);

        URLUtilities.openUrl(htmlFile.toURI().toURL().toString());
      } else if (showMapIn.equalsIgnoreCase(Constants.SHOW_IN_TYPE.PROGRAM.name())) {
        File nmeaFile = Files.changeExtension(dataFile, ".nmea");
        NMEAUtils.writeNmeaFile(nmeaFile, datagrams);

        String showMapProgram = ProgramConfig.getInstance().getShowMapProgram();
        List<String> command = new ArrayList<>();
        command.add(showMapProgram);
        command.add(nmeaFile.getCanonicalPath());
        StartProcess.startJava(command, false, ".");
      } else if (showMapIn.equalsIgnoreCase(Constants.SHOW_IN_TYPE.INTERNAL.name())) {
        final MapGUI mapGui = new MapGUI();
        mapGui.setData(datagrams);
        // mapGui.setModal(true);
        mapGui.setVisible(true);
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            if (mapGui != null) {
              mapGui.toFront();
            }
          }
        });
      }
    } catch (IOException | URISyntaxException e) {
      logger.error(e);
    }
  }

  private String getTranslatedString(final String key, Object... object) {
    String string = Application.getInstance().getContext().getResourceMap(getClass()).getString(key, object);
    return string;
  }

  private String getTransMeasureString(final String key, Object... object) {
    String string = "";
    switch (ProgramConfig.getInstance().getMeasureSystem()) {
    case IMPERIAL:
      string = getTranslatedString("imperial." + key, object);
      break;
    case METRICAL:
      string = getTranslatedString("metrical." + key, object);
      break;
    default:
      string = getTranslatedString("nautical." + key, object);
      break;
    }
    return string;
  }
}
