/*
 * Copyright (C) 2026 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
