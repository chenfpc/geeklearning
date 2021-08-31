##按照前缀删除redis的key
./redis-cli  --raw keys "test*" | xargs ./redis-cli del