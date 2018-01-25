#!/bin/sh

# This script assumes an existing aws model. Simply provide modelId and modelName when called.

echo "/predictionApiBot/predict/realtime executing..."
curl -i -H "Accept: application/json" -H "Content-Type: application/json" http://18.218.206.16:8080/predictionApiBot/predict/realtime -d createmodel=false -d modelId=$1 -d modelName=$2 -G
echo ""
echo "Realtime Prediction complete"
