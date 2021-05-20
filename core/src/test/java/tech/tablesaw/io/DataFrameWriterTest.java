package tech.tablesaw.io;

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.*;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

public class DataFrameWriterTest {

  private static final String LINE_END = System.lineSeparator();

  private double[] v1 = {1, 2, NaN};
  private double[] v2 = {1, 2, NaN};
  private Table table =
      Table.create("t", DoubleColumn.create("v", v1), DoubleColumn.create("v2", v2));

  @Test
  public void csv() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    table.write().csv(baos);
    String output = baos.toString();
    assertEquals(
        "v,v2" + LINE_END + "1.0,1.0" + LINE_END + "2.0,2.0" + LINE_END + "," + LINE_END + "",
        output);
  }

  @Test
  public void csv2() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamWriter osw = new OutputStreamWriter(baos);
    table.write().csv(osw);
    String output = baos.toString();
    assertEquals(
        "v,v2" + LINE_END + "1.0,1.0" + LINE_END + "2.0,2.0" + LINE_END + "," + LINE_END + "",
        output);
  }

  @Test
  public void testFileOutputStreamWhetherClose() throws IOException {
    File file = new File("../testoutput/example.csv");
    FileOutputStream fos = new FileOutputStream(file);
    table.write().csv(fos);
    FileInputStream fis = new FileInputStream(file);
    byte[] filecontent = new byte[(int) file.length()];
    fis.read(filecontent);
    String output = new String(filecontent);
    assertEquals(
        "v,v2" + LINE_END + "1.0,1.0" + LINE_END + "2.0,2.0" + LINE_END + "," + LINE_END + "",
        output);

    // cannot access the status of fos, so write again to test whether it close
    try {
      table.write().csv(fos);
      fis = new FileInputStream(file);
      filecontent = new byte[(int) file.length()];
      fis.read(filecontent);
      output = new String(filecontent);
      assertEquals(
          "v,v2" + LINE_END + "1.0,1.0" + LINE_END + "2.0,2.0" + LINE_END + "," + LINE_END + "v,v2"
              + LINE_END + "1.0,1.0" + LINE_END + "2.0,2.0" + LINE_END + "," + LINE_END + "",
          output);
    } catch (Exception e) {
      fail(e);
    } finally {
      file.delete();
    }
  }

  @Test
  public void testOutputStreamWriterWhetherClose() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamWriter osw = new OutputStreamWriter(baos);
    table.write().csv(osw);
    String output = baos.toString();
    assertEquals(
        "v,v2" + LINE_END + "1.0,1.0" + LINE_END + "2.0,2.0" + LINE_END + "," + LINE_END + "",
        output);

    // cannot access the status of osw directly, so write again to test whether it close
    try {
      table.write().csv(osw);
      assertEquals(
          "v,v2" + LINE_END + "1.0,1.0" + LINE_END + "2.0,2.0" + LINE_END + "," + LINE_END + "v,v2"
              + LINE_END + "1.0,1.0" + LINE_END + "2.0,2.0" + LINE_END + "," + LINE_END + "",
          baos.toString());
    } catch (Exception e) {
      fail(e);
    }
  }
}
