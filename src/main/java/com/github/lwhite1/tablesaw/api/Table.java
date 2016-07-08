package com.github.lwhite1.tablesaw.api;

import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.filtering.Filter;
import com.github.lwhite1.tablesaw.io.csv.CsvReader;
import com.github.lwhite1.tablesaw.io.csv.CsvWriter;
import com.github.lwhite1.tablesaw.io.html.HtmlTableWriter;
import com.github.lwhite1.tablesaw.io.jdbc.SqlResultSetReader;
import com.github.lwhite1.tablesaw.reducing.NumericReduceFunction;
import com.github.lwhite1.tablesaw.sorting.Sort;
import com.github.lwhite1.tablesaw.store.StorageManager;
import com.github.lwhite1.tablesaw.store.TableMetadata;
import com.github.lwhite1.tablesaw.table.Projection;
import com.github.lwhite1.tablesaw.table.Relation;
import com.github.lwhite1.tablesaw.table.Rows;
import com.github.lwhite1.tablesaw.table.SubTable;
import com.github.lwhite1.tablesaw.table.TemporaryView;
import com.github.lwhite1.tablesaw.table.ViewGroup;
import com.github.lwhite1.tablesaw.util.IntComparatorChain;
import com.github.lwhite1.tablesaw.util.ReversingIntComparator;
import com.github.lwhite1.tablesaw.util.Selection;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntIterator;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.lwhite1.tablesaw.sorting.Sort.Order;

/**
 * A table of data, consisting of some number of columns, each of which has the same number of rows.
 * All the data in a column has the same type: integer, float, category, etc., but a table may contain an arbitrary
 * number of columns of any type.
 * <p>
 * Tables are the main data-type and primary focus of Tablesaw.
 */
public class Table implements Relation, IntIterable {

  /**
   * The name of the table
   */
  private String name;

  /**
   * The columns that hold the data in this table
   */
  private final List<Column> columnList = new ArrayList<>();

  /**
   * Returns a new table initialized with the given name
   */
  private Table(String name) {
    this.name = name;
  }

  /**
   * Returns a new table initialized with data from the given TableMetadata object
   * <p>
   * The metadata is used by the storage module to save tables and read their data from disk
   */
  private Table(TableMetadata metadata) {
    this.name = metadata.getName();
  }

  /**
   * Returns a new Relation initialized with the given names and columns
   *
   * @param name    The name of the table
   * @param columns One or more columns, all of which must have either the same length or size 0
   */
  protected Table(String name, Column... columns) {
    this(name);
    for (Column column : columns) {
      this.addColumn(column);
    }
  }

  /**
   * Returns a new, empty table (without rows or columns) with the given name
   */
  public static Table create(String tableName) {
    return new Table(tableName);
  }


  /**
   * Returns a new, empty table constructed according to the given metadata
   */
  public static Table create(TableMetadata metadata) {
    return new Table(metadata);
  }

  /**
   * Returns a new table with the given columns and given name
   *
   * @param columns One or more columns, all of the same @code{column.size()}
   */
  public static Table create(String tableName, Column... columns) {
    return new Table(tableName, columns);
  }

  /**
   * Adds the given column to this table
   */
  @Override
  public void addColumn(Column... cols) {
    for (Column c: cols) {
      validateColumn(c);
      columnList.add(c);
    }
  }

  /**
   * Throws a runtime exception if a column with the given name is already in the table
   */
  private void validateColumn(Column newColumn) {
    Preconditions.checkNotNull(newColumn, "Attempted to add a null to the columns in table " + name);
    List<String> stringList = new ArrayList<>();
    for (String name: columnNames()) {
      stringList.add(name.toLowerCase());
    }
    if (stringList.contains(newColumn.name().toLowerCase())) {
      String message = String.format("Cannot add column with duplicate name %s to table %s", newColumn, name);
      throw new RuntimeException(message);
    }
  }

  /**
   * Adds the given column to this table at the given position in the column list
   *
   * @param index  Zero-based index into the column list
   * @param column Column to be added
   */
  public void addColumn(int index, Column column) {
    validateColumn(column);
    columnList.add(index, column);
  }

