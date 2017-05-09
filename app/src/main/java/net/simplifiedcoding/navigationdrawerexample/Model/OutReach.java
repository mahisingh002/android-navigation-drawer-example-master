package net.simplifiedcoding.navigationdrawerexample.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vibes on 4/4/17.
 */

public class OutReach  {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("tag")
    @Expose
    private String tag;
    @SerializedName("targetaudience")
    @Expose
    private List<OutReach.targetaudience> targetaudience = null;
    @SerializedName("statedata")
    @Expose
    private List<OutReach.state> statedata = null;

    public List<state> getStatedata() {
        return statedata;
    }

    public void setStatedata(List<state> statedata) {
        this.statedata = statedata;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<OutReach.targetaudience> getTargetaudience() {
        return targetaudience;
    }

    public void setTargetaudience(List<OutReach.targetaudience> targetaudience) {
        this.targetaudience = targetaudience;
    }

    public class targetaudience {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class state{
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
