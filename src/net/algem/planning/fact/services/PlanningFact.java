package net.algem.planning.fact.services;

import java.util.Date;

/**
 * Represents a planning fact, ie, a factual event that happened to a planning.
 */
public class PlanningFact {
    public String getPlanningDescription() {
        return planningDescription;
    }

    public void setPlanningDescription(String planningDescription) {
        this.planningDescription = planningDescription;
    }

    public enum Type {
        ABSENCE,
        REMPLACEMENT,
        RATTRAPAGE,
        ACTIVITE_BAISSE,
        ACTIVITE_SUP;

        public String toDBType() {
            return toString().toLowerCase();
        }
    }

    public static Type getType(String dbType) {
        return Type.valueOf(dbType.toUpperCase());
    }

    private long id;
    private Date date;
    private Type type;
    private int planning;
    private int prof;
    private String commentaire;
    private int dureeMinutes;
    private int statut;
    private int niveau;
    private String planningDescription;

    public PlanningFact(long id, Date date, Type type, int planning, int prof, String commentaire, int dureeMinutes, int statut, int niveau, String planningDescription) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.planning = planning;
        this.prof = prof;
        this.commentaire = commentaire;
        this.dureeMinutes = dureeMinutes;
        this.statut = statut;
        this.niveau = niveau;
        this.planningDescription = planningDescription;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getPlanning() {
        return planning;
    }

    public void setPlanning(int planning) {
        this.planning = planning;
    }

    public int getProf() {
        return prof;
    }

    public void setProf(int prof) {
        this.prof = prof;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public int getDureeMinutes() {
        return dureeMinutes;
    }

    public void setDureeMinutes(int dureeMinutes) {
        this.dureeMinutes = dureeMinutes;
    }

    public int getStatut() {
        return statut;
    }

    public void setStatut(int statut) {
        this.statut = statut;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    @Override
    public String toString() {
        return "PlanningFact{" +
                "id=" + id +
                ", date=" + date +
                ", type=" + type +
                ", planning=" + planning +
                ", prof=" + prof +
                ", commentaire='" + commentaire + '\'' +
                ", dureeMinutes=" + dureeMinutes +
                ", statut=" + statut +
                ", niveau=" + niveau +
                ", planningDescription='" + planningDescription + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanningFact that = (PlanningFact) o;

        if (id != that.id) return false;
        if (planning != that.planning) return false;
        if (prof != that.prof) return false;
        if (dureeMinutes != that.dureeMinutes) return false;
        if (statut != that.statut) return false;
        if (niveau != that.niveau) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (type != that.type) return false;
        if (commentaire != null ? !commentaire.equals(that.commentaire) : that.commentaire != null) return false;
        return !(planningDescription != null ? !planningDescription.equals(that.planningDescription) : that.planningDescription != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + planning;
        result = 31 * result + prof;
        result = 31 * result + (commentaire != null ? commentaire.hashCode() : 0);
        result = 31 * result + dureeMinutes;
        result = 31 * result + statut;
        result = 31 * result + niveau;
        result = 31 * result + (planningDescription != null ? planningDescription.hashCode() : 0);
        return result;
    }
}
