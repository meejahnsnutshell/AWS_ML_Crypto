#!/bin/sh
fsym = $1
tsym = $2
echo "http://54.183.247.184:8080/predictionApiBot/histo/backload executing..."
curl -i -H "Accept: application/json" -H "Content-Type: application/json" http://54.183.247.184:8080/predictionApiBot/histo/backload
echo "\n Backload complete"