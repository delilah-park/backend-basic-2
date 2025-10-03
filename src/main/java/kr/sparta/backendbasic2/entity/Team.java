package kr.sparta.backendbasic2.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "team")
@Getter
@Setter
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // CascadeType.ALL -> team 엔티티에서 하는일이 user에게도 전이됨(같은 사이클로 움직임)
    // orphanRemoval = true -> 고아객체 제거 기능 = team에서 제거하면 member row도 삭제
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LunchRound> rounds = new ArrayList<>();

}