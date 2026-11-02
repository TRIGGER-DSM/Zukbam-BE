# Datadog 모니터링

Zukbam-BE의 APM, 제출·채점 커스텀 로그, Synthetics, Discord 알림 설정을 코드로 관리한다.
Datadog 사이트는 **US5 (`us5.datadoghq.com`)**.

```
monitoring/datadog/
├── apply.py                  # 아래 JSON을 Datadog API로 반영 (생성 또는 갱신)
├── .env.example              # apply.py 실행용 환경변수 템플릿
├── logs/pipeline.json        # Log Pipeline (Grok parser + remapper)
├── logs/metrics/*.json       # 로그 기반 메트릭 (zukbam.solution.*)
├── monitors/*.json           # 모니터 (Discord 알림)
├── dashboards/*.json         # 대시보드
└── synthetics/*.json         # Synthetics API 테스트
```

## 구성

```
backend (dd-java-agent) ──trace/runtime metrics──▶ datadog-agent ──▶ Datadog (US5)
backend stdout 로그 ──docker json-file──▶ datadog-agent ─┘      │
                                                           Monitor ─▶ Webhook ─▶ Discord
Datadog Synthetics (서울, 30분마다) ──HTTP──▶ http://<공인 IP>:8080
```

- **APM**: 이미지에 `dd-java-agent`가 들어 있고, `docker-compose.yml`에서 `JAVA_TOOL_OPTIONS`로 붙인다.
  `DD_VERSION`은 CI가 커밋 SHA 7자리로 넣는다.
- **로그**: `datadog-agent`는 `com.datadoghq.ad.logs` 라벨이 있는 컨테이너(backend)의 로그만 수집한다.
  Judge0·DB 로그는 보내지 않는다.
- **에이전트 포트는 호스트에 열지 않는다.** compose 내부망에서만 받는다.

## 시크릿

| 이름 | 위치 | 용도 |
|---|---|---|
| `DD_API_KEY` | GitHub Secrets | CD가 `datadog-agent` 컨테이너에 넘긴다 |
| `DD_APP_KEY` | 로컬 `.env`만 | `apply.py` 실행용. 서버·GitHub에는 두지 않는다 |
| `DISCORD_WEBHOOK_URL` | 로컬 `.env`만 | Datadog Webhooks 통합에 등록된다 |
| `ZUKBAM_SYNTH_*` | 로컬 `.env`만 | Datadog Synthetics global variable(비밀번호는 secure)로 등록된다 |

## 커스텀 로그 (제출·채점)

`SolutionMetricLogger`가 logger `metrics.solution`으로 key=value 로그를 남긴다.
Grok parser가 이 키들을 `@solution.*` 속성으로 꺼낸다.

```
2026-11-02T08:28:16.854+0000  INFO [judge-1] [dd.trace_id=… dd.span_id=…] metrics.solution : event=grading_completed solution_id=1 problem_id=1 language=PYTHON result=WRONG_ANSWER system_failure=false judge_ms=812 turnaround_ms=1039 exec_time_ms=10 memory_kb=2000
```

| event | 키 |
|---|---|
| `solution_submitted` | `solution_id`, `problem_id`, `language` |
| `submission_rate_limited` | `user_id` |
| `grading_completed` | `solution_id`, `problem_id`, `language`, `result`, `system_failure`, `failure_reason`, `judge_ms`, `turnaround_ms`, `exec_time_ms`, `memory_kb` |

- `turnaround_ms`: 제출 시각부터 채점 결과 반영까지 걸린 시간. 대기열에서 기다린 시간이 포함된다.
- `judge_ms`: Judge0 호출에 걸린 시간. Judge0을 호출하지 않은 경로에는 이 키가 없다.
- `system_failure=true`: 사용자 코드 때문이 아니라 채점 시스템 원인으로 `INTERNAL_ERROR`가 된 경우. 원인은 `failure_reason`에 남는다.
  - `judge_error`: Judge0 호출 예외(타임아웃, 연결 실패 등)
  - `judge_reported`: Judge0이 내부 에러를 응답함
  - `no_testcase`: 문제에 테스트케이스가 없음
  - `queue_full`: 채점 대기열이 가득 참
- 값이 없는 키는 생략한다. 소스코드나 이메일은 남기지 않는다.
- Datadog 예약 속성(`status`, `duration`, `service` 등)과 겹치지 않게, 채점 결과 키는 `result`라고 쓴다.

> 로그 형식(`application.yaml`의 `logging.pattern.console`)이나 키 이름을 바꾸면
> `logs/pipeline.json`, `logs/metrics`, 대시보드, 모니터도 함께 바꾼다.
> 형식은 `SolutionMetricLoggerTest`가 고정하고 있다.

**로그 기반 메트릭**

| 메트릭 | 종류 | 태그 |
|---|---|---|
| `zukbam.solution.submitted` | count | language |
| `zukbam.solution.graded` | count | language, result, system_failure, failure_reason |
| `zukbam.solution.judge_ms` | distribution | language |
| `zukbam.solution.turnaround_ms` | distribution | – |
| `zukbam.solution.rate_limited` | count | – |

