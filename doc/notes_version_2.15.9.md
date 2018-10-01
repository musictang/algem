---
# Apply some language features (quotes, list symbol)
lang: fr-FR 
title: Notes de version Algem 2.15.9
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
