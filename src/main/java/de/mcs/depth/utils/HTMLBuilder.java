/**
 * MCS Media Computer Software
 * Copyright 2016 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: HTMLBuilder.java
 * EMail: W.Klaas@gmx.de
 * Created: 12.08.2016 wklaa_000
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.mcs.utils.Files;
import de.mcs.utils.Version;

/**
 * @author wklaa_000
 *
 */
public class HTMLBuilder {

  private static DateFormat format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM,
      SimpleDateFormat.MEDIUM, Locale.getDefault());

  private static final String NLS_PREFIX = "main.gui.";

  private StringBuilder builder;

  private ITranslator translator;

  private long count;

  public HTMLBuilder(ITranslator translator) {
    this.translator = translator;
    builder = new StringBuilder();
    startHtml();
  }

  private HTMLBuilder startHtml() {
    builder.append("<html><body>");
    addBr();
    return this;
  }

  private void addBr() {
    builder.append("<br/>");
  }

  public HTMLBuilder addFileData(File dataFile) {
    builder.append(translator.getTranslatedString(NLS_PREFIX + "file.name", dataFile.getName()));
    addBr();

    long fileSize = dataFile.length();
    builder.append(translator.getTranslatedString(NLS_PREFIX + "file.size", Files.readableFileSize(fileSize)));
    addBr();
    return this;
  }

  public HTMLBuilder addDatagramCount(long count) {
    this.count = count;
    builder.append(translator.getTranslatedString(NLS_PREFIX + "file.analyse.datagramm", count));
    addBr();
    return this;
  }

  public HTMLBuilder setErrorCount(int errors) {
    builder.append(translator.getTranslatedString(NLS_PREFIX + "file.analyse.errors", errors));
    addBr();
    builder.append(translator.getTranslatedString(NLS_PREFIX + "file.analyse.rate", errors * 100.0 / count));
    addBr();
    return this;
  }

  public HTMLBuilder setVersion(Version version) {
    builder.append(translator.getTranslatedString(NLS_PREFIX + "file.analyse.version", version.toString()));
    addBr();
    return this;
  }

  public HTMLBuilder setTimestamps(Date lastModified, Date firstTimeStamp) {
    builder.append(translator.getTranslatedString(NLS_PREFIX + "file.analyse.timestamp", format.format(firstTimeStamp),
        format.format(lastModified)));
    addBr();
    return this;
  }

  public HTMLBuilder closeHTML() {
    builder.append("</body></html>");
    return this;
  }

  @Override
  public String toString() {
    return builder.toString();
  }
}
