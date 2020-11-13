public class Event implements Comparable<Event> {
    private int timeLeft;
    private long creationTime;
    private String type;

    @Override
    public int compareTo(Event e) {
        return Integer.compare(e.timeLeft, timeLeft);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTime(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public void setCreationTime(long creationTime){
        this.creationTime = creationTime;
    }

    public int getTime() {
        return timeLeft;
    }

    public long getCreationTime() {
        return creationTime;
    }

}

