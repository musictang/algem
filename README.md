Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.

This file is part of Algem.
Algem is free software: you can redistribute it and/or modify it
under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Algem is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with Algem. If not, see <http://www.gnu.org/licenses/>.

# PRESENTATION
Algem se décompose en deux applications :
- Une application de bureau en Java (Swing), dédiée au back-office et
principalement utilisée par l'équipe administrative.
- Une application web en Java (Spring 3/Thymeleaf/JQuery), interface
publique du logiciel 
permettant aux adhérents, professeurs, élèves, visiteurs occasionnels
de consulter leur planning ou leur suivi pédagogique. Cette interface
permet aussi aux répétiteurs de réserver une salle en ligne.

Cette deuxième application est optionnelle. Le code source de l'application
web est disponible à cette adresse :  
[algem-web-app](https://github.com/musictang/algem-web-app).

## NOTES POUR LE DEVELOPPEUR
Si le projet vous intéresse, n'hésitez pas à nous contacter.  
Le projet est supporté par l'association **Musiques Tangentes**.

* Equipe développement : <info@algem.net>
* Responsable de l'association : <admin@musiques-tangentes.asso.fr>

## COMPILATION / LANCEMENT avec Maven
Préalables :  
* Le service postgresql doit être démarré et la base algem configurée
* L'exécutable mvn doit être présent dans le PATH.

Lancement :
* Compilez le projet : `mvn compile`
* Démarrez : `mvn -P local exec:java`

### CONTENU DE L'ARCHIVE
* `src` : code source de l'application
* `test` : code source des classes de test
* `doc` : documentation java, sql et notes de versions
* `sql` : modifications sql
* `resources` : fichiers statiques de resources (images, etc.)
* `lib` : dépendances
* `plugins` (optionnel) : projets associés
* `local-repo` : repository local du package scripthelper

### ARBORESCENCE DES PACKAGES
(entre parenthèses, usage et signification)
```
src/
`-- net
    `-- algem
        |-- accounting (comptabilité)
        |-- bank (banque, agence bancaire, rib)
        |-- billing (facturation)
        |-- config
        |-- contact
        |   |-- member (adhérent)
        |   `-- teacher
        |-- course (cours)
        |-- edition (impression, export)
        |-- enrolment (inscription)
        |-- group (groupe)
        |-- opt (optionnel, obsolète)
        |-- planning 
        |   |-- agenda
        |   |-- day (planning jour)
        |   |-- editing (édition planning)
        |   `-- month (planning mois)
        |-- room (salle de cours, établissement)
        |-- security (sécurité, utilisateur, droits)
        `-- util
            |-- event (socket event)
            |-- help
            |-- jdesktop (API Java Desktop)
            |-- menu
            |-- model
            |-- module (internal frame)
            |-- postit
            `-- ui (ihm)
```
**Notes** : 
* `group` a été choisi plutôt que band afin d'étendre la notion de groupe de
musiciens à un simple groupe de personnes
* Une plage est liée à un planning :
    * Les plannings collectifs comportent un ensemble de plages de même durée.
    * Les plannings individuels peuvent comporter des plages de durée différente.

### LOGS
- Le journal est enregistré par défaut dans le fichier `algem.log` dans le sous-répertoire `Journaux` de l'application. Cet emplacement est paramétrable.
- Le nom de l'utilisateur et la date sont visibles dans l'en-tête de chaque log.

### CONNEXION
- La gestion de la connexion est déportée dans la classe
`net.algem.util.DataConnection`

### CACHE
Algem a été conçu à l'origine pour être exclusivement utilisé sur le réseau local. Rien n'interdit cependant de déporter le serveur de bases de données sur une machine distante (hébergement internet ou VPN). \
Afin d'accélérer le chargement de l'application, l'affichage
des plannings et la lecture des données, il est essentiel de disposer d'un cache client. C'est le rôle de la classe `net.algem.util.DataCache`.

Les données mises en cache le sont sous 2 formes :
- A travers des listes de type `GemList<T>`, `T` implémentant l'interface `net.algem.util.model.GemModel`. Ces listes sont utilisées en particulier pour l'affichage des éléments dans les combobox. La mise à jour d'une liste est immédiatement répercutée dans la combobox correspondante.
- A travers des Hashtable (il est envisageable de remplacer ces hashtables par des objets implémentant Map ou Set)

Ce sont les données récurrentes (Action, Niveau, Status, Instrument, etc.) pour lesquelles la mise en cache se révèle la plus efficace.

### DISPATCHER
- Le dispatcher est un service (java) tournant en tache de fond. Il permet la synchronisation en temps réel des différents clients sur le réseau. Le client, en l'occurrence, étant la JVM lancée au démarrage d'Algem sur l'un des postes de travail.
    * Sous Linux, le script gemdispatcher peut être placé dans le répertoire `/etc/init.d` du serveur et démarré comme un service. Dans un environnement 100% Windows, il est nécessaire d'initialiser un service équivalent.

    * A l'instar des méthodes `add`, `update` et `delete`, la méthode `remoteEvent` du DataCache permet de déclencher les actions appropriées à la réception d'un événement.

### MVC
Algem est construit suivant une architecture MVC. Les développeurs se sont efforcés dans la mesure du possible de coller à cette architecture.

### SERVICES

- Des classes de service métier ont été créées pour regrouper les fonctions d'accès aux ressources. Un certain nombre de controlleurs jouent encore ce rôle mais ils devraient être idéalement déchargés de cette responsabilité.

### SECURITE
Des profils permettent de différencier les différents rôles d'utilisateurs. Dans la pratique, seuls 4 profils sont utilisés au quotidien : **Administrateur**, **Utilisateur**,  **Professeur**,  **Adhérent**. \
L'administrateur a tous les droits par défaut, alors que ceux de l'utilisateur (une personne de l'accueil, par exemple) sont limités à certaines opérations. \
Les deux derniers profils sont principalement utilisés dans le cadre de l'application web, afin d'identifier les élèves et les professeurs et de personnaliser ainsi leur interface de consultation.

