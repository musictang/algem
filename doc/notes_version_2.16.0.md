---
lang: fr-FR
title: Notes de version Algem 2.16.0
...
# 2.16.0 05/03/2019

## Passage à Java 8
**Important** : comme précédemment annoncé, Algem n'est plus compatible avec java 7. Vous devez donc disposer de **Java 8**, ce qui déjà le cas pour vous tous, normalement.  
**NE PAS UTILISER PAR CONTRE Java 11 et versions suivantes. Restez désormais sur la version 8 (ou java 1.8)**.

## Correctifs
* Les adresses, emails ou numéros de téléphones n'apparaissaient pas dans certains cas dans les exports. C'était le cas en particulier lorsqu'un enfant était lié à un payeur et que la fiche du payeur n'était liée à aucune organisation.  
**Rappel** : par principe, l'adresse, les emails ou les téléphones ne sont pas exportés si le payeur est une organisation.
* Les élèves de cours collectifs apparaissaient dans le désordre dans la feuille de présence. Ces élèves sont maintenant triés par nom, prénom.

## Cours, Formules
* Le champ `Nombre de places` a été supprimé dans la fenêtre de création/modification de cours. Ce paramètre était obsolète. Le nombre de places se définit directement dans un planning en accédant à ses propriétés : `Paramètres planification` après avoir cliqué sur un planning.
* La liste des élèves d'un cours/formule comporte une nouvelle colonne `Age` : Catalogue  → Cours, Catalogue  → Formules.  
**Rappel** : la liste des élèves d'un cours est disponible en cliquant sur le bouton `Suivant` dans la fiche d'un cours ou d'une formule.
