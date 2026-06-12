package se.inera.intyg.certificateprintservice.pdfbox.overlay;

import java.awt.Color;
import lombok.Builder;
import se.inera.intyg.certificateprintservice.pdfgenerator.api.custom.model.CustomText;

@Builder
public record TextInfo(CustomText customText,
                       Color color,
                       int mcid) {

}
