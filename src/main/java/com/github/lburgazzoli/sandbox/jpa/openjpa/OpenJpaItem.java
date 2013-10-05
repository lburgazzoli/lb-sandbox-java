package com.github.lburgazzoli.sandbox.jpa.openjpa;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "items")
public class OpenJpaItem {
    @Id
    @Column(name = "item_name")
    private String name;

    @Column(name = "item_description")
    private String description;

    public OpenJpaItem() {
        this(StringUtils.EMPTY,StringUtils.EMPTY);
    }

    public OpenJpaItem(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "OpenJpaItem [name=" + name + ", description=" + description + "]";
    }
}