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
package se.inera.intyg.certificateprintservice.pdfbox.acroform.overflow;

import java.util.Objects;
import java.util.TreeMap;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.springframework.stereotype.Component;

/**
 * Clones the accessibility structure tree entries from a template overflow page to a newly cloned
 * page. This ensures that static content (header, page number, personal number, footer) on cloned
 * overflow pages is properly tagged and visible to screen readers.
 *
 * <p>Works at the COS dictionary level to properly clone marked content references (MCIDs stored as
 * integer K values) with page references pointing to the cloned page.
 */
@Component
public class OverflowPageStructureCloner {

  private static final COSName S = COSName.S;
  private static final COSName K = COSName.K;
  private static final COSName P = COSName.P;
  private static final COSName PG = COSName.getPDFName("Pg");
  private static final COSName T = COSName.T;
  private static final COSName PARENT_TREE = COSName.PARENT_TREE;

  /**
   * Clones the template page's structure tree for a newly cloned page. Creates structure elements
   * with MCIDs pointing to the cloned page. The last child of the template page SECT is treated as
   * the overflow text area and is recreated empty.
   *
   * <p>After rendering text on the cloned page, call {@link #updateParentTreeForPage} to populate
   * the ParentTree reverse mapping needed for screen reader interactive reading.
   *
   * @param document the PDF document
   * @param templatePageIndex the index of the template overflow page
   * @param clonedPage the newly cloned page
   * @return the DIV element where overflow text should be rendered
   */
  public PDStructureElement cloneStructureForPage(
      PDDocument document, int templatePageIndex, PDPage clonedPage) {
    final var structuredTree = document.getDocumentCatalog().getStructureTreeRoot();
    final var documentTag = (PDStructureElement) structuredTree.getKids().getFirst();
    final var templateSect = getTemplateSect(documentTag, templatePageIndex);

    final var newPageSectDict = new COSDictionary();
    newPageSectDict.setItem(S, templateSect.getCOSObject().getDictionaryObject(S));
    newPageSectDict.setItem(P, documentTag.getCOSObject());
    final var newPageSect = new PDStructureElement(newPageSectDict);
    documentTag.appendKid(newPageSect);

    final var templateKids = getKidsArray(templateSect.getCOSObject());
    final var newKidsArray = new COSArray();
    PDStructureElement overflowDiv = null;

    if (templateKids != null) {
      for (var i = 0; i < templateKids.size(); i++) {
        final var isLastChild = (i == templateKids.size() - 1);
        final var kidObj = resolveObject(templateKids.get(i));

        if (isLastChild) {
          overflowDiv = new PDStructureElement(StandardStructureTypes.DIV, newPageSect);
          overflowDiv.getCOSObject().setItem(PG, clonedPage.getCOSObject());
          newKidsArray.add(overflowDiv.getCOSObject());
        } else if (kidObj instanceof COSDictionary kidDict && kidDict.containsKey(S)) {
          final var clonedChild = cloneStructureElement(kidDict, newPageSect, clonedPage);
          newKidsArray.add(clonedChild);
        }
      }
    }

    if (overflowDiv == null) {
      overflowDiv = new PDStructureElement(StandardStructureTypes.DIV, newPageSect);
      overflowDiv.getCOSObject().setItem(PG, clonedPage.getCOSObject());
      newKidsArray.add(overflowDiv.getCOSObject());
    }

    newPageSectDict.setItem(K, newKidsArray);
    assignStructParentsKey(document, clonedPage);
    return overflowDiv;
  }

  /**
   * Updates the ParentTree entry for a cloned page after all content (static + overflow text) has
   * been rendered. This populates the reverse mapping (StructParents → MCID → structure element)
   * needed by screen readers for interactive reading (click-to-read).
   *
   * <p>Must be called after all text rendering on the page is complete.
   *
   * @param document the PDF document
   * @param page the cloned page with StructParents already assigned
   */
  public void updateParentTreeForPage(PDDocument document, PDPage page) {
    final var structParentsKey = page.getCOSObject().getInt(COSName.STRUCT_PARENTS, -1);
    if (structParentsKey < 0) {
      return;
    }

    final var treeRoot = document.getDocumentCatalog().getStructureTreeRoot();
    final var documentTag = (PDStructureElement) treeRoot.getKids().getFirst();
    final var pageSect = findSectForPage(documentTag, page);
    if (pageSect == null) {
      return;
    }

    addParentTreeEntry(treeRoot, structParentsKey, pageSect);
  }

