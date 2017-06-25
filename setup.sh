sudo yum -y update
sudo yum -y install java-devel
#sudo yum install git

cd /usr/local/src
sudo wget http://download.redis.io/releases/redis-3.2.9.tar.gz
sudo tar xzf redis-3.2.9.tar.gz
cd redis-3.2.9
sudo make distclean
sudo make
sudo yum install -y tcl
sudo mkdir /etc/redis
sudo mkdir /var/lib/redis
sudo cp src/redis-server src/redis-cli /usr/local/bin/
sudo cp redis.conf /etc/redis/
sudo vim /etc/redis/redis.conf
#daemonize yes
#bind 127.0.0.1
#dir /var/lib/redis
cd /tmp
wget https://raw.github.com/saxenap/install-redis-amazon-linux-centos/master/redis-server
sudo mv redis-server /etc/init.d
sudo chmod 755 /etc/init.d/redis-server
sudo vim /etc/init.d/redis-server
#redis="/usr/local/bin/redis-server"
sudo chkconfig --add redis-server
sudo chkconfig --level 345 redis-server on
sudo service redis-server start
sudo vi /etc/systctl.conf
## ensure redis background save issue
#vm.overcommit_memory = 1
#systctl vm.overcommit_memory=1
