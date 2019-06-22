package de.mcs.depth.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.mcs.utils.Files;
import de.mcs.utils.StartProcess;

public class DiscImagerTool {

  private File toolsDir;
  private File appDir;

  public static DiscImagerTool newDiscImagerTool() {
    return new DiscImagerTool();
  }

  public DiscImagerTool setToolsDir(File toolsDir) {
    this.toolsDir = toolsDir;
    return this;
  }

  public DiscImagerTool setAppDir(File appDir) {
    this.appDir = appDir;
    return this;
  }

  public void readDiscImage(DriveInfo drive, File imageFile) throws IOException {
    StringBuilder build = new StringBuilder();
    build.append("@echo off\r\n");
    build.append("cd \"");
    build.append(toolsDir.getCanonicalPath());
    build.append("\"\r\n");
    build.append("win32diskimager.exe \"");
    build.append(imageFile.getCanonicalPath());
    build.append("\" ");
    build.append(Files.getDriveLetter(drive.getFile()));
    build.append(" read \r\n");
    File commandFile = new File(appDir, "backup.cmd");
    Files.writeStringToFile(commandFile, build.toString());

    File logFile = new File(appDir, "log.txt");
    if (logFile.exists()) {
      logFile.delete();
    }

    List<String> command = new ArrayList<>();
    command.add("cmd.exe");
    command.add("/c");
    command.add(commandFile.getCanonicalPath());
    command.add(">" + logFile.getCanonicalPath());
    StartProcess.startJava(command, true, ".");
  }

  public void writeDiscImage(DriveInfo drive, File imageFile) throws IOException {
    StringBuilder build = new StringBuilder();
    build.append("@echo off\r\n");
    build.append("cd \"");
    build.append(toolsDir.getCanonicalPath());
    build.append("\"\r\n");
    build.append("win32diskimager.exe \"");
    build.append(imageFile.getCanonicalPath());
    build.append("\" ");
    build.append(Files.getDriveLetter(drive.getFile()));
    build.append(" write \r\n");
    File writeCommand = new File(appDir, "write.cmd");
    Files.writeStringToFile(writeCommand, build.toString());

    File logFile = new File(appDir, "log.txt");
    if (logFile.exists()) {
      logFile.delete();
    }

    List<String> command = new ArrayList<>();
    command.add("cmd.exe");
    command.add("/c");
    command.add(writeCommand.getCanonicalPath());
    command.add(">" + logFile.getCanonicalPath());
    StartProcess.startJava(command, true, ".");
  }

}
