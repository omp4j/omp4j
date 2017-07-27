FROM pritunl/archlinux
MAINTAINER Petr Belohlavek <omp4j@petrbel.cz>

# install required environment
RUN pacman --noconfirm -S git \
                          jdk8-openjdk \
                          sbt
