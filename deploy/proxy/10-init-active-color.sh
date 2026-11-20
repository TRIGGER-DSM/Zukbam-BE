#!/bin/sh
# 처음 뜰 때는 blue로 시작한다
set -e

if [ ! -f /etc/nginx/state/active.conf ]; then
    echo 'set $backend_host backend-blue;' > /etc/nginx/state/active.conf
fi
