# Moteur de script

Un script est composé :

- d'un fichier de description script.json 
- d'un fichier d'implémentation script.js

## Description des scripts
TODO

## Implémentation des scripts

###Argument `args`
- `args: object` 

Les valeurs des arguments passés au script sont disponibles sous forme de paire de clef/valeurs dans l'objet args.

###Objet de sortie `out` 
L'objet de sortie permet au script d'écrire des résultats sous forme tabulaire

- `out.header(header: string[])` : écriture de l'en-tête
- `out.line(line: string[])` : écriture d'une ligne
- `out.resultSet(rs: resultSet)` : écriture des lignes comprises dans le resultSet

###Objet de connexion `conn`
- `conn: DataConnection` 

Une instance de DataConnection est accessible au script, 
les requêtes effectuées par le script sont englobées dans une transaction.