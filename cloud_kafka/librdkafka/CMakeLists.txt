cmake_minimum_required(VERSION 3.13)

if (NOT CMAKE_TOOLCHAIN_FILE)
    set(CMAKE_TOOLCHAIN_FILE "${CMAKE_SOURCE_DIR}/vcpkg/scripts/buildsystems/vcpkg.cmake")
endif ()

set(CMAKE_EXPORT_COMPILE_COMMANDS ON)
project(librdkafka-demo CXX)

include_directories(${CMAKE_SOURCE_DIR}/dependencies/include)
find_library(SERDES_LIBRARY serdes PATHS ${CMAKE_SOURCE_DIR}/dependencies/lib)
if (SERDES_LIBRARY)
    message(STATUS "SERDES_LIBRARY: ${SERDES_LIBRARY}")
else ()
    message(FATAL_ERROR "serdes not found")
endif ()

find_library(AVRO_LIBRARY avro)
if (AVRO_LIBRARY)
    message(STATUS "AVRO_LIBRARY: ${AVRO_LIBRARY}")
else ()
    message(FATAL_ERROR "avro not found")
endif ()

find_package(RdKafka CONFIG REQUIRED)

add_executable(avro_producer avro_producer.cc)
target_link_libraries(avro_producer PRIVATE RdKafka::rdkafka ${SERDES_LIBRARY} ${AVRO_LIBRARY})
