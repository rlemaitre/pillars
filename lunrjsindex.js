var documents = [

{
    "id": 0,
    "uri": "user-guide/configuration.html",
    "menu": "User Guide",
    "title": "Configuration",
    "text": " Table of Contents Configuration Pillars Configuration Application Configuration Configuration Pillars is configured using YAML v1.2 files. Pillars Configuration Pillars configuration is structured as follows: pillars: name: Bookstore db: enabled: true host: localhost port: 5432 database: bookstore username: bookstore password: bookstore pool-size: 10 debug: false api: enabled: true host: 0.0.0.0 port: 9876 authToken: maxConnections: 1024 admin: enabled: true host: 0.0.0.0 port: 19876 authToken: maxConnections: 32 observability: enabled: true service-name: bookstore Application Configuration "
},

{
    "id": 1,
    "uri": "user-guide/overview.html",
    "menu": "User Guide",
    "title": "Overview",
    "text": " Table of Contents Overview Features Usage Dependencies Overview This library is a basis for backend applications written in Scala 3 using the TypeLevel stack. It is a work in progress and is not ready for production use. Features Admin server Configuration Database access Feature flags Logging OpenTelemetry-based observability Usage This library is currently available for Scala binary version 3.3.1. To use the latest version, include the following in your build.sbt : libraryDependencies ++= Seq( \"com.rlemaitre\" %% \"pillars\" % \"@VERSION@\" ) Dependencies Cats Cats collections Cats time Mouse Ip4s Cats Effect Fs2 Circe Decline Ciris Skunk Scribe Tapir Iron Http4s Otel4s mUnit "
},

{
    "id": 2,
    "uri": "search.html",
    "menu": "-",
    "title": "search",
    "text": " Search Results "
},

{
    "id": 3,
    "uri": "lunrjsindex.html",
    "menu": "-",
    "title": "null",
    "text": " will be replaced by the index "
},

];
