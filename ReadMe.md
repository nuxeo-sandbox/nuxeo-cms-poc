
### About

#### Goal

The goal of this repository is to provide example code to add some Web CMS features to a Nuxeo Server.

#### Features

**Dynamic Renditions**

The idea is to be able to configure and create a Rendition from a REST URL, a typically example being:

    GET /nuxeo/api/v1/{doc_uuid}/@cmsAdapter/dynamicCrop?converter=pictureCrop&width=300@height=300

This feature implementation is broken into 2 sub modules:

 - [nuxeo-cms-poc-core](nuxeo-cms-poc-core/) that implements the Core features
    - Rendition and storage logic
 - [nuxeo-cms-poc-jaxrs](nuxeo-cms-poc-jaxrs/) that implements the REST Bindings
 	- adapter and webobject

See ReadMe files in submodules for me details.

**Page Fragments**

TBD    

### Building

Build without tests

    mvn -DskipTests clean install

Build with the tests

    mvn clean install

Test coverage

    mvn cobertura:cobertura


### Support

**These features are sand-boxed and not yet part of the Nuxeo Production platform.**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.

### Licensing

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

### About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris.

More information is available at [www.nuxeo.com](http://www.nuxeo.com).