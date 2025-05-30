FROM blackducksoftware/hub-docker-common:1.0.7 AS docker-common
FROM rabbitmq:3.13-alpine

ARG VERSION
ARG COMMITHASH

LABEL com.blackducksoftware.integration.alert.vendor="Black Duck Software, Inc." \
      com.blackducksoftware.integration.alert.version="$VERSION" \
      com.blackducksoftware.integration.alert.commitHash="$COMMITHASH"

ENV BLACKDUCK_HOME="/opt/blackduck"
ENV APPLICATION_NAME="rabbitmq"
ENV APPLICATION_HOME="$BLACKDUCK_HOME/$APPLICATION_NAME"
ENV RABBITMQ_HOME="/etc/rabbitmq"
ENV SECURITY_DIR=$APPLICATION_HOME/security
ENV CERTIFICATE_MANAGER_DIR=$APPLICATION_HOME/bin

RUN set -ex \
    && mkdir -p -m 775 "$APPLICATION_HOME/bin" "$APPLICATION_HOME/security" "$APPLICATION_HOME/logs" \
    && apk update \
    && apk add --no-cache --virtual .run-deps \
           curl \
           jq \
           openssl

COPY docker-entrypoint.sh generate-config.sh $APPLICATION_HOME/bin/
COPY --from=docker-common healthcheck.sh /usr/local/bin/docker-healthcheck.sh
COPY --from=docker-common certificate-manager.sh "$CERTIFICATE_MANAGER_DIR/certificate-manager.sh"
COPY --from=docker-common java.security "$SECURITY_DIR/java.security"

VOLUME [ "$APPLICATION_HOME/logs" ]

ENTRYPOINT [ "/opt/blackduck/rabbitmq/bin/docker-entrypoint.sh" ]
