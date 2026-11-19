#!/usr/bin/env python3
"""monitoring/datadog 의 JSON 정의를 Datadog에 반영한다.

이름(모니터·대시보드·파이프라인·Synthetics)이나 id(로그 기반 메트릭)로 기존 리소스를 찾아
있으면 갱신하고 없으면 만든다. 여러 번 실행해도 결과가 같다.
UI에서 직접 바꾼 내용은 다음 실행 때 JSON 내용으로 덮어써지므로, 바꿀 때는 JSON을 고친다.

필요한 환경변수 (monitoring/datadog/.env.example 참고, 커밋하지 말 것)
  DD_API_KEY, DD_APP_KEY          필수
  DD_SITE                         기본 us5.datadoghq.com
  DISCORD_WEBHOOK_URL             webhook 단계에서 필요
  ZUKBAM_BASE_URL                 synthetics 단계. 예) http://<공인 IP>:8080
  ZUKBAM_SYNTH_EMAIL / ZUKBAM_SYNTH_PASSWORD   synthetics 로그인용 전용 계정

사용법
  python3 monitoring/datadog/apply.py --check            # API 호출 없이 JSON만 검증
  python3 monitoring/datadog/apply.py                    # 전체 반영
  python3 monitoring/datadog/apply.py --only monitors dashboards
  python3 monitoring/datadog/apply.py --dry-run          # 조회만 하고 무엇을 만들지/바꿀지 출력
"""
import argparse
import json
import os
import re
import sys
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parent
STEPS = ["webhook", "pipeline", "log-metrics", "synthetics-variables", "monitors", "dashboards", "synthetics"]
WEBHOOK_NAME = "discord"
SYNTH_VARIABLES = {
    "ZUKBAM_BASE_URL": ("Zukbam-BE 공개 주소 (예: http://<공인 IP>:8080)", False),
    "ZUKBAM_SYNTH_EMAIL": ("Synthetics 전용 계정 이메일", False),
    "ZUKBAM_SYNTH_PASSWORD": ("Synthetics 전용 계정 비밀번호", True),
}
PLACEHOLDER = re.compile(r"\$\{(MONITOR_ID|GLOBAL_VAR_ID):([^}]+)\}")

# Discord는 Datadog 기본 payload 형식을 받지 않으므로 embeds 형식으로 보낸다
DISCORD_PAYLOAD = json.dumps({
    "username": "Datadog",
    "embeds": [{
        "title": "$EVENT_TITLE",
        "url": "$LINK",
        "description": "$TEXT_ONLY_MSG",
        "color": 15548997,
        "fields": [
            {"name": "상태", "value": "$ALERT_TRANSITION", "inline": True},
            {"name": "대상", "value": "$ALERT_SCOPE", "inline": True},
        ],
        "footer": {"text": "$HOSTNAME · $DATE"},
    }],
}, ensure_ascii=False)


class Datadog:
    def __init__(self, site, api_key, app_key, dry_run):
        self.base = f"https://api.{site}"
        self.headers = {
            "DD-API-KEY": api_key,
            "DD-APPLICATION-KEY": app_key,
            "Content-Type": "application/json",
            "Accept": "application/json",
        }
        self.dry_run = dry_run

    def request(self, method, path, body=None, allow_404=False):
        if self.dry_run and method != "GET":
            print(f"    (dry-run) {method} {path}")
            return {}
        data = json.dumps(body, ensure_ascii=False).encode() if body is not None else None
        req = urllib.request.Request(self.base + path, data=data, method=method, headers=self.headers)
        try:
            with urllib.request.urlopen(req, timeout=30) as res:
                raw = res.read()
                return json.loads(raw) if raw else {}
        except urllib.error.HTTPError as e:
            if allow_404 and e.code == 404:
                return None
            detail = e.read().decode(errors="replace")
            sys.exit(f"!! {method} {path} -> {e.code}\n{detail}")

    def upsert(self, label, existing_id, create, update):
        if existing_id is None:
            print(f"  + {label} 생성")
            return create()
        print(f"  ~ {label} 갱신 ({existing_id})")
        return update(existing_id)


def load(path):
    return json.loads(path.read_text(encoding="utf-8"))


def files(subdir):
    return sorted((ROOT / subdir).glob("*.json"))


