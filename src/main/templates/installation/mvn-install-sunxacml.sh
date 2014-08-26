#!/bin/bash
mvn install:install-file -Dfile=$(dirname $0)/../lib/sunxacml-2.0-M2-SNAPSHOT.jar \
			   -DgroupId=sunxacml \
			   -DartifactId=sunxacml \
			   -Dversion=2.0-M2 \
			   -Dpackaging=jar \
			   -DgeneratePom=true
