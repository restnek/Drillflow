package com.hashmapinc.tempus.witsml.exception;

import lombok.Getter;

@Getter
public class UnsupportedWitsmlVersionException extends WitsmlException {
  private final String version;

  public UnsupportedWitsmlVersionException(String version) {
    super("Unsupported witsml version " + version);
    this.version = version;
  }
}
