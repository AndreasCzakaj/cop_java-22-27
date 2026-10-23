#!/usr/bin/env bash
# Prints the slowest endpoints of an access log.
# Usage: ./slowest-endpoints.sh <log file> [top]
#
# Written in 2019 by someone who has left the company. It works. Nobody dares to touch it.

set -euo pipefail

file="${1:?usage: $0 <log file> [top]}"
top="${2:-5}"
[[ -r "$file" ]] || { echo "File not found: $file" >&2; exit 1; }

stats=$(awk '
NF == 0 { next }
NF != 5 || $5 !~ /^[0-9]+ms$/ { skipped++; next }
{
    path = $3; sub(/\?.*$/, "", path)
    n = split(path, seg, "/"); path = ""
    for (i = 2; i <= n; i++) path = path "/" (seg[i] ~ /^[0-9]+$/ ? "{id}" : seg[i])
    sum[$2 "\t" path] += $5 + 0; cnt[$2 "\t" path]++
}
END {
    for (k in sum) printf "%d\t%s\t%d\n", int(sum[k] / cnt[k]), k, cnt[k]
    printf "#\t%d\n", skipped
}' "$file")

total=$(grep -vc '^#' <<< "$stats" || true)
skipped=$(grep '^#' <<< "$stats" | cut -f2)

echo "Slowest endpoints in $(basename "$file") (top $(( top < total ? top : total )) of $total)"
grep -v '^#' <<< "$stats" | sort -t$'\t' -k1,1nr -k2,2 -k3,3 | head -n "$top" \
    | awk -F'\t' '{ printf "%2d. %-6s %-28s %6d ms %4d requests\n", NR, $2, $3, $1, $4 }'
(( skipped == 0 )) || echo "Skipped $skipped malformed lines"
