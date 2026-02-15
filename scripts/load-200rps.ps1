# ADG 부하 테스트 스크립트 (PowerShell)
# 요구사항: Decision(OpenRTB BidRequest/BidResponse), Event(노출/클릭/nurl/burl/lurl) — 인증 없음.
# 동시 접속수(Concurrency)만큼 워커를 띄우고, 각 워커가 접속당 요청량(RequestsPerConnection)만큼 순차 요청.
# 총 요청 수 = Concurrency x RequestsPerConnection (또는 TotalRequests 지정 시 자동 계산).
#
# 사용법:
#   .\scripts\load-200rps.ps1 [-BaseUrl "http://localhost:8080"] [-Concurrency 10] [-RequestsPerConnection 200]
#   .\scripts\load-200rps.ps1 -TotalRequests 20000 -Concurrent 100
#   권장: 2만 요청 시 -Concurrent 50 사용 시 에러율 감소 (동시 100은 서버·클라이언트 부하로 타임아웃 증가).
#   동시 100 + mock 셀프 호출 시 지연 가능하므로, 클라이언트 타임아웃(TimeoutSec) 필요 시 30초 등으로 상향.
#   .\scripts\load-200rps.ps1 -Endpoint impression -Concurrency 20 -RequestsPerConnection 100
# 연속 부하 시: 이전 실행으로 서버 Circuit Breaker가 OPEN일 수 있음. 전부 5xx면 서버 재기동 또는 wait-duration-in-open-state(기본 5s) 후 재측정 권장.

param(
    [string]$BaseUrl,
    [Alias("Concurrent")]
    [int]$Concurrency = 0,
    [int]$RequestsPerConnection = 0,
    [int]$TotalRequests = 0,
    [ValidateSet("decision", "impression", "click", "nurl", "burl", "lurl")]
    [string]$Endpoint = "decision"
)

if (-not $BaseUrl) { $BaseUrl = if ($env:BASE_URL) { $env:BASE_URL } else { "http://localhost:8080" } }
if ($Concurrency -le 0) { $Concurrency = if ($env:CONCURRENCY) { [int]$env:CONCURRENCY } else { 10 } }
if ($TotalRequests -gt 0 -and $Concurrency -gt 0) {
    $RequestsPerConnection = [math]::Max(1, [int]([math]::Ceiling($TotalRequests / $Concurrency)))
} elseif ($RequestsPerConnection -le 0) {
    $RequestsPerConnection = if ($env:REQUESTS_PER_CONNECTION) { [int]$env:REQUESTS_PER_CONNECTION } else { 200 }
}

# 엔드포인트별 URL·Body (요구사항: 인증 없음)
# Decision: OpenRTB 2.x Bid Request — 광고 응답 가능 항목 포함. id는 request_id(UNIQUE)이므로 요청마다 고유값 사용.
if ($Endpoint -eq "decision") {
    $uri = "$BaseUrl/v1/decision"
    $bodyTemplate = '{"id":"{{id}}","imp":[{"id":"placement-1","banner":{"w":300,"h":250}}],"site":{"id":"site-1","domain":"test.example.com","publisher":{"id":"pub-1","name":"Test Pub"}},"app":{"id":"app-1","bundle":"com.example.app","publisher":{"id":"pub-1"}},"device":{"os":"Android","ua":"Mozilla/5.0 (Linux; Android 10) ...","ifa":"device-idfa-1","geo":{"country":"KOR","region":"11","city":"Seoul"}},"user":{"id":"user-1"},"at":2,"tmax":500,"cur":["USD"]}'
} else {
    $uri = "$BaseUrl/v1/events/$Endpoint"
    $body = '{"url":"https://loadtest.example.com/callback"}'
    $bodyTemplate = $null
}

$total = $Concurrency * $RequestsPerConnection
if ($total -ge 20000 -and $Concurrency -gt 50) {
    Write-Host "권장: 총 요청 2만 이상이면 -Concurrent 50 이하 사용 시 성공률 향상. 현재 동시 수: $Concurrency"
}
Write-Host "Endpoint: $Endpoint (요구사항: OpenRTB/Event, 인증 없음)"
Write-Host "Target: $uri"
Write-Host "동시 접속수(concurrency): $Concurrency, 접속당 요청량(requests/connection): $RequestsPerConnection → 총 요청: $total"
# 프로브: Decision은 request_id(UNIQUE) 때문에 요청마다 고유 id 필요 → 프로브용 id 1회 생성
if ($Endpoint -eq "decision") {
    $probeBody = $bodyTemplate -replace '{{id}}', ("load-probe-" + [Guid]::NewGuid().ToString("N"))
} else {
    $probeBody = $body
}
try {
    $probe = Invoke-WebRequest -Uri $uri -Method POST -ContentType "application/json" -Body $probeBody -UseBasicParsing -TimeoutSec 5
    Write-Host "Probe OK: $($probe.StatusCode)"
} catch {
    Write-Host "Probe 실패 (서버 미기동 또는 URL/body 확인): $($_.Exception.Message)"
    if ($_.Exception.Response) { Write-Host "StatusCode: $($_.Exception.Response.StatusCode)" }
    exit 1
}
Write-Host "Start: $(Get-Date -Format 'o')"

$sw = [System.Diagnostics.Stopwatch]::StartNew()

