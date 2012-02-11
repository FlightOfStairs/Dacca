!#/bin/sh


mvn -f Common/pom.xml install
mvn -f Capture/pom.xml install
mvn -f Analysis/pom.xml install
mvn -f Display/pom.xml install
mvn -f App/pom.xml install

