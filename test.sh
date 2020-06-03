curl --data '{"credit" : 333}' \
       -v -X POST -H 'Content-Type:application/json' http://localhost:8080/users/create
curl --data '{"units" : 5}' \
       -v -X POST -H 'Content-Type:application/json' http://localhost:8080/stock/item/create/20
curl --data '{"orderTotal" : 0}' \
       -v -X POST -H 'Content-Type:application/json' http://localhost:8080/orders/create/1

