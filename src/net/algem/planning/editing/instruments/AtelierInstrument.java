package net.algem.planning.editing.instruments;

class AtelierInstrument {
    private int idAction;
    private int idPerson;
    private int idInstrument;

    public AtelierInstrument(int idAction, int idPerson, int idInstrument) {
        this.idAction = idAction;
        this.idPerson = idPerson;
        this.idInstrument = idInstrument;
    }

    public int getIdAction() {
        return idAction;
    }

    public void setIdAction(int idAction) {
        this.idAction = idAction;
    }

    public int getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(int idPerson) {
        this.idPerson = idPerson;
    }

    public int getIdInstrument() {
        return idInstrument;
    }

    public void setIdInstrument(int idInstrument) {
        this.idInstrument = idInstrument;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AtelierInstrument that = (AtelierInstrument) o;

        if (idAction != that.idAction) return false;
        if (idInstrument != that.idInstrument) return false;
        if (idPerson != that.idPerson) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idAction;
        result = 31 * result + idPerson;
        result = 31 * result + idInstrument;
        return result;
    }

    @Override
    public String toString() {
        return "AtelierInstrument{" +
                "idAction=" + idAction +
                ", idPerson=" + idPerson +
                ", idInstrument=" + idInstrument +
                '}';
    }
}
