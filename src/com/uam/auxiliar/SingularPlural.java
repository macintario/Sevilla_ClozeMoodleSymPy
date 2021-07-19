package com.uam.auxiliar;

/**
 *
 * @author Eduardo Mart&iacute;nez Cruz
 */
public class SingularPlural {
    private String plural;
    private String singular;

    public SingularPlural(String singular, String plural) {
        this.plural = plural;
        this.singular = singular;
    }

    public String getPlural() {
        return plural;
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }

    public String getSingular() {
        return singular;
    }

    public void setSingular(String singular) {
        this.singular = singular;
    }
    
    
}
