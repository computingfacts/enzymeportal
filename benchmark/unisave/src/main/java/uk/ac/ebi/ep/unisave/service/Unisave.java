package uk.ac.ebi.ep.unisave.service;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import uk.ac.ebi.ep.unisave.documents.UnisaveEntry;
import uk.ac.ebi.ep.unisave.documents.UnisaveVersion;
import uk.ac.ebi.ep.unisave.repositories.UnisaveEntryRepository;

/**
 *
 * @author Joseph
 */
public class Unisave {

    public static void main(String[] args) {

       // SpringApplication app = new SpringApplication(Unisave.class);
        AnnotationConfigReactiveWebApplicationContext context = new AnnotationConfigReactiveWebApplicationContext();
        //AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("dev");

        context.scan("uk.ac.ebi.ep.unisave.config");
        Properties p = new Properties();
        p.setProperty("couchbase.adminUser", "joseph");
        p.setProperty("couchbase.adminPassword", "joseph");

        p.setProperty("couchbase.host", "http://ves-oy-cf");
        p.setProperty("couchbase.bucket", "joseph");
        p.setProperty("couchbase.password", "joseph");
        System.setProperties(p);
        context.refresh();
        UnisaveEntryRepository entryRepository = context.getBean(UnisaveEntryRepository.class);
        System.out.println("entry repo " + entryRepository);

        //entryRepository.findAll().forEach(x -> System.out.println("data " + x));
        entryRepository.loadUnisaveEntries(entries());

    }

    private static List<UnisaveEntry> entries() {

//        return IntStream.range(1, 1_987_694)
//                .mapToObj(n -> newUnisaveEntry(n))
//                .collect(Collectors.toList());
        return IntStream.range(1, 100_000)
                .mapToObj(n -> newUnisaveEntry(n))
                .collect(Collectors.toList());

    }

    private static UnisaveEntry newUnisaveEntry(int id) {
        UnisaveEntry entry = new UnisaveEntry();
        entry.setAccesion("A0A009DWL0_" + id);
        entry.setLatest(999);
        entry.setType("Reactive_");

        List<UnisaveVersion> versions = IntStream.range(1, 18)
                .mapToObj(n -> createVersions(n))
                .collect(Collectors.toList());

        entry.setUnisaveVersions(versions);
        return entry;
    }

    private static UnisaveVersion createVersions(Integer number) {
        UnisaveVersion version = new UnisaveVersion();
        //version.setAccession(accession);

        String fullContent = "ID   A0A009DWL0_ACIBA        Unreviewed;        76 AA.\\nAC   A0A009DWL0;\\nDT   11-JUN-2014, integrated into UniProtKB/TrEMBL.\\nDT   11-JUN-2014, sequence version 1.\\nDT   11-JUN-2014, entry version 1.\\nDE   SubName: Full=Putative iSRSO8-transposase orfB protein;\\nGN   ORFNames=J504_3657;\\nOS   Acinetobacter baumannii 348935.\\nOC   Bacteria; Proteobacteria; Gammaproteobacteria; Pseudomonadales;\\nOC   Moraxellaceae; Acinetobacter;\\nOC   Acinetobacter calcoaceticus/baumannii complex.\\nOX   NCBI_TaxID=1310605;\\nRN   [1]\\nRP   NUCLEOTIDE SEQUENCE.\\nRC   STRAIN=348935;\\nRA   Harris A.D., Johnson K.J., George J., Shefchek K., Daugherty S.C.,\\nRA   Parankush S., Sadzewicz L., Tallon L., Sengamalay N., Hazen T.H.,\\nRA   Rasko D.A.;\\nRT   \\\"Comparative genomics and transcriptomics to identify genetic\\nRT   mechanisms underlying the emergence of carbapenem resistant\\nRT   Acinetobacter baumannii (CRAb).\\\";\\nRL   Submitted (FEB-2014) to the EMBL/GenBank/DDBJ databases.\\nCC   -!- CAUTION: The sequence shown here is derived from an\\nCC       EMBL/GenBank/DDBJ whole genome shotgun (WGS) entry which is\\nCC       preliminary data.\\nDR   EMBL; JEVW01000382; EXA54896.1; -; Genomic_DNA.\\nPE   4: Predicted;\\nSQ   SEQUENCE   76 AA;  8555 MW;  D2A9E69829D2B2D7 CRC64;\\n     MIHHSDRGVQ YLSIRYTNRL EAANLRASVG TTGDSYDNAL AETVNGLYKT EVIFKSRLAR\\n     FSRCTTCDTK LGRLVQ\\n//\\n";
        version.setDatabase("TrEMBL");
        version.setFullContent(fullContent);
        version.setMd5("c650acfd144df989079960aa3dd30713");
        version.setName("A0A009DWL0_ACIBA");
        version.setRelease("2014_08/2014_08");
        version.setReleaseDate("2014-09-03");
        version.setSequenceMd5("b3d56af5e9d541847ad810a50b17d357");
        version.setSequenceVersion(1);
        version.setVersionNumber(number);
        version.setType("Summary");
        return version;
    }

    private Set<JsonDocument> generateJsonDocs(int numberToCreate) {
        // Generate a number of dummy JSON documents
        int docsToCreate = numberToCreate;
        Set<JsonDocument> documents = new HashSet<>();
        for (int i = 0; i < docsToCreate; i++) {
            JsonObject content = JsonObject.create()
                    .put("id", UUID.randomUUID().toString())
                    .put("accession", "P0A0QWRDTY5_" + i)
                    .put("latext", "20")
                    .put("type", "Data");
            documents.add(JsonDocument.create("doc-" + i, content));
        }
        return documents;
    }

    private UnisaveEntry addUnisaveEntry() {
        UnisaveEntry entry = new UnisaveEntry();
        entry.setAccesion("A0A009DWL0");
        entry.setLatest(17);
        entry.setType("Test");

        List<UnisaveVersion> versions = IntStream.range(1, 18)
                .mapToObj(n -> createVersions(n))
                .collect(Collectors.toList());

        entry.setUnisaveVersions(versions);
        return entry;
    }

}
