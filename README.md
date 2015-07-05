###Current stack:
- Play 2.4.0
- Play-Slick 1.0.0 (uses Slick 3.0.0)
- Scaldi-Play 0.5.8
- Play-Silhouette 3.0.0-RC1
- SBT: 1.3.8
- Scala: 2.11.6
- Angular: 1.2.15

How to run
--------------------------------------------------

* This application uses in-memory database as main database and as a database for tests, so be aware that all users are lost on restart

* To install javascript part:
```
npm install
bower install
gulp deps_dev deps_test less
```

* Run the application:
```
sbt run
```

* test the application (server side):
```
sbt test
```

* test the application (client side):
```
karma test
```

Please report if there is any issue with running this seed, thanks!
