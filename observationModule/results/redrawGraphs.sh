#!/bin/bash
SYSTEM_NAME=$1
BACKUP=$2
BACKUP_DIR="";
if [[ $BACKUP == "-b" ]]; then
	BACKUP_DIR="${SYSTEM_NAME}_BACKUP_$(date +"%H-%d%m%Y")"
	mkdir $BACKUP_DIR
else
	echo "Generating charts without backing up."
fi
for DIR in ${SYSTEM_NAME}/*/
do
#	echo $DIR
	if [[ $BACKUP == "-b" ]]; then
		mkdir $BACKUP_DIR/$(basename $DIR)
		cp ${DIR}charts/*.pdf $BACKUP_DIR/$(basename $DIR)/
	fi
#	rm ${DIR}charts/*.pdf
done
if [[ $BACKUP == "-b" ]]; then
	zip -r $BACKUP_DIR.zip $BACKUP_DIR
fi 
#sh plotDetectedVReal.sh $SYSTEM_NAME
