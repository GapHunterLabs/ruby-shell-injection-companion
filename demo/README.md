# Demo data — Ruby Shell Injection Companion

For capturing the real Marketplace screenshot:

1. `./gradlew runIde`
2. Open `demo/pinger.rb` as a scratch/standalone file (or drop it into
   any sandbox project) inside the sandbox IDE.
3. The `system("ping -c 1 #{host}")` call inside `ping_host` shows the
   warning — hover it for the tooltip. `ping_host_safely`'s array-
   argument form stays clean, for contrast.
4. Enter Full Screen (`View > Appearance > Enter Full Screen`), capture
   with `Win+Shift+S`, save directly to `docs/screenshots/` in this
   repo.
