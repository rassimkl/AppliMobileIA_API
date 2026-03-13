#!/bin/bash

echo "Mise à jour du projet"
git pull

echo "Build du backend"
mvn clean package -DskipTests

echo "Redémarrage Docker"
docker compose down
docker compose up -d --build

echo "Déploiement terminé"