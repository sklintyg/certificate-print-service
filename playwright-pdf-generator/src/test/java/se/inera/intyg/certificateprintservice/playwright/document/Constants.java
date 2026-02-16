package se.inera.intyg.certificateprintservice.playwright.document;

import java.util.Objects;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public class Constants {

  public static final String TEXT = "Text";
  public static final String HEADER = "header";
  public static final String CONTENT = "content";
  public static final String TAG_TYPE = "Tag type";
  public static final String ATTRIBUTES = "Attributes";
  public static final String NUM_CHILDREN = "Number of children";
  public static final String NUM_ATTRIBUTES = "Number of Attributes";

  public static final String ALT = "alt";
  public static final String SRC = "src";
  public static final String HREF = "href";
  public static final String CLASS = "class";
  public static final String STYLE = "style";
  public static final String TITLE = "title";
  public static final String SCRIPT = "script";


  public static final Tag A = Tag.valueOf("a");
  public static final Tag P = Tag.valueOf("p");
  public static final Tag BR = Tag.valueOf("br");
  public static final Tag H1 = Tag.valueOf("h1");
  public static final Tag H2 = Tag.valueOf("h2");
  public static final Tag H3 = Tag.valueOf("h3");
  public static final Tag DIV = Tag.valueOf("div");
  public static final Tag IMG = Tag.valueOf("img");
  public static final Tag SPAN = Tag.valueOf("span");
  public static final Tag STRONG = Tag.valueOf("strong");

  public static String attributes(Element element, String attribute) {
    return Objects.requireNonNull(element.attribute(attribute)).getValue().replaceAll("\n", " ");
  }

  public static int attributesSize(Element element) {
    return element.attributes().asList().size();
  }

}
