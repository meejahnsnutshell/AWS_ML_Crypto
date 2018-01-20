#!/bin/sh
type = $1
fsym = $2
tsym = $3
e = $4
aggregate = $5
limit = $6
toTs = $7
sign = $8
tryConversion = $9
extraParams = $10


echo "http://54.183.247.184:8080/predictionApiBot/histo/backload executing..."
curl -i -H "Accept: application/json" -H "Content-Type: application/json" http://54.183.247.184:8080/predictionApiBot/histo
/data?type={$1}&fsym={$2}&tsym={$3}&e={$4}&aggregate={$5}&limit={$6}&toTs={$7}&sign={$8}&tryConversion={$9}&extraParams={$10}
echo "\n Backload complete"