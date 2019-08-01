package me.k11i.benchmark;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.GZIPOutputStream;

// https://next.json-generator.com/N1m9zly-D
@SuppressWarnings("unused")
public class Pojo {
    public enum EyeColor {
        blue,
        brown,
        green
    }

    public static class Name {
        private String first;
        private String last;

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }
    }

    public static class Address {
        private int no;
        private String street;
        private String city;
        private String state;
        private int code;

        public int getNo() {
            return no;
        }

        public void setNo(int no) {
            this.no = no;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    public static class Friend {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private String _id;
    private int index;
    private String guid;
    @JsonProperty("isActive")
    private boolean active;
    private String balance;
    private String picture;
    private int age;
    private EyeColor eyeColor;
    private Name name;
    private String company;
    private String email;
    private String phone;
    private Address address;
    private String about;
    private String registered;
    private double latitude;
    private double longitude;
    private List<String> tags;
    private int[] range;
    private List<Friend> friends;
    private String greeting;
    private String favoriteFruit;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public EyeColor getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(EyeColor eyeColor) {
        this.eyeColor = eyeColor;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int[] getRange() {
        return range;
    }

    public void setRange(int[] range) {
        this.range = range;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getFavoriteFruit() {
        return favoriteFruit;
    }

    public void setFavoriteFruit(String favoriteFruit) {
        this.favoriteFruit = favoriteFruit;
    }

    public static final List<Pojo> objects;

    static {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("dummy.json")) {
            objects = mapper.readValue(in, new TypeReference<List<Pojo>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        showSerializedAndCompressedSize(objects);
        showSerializedAndCompressedSize(IntStream.range(0, 10).mapToObj(ignore -> objects).flatMap(Collection::stream).collect(Collectors.toList()));
    }

    private static void showSerializedAndCompressedSize(List<Pojo> objects) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        int length;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            mapper.writeValue(out, objects);
            length = out.size();
            System.out.printf("Raw JSON: %d%n", length);
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzipOut = new GZIPOutputStreamWithCompressionLevel(1, out)) {
            mapper.writeValue(gzipOut, objects);
            gzipOut.close();
            System.out.printf("Gzipped JSON (level = 1): %d (%.2f%%)%n", out.size(), 100.0 * out.size() / length);
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzipOut = new GZIPOutputStreamWithCompressionLevel(6, out)) {
            mapper.writeValue(gzipOut, objects);
            gzipOut.close();
            System.out.printf("Gzipped JSON (level = 6): %d (%.2f%%)%n", out.size(), 100.0 * out.size() / length);
        }
    }
}
