package petApi;

import java.util.ArrayList;

public class CreatePet {
    private static PetCategory catCategory = new PetCategory(3L, "cats");
    private static PetTag blackAndWhiteTag = new PetTag(4L, "black&white");
    private static ArrayList<PetTag> tags = new ArrayList<petApi.PetTag>();


    public static PetData createInitialPet(Long id, String name) {
        if (tags.size()==0){
            tags.add(blackAndWhiteTag);
        }
        return new petApi.PetData(id, catCategory, name, new ArrayList<String>(), tags, "available");
    }
    public static PetData createBettyWhithChangedNameStatus(Long id, String name, String status) {

        return new petApi.PetData(id, catCategory, name, new ArrayList<String>(), tags, status);
    }
}
