#!/bin/sh

# To create a new model and give it an id and name, provide modelId & modelName when calling script.
# To create a new model and have the program generate an id & name, simply delete "-d modelId" & "-d modelName" before running script.

o "/predictionApiBot/predict/realtime executing..."
curl -i -H "Accept: application/json" -H "Content-Type: application/json" http://18.218.206.16:8080/predictionApiBot/predict/realtime -d createmodel=true -d modelId=$1 -d modelName=$2 -G
echo ""
echo "Realtime Prediction complete"

