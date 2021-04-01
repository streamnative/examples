# python-function

This is an example of how to use the python function.

## Project Structure

```
python_function
├── __init__.py
├── custom_object_function.py
└── pyserde
    ├── __init__.py
    └── serde.py
```

In python, if a folder contains `__init__` file, we call it a package, python_function and pyserde are both packages. Compressing `python_function` directory will generate `python_function.zip`.

## How to use

### Start pulsar standalone

```
docker run -d -it \
    -p 6650:6650 \
    -p 8080:8080 \
    --name pulsar-standalone \
    apachepulsar/pulsar-all:2.7.1 \
    bin/pulsar standalone
```

### Start python function by use zip package
```
./bin/pulsar-admin functions create \
    --tenant public   --namespace default   --name my_function \
    --py /YOUR-PATH/python_function.zip \
    --classname python_function.custom_object_function.CustomObjectFunction \
    --custom-serde-inputs '{"input-topic-1":"python_function.pyserde.serde.CustomSerDe","input-topic-2":"python_function.pyserde.serde.CustomSerDe"}' \
    --output-serde-classname python_function.pyserde.serde.CustomSerDe \
    --output output-topic-3
```
Replace `YOUR-PATH` with your file path.

### Run produce and consume

```
python test-producer-consumer.py
```