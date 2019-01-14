package com.huangfuren.amusementparkmanagementsystem.model;

public class Project {
	private int id;
	private String name;
	private int available;
	
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
	public int getAvailable() {
		return available;
	}
	public void setAvailable(int available) {
		this.available = available;
	}

	@Override
	public String toString() {
		return "Project{" +
				"id=" + id +
				", name='" + name + '\'' +
				", available=" + available +
				'}';
	}
}
