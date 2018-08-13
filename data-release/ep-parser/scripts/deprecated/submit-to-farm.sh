#!/bin/bash
# Rebuilds a fresh enzyme portal database. It asks for a
# password to delete all data and then sends the script enzyme-portal-data.sh to LSF.
# Param:
# $1: database environment (uzpdev|uzprel)

echo
echo "**************************** W A R N I N G ****************************"
echo "THIS WILL POPULATE FRESH DATA IN $1. PRESS ENTER WHEN YOU'RE READY"
echo "(Ctrl-C to cancel, Enter to continue)"
read ok

bsub -R "rusage[mem=32000]" -M 32000 -q production-rh6 $(dirname $0)/enzyme-portal-data.sh $1
