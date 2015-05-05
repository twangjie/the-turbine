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
package org.jimsey.projects.turbine.spring.camel.typeconverters;

import java.io.IOException;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.jimsey.projects.turbine.spring.domain.TurbineObject;
import org.springframework.util.SerializationUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Converter
public class TurbineObjectConverter {

  private static ObjectMapper json = new ObjectMapper();

  @Converter
  public static byte[] toBytes(TurbineObject object, Exchange exchange) {
    return SerializationUtils.serialize(object);
  }
  @Converter
  public static String toString(TurbineObject object, Exchange exchange) {
    return object.toString();
  }

  // --------------------------------------------
  @Converter
  public static TurbineObject toEntity(byte[] bytes, Exchange exchange) {
    return (TurbineObject) SerializationUtils.deserialize(bytes);
  }

  @Converter
  public static TurbineObject toEntity(String text, Exchange exchange) {
    TurbineObject object = null;
    try {
      object = json.readValue(text, TurbineObject.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return object;
  }

}