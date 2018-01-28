#!/bin/sh

echo "/predictionApiBot/predict/analyze executing..."
curl -i -H "Accept: application/json" -H "Content-Type: application/json" http://18.218.206.16:8080/predictionApiBot/predict/analyze
echo ""
echo "Analysis of Prediction complete"