  private COSDictionary cloneStructureElement(
      COSDictionary source, PDStructureElement parent, PDPage clonedPage) {
    final var cloned = new COSDictionary();
    cloned.setItem(S, source.getDictionaryObject(S));
    cloned.setItem(P, parent.getCOSObject());
    cloned.setItem(PG, clonedPage.getCOSObject());

    if (source.containsKey(T)) {
      cloned.setItem(T, source.getDictionaryObject(T));
    }

    final var sourceK = source.getDictionaryObject(K);
    if (sourceK != null) {
      cloned.setItem(K, cloneKEntry(resolveObject(sourceK), cloned, clonedPage));
    }

    return cloned;
  }

  private COSBase cloneKEntry(COSBase sourceK, COSDictionary parentDict, PDPage clonedPage) {
    if (sourceK instanceof COSInteger) {
      return sourceK;
    } else if (sourceK instanceof COSArray sourceArray) {
      final var clonedArray = new COSArray();
      for (var i = 0; i < sourceArray.size(); i++) {
        final var item = resolveObject(sourceArray.get(i));
        clonedArray.add(cloneKEntry(item, parentDict, clonedPage));
      }
      return clonedArray;
    } else if (sourceK instanceof COSDictionary sourceDict) {
      if (sourceDict.containsKey(S)) {
        return cloneStructureElement(sourceDict, new PDStructureElement(parentDict), clonedPage);
      } else if (sourceDict.containsKey(COSName.MCID)) {
        final var mcrClone = new COSDictionary();
        mcrClone.setInt(COSName.MCID, sourceDict.getInt(COSName.MCID));
        mcrClone.setItem(PG, clonedPage.getCOSObject());
        return mcrClone;
      }
    }
    return sourceK;
  }

  private COSArray getKidsArray(COSDictionary elementDict) {
    final var kObj = resolveObject(elementDict.getDictionaryObject(K));
    if (kObj instanceof COSArray array) {
      return array;
    } else if (kObj != null) {
      final var wrapper = new COSArray();
      wrapper.add(kObj);
      return wrapper;
    }
    return null;
  }

  private COSBase resolveObject(COSBase base) {
    if (base instanceof COSObject cosObj) {
      return cosObj.getObject();
    }
    return base;
  }

  private void assignStructParentsKey(PDDocument document, PDPage clonedPage) {
    final var treeRoot = document.getDocumentCatalog().getStructureTreeRoot();
    final var nextKey =
        treeRoot.getCOSObject().getInt(COSName.getPDFName("ParentTreeNextKey"), 100);
    clonedPage.getCOSObject().setInt(COSName.STRUCT_PARENTS, nextKey);
    treeRoot.getCOSObject().setInt(COSName.getPDFName("ParentTreeNextKey"), nextKey + 1);
  }

  private COSDictionary findSectForPage(PDStructureElement documentTag, PDPage page) {
    final var pageCos = page.getCOSObject();
    final var kids = documentTag.getKids();
    for (var i = kids.size() - 1; i >= 0; i--) {
      if (kids.get(i) instanceof PDStructureElement sect) {
        final var sectDict = sect.getCOSObject();
        final var sectKids = getKidsArray(sectDict);
        if (sectKids != null && hasPageReference(sectKids, pageCos)) {
          return sectDict;
        }
      }
    }
    return null;
  }

  private boolean hasPageReference(COSArray kids, COSDictionary pageCos) {
    for (var i = 0; i < kids.size(); i++) {
      final var kid = resolveObject(kids.get(i));
      if (kid instanceof COSDictionary kidDict) {
        final var pg = kidDict.getDictionaryObject(PG);
        if (pg == pageCos) {
          return true;
        }
      }
    }
    return false;
  }

