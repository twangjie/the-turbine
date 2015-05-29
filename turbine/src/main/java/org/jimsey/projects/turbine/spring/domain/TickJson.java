/**
 * The MIT License
 * Copyright (c) 2015 the-james-burton
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jimsey.projects.turbine.spring.domain;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.verdelhan.ta4j.Tick;

@JsonAutoDetect(
    fieldVisibility = Visibility.NONE,
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.NONE,
    setterVisibility = Visibility.NONE)
public class TickJson extends Tick implements Serializable {

  // TODO serialization does not work because Tick is a constructor immutable - maybe raise a request in ta4j...
  private static final long serialVersionUID = 1L;

  private static ObjectMapper json = new ObjectMapper();

  public TickJson(LocalDateTime date, double open, double high, double low, double close, double volume) {
    super(DateTime.parse(date.toString()), open, high, low, close, volume);
  }

  @JsonCreator
  public TickJson(@JsonProperty("date") long date,
      @JsonProperty("open") double open,
      @JsonProperty("high") double high,
      @JsonProperty("low") double low,
      @JsonProperty("close") double close,
      @JsonProperty("volume") double volume) {
    super(DateTime.parse(LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault()).toString()),
        open, high, low, close, volume);
  }

  @JsonProperty("date")
  public long getDate() {
    return super.getEndTime().toInstant().getMillis();
  }

  @JsonProperty("close")
  public double getClose() {
    return super.getClosePrice().toDouble();
  }

  @JsonProperty("open")
  public double getOpen() {
    return super.getOpenPrice().toDouble();
  }

  @JsonProperty("high")
  public double getHigh() {
    return super.getMaxPrice().toDouble();
  }

  @JsonProperty("volume")
  public double getVol() {
    return super.getVolume().toDouble();
  }

  @JsonProperty("low")
  public double getLow() {
    return super.getMinPrice().toDouble();
  }

  @Override
  public String toString() {
    String result = null;
    try {
      result = json.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return result;
  }

  @Deprecated
  public String toStringWithoutJson() {
    // {"date": 1401174943825, "open": 99.52, "high": 99.58, "low": 98.99, "close": 99.08, "volume": 100},

    ZonedDateTime date = ZonedDateTime.parse(getEndTime().toString());

    return String.format("{\"date\": %tQ, \"open\": %.2f, \"high\": %.2f, \"low\": %.2f, \"close\": %.2f, \"volume\": %.0f},",
        date,
        getOpenPrice().toDouble(),
        getMaxPrice().toDouble(),
        getMinPrice().toDouble(),
        getClosePrice().toDouble(),
        getVolume().toDouble()
        );
  }

}
