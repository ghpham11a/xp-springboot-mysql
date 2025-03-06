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
$MYSQL_ROOT_PASSWORD = [System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String( (kubectl get secret --namespace default xp-mysql -o jsonpath="{.data.mysql-root-password}") ))
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
