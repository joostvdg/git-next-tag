FROM maven:3.8.7-amazoncorretto-19

# GraalVM
ARG TARGETPLATFORM
ARG BUILDPLATFORM
ARG GRAAL_JAVA_VERSION=19
ARG GRAAL_VERSION=22.3.0
ARG INSTALL_PKGS="gzip git gcc"
ENV GRAALVM_HOME /opt/graalvm
ENV JAVA_HOME /opt/graalvm

RUN yum install -y ${INSTALL_PKGS}

RUN echo "export GRAAL_PLATFORM=${TARGETPLATFORM:0:5};export GRAAL_ARCH=${TARGETPLATFORM:6};" >> /envfile
RUN . /envfile; echo "GRAAL_PLATFORM=$GRAAL_PLATFORM, GRAAL_ARCH=$GRAAL_ARCH"
  # for some reason the GraalVM folks use aarch64 instead of arm64 like the rest of us
RUN sed -i 's/arm64/aarch64/g' /envfile ## https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.3.0/graalvm-ce-java19-linux-amd64-22.3.0.tar.gz
RUN . /envfile; echo "export GRAAL_CE_URL=https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-${GRAAL_VERSION}/graalvm-ce-java${GRAAL_JAVA_VERSION}-${GRAAL_PLATFORM}-${GRAAL_ARCH}-${GRAAL_VERSION}.tar.gz;" >> envfile
RUN . /envfile; echo "GRAAL_PLATFORM=$GRAAL_PLATFORM, GRAAL_ARCH=$GRAAL_ARCH, GRAAL_CE_URL=${GRAAL_CE_URL}"
run . /envfile; curl "${GRAAL_CE_URL}"

RUN mkdir -p ${GRAALVM_HOME} && cd ${GRAALVM_HOME} && \
    . /envfile; curl -fsSL "${GRAAL_CE_URL}" | tar -xzC ${GRAALVM_HOME} --strip-components=1

RUN yum clean all && rm -rf /var/cache/yum
RUN . /envfile; rm -f /tmp/graalvm-ce-${GRAAL_ARCH}.tar.gz 

ENV PATH $GRAALVM_HOME/bin:$PATH
RUN gu install native-image

#COPY pom.xml .
#RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline
#COPY . .
#
#RUN --mount=type=cache,target=/root/.m2 mvn package -Dpackaging=native-image -e --show-version
#RUN chmod +x target/git-next-tag
#RUN target/git-next-tag -v -b "v0.1.*"

#RUN --mount=type=cache,target=/root/.m2 mvn package -e --show-version
#RUN java -jar target/git-next-tag-0.1.jar -v -b "v0.1.*"