- 로그를 인덱싱(보관)하지 않아도 메트릭은 만들어진다. 로그 용량이 부담되면 Logs > Indexes에서 `@solution.event:*` 로그를 제외 필터로 빼도 대시보드와 모니터는 그대로 동작한다.
- 메트릭은 만든 시점 이후에 들어온 로그부터 쌓인다.

## 모니터 (Discord `@webhook-discord`)

| 모니터 | 조건 |
|---|---|
| API 에러율 급증 | 5분간 에러율 > 5% **그리고** 에러 ≥ 5건 (composite) |
| API p95 응답시간 | 10분 p95 > 2초 (warning 1초) |
| 채점 시스템 실패 | 15분간 `system_failure:true` ≥ 3건 (warning 1건) |
| 채점 소요시간 p95 | 15분 제출~완료 p95 > 30초 (warning 15초) |
| Synthetics | 30분마다 실행, 로그인·문제 조회 흐름이 재시도 후에도 실패 |

- 에러율은 트래픽이 적은 시간대에 요청 2건 중 1건만 실패해도 50%가 된다. 그래서 건수 조건과 묶은 composite로 알린다.
- `(조건)`이 붙은 모니터 두 개는 composite의 재료라서 Discord로 직접 알리지 않는다.

## 적용 방법

### 1. 서버

1. GitHub repo > Settings > Secrets에 `DD_API_KEY`를 추가한다. Datadog > Organization Settings > API Keys에서 발급한다.
2. develop에 머지하면 CD가 `datadog-agent`를 함께 띄운다.
3. 확인
   - Infrastructure에 `zukbam-home` 호스트가 보인다.
   - APM > Services에 `zukbam-be`가 보인다.
   - Logs에서 `service:zukbam-be`로 검색된다.

### 2. Datadog 설정 (로컬에서)

```bash
cp monitoring/datadog/.env.example monitoring/datadog/.env   # 값 채우기
set -a; source monitoring/datadog/.env; set +a

python3 monitoring/datadog/apply.py --check     # JSON만 검증 (API 호출 없음)
python3 monitoring/datadog/apply.py --dry-run   # 무엇이 만들어지고 바뀔지 확인
python3 monitoring/datadog/apply.py             # 반영
python3 monitoring/datadog/apply.py --only monitors dashboards   # 일부만
```

- `DD_APP_KEY`는 Organization Settings > Application Keys에서 만든다.
  scope는 monitors, dashboards, logs pipelines/metrics, synthetics, webhooks 쓰기 권한이 필요하다.
- Discord 웹후크 URL은 채널 설정 > 연동 > 웹후크에서 만든다.
- 반영 후 Logs > Pipelines > `Zukbam-BE (Spring Boot)` > Grok parser를 열면 샘플 4개가 모두 매칭되는지 확인할 수 있다.
  로컬에서는 근사 검증만 했으므로 실제 Grok 엔진에서 처음 한 번은 꼭 확인한다.

### 3. Synthetics 준비

- `ZUKBAM_BASE_URL`에는 외부에서 접근 가능한 주소를 넣는다. 공유기에서 8080 포트 포워딩이 필요하다.
- **전용 계정을 따로 만든다.** 학생 계정이면 `@dsm.hs.kr` 이메일이 필요하다.
  30분마다 로그인하므로 개인 계정은 쓰지 않는다.
- 문제 목록이 비어 있으면 '문제 상세 조회' 단계가 실패한다. 문제가 하나 이상 있어야 한다.
- **제출 API 체크는 보류 중이다.** 실제 Solution이 생기고 스트릭·랭킹에 반영되는 문제 때문에, 전용 계정·문제 정책을 정한 뒤 추가한다.

## Student Pack 한도 (월)

- API 테스트 10,000회
  - multistep 테스트는 **단계마다 1회**로 센다.
  - 현재 4단계 × 30분마다 = 월 약 5,760회.
  - 주기를 줄이거나 단계를 추가하면 한도를 다시 계산한다.
- 브라우저 RUM 세션 1,000개
  - RUM을 붙이면 `sessionSampleRate`를 낮게 잡는다.
- 커스텀 메트릭
  - 로그 기반 메트릭은 커스텀 메트릭으로 센다.
  - 분포 메트릭은 태그 조합마다 약 10개가 잡히므로, 태그는 대시보드·모니터에 실제로 쓰는 것만 둔다.

## 운영 메모

- **에이전트 버전 올리기**
  - `datadog/agent` 이미지는 `docker-compose.yml`의 태그를 바꾼다.
  - `dd-java-agent`는 `Dockerfile`의 `DD_JAVA_AGENT_VERSION`과 `DD_JAVA_AGENT_SHA256`을 함께 바꾼다. sha256은 Maven Central의 jar로 계산한다.
- **로컬 실행에는 영향이 없다.** 로컬에서 `bootRun`할 때는 에이전트가 붙지 않아 로그의 `dd.trace_id`가 `0`으로 남는다.
- Datadog Agent는 호스트 포트를 쓰지 않으므로, 나중에 같은 서버에 다른 모니터링 도구를 올려도 포트가 겹치지 않는다.
