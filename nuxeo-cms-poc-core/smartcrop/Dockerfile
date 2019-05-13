FROM jjanzic/docker-python3-opencv
MAINTAINER Tiry <tdelprat@nuxeo.com>

RUN pip install git+https://github.com/tiry/python-smart-crop

VOLUME ["/in", '/out']

ENTRYPOINT ["smartcrop"]