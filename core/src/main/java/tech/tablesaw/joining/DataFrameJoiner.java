package tech.tablesaw.joining;

import com.google.common.collect.Streams;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.dates.DateColumnType;
import tech.tablesaw.columns.datetimes.DateTimeColumnType;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.columns.numbers.LongColumnType;
import tech.tablesaw.columns.numbers.ShortColumnType;
import tech.tablesaw.columns.strings.StringColumnType;
import tech.tablesaw.columns.times.TimeColumnType;
import tech.tablesaw.index.IntIndex;
import tech.tablesaw.index.LongIndex;
import tech.tablesaw.index.ShortIndex;
import tech.tablesaw.index.StringIndex;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DataFrameJoiner {

    private static final String TABLE_ALIAS = "T";

    private final Table table;
    private final CategoricalColumn<?> column;
    private CategoricalColumn<?>[] columns;
    private String[] columnNames;
    private AtomicInteger joinTableId = new AtomicInteger(2);

/*    public DataFrameJoiner(Table table, String column) {
        this.table = table;
        this.column = table.categoricalColumn(column);
        columns = new CategoricalColumn<?>[] {this.column};
        columnNames = new String[] {this.column.name()};
    }
*/
    /**
     * Constructor.  Takes an initial value to start the joinTableId.  Useful during
     * multiple table joins so that each new table joined can have its duplicate columns
     * distinguished from the other tables.
     * @param table					The table to join on
     * @param columnName			The column name to join on
     * @param joinTableInitialId	The initial index used when naming duplicate columns, e.g. "T3.Age"
     */
/*    public DataFrameJoiner(Table table, String columnName, int joinTableInitialId) {
    	this(table, columnName);
    	joinTableId.set(joinTableInitialId);
    }
*/
    public DataFrameJoiner(Table table, String... columnNames) {
        this.table = table;
        column = table.categoricalColumn(columnNames[0]);
        columns = new CategoricalColumn<?>[columnNames.length];
        this.columnNames = columnNames;
        for(int i=0; i<this.columnNames.length; i++) {
        	String colName = this.columnNames[i];	
            this.columns[i] = table.categoricalColumn(colName);
        }
	}

    public DataFrameJoiner(Table table, int joinTableIdVal, String... columnNames) {
    	this(table, columnNames);
    	joinTableId.set(joinTableIdVal);
    }
    
    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param tables The tables to join with
     */
    public Table inner(Table... tables) {
        return inner(false, tables);
    }
    
    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed*
     * @param tables The tables to join with
     */
   	public Table inner(boolean allowDuplicateColumnNames, Table... tables) {
    	Table joined = table;
    	
    	for (int i=0; i<tables.length; i++) {
    		Table currT = tables[i];
    		// if first iteration then join to initial table
    		if(joined.equals(table)) {
    			joined = inner(currT, allowDuplicateColumnNames, columnNames);
    		} // else join to result of last join
    		else {
    			joined = joined.join(i+2, columnNames)
    					.inner(currT, allowDuplicateColumnNames, columnNames);
    		}
    	}
    	return joined;
    }    
    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     */
    public Table inner(Table table2, String[] col2Names) {
        return inner(table2, false, col2Names);
    }
    public Table inner(Table table2, String col2Name) {
        return inner(table2, false, col2Name);
    }
    
    public Table inner(Table table2, String col2Name, boolean allowDuplicateColumnNames) {
    	return inner(table2, allowDuplicateColumnNames, col2Name);
    }
    
    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed*
     */
    public Table inner(Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
        return inner(table2, false, allowDuplicateColumnNames, col2Names);
    }
    public Table inner(Table table2, boolean outer, boolean allowDuplicateColumnNames, String... col2Names) {
    	Table joinedTable;
    	if(col2Names.length > 1) {
    		joinedTable = joinInternalMultiple(table2, outer, allowDuplicateColumnNames, col2Names);
    	} else {
    		joinedTable = joinInternal(table2, col2Names[0], outer, allowDuplicateColumnNames);
    	}
    	return joinedTable;
    }

    private Table joinInternal(Table table2, String col2Name, boolean outer, boolean allowDuplicates) {
        if (allowDuplicates) {
            renameColumnsWithDuplicateNames(table2, col2Name);
        }

        Table result = emptyTableFromColumns(table, table2, col2Name);
        ColumnType type = column.type();
        if (type instanceof DateColumnType) {
            IntIndex index = new IntIndex(table2.dateColumn(col2Name));
            DateColumn col1 = (DateColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                int value = col1.getIntInternal(i);
                Table table1Rows = table.where(Selection.with(i));
                Table table2Rows = table2.where(index.get(value));
                table2Rows.removeColumns(col2Name);
                if (outer && table2Rows.isEmpty()) {
                    withMissingLeftJoin(result, table1Rows);
                } else {
                    crossProduct(result, table1Rows, table2Rows);
                }
            }
        } else if (type instanceof DateTimeColumnType) {
            LongIndex index = new LongIndex(table2.dateTimeColumn(col2Name));
            DateTimeColumn col1 = (DateTimeColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                long value = col1.getLongInternal(i);
                Table table1Rows = table.where(Selection.with(i));
                Table table2Rows = table2.where(index.get(value));
                table2Rows.removeColumns(col2Name);
                if (outer && table2Rows.isEmpty()) {
                    withMissingLeftJoin(result, table1Rows);
                } else {
                    crossProduct(result, table1Rows, table2Rows);
                }
            }
        } else if (type instanceof TimeColumnType) {
            IntIndex index = new IntIndex(table2.timeColumn(col2Name));
            TimeColumn col1 = (TimeColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                int value = col1.getIntInternal(i);
                Table table1Rows = table.where(Selection.with(i));
                Table table2Rows = table2.where(index.get(value));
                table2Rows.removeColumns(col2Name);
                if (outer && table2Rows.isEmpty()) {
                    withMissingLeftJoin(result, table1Rows);
                } else {
                    crossProduct(result, table1Rows, table2Rows);
                }
            }
        } else if (type instanceof StringColumnType) {
            StringIndex index = new StringIndex(table2.stringColumn(col2Name));
            StringColumn col1 = (StringColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                String value = col1.get(i);
                Table table1Rows = table.where(Selection.with(i));
                Table table2Rows = table2.where(index.get(value));
                table2Rows.removeColumns(col2Name);
                if (outer && table2Rows.isEmpty()) {
                    withMissingLeftJoin(result, table1Rows);
                } else {
                    crossProduct(result, table1Rows, table2Rows);
                }
            }
        } else if (type instanceof IntColumnType) {
            IntIndex index = new IntIndex(table2.intColumn(col2Name));
            IntColumn col1 = (IntColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                int value = col1.getInt(i);
                Table table1Rows = table.where(Selection.with(i));
                Table table2Rows = table2.where(index.get(value));
                table2Rows.removeColumns(col2Name);
                if (outer && table2Rows.isEmpty()) {
                    withMissingLeftJoin(result, table1Rows);
                } else {
                    crossProduct(result, table1Rows, table2Rows);
                }
            }
        } else if (type instanceof LongColumnType) {
            LongIndex index = new LongIndex(table2.intColumn(col2Name));
            LongColumn col1 = (LongColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                long value = col1.getLong(i);
                Table table1Rows = table.where(Selection.with(i));
                Table table2Rows = table2.where(index.get(value));
                table2Rows.removeColumns(col2Name);
                if (outer && table2Rows.isEmpty()) {
                    withMissingLeftJoin(result, table1Rows);
                } else {
                    crossProduct(result, table1Rows, table2Rows);
                }
            }
        } else if (type instanceof ShortColumnType) {
            ShortIndex index = new ShortIndex(table2.shortColumn(col2Name));
            ShortColumn col1 = (ShortColumn) column;
            for (int i = 0; i < col1.size(); i++) {
                short value = col1.getShort(i);
                Table table1Rows = table.where(Selection.with(i));
                Table table2Rows = table2.where(index.get(value));
                table2Rows.removeColumns(col2Name);
                if (outer && table2Rows.isEmpty()) {
                    withMissingLeftJoin(result, table1Rows);
                } else {
                    crossProduct(result, table1Rows, table2Rows);
                }
            }
        } else {
            throw new IllegalArgumentException(
                    "Joining is supported on integer, string, and date-like columns. Column "
                            + column.name() + " is of type " + column.type());
        }
        return result;
    }
    
    /**
     * Joins the joiner to the {@code table2}, using the given columns for the second table and returns the resulting table
     *
     * @param table2    The table to join with
     * @param col2Names The columns to join on. If a col2Name refers to a double column, the join is performed after
     *                  rounding to integers.
     * @param outer     True if this join is actually an outer join, left or right or full, otherwise false.
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed*
     */
    private Table joinInternalMultiple(Table table2, boolean outer, boolean allowDuplicates, String... col2Names) {
        if (allowDuplicates) {
            renameColumnsWithDuplicateNames(table2, col2Names);
        }
        Table result = emptyTableFromColumns(table, table2, col2Names);
        for (Row row : table) {
        	int ri = row.getRowNumber();
            Table table1Rows = table.where(Selection.with(ri));
        	Selection rowBitMapMultiCol = null;
           	for(int i=0; i<columns.length; i++) {
           		CategoricalColumn<?> column = columns[i];
                ColumnType type = column.type();
           		// relies on both arrays, columns, and col2Names,
           		// having corresponding values at same index
            	String col2Name = col2Names[i];
            	Selection rowBitMapOneCol = null;
            	if (type instanceof DateColumnType) {
                    IntIndex index = new IntIndex(table2.dateColumn(col2Name));
                    DateColumn col1 = (DateColumn) column;
                    int value = col1.getIntInternal(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof DateTimeColumnType) {
                    LongIndex index = new LongIndex(table2.dateTimeColumn(col2Name));
                    DateTimeColumn col1 = (DateTimeColumn) column;
                    long value = col1.getLongInternal(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof TimeColumnType) {
                    IntIndex index = new IntIndex(table2.timeColumn(col2Name));
                    TimeColumn col1 = (TimeColumn) column;
                    int value = col1.getIntInternal(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof StringColumnType) {
                    StringIndex index = new StringIndex(table2.stringColumn(col2Name));
                    StringColumn col1 = (StringColumn) column;
                    String value = col1.get(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof IntColumnType) {
                    IntIndex index = new IntIndex(table2.intColumn(col2Name));
                    IntColumn col1 = (IntColumn) column;
                    int value = col1.getInt(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof LongColumnType) {
                    LongIndex index = new LongIndex(table2.intColumn(col2Name));
                    LongColumn col1 = (LongColumn) column;
                    long value = col1.getLong(ri);
                    rowBitMapOneCol = index.get(value);
                } else if (type instanceof ShortColumnType) {
                    ShortIndex index = new ShortIndex(table2.shortColumn(col2Name));
                    ShortColumn col1 = (ShortColumn) column;
                    short value = col1.getShort(ri);
                    rowBitMapOneCol = index.get(value);
                } else {
                    throw new IllegalArgumentException(
                            "Joining is supported on numeric, string, and date-like columns. Column "
                                    + column.name() + " is of type " + column.type());
                }
            	// combine Selection's into one big AND Selection
            	if(rowBitMapOneCol != null) {
            		rowBitMapMultiCol = rowBitMapMultiCol != null?rowBitMapMultiCol.and(rowBitMapOneCol):rowBitMapOneCol;
            	}
            }            
            Table table2Rows = table2.where(rowBitMapMultiCol);
            table2Rows.removeColumns(col2Names);
            if (outer && table2Rows.isEmpty()) {
                withMissingLeftJoin(result, table1Rows);
            } else {
                crossProduct(result, table1Rows, table2Rows);
            }
            
        }
        return result;
    }

    private void renameColumnsWithDuplicateNames(Table table2, String... col2Names) {
        String table2Alias = TABLE_ALIAS + joinTableId.getAndIncrement();
        List<String> list = Arrays.asList(col2Names);
        for (Column<?> table2Column : table2.columns()) {
            String columnName = table2Column.name();
            if (table.columnNames().contains(columnName)
                    && !(list.contains(columnName))) {
                table2Column.setName(newName(table2Alias, columnName));
            }
        }
    }

    private String newName(String table2Alias, String columnName) {
        return table2Alias + "." + columnName;
    }

    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param tables The tables to join with
     */
    public Table fullOuter(Table... tables) {
        return fullOuter(false, tables);
    }

    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed*
     * @param tables The tables to join with
     */
    public Table fullOuter(boolean allowDuplicateColumnNames, Table... tables) {
        Table joined = table;
        for (Table table2 : tables) {
            joined = fullOuter(table2, column.name(), allowDuplicateColumnNames);
        }
        return joined;
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     */
    public Table fullOuter(Table table2, String col2Name) {
        return fullOuter(table2, col2Name, false);
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed
     */
    public Table fullOuter(Table table2, String col2Name, boolean allowDuplicateColumnNames) {
        Table result = joinInternal(table2, col2Name, true, allowDuplicateColumnNames);

        Selection selection = new BitmapBackedSelection();
        ColumnType type = column.type();

        if (type instanceof DateColumnType) {
            IntIndex index = new IntIndex(result.dateColumn(col2Name));
            DateColumn col2 = (DateColumn) table2.column(col2Name);
            for (int i = 0; i < col2.size(); i++) {
                int value = col2.getIntInternal(i);
                if (index.get(value).isEmpty()) {
                    selection.add(i);
                }
            }
        } else if (type instanceof DateTimeColumnType) {
            LongIndex index = new LongIndex(result.dateTimeColumn(col2Name));
            DateTimeColumn col2 = (DateTimeColumn) table2.column(col2Name);
            for (int i = 0; i < col2.size(); i++) {
                long value = col2.getLongInternal(i);
                if (index.get(value).isEmpty()) {
                    selection.add(i);
                }
            }
        } else if (type instanceof TimeColumnType) {
            IntIndex index = new IntIndex(result.timeColumn(col2Name));
            TimeColumn col2 = (TimeColumn) table2.column(col2Name);
            for (int i = 0; i < col2.size(); i++) {
                int value = col2.getIntInternal(i);
                if (index.get(value).isEmpty()) {
                    selection.add(i);
                }
            }
        } else if (type instanceof StringColumnType) {
            StringIndex index = new StringIndex(result.stringColumn(col2Name));
            StringColumn col2 = (StringColumn) table2.column(col2Name);
            for (int i = 0; i < col2.size(); i++) {
                String value = col2.get(i);
                if (index.get(value).isEmpty()) {
                    selection.add(i);
                }
            }
        } else if (type instanceof IntColumnType) {
            IntIndex index = new IntIndex(result.intColumn(col2Name));
            IntColumn col2 = (IntColumn) table2.column(col2Name);
            for (int i = 0; i < col2.size(); i++) {
                int value = col2.getInt(i);
                if (index.get(value).isEmpty()) {
                    selection.add(i);
                }
            }
        } else if (type instanceof LongColumnType) {
            LongIndex index = new LongIndex(result.intColumn(col2Name));
            LongColumn col2 = (LongColumn) table2.column(col2Name);
            for (int i = 0; i < col2.size(); i++) {
                long value = col2.getLong(i);
                if (index.get(value).isEmpty()) {
                    selection.add(i);
                }
            }
        } else if (type instanceof ShortColumnType) {
            ShortIndex index = new ShortIndex(result.shortColumn(col2Name));
            ShortColumn col2 = (ShortColumn) table2.column(col2Name);
            for (int i = 0; i < col2.size(); i++) {
                short value = col2.getShort(i);
                if (index.get(value).isEmpty()) {
                    selection.add(i);
                }
            }
        } else {
            throw new IllegalArgumentException(
                    "Joining is supported on numeric, string, and date-like columns. Column "
                            + column.name() + " is of type " + column.type());
        }
        
        Table table2OnlyRows = table2.where(selection);
        CategoricalColumn<?> joinColumn = table2OnlyRows.categoricalColumn(col2Name);
        table2OnlyRows.removeColumns(joinColumn);
        withMissingRightJoin(result, joinColumn, table2OnlyRows);        
        return result;
    }

    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param tables The tables to join with
     */
    public Table leftOuter(Table... tables) {
        return leftOuter(false, tables);
    }

    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed*
     * @param tables The tables to join with
     */
    public Table leftOuter(boolean allowDuplicateColumnNames, Table... tables) {
        Table joined = table;
        for (Table table2 : tables) {
          joined = leftOuter(table2, allowDuplicateColumnNames, columnNames);
        }
        return joined;
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     */
    public Table leftOuter(Table table2, String... col2Names) {
        return leftOuter(table2, false, col2Names);
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed
     */
    public Table leftOuter(Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
        return joinInternalMultiple(table2, true, allowDuplicateColumnNames, col2Names);
    }

    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param tables The tables to join with
     */
    public Table rightOuter(Table... tables) {
        return rightOuter(false, tables);
    }

    /**
     * Joins to the given tables assuming that they have a column of the name we're joining on
     *
     * @param tables The tables to join with
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed
     */
    public Table rightOuter(boolean allowDuplicateColumnNames, Table... tables) {
        Table joined = table;
        for (Table table2 : tables) {
          joined = rightOuter(table2, allowDuplicateColumnNames, columnNames);
        }
        return joined;
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     */
    public Table rightOuter(Table table2, String col2Name) {
        return rightOuter(table2, false, col2Name);
    }

    /**
     * Joins the joiner to the table2, using the given column for the second table and returns the resulting table
     *
     * @param table2   The table to join with
     * @param col2Name The column to join on. If col2Name refers to a double column, the join is performed after
     *                 rounding to integers.
     * @param allowDuplicateColumnNames if {@code false} the join will fail if any columns other than the join column have the same name
     *                                  if {@code true} the join will succeed and duplicate columns are renamed
     */

    public Table rightOuter(Table table2, boolean allowDuplicateColumnNames, String... col2Names) {
        Table leftOuter = table2.join(col2Names).leftOuter(table, allowDuplicateColumnNames, columnNames);

        // reverse the columns
        Table result = Table.create(leftOuter.name());
        // loop on table that was originally first (left) and add the left-joined matching columns by name
        for (String name : table.columnNames()) {
            try {
                result.addColumns(leftOuter.column(name));
            } catch (IllegalStateException e) {
                System.out.println("NOTE: DataFrameJoiner.rightOuter(): skipping left table's column,'"
                        +name+"', in favor of right table's matching column that was kept in join operation.");
            }
        }
        for (String name : table2.columnNames()) {
            if (!result.columnNames().contains(name)) {
                result.addColumns(leftOuter.column(name));
            }
        }
        return result;
    }

    private Table emptyTableFromColumns(Table table1, Table table2, String... col2Names) {
    	Column<?>[] cols = Streams.concat(
    			table1.columns().stream(),
    			table2.columns().stream().filter(c -> !Arrays.asList(col2Names).contains(c.name()))
    			).map(col -> col.emptyCopy(col.size())).toArray(Column[]::new);
    	return Table.create(table1.name(), cols);
    }
    

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void crossProduct(Table destination, Table table1, Table table2) {
        for (int c = 0; c < table1.columnCount() + table2.columnCount(); c++) {
            for (int r1 = 0; r1 < table1.rowCount(); r1++) {
                for (int r2 = 0; r2 < table2.rowCount(); r2++) {
                    if (c < table1.columnCount()) {
                	Column t1Col = table1.column(c);
                        destination.column(c).append(t1Col, r1);
                    } else {
                	Column t2Col = table2.column(c - table1.columnCount());
                        destination.column(c).append(t2Col, r2);
                    }
                }
            }
        }
    }

    /**
     * Adds rows to destination for each row in table1, with the columns from table2 added as missing values in each
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void withMissingLeftJoin(Table destination, Table table1) {
        for (int c = 0; c < destination.columnCount(); c++) {
            if (c < table1.columnCount()) {
        	Column t1Col = table1.column(c);
        	destination.column(c).append(t1Col);
            } else {
                for (int r1 = 0; r1 < table1.rowCount(); r1++) {
                    destination.column(c).appendMissing();
                }
            }
        }
    }

    /**
     * Adds rows to destination for each row in the joinColumn and table2
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void withMissingRightJoin(Table destination, CategoricalColumn joinColumn, Table table2) {
        int t2StartCol = destination.columnCount() - table2.columnCount();
        for (int c = 0; c < destination.columnCount(); c++) {
            if (destination.column(c).name().equalsIgnoreCase(joinColumn.name())) {
        	destination.column(c).append(joinColumn);
                continue;
            }
            if (c < t2StartCol) {
                for (int r2 = 0; r2 < table2.rowCount(); r2++) {
                    destination.column(c).appendMissing();
                }
            } else {
        	Column t2Col = table2.column(c - t2StartCol);
        	destination.column(c).append(t2Col);
            }
        }
    }
}