  /**
   * Sets the name of the table
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the column at the given index in the column list
   *
   * @param columnIndex an integer at least 0 and less than number of columns in the relation
   */
  @Override
  public Column column(int columnIndex) {
    return columnList.get(columnIndex);
  }

  /**
   * Returns the number of columns in the table
   */
  @Override
  public int columnCount() {
    return columnList.size();
  }

  /**
   * Returns the number of rows in the table
   */
  @Override
  public int rowCount() {
    int result = 0;
    if (!columnList.isEmpty()) {
      // all the columns have the same number of elements, so we can check any of them
      result = columnList.get(0).size();
    }
    return result;
  }

  /**
   * Returns the list of columns
   */
  @Override
  public List<Column> columns() {
    return columnList;
  }


  /**
   * Returns only the columns whose names are given in the input array
   */
  public List<Column> columns(String... columnNames) {
    List<Column> columns = new ArrayList<>();
    for (String columnName : columnNames) {
      columns.add(column(columnName));
    }
    return columns;
  }

  /**
   * Returns the index of the column with the given name
   *
   * @throws IllegalArgumentException if the input string is not the name of any column in the table
   */
  public int columnIndex(String columnName) {
    int columnIndex = -1;
    for (int i = 0; i < columnList.size(); i++) {
      if (columnList.get(i).name().equalsIgnoreCase(columnName)) {
        columnIndex = i;
        break;
      }
    }
    if (columnIndex == -1) {
      throw new IllegalArgumentException(String.format("Column %s is not present in table %s", columnName, name));
    }
    return columnIndex;
  }

  /**
   * Returns the index of the given column (its position in the list of columns)
   * <p>
   *
   * @throws IllegalArgumentException if the column is not present in this table
   */
  public int columnIndex(Column column) {
    int columnIndex = -1;
    for (int i = 0; i < columnList.size(); i++) {
      if (columnList.get(i).equals(column)) {
        columnIndex = i;
        break;
      }
    }
    if (columnIndex == -1) {
      throw new IllegalArgumentException(String.format("Column %s is not present in table %s", column.name(), name));
    }
    return columnIndex;
  }

  /**
   * Returns the name of the table
   */
  @Override
  public String name() {
    return name;
  }

  /**
   * Returns a List of the names of all the columns in this table
   */
  public List<String> columnNames() {
    List<String> names = new ArrayList<>(columnList.size());
    names.addAll(columnList.stream().map(Column::name).collect(Collectors.toList()));
    return names;
  }

  /**
   * Returns a string representation of the value at the given row and column indexes
   *
   * @param c the column index, 0 based
   * @param r the row index, 0 based
   */
  @Override
  public String get(int c, int r) {
    Column column = column(c);
    return column.getString(r);
  }

  /**
   * Returns a table with the same columns as this table, but no data
   */
  public Table emptyCopy() {
    Table copy = new Table(name);
    for (Column column : columnList) {
      copy.addColumn(column.emptyCopy());
    }
    return copy;
  }

  /**
   * Returns a table with the same columns as this table, but no data, initialized to the given row size
   */
  public Table emptyCopy(int rowSize) {
    Table copy = new Table(name);
    for (Column column : columnList) {
      copy.addColumn(column.emptyCopy(rowSize));
    }
    return copy;
  }

  /**
   * Clears all the data from this table
   */
  @Override
  public void clear() {
    columnList.forEach(Column::clear);
  }

  /**
   * Returns a new table containing the first {@code nrows} of data in this table
   */
  public Table first(int nRows) {
    nRows = Math.min(nRows, rowCount());
    Table newTable = emptyCopy(nRows);
    Rows.head(nRows, this, newTable);
    return newTable;
  }

  /**
   * Returns a new table containing the last {@code nrows} of data in this table
   */
  public Table last(int nRows) {
    nRows = Math.min(nRows, rowCount());
    Table newTable = emptyCopy(nRows);
    Rows.tail(nRows, this, newTable);
    return newTable;
  }

