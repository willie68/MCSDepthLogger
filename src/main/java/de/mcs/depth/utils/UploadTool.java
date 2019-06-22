/**
 * MCS Media Computer Software
 * Copyright 2014 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: UploadTool.java
 * EMail: W.Klaas@gmx.de
 * Created: 29.04.2014 wklaa_000
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
package de.mcs.depth.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ProxySelector;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import de.mcs.depth.ProgramConfig;
import de.mcs.depth.StatusAndProgress;
import de.mcs.depth.UploadSettingsPanel;
import de.mcs.depth.logger.DepthLoggerConfig;
import de.mcs.utils.Files;
import de.mcs.utils.FolderZipper;
import de.mcs.utils.StartProcess;
import de.mcs.utils.StringUtils;

/**
 * @author wklaa_000
 * 
 */
public class UploadTool {

  private Logger logger = Logger.getLogger(UploadTool.class);

  private static final String NLS_PREFIX = "main.gui.";

  private StatusAndProgress sap = null;

  private static UploadTool instance = null;

  public static UploadTool getInstance() {
    return instance;
  }

  public UploadTool(StatusAndProgress sap) {
    this.sap = sap;
    instance = this;
  }

  public boolean uploadDataFile(List<File> files, String uploadURL, DepthLoggerConfig loggerConfig) {
    boolean result = true;
    sap.setStatusbar(NLS_PREFIX + "statusbar.copy");
    sap.setProgress(0);

    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
    String fileName = "upl" + format.format(new Date());
    File temp = new File(Files.getTempPath(), fileName);
    temp.mkdirs();
    String message = "";
    if (temp.exists()) {

      try {
        List<File> destDataFiles = new ArrayList<>();
        for (File file : files) {
          File destFile = new File(temp, file.getName());
          logger.info(String.format("copy file %s to folder %s", file.getCanonicalPath(), temp.getCanonicalPath()));
          Files.fileCopy(file, destFile);
          destDataFiles.add(destFile);
        }
        // create vessel id file in folder
        if (loggerConfig != null) {
          File vesselIdFile = new File(temp, "vessel.id");
          Properties props = new Properties();

          String vesselId = String.format("%d", loggerConfig.getVesselid());
          if ((vesselId != null) && !vesselId.equals("")) {
            props.setProperty("vesselid", vesselId);
          }
          String username = ProgramConfig.getInstance().getUploadUsername();
          if (username != null && !username.equals("")) {
            props.setProperty("username", username);
          }
          StringBuilder b = new StringBuilder();
          boolean first = true;
          for (File file : destDataFiles) {
            if (!first) {
              b.append(',');
            }
            first = false;
            b.append('"');
            b.append(file.getName());
            b.append('"');
          }
          props.setProperty("files", b.toString());

          FileWriter writer = new FileWriter(vesselIdFile);
          props.store(writer, "created by MCSDepthLogger");
          writer.close();

          File configFile = new File(temp, "config.dat");
          loggerConfig.writeConfig(configFile);
          configFile = new File(temp, "config.desc");
          loggerConfig.writeDescriptiveConfig(configFile);
        }

        if (ProgramConfig.getInstance().isInternalProcessing()) {
          for (File file : destDataFiles) {
            File oldFile = new File(file.getParentFile(), file.getName() + ".old");
            DataTool.getInstance().saveDataFile(file, oldFile);
            oldFile.delete();
          }
        }
        String batchCommandStr = ProgramConfig.getInstance().getUploadBatch();
        File batchCommand = new File(batchCommandStr);
        if (batchCommand.exists()) {
          for (File file : destDataFiles) {
            List<String> command = new ArrayList<>();
            command.add("cmd.exe");
            command.add("/c");
            command.add(batchCommand.getCanonicalPath());
            command.add(file.getCanonicalPath());
            StartProcess.startJava(command, true, ".");
          }
        }

        sap.setStatusbar(NLS_PREFIX + "statusbar.compress");
        sap.setProgress(30);
        File destZipFile = new File(Files.getTempPath(), fileName + ".zip");

        logger.info(String.format("compress folder to file %s", destZipFile.getCanonicalPath()));
        FolderZipper.zipFolder(temp.getCanonicalPath(), destZipFile.getCanonicalPath());
        Files.remove(temp, true);

        sap.setStatusbar(NLS_PREFIX + "statusbar.upload");
        sap.setProgress(60);
        logger.info(String.format("upload file %s to %s", destZipFile.getCanonicalPath(), uploadURL));

        URL uploadUrl = new URL(uploadURL);

        HttpClientBuilder httpclientBuilder = HttpClients.custom();
        SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
        httpclientBuilder.setRoutePlanner(routePlanner);
        String username = ProgramConfig.getInstance().getUploadUsername();
        if (username != null && !username.equals("")) {
          String password = ProgramConfig.getInstance().getUploadPassword();
          password = StringUtils.decrypt(password, UploadSettingsPanel.KEY);

          CredentialsProvider credsProvider = new BasicCredentialsProvider();

          credsProvider.setCredentials(new AuthScope(uploadUrl.getHost(), uploadUrl.getPort()),
              new UsernamePasswordCredentials(username, password));
          httpclientBuilder.setDefaultCredentialsProvider(credsProvider);
        }

        CloseableHttpClient httpclient = httpclientBuilder.build();

        HttpPost httpPost = new HttpPost(uploadUrl.toURI());

        FileBody bin = new FileBody(destZipFile, ContentType.DEFAULT_BINARY, destZipFile.getName());
        StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);

        HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("userfile", bin).addPart("comment", comment)
            .build();

        httpPost.setEntity(reqEntity);
        CloseableHttpResponse response2 = httpclient.execute(httpPost);
        try {
          logger.info(String.format("%s", response2.getStatusLine()));
          message = response2.getStatusLine().toString();
          HttpEntity entity2 = response2.getEntity();
          entity2.writeTo(System.out);
          // do something useful with the response body
          // and ensure it is fully consumed
          EntityUtils.consume(entity2);
        } finally {
          response2.close();
        }

        sap.setStatusbar(NLS_PREFIX + "statusbar.upload.remove", NLS_PREFIX + "statusbar.upload.remove");
        logger.info(String.format("removing file %s", destZipFile.getCanonicalPath()));
        destZipFile.delete();

      } catch (IOException e) {
        message = "An error occured, see log file";
        logger.error(e);
        result = false;
      } catch (URISyntaxException e) {
        message = "An error occured, see log file";
        logger.error(e);
        result = false;
      }
    }
    sap.setStatusbar(message);
    sap.setProgress(0);
    return result;
  }
}
