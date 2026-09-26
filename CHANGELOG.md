<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Ruby Shell Injection Companion Changelog

## [Unreleased]

## [0.1.1]

### Fixed

- Review/star CTA now links to this plugin's own Marketplace
  reviews page instead of the vendor's generic plugin list.

## [0.1.0]

### Added

- Warning on `system`/`exec`/backtick/`%x` calls whose command string
  contains `#{...}` interpolation -- a documented shell command
  injection risk (CWE-78) per Ruby's own official documentation, not
  covered by any of RuboCop's Security cops.
- 100% static text analysis, no Ruby plugin dependency, no network
  calls, no telemetry. Free.

[Unreleased]: https://github.com/GapHunterLabs/ruby-shell-injection-companion/compare/0.1.1...HEAD
[0.1.1]: https://github.com/GapHunterLabs/ruby-shell-injection-companion/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/GapHunterLabs/ruby-shell-injection-companion/commits/0.1.0
