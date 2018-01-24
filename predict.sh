#!/bin/sh

echo "/predictionApiBot/predict/realtime executing..."
curl -i -H "Accept: application/json" -H "Content-Type: application/json" http://18.218.206.16:8080/predictionApiBot/predict/realtime -d createmodel=$1 -d modelId=$2 -d modelName=$3 -G
echo ""
echo "Realtime Prediction complete"