  private void addParentTreeEntry(
      PDStructureTreeRoot treeRoot, int key, COSDictionary pageStructDict) {
    final var mcidToElement = new TreeMap<Integer, COSDictionary>();
    collectMcidMappings(pageStructDict, mcidToElement);

    if (mcidToElement.isEmpty()) {
      return;
    }

    final var maxMcid = mcidToElement.lastKey();
    final var parentArray = new COSArray();
    for (var i = 0; i <= maxMcid; i++) {
      final var elem = mcidToElement.get(i);
      parentArray.add(Objects.requireNonNullElse(elem, COSNull.NULL));
    }

    final var parentTreeDict =
        (COSDictionary) resolveObject(treeRoot.getCOSObject().getDictionaryObject(PARENT_TREE));
    if (parentTreeDict == null) {
      return;
    }

    final var kidsObj =
        resolveObject(parentTreeDict.getDictionaryObject(COSName.getPDFName("Kids")));
    if (kidsObj instanceof COSArray kidsArray && kidsArray.size() > 0) {
      if (replaceInKids(kidsArray, key, parentArray)) {
        return;
      }
      final var lastKid = (COSDictionary) resolveObject(kidsArray.get(kidsArray.size() - 1));
      final var nums =
          (COSArray) resolveObject(lastKid.getDictionaryObject(COSName.getPDFName("Nums")));
      if (nums != null) {
        nums.add(COSInteger.get(key));
        nums.add(parentArray);
        final var limits =
            (COSArray) resolveObject(lastKid.getDictionaryObject(COSName.getPDFName("Limits")));
        if (limits != null && limits.size() >= 2) {
          limits.set(1, COSInteger.get(key));
        }
      }
    } else {
      final var nums =
          resolveObject(parentTreeDict.getDictionaryObject(COSName.getPDFName("Nums")));
      if (nums instanceof COSArray numsArray && !replaceInNums(numsArray, key, parentArray)) {
        numsArray.add(COSInteger.get(key));
        numsArray.add(parentArray);
      }
    }
  }

  private boolean replaceInKids(COSArray kidsArray, int key, COSArray newValue) {
    for (var k = 0; k < kidsArray.size(); k++) {
      final var kid = (COSDictionary) resolveObject(kidsArray.get(k));
      final var nums =
          (COSArray) resolveObject(kid.getDictionaryObject(COSName.getPDFName("Nums")));
      if (nums != null && replaceInNums(nums, key, newValue)) {
        return true;
      }
    }
    return false;
  }

  private boolean replaceInNums(COSArray nums, int key, COSArray newValue) {
    for (var i = 0; i < nums.size() - 1; i += 2) {
      final var numObj = resolveObject(nums.get(i));
      if (numObj instanceof COSInteger num && num.intValue() == key) {
        nums.set(i + 1, newValue);
        return true;
      }
    }
    return false;
  }

  private void collectMcidMappings(
      COSDictionary element, java.util.TreeMap<Integer, COSDictionary> map) {
    final var kEntry = resolveObject(element.getDictionaryObject(K));
    if (kEntry instanceof COSInteger mcid) {
      map.put(mcid.intValue(), element);
    } else if (kEntry instanceof COSDictionary kDict) {
      if (kDict.containsKey(COSName.MCID)) {
        map.put(kDict.getInt(COSName.MCID), element);
      } else if (kDict.containsKey(S)) {
        collectMcidMappings(kDict, map);
      }
    } else if (kEntry instanceof COSArray kArray) {
      for (var i = 0; i < kArray.size(); i++) {
        final var item = resolveObject(kArray.get(i));
        if (item instanceof COSInteger mcid) {
          map.put(mcid.intValue(), element);
        } else if (item instanceof COSDictionary itemDict) {
          if (itemDict.containsKey(COSName.MCID)) {
            map.put(itemDict.getInt(COSName.MCID), element);
          } else if (itemDict.containsKey(S)) {
            collectMcidMappings(itemDict, map);
          }
        }
      }
    }
  }

  private PDStructureElement getTemplateSect(
      PDStructureElement documentTag, int templatePageIndex) {
    final var kids = documentTag.getKids();
    if (templatePageIndex >= kids.size()) {
      return (PDStructureElement) kids.getLast();
    }
    return (PDStructureElement) kids.get(templatePageIndex);
  }
}
