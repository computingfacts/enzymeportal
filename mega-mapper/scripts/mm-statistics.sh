#!/bin/bash
# Generates statistics for the mega-map and saves them in a tsv file.
# Param:
# $1: database environment (enzdev|ezprel)

. $(dirname $0)/readDbConfig.sh
. $(dirname $0)/ts.sh

STATS_SQL=$(dirname $0)/../src/main/sql/util/statistics
STATS_DIR=$(dirname $0)/../src/site/resources/statistics
TSV_FILE=${STATS_DIR}/stats-${NOW}.tsv

echo "Generating statistics..."
sqlplus ${DB_USER}/${DB_PASSWD}@${1} @${STATS_SQL} ${TSV_FILE}
echo "Statistics dumped to file $TSV_FILE"

# Add the statistics file to svn and commit:
TRUNK_URL=$(svn info | grep ^URL | sed 's/^URL: //' | \
    sed 's/trunk\/.*//;s/tags\/.*//;s/branches\/.*//')trunk/enzyme-portal/mega-mapper
# Are we working with the trunk (0) or not (1)?:
IS_TRUNK=$(svn info | grep $TRUNK_URL)

while read SVN_ADD
do
    svn add ${SVN_ADD:8}
    svn ci -m 'new release' ${SVN_ADD:8}
    if [ ! "$IS_TRUNK" ]
    then
        svn cp ${SVN_ADD:8} $TRUNK_URL/${SVN_ADD:8} -m 'copying to trunk'
    fi
done < <(svn st $STATS_DIR | grep '^\?')

