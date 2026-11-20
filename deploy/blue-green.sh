#!/usr/bin/env bash
# prod 무중단 배포(blue-green).
#
# 1. 쉬고 있는 색을 새 이미지(APP_IMAGE)로 띄운다
# 2. 헬스체크가 통과하면 proxy를 새 색으로 돌린다
# 3. 이전 색이 처리 중인 요청과 채점을 끝내고 내려가기를 기다린다
#
# 새 색이 뜨지 못하면 그 색만 치우고 실패로 끝난다. 그동안 이전 색은 계속 요청을 받는다.
# docker-compose.yml이 요구하는 환경변수(APP_IMAGE, DB_URL 등)가 모두 있어야 한다.
set -euo pipefail

cd "$(dirname "$0")/.."

: "${APP_IMAGE:?APP_IMAGE is required}"
PROJECT="${COMPOSE_PROJECT:-zukbam}"
HEALTH_TIMEOUT="${HEALTH_TIMEOUT:-180}"
INFRA_SERVICES=(db redis judge0-db judge0-redis judge0-server judge0-workers datadog-agent)

export COMPOSE_PROFILES=blue-green

compose() {
    docker compose -p "$PROJECT" "$@"
}

container_id() {
    compose ps -aq "$1"
}

is_running() {
    [ -n "$(compose ps -q --status running "$1")" ]
}

# 컨테이너의 healthcheck가 healthy가 될 때까지 기다린다. 컨테이너가 죽거나 시간이 지나면 실패한다
wait_healthy() {
    local service="$1" id status
    id="$(container_id "$service")"
    for ((elapsed = 0; elapsed < HEALTH_TIMEOUT; elapsed += 3)); do
        status="$(docker inspect --format '{{.State.Status}}/{{.State.Health.Status}}' "$id")"
        case "$status" in
            running/healthy) return 0 ;;
            running/*) sleep 3 ;;
            *) echo "$service 컨테이너가 종료됐습니다 ($status)" >&2; return 1 ;;
        esac
    done
    echo "$service 가 ${HEALTH_TIMEOUT}초 안에 준비되지 않았습니다" >&2
    return 1
}

# proxy를 거쳐서 새 색이 실제로 응답하는지 확인한다
proxy_serves() {
    for _ in 1 2 3 4 5; do
        if compose exec -T proxy wget -qO- http://127.0.0.1/actuator/health/readiness 2>/dev/null | grep -q '"status":"UP"'; then
            return 0
        fi
        sleep 1
    done
    return 1
}

# proxy 이미지 태그를 설정 파일 내용으로 정한다. 내용이 그대로면 태그도 그대로라 proxy 컨테이너를 다시 만들지 않는다.
# (다시 만들면 그 순간 연결이 끊기므로, proxy 설정을 바꾼 배포에서만 잠깐 끊긴다)
PROXY_HASH="$(find deploy/proxy -type f | LC_ALL=C sort | xargs cat | { sha256sum 2>/dev/null || shasum -a 256; } | cut -c1-12)"
export PROXY_IMAGE="zukbam-proxy:$PROXY_HASH"
if ! docker image inspect "$PROXY_IMAGE" >/dev/null 2>&1; then
    compose build proxy
fi

compose up -d "${INFRA_SERVICES[@]}"

ACTIVE=""
if is_running proxy; then
    ACTIVE="$(compose exec -T proxy active-color)"
fi
# proxy가 가리키는 색이 떠 있지 않으면(첫 배포, 수동으로 내린 경우) 내릴 이전 색이 없는 것으로 본다
if [ -n "$ACTIVE" ] && ! is_running "backend-$ACTIVE"; then
    ACTIVE=""
fi
if [ "$ACTIVE" = "blue" ]; then NEW=green; else NEW=blue; fi

echo "현재: ${ACTIVE:-없음}, 새로 띄울 색: $NEW ($APP_IMAGE)"

compose rm -sf "backend-$NEW" >/dev/null 2>&1
compose up -d --no-deps "backend-$NEW"

if ! wait_healthy "backend-$NEW"; then
    compose logs --tail 100 "backend-$NEW" >&2 || true
    compose rm -sf "backend-$NEW" >/dev/null
    echo "배포 실패: 새 버전이 뜨지 못했습니다. 기존 버전은 그대로 서비스 중입니다" >&2
    exit 1
fi

# blue-green으로 넘어오기 전의 backend 컨테이너가 8080을 잡고 있으면 proxy가 뜰 수 없다.
# 첫 배포 한 번만 여기서 잠깐 끊긴다
if [ -n "$(container_id backend)" ]; then
    echo "기존 backend 컨테이너를 내리고 proxy로 교체합니다"
    compose rm -sf backend >/dev/null
fi

compose up -d --no-deps proxy
compose exec -T proxy switch-color "$NEW"

if ! proxy_serves; then
    echo "proxy를 거친 확인에 실패했습니다" >&2
    if [ -n "$ACTIVE" ]; then
        compose exec -T proxy switch-color "$ACTIVE"
        compose rm -sf "backend-$NEW" >/dev/null
        echo "배포 실패: 이전 색($ACTIVE)으로 되돌렸습니다" >&2
    fi
    exit 1
fi

echo "$NEW 로 전환했습니다"

if [ -n "$ACTIVE" ]; then
    # 처리 중인 요청과 채점이 끝나기를 기다렸다가 내려간다 (stop_grace_period)
    compose stop "backend-$ACTIVE"
    compose rm -f "backend-$ACTIVE" >/dev/null
    echo "이전 색($ACTIVE)을 내렸습니다"
fi
