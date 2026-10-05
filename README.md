# Ruby Shell Injection Companion

Warning on `system(...)`, `exec(...)`, backticks (`` ` ``), or
`%x[...]` whose command string contains `#{...}` interpolation —
Ruby's own official documentation states: "Some Ruby core methods
accept string data that includes text to be executed as a system
command and should not be called with unknown or unsanitized
commands", specifically naming `Kernel#exec`, `Kernel#spawn`,
`Kernel#system`, and the backtick method.

Confirmed real and undetected: none of RuboCop's 7 `Security` cops
(`Eval`, `Open`, `IoMethods`, `MarshalLoad`, `YAMLLoad`, `JSONLoad`,
`CompoundHash`) cover shell command injection — confirmed by reading
RuboCop's own Security cops documentation page directly before
building this.

## Why it exists

```ruby
system("ping #{host}")
```

compiles and runs fine — until `host` ever contains something like
`"; rm -rf /"` from user input, at which point it's a full shell
command injection. The single-string form of `system`/`exec`/
backticks always goes through a shell, unlike the safe
`system(cmd, arg1, arg2)` array form.

## Why built this way

- **100% static text analysis** — a regex-based line scanner, not a
  real Ruby parser, so it works whether the Ruby plugin is installed
  or not.

## v0.1 scope — stated honestly, not exhaustively

Doesn't trace whether the interpolated value actually originates from
untrusted input, so a `#{}` interpolation of a hardcoded constant is a
possible (rare) false positive. The safe `system(cmd, arg1, arg2)`
array form (separate arguments, no shell interpretation) is correctly
never flagged.

## Usage

Open any `.rb` file. A `system`/`exec`/backtick/`%x` call with an
interpolated command string shows a warning.

## Support

- **Bugs and feature requests:** [GitHub Issues](https://github.com/GapHunterLabs/ruby-shell-injection-companion/issues)
- **Questions, or custom rules for a team's codebase:** **gaphunterlabs@gmail.com**
- **Security vulnerabilities:** report privately as described in [SECURITY.md](SECURITY.md), not in a public issue.
- **Privacy and network behavior:** [PRIVACY.md](PRIVACY.md)

## Development

```
./gradlew test           # unit tests
./gradlew buildPlugin    # generates build/distributions/*.zip
./gradlew verifyPlugin   # checks compatibility against real IDEs
```

## License

Apache-2.0. See `LICENSE`.
