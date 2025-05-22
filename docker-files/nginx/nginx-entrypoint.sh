#!/bin/sh

envsubst '$DOMAIN_NAME $SSL_CERT_PATH $SSL_CERT_KEY' \
  < /etc/nginx/conf.d/default.conf.template \
  > /etc/nginx/conf.d/default.conf

exec nginx -g 'daemon off;'
