#!/usr/bin/env bash
# ADG 부하 테스트 스크립트 (Bash)
# 요구사항: Decision(OpenRTB BidRequest/BidResponse), Event(노출/클릭/nurl/burl/lurl) — 인증 없음.
# 동시 접속수(CONCURRENCY)만큼 워커를 띄우고, 각 워커가 접속당 요청량(REQUESTS_PER_CONNECTION)만큼 순차 요청.
#
# 사용법:
#   ./scripts/load-200rps.sh [BASE_URL] [CONCURRENCY] [REQUESTS_PER_CONNECTION]
#   ENDPOINT=impression CONCURRENCY=20 REQUESTS_PER_CONNECTION=100 ./scripts/load-200rps.sh
#   TOTAL_REQUESTS=20000 CONCURRENCY=100 ./scripts/load-200rps.sh

set -e

BASE_URL="${1:-${BASE_URL:-http://localhost:8080}}"
CONCURRENCY="${2:-${CONCURRENCY:-10}}"
REQUESTS_PER_CONNECTION="${3:-${REQUESTS_PER_CONNECTION:-200}}"
ENDPOINT="${ENDPOINT:-decision}"
TOTAL_REQUESTS="${TOTAL_REQUESTS:-0}"

if [ "$TOTAL_REQUESTS" -gt 0 ] && [ "$CONCURRENCY" -gt 0 ]; then
  REQUESTS_PER_CONNECTION=$(( (TOTAL_REQUESTS + CONCURRENCY - 1) / CONCURRENCY ))
  [ "$REQUESTS_PER_CONNECTION" -lt 1 ] && REQUESTS_PER_CONNECTION=1
fi

# 엔드포인트별 URL·Body (요구사항: 인증 없음)
case "$ENDPOINT" in
  decision)   BODY='{"id":"load-req-1","imp":[{"id":"p1"}]}'; URL="${BASE_URL}/v1/decision" ;;
  impression|click|nurl|burl|lurl) BODY='{"url":"https://loadtest.example.com/callback"}'; URL="${BASE_URL}/v1/events/${ENDPOINT}" ;;
  *) echo "ENDPOINT must be: decision|impression|click|nurl|burl|lurl"; exit 1 ;;
esac

TOTAL=$((CONCURRENCY * REQUESTS_PER_CONNECTION))

# 응답 시간·상태 코드 수집용 임시 디렉터리 (워커별 파일로 동시 쓰기)
RESULTS_DIR=$(mktemp -d 2>/dev/null || echo "/tmp/adg-load-$$")
trap 'rm -rf "$RESULTS_DIR"' EXIT

echo "Endpoint: $ENDPOINT (요구사항: OpenRTB/Event, 인증 없음)"
echo "Target: $URL"
echo "동시 접속수(concurrency): $CONCURRENCY, 접속당 요청량(requests/connection): $REQUESTS_PER_CONNECTION → 총 요청: $TOTAL"
echo "Start: $(date -Iseconds 2>/dev/null || date)"

start_ts=$(date +%s.%N)

# 워커: 한 접속이 REQUESTS_PER_CONNECTION번 순차 요청. 각 줄에 "http_code\ttime_total(초)" 출력
run_one_worker() {
  local wid=$1
  local out="$RESULTS_DIR/w.$wid"
  local i=0
  while [ "$i" -lt "$REQUESTS_PER_CONNECTION" ]; do
    curl -s -o /dev/null -w "%{http_code}\t%{time_total}\n" -X POST "$URL" \
      -H "Content-Type: application/json" \
      -d "$BODY" >> "$out"
    i=$((i + 1))
  done
}

export URL BODY REQUESTS_PER_CONNECTION RESULTS_DIR

c=0
while [ "$c" -lt "$CONCURRENCY" ]; do
  ( run_one_worker "$c" ) &
  c=$((c + 1))
done
wait

end_ts=$(date +%s.%N)
elapsed=$(echo "$end_ts - $start_ts" | bc 2>/dev/null || echo "0")

# --- 성능 결과 집계 (정렬 후 awk로 min/max/mean/P50,P90,P95,P99, 성공률) ---
ALL="$RESULTS_DIR/all"
cat "$RESULTS_DIR"/w.* 2>/dev/null > "$ALL" || true

if [ ! -s "$ALL" ]; then
  echo "No response data collected."
  exit 1
fi

# 정렬: 두 번째 컬럼(응답 시간) 기준 오름차순
sort -t$'\t' -k2 -n "$ALL" > "$RESULTS_DIR/sorted"

awk -F'\t' -v elapsed_sec="$elapsed" '
  BEGIN { n=0; sum=0; success=0 }
  { code=$1; t=$2; if (t+0==t) { a[++n]=t; sum+=t; if (code+0>=200 && code+0<300) success++ } }
  END {
    if (n==0) { print "No valid samples."; exit }
    mean = sum/n
    p50 = (n*0.50+0.5); p50 = int(p50); if (p50<1) p50=1; if (p50>n) p50=n
    p90 = (n*0.90+0.5); p90 = int(p90); if (p90<1) p90=1; if (p90>n) p90=n
    p95 = (n*0.95+0.5); p95 = int(p95); if (p95<1) p95=1; if (p95>n) p95=n
    p99 = (n*0.99+0.5); p99 = int(p99); if (p99<1) p99=1; if (p99>n) p99=n
    min = a[1]; max = a[n]
    # ms 단위로 출력 (curl time_total 은 초)
    printf "\n"
    printf "========== 성능 테스트 결과 ==========\n"
    printf "  총 요청 수      : %d\n", n
    printf "  성공(2xx)       : %d\n", success
    printf "  실패/에러       : %d\n", n - success
    printf "  성공률          : %.2f%%\n", (n>0 ? success/n*100 : 0)
    printf "  소요 시간       : %.2f s\n", elapsed_sec+0
    printf "  실제 RPS        : %.2f\n", (elapsed_sec+0>0 ? n/elapsed_sec : 0)
    printf "  ----------------------------------------\n"
    printf "  응답 시간 (ms)\n"
    printf "    min           : %.2f\n", min*1000
    printf "    max           : %.2f\n", max*1000
    printf "    mean (평균)   : %.2f\n", mean*1000
    printf "    median (P50)  : %.2f\n", a[p50]*1000
    printf "    P90           : %.2f\n", a[p90]*1000
    printf "    P95           : %.2f\n", a[p95]*1000
    printf "    P99           : %.2f\n", a[p99]*1000
    printf "========================================\n"
  }
' "$RESULTS_DIR/sorted"

echo "End: $(date -Iseconds 2>/dev/null || date)"
echo "Done."
