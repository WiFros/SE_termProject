package com.LEGENO.SaveTheTime;

// 사실상 보여주는 때만 쓸 듯
public class GroupSchedule extends Schedule{
    private String groupID = "";
    private String groupName = "";

    GroupSchedule(){
        super();
    }

    GroupSchedule(String groupID){
        //super(ID);
        this.groupID = groupID;
    }

    GroupSchedule(String groupID, String groupName){
        this.groupID = groupID;
        this.groupName = groupName;
    }

    GroupSchedule(String groupID, String groupName, Schedule schedule){
        super(schedule);
        this.groupID = groupID;
        this.groupName = groupName;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public boolean equals(Object o) {
        if (super.equals(o))
            return true;

        if (o instanceof GroupSchedule) {
            if (super.getID().equals(((GroupSchedule) o).getID()))
                return true;
        }

        return false;
    }

    public boolean groupEquals(GroupSchedule g){
        if (g.groupID.equals(this.groupID))
            return true;
        return false;
    }

    public String toString(){
        return groupID + " / " + groupName + super.toString();
    }
}
