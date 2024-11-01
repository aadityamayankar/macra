**I've set the flyway locations using the filesystem path since dataacess module doesn't create a build artifact**

The command to run clean and migrate is as follows:
```shell
mvn clean flyway:clean flyway:migrate -Dflyway.configFiles=flyway.conf
```