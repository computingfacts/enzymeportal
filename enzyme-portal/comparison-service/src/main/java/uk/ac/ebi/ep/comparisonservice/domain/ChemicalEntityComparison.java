package uk.ac.ebi.ep.base.comparison;

import uk.ac.ebi.ep.data.enzyme.model.ChemicalEntity;



/**
 * Comparison for chemical entities related to an enzyme. This includes
 * activators, inhibitors, cofactors, drugs and bioactive ligands but excludes
 * reactions (already covered by {@link ReactionPathwayComparison}.
 * 
 * @author rafa
 * @since 1.1.0
 */
public class ChemicalEntityComparison
extends AbstractComparison<ChemicalEntity> {

    public ChemicalEntityComparison(ChemicalEntity ce1, ChemicalEntity ce2) {
        compared = new ChemicalEntity[] { ce1, ce2 };
        init(ce1, ce2);
    }

    protected void getSubComparisons(ChemicalEntity ce1, ChemicalEntity ce2) {
        if (ce1.getActivators() != null || ce2.getActivators() != null) {
            subComparisons.put("Activators", new ListComparison(
                    ce1.getActivators() == null?
                            null : ce1.getActivators().getMolecule(),
                    ce2.getActivators() == null?
                            null : ce2.getActivators().getMolecule()));
        }
        if (ce1.getInhibitors() != null || ce2.getInhibitors() != null) {
            subComparisons.put("Inhibitors", new ListComparison(
                    ce1.getInhibitors() == null?
                            null : ce1.getInhibitors().getMolecule(),
                    ce2.getInhibitors() == null?
                            null : ce2.getInhibitors().getMolecule()));
        }
        if (ce1.getCofactors() != null || ce2.getCofactors() != null) {
            subComparisons.put("Cofactors", new ListComparison(
                    ce1.getCofactors() == null?
                            null : ce1.getCofactors().getMolecule(),
                    ce2.getCofactors() == null?
                            null : ce2.getCofactors().getMolecule()));
        }
        if (ce1.getDrugs() != null || ce2.getDrugs() != null) {
            subComparisons.put("Drugs", new ListComparison(
                    ce1.getDrugs() == null?
                            null : ce1.getDrugs().getMolecule(),
                    ce2.getDrugs() == null?
                            null : ce2.getDrugs().getMolecule()));
        }
        if (ce1.getBioactiveLigands() != null
                || ce2.getBioactiveLigands() != null) {
            subComparisons.put("Bioactive ligands", new ListComparison(
                    ce1.getBioactiveLigands() == null?
                            null : ce1.getBioactiveLigands().getMolecule(),
                    ce2.getBioactiveLigands() == null?
                            null : ce2.getBioactiveLigands().getMolecule()));
        }
    }

    @Override
    public String toString() {
        return "Small molecules";
    }

}
