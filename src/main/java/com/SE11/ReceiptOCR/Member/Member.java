package com.SE11.ReceiptOCR.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {
    @Id
    private String user_id;
    private String name;
    private String email;
    private String password;
}