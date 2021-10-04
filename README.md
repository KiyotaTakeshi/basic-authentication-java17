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

## Adventure about the substance of cookies

access [localhost:8081/api/employees](http://localhost:8081/api/employees), cookie generated after success to login

```shell
$ file ~/Library/Application\ Support/Google/Chrome/Default/Cookies
/Users/t.kiyota/Library/Application Support/Google/Chrome/Default/Cookies: SQLite 3.x database, last written using SQLite version 3036000

$ sqlite3 ~/Library/Application\ Support/Google/Chrome/Default/Cookies
```

```sqlite
sqlite> .schema cookies
CREATE TABLE cookies(creation_utc INTEGER NOT NULL,top_frame_site_key TEXT NOT NULL,host_key TEXT NOT NULL,name TEXT NOT NULL,value TEXT NOT NULL,encrypted_value BLOB DEFAULT '',path TEXT NOT NULL,expires_utc INTEGER NOT NULL,is_secure INTEGER NOT NULL,is_httponly INTEGER NOT NULL,last_access_utc INTEGER NOT NULL,has_expires INTEGER NOT NULL DEFAULT 1,is_persistent INTEGER NOT NULL DEFAULT 1,priority INTEGER NOT NULL DEFAULT 1,samesite INTEGER NOT NULL DEFAULT -1,source_scheme INTEGER NOT NULL DEFAULT 0,source_port INTEGER NOT NULL DEFAULT -1,is_same_party INTEGER NOT NULL DEFAULT 0,UNIQUE (top_frame_site_key, host_key, name, path));

sqlite> select host_key,source_port,path,name from cookies where host_key like 'localhost%';

localhost|8000|/auth/realms/sample/|AUTH_SESSION_ID
localhost|8000|/auth/realms/sample/|AUTH_SESSION_ID_LEGACY
localhost|8000|/auth/realms/sample/|KEYCLOAK_IDENTITY
localhost|8000|/auth/realms/sample/|KEYCLOAK_IDENTITY_LEGACY
localhost|8080|/auth|JSESSIONID
localhost|8080|/auth/realms/master/|AUTH_SESSION_ID
localhost|8080|/auth/realms/master/|AUTH_SESSION_ID_LEGACY
localhost|8080|/auth/realms/master/|KEYCLOAK_IDENTITY
localhost|8080|/auth/realms/master/|KEYCLOAK_IDENTITY_LEGACY
localhost|8080|/|JSESSIONID
```
