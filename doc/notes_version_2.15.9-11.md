---
# Apply some language features (quotes, list symbol)
lang: fr-FR 
title: Notes de version Algem 2.15.9-11
...
# 2.15.9 07/06/2018

## CORRECTIFS
* La consultation d'une facture n'était plus possible si Algem était démarré avec une version de Java supérieure à 1.8 (java 9, 10, ...).
* Les lignes de contrepartie de facturation (Mode de règlement `FAC`) associées à un compte de produit (classe 7) n'étaient pas marquées "payées" et "transférées" dans certains cas. Ces lignes n'ont pas de sens en comptabilité, elles ne doivent donc pas être exportées.
* L'annulation d'une répétition de groupe n'entraînait pas systématiquement la suppression de l'échéance de paiement associée (si cette suppression avait été autorisée).
* Le choix d'un compte de tiers (classe 4) dans les échéances standards n'entraînait pas la création d'une ligne de contrepartie.

## COMPTABILITE
Des modifications de détail ont été faites afin de faciliter la facturation systématique des échéances et le passage à une comptabilité "d'**engagement**"[^1] :

* après création d'une répétition (payante), une ligne de contrepartie est automatiquement créée dans l'échéancier du payeur si le compte par défaut associé aux répétitions est un compte de tiers.
* idem pour l'ajout automatique des échéances standards après une inscription.
* si des lignes de paiement ou de facturation sont associées à un compte de tiers et qu'elles ne comportent pas de numéro de facture, elles ne seront pas incluses dans les différents exports au format NATIF (lorsque l'option csv n'est pas cochée) du menu Compta (Transfert Echéancier, Transfert pièce).
 
[^1]: Petit mémo sur la comptabilité d'engagement : 
[compta-facile.com/comptabilite-d-engagement](https://www.compta-facile.com/comptabilite-d-engagement/).\
La comptabilité d’engagement est une méthode d’enregistrement comptable par laquelle les recettes et les dépenses sont comptabilisées lorsqu’elles sont acquises (recettes) ou engagées (dettes) même si elles se rapportent à des opérations qui ne se sont pas dénouées sur le plan financier (payées).\
En pratique, elle consiste à enregistrer toutes les pièces justificatives au jour d’établissement de celles-ci :
_les factures d’achats et de ventes sont comptabilisées à leur date de facture_ &mdash; 
_les encaissements et paiements sont comptabilisés à leur date d’émission (ou date d’effet)_

Dans Algem, cela consiste en résumé à facturer toute opération comptable au jour de l'opération (inscription, prise de répétition, etc.). La présence d'une ligne de contrepartie (Mode de règlement `FAC`) permet entre autres de garder une trace du montant initial de la transaction.
 
**Rappel**\
L'usage de comptes de tiers dans vos comptes par défaut (menu `Compta → Comptes par défaut`) permet la **création automatique de comptes clients** dans votre logiciel comptable après importation automatisée des écritures en mode "**natif**" (au format de votre logiciel comptable).\
Un compte de tiers est systématiquement associé à un compte de produits équivalent (menu `Compta → Correspondances de comptes`).\
En pratique, on duplique le compte de tiers "411" autant de fois qu'il y a d'activités.

> Exemple :\
 4110000 Adhésions\
 4110000 Cotisations aux cours\
 4110000 Répétitions\
 etc.
 
> et les comptes de produit associés peuvent être les suivants :\
 7560000 Prd Adhésions\
 7060100 Prd Cotisations\
 7060200 Prd Répétitions\
 etc.
 
# 2.15.10 01/10/2018

## CORRECTIFS
* Les mots de passe de plus de 16 caractères étaient improprement identifiés comme "faibles".
* La longueur maximale autorisée pour le mot de passe est maintenant de **32** caractères (16 auparavant).
* Le libellé de modification de planning `Etirer/compresser/différer (cours co.)` prêtait à confusion.\
`Différer` peut en effet avoir plusieurs sens :
    * déplacer à une autre date
    * **décaler** le cours à un autre horaire dans la journée

    Or, **seule cette dernière possibilité** est autorisée dans ce contexte. Le libellé a donc été renommé `Etirer/compresser/décaler (cours co.)`. On ne modifie ainsi que l'horaire, sans changer de jour.\
    **Rappel** : _ce troisième choix (décaler) n'est envisageable que pour les cours collectifs, qu'on peut librement déplacer dans la journée, contrairement aux cours individuels, qui imposent une certaine stabilité, compte tenu de la présence des élèves à des horaires disparates_.
* Il n'est plus possible de modifier la date de début lorsqu'on utilise les fonctions `Changer salle` ou `Etirer/compresser/décaler (cours co.)`. Ces modifications ne s'appliquaient qu'à partir de la date de séance sur laquelle on avait cliqué. Il était donc inutile d'indiquer une date antérieure.

## ECHEANCIER
* Le numéro de pièce peut comporter maintenant jusqu'à **12** caractères[^2]. Auparavant, la taille maximale était de 10 caractères.

[^2]:_Notez cependant que si "DVLOG" correspond à votre format d'export comptable, la longueur maximale autorisée reste de 10 caractères_.\
**Rappel** : _le format d'export comptable se paramètre dans le menu Configuration → Paramètres → Généraux, section **Infos comptables**_.

* De nouvelles vérifications sont faites en modification d'échéance :
    * les dates aberrantes ne sont plus acceptées, ex. : 15-17-2018, 33-02-2018, 01-02-3018.
    * le nombre de caractères du champ `Numéro de pièce` est contraint à la taille maximale autorisée.
    
# 2.15.11 03/10/2018

## Postits
Il n'est plus indispensable de redémarrer le logiciel pour mettre à jour la liste des postits. Désormais, cette fenêtre est automatiquement mise à jour toutes les 5 minutes.
