
# Usage

```
GET http://localhost:8080/api/accounts/1
```

```
POST http://localhost:8080/api/accounts
{
  "email": "john.doe@example.com",
  "dateOfBirth": "1985-07-13",
  "accountNumber": "ACCT-12345-XYZ",
  "balance": 1234.56,
}
```

Check Kafka

```
GET http://localhost:8080/api/kafka-events/stats
GET http://localhost:8080/api/kafka-events/topic/account_created
```

# Helpful commands

```
# Encode string value in Powershell
[Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes('secret-value'))

# Decode string value in Powershell
[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String('bXlwYXNzd29yZDE='))
```


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

# 2. Setup MySQL Database

Make sure any associated PVCs and PVs are deleted first

```
helm install xp-mysql oci://registry-1.docker.io/bitnamicharts/mysql
```

Execute the following to get the administrator credentials:

Bash

```
MYSQL_ROOT_PASSWORD=$(kubectl get secret --namespace default xp-mysql -o jsonpath="{.data.mysql-root-password}" | base64 -d)
```

Powershell

```
$MYSQL_ROOT_PASSWORD = [System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String( (kubectl get secret --namespace default xp-mysql -o jsonpath="{.data.mysql-root-password}")))
```

Run a pod that you can use as a client:

```
kubectl run xp-mysql-client --rm --tty -i --restart='Never' --image  docker.io/bitnami/mysql:8.4.4-debian-12-r4 --namespace default --env MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD --command -- bash
```

Connect to the MySQL pod. Fill in the $MYSQL_ROOT_PASSWORD part

```
mysql -h xp-mysql.default.svc.cluster.local -uroot -p"$MYSQL_ROOT_PASSWORD"
```

Create a user that will be the user to give to the JDBC. Note this is the user and password that will be set in dev-secrets.yaml.

```
CREATE USER 'appuser'@'%' IDENTIFIED BY 'mypassword1';
GRANT ALL PRIVILEGES ON my_database.* TO 'appuser'@'%';
FLUSH PRIVILEGES;
```

```
SHOW DATABASES;

USE my_database;

CREATE TABLE Accounts (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Email VARCHAR(50) NOT NULL,
    DateOfBirth DATE,
    AccountNumber VARCHAR(20) UNIQUE,
    Balance DECIMAL(18, 2) DEFAULT 0.00,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP
);

SHOW TABLES;

INSERT INTO Accounts (Email, DateOfBirth, AccountNumber, Balance)
VALUES 
('john.doe@example.com', '1985-06-15', 'ACC123456', 1000.00),
('jane.smith@example.com', '1990-09-25', 'ACC654321', 2500.50),
('alice.jones@example.com', '1978-12-05', 'ACC789012', 150.75);
```

To exit

```
exit
```

Exit from the pod bash

```
exit
```

# 3.  Setup Kafka

Install Kafka Helm chart

```
helm install xp-kafka oci://registry-1.docker.io/bitnamicharts/kafka
```

To check that pods were spun up

```
kubectl get pods --selector app.kubernetes.io/instance=xp-kafka
```

To create the topics, we will start another pod that goes into the Kafka pods and creates the topics and shuts down. To do this, we need to find the Kafka password and update it's value in the dev-kafka-topics-admin.yaml.

```
# This gets an encoded value
kubectl get secret xp-kafka-user-passwords -o jsonpath='{.data.client-passwords}'

# we need the decoded version to put into the yaml
# Powershell
[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String((kubectl get secret xp-kafka-user-passwords -o jsonpath='{.data.client-passwords}')))

# bash
kubectl get secret xp-kafka-user-passwords -o jsonpath='{.data.client-passwords}' | base64 -d
```

Get the password and update the password field in dev-kafka-topics-admin.yaml

```
... required username=\"user1\" password=\"bFHKRJA2Y5\";" >> /tmp/kafka-client.properties ...
```

Then just run this command which applies the yaml updates

```
kubectl apply -f dev-kafka-topics-admin.yaml
```

# 4. Setup Redis

Install Redis Helm chart

```
helm install xp-redis oci://registry-1.docker.io/bitnamicharts/redis
```

Optional: connect to Redis

```
# Store the password in Powershell
$REDIS_PASSWORD = [System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String((kubectl get secret --namespace default xp-redis -o jsonpath="{.data.redis-password}")))

# Store the password in Bash
export REDIS_PASSWORD=$(kubectl get secret --namespace default xp-redis -o jsonpath="{.data.redis-password}" | base64 -d)

kubectl run --namespace default redis-client --restart='Never' --env REDIS_PASSWORD=$REDIS_PASSWORD  --image docker.io/bitnami/redis:7.4.2-debian-12-r4 --command -- sleep infinity

kubectl exec --tty -i redis-client --namespace default -- bash

redis-cli -h xp-redis-master -p 6379

AUTH [REDIS_PASSWORD]
```

# 5. Clean up

```
kubectl delete deployment xp-springboot-mysql
```