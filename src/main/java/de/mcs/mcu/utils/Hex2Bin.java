package de.mcs.mcu.utils;

/**
 * MCS Media Computer Software
 * Copyright 2014 by Wilfried Klaas
 * Project: MCSDepthLogger
 * File: Hex2Bin.java
 * EMail: W.Klaas@gmx.de
 * Created: 21.03.2014 wklaa_000
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author wklaa_000
 * 
 */
public class Hex2Bin {

  static byte[] data = new byte[32768];

  static boolean debug = false;

  public static void main(String[] args) {
    if (args.length >= 1) {
      File file = new File(args[0]);
      if (file.isDirectory()) {
        File[] files = file.listFiles(new FileFilter() {

          @Override
          public boolean accept(File file) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".hex")) {
              return true;
            }
            return false;
          }
        });
        for (File file2 : files) {
          convertFile(file2);
        }
      } else {
        convertFile(file);
      }
    } else {
      System.out.println("Hex2Bin converting intel hex to binary file.");
      System.out.println("usage Hex2Bin [file]");
      System.out.println(
          "file must be an intel hex file. The binary file will be create in the same directory with suffix bin.");

    }
  }

  private static void convertFile(File file) {
    if (file.exists()) {
      if (file.getName().endsWith(".bin")) {
        System.out.println("bin file already exists. ");
      } else {
        // saving file
        String name = file.getName();
        if (name.lastIndexOf(".") >= 0) {
          name = name.substring(0, name.lastIndexOf(".")) + ".BIN";
        }

        File outputFile = new File(file.getParentFile(), name);
        convert(file, outputFile);
      }
    } else {
      System.out.println("file not found.");
    }
  }

  public static void convert(File sourceFile, File outputFile) {
    System.out.println(String.format("reading file %s", sourceFile.getName()));
    try {
      BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
      String aux = "";
      int linenumber = 1;
      int byteCount = 0;
      while ((aux = reader.readLine()) != null) {
        aux = aux.trim();
        if (aux.startsWith(":")) {
          String sizeStr = aux.substring(1, 3);
          int size = Integer.parseInt(sizeStr, 16);

          // String length is, 1 ':', 2 record length, 4 adress, 2 type,
          // record, 2 crc
          int dataLength = 1 + 2 + 4 + 2 + size * 2 + 2;
          if (dataLength != aux.length()) {
            System.out.println(
                String.format("line has not the correct length. Must be %d, has %d", dataLength, aux.length()));
            System.out.println(aux);
            System.exit(-1);
          }
          int startAdress = Integer.parseInt(aux.substring(3, 7), 16);
          int type = Integer.parseInt(aux.substring(8, 9), 16);
          if (debug)
            System.out.println(String.format("%x %h %x %s", size, startAdress, type, aux));
          if (type == 0) {
            int pos = 0;
            for (int i = 0; i < size; i++) {
              pos = 9 + i * 2;
              byte myByte = (byte) Integer.parseInt(aux.substring(pos, pos + 2), 16);
              data[byteCount] = myByte;
              byteCount++;
              if (debug)
                System.out.print(String.format("%x ", myByte));
            }
            if (debug)
              System.out.println();
            pos += 2;
            byte checksum = (byte) Integer.parseInt(aux.substring(pos, pos + 2), 16);
            String checkString = aux.substring(1, pos);

            byte computedChecksum = computeChecksum(aux, pos);

            if (computedChecksum != checksum) {
              System.err.println(String.format("checksumerror: checksum in record %x, computed %x for %s", checksum,
                  computedChecksum, checkString));
              System.exit(-1);
            }
            if (debug)
              System.out.println(
                  String.format("checksum in record %x, computed %x for %s", checksum, computedChecksum, checkString));
          }
        } else {
          System.out.println(String.format("no data found in line %d.", linenumber));
        }
        linenumber++;
      }

      System.out.println(String.format("writing %d bytes to output file %s", byteCount, outputFile.getName()));
      FileOutputStream out = new FileOutputStream(outputFile);
      out.write(data, 0, byteCount);
      out.close();
      reader.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private static byte computeChecksum(String aux, int pos) {
    int sum = 0;
    for (int i = 0; i < pos / 2; i++) {
      int newpos = 1 + i * 2;
      byte myByte = (byte) Integer.parseInt(aux.substring(newpos, newpos + 2), 16);
      sum += myByte;
    }
    return (byte) ((sum % 256) * (0xFF));
  }
}
