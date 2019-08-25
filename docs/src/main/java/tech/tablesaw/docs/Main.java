package tech.tablesaw.docs;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/** Main class that will run all the docs. All Docs classes have to be registered in main. */
public class Main {

  /** */
  public static void main(String[] args) throws IOException {
    List<DocsSourceFile> docsClasses =
        Arrays.asList(
            // Register new docs classes here.
            new Tutorial());

    for (DocsSourceFile docsClass : docsClasses) {
      docsClass.run();
    }
  }
}
