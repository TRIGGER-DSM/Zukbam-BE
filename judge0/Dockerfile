# Judge0 1.13.1은 Java가 OpenJDK 13뿐이라 OpenJDK 21(Temurin)을 같이 넣는다
FROM judge0/judge0:1.13.1

ARG JDK21_URL=https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.13.1%2B2/OpenJDK21U-jdk_x64_linux_hotspot_21.0.13.1_2.tar.gz
ARG JDK21_SHA256=bb8a93f9ae6e7a693b2b721e85dbeff26ae1a77447985a8d7afcdd9c7cf6222e

RUN curl -fsSL "$JDK21_URL" -o /tmp/jdk21.tar.gz \
    && echo "$JDK21_SHA256  /tmp/jdk21.tar.gz" | sha256sum -c - \
    && mkdir /usr/local/openjdk21 \
    && tar -xzf /tmp/jdk21.tar.gz -C /usr/local/openjdk21 --strip-components=1 \
    && rm /tmp/jdk21.tar.gz
