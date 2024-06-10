package com.chapssal.message.service;

import com.chapssal.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int userNum;
    private String userName;
    private String profilePictureUrl;

    public UserDTO(User user) {
        this.userNum = user.getUserNum();
        this.userName = user.getUserName();
        this.profilePictureUrl = user.getProfilePictureUrl();
    }
}
