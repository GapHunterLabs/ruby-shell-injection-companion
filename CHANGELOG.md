<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Ruby Shell Injection Companion Changelog

## [Unreleased]

### Added

- A description page for the inspection in **Settings | Editor |
  Inspections**, which showed "Under construction".

### Changed

- The rating prompt's local counter keeps one-way fingerprints of findings
  instead of their file paths, and deletes the list that earlier versions
  kept.
- `PRIVACY.md` describes the values the plugin keeps in the IDE's local
  settings.

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
