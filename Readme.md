# 1. Build docker image

Used this builder because 21 is not supported yet

```dockerfile
FROM gradle:8.3.0-jdk17 AS builder
```

Also have to update build.gradle

```groovy
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
```

```sh
docker build -t xp-springboot-mysql .
```

# 2. Setup MySQL

```
helm install xp-mysql oci://registry-1.docker.io/bitnamicharts/mysql
```

Execute the following to get the administrator credentials:

```
MYSQL_ROOT_PASSWORD=$(kubectl get secret --namespace default xp-mysql -o jsonpath="{.data.mysql-root-password}" | base64 -d)
```

Run a pod that you can use as a client:

```
kubectl run xp-mysql-client --rm --tty -i --restart='Never' --image  docker.io/bitnami/mysql:8.4.4-debian-12-r4 --namespace default --env MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD --command -- bash
```

Connect to the MySQL pod

```
mysql -h xp-mysql.default.svc.cluster.local -uroot -p"$MYSQL_ROOT_PASSWORD"
```

```
CREATE DATABASE orders;

SHOW DATABASES;

USE orders;

CREATE TABLE orders (
    order_id      VARCHAR(50)     NOT NULL,
    order_amount  DECIMAL(10,2),
    description   VARCHAR(255),
    order_date    DATE,
    PRIMARY KEY (order_id)
);

INSERT INTO orders (order_id, order_amount) VALUES ('1234', 10.00);
```

To exit

```
exit
```

Exit from the pod bash

```
exit
```



Tip:

Watch the deployment status using the command: kubectl get pods -w --namespace default

Services:

echo Primary: xp-mysql.default.svc.cluster.local:3306

Execute the following to get the administrator credentials:

echo Username: root
MYSQL_ROOT_PASSWORD=$(kubectl get secret --namespace default xp-mysql -o jsonpath="{.data.mysql-root-password}" | base64 -d)

To connect to your database:

1. Run a pod that you can use as a client:

   kubectl run xp-mysql-client --rm --tty -i --restart='Never' --image  docker.io/bitnami/mysql:8.4.4-debian-12-r4 --namespace default --env MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD --command -- bash

2. To connect to primary service (read/write):

   mysql -h xp-mysql.default.svc.cluster.local -uroot -p"$MYSQL_ROOT_PASSWORD"
3. 
   z5ooSkHFa8



