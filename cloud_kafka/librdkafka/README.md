# librdkafka examples

## How to build

First, install vcpkg for dependency management.

```bash
git clone https://github.com/microsoft/vcpkg.git
./vcpkg/bootstrap-vcpkg.sh
```

To access the schema registry, you need to install `libserdes` manually if you're not using Confluent Platform. However, building this library is very complicated, please follow the instructions below to build it.

```bash
cd third_party
cmake -B build
export TRIPLET=$(cmake -B build | grep "VCPKG_TARGET_TRIPLET" | sed 's/^.*: //')
export ROOT=$PWD/build/vcpkg_installed/$TRIPLET
export CPPFLAGS="-I$ROOT/include"
export LDFLAGS="-L $ROOT/lib -lssl -lcrypto -llz4"
# If your OS is macOS, you need to set the following LDFLAGS
#export LDFLAGS="-L $ROOT/lib -lssl -lcrypto -llz4 -framework CoreFoundation -framework CoreServices -framework SystemConfiguration"

cd ..
git clone https://github.com/confluentinc/libserdes.git
cd libserdes
./configure --prefix=$ROOT
make -j8
make install
export CPPFLAGS=
export LDFLAGS=
```

After that, the libserdes library will be installed in the `./dependencies` directory like:

```
dependencies
├── include
│   └── libserdes
│       ├── serdes-avro.h
│       ├── serdes-common.h
│       └── serdes.h
└── lib
    ├── libserdes.1.dylib
    ├── libserdes.a
    ├── libserdes.dylib -> libserdes.1.dylib
    └── pkgconfig
        ├── serdes-static.pc
        └── serdes.pc
```

## Build the examples

After building the dependencies, you can just build and run examples with the following command:

```bash
cmake -B build
cmake --build build
```

A source file `xxx.cc` will be built into an executable `xxx` in the `build` directory.

