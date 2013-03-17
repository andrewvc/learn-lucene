package lia.extsearch.perf;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmartDayQueryParser extends QueryParser {
  public static final DateFormat formatter =
      new SimpleDateFormat("yyyyMMdd");

  public SmartDayQueryParser(String field, Analyzer analyzer) {
    super(field, analyzer);
  }

  protected Query getRangeQuery(String field, Analyzer analyzer,
                                String part1, String part2,
                                boolean inclusive)
                                           throws ParseException {
    try {
      DateFormat df =
          DateFormat.getDateInstance(DateFormat.SHORT,
              getLocale());
      df.setLenient(true);
      Date d1 = df.parse(part1);
      Date d2 = df.parse(part2);
      part1 = formatter.format(d1);
      part2 = formatter.format(d2);
    } catch (Exception ignored) {
    }

    return new RangeQuery(
                new Term(field, part1),
                new Term(field, part2),
                inclusive);
  }
}
