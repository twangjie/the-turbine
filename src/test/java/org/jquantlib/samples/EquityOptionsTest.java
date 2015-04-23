package org.jquantlib.samples;

import org.apache.commons.lang3.StringUtils;
import org.jquantlib.Settings;
import org.jquantlib.daycounters.Actual365Fixed;
import org.jquantlib.daycounters.DayCounter;
import org.jquantlib.exercise.AmericanExercise;
import org.jquantlib.exercise.BermudanExercise;
import org.jquantlib.exercise.EuropeanExercise;
import org.jquantlib.exercise.Exercise;
import org.jquantlib.instruments.EuropeanOption;
import org.jquantlib.instruments.Option;
import org.jquantlib.instruments.Payoff;
import org.jquantlib.instruments.PlainVanillaPayoff;
import org.jquantlib.instruments.VanillaOption;
import org.jquantlib.methods.lattices.AdditiveEQPBinomialTree;
import org.jquantlib.methods.lattices.CoxRossRubinstein;
import org.jquantlib.methods.lattices.JarrowRudd;
import org.jquantlib.methods.lattices.Joshi4;
import org.jquantlib.methods.lattices.LeisenReimer;
import org.jquantlib.methods.lattices.Tian;
import org.jquantlib.methods.lattices.Trigeorgis;
import org.jquantlib.pricingengines.AnalyticEuropeanEngine;
import org.jquantlib.pricingengines.PricingEngine;
import org.jquantlib.pricingengines.vanilla.BaroneAdesiWhaleyApproximationEngine;
import org.jquantlib.pricingengines.vanilla.BinomialVanillaEngine;
import org.jquantlib.pricingengines.vanilla.BjerksundStenslandApproximationEngine;
import org.jquantlib.pricingengines.vanilla.IntegralEngine;
import org.jquantlib.pricingengines.vanilla.JuQuadraticApproximationEngine;
import org.jquantlib.pricingengines.vanilla.finitedifferences.FDAmericanEngine;
import org.jquantlib.pricingengines.vanilla.finitedifferences.FDBermudanEngine;
import org.jquantlib.pricingengines.vanilla.finitedifferences.FDEuropeanEngine;
import org.jquantlib.processes.BlackScholesMertonProcess;
import org.jquantlib.quotes.Handle;
import org.jquantlib.quotes.Quote;
import org.jquantlib.quotes.SimpleQuote;
import org.jquantlib.termstructures.BlackVolTermStructure;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.termstructures.volatilities.BlackConstantVol;
import org.jquantlib.termstructures.yieldcurves.FlatForward;
import org.jquantlib.time.Calendar;
import org.jquantlib.time.Date;
import org.jquantlib.time.Month;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;
import org.jquantlib.time.calendars.Target;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;

public class EquityOptionsTest {

  private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

  private BlackScholesMertonProcess bsmProcess;
  private VanillaOption europeanOption;
  private VanillaOption bermudanOption;
  private VanillaOption americanOption;
  private int timeSteps;

  private class Result {
    public String instrument;
    public String algorithm;
    public Double value;
    public Long time;
    ObjectMapper objectMapper = new ObjectMapper();

    public Result(String instrument, String algorithm, Double value, Long time) {
      this.instrument = instrument;
      this.algorithm = algorithm;
      this.value = value;
      this.time = time;
    }

    @Override
    public String toString() {
      String result = null;
      try {
        result = objectMapper.writeValueAsString(this);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
      return result;
    }

  }

  @Before
  public void before() {

    Calendar calendar = new Target();
    Date todaysDate = new Date(15, Month.May, 1998);
    Date settlementDate = new Date(17, Month.May, 1998);
    new Settings().setEvaluationDate(todaysDate);

    Option.Type type = Option.Type.Put;
    double strike = 50.0D;
    double underlying = 47.0D;
    double riskFreeRate = 0.02D;
    double volatility = 0.3D;
    double dividendYield = 0.03D;

    Date maturity = new Date(7, Month.June, 2015);
    DayCounter dayCounter = new Actual365Fixed();

    Exercise europeanExercise = new EuropeanExercise(maturity);

    int bermudanForwards = 4;
    Date[] exerciseDates = new Date[bermudanForwards];
    for (int i = 1; i <= 4; i++) {
      exerciseDates[(i - 1)] = settlementDate.add(new Period(3 * i, TimeUnit.Months));
    }

    Exercise bermudanExercise = new BermudanExercise(exerciseDates);

    Exercise americanExercise = new AmericanExercise(settlementDate, maturity);

    Handle<Quote> underlyingH = new Handle<Quote>(new SimpleQuote(underlying));

    Handle<YieldTermStructure> flatDividendTS = new Handle<YieldTermStructure>(
        new FlatForward(settlementDate, dividendYield, dayCounter));

    Handle<YieldTermStructure> flatTermStructure = new Handle<YieldTermStructure>(
        new FlatForward(settlementDate, riskFreeRate, dayCounter));

    Handle<BlackVolTermStructure> flatVolTS = new Handle<BlackVolTermStructure>(
        new BlackConstantVol(settlementDate, calendar, volatility, dayCounter));

    Payoff payoff = new PlainVanillaPayoff(type, strike);

    bsmProcess = new BlackScholesMertonProcess(underlyingH, flatDividendTS, flatTermStructure, flatVolTS);

    europeanOption = new EuropeanOption(payoff, europeanExercise);

    bermudanOption = new VanillaOption(payoff, bermudanExercise);

    americanOption = new VanillaOption(payoff, americanExercise);

    timeSteps = 500;

  }