# 워커: Decision은 매 요청마다 body의 id를 고유값(workerIndex-requestIndex)으로 치환. Event는 동일 body 반복.
# 참고: Start-Job에 Hashtable을 넘기면 역직렬화 문제로 실패할 수 있으므로 Job 내부에서 헤더 생성.
$worker = {
    param($Url, $BodyOrTemplate, $Count, $WorkerIndex, $UseTemplate)
    $list = [System.Collections.Generic.List[object]]::new()
    $reqHeaders = @{ "Content-Type" = "application/json" }
    for ($i = 0; $i -lt $Count; $i++) {
        $ms = 0
        $code = 0
        $reqBody = if ($UseTemplate) { $BodyOrTemplate -replace '{{id}}', ("load-req-" + $WorkerIndex + "-" + $i) } else { $BodyOrTemplate }
        $tw = [System.Diagnostics.Stopwatch]::StartNew()
        try {
            $r = Invoke-WebRequest -Uri $Url -Method POST -Headers $reqHeaders -Body $reqBody -UseBasicParsing -TimeoutSec 30
            $tw.Stop()
            $ms = $tw.Elapsed.TotalMilliseconds
            $code = $r.StatusCode
        } catch {
            $tw.Stop()
            $ms = $tw.Elapsed.TotalMilliseconds
            if ($_.Exception.Response) {
                try { $code = [int]$_.Exception.Response.StatusCode.value__ } catch { $code = 0 }
            }
        }
        $list.Add([PSCustomObject]@{ TimeMs = $ms; StatusCode = $code })
    }
    , $list
}

$jobs = 1..$Concurrency | ForEach-Object {
    $workerIndex = $_ - 1
    if ($Endpoint -eq "decision") {
        Start-Job -ScriptBlock $worker -ArgumentList $uri, $bodyTemplate, $RequestsPerConnection, $workerIndex, $true
    } else {
        Start-Job -ScriptBlock $worker -ArgumentList $uri, $body, $RequestsPerConnection, $workerIndex, $false
    }
}

$jobOutputs = $jobs | Wait-Job | Receive-Job
$sw.Stop()

# 워커별 반환 리스트를 하나로 합침
$all = [System.Collections.Generic.List[object]]::new()
foreach ($o in $jobOutputs) {
    if ($o -is [System.Collections.IList]) {
        foreach ($item in $o) { $all.Add($item) }
    } else {
        $all.Add($o)
    }
}
$jobs | Remove-Job -Force

$n = $all.Count
if ($n -eq 0) {
    Write-Host "No response data collected."
    exit 1
}
$success = ($all | Where-Object { $_.StatusCode -ge 200 -and $_.StatusCode -lt 300 }).Count
$clientErrors = ($all | Where-Object { $_.StatusCode -eq 0 }).Count
$server5xx = ($all | Where-Object { $_.StatusCode -ge 500 }).Count
$server4xx = ($all | Where-Object { $_.StatusCode -ge 400 -and $_.StatusCode -lt 500 }).Count
if ($success -eq 0) {
    Write-Host ""
    Write-Host "주의: 모든 요청이 실패했습니다. 서버가 실행 중인지(BaseUrl), 방화벽/연결 설정을 확인하세요."
    Write-Host ""
}

# 응답 시간만 추출 후 정렬 (percentile 계산용)
$times = @($all | ForEach-Object { [double]$_.TimeMs } | Sort-Object)
$elapsed = $sw.Elapsed.TotalSeconds
$rps = if ($elapsed -gt 0) { [math]::Round($n / $elapsed, 2) } else { 0 }

$minMs = $times[0]
$maxMs = $times[-1]
$sumMs = ($times | Measure-Object -Sum).Sum
$meanMs = $sumMs / $n

function Get-Percentile {
    param([double[]]$sorted, [double]$p)
    $idx = [int]([math]::Ceiling($sorted.Count * $p)) - 1
    if ($idx -lt 0) { $idx = 0 }
    if ($idx -ge $sorted.Count) { $idx = $sorted.Count - 1 }
    return $sorted[$idx]
}

$p50 = Get-Percentile $times 0.50
$p90 = Get-Percentile $times 0.90
$p95 = Get-Percentile $times 0.95
$p99 = Get-Percentile $times 0.99

$successPct = [math]::Round($success / $n * 100, 2)

Write-Host ""
Write-Host "========== 성능 테스트 결과 =========="
Write-Host "  총 요청 수      : $n"
Write-Host "  성공(2xx)       : $success"
Write-Host "  실패/에러       : $($n - $success)"
if (($n - $success) -gt 0) {
    Write-Host "  실패 내역       : 클라이언트(타임아웃/연결)=$clientErrors, 5xx=$server5xx, 4xx=$server4xx"
}
Write-Host "  성공률          : ${successPct}%"
Write-Host "  소요 시간       : $([math]::Round($elapsed, 2)) s"
Write-Host "  실제 RPS        : $rps"
Write-Host "  ----------------------------------------"
Write-Host "  응답 시간 (ms)"
Write-Host "    min           : $([math]::Round($minMs, 2))"
Write-Host "    max           : $([math]::Round($maxMs, 2))"
Write-Host "    mean (평균)   : $([math]::Round($meanMs, 2))"
Write-Host "    median (P50)  : $([math]::Round($p50, 2))"
Write-Host "    P90           : $([math]::Round($p90, 2))"
Write-Host "    P95           : $([math]::Round($p95, 2))"
Write-Host "    P99           : $([math]::Round($p99, 2))"
Write-Host "========================================"
Write-Host "End: $(Get-Date -Format 'o')"
Write-Host "Done."
