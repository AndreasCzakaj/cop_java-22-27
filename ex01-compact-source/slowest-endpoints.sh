#!/usr/bin/env bash
# Prints the slowest endpoints of an access log.
# Usage: ./slowest-endpoints.sh <log file> [top]
#
# Written in 2019 by someone who has left the company. It works. Nobody dares to touch it.

set -euo pipefail

file="${1:?usage: $0 <log file> [top]}"
top="${2:-5}"

echo "Slowest endpoints in $(basename "$file")"
awk '
NF != 5 || $5 !~ /^[0-9]+ms$/ { next }
{
    path = $3; sub(/\?.*$/, "", path)
    n = split(path, seg, "/"); path = ""
    for (i = 2; i <= n; i++) path = path "/" (seg[i] ~ /^[0-9]+$/ ? "{id}" : seg[i])
    sum[$2 " " path] += $5 + 0; cnt[$2 " " path]++
}
END { for (k in sum) printf "%d\t%s\t%d\n", int(sum[k] / cnt[k]), k, cnt[k] }
' "$file" | sort -t$'\t' -k1,1nr -k2,2 | head -n "$top" \
    | awk -F'\t' '{ printf "%2d. %-32s %6d ms %4d requests\n", NR, $2, $1, $3 }'
