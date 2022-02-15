package org.paulobichara.springbootapi.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
public class TimeZoneConfig {

  private static final String TIMEZONE = "UTC";

  @PostConstruct
  void started() {
    TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE));
  }
}
