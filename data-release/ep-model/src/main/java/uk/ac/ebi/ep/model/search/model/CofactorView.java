package uk.ac.ebi.ep.model.search.model;

import org.springframework.data.web.ProjectedPayload;

/**
 *
 * @author joseph
 */
@ProjectedPayload
public interface CofactorView {

    String getCompoundName();

    String getCompoundId();
}
