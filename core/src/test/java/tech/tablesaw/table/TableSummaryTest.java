package tech.tablesaw.table;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class TableSummaryTest {
  @Test
  public void summaryTestTwoDoubleColumnsStatistics() {
    DoubleColumn value = DoubleColumn.create("value1", new double[]{1.0, 1.1, 1.2});
    DoubleColumn value2 = DoubleColumn.create("value2", new double[]{2.0, 2.1, 2.2});

    Table testTable = Table.create("Data", value, value2);
    Table result = testTable.summary();
    assertEquals(
        "                            Data                             \n"
            + " Summary   |         value1         |        value2         |\n"
            + "-------------------------------------------------------------\n"
            + "    Count  |                     3  |                    3  |\n"
            + "      sum  |                   3.3  |                  6.3  |\n"
            + "     Mean  |                   1.1  |                  2.1  |\n"
            + "      Min  |                     1  |                    2  |\n"
            + "      Max  |                   1.2  |                  2.2  |\n"
            + "    Range  |   0.19999999999999996  |  0.20000000000000018  |\n"
            + " Variance  |  0.009999999999999995  |  0.01000000000000004  |\n"
            + " Std. Dev  |   0.09999999999999998  |   0.1000000000000002  |",
        result.print());
  }

  @Test
  public void summaryMixedTypes() {
    StringColumn label =
        StringColumn.create("label", new String[]{"yellow", "yellow", "green"});
    DoubleColumn value = DoubleColumn.create("value1", new double[]{ 1.0, 1.1, 1.2});
    BooleanColumn booleanValue = BooleanColumn.create("truthy", new boolean[]{ true, false, true});
    DateColumn dateColumn = DateColumn.create("dates", new LocalDate[]{
        LocalDate.of(2001, 1, 1),
        LocalDate.of(2002, 1, 1),
        LocalDate.of(2001, 1, 1) });

    Table testTable = Table.create("Data", label, value, booleanValue, dateColumn);
    Table result = testTable.summary();
    assertEquals(
        "                                   Data                                    \n"
            + "  Summary   |  label   |         value1         |  truthy  |    dates     |\n"
            + "---------------------------------------------------------------------------\n"
            + "     Count  |       3  |                     3  |          |           3  |\n"
            + "    Unique  |       2  |                        |          |              |\n"
            + "       Top  |  yellow  |                        |          |              |\n"
            + " Top Freq.  |       2  |                        |          |              |\n"
            + "       sum  |          |                   3.3  |          |              |\n"
            + "      Mean  |          |                   1.1  |          |              |\n"
            + "       Min  |          |                     1  |          |              |\n"
            + "       Max  |          |                   1.2  |          |              |\n"
            + "     Range  |          |   0.19999999999999996  |          |              |\n"
            + "  Variance  |          |  0.009999999999999995  |          |              |\n"
            + "  Std. Dev  |          |   0.09999999999999998  |          |              |\n"
            + "     false  |          |                        |       1  |              |\n"
            + "      true  |          |                        |       2  |              |\n"
            + "   Missing  |          |                        |          |           0  |\n"
            + "  Earliest  |          |                        |          |  2001-01-01  |\n"
            + "    Latest  |          |                        |          |  2002-01-01  |",
        result.print());
  }
}
