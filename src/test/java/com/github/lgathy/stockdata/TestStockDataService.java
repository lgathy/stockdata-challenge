package com.github.lgathy.stockdata;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class TestStockDataService {
    
    private StockDataService service;
    
    private ImmutableList<String> inputFileContent;
    
    @Before
    public void setup() throws IOException {
        service = new StockDataServiceImpl();
        inputFileContent = openInputFile("/testdata.csv").collect(ImmutableList.toImmutableList());
    }
    
    @Test
    public void processTestdata() throws IOException {
        List<DailyClosePrice> monthlyClosePrices = service.collectMonthlyClosePrices(inputFileContent.stream());
        assertNotNull("monthlyClosePrices", monthlyClosePrices);
        assertThat(copySorted(monthlyClosePrices), Matchers.contains(EXPECTED_RESULTS_IN_ORDER));
    }
    
    @Test
    public void processTestdataParallel() throws IOException {
        List<DailyClosePrice> monthlyClosePrices = service.collectMonthlyClosePrices(inputFileContent.parallelStream());
        assertNotNull("monthlyClosePrices", monthlyClosePrices);
        assertThat(copySorted(monthlyClosePrices), Matchers.contains(EXPECTED_RESULTS_IN_ORDER));
    }
    
    @Test
    public void stressTestOneMillionElements() throws IOException {
        Iterable<String> oneMillionElements = Iterables.limit(Iterables.cycle(inputFileContent), 1_000_000);
        Stream<String> hugeStream = StreamSupport.stream(oneMillionElements.spliterator(), false);
        List<DailyClosePrice> monthlyClosePrices = service.collectMonthlyClosePrices(hugeStream);
        assertThat(copySorted(monthlyClosePrices), Matchers.contains(EXPECTED_RESULTS_IN_ORDER));
    }
    
    @Test
    public void stressTestOneMillionElementsParallel() throws IOException {
        Stream<String> aggregatedStream = inputFileContent.parallelStream();
        for (int i = 0; i < 250; ++i) {
            aggregatedStream = Stream.concat(aggregatedStream, inputFileContent.parallelStream());
        }
        List<DailyClosePrice> monthlyClosePrices = service.collectMonthlyClosePrices(aggregatedStream);
        assertThat(copySorted(monthlyClosePrices), Matchers.contains(EXPECTED_RESULTS_IN_ORDER));
    }
    
    @Test
    public void stressTestMultipleRuns() throws IOException {
        for (int i = 0; i < 250; ++i) {
            assertNotNull(service.collectMonthlyClosePrices(inputFileContent.stream()));
        }
    }
    
    protected Stream<String> openInputFile(String filePath) throws IOException {
        List<String> inputLines = Resources.readLines(getClass().getResource(filePath), Charsets.UTF_8);
        return inputLines.stream().skip(1L); // the purpose is to ignore the header line placed in the testfile
    }
    
    static ArrayList<DailyClosePrice> copySorted(List<DailyClosePrice> monthlyClosePrices) {
        ArrayList<DailyClosePrice> sortedResults = new ArrayList<>(monthlyClosePrices);
        Collections.sort(sortedResults, Comparator.comparing(DailyClosePrice::getDate));
        return sortedResults;
    }
    
    static DailyClosePrice resultOf(String date, String price) {
        return new DailyClosePrice()
            .setDate(LocalDate.parse(date))
            .setClose(new BigDecimal(price));
    }
    
    private static final DailyClosePrice[] EXPECTED_RESULTS_IN_ORDER = {
        resultOf("2001-08-31", "114.15"),
        resultOf("2001-09-28", "104.44"),
        resultOf("2001-10-31", "105.8"),
        resultOf("2001-11-30", "114.05"),
        resultOf("2001-12-31", "114.3"),
        resultOf("2002-01-31", "113.18"),
        resultOf("2002-02-28", "111.15"),
        resultOf("2002-03-28", "114.52"),
        resultOf("2002-04-30", "107.86"),
        resultOf("2002-05-31", "106.92"),
        resultOf("2002-06-28", "98.96"),
        resultOf("2002-07-31", "91.25"),
        resultOf("2002-08-30", "91.78"),
        resultOf("2002-09-30", "81.79"),
        resultOf("2002-10-31", "88.52"),
        resultOf("2002-11-29", "93.95"),
        resultOf("2002-12-31", "88.23"),
        resultOf("2003-01-31", "86.06"),
        resultOf("2003-02-28", "84.9"),
        resultOf("2003-03-31", "84.74"),
        resultOf("2003-04-30", "91.91"),
        resultOf("2003-05-30", "96.95"),
        resultOf("2003-06-30", "97.63"),
        resultOf("2003-07-31", "99.39"),
        resultOf("2003-08-29", "101.44"),
        resultOf("2003-09-30", "99.95"),
        resultOf("2003-10-31", "105.3"),
        resultOf("2003-11-28", "106.5"),
        resultOf("2003-12-31", "111.28"),
        resultOf("2004-01-30", "113.48"),
        resultOf("2004-02-27", "115.09"),
        resultOf("2004-03-31", "113.16"),
        resultOf("2004-04-30", "110.96"),
        resultOf("2004-05-28", "112.86"),
        resultOf("2004-06-30", "114.53"),
        resultOf("2004-07-30", "110.82"),
        resultOf("2004-08-31", "111.13"),
        resultOf("2004-09-30", "111.76"),
        resultOf("2004-10-29", "113.2"),
        resultOf("2004-11-30", "117.89"),
        resultOf("2004-12-31", "120.87"),
        resultOf("2005-01-31", "118.16"),
        resultOf("2005-02-28", "120.63"),
        resultOf("2005-03-31", "117.96"),
        resultOf("2005-04-29", "115.75"),
        resultOf("2005-05-31", "119.48"),
        resultOf("2005-06-30", "119.18"),
        resultOf("2005-07-29", "123.74"),
        resultOf("2005-08-31", "122.58"),
        resultOf("2005-09-30", "123.04"),
        resultOf("2005-10-31", "120.13"),
        resultOf("2005-11-30", "125.41"),
        resultOf("2005-12-30", "124.51"),
        resultOf("2006-01-31", "127.5"),
        resultOf("2006-02-28", "128.23"),
        resultOf("2006-03-31", "129.83"),
        resultOf("2006-04-28", "131.47"),
        resultOf("2006-05-31", "127.51"),
        resultOf("2006-06-30", "127.23"),
        resultOf("2006-07-31", "127.85"),
        resultOf("2006-08-31", "130.64"),
        resultOf("2006-09-29", "133.58"),
        resultOf("2006-10-31", "137.79"),
        resultOf("2006-11-30", "140.53"),
        resultOf("2006-12-29", "141.62"),
        resultOf("2007-01-31", "143.75"),
        resultOf("2007-02-28", "140.93"),
        resultOf("2007-03-30", "142"),
        resultOf("2007-04-30", "148.29"),
        resultOf("2007-05-31", "153.32"),
        resultOf("2007-06-29", "150.43"),
        resultOf("2007-07-31", "145.72"),
        resultOf("2007-08-31", "147.59"),
        resultOf("2007-09-28", "152.58"),
        resultOf("2007-10-31", "154.65"),
        resultOf("2007-11-30", "148.66"),
        resultOf("2007-12-31", "146.21"),
        resultOf("2008-01-31", "137.37"),
        resultOf("2008-02-29", "133.82"),
        resultOf("2008-03-31", "131.89"),
        resultOf("2008-04-30", "138.26"),
        resultOf("2008-05-30", "140.26"),
        resultOf("2008-06-30", "128.04"),
        resultOf("2008-07-31", "126.83"),
        resultOf("2008-08-29", "128.69"),
        resultOf("2008-09-30", "115.99"),
        resultOf("2008-10-31", "96.83"),
        resultOf("2008-11-28", "90.09"),
        resultOf("2008-12-31", "90.24"),
        resultOf("2009-01-30", "82.83"),
        resultOf("2009-02-27", "73.93"),
        resultOf("2009-03-31", "79.52"),
        resultOf("2009-04-30", "87.42"),
        resultOf("2009-05-29", "92.53"),
        resultOf("2009-06-30", "91.95"),
        resultOf("2009-07-31", "98.81"),
        resultOf("2009-08-31", "102.46"),
        resultOf("2009-09-30", "105.59"),
        resultOf("2009-10-30", "103.56"),
        resultOf("2009-11-30", "109.94"),
        resultOf("2009-12-31", "111.44"),
        resultOf("2010-01-29", "107.39"),
        resultOf("2010-02-26", "110.74"),
        resultOf("2010-03-31", "117"),
        resultOf("2010-04-30", "118.81"),
        resultOf("2010-05-28", "109.37"),
        resultOf("2010-06-30", "103.22"),
        resultOf("2010-07-30", "110.27"),
        resultOf("2010-08-31", "105.31"),
        resultOf("2010-09-30", "114.13"),
        resultOf("2010-10-29", "118.49"),
        resultOf("2010-11-30", "118.49"),
        resultOf("2010-12-31", "125.75"),
        resultOf("2011-01-31", "128.68"),
        resultOf("2011-02-28", "133.15"),
        resultOf("2011-03-31", "132.51"),
        resultOf("2011-04-29", "136.54"),
        resultOf("2011-05-31", "134.89"),
        resultOf("2011-06-30", "131.97"),
        resultOf("2011-07-29", "129.05"),
        resultOf("2011-08-31", "122.06"),
        resultOf("2011-09-30", "113.17"),
        resultOf("2011-10-31", "125.45"),
        resultOf("2011-11-30", "125.11"),
        resultOf("2011-12-30", "125.5"),
        resultOf("2012-01-31", "131.21"),
        resultOf("2012-02-29", "136.87"),
        resultOf("2012-03-30", "140.72"),
        resultOf("2012-04-30", "139.77"),
        resultOf("2012-05-31", "131.49"),
        resultOf("2012-06-29", "136.27"),
        resultOf("2012-07-31", "137.71"),
        resultOf("2012-08-31", "141.24"),
        resultOf("2012-09-28", "143.93"),
        resultOf("2012-10-31", "141.18"),
        resultOf("2012-11-30", "142.16"),
        resultOf("2012-12-31", "142.41"),
        resultOf("2013-01-31", "149.7"),
        resultOf("2013-02-28", "151.61"),
        resultOf("2013-03-28", "156.67"),
        resultOf("2013-04-30", "159.68"),
        resultOf("2013-05-31", "163.44"),
        resultOf("2013-06-28", "160.42"),
        resultOf("2013-07-31", "168.71"),
        resultOf("2013-08-30", "163.65"),
        resultOf("2013-09-30", "168.01"),
        resultOf("2013-10-31", "175.79"),
        resultOf("2013-11-29", "181"),
        resultOf("2013-12-31", "184.69"),
        resultOf("2014-01-31", "178.18"),
        resultOf("2014-02-28", "186.29"),
        resultOf("2014-03-31", "187.01"),
        resultOf("2014-04-30", "188.31"),
        resultOf("2014-05-30", "192.68"),
        resultOf("2014-06-30", "195.72"),
        resultOf("2014-07-31", "193.09"),
        resultOf("2014-08-29", "200.71"),
        resultOf("2014-09-30", "197.02"),
        resultOf("2014-10-31", "201.66"),
        resultOf("2014-11-28", "207.2"),
        resultOf("2014-12-31", "205.54"),
        resultOf("2015-01-30", "199.45"),
        resultOf("2015-02-27", "210.66"),
        resultOf("2015-03-31", "206.43"),
        resultOf("2015-04-30", "208.46"),
        resultOf("2015-05-29", "211.14"),
        resultOf("2015-06-30", "205.85"),
        resultOf("2015-07-31", "210.5"),
        resultOf("2015-08-31", "197.54"),
        resultOf("2015-09-30", "191.63"),
        resultOf("2015-10-30", "207.93"),
        resultOf("2015-11-30", "208.69"),
        resultOf("2015-12-31", "203.87"),
        resultOf("2016-01-29", "193.72"),
        resultOf("2016-02-29", "193.35"),
        resultOf("2016-03-31", "205.52"),
        resultOf("2016-04-29", "206.33"),
        resultOf("2016-05-31", "209.84"),
        resultOf("2016-06-30", "209.48"),
        resultOf("2016-07-29", "217.12"),
        resultOf("2016-08-31", "217.38"),
        resultOf("2016-09-30", "216.3"),
        resultOf("2016-10-31", "212.55"),
        resultOf("2016-11-30", "220.38"),
        resultOf("2016-12-30", "223.53"),
        resultOf("2017-01-31", "227.53"),
        resultOf("2017-02-28", "236.47"),
        resultOf("2017-03-31", "235.74"),
        resultOf("2017-04-28", "238.08"),
        resultOf("2017-05-31", "241.44"),
        resultOf("2017-06-30", "241.8"),
        resultOf("2017-07-25", "247.42")
    };
}