Un certain nombre de droits sont paramétrables au cas par cas, indépendamment du profil de l'utilisateur.

Il est possible de forcer la saisie d'un login et d'un mot de passe au démarrage d'Algem. Dans le cas contraire, on vérifie que l'utilisateur choisi en argument est bien présent en base de données.

Optionnellement, on peut exiger la présence chez l'utilisateur d'un certificat SSL (de connexion à la base de données).

### I18N
* Le fichier `algem.properties` (pouvant être détaché du jar) est destiné à la traduction des labels, des titres ou des menus.
Ce fichier peut être placé dans le répertoire de travail (à l'extérieur du .jar) pour des raisons pratiques et historiques. Le cas échéant, il est possible pour le client de modifier certains labels afin qu'ils correspondent aux besoins spécifiques de l'entreprise. \
Les clés sont en anglais et commencent arbitrairement par une majuscule.

* Le fichier `messages.properties` (inclus dans le jar) est utilisé, comme son nom l'indique pour les messages et les différents textes d'accompagnement : clés en minuscules et mots séparés par des points.

### PLUGINS
Le package plugins a été conçu pour rendre extensible l'application et l'adapter à l'organisation dans laquelle elle est mise en service. C'est le rôle en particulier de la classe `StatisticsPlugin`, qui peut être développée de manière indépendante. Une fois compilée et archivée dans un jar, il suffit de placer ce jar dans le classpath pour que l'organisation dispose de statistiques (exportation de données) personnalisées.

### SCRIPTS
Les scripts permettent d'effectuer des exports "ON THE FLY". C'est une manière simple d'étendre les DAO existants sans avoir besoin de recompiler.

#### EN PROJET
- Gestion des acomptes dans l'échéancier après création de facture.
- Gestion optimisée des absences.
- Gestion de bulletin scolaire
- Optimisations Loi de Finances 2016
