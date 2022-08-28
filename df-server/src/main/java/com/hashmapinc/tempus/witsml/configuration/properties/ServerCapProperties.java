package com.hashmapinc.tempus.witsml.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "wmls")
public class ServerCapProperties {

  private String contactName;
  private String contactEmail;
  private String contactPhone;
  private int changeDetectionPeriod;
  private String description;
  private String name;
  private Boolean supportUomConversion;
  private String compressionMethod;
  private Boolean cascadedDelete;
}
