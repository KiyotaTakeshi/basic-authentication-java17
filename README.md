# basic-authentication

## Run Application

- Check JDK version

```shell
export JAVA_HOME=`/usr/libexec/java_home -v 17`

$ java -version
openjdk version "17" 2021-09-14 LTS
OpenJDK Runtime Environment Corretto-17.0.0.35.2 (build 17+35-LTS)
OpenJDK 64-Bit Server VM Corretto-17.0.0.35.2 (build 17+35-LTS, mixed mode, sharing)
```

- Build

```shell
./mvnw clean package
```

- Run

```shell
java -jar target/basic-authentication-0.0.1-SNAPSHOT.jar
```

or

```shell
./mvnw spring-boot:run
```

## Test api

you can use [postman collection](./postman)

- public resource 

```shell
curl localhost:8081/api/greeting
hello, there%
```

- protected resource 

```shell
$ curl localhost:8081/api/employees
{"timestamp":"2021-10-01T01:47:15.737+00:00","status":401,"error":"Unauthorized","path":"/api/employees"}%
```

with Authorization header

```shell
# decode user information to token
$ echo -n "user:9c025395-5d88-4610-bef5-bb6949758e0c" | base64         
dXNlcjo5YzAyNTM5NS01ZDg4LTQ2MTAtYmVmNS1iYjY5NDk3NThlMG

curl --location --request GET 'http://localhost:8081/api/employees' \
--header 'Authorization: Basic dXNlcjo5YzAyNTM5NS01ZDg4LTQ2MTAtYmVmNS1iYjY5NDk3NThlMGM='
```
