package com.example.covidtracker.models;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name="state_record")
public class StateRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "state")
    private String state;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "cases")
    private Integer cases;
    @Column(name = "deaths")
    private Integer death;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getCases() {
        return cases;
    }

    public void setCases(Integer cases) {
        this.cases = cases;
    }

    public Integer getDeath() {
        return death;
    }

    public void setDeath(Integer death) {
        this.death = death;
    }


}
