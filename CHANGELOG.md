# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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