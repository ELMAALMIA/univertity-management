# Se connecter au conteneur MySQL
cd config

docker exec -i mysql_db mysql -uroot -proot < init.sql


docker exec -it mysql_db mysql -uroot -proot

# Une fois connecté, sélectionner la base de données
USE gestion_examens;

# Vous pouvez maintenant exécuter des commandes SQL
SHOW TABLES;