  /**
   * Returns a sort Key that can be used for simple or chained comparator sorting
   * <p>
   * You can extend the sort key by using .next() to fill more columns to the sort order
   */
  private static Sort first(String columnName, Sort.Order order) {
    return Sort.on(columnName, order);
  }

  /**
   * Returns a copy of this table sorted on the given column names, applied in order, ascending
   */
  public Table sortOn(String... columnNames) {
    Sort key = null;
    for (String s : columnNames) {
      if (key == null) {
        key = first(s, Order.ASCEND);
      } else {
        key.next(s, Order.ASCEND);
      }
    }
    return sortOn(key);
  }

  /**
   * Returns a copy of this table sorted in the order of the given column names, in ascending order
   */
  public Table sortAscendingOn(String... columnNames) {
    return this.sortOn(columnNames);
  }

  /**
   * Returns a copy of this table sorted on the given column names, applied in order, descending
   */
  public Table sortDescendingOn(String... columnNames) {
    Sort key = getSort(columnNames);
    return sortOn(key);
  }

  /**
   * Returns an object that can be used to sort this table in the order specified for by the given column names
   */
  @VisibleForTesting
  public static Sort getSort(String... columnNames) {
    Sort key = null;
    for (String s : columnNames) {
      if (key == null) {
        key = first(s, Order.DESCEND);
      } else {
        key.next(s, Order.DESCEND);
      }
    }
    return key;
  }

  /**
   * Returns a copy of this table sorted on the given columns
   * <p>
   * The columns are sorted in reverse order if they value matching the name is {@code true}
   */
  public Table sortOn(Sort key) {
    Preconditions.checkArgument(!key.isEmpty());
    if (key.size() == 1) {
      IntComparator comparator = getComparator(key);
      return sortOn(comparator);
    }
    IntComparatorChain chain = getChain(key);
    return sortOn(chain);
  }

  /**
   * Returns a comparator that can be used to sort the records in this table according to the given sort key
   */
  public IntComparator getComparator(Sort key) {
    Iterator<Map.Entry<String, Sort.Order>> entries = key.iterator();
    Map.Entry<String, Sort.Order> sort = entries.next();
    IntComparator comparator;
    if (sort.getValue() == Order.ASCEND) {
      comparator = rowComparator(sort.getKey(), false);
    } else {
      comparator = rowComparator(sort.getKey(), true);
    }
    return comparator;
  }

  /**
   * Returns a comparator chain for sorting according to the given key
   */
  private IntComparatorChain getChain(Sort key) {
    Iterator<Map.Entry<String, Sort.Order>> entries = key.iterator();
    Map.Entry<String, Sort.Order> sort = entries.next();

    IntComparator comparator;
    if (sort.getValue() == Order.ASCEND) {
      comparator = rowComparator(sort.getKey(), false);
    } else {
      comparator = rowComparator(sort.getKey(), true);
    }

    IntComparatorChain chain = new IntComparatorChain(comparator);
    while (entries.hasNext()) {
      sort = entries.next();
      if (sort.getValue() == Order.ASCEND) {
        chain.addComparator(rowComparator(sort.getKey(), false));
      } else {
        chain.addComparator(rowComparator(sort.getKey(), true));
      }
    }
    return chain;
  }

  /**
   * Returns a copy of this table sorted using the given comparator
   */
  public Table sortOn(IntComparator rowComparator) {
    Table newTable = emptyCopy(rowCount());

    int[] newRows = rows();
    IntArrays.parallelQuickSort(newRows, rowComparator);

    Rows.copyRowsToTable(IntArrayList.wrap(newRows), this, newTable);
    return newTable;
  }

  /**
   * Returns an array of ints of the same number of rows as the table
   */
  @VisibleForTesting
  public int[] rows() {
    int[] rowIndexes = new int[rowCount()];
    for (int i = 0; i < rowCount(); i++) {
      rowIndexes[i] = i;
    }
    return rowIndexes;
  }

  /**
   * Returns a comparator for the column matching the specified name
   *
   * @param columnName The name of the column to sort
   * @param reverse    {@code true} if the column should be sorted in reverse
   */
  private IntComparator rowComparator(String columnName, boolean reverse) {

    Column column = this.column(columnName);
    IntComparator rowComparator = column.rowComparator();

    if (reverse) {
      return ReversingIntComparator.reverse(rowComparator);
    } else {
      return rowComparator;
    }
  }