  @After
  public void after() {
  }

  private String objectName(Object engine) {
    String result = engine.getClass().getName();
    if (result != null && !"".equals(result)) {
      return result;
    }
    result = engine.getClass().getGenericSuperclass().getClass().getTypeName();
    return result;
  }
  
  private void price(VanillaOption option, PricingEngine engine) {
    Stopwatch clock = Stopwatch.createStarted();
    option.setPricingEngine(engine);
    Double value = option.NPV();
    long elapsed = clock.elapsed(java.util.concurrent.TimeUnit.MILLISECONDS);
    Result result = new Result(objectName(option), objectName(engine), value, elapsed);
    logger.info(result.toString());
  }

  @Test
  public void testBlackScholes() {
    price(europeanOption, new AnalyticEuropeanEngine(bsmProcess));
  }

  @Test
  public void testBaroneAdesiWhaley() {
    price(americanOption, new BaroneAdesiWhaleyApproximationEngine(bsmProcess));
  }

  @Test
  public void testBjerksundStensland() {
    price(americanOption, new BjerksundStenslandApproximationEngine(bsmProcess));
  }

  @Test
  public void testJuQuadratic() {
    price(americanOption, new JuQuadraticApproximationEngine(bsmProcess));
  }

  @Test
  public void testIntegral() {
    price(europeanOption, new IntegralEngine(bsmProcess));
  }

  @Test
  public void testBinomialJarrowRudd() {
    price(europeanOption, new BinomialVanillaEngine<JarrowRudd>(bsmProcess, timeSteps) {
    });
    price(bermudanOption, new BinomialVanillaEngine<JarrowRudd>(bsmProcess, timeSteps) {
    });
    price(americanOption, new BinomialVanillaEngine<JarrowRudd>(bsmProcess, timeSteps) {
    });
  }

  @Test
  public void testBinomialCoxRossRubinstein() {
    price(europeanOption, new BinomialVanillaEngine<CoxRossRubinstein>(bsmProcess, timeSteps) {
    });
    price(bermudanOption, new BinomialVanillaEngine<CoxRossRubinstein>(bsmProcess, timeSteps) {
    });
    price(americanOption, new BinomialVanillaEngine<CoxRossRubinstein>(bsmProcess, timeSteps) {
    });
  }

  @Test
  public void testAdditiveEquiProbabilities() {
    price(europeanOption, new BinomialVanillaEngine<AdditiveEQPBinomialTree>(bsmProcess, timeSteps) {
    });
    price(bermudanOption, new BinomialVanillaEngine<AdditiveEQPBinomialTree>(bsmProcess, timeSteps) {
    });
    price(americanOption, new BinomialVanillaEngine<AdditiveEQPBinomialTree>(bsmProcess, timeSteps) {
    });
  }

  @Test
  public void testBinomialTrigeorgis() {
    price(europeanOption, new BinomialVanillaEngine<Trigeorgis>(bsmProcess, timeSteps) {
    });
    price(bermudanOption, new BinomialVanillaEngine<Trigeorgis>(bsmProcess, timeSteps) {
    });
    price(americanOption, new BinomialVanillaEngine<Trigeorgis>(bsmProcess, timeSteps) {
    });
  }

  @Test
  public void testBinomialTian() {
    price(europeanOption, new BinomialVanillaEngine<Tian>(bsmProcess, timeSteps) {
    });
    price(bermudanOption, new BinomialVanillaEngine<Tian>(bsmProcess, timeSteps) {
    });
    price(americanOption, new BinomialVanillaEngine<Tian>(bsmProcess, timeSteps) {
    });
  }

  @Test
  public void testBinomialLeisenReimer() {
    price(europeanOption, new BinomialVanillaEngine<LeisenReimer>(bsmProcess, timeSteps) {
    });
    price(bermudanOption, new BinomialVanillaEngine<LeisenReimer>(bsmProcess, timeSteps) {
    });
    price(americanOption, new BinomialVanillaEngine<LeisenReimer>(bsmProcess, timeSteps) {
    });
  }

  @Test
  public void testBinomialJoshi() {
    price(europeanOption, new BinomialVanillaEngine<Joshi4>(bsmProcess, timeSteps) {
    });
    price(bermudanOption, new BinomialVanillaEngine<Joshi4>(bsmProcess, timeSteps) {
    });
    price(americanOption, new BinomialVanillaEngine<Joshi4>(bsmProcess, timeSteps) {
    });
  }

  @Test
  public void testFiniteDifferences() {
    price(europeanOption, new FDEuropeanEngine(bsmProcess, timeSteps, timeSteps - 1, false));
    price(bermudanOption, new FDBermudanEngine(bsmProcess, timeSteps, timeSteps - 1));
    price(americanOption, new FDAmericanEngine(bsmProcess, timeSteps, timeSteps - 1, false));
  }

}
