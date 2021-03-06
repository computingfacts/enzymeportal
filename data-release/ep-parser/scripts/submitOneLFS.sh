#!/bin/bash

#use this java setup
#export JAVA_HOME=/nfs/public/rw/webadmin/java/jdks/latest_1.8
#export PATH=$PATH:$JAVA_HOME/bin
#source ~/.bashrc
echo $JAVA_HOME
echo $MAVEN_HOME

# e.g - how to run: sh submitOne.sh uzprel ./cofactor.sh
# Param:
# $1: database environment (uzpdev|uzprel)
# $2 : the script to be submitted to the farm with extension e.g chebi.sh
TODAY=$(date +%Y%m%d_%H-%M-%S)
LOG_NAME=$(basename $2 .sh)
LOG_DIR=/ebi/uniprot/production/enzyme_portal/logs/parser/bsub-$LOG_NAME-$TODAY.log

echo
echo "**************************** W A R N I N G ****************************"
echo "THIS MAY OVERWRITE ALL DATA IN $1. ARE YOU SURE?"
echo "(Ctrl-C to cancel, Enter to continue)"
echo "LSF logs can be found here $LOG_DIR "
read ok

#bsub -R "rusage[mem=32000]" -M 32000 -q production-rh6 $(dirname $0)/$2 $1
#bsub -q production-rh6 $(dirname $0)/$2 $1
#bsub $(dirname $0)/$2 $1
#bsub -R "rusage[mem=32000]" -M 32000 -q production-rh7 uzprel ./cofactor.sh
#bsub -R "rusage[mem=32000]" -M 32000 -q production-rh7 $(dirname $0)/$2 $1
#bsub -R "rusage[mem=32000]" -M 64000 -q production-rh7 -o $LOG_DIR $(dirname $0)/$2 $1
bsub -R "rusage[mem=32000]" -M 128000 -q production-rh74 -o $LOG_DIR $(dirname $0)/$2 $1
