### About

This folder contains the Docker file to build the Docker image that allows Nuxeo to run the [python-smart-crop](https://github.com/epixelic/python-smart-crop).

The goal is to leverage [OpenCV](https://opencv.org/) to do Smart Crop.

### Building

Just build and deploy the DockerFile:

    docker build -t nuxeo/smartcrop:1.0 -t nuxeo/smartcrop:latest

You can also simply run the `build.sh` script (that does just that).

### Nuxeo integration

The Nuxeo integration is done via a Contribution to the `CommandLineService` and then a contribution to the `ConverterService`.

Because Docker volume system does not play well with some low level optimizations that create symbolic links under Linux (rather than copy), a custom executer is used.

The contributed converter is called `smartCrop` and takes 2 paramater `W` and `H`.

The UnitTest should detect if the Docker image is not installed and the test should be skipped.


