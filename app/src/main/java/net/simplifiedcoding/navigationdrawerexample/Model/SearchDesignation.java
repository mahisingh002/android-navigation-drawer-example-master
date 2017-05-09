package net.simplifiedcoding.navigationdrawerexample.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vibes on 3/4/17.
 */

public class SearchDesignation {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("tag")
    @Expose
    private String tag;
    @SerializedName("function")
    @Expose
    private List<SearchDesignation.Function> functions = null;
    @SerializedName("level")
    @Expose
    private List<SearchDesignation.Level> levels = null;
    @SerializedName("region")
    @Expose
    private List<SearchDesignation.Region> regions = null;

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

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    public List<Level> getLevels() {

        return levels;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }

    public class Function {

        @SerializedName("user_function")
        @Expose
        private String user_function;

        public String getUser_function() {
            return user_function;
        }

        public void setUser_function(String user_function) {
            this.user_function = user_function;
        }
    }
    public class Level {

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

    public class Region {

        @SerializedName("user_region")
        @Expose
        private String user_region;

        public String getUser_region() {
            return user_region;
        }

        public void setUser_region(String user_region) {
            this.user_region = user_region;
        }
    }

}
