/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.columns;

import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Column functionality that is common across column types
 */
public class ColumnTest {

    private static final ColumnType[] types = {
            ColumnType.LOCAL_DATE,     // date of poll
            ColumnType.DOUBLE,         // approval rating (pct)
            ColumnType.STRING          // polling org
    };

    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").columnTypes(types));
    }

    @Test
    public void testFirst() {
        // test with dates
        DateColumn first = (DateColumn) table.dateColumn("date").first(3);
        assertEquals(LocalDate.parse("2004-02-04"), first.get(0));
        assertEquals(LocalDate.parse("2004-01-21"), first.get(1));
        assertEquals(LocalDate.parse("2004-01-07"), first.get(2));

        // test with ints
        NumberColumn first2 = (NumberColumn) table.numberColumn("approval").first(3);
        assertEquals(53, first2.get(0), 0.0001);
        assertEquals(53, first2.get(1), 0.0001);
        assertEquals(58, first2.get(2), 0.0001);

        // test with categories
        StringColumn first3 = (StringColumn) table.stringColumn("who").first(3);
        assertEquals("fox", first3.get(0));
        assertEquals("fox", first3.get(1));
        assertEquals("fox", first3.get(2));
    }

    @Test
    public void testLast() {

        // test with dates
        DateColumn last = (DateColumn) table.dateColumn("date").last(3);
        assertEquals(LocalDate.parse("2001-03-27"), last.get(0));
        assertEquals(LocalDate.parse("2001-02-27"), last.get(1));
        assertEquals(LocalDate.parse("2001-02-09"), last.get(2));

        // test with ints
        NumberColumn last2 = (NumberColumn) table.numberColumn("approval").last(3);
        assertEquals(52, last2.get(0), 0.0001);
        assertEquals(53, last2.get(1), 0.0001);
        assertEquals(57, last2.get(2), 0.0001);

        // test with categories
        StringColumn last3 = (StringColumn) table.stringColumn("who").last(3);
        assertEquals("zogby", last3.get(0));
        assertEquals("zogby", last3.get(1));
        assertEquals("zogby", last3.get(2));
    }

    @Test
    public void testName() {
        Column<?> c = table.numberColumn("approval");
        assertEquals("approval", c.name());
    }

    @Test
    public void testType() {
        Column<?> c = table.numberColumn("approval");
        assertEquals(ColumnType.DOUBLE, c.type());
    }

    @Test
    public void testContains() {
        Column<String> c = table.stringColumn("who");
        assertTrue(c.contains("fox"));
        assertFalse(c.contains("foxes"));
    }

    @Test
    public void testAsList() {
        Column<String> whoColumn = table.stringColumn("who");
        List<String> whos = whoColumn.asList();
        assertEquals(whos.size(), whoColumn.size());
    }

    @Test
    public void testMin() {
        double[] d1 = {1, 0, -1};
        double[] d2 = {2, -4, 3};

        DoubleColumn dc1 = DoubleColumn.create("t1", d1);
        DoubleColumn dc2 = DoubleColumn.create("t2", d2);
        DoubleColumn dc3 = (DoubleColumn) dc1.min(dc2);
        assertTrue(dc3.contains(1));
        assertTrue(dc3.contains(-4));
        assertTrue(dc3.contains(-1));
    }

    @Test
    public void testMax() {
        double[] d1 = {1, 0, -1};
        double[] d2 = {2, -4, 3};

        DoubleColumn dc1 = DoubleColumn.create("t1", d1);
        DoubleColumn dc2 = DoubleColumn.create("t2", d2);
        DoubleColumn dc3 = (DoubleColumn) dc1.max(dc2);
        assertTrue(dc3.contains(2));
        assertTrue(dc3.contains(0));
        assertTrue(dc3.contains(3));
    }
    
    // Functional methods

    private Predicate<Double> isPositiveOrZero = d -> d >= 0, isNegative = isPositiveOrZero.negate();
    private DoublePredicate isPositiveOrZeroD = d -> d >= 0, isNegativeD = d -> d < 0;

    @Test
    public void testCountAtLeast() {
        assertEquals(2, DoubleColumn.create("t1", new double[] {0, 1, 2}).count(isPositiveOrZero, 2));
        assertEquals(0, DoubleColumn.create("t1", new double[] {0, 1, 2}).count(isNegative, 2));
    }
    @Test
    public void testCountAtLeastDoubles() {
        assertEquals(2, DoubleColumn.create("t1", new double[] {0, 1, 2}).countDoubles(isPositiveOrZeroD, 2));
        assertEquals(0, DoubleColumn.create("t1", new double[] {0, 1, 2}).countDoubles(isNegativeD, 2));
    }

    @Test
    public void testCount() {
        assertEquals(3, DoubleColumn.create("t1", new double[] {0, 1, 2}).count(isPositiveOrZero));
        assertEquals(0, DoubleColumn.create("t1", new double[] {0, 1, 2}).count(isNegative));
    }
    @Test
    public void testCountDoubles() {
        assertEquals(3, DoubleColumn.create("t1", new double[] {0, 1, 2}).countDoubles(isPositiveOrZeroD));
        assertEquals(0, DoubleColumn.create("t1", new double[] {0, 1, 2}).countDoubles(isNegativeD));
    }

    @Test
    public void testAllMatch() {
        assertTrue(DoubleColumn.create("t1", new double[] {0, 1, 2}).allMatch(isPositiveOrZero));
        assertFalse(DoubleColumn.create("t1", new double[] {-1, 0, 1}).allMatch(isPositiveOrZero));
        assertFalse(DoubleColumn.create("t1", new double[] {1, 0, -1}).allMatch(isPositiveOrZero));
    }
    @Test
    public void testAllMatchDoubles() {
        assertTrue(DoubleColumn.create("t1", new double[] {0, 1, 2}).allMatchDoubles(isPositiveOrZeroD));
        assertFalse(DoubleColumn.create("t1", new double[] {-1, 0, 1}).allMatchDoubles(isPositiveOrZeroD));
        assertFalse(DoubleColumn.create("t1", new double[] {1, 0, -1}).allMatchDoubles(isPositiveOrZeroD));
    }

    @Test
    public void testAnyMatch() {
        assertTrue(DoubleColumn.create("t1", new double[] {0, 1, 2}).anyMatch(isPositiveOrZero));
        assertTrue(DoubleColumn.create("t1", new double[] {-1, 0, -1}).anyMatch(isPositiveOrZero));
        assertFalse(DoubleColumn.create("t1", new double[] {0, 1, 2}).anyMatch(isNegative));
    }
    @Test
    public void testAnyMatchDoubles() {
        assertTrue(DoubleColumn.create("t1", new double[] {0, 1, 2}).anyMatchDoubles(isPositiveOrZeroD));
        assertTrue(DoubleColumn.create("t1", new double[] {-1, 0, -1}).anyMatchDoubles(isPositiveOrZeroD));
        assertFalse(DoubleColumn.create("t1", new double[] {0, 1, 2}).anyMatchDoubles(isNegativeD));
    }
    
    @Test
    public void noneMatch() {
        assertTrue(DoubleColumn.create("t1", new double[] {0, 1, 2}).noneMatch(isNegative));
        assertFalse(DoubleColumn.create("t1", new double[] {-1, 0, 1}).noneMatch(isNegative));
        assertFalse(DoubleColumn.create("t1", new double[] {1, 0, -1}).noneMatch(isNegative));
    }
    @Test
    public void noneMatchDoubles() {
        assertTrue(DoubleColumn.create("t1", new double[] {0, 1, 2}).noneMatchDoubles(isNegativeD));
        assertFalse(DoubleColumn.create("t1", new double[] {-1, 0, 1}).noneMatchDoubles(isNegativeD));
        assertFalse(DoubleColumn.create("t1", new double[] {1, 0, -1}).noneMatchDoubles(isNegativeD));
    }
    
    @Test
    public void testFilter() {
        Column<Double> filtered = DoubleColumn.create("t1", new double[] {-1, 0, 1}).filter(isPositiveOrZero);
        assertEquals(2, filtered.size());
        assertTrue(filtered.contains(0.0));
        assertTrue(filtered.contains(1.0));
        assertFalse(filtered.contains(-1.0));
    }
    @Test
    public void testFilterDoubles() {
        Column<Double> filtered = DoubleColumn.create("t1", new double[] {-1, 0, 1}).filterDoubles(isPositiveOrZeroD);
        assertEquals(2, filtered.size());
        assertTrue(filtered.contains(0.0));
        assertTrue(filtered.contains(1.0));
        assertFalse(filtered.contains(-1.0));
    }

    private <T> void check(Column<T> column, T... ts) {
        assertEquals(ts.length, column.size());
        for (int i = 0; i < ts.length; i++) {
            assertEquals(ts[i], column.get(i));
        }
    }
    
    private Function<Double, String> toString = d -> d.toString();
    private DoubleFunction<String> toStringD = d -> String.valueOf(d);
    
    @Test
    public void testMapInto() {
        check(DoubleColumn.create("t1", new double[] {-1, 0, 1}).mapInto(toString, StringColumn.create("result")), "-1.0", "0.0", "1.0");
    }
    @Test
    public void testMapIntoDoubles() {
        check(DoubleColumn.create("t1", new double[] {-1, 0, 1}).mapDoublesInto(toStringD, StringColumn.create("result")), "-1.0", "0.0", "1.0");
    }

    private Function<Double, Double> negate = d -> -d;
    
    @Test
    public void testMap() {
        check(DoubleColumn.create("t1", new double[] {-1, 0, 1}).map(negate), 1.0, -0.0, -1.0);
    }
    
    private ToDoubleFunction<String> valueOf = s -> Double.valueOf(s);

    @Test
    public void testMapToDoubles() {
        check(StringColumn.create("t1", new String[] {"-1.0", "0.0", "1.0"}).mapToDoubles(valueOf), -1.0, 0.0, 1.0);
    }
    
    @Test
    public void testMaxComparator() {
        assertEquals(Double.valueOf(1.0), DoubleColumn.create("t1", new double[] {-1, 0, 1}).max(Double::compare).get());
        assertFalse(DoubleColumn.create("t1").max((d1, d2) -> (int) (d1 - d2)).isPresent());
    }
    @Test
    public void testMaxDoubleComparator() {
        assertEquals(Double.valueOf(1.0), DoubleColumn.create("t1", new double[] {-1, 0, 1}).maxDoubles(Double::compare).get());
        assertFalse(DoubleColumn.create("t1").maxDoubles((d1, d2) -> (int) (d1 - d2)).isPresent());
    }

    @Test
    public void testMinComparator() {
        assertEquals(Double.valueOf(-1.0), DoubleColumn.create("t1", new double[] {-1, 0, 1}).min(Double::compare).get());
        assertFalse(DoubleColumn.create("t1").min((d1, d2) -> (int) (d1 - d2)).isPresent());
    }
    @Test
    public void testMinDoubleComparator() {
        assertEquals(Double.valueOf(-1.0), DoubleColumn.create("t1", new double[] {-1, 0, 1}).minDoubles(Double::compare).get());
        assertFalse(DoubleColumn.create("t1").minDoubles((d1, d2) -> (int) (d1 - d2)).isPresent());
    }

    private BinaryOperator<Double> sum = (d1, d2) -> d1 + d2;
    private DoubleBinaryOperator sumD = (d1, d2) -> d1 + d2;
   
    @Test
    public void testReduceTBinaryOperator() {
        assertEquals(Double.valueOf(1.0), DoubleColumn.create("t1", new double[] {-1, 0, 1}).reduce(1.0, sum));
    }
    @Test
    public void testReduceTDoubleBinaryOperator() {
        assertEquals(1.0, DoubleColumn.create("t1", new double[] {-1, 0, 1}).reduceDoubles(1.0, sumD), 0.0);
    }
    
    @Test
    public void testReduceBinaryOperator() {
        assertEquals(Double.valueOf(0.0), DoubleColumn.create("t1", new double[] {-1, 0, 1}).reduce(sum).get());
        assertFalse(DoubleColumn.create("t1", new double[] {}).reduce(sum).isPresent());
    }
    @Test
    public void testReduceDoubleBinaryOperator() {
        assertEquals(Double.valueOf(0.0), DoubleColumn.create("t1", new double[] {-1, 0, 1}).reduceDoubles(sumD).get());
        assertFalse(DoubleColumn.create("t1", new double[] {}).reduceDoubles(sumD).isPresent());
    }
    
    @Test
    public void sorted() {
        check(DoubleColumn.create("t1", new double[] {1, -1, 0}).sorted(Double::compare), -1.0, 0.0, 1.0);
    }
}
