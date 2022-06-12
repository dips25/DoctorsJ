package com.example.doctorsj.Models;

import java.util.ArrayList;

public class Doctors {

        String id;
        String name;
        String email;
        String fulladdress;
        Double latitude;
        Double longitude;
        String profilepic;
        String preftime;
        boolean isenabled;
        ArrayList<String> searchparams;



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

        public String getFulladdress() {
            return fulladdress;
        }

        public void setFulladdress(String fulladdress) {
            this.fulladdress = fulladdress;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    public ArrayList<String> getSearchparams() {
        return searchparams;
    }

    public void setSearchparams(ArrayList<String> searchparams) {
        this.searchparams = searchparams;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getPreftime() {
        return preftime;
    }

    public void setPreftime(String preftime) {
        this.preftime = preftime;
    }

    public boolean isIsenabled() {
        return isenabled;
    }

    public void setIsenabled(boolean isenabled) {
        this.isenabled = isenabled;
    }
}



