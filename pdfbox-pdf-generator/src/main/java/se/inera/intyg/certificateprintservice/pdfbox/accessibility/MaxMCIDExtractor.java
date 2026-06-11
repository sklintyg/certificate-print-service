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
package se.inera.intyg.certificateprintservice.pdfbox.accessibility;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkedContentReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureNode;

// TODO: Is it possible to use this to identify the MCID to increment from instead of hardcoding and
// sending it through the api
public class MaxMCIDExtractor {

  public static int findMaxMcid(PDDocument document) {
    final var markInfo = document.getDocumentCatalog().getMarkInfo();
    if (markInfo == null) {
      return -1;
    }

    final var structureTreeRoot = document.getDocumentCatalog().getStructureTreeRoot();
    if (structureTreeRoot == null) {
      return -1;
    }

    return findMaxMcidInElement(structureTreeRoot);
  }

  private static int findMaxMcidInElement(PDStructureNode node) {
    int max = -1;

    for (Object kid : node.getKids()) {
      if (kid instanceof PDStructureElement element) {
        // Recurse into child structure elements
        max = Math.max(max, findMaxMcidInElement(element));
      } else if (kid instanceof PDMarkedContentReference mcr) {
        final var dict = mcr.getCOSObject();
        if (dict.containsKey(COSName.MCID)) {
          int mcid = dict.getInt(COSName.MCID);
          max = Math.max(max, mcid);
        }
      } else if (kid instanceof COSDictionary dict) {
        // Inline MCID reference (some PDFs skip the wrapper object)
        if (dict.containsKey(COSName.MCID)) {
          max = Math.max(max, dict.getInt(COSName.MCID));
        }
      }
    }

    return max;
  }
}
