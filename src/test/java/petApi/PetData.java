package petApi;

import java.util.ArrayList;

public class PetData {
    private Long id;
    private PetCategory category;
    private String name;

    private ArrayList<String> photoUrls;
    private ArrayList<PetTag> tags;
    private String status;

    public PetData(){

    }

    public PetData(Long id, PetCategory category, String name, ArrayList<String> photoUrls, ArrayList<PetTag> tags, String status) {
        this.id =  id;
        this.category = category;
        this.name = name;
        this.photoUrls = photoUrls;
        this.tags = tags;
        this.status = status;
    }



    public Long getId() {
        return id;
    }

    public PetCategory getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getPhotoUrls() {
        return photoUrls;
    }

    public ArrayList<PetTag> getTags() {
        return tags;
    }

    public String getStatus() {
        return status;
    }
}
