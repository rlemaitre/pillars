# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v0.2.28] - 2024-09-05
### :sparkles: New Features
- [`d502b6f`](https://github.com/rlemaitre/pillars/commit/d502b6f99c7eb98a6b1a349c8fa8e995c554d000) - **api-server**: can be built from controllers *(PR [#149](https://github.com/rlemaitre/pillars/pull/149) by [@vbergeron](https://github.com/vbergeron))*


## [v0.2.27] - 2024-09-03
### :wrench: Chores
- [`ac5b18c`](https://github.com/rlemaitre/pillars/commit/ac5b18cd24baa6263ac0182d29e06cab98465c54) - Patches/Minor updates *(PR [#147](https://github.com/rlemaitre/pillars/pull/147) by [@scala-steward](https://github.com/scala-steward))*


## [v0.2.26] - 2024-08-22
### :wrench: Chores
- [`39e1ae3`](https://github.com/rlemaitre/pillars/commit/39e1ae33154d9430f9c745038c0c3daa63f67293) - Add labels to issue templates and add documentation template *(PR [#145](https://github.com/rlemaitre/pillars/pull/145) by [@rlemaitre](https://github.com/rlemaitre))*


## [v0.2.23] - 2024-08-22
### :wrench: Chores
- [`c2da005`](https://github.com/rlemaitre/pillars/commit/c2da00542b0cd52e9d864f738e506957909b1080) - Add templates for issues *(PR [#144](https://github.com/rlemaitre/pillars/pull/144) by [@rlemaitre](https://github.com/rlemaitre))*


## [v0.2.22] - 2024-08-20
### :wrench: Chores
- [`2f7c511`](https://github.com/rlemaitre/pillars/commit/2f7c51169e15a0fca376c9818147a261c8997f6b) - Patches/Minor updates *(PR [#142](https://github.com/rlemaitre/pillars/pull/142) by [@scala-steward](https://github.com/scala-steward))*


## [v0.2.21] - 2024-08-10
### :bug: Bug Fixes
- [`5c8ad04`](https://github.com/rlemaitre/pillars/commit/5c8ad042e83b2c676a10635bf8f07957bbb8e1ef) - **docs**: Fix project name replacement in overview *(PR [#141](https://github.com/rlemaitre/pillars/pull/141) by [@rlemaitre](https://github.com/rlemaitre))*


## [v0.2.18] - 2024-08-07
### :wrench: Chores
- [`e6eac0d`](https://github.com/rlemaitre/pillars/commit/e6eac0de122d0410e7c0fef2cd0df0169ac4fa1f) - Patches/Minor updates *(PR [#138](https://github.com/rlemaitre/pillars/pull/138) by [@scala-steward](https://github.com/scala-steward))*


## [v0.2.16] - 2024-08-07
### :sparkles: New Features
- [`84bad0c`](https://github.com/rlemaitre/pillars/commit/84bad0c12adf4f6aea9f3954861c2f9d10237a5b) - **db-skunk**: Allow session configuration *(PR [#140](https://github.com/rlemaitre/pillars/pull/140) by [@rlemaitre](https://github.com/rlemaitre))*


## [v0.2.15] - 2024-07-26
### :wrench: Chores
- [`c4b66a3`](https://github.com/rlemaitre/pillars/commit/c4b66a34aedae06b58c21fcf1d191c3961248f4f) - Upgrade Github Actions versions *(commit by [@rlemaitre](https://github.com/rlemaitre))*


## [v0.2.0] - 2024-07-23
### :boom: BREAKING CHANGES
- due to [`c49773d`](https://github.com/rlemaitre/pillars/commit/c49773de50b466bfb5769f61fba0141932df0b19) - Rename db to db-skunk *(PR [#95](https://github.com/rlemaitre/pillars/pull/95) by [@rlemaitre](https://github.com/rlemaitre))*:

  dependencies have to be updated to `pillars-db-skunk` if you used `pillars-db`


### :sparkles: New Features
- [`82955ca`](https://github.com/rlemaitre/pillars/commit/82955ca34d66dbee8586b542a9e95fed2ecdc879) - **core**: Errors are returned in json *(PR [#102](https://github.com/rlemaitre/pillars/pull/102) by [@rlemaitre](https://github.com/rlemaitre))*
- [`bd47d89`](https://github.com/rlemaitre/pillars/commit/bd47d8968ea5262b2f0f57fc97bf18aabceac3a8) - Add metrics for http-client and http servers*(PR [#125](https://github.com/rlemaitre/pillars/pull/125) by [@rlemaitre](https://github.com/rlemaitre))*
  - :arrow_lower_right: *addresses issue [#35](https://github.com/rlemaitre/pillars/issues/35) opened by [@rlemaitre](https://github.com/rlemaitre)*
  - :arrow_lower_right: *addresses issue [#127](https://github.com/rlemaitre/pillars/issues/127) opened by [@rlemaitre](https://github.com/rlemaitre)*

### :recycle: Refactors
- [`c49773d`](https://github.com/rlemaitre/pillars/commit/c49773de50b466bfb5769f61fba0141932df0b19) - **db**: Rename db to db-skunk *(PR [#95](https://github.com/rlemaitre/pillars/pull/95) by [@rlemaitre](https://github.com/rlemaitre))*

### :wrench: Chores
- [`f309fdc`](https://github.com/rlemaitre/pillars/commit/f309fdc203e8b4c9eae3fb9d7a46ecc23ac1da8c) - Make Scala Steward follow conventional commits *(commit by [@rlemaitre](https://github.com/rlemaitre))*
- [`dcf775c`](https://github.com/rlemaitre/pillars/commit/dcf775c5f2db75891d8939e222a1def845d86b6c) - Update openapi-circe-yaml from 0.7.4 to 0.8.0 *(PR [#89](https://github.com/rlemaitre/pillars/pull/89) by [@scala-steward](https://github.com/scala-steward))*
- [`1d74de5`](https://github.com/rlemaitre/pillars/commit/1d74de5c449aaab1222dc770353acd98d42f5f1f) - Patches/Minor updates *(PR [#92](https://github.com/rlemaitre/pillars/pull/92) by [@scala-steward](https://github.com/scala-steward))*
- [`a7df4e2`](https://github.com/rlemaitre/pillars/commit/a7df4e2a68baa0120cb4dd91ac05bcb46014e721) - Update sbt-buildinfo from 0.11.0 to 0.12.0 *(PR [#93](https://github.com/rlemaitre/pillars/pull/93) by [@scala-steward](https://github.com/scala-steward))*
- [`e5d3a95`](https://github.com/rlemaitre/pillars/commit/e5d3a95ea32557a33424cb7e36346fa66ef8351d) - Minor dependencies upgrade *(PR [#100](https://github.com/rlemaitre/pillars/pull/100) by [@rlemaitre](https://github.com/rlemaitre))*
- [`e7d8b34`](https://github.com/rlemaitre/pillars/commit/e7d8b34f89a093238456d6f16b25f73bd514bd9f) - Update munit-cats-effect from 2.0.0-M4 to 2.0.0-M5 *(PR [#97](https://github.com/rlemaitre/pillars/pull/97) by [@scala-steward](https://github.com/scala-steward))*
- [`b3ad72e`](https://github.com/rlemaitre/pillars/commit/b3ad72e37827b48fee9728c83209cdfdf9bda2c8) - Update openapi-circe-yaml from 0.8.0 to 0.9.0 *(PR [#98](https://github.com/rlemaitre/pillars/pull/98) by [@scala-steward](https://github.com/scala-steward))*
- [`04119ff`](https://github.com/rlemaitre/pillars/commit/04119ff3e81a994479e8875b01c7f20238f85db9) - Update munit, munit-scalacheck from 1.0.0-M11 to 1.0.0-M12 *(PR [#103](https://github.com/rlemaitre/pillars/pull/103) by [@scala-steward](https://github.com/scala-steward))*
- [`3603f53`](https://github.com/rlemaitre/pillars/commit/3603f53ba1ea2178e3285a2fefae5b0660e1a766) - Patches/Minor updates *(PR [#104](https://github.com/rlemaitre/pillars/pull/104) by [@scala-steward](https://github.com/scala-steward))*
- [`8a8c9f0`](https://github.com/rlemaitre/pillars/commit/8a8c9f0f75c3f6d6f5ebfb59ea7e02d3edb5b283) - Update skunk-circe, skunk-core from 1.0.0-M4 to 1.0.0-M5, otel4s from 0.0.4 to 0.0.5 *(PR [#105](https://github.com/rlemaitre/pillars/pull/105) by [@scala-steward](https://github.com/scala-steward))*
- [`b1a3932`](https://github.com/rlemaitre/pillars/commit/b1a393272859ed72f84f495e0c86233ab67998ee) - Update munit-scalacheck from 1.0.0-M12 to 1.0.0-RC1 *(PR [#109](https://github.com/rlemaitre/pillars/pull/109) by [@scala-steward](https://github.com/scala-steward))*
- [`17091bb`](https://github.com/rlemaitre/pillars/commit/17091bb3f0d70db12b5c315f8d1a057ad37b00f1) - Update munit-cats-effect from 2.0.0-M5 to 2.0.0 *(PR [#117](https://github.com/rlemaitre/pillars/pull/117) by [@scala-steward](https://github.com/scala-steward))*
- [`f462abd`](https://github.com/rlemaitre/pillars/commit/f462abd6573bb50317bd9d89a2afd1c760b297c6) - Update munit-scalacheck from 1.0.0-RC1 to 1.0.0 *(PR [#116](https://github.com/rlemaitre/pillars/pull/116) by [@scala-steward](https://github.com/scala-steward))*
- [`a14aad1`](https://github.com/rlemaitre/pillars/commit/a14aad1b54685a311f17ae1c6893421a6617e986) - Update munit from 1.0.0-M12 to 1.0.0 *(PR [#115](https://github.com/rlemaitre/pillars/pull/115) by [@scala-steward](https://github.com/scala-steward))*
- [`ee9245e`](https://github.com/rlemaitre/pillars/commit/ee9245e96871b61895bc30065871cc52426ca3d0) - Update skunk-circe, skunk-core from 1.0.0-M5 to 1.0.0-M6 *(PR [#113](https://github.com/rlemaitre/pillars/pull/113) by [@scala-steward](https://github.com/scala-steward))*
- [`9453aba`](https://github.com/rlemaitre/pillars/commit/9453aba6c44b8500bf20c250809db3c00370d22d) - Patches/Minor updates *(PR [#111](https://github.com/rlemaitre/pillars/pull/111) by [@scala-steward](https://github.com/scala-steward))*
- [`044571c`](https://github.com/rlemaitre/pillars/commit/044571cf2899d1c8db474fac9873d931b13e5482) - Patches/Minor updates *(PR [#118](https://github.com/rlemaitre/pillars/pull/118) by [@scala-steward](https://github.com/scala-steward))*
- [`9827be6`](https://github.com/rlemaitre/pillars/commit/9827be6c442a6d32047b6b5509cda42ac1e2668f) - Patches/Minor updates *(PR [#120](https://github.com/rlemaitre/pillars/pull/120) by [@scala-steward](https://github.com/scala-steward))*
- [`0f5f36e`](https://github.com/rlemaitre/pillars/commit/0f5f36e3c8bb4c6d700f3ceee183a20acb93a30e) - Patches/Minor updates *(PR [#122](https://github.com/rlemaitre/pillars/pull/122) by [@scala-steward](https://github.com/scala-steward))*
- [`a2b1e85`](https://github.com/rlemaitre/pillars/commit/a2b1e85ce85e7f2ffee9b0b9223d4e4a29e73651) - Upgrade otel4s to 0.8.0 and skunk to 1.0.0-M7 *(PR [#123](https://github.com/rlemaitre/pillars/pull/123) by [@rlemaitre](https://github.com/rlemaitre))*


## [v0.1.5] - 2024-03-14
### :bug: Bug Fixes
- [`0e6ca91`](https://github.com/rlemaitre/pillars/commit/0e6ca91e41507bbd46e5e549f71b1ca85d02d59b) - **core**: Handle correctly PillarsError in API *(PR [#86](https://github.com/rlemaitre/pillars/pull/86) by [@rlemaitre](https://github.com/rlemaitre))*


## [v0.1.4] - 2024-03-13
### :sparkles: New Features
- [`bb6627e`](https://github.com/rlemaitre/pillars/commit/bb6627e234a0b5fdc6c7eb5189261d0f5a85f531) - add tapir metrics *(PR [#85](https://github.com/rlemaitre/pillars/pull/85) by [@jnicoulaud-ledger](https://github.com/jnicoulaud-ledger))*

### :bug: Bug Fixes
- [`a73fb77`](https://github.com/rlemaitre/pillars/commit/a73fb77bb9990c81fe6bf55435335cd3d32d8c26) - **core**: Make Observability usable *(PR [#84](https://github.com/rlemaitre/pillars/pull/84) by [@rlemaitre](https://github.com/rlemaitre))*


## [v0.1.3] - 2024-03-13
### :sparkles: New Features
- [`a6a482f`](https://github.com/rlemaitre/pillars/commit/a6a482f17696b1e4965d69c0e7e905275d3482bd) - Add OpenAPI generation *(PR [#81](https://github.com/rlemaitre/pillars/pull/81) by [@rlemaitre](https://github.com/rlemaitre))*
- [`590b98b`](https://github.com/rlemaitre/pillars/commit/590b98b3d42687eeb743235a7a27e2cc0e5ecd52) - Add rediculous redis module *(PR [#80](https://github.com/rlemaitre/pillars/pull/80) by [@estrauser-ledger](https://github.com/estrauser-ledger))*
- [`3aa338d`](https://github.com/rlemaitre/pillars/commit/3aa338d66cc6db80ebfa0a0b14a4acff5e2e2c54) - add rabbitmq fs2 module *(PR [#79](https://github.com/rlemaitre/pillars/pull/79) by [@jnicoulaud-ledger](https://github.com/jnicoulaud-ledger))*

### :bug: Bug Fixes
- [`090983d`](https://github.com/rlemaitre/pillars/commit/090983dfc83093367a4b18a9ebb7448e973400d0) - Add doobie loader *(PR [#82](https://github.com/rlemaitre/pillars/pull/82) by [@estrauser-ledger](https://github.com/estrauser-ledger))*

### :recycle: Refactors
- [`7acb4c3`](https://github.com/rlemaitre/pillars/commit/7acb4c34a8ebc49b71dba7c7398b86c95e9116f4) - **db-migration**: Use Flyway instead of dumbo *(PR [#83](https://github.com/rlemaitre/pillars/pull/83) by [@rlemaitre](https://github.com/rlemaitre))*


## [v0.1.1] - 2024-03-13
### :bug: Bug Fixes
- [`c0272e3`](https://github.com/rlemaitre/pillars/commit/c0272e3e8e68a48125955641e760f78e15670cdb) - remove db migration clash *(commit by [@rlemaitre](https://github.com/rlemaitre))*


## [v0.1.0] - 2024-03-13
### :sparkles: New Features
- [`12e9fbe`](https://github.com/rlemaitre/pillars/commit/12e9fbea5a902d1ac9b3e53164fdd50fa011f30a) - **flags**: [#39](https://github.com/rlemaitre/pillars/pull/39) Add ability to modify feature flags at runtime *(PR [#63](https://github.com/rlemaitre/pillars/pull/63) by [@rlemaitre](https://github.com/rlemaitre))*
- [`ac48e21`](https://github.com/rlemaitre/pillars/commit/ac48e216e447d911827a2f405d1c83ae2e20a91b) - **core**: Add circe codec for Path *(commit by [@rlemaitre](https://github.com/rlemaitre))*
- [`7084a6b`](https://github.com/rlemaitre/pillars/commit/7084a6bb9d4fa2350d27ac67b57fe9297a8406d1) - **db**: [#57](https://github.com/rlemaitre/pillars/pull/57) Add DB migration module *(PR [#72](https://github.com/rlemaitre/pillars/pull/72) by [@rlemaitre](https://github.com/rlemaitre))*
  - :arrow_lower_right: *addresses issue [#57](https://github.com/rlemaitre/pillars/issues/57) opened by [@rlemaitre](https://github.com/rlemaitre)*

### :bug: Bug Fixes
- [`ba6a282`](https://github.com/rlemaitre/pillars/commit/ba6a282db2715678a0a5cd680ead3cb53d18fb87) - **docs**: [#61](https://github.com/rlemaitre/pillars/pull/61) Reflect usage of context function in home page *(PR [#62](https://github.com/rlemaitre/pillars/pull/62) by [@rlemaitre](https://github.com/rlemaitre))*

### :recycle: Refactors
- [`2483dac`](https://github.com/rlemaitre/pillars/commit/2483dacba2ee2b1a250456c5b83052445f630cc9) - **core**: Use context functions *(PR [#59](https://github.com/rlemaitre/pillars/pull/59) by [@rlemaitre](https://github.com/rlemaitre))*
- [`2387afe`](https://github.com/rlemaitre/pillars/commit/2387afed7d06edd009dcd0808e897f4ecf7acbcb) - **core**: Remove usage of reflection for modules *(PR [#60](https://github.com/rlemaitre/pillars/pull/60) by [@rlemaitre](https://github.com/rlemaitre))*
- [`42b2f6c`](https://github.com/rlemaitre/pillars/commit/42b2f6c90481a65db09381a68b5be28aacbe264f) - **core**: [#64](https://github.com/rlemaitre/pillars/pull/64) Use fs2 Path instead of java.nio *(PR [#66](https://github.com/rlemaitre/pillars/pull/66) by [@rlemaitre](https://github.com/rlemaitre))*
  - :arrow_lower_right: *addresses issue [#64](https://github.com/rlemaitre/pillars/issues/64) opened by [@rlemaitre](https://github.com/rlemaitre)*

### :white_check_mark: Tests
- [`b53bb35`](https://github.com/rlemaitre/pillars/commit/b53bb354b761e4006543a90a174b060e351d1e1f) - **db-migration**: Add tests for DB migrations *(PR [#74](https://github.com/rlemaitre/pillars/pull/74) by [@rlemaitre](https://github.com/rlemaitre))*

### :wrench: Chores
- [`dd3fdf6`](https://github.com/rlemaitre/pillars/commit/dd3fdf6348f8a893d4f6f2e75f957c7fb7023f76) - Update scalafmt *(commit by [@rlemaitre](https://github.com/rlemaitre))*


[v0.1.0]: https://github.com/rlemaitre/pillars/compare/v0.0.2...v0.1.0
[v0.1.1]: https://github.com/rlemaitre/pillars/compare/v0.1.0...v0.1.1
[v0.1.3]: https://github.com/rlemaitre/pillars/compare/v0.1.1...v0.1.3
[v0.1.4]: https://github.com/rlemaitre/pillars/compare/v0.1.3...v0.1.4
[v0.1.5]: https://github.com/rlemaitre/pillars/compare/v0.1.4...v0.1.5
[v0.2.0]: https://github.com/rlemaitre/pillars/compare/v0.1.5...v0.2.0
[v0.2.0]: https://github.com/rlemaitre/pillars/compare/v0.1.5...v0.2.0
[v0.2.15]: https://github.com/rlemaitre/pillars/compare/v0.2.14...v0.2.15
[v0.2.16]: https://github.com/rlemaitre/pillars/compare/v0.2.15...v0.2.16
[v0.2.18]: https://github.com/rlemaitre/pillars/compare/v0.2.17...v0.2.18
[v0.2.21]: https://github.com/rlemaitre/pillars/compare/v0.2.20...v0.2.21
[v0.2.22]: https://github.com/rlemaitre/pillars/compare/v0.2.21...v0.2.22
[v0.2.23]: https://github.com/rlemaitre/pillars/compare/v0.2.22...v0.2.23
[v0.2.26]: https://github.com/rlemaitre/pillars/compare/v0.2.25...v0.2.26
[v0.2.27]: https://github.com/rlemaitre/pillars/compare/v0.2.26...v0.2.27
[v0.2.28]: https://github.com/rlemaitre/pillars/compare/v0.2.27...v0.2.28
