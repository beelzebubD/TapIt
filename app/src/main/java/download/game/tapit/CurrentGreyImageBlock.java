package download.game.tapit;

public class CurrentGreyImageBlock {
    private boolean tapped;
    private int id;

    public CurrentGreyImageBlock() {
    }

    public boolean isTapped() {
        return tapped;
    }

    public void setTapped(boolean tapped) {
        this.tapped = tapped;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
