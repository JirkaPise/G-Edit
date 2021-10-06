package gedit;

public enum enumNastroj {
    TUZKA("Tužka"),
    OBDELNIK("Obdelník"),
    KRUH("Kruh"),
    GUMA("Guma");

    private String jmeno;

    private enumNastroj(String jmeno) {
        this.jmeno = jmeno;
    }

    public Enum[] getNastroje() {
        Enum[] vycet = {TUZKA, OBDELNIK, KRUH, GUMA};
        return vycet;
    }
}
