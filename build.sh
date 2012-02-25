cd "$(dirname "$(type -p "$0")")"

mvn -q clean
mvn install

cp App/target/App-*-jar-with-dependencies.jar Dacca.jar 
