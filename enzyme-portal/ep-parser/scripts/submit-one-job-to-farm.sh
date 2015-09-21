#!/bin/bash
# Rebuilds a fresh enzyme portal database. It asks for a
# password to delete all data and then sends the script enzyme-portal-data.sh to LSF.
# Param:
# $1: database environment (uzpdev|uzprel)
# $2 : the script to be submitted to the farm with extention e.g chebi.sh

echo
echo "**************************** W A R N I N G ****************************"
echo "THIS WILL OVERWRITE ALL DATA IN $1. ARE YOU SURE?"
echo "(Ctrl-C to cancel, Enter to continue)"
read ok

bsub -R "rusage[mem=64000]" -M 64000 -q production-rh6 $(dirname $0)/$2 $1
