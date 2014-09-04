irond
=====

This package contains an *experimental* MAP server based on JAVA.
Pre-configured and "ready to run" binary packages are available
in the [release section][4].
The server is based on the [IF-MAP 2.2][1] specification.

irond supports both basic authentication and certificate-based
authentication (using X.509 certificates) of MAP clients.

Development was started within the IRON project at Fachhochschule
Hannover (Hannover University of Applied Sciences and Arts).

[![Build Status](https://travis-ci.org/trustathsh/irond.png)](https://travis-ci.org/trustathsh/irond)


Configuration
=============

General configuration can be done through the `ifmap.properties`
file. For example, which keystore and truststore file should be
used, as well as the ports to be used for basic and
certificate-based authentication. The provided `ifmap.properties`
lists commented set of configuration options. As irond may decide
to recreate the configuration file without the comments, those
comments are backed up in the file `ifmap.properties.orig`.


Client Authentication
=====================

## Basic Authentication:

Username/password combinations for basic authentication MAP clients
can be configured using a properties file. The file to be used
is set to be set in the `ifmap.properties` file. By default the file
`basicauthusers.properties` is used. The format is `<user>:<password>`.

## Certificate-based Authentication:

Certificates of the allowed MAP clients need to be added to the used
keystore. In this package, the certificates need to be added to
the file `irond.jks`, which is located in the directory keystore.
The [keytool][2] program provided by JAVA may be used for this purpose.


publisher-id Mapping
====================

The file `publisher.properties` is used to map a MAPC identification
to publisher-ids. Entries in this file are of the form
`<client identification>=<publisher-id>`.
The `<client identification>` field either represents the username used
for basic authentication, or the common name of the client's certificate,
used during certificate-based authentication.

If no mapping of a MAPC identification to a publisher-id can be found,
an entry is created. This entry can be freely changed later on to
modify the publisher-id a MAPC gets assigned.


Authorization of MAPC
=====================

irond supports two basic types of MAPCs. Either read-only or read-write
MAPCs.
The file `authorization.properties` is used to restrict MAPCs to read-only
operations. The entries of this file need to be in the form
`<client identification>=[ro|rw]`. The `<client identification>` is the as
used in the `publisher.properties` file.

**Note:**
If no entry for a given MAPC is given, this MAPC is allowed to do both,
read and write operations.
However, if an entry exists, but is set to any other value then `ro` or
`rw`, the MAPC is restricted to read-only operations.


Building
========
Just run
    $ mvn package
as Maven will install the dependency to the [SunXACML implementation][5] on its own before
compiling.


Running
=======
The server was developed using JAVA 1.6. It is therefore
recommended to test it with JAVA 1.6. The `log4j.properties`
file can be used to reduce the output. However, as of the
experimental state the server is currently in, it makes sense
to leave it on `TRACE`.

The server can be started with the following command:

	$ java -jar irond.jar

or by using the contained start scripts.


Testing
=======
Two simple soapUI projects for testing purposes are included in
the soapui-examples folder:

`soapuiIfmap.xml`: simply runs a new session request, publishes
some metadata, runs a search for the published metadata, and ends
by calling end session.

`irond-demo-soapui-project.xml`: cotains a more complex example
where a 802.1X AR connects to a network.


Feedback
========
If you have any questions, problems or comments, please contact
	trust@f4-i.fh-hannover.de


LICENSE
=======
irond is licensed under the [Apache License, Version 2.0][3].


Notes on Sun XACML
==================
The original Sun XACML implementation in version `2.0-M2-SNAPSHOT` was changed to not
include an own log4j properties file.
The altered jar-file was then handled by a Python script by [Nikita Volkov][6] to
generate a local Maven repository for irond.
It installs in the users Maven repository (~/.m2/repository) as `net/sf/sunxacml/sunxacml/2.0-M2.trustathsh-SNAPSHOT/`.
The license of the original Sun XACML implementation is attached as `LICENSE.SUNXACML.txt`.


[1]: http://www.trustedcomputinggroup.org/files/static_page_files/FF3CB868-1A4B-B294-D093D8383D733B8A/TNC_IFMAP_v2_2r9.pdf
[2]: http://download.oracle.com/javase/6/docs/technotes/tools/windows/keytool.html
[3]: http://www.apache.org/licenses/LICENSE-2.0.html
[4]: https://github.com/trustathsh/irond/releases
[5]: http://sourceforge.net/projects/sunxacml/files/maven/snapshots/net/sf/sunxacml/sunxacml/2.0-M2-SNAPSHOT/
[6]: https://github.com/nikita-volkov/install-to-project-repo