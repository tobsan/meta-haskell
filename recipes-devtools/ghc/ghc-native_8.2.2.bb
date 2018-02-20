DESCRIPTION = "The Glorious Glasgow Haskell Compilation System"
PV = "8.2.2"
PR = "r0"

LICENSE = "GHC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7cb08deb79c4385547f57d6bb2864e0f"

DEPENDS = "gmp-native"

SRC_URI = "https://downloads.haskell.org/~${BPN}/${PV}/${BPN}-${PV}-src.tar.xz"
SRC_URI[md5sum] = "cd25f62e85f4e0343fd4655bbd52f3e7"
SRC_URI[sha256sum] = "bb8ec3634aa132d09faa270bbd604b82dfa61f04855655af6f9d14a9eedc05fc"

S = "${WORKDIR}/${BPN}-${PV}"

inherit native pkgconfig autotools-brokensep perlnative

# GHC has its own aclocal file already, we want to keep it
EXTRA_AUTORECONF += " --exclude=aclocal " 

# The autodetection in GHC doesn't work, and either Yocto or GHC appends a
# space to CC and LD variables by default. Passing them like this fixes the
# issue.
EXTRA_OECONF += "\
    CC=${CC} \
    LD=${LD} \ 
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

    echo "include mk/flavours/quick.mk" >> ${S}/mk/build.mk
}
