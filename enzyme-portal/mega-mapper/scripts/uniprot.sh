#!/bin/bash

#DOWNLOAD_BASE=ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/complete
DOWNLOAD_BASE=/ebi/extserv/projects/ebinocle/data/uniprot/latest
SWISSPROT=$DOWNLOAD_BASE/uniprot_sprot.xml
TREMBL=$DOWNLOAD_BASE/uniprot_trembl.xml
MM_INDEX=/nfs/seqdb/production/intenz/users/rafalcan/tmp/ep/mmIndex

# xsltproc builds the tree in DOM, too much menory.
#XSL=$(dirname $0)/../src/main/resources/uniprot2species-tab.xsl
#xsltproc $XSL $SWISSPROT > SwissProt2species.tab

cd $(dirname $0)/..
mvn clean package

CP=.
for JAR in target/*.jar
do
    CP=$CP:$JAR
done

java -classpath $CP uk.ac.ebi.ep.mm.UniprotIndexer \
    -xmlFile $SWISSPROT -indexDir $MM_INDEX \
&& java -classpath $CP uk.ac.ebi.ep.mm.UniprotIndexer \
    -xmlFile $TREMBL -indexDir $MM_INDEX

