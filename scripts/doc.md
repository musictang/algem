# Moteur de script

Le moteur de script est basé sur le moteur Javascript Nashorn, qui permet l'éxécution de code javascript au sein de la
JVM, et offre une intéropérabilité complète entre le code Javascript et Java. 
Le moteur Nashorn est inclus de base dans le JRE7. Le formalisme Typescript sera employé pour expliciter le type des objets.

Un script est composé :

- d'un fichier de description script.json, qui décrit les métadonnées et arguments du script
- d'un fichier d'implémentation script.js, qui effectue le travail de requêtage et d'export.

## Description des scripts
Chaque script est accompanié d'un fichier JSON qui décrit les méta données du script.

### Exemple
Un exemple de fichier JSON : 

```
{
  "description": "Un script de test",
  "args": [{
    "name" : "text1", "label": "Un text", "type": "text"
  }, {
    "name" : "int1", "label": "Un entier", "type": "int"
  }, {
    "name" : "bool1", "label": "Vrai / faux", "type": "bool"
  },{
    "name" : "date1", "label": "Date", "type": "date"
  }]
}
```

### Description
Une description user friendly du script peut être donné dans le champ `description`

### Paramètres
`args` est la liste des arguments que peut prendre le script, pour par exemple, 
laisser l'utilisateur saisir une date sur laquelle doit s'éxécuter le script, chercher par du texte, 
ou activer certains paramètres d'affichage.
Chaque argument est un objet json:

```
{
    "name" : "text1", "label": "Un text", "type": "text"
}
```

- `name: string` : le nom technique du paramètre
- `label: string` : le nom affiché du paramètre
- `type` : le type du paramètre
    - `text` : entrée textuelle
    - `int` : entrée d'un nombre entier
    - `bool` : entrée boolean, représenté par une case à cocher
    - `date` : entrée date, représenté par un date picker

## Implémentation des scripts

###Argument `args`
- `args: object` 

Les valeurs des arguments passés au script sont disponibles sous forme de paire de clef/valeurs dans l'objet args.
En fonction du type de paramètre, la valeur est typé selon la correspondance suivante : 

- `text` -> `string` 
- `int` -> `number`
- `bool` -> `bool`
- `date` -> `java.util.Date`

###Objet de sortie `out` 
L'objet de sortie permet au script d'écrire des résultats sous forme tabulaire

- `out.header(header: string[])` : écriture de l'en-tête, doit être appelé au moins une fois par le script
- `out.line(line: string[])` : écriture d'une ligne
- `out.resultSet(rs: resultSet)` : écriture de l'en-tête et des lignes comprises dans le resultSet

###Objet de connexion `conn`
- `conn: DataConnection` 

Une instance de DataConnection est accessible au script, 
les requêtes effectuées par le script sont englobées dans une transaction.

###Objet utilitaire `utils`
L'objets `utils` contient des fonctions utilisées fréquemment dans les scripts

- `utils.print(message: string)` : affichage d'un message dans la console.
- `utils.sqlDate(date: java.util.Date): string` : transforme une date java en une date postgres