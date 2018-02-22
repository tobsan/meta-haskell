FROM crops/yocto:ubuntu-16.04-base

LABEL description="Meta-Haskell Build Environment"
LABEL maintainer="tobsan@tobsan.se"

USER root

# Install dependencies in one command to avoid potential use of previous cache
# like explained here: https://stackoverflow.com/a/37727984
RUN apt-get update && apt-get install -y \
        openssh-server \
        inetutils-ping \
        iptables \
        cvs \
        subversion \
        coreutils \
        python3-pip \
        libfdt1 \
        python-pysqlite2 \
        help2man \
        libxml2-utils \
        libsdl1.2-dev \
        graphviz \
        qemu-user \
        g++-multilib \
        curl \
        repo \
        rsync

# Remove all apt lists to avoid build caching
RUN rm -rf /var/lib/apt/lists/*

# en_US.utf8 is required by Yocto sanity check
RUN localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8
RUN echo 'export LC_ALL="en_US.UTF-8"' >> /etc/profile
ENV LANG en_US.utf8

# SSH settings
RUN mkdir /var/run/sshd
EXPOSE 22
CMD  ["/usr/sbin/sshd", "-D"]
