var documents = [

{
    "id": 0,
    "uri": "user-guide/index.html",
    "menu": "user-guide",
    "title": "Overview",
    "text": " Table of Contents Overview Features Usage Dependencies Overview This library is a basis for backend applications written in Scala 3 using the TypeLevel stack. It is a work in progress and is not ready for production use. Features Admin server Configuration Database access Feature flags Logging OpenTelemetry-based observability Usage This library is currently available for Scala binary version 3.3.1. To use the latest version, include the following in your build.sbt : libraryDependencies ++= Seq( \"com.rlemaitre\" %% \"pillars\" % \"@VERSION@\" ) Dependencies Cats Cats collections Cats time Mouse Ip4s Cats Effect Fs2 Circe and Circe YAML Decline Skunk Scribe Tapir Iron Http4s Otel4s mUnit "
},

{
    "id": 1,
    "uri": "user-guide/20_features/30_api-server.html",
    "menu": "user-guide",
    "title": "API Server",
    "text": " Table of Contents API Server API Server This documentation needs to be written. You can help us by contributing to the documentation . "
},

{
    "id": 2,
    "uri": "user-guide/20_features/50_observability.html",
    "menu": "user-guide",
    "title": "Observability",
    "text": " Table of Contents Observability Observability This documentation needs to be written. You can help us by contributing to the documentation . "
},

{
    "id": 3,
    "uri": "user-guide/20_features/40_admin-server.html",
    "menu": "user-guide",
    "title": "Admin Server",
    "text": " Table of Contents Admin Server Admin Server This documentation needs to be written. You can help us by contributing to the documentation . "
},

{
    "id": 4,
    "uri": "user-guide/20_features/20_logging.html",
    "menu": "user-guide",
    "title": "Logging",
    "text": " Table of Contents Logging Logging This documentation needs to be written. You can help us by contributing to the documentation . "
},

{
    "id": 5,
    "uri": "user-guide/20_features/10_configuration.html",
    "menu": "user-guide",
    "title": "Configuration",
    "text": " Table of Contents Configuration Pillars Configuration Application Configuration Configuration Pillars is configured using YAML v1.2 files. Pillars Configuration Pillars configuration is structured as follows: name: Bookstore log: level: info format: enhanced output: type: console db: host: localhost port: 5432 database: bookstore username: postgres password: postgres pool-size: 10 debug: false probe: timeout: PT5s interval: PT10s failure-count: 3 api: enabled: true http: host: 0.0.0.0 port: 9876 auth-token: max-connections: 1024 probe: timeout: PT5s interval: PT10s failure-count: 3 admin: enabled: true http: host: 0.0.0.0 port: 19876 max-connections: 32 observability: enabled: true service-name: bookstore feature-flags: enabled: true flags: - name: feature-1 status: enabled - name: feature-2 status: disabled Application Configuration "
},

{
    "id": 6,
    "uri": "user-guide/30_modules/index.html",
    "menu": "user-guide",
    "title": "Optional Modules",
    "text": " Table of Contents Modules Database HTTP Client Feature Flags Write your own module Modules Pillars includes several optional modules: Database HTTP Client Feature Flags Database The database module provides a simple abstraction over the database access layer. It is based on the skunk library and provides a simple interface to execute queries and transactions. Read more HTTP Client The HTTP Client module provides a simple abstraction over the HTTP client layer. It is based on the http4s library using Netty and provides a simple interface to execute HTTP requests. Read more Feature Flags The Feature Flags module provides a simple abstraction over the feature flags layer. Read more Write your own module You can easily write your own module by implementing the Module trait. Read more "
},

{
    "id": 7,
    "uri": "user-guide/10_quick-start.html",
    "menu": "user-guide",
    "title": "Quick Start",
    "text": " Table of Contents Quick Start Quick Start This documentation needs to be written. You can help us by contributing to the documentation . "
},

{
    "id": 8,
    "uri": "user-guide/30_modules/30_flags.html",
    "menu": "user-guide",
    "title": "Feature Flags module",
    "text": " Table of Contents Feature Flags module Feature Flags module This documentation needs to be written. You can help us by contributing to the documentation . "
},

{
    "id": 9,
    "uri": "user-guide/30_modules/20_http-client.html",
    "menu": "user-guide",
    "title": "HTTP Client Module",
    "text": " Table of Contents HTTP Client module HTTP Client module This documentation needs to be written. You can help us by contributing to the documentation . "
},

{
    "id": 10,
    "uri": "user-guide/30_modules/10_db.html",
    "menu": "user-guide",
    "title": "Database Module",
    "text": " Table of Contents Database module Database module This documentation needs to be written. You can help us by contributing to the documentation . "
},

{
    "id": 11,
    "uri": "user-guide/30_modules/100_write-your-own-module.html",
    "menu": "user-guide",
    "title": "Write your own module",
    "text": " Table of Contents Write your own module Write your own module This documentation needs to be written. You can help us by contributing to the documentation . "
},

{
    "id": 12,
    "uri": "contribute/20_code_of_conduct.html",
    "menu": "contribute",
    "title": "Code of Conduct",
    "text": " Table of Contents Code of Conduct Our Pledge Our Standards Our Responsibilities Scope Enforcement Enforcement Guidelines Attribution Code of Conduct Our Pledge In the interest of fostering an open and welcoming environment, we as contributors and maintainers pledge to make participation in our project and our community a harassment-free experience for everyone, regardless of age, body size, disability, ethnicity, sex characteristics, gender identity and expression, level of experience, education, socio-economic status, nationality, personal appearance, race, religion, or sexual identity and orientation. Our Standards Examples of behavior that contributes to a positive environment for our community include: Demonstrating empathy and kindness toward other people Being respectful of differing opinions, viewpoints, and experiences Giving and gracefully accepting constructive feedback Accepting responsibility and apologizing to those affected by our mistakes, and learning from the experience Focusing on what is best not just for us as individuals, but for the overall community Examples of unacceptable behavior include: The use of sexualized language or imagery, and sexual attention or advances Trolling, insulting or derogatory comments, and personal or political attacks Public or private harassment Publishing others' private information, such as a physical or email address, without their explicit permission Other conduct which could reasonably be considered inappropriate in a professional setting Our Responsibilities Project maintainers are responsible for clarifying and enforcing our standards of acceptable behavior and will take appropriate and fair corrective action in response to any behavior that they deem inappropriate, threatening, offensive, or harmful. Project maintainers have the right and responsibility to remove, edit, or reject comments, commits, code, wiki edits, issues, and other contributions that are not aligned to this Code of Conduct, and will communicate reasons for moderation decisions when appropriate. Scope This Code of Conduct applies within all community spaces, and also applies when an individual is officially representing the community in public spaces. Examples of representing our community include using an official e-mail address, posting via an official social media account, or acting as an appointed representative at an online or offline event. Enforcement Instances of abusive, harassing, or otherwise unacceptable behavior may be reported to the community leaders responsible for enforcement at contact@rlemaitre.com . All complaints will be reviewed and investigated promptly and fairly. All community leaders are obligated to respect the privacy and security of the reporter of any incident. Enforcement Guidelines Community leaders will follow these Community Impact Guidelines in determining the consequences for any action they deem in violation of this Code of Conduct: 1. Correction Community Impact : Use of inappropriate language or other behavior deemed unprofessional or unwelcome in the community. Consequence : A private, written warning from community leaders, providing clarity around the nature of the violation and an explanation of why the behavior was inappropriate. A public apology may be requested. 2. Warning Community Impact : A violation through a single incident or series of actions. Consequence : A warning with consequences for continued behavior. No interaction with the people involved, including unsolicited interaction with those enforcing the Code of Conduct, for a specified period of time. This includes avoiding interactions in community spaces as well as external channels like social media. Violating these terms may lead to a temporary or permanent ban. 3. Temporary Ban Community Impact : A serious violation of community standards, including sustained inappropriate behavior. Consequence : A temporary ban from any sort of interaction or public communication with the community for a specified period of time. No public or private interaction with the people involved, including unsolicited interaction with those enforcing the Code of Conduct, is allowed during this period. Violating these terms may lead to a permanent ban. 4. Permanent Ban Community Impact : Demonstrating a pattern of violation of community standards, including sustained inappropriate behavior, harassment of an individual, or aggression toward or disparagement of classes of individuals. Consequence : A permanent ban from any sort of public interaction within the community. Attribution This Code of Conduct is adapted from the Contributor Covenant , version 1.4 and 2.0 , and was generated by contributing-gen . "
},

{
    "id": 13,
    "uri": "contribute/10_contributing.html",
    "menu": "contribute",
    "title": "Contributing to Pillars",
    "text": " Table of Contents Contributing to Pillars Code of Conduct I Have a Question I Want To Contribute Style guides Join The Project Team Attribution Contributing to Pillars First off, thanks for taking the time to contribute! ❤️ All types of contributions are encouraged and valued. See the [toc] for different ways to help and details about how this project handles them. Please make sure to read the relevant section before making your contribution. It will make it a lot easier for us maintainers and smooth out the experience for all involved. The community looks forward to your contributions. 🎉 And if you like the project, but just don&#8217;t have time to contribute, that&#8217;s fine. There are other easy ways to support the project and show your appreciation, which we would also be very happy about: Star the project Tweet about it Refer this project in your project&#8217;s readme Mention the project at local meetups and tell your friends/colleagues Code of Conduct This project and everyone participating in it is governed by the Pillars Code of Conduct . By participating, you are expected to uphold this code. Please report unacceptable behavior to pillars@rlemaitre.com . I Have a Question If you want to ask a question, we assume that you have read the available Documentation . Before you ask a question, it is best to search for existing Issues that might help you. In case you have found a suitable issue and still need clarification, you can write your question in this issue. It is also advisable to search the internet for answers first. If you then still feel the need to ask a question and need clarification, we recommend the following: Open an Issue . Provide as much context as you can about what you&#8217;re running into. Provide project and platform versions (scala, sbt, etc), depending on what seems relevant. We will then take care of the issue as soon as possible. I Want To Contribute When contributing to this project, you must agree that you have authored 100% of the content, that you have the necessary rights to the content and that the content you contribute may be provided under the project license. Reporting Bugs Before Submitting a Bug Report A good bug report shouldn&#8217;t leave others needing to chase you up for more information. Therefore, we ask you to investigate carefully, collect information and describe the issue in detail in your report. Please complete the following steps in advance to help us fix any potential bug as fast as possible. Make sure that you are using the latest version. Determine if your bug is really a bug and not an error on your side e.g. using incompatible environment components/versions (Make sure that you have read the documentation . If you are looking for support, you might want to check this section . To see if other users have experienced (and potentially already solved) the same issue you are having, check if there is not already a bug report existing for your bug or error in the bug tracker . Also make sure to search the internet (including Stack Overflow) to see if users outside of the GitHub community have discussed the issue. Collect information about the bug: Stack trace (Traceback) OS, Platform and Version (Windows, Linux, macOS, x86, ARM) Version of the interpreter, compiler, SDK, runtime environment, package manager, depending on what seems relevant. Possibly your input and the output Can you reliably reproduce the issue? And can you also reproduce it with older versions? How Do I Submit a Good Bug Report? You must never report security related issues, vulnerabilities or bugs including sensitive information to the issue tracker, or elsewhere in public. Instead, sensitive bugs must be sent by email to security@rlemaitre.com . We use GitHub issues to track bugs and errors. If you run into an issue with the project: Open an Issue . (Since we can&#8217;t be sure at this point whether it is a bug or not, we ask you not to talk about a bug yet and not to label the issue.) Explain the behavior you would expect and the actual behavior. Please provide as much context as possible and describe the reproduction steps that someone else can follow to recreate the issue on their own. This usually includes your code. For good bug reports you should isolate the problem and create a reduced test case. Provide the information you collected in the previous section. Once it&#8217;s filed: The project team will label the issue accordingly. A team member will try to reproduce the issue with your provided steps. If there are no reproduction steps or no obvious way to reproduce the issue, the team will ask you for those steps and mark the issue as needs-repro . Bugs with the needs-repro tag will not be addressed until they are reproduced. If the team is able to reproduce the issue, it will be marked needs-fix , as well as possibly other tags (such as critical ), and the issue will be left to be implemented by someone . Suggesting Enhancements This section guides you through submitting an enhancement suggestion for Pillars, including completely new features and minor improvements to existing functionality . Following these guidelines will help maintainers and the community to understand your suggestion and find related suggestions. Before Submitting an Enhancement Make sure that you are using the latest version. Read the documentation carefully and find out if the functionality is already covered, maybe by an individual configuration. Perform a search to see if the enhancement has already been suggested. If it has, add a comment to the existing issue instead of opening a new one. Find out whether your idea fits with the scope and aims of the project. It&#8217;s up to you to make a strong case to convince the project&#8217;s developers of the merits of this feature. Keep in mind that we want features that will be useful to the majority of our users and not just a small subset. If you&#8217;re just targeting a minority of users, consider writing an add-on/plugin library. How Do I Submit a Good Enhancement Suggestion? Enhancement suggestions are tracked as GitHub issues . Use a clear and descriptive title for the issue to identify the suggestion. Provide a step-by-step description of the suggested enhancement in as many details as possible. Describe the current behavior and explain which behavior you expected to see instead and why. At this point you can also tell which alternatives do not work for you. Explain why this enhancement would be useful to most Pillars users. You may also want to point out the other projects that solved it better and which could serve as inspiration. Your First Code Contribution TBD Improving The Documentation TBD Style guides Commit Messages TBD Join The Project Team TBD Attribution This guide is based on the contributing-gen . Make your own ! "
},

{
    "id": 14,
    "uri": "search.html",
    "menu": "-",
    "title": "search",
    "text": " Search Results "
},

{
    "id": 15,
    "uri": "lunrjsindex.html",
    "menu": "-",
    "title": "null",
    "text": " will be replaced by the index "
},

];
