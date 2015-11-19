# SuperSecureCalculator
Commencer par compiler le projet à l'aide de la commande : 
```$ ant```

## Serveur
Pour lancer le serveur, démanrer d'abord le RMIRegistry à l'aide des commandes : 
```$ cd bin/
$ /opt/java/jdk8.x86_64/bin/rmiregistry 5002```

Ensuite, démarrer le serveur avec la commande : 
```$ ./server 10 0.2```

- Le premier paramètre correspond au Qi : nombre de ressource acceptée
- Le second paramètre correspond au taux de malice du serveur

## Répartiteur
Créer un fichier scenario1.properties dans le dossier resources/scheduler/ et indiqué les adresses IP des serveurs :
```servers=127.0.0.1,127.0.0.2```

Vous pouvez lancer le scheduler en mode sécurisé sur le fichier donnees-2317.txt avec la commande :
```$ ./scheduler donnees-2317.txt```

Pour le mode non sécurisé :   
```$ ./scheduler donnees-2317.txt unsafe```