def substitute(obj, ids):
    """${MONITOR_ID:이름}, ${GLOBAL_VAR_ID:이름} 을 실제 id로 바꾼다."""
    text = json.dumps(obj, ensure_ascii=False)

    def repl(m):
        key = (m.group(1), m.group(2))
        if key not in ids:
            sys.exit(f"!! {m.group(0)} 에 해당하는 리소스를 찾지 못했습니다. 먼저 만들어지는 단계가 실행됐는지 확인하세요.")
        return str(ids[key])

    return json.loads(PLACEHOLDER.sub(repl, text))


# ---------------------------------------------------------------- steps

def apply_webhook(dd, ids):
    url = os.environ.get("DISCORD_WEBHOOK_URL")
    if not url:
        print("  - DISCORD_WEBHOOK_URL 이 없어 건너뜀")
        return
    body = {"name": WEBHOOK_NAME, "url": url, "payload": DISCORD_PAYLOAD, "encode_as": "json"}
    path = "/api/v1/integration/webhooks/configuration/webhooks"
    exists = dd.request("GET", f"{path}/{WEBHOOK_NAME}", allow_404=True)
    dd.upsert(f"webhook @webhook-{WEBHOOK_NAME}", WEBHOOK_NAME if exists else None,
              lambda: dd.request("POST", path, body),
              lambda name: dd.request("PUT", f"{path}/{name}", body))


def apply_pipeline(dd, ids):
    body = load(ROOT / "logs" / "pipeline.json")
    pipelines = dd.request("GET", "/api/v1/logs/config/pipelines") or []
    found = next((p["id"] for p in pipelines if p.get("name") == body["name"]), None)
    dd.upsert(f"log pipeline '{body['name']}'", found,
              lambda: dd.request("POST", "/api/v1/logs/config/pipelines", body),
              lambda pid: dd.request("PUT", f"/api/v1/logs/config/pipelines/{pid}", body))


def apply_log_metrics(dd, ids):
    for f in files("logs/metrics"):
        body = load(f)
        mid = body["data"]["id"]
        path = "/api/v2/logs/config/metrics"
        exists = dd.request("GET", f"{path}/{mid}", allow_404=True)
        attrs = body["data"]["attributes"]
        # 집계 방식(compute)은 만든 뒤 바꿀 수 없어서 갱신 때는 필터·그룹만 보낸다
        patch = {"data": {"type": "logs_metrics", "attributes": {
            "filter": attrs["filter"], "group_by": attrs.get("group_by", [])}}}
        if exists and exists["data"]["attributes"]["compute"]["aggregation_type"] != attrs["compute"]["aggregation_type"]:
            print(f"  ! {mid}: 집계 방식이 달라 갱신할 수 없습니다. Datadog에서 지운 뒤 다시 실행하세요.")
            continue
        dd.upsert(f"log metric {mid}", mid if exists else None,
                  lambda: dd.request("POST", path, body),
                  lambda i: dd.request("PATCH", f"{path}/{i}", patch))


def apply_synthetics_variables(dd, ids):
    existing = {v["name"]: v["id"] for v in (dd.request("GET", "/api/v1/synthetics/variables") or {}).get("variables", [])}
    for name, (description, secure) in SYNTH_VARIABLES.items():
        value = os.environ.get(name)
        if not value:
            if name in existing:
                print(f"  = global variable {name} 그대로 둠 (환경변수 없음)")
                ids[("GLOBAL_VAR_ID", name)] = existing[name]
            else:
                print(f"  - global variable {name}: 환경변수가 없고 Datadog에도 없어 건너뜀")
            continue
        body = {"name": name, "description": description,
                "tags": ["service:zukbam-be", "managed-by:monitoring/datadog"],
                "value": {"value": value, "secure": secure}}
        res = dd.upsert(f"global variable {name}", existing.get(name),
                        lambda: dd.request("POST", "/api/v1/synthetics/variables", body),
                        lambda i: dd.request("PUT", f"/api/v1/synthetics/variables/{i}", body))
        ids[("GLOBAL_VAR_ID", name)] = (res or {}).get("id") or existing.get(name) or f"<dry-run:{name}>"


def find_monitor(dd, name):
    candidates = dd.request("GET", "/api/v1/monitor?" + urllib.parse.urlencode({"name": name})) or []
    return next((m["id"] for m in candidates if m.get("name") == name), None)


def apply_monitors(dd, ids):
    defs = [load(f) for f in files("monitors")]
    # composite는 참조하는 모니터의 id가 필요하므로 마지막에 만든다
    for body in sorted(defs, key=lambda b: b["type"] == "composite"):
        if body["type"] == "composite":
            body = substitute(body, ids)
        found = find_monitor(dd, body["name"])
        res = dd.upsert(f"monitor '{body['name']}'", found,
                        lambda: dd.request("POST", "/api/v1/monitor", body),
                        lambda i: dd.request("PUT", f"/api/v1/monitor/{i}", body))
        ids[("MONITOR_ID", body["name"])] = (res or {}).get("id") or found or 0


