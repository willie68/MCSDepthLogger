/**
 * MCS Media Computer Software
 * Copyright 2016 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: DefaultSentectFilter.java
 * EMail: W.Klaas@gmx.de
 * Created: 24.04.2016 wklaa_000
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
package de.mcs.depth.logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wklaa_000
 *
 */
public class DefaultSentenceFilter {

  public static final SentenceFilter RMC_SENTENCEFILTER = new SentenceFilter() {
    private Pattern pattern = Pattern.compile("(GPRMC)");

    @Override
    public boolean filterSentence(String nmemonic) {
      if (nmemonic != null) {
        String stc = nmemonic.toUpperCase();
        Matcher matcher = pattern.matcher(stc);
        return matcher.find();
      }
      return false;
    }
  };

  public static final String PATTERNFILTER_GPS_ECHO = "(..RMC|POSMACC|POSMVCC|POSMGYR|SDDBT|SDDBK|SDDBS)";
  public static final SentenceFilter GPSECHOSENTENCEFILTER = new SentenceFilter() {
    private Pattern pattern = Pattern.compile(PATTERNFILTER_GPS_ECHO);

    @Override
    public boolean filterSentence(String nmemonic) {
      if (nmemonic != null) {
        String stc = nmemonic.toUpperCase();
        Matcher matcher = pattern.matcher(stc);
        return matcher.find();
      }
      return false;
    }

  };

  public static final SentenceFilter KNOWN_SENTENCEFILTER = new SentenceFilter() {
    private Pattern pattern = Pattern.compile("(GPRMC|POSMACC|POSMVCC|POSMGYR|SDDBT|SDDBK|SDDBS)");

    // Pattern.compile("(GPRMC|POSMACC)");

    @Override
    public boolean filterSentence(String nmemonic) {
      if (nmemonic != null) {
        String stc = nmemonic.toUpperCase();
        Matcher matcher = pattern.matcher(stc);
        return matcher.find();
      }
      return false;
    }

  };
}
