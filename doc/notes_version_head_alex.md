Gestion des faits de planification
===

La gestion des faits de planifications permet de garder traces de différents (événements) se produisant sur le planning **réel**.
Ces faits, portent sur un planning, et sont de la nature suivante :

- Absence d'un prof
- Rattrapage d'un prof
- Remplacement d'un prof
- Suppression pour cause d'une baisse d'activité
- Ajout d'un planning pour cause d'une augmentation d'activité

Ces faits sont enregistrés, lors de l'accomplissement d'une commande utilisateur sur un planning :

- Absence à rattraper : le planning sera placé en salle de rattrapage, 
- Replanifier : permet de changer la salle/prof/date d'un planning. 
  Si nécessaire, La commande génèrera intelligemment les faits d'absences, rattrapage et remplacement.
- Supprimer (baisse d'activité) : le planning sera supprimé, et un fait de baisse d'activité sera historisé.
- Marquer comme activité supplémentaire : enregistre un fait de hausse d'activité lié à cet événement.

Actuellement ces commandes sont séparés des actions existantes de modification du planning, 
ces dernières permettant de modifier le planning théorique sans engendrer la création de faits de planning.

Moteur de script
===

Le moteur de script permet aux régisseurs de lancer des scripts d'export ou de statistiques spécifiques.
Ces scripts sont fournis par les developpeurs en utilisant le langage javascript et une API Simple permettant
notamment d'accéder à la base, d'écrire une ligne dans le tableau de sortie. 
L'avantage est que ces scripts peuvent être écrits et déployés sans recompilation d'Algem, 
et sont relativement rapide à écrire, car généralement ils ne font qu'englober une requête SQL.
Une documentation spécifique pour le développeur est fournie dans le dossier script.

Affectation des élèves aux plages de cours collectif
===

Cette fonctionnalité permet pour un ensemble d'élèves inscrits à une même action de planification d'un cours collectif,
de gérer jour par jour l'affectation des élèves aux cours. En double cliquant, sur le nom d'un élève il est possible, 
d'inscrire ou de désinscrire les élèves selon un motif répétitif. C'est particulièrement utile lorsque 
l'on veut inscrire en alternance deux élèves sur un même cours (principe de S1 / S2 utilisé par MH)
