package local.to.popcornmovies.models;


public class Subtitle {

    public final String file,kind,label;

    public Subtitle(String label, String file, String kind){
        this.label = label;
        this.file = file;
        this.kind = kind;
    }

    @Override
    public String toString(){
        return "{"+
                "label : "+this.label+",\t"+
                "file : "+this.file+",\t"+
                "kind : "+this.kind+",\t"+
                "}";
    }
}