  public Table selectWhere(Selection selection) {
    Table newTable = this.emptyCopy(selection.size());
    Rows.copyRowsToTable(selection, this, newTable);
    return newTable;
  }

  public BooleanColumn selectIntoColumn(String newColumnName, Selection selection) {
    return BooleanColumn.create(newColumnName, selection, rowCount());
  }

  public Table selectWhere(Filter filter) {
    Selection map = filter.apply(this);
    Table newTable = this.emptyCopy(map.size());
    Rows.copyRowsToTable(map, this, newTable);
    return newTable;
  }

  public BooleanColumn selectIntoColumn(String newColumnName, Filter filter) {
    return BooleanColumn.create(newColumnName, filter.apply(this), rowCount());
  }

  public String printHtml() {
    return HtmlTableWriter.write(this, "");
  }

  public Table structure() {
    Table t = new Table("Structure of " + name());
    IntColumn index = new IntColumn("Index", columnCount());
    CategoryColumn columnName = new CategoryColumn("Column Name", columnCount());
    CategoryColumn columnType = new CategoryColumn("Column Type", columnCount());
    t.addColumn(index);
    t.addColumn(columnName);
    t.addColumn(columnType);
    columnName.addAll(columnNames());
    for (int i = 0; i < columnCount(); i++) {
      Column column = columnList.get(i);
      index.add(i);
      columnType.add(column.type().name());
    }
    return t;
  }

  public Projection select(String... columnName) {
    return new Projection(this, columnName);
  }

  /**
   * Removes the given columns
   */
  @Override
  public void removeColumns(Column... columns) {
    for (Column c : columns)
      columnList.remove(c);
  }

  /**
   * Removes the given columns
   */
  public void retainColumns(Column... columns) {
    List<Column> retained = Arrays.asList(columns);
    columnList.retainAll(retained);
  }

  public void retainColumns(String... columnNames) {
    columnList.retainAll(columns(columnNames));
  }

  public Table countBy(String byColumnName) {
    ViewGroup group = ViewGroup.create(this, byColumnName);
    Table resultTable = new Table(name + " summary");
    CategoryColumn groupColumn = CategoryColumn.create("Group", group.size());
    IntColumn countColumn = IntColumn.create("Count", group.size());
    resultTable.addColumn(groupColumn);
    resultTable.addColumn(countColumn);

    for (TemporaryView subTable : group.getSubTables()) {
      int count = subTable.rowCount();
      String groupName = subTable.name();
      groupColumn.add(groupName);
      countColumn.add(count);
    }
    return resultTable;
  }

  private SubTable splitGroupingColumn(SubTable subTable, List<Column> columnNames) {
    ArrayList newColumns = new ArrayList();
    Iterator row = columnNames.iterator();

    Column c;
    while (row.hasNext()) {
      c = (Column) row.next();
      Column col = c.emptyCopy();
      newColumns.add(col);
    }

    for (int var7 = 0; var7 < subTable.rowCount(); ++var7) {
      String[] var8 = subTable.name().split("|||");

      for (int var9 = 0; var9 < newColumns.size(); ++var9) {
        ((Column) newColumns.get(var9)).addCell(var8[var9]);
      }
    }

    row = newColumns.iterator();

    while (row.hasNext()) {
      c = (Column) row.next();
      subTable.addColumn(c);
    }

    return subTable;
  }

  public Table sum(IntColumn sumColumn, Column byColumn) {
    ViewGroup groupTable = new ViewGroup(this, byColumn);
    Table resultTable = new Table(name + " Summary");

    CategoryColumn groupColumn = CategoryColumn.create("Group", groupTable.size());
    IntColumn sumColumn1 = IntColumn.create("Sum", groupTable.size());

    resultTable.addColumn(groupColumn);
    resultTable.addColumn(sumColumn1);

    for (TemporaryView subTable : groupTable.getSubTables()) {
      long sum = subTable.intColumn(sumColumn.name()).sum();
      String groupName = subTable.name();
      groupColumn.add(groupName);
      sumColumn1.add((int) sum);
    }
    return resultTable;
  }

