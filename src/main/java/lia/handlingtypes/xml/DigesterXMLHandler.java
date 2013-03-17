package lia.handlingtypes.xml;

import lia.handlingtypes.framework.DocumentHandler;
import lia.handlingtypes.framework.DocumentHandlerException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

public class DigesterXMLHandler implements DocumentHandler {

  private Digester dig;
  private static Document doc;

  public DigesterXMLHandler() {

    // instantiate Digester and disable XML validation
    dig = new Digester();
    dig.setValidating(false);

    // instantiate DigesterXMLHandler class
    dig.addObjectCreate("address-book", DigesterXMLHandler.class);
    // instantiate Contact class
    dig.addObjectCreate("address-book/contact", Contact.class);

    // set type property of Contact instance when 'type'
    // attribute is found
    dig.addSetProperties("address-book/contact", "type", "type");

    // set different properties of Contact instance using
    // specified methods
    dig.addCallMethod("address-book/contact/name",
      "setName", 0);
    dig.addCallMethod("address-book/contact/address",
      "setAddress", 0);
    dig.addCallMethod("address-book/contact/city",
      "setCity", 0);
    dig.addCallMethod("address-book/contact/province",
      "setProvince", 0);
    dig.addCallMethod("address-book/contact/postalcode",
      "setPostalcode", 0);
    dig.addCallMethod("address-book/contact/country",
      "setCountry", 0);
    dig.addCallMethod("address-book/contact/telephone",
      "setTelephone", 0);

    // call 'populateDocument' method when the next
    // 'address-book/contact' pattern is seen
    dig.addSetNext("address-book/contact", "populateDocument");
  }

  public synchronized Document getDocument(InputStream is)
    throws DocumentHandlerException {

    try {
      dig.parse(is);
    }
    catch (IOException e) {
      throw new DocumentHandlerException(
        "Cannot parse XML document", e);
    }
    catch (SAXException e) {
      throw new DocumentHandlerException(
        "Cannot parse XML document", e);
    }

    return doc;
  }

  public void populateDocument(Contact contact) {

    // create a blank Lucene Document
    doc = new Document();

    doc.add(Field.Keyword("type", contact.getType()));
    doc.add(Field.Keyword("name", contact.getName()));
    doc.add(Field.Keyword("address", contact.getAddress()));
    doc.add(Field.Keyword("city", contact.getCity()));
    doc.add(Field.Keyword("province", contact.getProvince()));
    doc.add(Field.Keyword("postalcode", contact.getPostalcode()));
    doc.add(Field.Keyword("country", contact.getCountry()));
    doc.add(Field.Keyword("telephone", contact.getTelephone()));
  }

  /**
   * JavaBean class that holds properties of each Contact
   * entry.   It is important that this class be public and
   * static, in order for Digester to be able to instantiate
   * it.
   */
  public static class Contact {
    private String type;
    private String name;
    private String address;
    private String city;
    private String province;
    private String postalcode;
    private String country;
    private String telephone;

    public void setType(String newType) {
      type = newType;
    }
    public String getType() {
      return type;
    }

    public void setName(String newName) {
      name = newName;
    }
    public String getName() {
      return name;
    }

    public void setAddress(String newAddress) {
      address = newAddress;
    }
    public String getAddress() {
      return address;
    }

    public void setCity(String newCity) {
      city = newCity;
    }
    public String getCity() {
      return city;
    }

    public void setProvince(String newProvince) {
      province = newProvince;
    }
    public String getProvince() {
      return province;
    }

    public void setPostalcode(String newPostalcode) {
      postalcode = newPostalcode;
    }
    public String getPostalcode() {
      return postalcode;
    }

    public void setCountry(String newCountry) {
      country = newCountry;
    }
    public String getCountry() {
      return country;
    }

    public void setTelephone(String newTelephone) {
      telephone = newTelephone;
    }
    public String getTelephone() {
      return telephone;
    }
  }

  public static void main(String[] args) throws Exception {
    DigesterXMLHandler handler = new DigesterXMLHandler();
    Document doc =
      handler.getDocument(new FileInputStream(new File(args[0])));
    System.out.println(doc);
  }
}
