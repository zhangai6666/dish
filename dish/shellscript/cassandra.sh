

mkdir -p ~/opt/packages && cd $_
curl -O http://www.eu.apache.org/dist/cassandra/3.0.10/apache-cassandra-3.0.10-bin.tar.gz
gzip -dc apache-cassandra-3.0.10-bin.tar.gz | tar xf -
ln -s ~/opt/packages/apache-cassandra-3.0.10 ~/opt/cassandra
mkdir -p ~/opt/cassandra/data/data
mkdir -p ~/opt/cassandra/data/commitlog
mkdir -p ~/opt/cassandra/data/saved_caches
mkdir -p ~/opt/cassandra/logs

cd ~/opt/packages/apache-cassandra-3.0.10
bin/cassandra  -f


