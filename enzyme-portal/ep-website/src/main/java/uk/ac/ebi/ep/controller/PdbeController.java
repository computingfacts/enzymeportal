package uk.ac.ebi.ep.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import uk.ac.ebi.das.jdas.adapters.features.DasGFFAdapter.SegmentAdapter;
import uk.ac.ebi.das.jdas.adapters.features.FeatureAdapter;
import uk.ac.ebi.ep.adapter.das.IDASFeaturesAdapter;
import uk.ac.ebi.ep.adapter.das.SimpleDASFeaturesAdapter;
import uk.ac.ebi.ep.data.enzyme.model.DASSummary;
import uk.ac.ebi.ep.data.enzyme.model.Image;
import uk.ac.ebi.ep.data.enzyme.model.ProteinStructure;

/**
 * Ajax workaround for enzymes with many structures.
 *
 * @author rafa
 *
 */
@Deprecated
@Controller
public class PdbeController {

  
     private final Logger LOGGER = LoggerFactory.getLogger(PdbeController.class);

    //@RequestMapping(value = "/ajax/pdbe/{pdbId}")
    protected String getStructure(Model model, @PathVariable String pdbId) {
        String retValue = "proteinStructure-single";
        ProteinStructure structure = null;
        try {
            SimpleDASFeaturesAdapter pdbeAdapter =
                    new SimpleDASFeaturesAdapter(IDASFeaturesAdapter.PDBE_DAS_URL);
            SegmentAdapter segment = pdbeAdapter.getSegment(pdbId);
            structure = new ProteinStructure();
            structure.setId(segment.getId());

            for (FeatureAdapter feature : segment.getFeature()) {
                if (feature.getType().getId().equals("description")) {
                    structure.setDescription(feature.getNotes().get(0)); // FIXME?
                    structure.setName(feature.getNotes().get(0));

                } else if (feature.getType().getId().equals("image")) {
                    Image image = new Image();
                    image.setSource(feature.getLinks().get(0).getHref());
                    image.setCaption(feature.getLinks().get(0).getContent());
                    image.setHref(feature.getLinks().get(1).getHref());
                    structure.setImage(image);
                } else if (feature.getType().getId().equals("provenance")) {
                    structure.setProvenance(feature.getNotes());
                } else if (feature.getType().getId().equals("summary")) {
                    DASSummary summary = new DASSummary();
                    summary.setLabel(feature.getLabel());
                    summary.setNote(feature.getNotes());
                    structure.getSummary().add(summary);
                }
            }
         model.addAttribute("proteinStructure", structure);

		} catch (Exception e) {
			LOGGER.error("Unable to retrieve structure " + pdbId, e);
			retValue = "error";
		}
        
        return retValue;
    }
}
