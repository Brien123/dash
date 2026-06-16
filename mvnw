#!/bin/sh
# Maven Wrapper

APP_HOME="$(cd "$(dirname "$0")" && pwd)"

JAVACMD=${JAVA_HOME:+$JAVA_HOME/bin/java}
JAVACMD=${JAVACMD:-java}

MVNW_JAR="$APP_HOME/.mvn/wrapper/maven-wrapper.jar"
MVNW_URL=${MVNW_URL:-https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar}

if [ ! -f "$MVNW_JAR" ]; then
    echo "Downloading Maven Wrapper..."
    if command -v curl > /dev/null 2>&1; then
        curl -sSfL "$MVNW_URL" -o "$MVNW_JAR" || exit 1
    elif command -v wget > /dev/null 2>&1; then
        wget -q "$MVNW_URL" -O "$MVNW_JAR" || exit 1
    else
        echo "ERROR: curl or wget required"
        exit 1
    fi
fi

exec "$JAVACMD" \
    $MAVEN_OPTS \
    -classpath "$MVNW_JAR" \
    "-Dmaven.home=$APP_HOME" \
    "-Dmaven.multiModuleProjectDirectory=$APP_HOME" \
    org.apache.maven.wrapper.MavenWrapperMain "$@"
