package tech.tablesaw.io.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.json.flattener.JsonFlattener;

import tech.tablesaw.api.Table;
import tech.tablesaw.io.ReadOptions;
import tech.tablesaw.io.TableBuildingUtils;

public class JsonReader {

    private static final ObjectMapper mapper = new ObjectMapper();

    public Table read(Reader json, String tableName) throws IOException {
        ReadOptions options = ReadOptions.builder(new StringReader(""), tableName).build(); // TODO: this should be passed in
        JsonNode jsonObj = mapper.readTree(json);
        if (!jsonObj.isArray()) {
            throw new IllegalStateException(
                    "Only reading a json array or arrays or objects is currently supported");
        }
        if (jsonObj.size() == 0) {
            return Table.create(tableName);
        }
        // array of arrays
        JsonNode firstNode = jsonObj.get(0);
        if (firstNode.isArray()) {
            boolean firstRowAllStrings = true;
            List<String> columnNames = new ArrayList<>();
            for (JsonNode n : firstNode) {
        	if (!n.isTextual()) {
        	    firstRowAllStrings = false;
        	}
            }
            boolean hasHeader = firstRowAllStrings;
            for (int i = 0; i < firstNode.size(); i++) {
        	columnNames.add(hasHeader ? firstNode.get(i).textValue() : "Column " + i);
            }
            List<String[]> dataRows = new ArrayList<>();
            for (int i = hasHeader ? 1 : 0; i < jsonObj.size(); i++) {
        	JsonNode arr = jsonObj.get(i);
                String[] row = new String[arr.size()];
                for (int j = 0; j < arr.size(); j++) {
                    row[j] = arr.get(j).asText();
                }
                dataRows.add(row);
            }
            return TableBuildingUtils.build(tableName, columnNames, dataRows, options);
        }
        // array of objects
        // flatten each object inside the array
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < jsonObj.size(); i++) {
            JsonNode rowObj = jsonObj.get(i);
            String flattenedRow = JsonFlattener.flatten(mapper.writeValueAsString(rowObj));
            if (i != 0) {
                result.append(",");
            }
            result.append(flattenedRow);
        }
        String flattenedJsonString = result.append("]").toString();
        JsonNode flattenedJsonObj = mapper.readTree(flattenedJsonString);

        Set<String> colNames = new HashSet<>();
        for (JsonNode row : flattenedJsonObj) {
            Iterator<String> fieldNames = row.fieldNames();
            while (fieldNames.hasNext()) {
        	colNames.add(fieldNames.next());
            }
        }

        List<String> columnNames = new ArrayList<>(colNames);
        List<String[]> dataRows = new ArrayList<>();
        for (JsonNode node : flattenedJsonObj) {
            String[] arr = new String[columnNames.size()];
            for (int i = 0; i < columnNames.size(); i++) {
        	arr[i] = node.get(columnNames.get(i)).asText();
            }
            dataRows.add(arr);
        }

        return TableBuildingUtils.build(tableName, columnNames, dataRows, options);
    }

}
