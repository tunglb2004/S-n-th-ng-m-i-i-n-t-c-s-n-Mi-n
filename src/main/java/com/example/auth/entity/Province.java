package com.example.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "provinces")
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name; // Hà Nội, Hồ Chí Minh, Đà Nẵng...

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    public Province() {}
    public Province(String name, Region region) {
        this.name = name;
        this.region = region;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Region getRegion() { return region; }
    public void setRegion(Region region) { this.region = region; }
}
