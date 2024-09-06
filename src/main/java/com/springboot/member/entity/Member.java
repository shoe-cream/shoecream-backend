package com.springboot.member.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.springboot.order_header.entity.OrderHeaders;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberId;

    @Column(nullable = false, updatable = false, unique = true)
    private long employeeId;

    @Column(nullable = false, updatable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 15)
    private String tel;

    @Column(nullable = false)
    private String address;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Column(nullable = true)
    private String profileUrl = "https://img.hankyung.com/photo/202208/BF.30820179.1.jpg";

    @OneToMany(mappedBy = "member")
    @JsonManagedReference
    private List<OrderHeaders> orderHeaders = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private MemberStatus memberStatus = MemberStatus.MEMBER_ACTIVE;

    public enum MemberStatus {
        MEMBER_ACTIVE("활동중"),
        MEMBER_SLEEP("휴면 상태"),
        MEMBER_QUIT("탈퇴 상태");

        @Getter
        private String status;

        MemberStatus(String status) {
            this.status = status;
        }
    }


}
