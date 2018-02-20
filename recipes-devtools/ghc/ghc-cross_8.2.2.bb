DESCRIPTION = "The Glorious Glasgow Haskell Compilation System"
PN = "ghc-cross"
PV = "8.2.2"
PR = "r0"

LICENSE = "GHC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7cb08deb79c4385547f57d6bb2864e0f"

DEPENDS = "gmp-native gcc-cross-${TARGET_ARCH} binutils-cross-${TARGET_ARCH}"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI = "\
    https://downloads.haskell.org/~ghc/${PV}/ghc-${PV}-src.tar.xz \
    file://0001-derive-constants-split-cc.patch \
"

SRC_URI[md5sum] = "cd25f62e85f4e0343fd4655bbd52f3e7"
SRC_URI[sha256sum] = "bb8ec3634aa132d09faa270bbd604b82dfa61f04855655af6f9d14a9eedc05fc"

S = "${WORKDIR}/ghc-${PV}"

inherit pkgconfig autotools-brokensep perlnative

# GHC has its own aclocal file already, we want to keep it
EXTRA_AUTORECONF += " --exclude=aclocal "

# There is a bug in ld.bfd that GHC checks for. Need to use gold.
REQUIRED_DISTRO_FEATURES += "${@bb.utils.contains('HOST_ARCH', 'arm', 'ld-is-gold', '', d)}"

# The autodetection in GHC doesn't work, pass some values explicitly here
EXTRA_OECONF += "\
    --host=${BUILD_SYS} \
    --with-nm=${NM} \
    --with-ar=${AR} \
    --with-objdump=${OBJDUMP} \
    --with-ranlib=${RANLIB} \
"

# Simulate the autoreconf part of the boot script. Otherwise we end up with
# a bunch of libraries that hasn't been autoconf'ed yet.
do_configure_append() {
    for LIB in ${S}/libraries/*; do
        if [ -f ${LIB}/configure.ac ]; then
            cd ${LIB}
            autoreconf -ivf
        fi
    done;

    if ! [ -f ${S}/mk/build.mk ]; then
        echo "include mk/flavours/perf-cross.mk" >> ${S}/mk/build.mk
    fi
}


# TODO: configure.ac
# +CC_ORIG="$CC"
# +CC="`echo $CC_ORIG | awk '{ print $1 }'`"
# +CFLAGS="$CFLAGS `echo $CC_ORIG | cut -d' ' -f 2-`"
# +
# +echo "Tobsan CC is $CC"
# +echo "Tobsan CFLAGS is $CFLAGS"
#
# TODO: Find out how to disable GHCi, we have no use for it in a cross-compiler
#
