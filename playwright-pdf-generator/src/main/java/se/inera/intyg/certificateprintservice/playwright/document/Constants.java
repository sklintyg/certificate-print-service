package se.inera.intyg.certificateprintservice.playwright.document;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

  public static final String STYLE = "style";
  public static final String SRC = "src";
  public static final String ALT = "alt";
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String HREF = "href";
  public static final String CONTENT = "content";

  public static final String HEADER_STYLE = """
      margin: 10mm 20mm;
      display: grid;
      width: 17cm;
      font-family: 'Liberation Sans', sans-serif;
      font-size: 10pt;""";

  public static final String FOOTER_STYLE = """
      margin: 0 20mm;
      width: 17cm;
      height: 25mm;
      border-top: black solid 1px;
      justify-content: space-between;
      display: flex;
      font-family: 'Liberation Sans', sans-serif;
      font-size: 10pt;""";

  public static final String LEFT_MARGIN_INFO_STYLE = """
      position: absolute;
      left: 1cm;
      bottom: 35mm;
      transform: rotate(-90deg) translateY(-50%);
      transform-origin: top left;
      font-family: 'Liberation Sans', sans-serif;
      font-size: 10pt;""";

  public static final String RIGHT_MARGIN_INFO_STYLE = """
      position: absolute;
      left: 20cm;
      bottom: 35mm;
      width: 100%;
      transform: rotate(-90deg) translateY(-50%);
      transform-origin: top left;
      font-family: 'Liberation Sans', sans-serif;
      font-size: 10pt;""";

  public static final String WATERMARK_STYLE = """
      position: absolute;
      top: calc(50% - 8mm);
      left: 50%;
      transform: translateX(-50%) translateY(-50%) rotate(315deg);
      color: rgb(128, 128, 128);
      opacity: 0.5;
      z-index: -1;
      font-family: 'Liberation Sans', sans-serif;
      font-size: 100pt;""";

}
