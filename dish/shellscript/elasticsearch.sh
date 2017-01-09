

mkdir -p ~/opt/packages && cd $_
curl -O   https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-5.1.1.tar.gz
gzip -dc  elasticsearch-5.1.1.tar.gz  | tar xf -
cd elasticsearch-5.1.1
bin/elasticsearch
