# 2.15.10 07/06/2018
## CORRECTIFS
* Les mots de passe de plus de 16 caractères étaient improprement identifiés comme "faibles".
* La longueur maximale autorisée pour le mot de passe est maintenant de **32** caractères (16 auparavant).
* Le libellé de modification de planning `Etirer/compresser/différer (cours co.)` prêtait à confusion.\
`Différer` peut en effet avoir plusieurs sens :
    * déplacer à une autre date
    * **décaler** le cours à un autre horaire dans la journée

    Or, **seule cette dernière possibilité** est autorisée dans ce contexte. Le libellé a donc été renommé `Etirer/compresser/décaler (cours co.)`. On ne modifie ainsi que l'horaire, sans changer de jour.\
    **Rappel** : _ce troisième choix (décaler) n'est envisageable que pour les cours collectifs, qu'on peut librement déplacer dans la journée, contrairement aux cours individuels, qui imposent une certaine stabilité, compte tenu de la présence des élèves à des horaires disparates_.

## ECHEANCIER
* Le numéro de pièce peut comporter maintenant jusqu'à **12** caractères*. Auparavant, la taille maximale était de 10 caractères.\
*_Notez cependant que si "DVLOG" correspond à votre format d'export comptable, la longueur maximale autorisée reste de 10 caractères_.

**Rappel** : _le format d'export comptable se paramètre dans le menu Configuration  → Paramètres  → Généraux, section **Infos comptables**_.

* De nouvelles vérifications sont faites en modification d'échéance :
    * les dates aberrantes ne sont plus acceptées, ex. : 15-17-2018, 33-02-2018, 01-02-3018.
    * le nombre de caractères du champ `Numéro de pièce` est contraint à la taille maximale autorisée.
