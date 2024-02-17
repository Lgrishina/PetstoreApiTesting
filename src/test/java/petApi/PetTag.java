package petApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PetTag {
    private Long id;
    private String name;

    public PetTag(){}

    public PetTag(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public PetTag(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }
}
