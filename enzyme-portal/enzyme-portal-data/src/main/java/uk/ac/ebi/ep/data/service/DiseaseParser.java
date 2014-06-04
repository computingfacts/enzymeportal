/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.ep.data.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.ep.data.domain.Disease;
import uk.ac.ebi.ep.data.domain.UniprotEntry;

/**
 * Class to parse the file - either
 * <a
 * href="http://research.isb-sib.ch/unimed/Swiss-Prot_mesh_mapping.html">HTML</a>
 * or <a href="http://research.isb-sib.ch/unimed/SP_MeSH.tab">tab-delimited</a>
 * - containing a table of equivalences from UniProt accessions to OMIM IDs and
 * MeSH terms.
 *
 * $Author$
 */
@Transactional
@Service
public class DiseaseParser {
    
    @Autowired
    private BioPortalService bioPortalService;
    @Autowired
    private DiseaseService diseaseService;
    
    @Autowired
    private UniprotEntryService uniprotEntryService;
    
    private static final Logger LOGGER
            = Logger.getLogger(DiseaseParser.class);

    /**
     * The format of the provided file to parse.
     *
     * @author rafa
     */
    protected enum Format {
        
        html, tab
    }

    /**
     * Minimum scores to accept a mapping. Currently set to the threshold
     * already set in the UniMed mapping file (-2.5), according to the
     * <a href="http://www.biomedcentral.com/1471-2105/9/S5/S3">paper</a> (see
     * <a href="http://www.biomedcentral.com/1471-2105/9/S5/S3/figure/F4">figure
     * 4).
     */
    private final double minScore = -2.5;
    
    private final Pattern htmlTablePattern = Pattern.compile(
            "^(?:</TR>)?<TR><TD>(.*?)<\\/TD>"
            + "<TD>(.*?)<\\/TD><TD>(.*?)<\\/TD><TD>(.*?)<\\/TD>"
            + "<TD>(.*?)<\\/TD>");
    
    private void LoadToDB(String[] fields) throws InterruptedException {
        double[] scores = new double[1];
        System.out.println("num fields "+ fields.length);
      if(fields.length > 4){  
        String[] scoresCell = fields[4].split(" ?/ ?");
        String accession = fields[0];
        String[] omimCell = fields[1].split("\\s");
        String[] meshIdsCell = fields[2].split(" ?/ ?");
        String[] meshHeadsCell = fields[3].split(" / ");
        
        if (fields[4].contains("/")) {
           
            scores = new double[scoresCell.length];
            for (int i = 0; i < scoresCell.length; i++) {
                final String scoreString = scoresCell[i].trim();
                if (scoreString.equals("exact")) {
                    scores[i] = Double.MAX_VALUE;
                } else {
                    scores[i] = Double.valueOf(scoreString);
                }
            }
        } else {
           
            if (scoresCell[0].equals("exact")) {
                scores[0] = Double.MAX_VALUE;
            } else {
                scores[0] = Double.valueOf(scoresCell[0]);
            }
        } 
        String definition = "";
        for (int i = 0; i < scores.length; i++) {

            //check to see if accession is an enzyme
            UniprotEntry uniprotEntry = uniprotEntryService.findByAccession(accession);
            if (uniprotEntry != null) {
                      
                    definition = bioPortalService.getDiseaseDescription(meshIdsCell[i].trim());


                Disease disease = new Disease();
                disease.setUniprotaccession(accession);
                disease.setDiseaseName(meshHeadsCell[i]);
                disease.setMeshId(meshIdsCell[i]);
                disease.setOmimNumber(omimCell[0]);
                disease.setScore(Double.toString(scores[i]));
                disease.setDefinition(definition);
                disease.setAccession(uniprotEntry);
                
                diseaseService.addDisease(disease);
                
                LOGGER.debug(accession + " mim : " + omimCell[0] + " mesh :" + meshIdsCell[i]
                        + " name: " + meshHeadsCell[i] + " score : " + scores[i]);
                
                System.out.println(accession + " mim : " + omimCell[0] + " mesh :" + meshIdsCell[i]
                        + " name: " + meshHeadsCell[i] + " score : " + scores[i]);

            }
        }
      }else{
          LOGGER.fatal("ArrayIndexOutOfBoundsException. The size of fields is "+ fields.length);
          throw new ArrayIndexOutOfBoundsException();
      }
        
    }
    
    public void parse(String file) throws Exception {
        // Check the extension of the file:
        Format format = Format.valueOf(file.substring(file.lastIndexOf('.') + 1));
        BufferedReader br = null;
        InputStreamReader isr = null;
        InputStream is = null;
        try {
           
            is = file.startsWith("http://")
                    ? new URL(file).openStream()
                    : new FileInputStream(file);
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            LOGGER.info("Parsing start");
            System.out.println("parsing starts ...");
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = getFields(format, line);
                if (fields == null) {
                    continue; // header lines
                }
                System.out.println("fields "+ fields);
                LoadToDB(fields);
                
            }
            LOGGER.info("Parsing end");
            
            //LOGGER.info("Map closed");
        } catch (IOException | InterruptedException e) {
            LOGGER.error("During parsing", e);
            
            throw e;
        } finally {
            if (is != null) {
                is.close();
            }
            if (br != null) {
                br.close();
            }
        }
    }

    /**
     * Splits the fields in one line of the file, namely:
     * <ul>
     * <li>[0] - UniProt accession</li>
     * <li>[1] - MIM number(s)</li>
     * <li>[2] - MeSH ID(s)</li>
     * <li>[3] - MeSH heading(s)</li>
     * <li>[4] - Score(s)</li>
     * </ul>
     *
     * @param format the {@link Format} of the file.
     * @param line one line read from the file.
     * @return the split fields in the line, or <code>null</code> if it is a
     * header line. Note that multi-valued fields must be split further.
     */
    protected String[] getFields(Format format, String line) {
        String[] fields = null;
        switch (format) {
            case html:
                Matcher m = htmlTablePattern.matcher(line);
                // Discard header lines:
                if (!m.matches()) {
                    return null;
                }
                fields = new String[5];
                fields[0] = m.group(1).replaceAll("<\\/?a[^>]*>", "");
                fields[1] = m.group(2).replaceAll("<\\/?a[^>]*>", "");
                fields[2] = m.group(3).replaceAll("<\\/?a[^>]*>", "");
                fields[3] = m.group(4);
                fields[4] = m.group(5);
                break;
            case tab:
                // Discard header lines:
                if (line.startsWith("Swiss-Prot")) {
                    return null;
                }
                fields = line.split("\t");
                break;
        }
        return fields;
    }
    
    
    
}
