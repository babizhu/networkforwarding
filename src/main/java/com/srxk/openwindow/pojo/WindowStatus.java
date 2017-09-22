package com.srxk.openwindow.pojo;

import lombok.Data;

@Data
public class WindowStatus {

  public static final WindowStatus EMPTY =new WindowStatus(new byte[3], (byte) 4);
  private final byte[] address;
  private final byte result;

}
