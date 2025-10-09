FROM tomee:10.1.2-jre21-alpine-webprofile

# Install unzip if not already available
RUN apk add --no-cache unzip curl

# Ensure webapps directory is clean and create the ROOT directory
RUN rm -rf /usr/local/tomee/webapps/* && mkdir -p /usr/local/tomee/webapps/ROOT

# Copy the WAR file, unzip it to ROOT directory, and then remove the WAR
COPY target/primefaces-showcase-*.war /tmp/primefaces-showcase.war
RUN unzip -q /tmp/primefaces-showcase.war -d /usr/local/tomee/webapps/ROOT && rm /tmp/primefaces-showcase.war

ENV CATALINA_OPTS="-Xms512m -Xmx1024m"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/jakarta.faces.resource/components.css.xhtml?ln=primefaces || exit 1

CMD ["catalina.sh", "run"]