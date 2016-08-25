# Manager

Manager is a sample project to show off my Java, JavaScript, Hibernate and Spring knowledge.

### Version
1.0.0

### Technologies

Manager uses the following frameworks/libraries

#### Frontend

* [AngularJS] - Javascript frontend framework.
* [Twitter Bootstrap] - Responsive design and smart components.
* [Gulp] - UI build system.

#### Backend

* [Spring Framework 4] - IoC, MVC & Validation.
* [JPA 2.1] - Persistence API.
* [Hibernate 5] - ORM implementation of JPA 2.1.

### Caveats

I was experiencing this strange exception

```java
Caused by: java.lang.NullPointerException
	at java.util.Properties$LineReader.readLine(Properties.java:434)
	at java.util.Properties.load0(Properties.java:353)
	at java.util.Properties.load(Properties.java:341)
	at com.mysql.cj.jdbc.util.TimeUtil.loadTimeZoneMappings(TimeUtil.java:163)
	at com.mysql.cj.jdbc.util.TimeUtil.getCanonicalTimezone(TimeUtil.java:109)
	at com.mysql.cj.mysqla.MysqlaSession.configureTimezone(MysqlaSession.java:308)
	at com.mysql.cj.jdbc.ConnectionImpl.initializePropsFromServer(ConnectionImpl.java:2474)
	at com.mysql.cj.jdbc.ConnectionImpl.connectOneTryOnly(ConnectionImpl.java:1817)
	... 130 more
[INFO] Jetty server exiting.
```
Setting a **global timezone** in mysql server solved the issue (-3:00 is Argentina timezone)
```sql
SELECT @@global.time_zone, @@session.time_zone;
+--------------------+---------------------+
| @@global.time_zone | @@session.time_zone |
+--------------------+---------------------+
| SYSTEM             | SYSTEM              |
+--------------------+---------------------+
1 row in set (0.00 sec)

mysql> SET GLOBAL time_zone = '-3:00';
Query OK, 0 rows affected (0.03 sec)

mysql> SELECT @@global.time_zone, @@session.time_zone;
+--------------------+---------------------+
| @@global.time_zone | @@session.time_zone |
+--------------------+---------------------+
| -03:00             | SYSTEM              |
+--------------------+---------------------+
1 row in set (0.00 sec)
```

### Debug in eclipse

```sh
export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000"
```
   
   [Twitter Bootstrap]: <http://twitter.github.com/bootstrap/>
   [AngularJS]: <http://angularjs.org>
   [Gulp]: <http://gulpjs.com>
   [Spring Framework 4]: <https://projects.spring.io/spring-framework/>
   [Hibernate 5]: <http://hibernate.org/orm/>
   [JPA 2.1]: <https://jcp.org/en/jsr/detail?id=338>