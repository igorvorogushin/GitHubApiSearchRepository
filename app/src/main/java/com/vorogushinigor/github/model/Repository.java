package com.vorogushinigor.github.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Repository {

    @SerializedName("total_count")
    @Expose
    private Integer total;

    @SerializedName("items")
    @Expose
    private List<Items> listItems;

    public Integer getTotal() {
        return total;
    }

    public List<Items> getListItems() {
        return listItems;
    }

    public class Items {
        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("description")
        @Expose
        private String description;

        @SerializedName("owner")
        @Expose
        private Owner owner;

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Owner getOwner() {
            return owner;
        }

        public class Owner {
            @SerializedName("avatar_url")
            @Expose
            private String avatar_url;

            public String getAvatar_url() {
                return avatar_url;
            }
        }

    }

}
