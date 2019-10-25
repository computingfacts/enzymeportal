package uk.ac.ebi.ep.enzymeservice.intenz.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import uk.ac.ebi.ep.enzymeservice.intenz.dto.EcClass;
import uk.ac.ebi.ep.enzymeservice.intenz.dto.EnzymeHierarchy;
import uk.ac.ebi.ep.enzymeservice.intenz.model.EcClassType;
import uk.ac.ebi.ep.enzymeservice.intenz.model.EcSubclassType;
import uk.ac.ebi.ep.enzymeservice.intenz.model.EcSubsubclassType;
import uk.ac.ebi.ep.enzymeservice.intenz.model.EntryType;
import uk.ac.ebi.ep.enzymeservice.intenz.model.EnzymeNameType;
import uk.ac.ebi.ep.enzymeservice.intenz.model.Intenz;
import uk.ac.ebi.ep.enzymeservice.intenz.model.XmlContentType;

/**
 *
 * @since 1.0
 * @author joseph
 */
@Slf4j
public class IntenzCallable {

    private static final String INTENZ_PACKAGE = "uk.ac.ebi.ep.enzymeservice.intenz.model";
    private static JAXBContext jaxbContext = null;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(INTENZ_PACKAGE);
        } catch (JAXBException ex) {
            log.error("Unable to find the package "
                    + INTENZ_PACKAGE + " to map the intenz xml file!", ex);
        }
    }

    public static class GetIntenzCaller implements Callable<Intenz> {

        protected String ecUrl;

        public GetIntenzCaller(String ecUrl) {
            this.ecUrl = ecUrl;
        }

        public GetIntenzCaller() {
        }

        @Override
        public Intenz call() throws Exception {
            return getData();
        }

        public Intenz getData() {
            Intenz intenz = null;
            try {

                URL url = new URL(ecUrl);

                URLConnection con = url.openConnection(Proxy.NO_PROXY);
                con.connect();

                InputStream is = con.getInputStream();

                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                if (is != null) {

                    intenz = (Intenz) unmarshaller.unmarshal(is);
                    return intenz;

                }

            } catch (IOException | JAXBException ex) {
                log.error(ex.getMessage());
            }
            return intenz;
        }

        private EcClass setEnzymeName(XmlContentType contentType, String ecNumber) {
            EcClass ecClass = new EcClass();
            String name = null;
            if (contentType != null) {
                List<Object> nameObject = contentType.getContent();
                name = (String) nameObject.get(0);
            } else {
                name = ecNumber;
            }

            ecClass.setEc(ecNumber);
            ecClass.setName(name);
            return ecClass;
        }

        private List<EcClass> getEcClass(Intenz intenz) {
            List<EcClass> ecClasseList = new ArrayList<>();
            EcClassType levelOne = intenz.getEcClass().get(0);
            String levelOneEc = levelOne.getEc1().toString();
            EcClass ecClass = setEnzymeName(levelOne.getName(), levelOneEc);
            ecClasseList.add(ecClass);

            EcSubclassType levelTwo = levelOne.getEcSubclass().get(0);
            String levelTwoEc = levelOneEc + "." + levelTwo.getEc2().toString();
            EcClass ecClass2 = setEnzymeName(levelTwo.getName(), levelTwoEc);
            ecClasseList.add(ecClass2);

            EcSubsubclassType levelThree = levelTwo.getEcSubSubclass().get(0);
            String levelThreeEc
                    = levelTwoEc + "." + levelThree.getEc3().toString();
            EcClass ecClass3
                    = setEnzymeName(levelThree.getName(), levelThreeEc);
            ecClasseList.add(ecClass3);

            EntryType levelFour = levelThree.getEnzyme().get(0);
            List<EnzymeNameType> acceptedNames = levelFour.getAcceptedName();
            String enzymeName = null;
            if (acceptedNames == null || acceptedNames.isEmpty()) {
                // transferred or deleted entry?
                if (levelFour.getTransferred() != null) {
                    enzymeName = levelFour.getTransferred().getNote();
                } else if (levelFour.getDeleted() != null) {
                    enzymeName = levelFour.getDeleted().getNote();
                }
            } else {
                enzymeName = (String) acceptedNames.get(0).getContent().get(0);
            }
            final String ecNumber = levelFour.getEc().replace("EC ", "");
            EcClass ecClass4 = new EcClass().withEc(ecNumber)
                    .withName(enzymeName);
            ecClasseList.add(ecClass4);

            return ecClasseList;
        }
    }

    public static class GetEcHierarchyCaller
            implements Callable<EnzymeHierarchy> {

        protected GetIntenzCaller intenzCaller;

        public GetEcHierarchyCaller(String ecUrl) {
            intenzCaller = new GetIntenzCaller(ecUrl);
        }

        public GetEcHierarchyCaller() {
            intenzCaller = new GetIntenzCaller();
        }

        @Override
        public EnzymeHierarchy call() throws Exception {
            Intenz intenz = intenzCaller.getData();
            return getEcHierarchy(intenz);
        }

        public EnzymeHierarchy getEcHierarchy(Intenz intenz) {
            EnzymeHierarchy enzymeHierarchy = new EnzymeHierarchy();
            List<EcClass> ecClassList = intenzCaller.getEcClass(intenz);
            enzymeHierarchy.setEcclass(ecClassList);
            return enzymeHierarchy;
        }
    }

}
