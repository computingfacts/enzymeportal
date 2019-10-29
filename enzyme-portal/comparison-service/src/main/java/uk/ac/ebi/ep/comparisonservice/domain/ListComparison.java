package uk.ac.ebi.ep.comparisonservice.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import uk.ac.ebi.ep.comparisonservice.model.Compound;
import uk.ac.ebi.ep.comparisonservice.model.Disease;
import uk.ac.ebi.ep.comparisonservice.model.ReactionPathway;
import uk.ac.ebi.ep.enzymeservice.reactome.view.PathWay;

/**
 * Comparison for lists of items. It does not take into account their order, so
 * for this class <code>[ "1", "2" ]</code> is not different from
 * <code>[ "2", "1" ]</code>.<br>
 * This comparison requires that the items class implements properly the equals
 * and hashCode methods.
 *
 * @author Joseph
 * @since 1.1.0
 */
public class ListComparison extends AbstractComparison<List<?>> {

    private static final String DIFF = "diff-";

    public ListComparison(final List<?> l1, final List<?> l2) {
        compared = new List<?>[]{l1, l2};
        init(l1, l2);
    }

    @Override
    protected void getSubComparisons(List<?> l1, List<?> l2) {
        if (l1 == null) {
            l1 = new ArrayList<>();
        }
        if (l2 == null) {
            l2 = new ArrayList<>();
        }

        List<?> common = ListUtils.intersection(l1, l2);
        List<?> onlyL1 = CollectionUtils.subtract(l1, common).stream().collect(Collectors.toList());
        List<?> onlyL2 = CollectionUtils.subtract(l2, common).stream().collect(Collectors.toList());

        int c = 0;
        for (Object o : common) {
            // We do not use just Object o, they are different objects!
            subComparisons.put("same-" + c++, getItemComparison(
                    l1.get(l1.indexOf(o)), l2.get(l2.indexOf(o))));
        }
        final int min = Math.min(onlyL1.size(), onlyL2.size());
        int i = 0;
        for (; i < min; i++) {
            subComparisons.put(DIFF + i,
                    getItemComparison(onlyL1.get(i), onlyL2.get(i)));
        }
        if (onlyL1.size() > onlyL2.size()) {
            for (; i < onlyL1.size(); i++) {
                subComparisons.put(DIFF + i,
                        getItemComparison(onlyL1.get(i), null));
            }
        } else if (onlyL1.size() < onlyL2.size()) {
            for (; i < onlyL2.size(); i++) {
                subComparisons.put(DIFF + i,
                        getItemComparison(null, onlyL2.get(i)));
            }
        }
        differ = !onlyL1.isEmpty() || !onlyL2.isEmpty();
    }

    /**
     * Factory method to get a comparison for two objects according to their
     * class.
     *
     * @param o1
     * @param o2
     * @return an adequate Comparison.
     */
    private Comparison<?> getItemComparison(Object o1, Object o2) {
        Comparison<?> itemComparison = null;
        Class<? extends Object> theClass = o1 != null
                ? o1.getClass() : o2.getClass();
        if (theClass.equals(String.class)) {
            itemComparison = new StringComparison((String) o1, (String) o2);
        } else if (theClass.equals(ReactionPathway.class)) {
            itemComparison = new ReactionPathwayComparison(
                    (ReactionPathway) o1, (ReactionPathway) o2);
        } else if (theClass.equals(PathWay.class)) {
            itemComparison = new PathwayComparison((PathWay) o1, (PathWay) o2);
        } else if (theClass.equals(Compound.class)) {
            itemComparison = new MoleculeComparison(
                    (Compound) o1, (Compound) o2);
        } else if (theClass.equals(Disease.class)) {
            itemComparison = new DiseaseComparison((Disease) o1, (Disease) o2);
        }
        return itemComparison;
    }

    @Override
    public String toString() {
        return subComparisons == null || subComparisons.isEmpty() ? ""
                : subComparisons.entrySet().iterator().next().getValue()
                        .toString();
    }
}
