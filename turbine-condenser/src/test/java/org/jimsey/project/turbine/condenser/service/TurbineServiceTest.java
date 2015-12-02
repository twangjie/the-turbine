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
package org.jimsey.project.turbine.condenser.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.invoke.MethodHandle;
import java.util.List;

import org.jimsey.projects.turbine.condenser.service.Ping;
import org.jimsey.projects.turbine.condenser.service.TurbineService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurbineServiceTest {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandle.class.getName());

  @InjectMocks
  private TurbineService turbineService;

  @Mock
  private Ping ping;

  @Before
  public void setup() {
    turbineService = new TurbineService();
  }

  @Test
  public void testListIndicators() throws Exception {
    // logger.info("{}", turbineService.listIndicators());
    List<String> indicators = turbineService.listIndicators();
    assertThat(indicators.size(), is(greaterThan(0)));
  }

  @Test
  public void testListStrategies() throws Exception {
    // logger.info("{}", turbineService.listStrategies());
    List<String> strategies = turbineService.listStrategies();
    assertThat(strategies.size(), is(greaterThan(0)));
  }

}
