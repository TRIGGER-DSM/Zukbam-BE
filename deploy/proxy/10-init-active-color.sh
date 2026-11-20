#!/bin/sh
# 처음 뜰 때는 어느 색으로 보낼지 정해진 것이 없으므로 blue로 시작한다
set -e

if [ ! -f /etc/nginx/state/active.conf ]; then
    echo 'set $backend_host backend-blue;' > /etc/nginx/state/active.conf
fi
