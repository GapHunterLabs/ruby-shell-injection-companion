# Demo data for Ruby Shell Injection Companion — used with
# `./gradlew runIde` to capture the real Marketplace screenshot. Open
# this file, the warning should appear on the system() line inside
# ping_host.
def ping_host(host)
  # Interpolated command string -- FLAGGED.
  system("ping -c 1 #{host}")
end

def ping_host_safely(host)
  # Safe array-argument form, no shell interpretation -- NOT flagged.
  system("ping", "-c", "1", host)
end
