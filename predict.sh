#!/bin/sh

createmodel = $1
modelId = $2
modelName = $3

echo "/predictionApiBot/predict/realtime executing..."
curl -i -H "Accept: application/json" -H "Content-Type: application/json" http://18.218.206.16:8080/predictionApiBot/
predict/realtime?createmodel={$1}&modelId={$2}&modelName={$3}
echo "\n Realtime Prediction complete"