  public Table sum(IntColumn sumColumn, String... byColumnNames) {
    ViewGroup groupTable = ViewGroup.create(this, byColumnNames);
    Table resultTable = new Table(name + " summary");

    CategoryColumn groupColumn = CategoryColumn.create("Group", groupTable.size());
    IntColumn sumColumn1 = IntColumn.create("Sum", groupTable.size());

    resultTable.addColumn(groupColumn);
    resultTable.addColumn(sumColumn1);

    for (TemporaryView subTable : groupTable.getSubTables()) {
      long sum = subTable.intColumn(sumColumn.name()).sum();
      String groupName = subTable.name();
      groupColumn.add(groupName);
      sumColumn1.add((int) sum);
    }
    return resultTable;
  }

  public Table sum(FloatColumn sumColumn, String... byColumnNames) {
    ViewGroup groupTable = ViewGroup.create(this, byColumnNames);
    Table resultTable = new Table(name + " summary");

    CategoryColumn groupColumn = CategoryColumn.create("Group", groupTable.size());
    IntColumn sumColumn1 = IntColumn.create("Sum", groupTable.size());

    resultTable.addColumn(groupColumn);
    resultTable.addColumn(sumColumn1);

    for (TemporaryView subTable : groupTable.getSubTables()) {
      double sum = subTable.floatColumn(sumColumn.name()).sum();
      String groupName = subTable.name();
      groupColumn.add(groupName);
      sumColumn1.add((int) sum);
    }
    return resultTable;
  }

/*
  public Average average(String summarizedColumnName) {
    return new Average(this, summarizedColumnName);
  }
*/

  public void append(Table tableToAppend) {
    for (Column column : columnList) {
      Column columnToAppend = tableToAppend.column(column.name());
      column.append(columnToAppend);
    }
  }

  /**
   * Exports this table as a CSV file with the name (and path) of the given file
   *
   * @param fileNameWithPath The name of the file to save to. By default, it writes to the working directory,
   *                         but you can specify a different folder by providing the path (e.g. mydata/myfile.csv)
   */
  public void exportToCsv(String fileNameWithPath) {
    try {
      CsvWriter.write(fileNameWithPath, this);
    } catch (IOException e) {
      System.err.println("Unable to export table as CSV file");
      e.printStackTrace();
    }
  }

  public String save(String folder) {
    String storageFolder = "";
    try {
      storageFolder = StorageManager.saveTable(folder, this);
    } catch (IOException e) {
      System.err.println("Unable to save table in Tablesaw format");
      e.printStackTrace();
    }
    return storageFolder;
  }

  public static Table readTable(String tableNameAndPath) {
    Table t;
    try {
      t = StorageManager.readTable(tableNameAndPath);
    } catch (IOException e) {
      System.err.println("Unable to load table from Tablesaw table format");
      e.printStackTrace();
      return null;
    }
    return t;
  }

  /**
   * Returns the result of applying the given function to the specified column
   *
   * @param numericColumnName The name of a numeric (integer, float, etc.) column in this table
   * @param function          A numeric reduce function
   * @return the function result
   * @throws IllegalArgumentException if numericColumnName doesn't name a numeric column in this table
   */
  public double reduce(String numericColumnName, NumericReduceFunction function) {
    Column column = column(numericColumnName);

    return function.reduce(column.toDoubleArray());
  }

  public Table reduce(String numericColumnName, NumericReduceFunction function, String... groupColumnName) {
    ViewGroup tableGroup = ViewGroup.create(this, groupColumnName);
    return tableGroup.reduce(numericColumnName, function);
  }

  /**
   * Returns a new table constructed from a character delimited (aka CSV) text file
   * <p>
   * It is assumed that the file is truly comma-separated, and that the file has a one-line header,
   * which is used to populate the column names
   *
   * @param csvFileName The name of the file to import
   * @throws IOException
   */
  public static Table createFromCsv(String csvFileName) throws IOException {
    return CsvReader.read(csvFileName, true, ',');
  }

