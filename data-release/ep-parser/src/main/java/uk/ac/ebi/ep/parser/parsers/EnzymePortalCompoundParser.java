package uk.ac.ebi.ep.parser.parsers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.ep.centralservice.chembl.service.ChemblService;
import uk.ac.ebi.ep.model.EnzymePortalCompound;
import uk.ac.ebi.ep.model.repositories.EnzymePortalCompoundRepository;
import uk.ac.ebi.ep.model.service.EnzymePortalParserService;
import uk.ac.ebi.ep.parser.xmlparser.ChemblXmlParser;

/**
 *
 * @author joseph <joseph@ebi.ac.uk>
 */
@Service
public class EnzymePortalCompoundParser {

    @Autowired
    private EnzymePortalCompoundRepository compoundRepository;

    @Autowired
    private ChemblService chemblService;
    @Autowired
    private ChemblXmlParser chemblXmlParser;
    @Autowired
    private EnzymePortalParserService parserService;

    @Transactional
    public EnzymePortalCompound addCompound(EnzymePortalCompound c) {
        EnzymePortalCompound compound = compoundRepository.saveAndFlush(c);
        return compound;
    }

    @Transactional
    public List<EnzymePortalCompound> addCompound(List<EnzymePortalCompound> compounds) {
        List<EnzymePortalCompound> portalCompounds = compoundRepository.saveAll(compounds);
        return portalCompounds;
    }

    @Transactional
    public void loadChemblFDA() {

        SmallMolecules fda = new FDA(parserService, chemblService);
        fda.loadChEMBL();

    }

    @Transactional
    public void loadCofactorsFromFTPFiles() {

        GenericCompound cofactor = new CofactorsFtpFiles(parserService);
        cofactor.loadCompoundToDatabase();

    }

    @Transactional
    public void loadUniqueCofactorsToDatabase() {
        CofactorsFtpFiles cofactor = new CofactorsFtpFiles(parserService);
        cofactor.loadUniqueCofactorsToDatabase();

    }

    @Transactional
    @Modifying
    public void loadChemblTargets() {
        TargetParser chemblTargets = new TargetParser(chemblXmlParser, parserService);
        chemblTargets.loadChemblTargetsToDB();

    }

}
