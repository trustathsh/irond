To build the current version of irond, the latest snapshot of sunxacml-2.0 is
needed. You can get this file from [1]. To have maven add this file to its
repository, execute the following command:

  mvn install:install-file -Dfilepath/to/sunxacml-2.0-M2-SNAPSHOT.jar \
			   -DgroupId=sunxacml \
			   -DartifactId=sunxacml \
			   -Dversion=2.0-M2 \
			   -Dpackaging=jar \
			   -DgeneratePom=true

[1] http://sourceforge.net/projects/sunxacml/files/maven/snapshots/net/sf/sunxacml/sunxacml/2.0-M2-SNAPSHOT/
