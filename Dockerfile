#######################
## BUILDER
FROM ghcr.io/joostvdg/maven-graal-ce-build:0.4.0-m3.8-j19-g22.3 AS build
WORKDIR /code
COPY build-settings.xml /code/
COPY pom.xml /code/
RUN --mount=type=cache,target=/code/maven-repo/ \
    mvn dependency:go-offline -e -C -B --show-version --settings=/code/build-settings.xml
COPY src /code/src
RUN --mount=type=cache,target=/code/maven-repo/ \
    mvn package -Dpackaging=native-image -e -C -B --show-version --settings=/code/build-settings.xml
RUN chmod +x target/git-next-tag
RUN target/git-next-tag -v -b "v0.1.*"
###################
## Runner
#FROM quay.io/quarkus/quarkus-micro-image:2.0
#FROM registry.access.redhat.com/ubi8/ubi-minimal:8.6
FROM alpine/git:v2.36.3
LABEL org.opencontainers.image.source = "https://github.com/joostvdg/git-next-tag"
WORKDIR /work/

RUN adduser -D gitnexttag

RUN chown gitnexttag /work \
    && chmod "g+rwX" /work \
    && chown gitnexttag:root /work

COPY --from=build --chown=gitnexttag:root /code/target/git-next-tag /work/application

USER gitnexttag
RUN git config --global safe.directory '*'

ENTRYPOINT ["./application"]
CMD ["--help"]
##