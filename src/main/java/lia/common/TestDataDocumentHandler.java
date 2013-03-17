package lia.common;

import org.apache.lucene.ant.DocumentHandlerException;
import org.apache.lucene.ant.ConfigurableDocumentHandler;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestDataDocumentHandler implements ConfigurableDocumentHandler {
  private String basedir;

  public Document getDocument(File file) throws DocumentHandlerException {
    Properties props = new Properties();
    try {
      props.load(new FileInputStream(file));
    } catch (IOException e) {
      throw new DocumentHandlerException(e);
    }

    Document doc = new Document();

    // category comes from relative path below the base directory
    String category = file.getParent().substring(basedir.length());
    category = category.replace(File.separatorChar, '/');

    String isbn = props.getProperty("isbn");
    String title = props.getProperty("title");
    String author = props.getProperty("author");
    String url = props.getProperty("url");
    String subject = props.getProperty("subject");
    String pubmonth = props.getProperty("pubmonth");

    System.out.println(title + "\n" + author + "\n" + subject + "\n" + category + "\n---------");

    doc.add(Field.Keyword("isbn", isbn));
    doc.add(Field.Keyword("category", category));
    doc.add(Field.Text("title", title));

    // split multiple authors into unique field instances
    String[] authors = author.split(",");
    for (int i = 0; i < authors.length; i++) {
      doc.add(Field.Keyword("author", authors[i]));
    }

    doc.add(Field.UnIndexed("url", url));
    doc.add(Field.UnStored("subject", subject, true));

    doc.add(Field.Keyword("pubmonth", pubmonth));

    doc.add(Field.UnStored("contents",
        aggregate(new String[] { title, subject, author})));

    return doc;
  }

  private String aggregate(String[] strings) {
    StringBuffer buffer = new StringBuffer();

    for (int i = 0; i < strings.length; i++) {
      buffer.append(strings[i]);
      buffer.append(" ");
    }

    return buffer.toString();
  }

  public void configure(Properties props) {
    this.basedir = props.getProperty("basedir");
  }
}
