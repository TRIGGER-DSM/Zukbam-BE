FROM nginx:1.27-alpine

COPY default.conf /etc/nginx/conf.d/default.conf
COPY 10-init-active-color.sh /docker-entrypoint.d/10-init-active-color.sh
COPY active-color switch-color /usr/local/bin/
RUN chmod +x /docker-entrypoint.d/10-init-active-color.sh /usr/local/bin/active-color /usr/local/bin/switch-color