  /**
   * Returns a new table constructed from a character delimited (aka CSV) text file
   * <p>
   * It is assumed that the file is truly comma-separated, and that the file has a one-line header,
   * which is used to populate the column names
   *
   * @param csvFileName The name of the file to import
   * @param header      True if the file has a single header row. False if it has no header row.
   *                    Multi-line headers are not supported
   * @throws IOException
   */
  public static Table createFromCsv(String csvFileName, boolean header) throws IOException {
    return CsvReader.read(csvFileName, header, ',');
  }

  /**
   * Returns a new table constructed from a character delimited (aka CSV) text file
   * <p>
   * It is assumed that the file is truly comma-separated, and that the file has a one-line header,
   * which is used to populate the column names
   *
   * @param csvFileName The name of the file to import
   * @param header      True if the file has a single header row. False if it has no header row.
   *                    Multi-line headers are not supported
   * @param delimiter   a char that divides the columns in the source file, often a comma or tab
   * @throws IOException
   */
  public static Table createFromCsv(String csvFileName, boolean header, char delimiter) throws IOException {
    return CsvReader.read(csvFileName, header, delimiter);
  }

  /**
   * Returns a new table constructed from a character delimited (aka CSV) text file
   * <p>
   * It is assumed that the file is truly comma-separated, and that the file has a one-line header,
   * which is used to populate the column names
   *
   * @param types       The column types
   * @param csvFileName The name of the file to import
   * @throws IOException
   */
  public static Table createFromCsv(ColumnType[] types, String csvFileName) throws IOException {
    return CsvReader.read(types, true, ',', csvFileName);
  }

  /**
   * Returns a new table constructed from a character delimited (aka CSV) text file
   * <p>
   * It is assumed that the file is truly comma-separated
   *
   * @param types       The column types
   * @param header      True if the file has a single header row. False if it has no header row.
   *                    Multi-line headers are not supported
   * @param csvFileName the name of the file to import
   * @throws IOException
   */
  public static Table createFromCsv(ColumnType[] types, String csvFileName, boolean header) throws IOException {
    return CsvReader.read(types, header, ',', csvFileName);
  }

  /**
   * Returns a new table constructed from a character delimited (aka CSV) text file
   *
   * @param types       The column types
   * @param header      true if the file has a single header row. False if it has no header row.
   *                    Multi-line headers are not supported
   * @param delimiter   a char that divides the columns in the source file, often a comma or tab
   * @param csvFileName the name of the file to import
   * @throws IOException
   */
  public static Table createFromCsv(ColumnType[] types, String csvFileName, boolean header, char delimiter)
      throws IOException {
    return CsvReader.read(types, header, delimiter, csvFileName);
  }

  /**
   * Returns a new table constructed from a character delimited (aka CSV) text file
   *
   * @param types       The column types
   * @param header      true if the file has a single header row. False if it has no header row.
   *                    Multi-line headers are not supported
   * @param delimiter   a char that divides the columns in the source file, often a comma or tab
   * @param stream      an InputStream from a file, URL, etc.
   * @param tableName   the name of the resulting table
   * @throws IOException
   */
  public static Table createFromStream(ColumnType[] types,
                                       boolean header,
                                       char delimiter,
                                       InputStream stream,
                                       String tableName)
      throws IOException {
    return CsvReader.read(tableName, types, header, delimiter, stream);
  }

  /**
   * Returns a new Relation with the given name, and containing the data in the given result set
   */
  public static Table create(ResultSet resultSet, String tableName) throws SQLException {
    return SqlResultSetReader.read(resultSet, tableName);
  }


  @Override
  public String toString() {
    return "Table " + name + ": Size = " + rowCount() + " x " + columnCount();
  }

  @Override
  public IntIterator iterator() {

    return new IntIterator() {

      private int i = 0;

      @Override
      public int nextInt() {
        return i++;
      }

      @Override
      public int skip(int k) {
        return i + k;
      }

      @Override
      public boolean hasNext() {
        return i < rowCount();
      }

      @Override
      public Integer next() {
        return i++;
      }
    };
  }
}
