package com.example.doctorsj.Models;

import java.util.ArrayList;

public class Patients {

        String id;
        String name;
        String email;
        String fulladdress;
        Double latitude;
        Double longitude;
        String profilepic;
        String date;
        String time;
        int convertedtime;
        String timestamp;
        String status;


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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getConvertedtime() {
        return convertedtime;
    }

    public void setConvertedtime(int convertedtime) {
        this.convertedtime = convertedtime;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