def apply_dashboards(dd, ids):
    dashboards = (dd.request("GET", "/api/v1/dashboard") or {}).get("dashboards", [])
    for f in files("dashboards"):
        body = load(f)
        found = next((d["id"] for d in dashboards if d.get("title") == body["title"]), None)
        res = dd.upsert(f"dashboard '{body['title']}'", found,
                        lambda: dd.request("POST", "/api/v1/dashboard", body),
                        lambda i: dd.request("PUT", f"/api/v1/dashboard/{i}", body))
        if res and res.get("url"):
            print(f"    https://{os.environ.get('DD_SITE', 'us5.datadoghq.com')}{res['url']}")


def apply_synthetics(dd, ids):
    missing = [n for n in SYNTH_VARIABLES if ("GLOBAL_VAR_ID", n) not in ids]
    if missing:
        print(f"  - global variable {', '.join(missing)} 가 없어 Synthetics 테스트를 건너뜀")
        return
    tests = (dd.request("GET", "/api/v1/synthetics/tests") or {}).get("tests", [])
    for f in files("synthetics"):
        body = substitute(load(f), ids)
        found = next((t["public_id"] for t in tests if t.get("name") == body["name"]), None)
        dd.upsert(f"synthetics '{body['name']}'", found,
                  lambda: dd.request("POST", "/api/v1/synthetics/tests/api", body),
                  lambda i: dd.request("PUT", f"/api/v1/synthetics/tests/api/{i}", body))


APPLY = {
    "webhook": apply_webhook,
    "pipeline": apply_pipeline,
    "log-metrics": apply_log_metrics,
    "synthetics-variables": apply_synthetics_variables,
    "monitors": apply_monitors,
    "dashboards": apply_dashboards,
    "synthetics": apply_synthetics,
}


def check():
    """API 호출 없이 JSON 문법과 참조(placeholder)가 맞는지 확인한다."""
    all_files = [ROOT / "logs" / "pipeline.json", *files("logs/metrics"), *files("monitors"),
                 *files("dashboards"), *files("synthetics")]
    monitor_names = {load(f)["name"] for f in files("monitors")}
    errors = 0
    for f in all_files:
        body = load(f)
        for kind, name in PLACEHOLDER.findall(json.dumps(body, ensure_ascii=False)):
            if kind == "MONITOR_ID" and name not in monitor_names:
                print(f"!! {f.relative_to(ROOT)}: 없는 모니터 '{name}' 참조"); errors += 1
            if kind == "GLOBAL_VAR_ID" and name not in SYNTH_VARIABLES:
                print(f"!! {f.relative_to(ROOT)}: 없는 global variable '{name}' 참조"); errors += 1
        if f.parent.name == "monitors" and "@webhook-" not in body["message"] and "(조건)" not in body["name"]:
            print(f"!! {f.relative_to(ROOT)}: 알림 대상(@webhook-...)이 없습니다"); errors += 1
    print(f"{len(all_files)}개 파일 확인, 오류 {errors}건")
    return errors == 0


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--only", nargs="+", choices=STEPS, help="일부 단계만 실행")
    parser.add_argument("--dry-run", action="store_true", help="조회만 하고 변경하지 않음")
    parser.add_argument("--check", action="store_true", help="API 호출 없이 JSON만 검증")
    args = parser.parse_args()

    if args.check:
        sys.exit(0 if check() else 1)
    if not check():
        sys.exit(1)

    missing = [k for k in ("DD_API_KEY", "DD_APP_KEY") if not os.environ.get(k)]
    if missing:
        sys.exit(f"!! 환경변수 {', '.join(missing)} 가 필요합니다")
    site = os.environ.get("DD_SITE", "us5.datadoghq.com")
    dd = Datadog(site, os.environ["DD_API_KEY"], os.environ["DD_APP_KEY"], args.dry_run)

    steps = args.only or STEPS
    ids = {}
    # composite 모니터·Synthetics는 다른 단계에서 얻은 id가 필요하다
    if "synthetics" in steps and "synthetics-variables" not in steps:
        steps = ["synthetics-variables", *steps]
    for step in STEPS:
        if step in steps:
            print(f"[{step}]")
            APPLY[step](dd, ids)
    print("완료")


if __name__ == "__main__":
    main()
