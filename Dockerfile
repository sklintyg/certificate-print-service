ARG from_image
FROM $from_image

ARG from_image
ARG project_name
ARG artifact
ARG artifact_name
ARG artifact_version
ARG vcs_url
ARG vcs_ref

LABEL se.inera.from_image=${from_image} \
      se.inera.project=${project_name} \
      se.inera.artifact=${artifact} \
      se.inera.artifact_name=${artifact_name} \
      se.inera.version=${artifact_version} \
      se.inera.vcs_url=${vcs_url} \
      se.inera.vcs_ref=${vcs_ref}

ENV APP_NAME=$artifact
ENV SCRIPT_DEBUG=true
ENV XDG_CONFIG_HOME=/tmp/.chromium
ENV XDG_CACHE_HOME=/tmp/.chromium
ENV CHROME_BIN=/usr/bin/google-chrome
ENV CHROME_PATH=/usr/bin/google-chrome

# On update of playwright version, find supported stable chrome version here
# https://playwright.dev/docs/release-notes
# Then find latest minor of supported chrome version here:
# https://www.ubuntuupdates.org/package/google_chrome/stable/main/base/google-chrome-stable
ENV CHROME_VERSION 147.0.7727.137-1

USER root

RUN apt-get update \
    && apt-get install -y wget fonts-liberation ca-certificates --no-install-recommends \
    && wget -q -O /tmp/google-chrome-stable.deb "https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_${CHROME_VERSION}_amd64.deb" \
    && apt-get install -y /tmp/google-chrome-stable.deb \
    && rm -f /tmp/google-chrome-stable.deb \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY app/build/libs/app.jar /deployments/app.jar

USER nobody
