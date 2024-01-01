<div class="hero">
    <h1 class="hero__title">Pillars</h1>
    <p class="hero__description">Build your backend service in Scala 3 the easy way</p>
</div>
<div class="hero__terminal">
    <pre>
        <code class="language-scala">
import ... // import your dependencies

case class BookStore(name: AppName, version: Version, description: Description) extends pillars.App[IO]:
    override type ConfigType = BookstoreConfig
    override def run(pillars: Pillars[IO]): IO[Unit] = ???
    override def command: Command[IO[ExitCode]]      = ???

object Main extends pillars.EntryPoint(
    BookStore(AppName("BookStore"), Version("0.0.1"), Description("A simple bookstore"))
)
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
                <code class="highlight language-scala">libraryDependencies ++= Seq("com.rlemaitre" %% "pillars" % "0.0.1")</code>
            </pre>
            <pre class="code tab__pane mill">
                <code class="highlight language-scala">ivy"com.rlemaitre::pillars:0.0.1"</code>
            </pre>
            <pre class="code tab__pane scala-cli">
                <code class="highlight language-scala">//> using dep com.rlemaitre::pillars:0.0.1</code>
            </pre>
            <pre class="code tab__pane pants">
                <code class="highlight language-scala">
scala_artifact(
    group="com.rlemaitre",
    artifact="pillars",
    version="0.0.1",
    packages=["pillars.**"],
)
                </code>
            </pre>
            <pre class="code tab__pane gradle">
                <code class="highlight language-gradle">implementation 'com.rlemaitre:pillars:0.0.1'</code>
            </pre>
            <pre class="code tab__pane maven">
                <code class="highlight language-xml">
&lt;dependency>
    &lt;groupId>com.rlemaitre&lt;/groupId>
    &lt;artifactId>pillars&lt;/artifactId>
    &lt;version>0.0.1&lt;/version>
&lt;/dependency>
                </code>
            </pre>
        </div>
    </div>
    <div class="feature">
        <div class="feature__item">
            <h3 class="section__title">Configuration</h3>
            <p>I don't know what you're talking about. I am a member of the Imperial Senate on a diplomatic mission to Alderaan-- Red Five standing by. Red Five standing by. I don't know what you're talking about. I am a member of the Imperial Senate on a diplomatic mission to Alderaan--</p>
        </div>
        <div class="feature__item">
            <h3 class="section__title">Database Access</h3>
            <p>I find your lack of faith disturbing. What?! Hokey religions and ancient weapons are no match for a good blaster at your side, kid. I need your help, Luke. She needs your help. I'm getting too old for this sort of thing.</p>
        </div>
        <div class="feature__item">
            <h3 class="section__title">Observability</h3>
            <p>I'm surprised you had the courage to take the responsibility yourself. You don't believe in the Force, do you? She must have hidden the plans in the escape pod. Send a detachment down to retrieve them, and see to it personally, Commander. There'll be no one to stop us this time!</p>
        </div>
        <div class="feature__item">
            <h3 class="section__title">Admin Server</h3>
            <p>You are a part of the Rebel Alliance and a traitor! Take her away! A tremor in the Force. The last time I felt it was in the presence of my old master. Obi-Wan is here. The Force is with him. I suggest you try it again, Luke. This time, let go your conscious self and act on instinct.</p>
        </div>
        <div class="feature__item">
            <h3 class="section__title">Feature Flags</h3>
            <p>But with the blast shield down, I can't even see! How am I supposed to fight? Leave that to me. Send a distress signal, and inform the Senate that all on board were killed. What!? Still, she's got a lot of spirit. I don't know, what do you think?</p>
        </div>
    </div>
    <div class="callout">
        <p>Read our documentation for how to use Pillars</p>
        <a href="user-guide/overview.html" class="button--primary">Documentation</a>
    </div>
</div>
