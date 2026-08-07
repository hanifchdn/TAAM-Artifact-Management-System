package group17_b07summer2026;

/**
 * Artifact class that defines the Artifact structure
 * Represents the type contract and fields of an Artifact.
 *
 */
public class Artifact implements DatabaseItem{

    private String LOT;
    private String name;
    private String description;
    private String category;
    private String material;
    private String dynasty;
    private String culturalOrigin;
    private String height;
    private String width;
    private String depth;

    private String condition;
    private String currentLocation;
    private String acquisitionMethod;
    private String provenance;
    private String accessionNumber;
    private String notes;
    private String imageUrl;

    /**
     * No Argument constructor for firebase, do not use to create an artifact
     */
    public Artifact() {}

    /**
     * Creates an Artifact with its basic required information
     *
     * @param LOT the unique lot identifier of the artifact
     * @param name the name of the artifact
     * @param description a description of the artifact
     * @param category the category the artifact belongs to
     * @param material the material the artifact is made of
     * @param dynasty the dynasty or historical period the artifact is from
     */
    public Artifact(String LOT, String name, String description, String category, String material, String dynasty) {
        this.LOT = LOT;
        this.name = name;
        this.description = description;
        this.category = category;
        this.material = material;
        this.dynasty = dynasty;

    }

    public String getLOT() {
        return LOT;
    }

    public void setLOT(String LOT) {
        this.LOT = LOT;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDynasty() {
        return dynasty;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }

    public String getCulturalOrigin() {
        return culturalOrigin;
    }

    public void setCulturalOrigin(String culturalOrigin) {
        this.culturalOrigin = culturalOrigin;
    }

    public String getHeight(){
        return height;
    }
    public void setHeight(String height){
        this.height = height;
    }
    public String getWidth(){
        return width;
    }
    public void setWidth(String width){
        this.width = width;
    }
    public String getDepth(){
        return depth;
    }
    public void setDepth(String depth){
        this.depth = depth;
    }


    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getAcquisitionMethod() {
        return acquisitionMethod;
    }

    public void setAcquisitionMethod(String acquisitionMethod) {
        this.acquisitionMethod = acquisitionMethod;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}
