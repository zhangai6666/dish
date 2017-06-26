
cd dish/
mv tools/apache-cassandra-3.0.10-bin.tar.gz ~/opt/
mv tools/elasticsearch-5.1.1.tar.gz ~/opt/
cd ~/opt/
tar -zvxf apache-cassandra-3.0.10-bin.tar.gz
tar -zvxf elasticsearch-5.1.1.tar.gz
apache-cassandra-3.0.10/bin/cassandra -f
Control + Z ?
elasticsearch-5.1.1/bin/elasticsearch


