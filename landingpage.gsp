<div class="hero">
    <h1 class="hero__title">Pillars</h1>
    <p class="hero__description">Build your backend service in Scala 3 the easy way</p>
</div>
<div class="hero__terminal">
    <pre>
        <code class="language-scala">
import ... // import your dependencies

object Main extends pillars.EntryPoint:
    def app: pillars.App[IO] = new pillars.App[IO]:
        def name        = Name("BookStore")
        def version     = Version("0.0.1")
        def description = Description("A simple bookstore")

        def run(pillars: Pillars[IO]): IO[Unit] =
            import pillars.*
            for
                _ <- logger.info(s"📚 Welcome to \${pillars.config.name}!")
                _ <- pillars.whenEnabled(FeatureFlag.Name("feature-1")):
                    pillars.db.use: s =>
                        for
                            d <- s.unique(sql"select now()".query(timestamptz))
                            _ <- logger.info(s"The current date is \$d.")
                        yield ()
                _ <- pillars.apiServer.start(endpoints.all)
            yield ()
        </code>
    </pre>
</div>
<div class="wrapper">
    <div class="installation">
        <h3 class="section__title">Installation</h3>
        <div class="tab__container">
            <ul class="tab__menu">
                <li class="tab active" data-tab="sbt">sbt</li>
                <li class="tab" data-tab="mill">mill</li>
                <li class="tab" data-tab="scala-cli">scala-cli</li>
                <li class="tab" data-tab="pants">pants</li>
                <li class="tab" data-tab="gradle">Gradle</li>
                <li class="tab" data-tab="maven">Maven</li>
            </ul>
            <pre class="code tab__pane active sbt">
                <code class="highlight language-scala">libraryDependencies ++= Seq("com.rlemaitre" %% "pillars-core" % "0.0.1")</code>
            </pre>
            <pre class="code tab__pane mill">
                <code class="highlight language-scala">ivy"com.rlemaitre::pillars-core:0.0.1"</code>
            </pre>
            <pre class="code tab__pane scala-cli">
                <code class="highlight language-scala">//> using dep com.rlemaitre::pillars-core:0.0.1</code>
            </pre>
            <pre class="code tab__pane pants">
                <code class="highlight language-scala">
scala_artifact(
    group="com.rlemaitre",
    artifact="pillars-core",
    version="0.0.1",
    packages=["pillars.**"],
)
                </code>
            </pre>
            <pre class="code tab__pane gradle">
                <code class="highlight language-gradle">implementation 'com.rlemaitre:pillars-core:0.0.1'</code>
            </pre>
            <pre class="code tab__pane maven">
                <code class="highlight language-xml">
&lt;dependency>
    &lt;groupId>com.rlemaitre&lt;/groupId>
    &lt;artifactId>pillars-core&lt;/artifactId>
    &lt;version>0.0.1&lt;/version>
&lt;/dependency>
                </code>
            </pre>
        </div>
    </div>
    <div class="feature">
        <div class="feature__item">
            <h3 class="section__title">Modularity</h3>
            <p>It offers a modular structure, allowing developers to use only the components they need, resulting in efficient and streamlined applications.</p>
        </div>
        <div class="feature__item">
            <h3 class="section__title">Observability</h3>
            <p>Using OpenTelemetry, Pillars includes built-in support for observability, allowing you to monitor and understand the behavior of your application. This can be crucial for identifying and resolving issues quickly.</p>
        </div>
        <div class="feature__item">
            <h3 class="section__title">Performance</h3>
            <p>Pillars uses <a href="https://netty.io">Netty</a> for HTTP servers and clients, which is known for its high performance and scalability. This can help to ensure that your application remains responsive under heavy load.</p>
        </div>
        <div class="feature__item">
            <h3 class="section__title">Admin Server</h3>
            <p>Pillars includes an admin server feature, providing a separate interface for administrative tasks. This can help to keep your main application server focused on serving user requests.</p>
        </div>
        <div class="feature__item">
            <h3 class="section__title">Database Access</h3>
            <p>Pillars provides a streamlined interface for interacting with databases. This can help to reduce the complexity of your codebase and make it easier to manage data operations.</p>
        </div>
        <div class="feature__item">
            <h3 class="section__title">Feature Flags</h3>
            <p>Pillars supports feature flags, allowing you to toggle features on and off without needing to redeploy your application. This can be particularly useful for testing new features or managing rollouts.</p>
        </div>
    </div>
    <div class="callout">
        <p>Read our documentation for how to use Pillars</p>
        <a href="user-guide/overview.html" class="button--primary">Documentation</a>
    </div>
</div>
